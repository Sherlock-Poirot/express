package com.express.yto.service.impl;

import static com.express.yto.util.AreaUtil.AREA_4;
import static com.express.yto.util.AreaUtil.*;
import static com.express.yto.util.BillDealUtil.getPrepaymentByDate;
import static com.express.yto.util.BillDealUtil.isDateInRange;
import static com.express.yto.util.BillDealUtil.judgeOver;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.FeeGroupDTO;
import com.express.yto.enums.FourModelEnum;
import com.express.yto.model.Area;
import com.express.yto.model.Customer;
import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.AreaService;
import com.express.yto.service.CalculationService;
import com.express.yto.service.CustomerService;
import com.express.yto.service.ExtraFeeService;
import com.express.yto.service.FixedFeeService;
import com.express.yto.service.OverFeeService;
import com.express.yto.service.PrepaymentService;
import com.express.yto.util.LocalDateRange;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2025/9/23
 */
@Service
@Slf4j
public class CalculationServiceImpl implements CalculationService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private FixedFeeService fixedFeeService;

    @Autowired
    private OverFeeService overFeeService;

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private ExtraFeeService extraFeeService;

    @Autowired
    private AreaService areaService;

    @Override
    public List<ContractShopExcelDTO> calculation(List<ContractShopExcelDTO> list, String companyId) {
        return doCalculation(list, companyId);
    }

    private List<ContractShopExcelDTO> doCalculation(List<ContractShopExcelDTO> list, String companyId) {
        // 1. 前置校验：空数据直接返回
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        int totalCount = list.size();
        log.info("开始结算计算，总数据量：{}", totalCount);

        // 2. 预处理：给所有DTO加标记（避免removeAll）+ 提前处理空值
        list.forEach(dto -> {
            dto.setProcessed(false); // 新增processed字段标记是否已处理
            // 预处理officeExtra，避免循环内重复判断空值
            if (dto.getOfficeExtra() == null) {
                dto.setOfficeExtra(BigDecimal.ZERO);
            }
        });

        // 3. 一次性查询所有基础数据（原逻辑保留，已最优）
        ContractShopExcelDTO firstDTO = list.get(0); // 替代stream().findFirst().get()（避免Optional开销）
        String kCode = firstDTO.getKCode();
        Customer customer = customerService.getOne(new QueryWrapper<Customer>().eq("k_code", kCode));
        List<Prepayment> prepaymentList = prepaymentService.list(new QueryWrapper<Prepayment>().eq("k_code", kCode));
        List<FixedFee> fixedFeeList = fixedFeeService.list(new QueryWrapper<FixedFee>().eq("k_code", kCode));
        List<OverFee> overFeeList = overFeeService.list(new QueryWrapper<OverFee>().eq("k_code", kCode));
        List<ExtraFee> extraFeeList = extraFeeService.list(new QueryWrapper<ExtraFee>().eq("k_code", kCode));
        List<Area> areaList = areaService.list(new QueryWrapper<Area>().eq("company_id", companyId));

        // 4. 预缓存：预付款（时间区间→金额）- 核心优化：避免重复过滤
        // 封装时间区间匹配工具，替代字符串拼接key
        Map<LocalDateRange, BigDecimal> prepaymentRangeMap = new HashMap<>(prepaymentList.size());
        for (Prepayment prepayment : prepaymentList) {
            prepaymentRangeMap.put(
                    new LocalDateRange(prepayment.getStartTime(), prepayment.getEndTime()),
                    prepayment.getPreFee()
            );
        }
        Map<String, Integer> areaMap = new HashMap<>(areaList.size());
        for (Area area : areaList) {
            areaMap.put(area.getAreaCity(), area.getAreaNum());
        }

        // 5. 预缓存：固定费用分组（提前计算扣减预付款后的值）
        List<FeeGroupDTO> feeGroupList = fixedFeeList.stream()
                .map(fixedFee -> {
                    FeeGroupDTO dto = new FeeGroupDTO();
                    BeanUtils.copyProperties(fixedFee, dto);
                    // 快速匹配预付款（无需字符串拼接）
                    BigDecimal prepayment = getPrepaymentByDate(fixedFee.getStartTime(), prepaymentRangeMap);
                    dto.setFee(fixedFee.getFee().subtract(prepayment != null ? prepayment : BigDecimal.ZERO));
                    return dto;
                })
                .collect(Collectors.toList());

        // 6. 处理固定重量区间数据（核心优化：标记法替代removeAll）
        List<ContractShopExcelDTO> exportList = new ArrayList<>(totalCount);
        for (FeeGroupDTO feeGroupDTO : feeGroupList) {
            // 批量过滤符合条件的DTO（改用并行流加速）
            List<ContractShopExcelDTO> subList = list.parallelStream()
                    .filter(dto -> !dto.isProcessed() // 只处理未标记的
                            && isDateInRange(dto.getScanDate(), feeGroupDTO.getStartTime(), feeGroupDTO.getEndTime())
                            && isThisArea(areaMap, dto.getProvince(), feeGroupDTO.getArea(), customer.getThreeFlag())
                            && feeGroupDTO.getWeight().compareTo(dto.getWeight()) >= 0)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(subList)) {
                continue;
            }
            // 批量赋值+标记已处理
            subList.forEach(dto -> {
                dto.setExpense(feeGroupDTO.getFee().add(dto.getOfficeExtra()));
                dto.setProcessed(true); // 标记为已处理，避免重复计算
            });
            exportList.addAll(subList);
            log.info("固定重量区间[{}]匹配数量：{}", feeGroupDTO.getWeight(), subList.size());
        }

        // 7. 处理超重数据（核心优化：扁平化嵌套循环）
        if (exportList.size() != totalCount) {
            // 先缓存续重规则，再批量处理未标记的DTO
            List<ContractShopExcelDTO> unProcessedList = list.stream()
                    .filter(dto -> !dto.isProcessed())
                    .collect(Collectors.toList());
            log.info("开始处理超重数据，待处理数量：{}", unProcessedList.size());

            for (ContractShopExcelDTO dto : unProcessedList) {
                // 匹配符合条件的续重规则
                OverFee matchOverFee = overFeeList.stream()
                        .filter(overFee -> judgeOver(areaMap, dto, overFee, customer.getThreeFlag()))
                        .findFirst().orElse(null);
                if (matchOverFee == null) {
                    continue;
                }
                // 计算超重费用（提前处理向上取整，减少临时变量）
                BigDecimal overWeight = dto.getWeight().setScale(0, RoundingMode.CEILING)
                        .subtract(matchOverFee.getFirstWeight());
                BigDecimal overFee = overWeight.multiply(matchOverFee.getFee())
                        .add(matchOverFee.getFirstFee())
                        .add(dto.getOfficeExtra());
                // 快速获取预付款（无需重复Stream）
                BigDecimal prepayment = getPrepaymentByDate(dto.getScanDate(), prepaymentRangeMap);
                dto.setExpense(overFee.subtract(prepayment != null ? prepayment : BigDecimal.ZERO));
                dto.setOverFlag(true);
                dto.setProcessed(true);
                exportList.add(dto);
            }
        }

        // 8. 校验数量（优化：批量打印重复ID，避免循环内判断）
        if (totalCount != exportList.size()) {
            // 批量检查重复ID
            Map<String, Integer> idCountMap = new HashMap<>();
            for (ContractShopExcelDTO dto : exportList) {
                idCountMap.put(dto.getId(), idCountMap.getOrDefault(dto.getId(), 0) + 1);
            }
            List<String> duplicateIds = idCountMap.entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (!duplicateIds.isEmpty()) {
                log.error("发现重复ID：{}", duplicateIds);
            }
            // 找出未处理的数据
            List<ContractShopExcelDTO> unProcessed = list.stream()
                    .filter(dto -> !dto.isProcessed())
                    .collect(Collectors.toList());
            log.error("未匹配到规则的数据：{}",
                    unProcessed.stream().map(ContractShopExcelDTO::getId).collect(Collectors.toList()));
            throw new RuntimeException("结算数量不一致，总数量：" + totalCount + "，已处理：" + exportList.size());
        }

        // 9. 处理地区加收（修复 Lambda 变量 final 问题）
        if (CollectionUtils.isNotEmpty(extraFeeList)) {
            // 预缓存：地区名称→加收金额
            Map<String, BigDecimal> extraFeeMap = new HashMap<>();
            // 关键修复：声明为 final（用 BigDecimal.ZERO 初始化，确保赋值后不再修改）
            final BigDecimal[] shenZhenExtra = {BigDecimal.ZERO}; // 数组包装，实现"有效final"
            final BigDecimal[] zhouShanExtra = {BigDecimal.ZERO};

            for (ExtraFee extraFee : extraFeeList) {
                extraFeeMap.put(extraFee.getAreaName(), extraFee.getFee());
                // 仅赋值一次，无后续修改
                if ("深圳".contains(extraFee.getAreaName())) {
                    shenZhenExtra[0] = extraFee.getFee();
                }
                if ("舟山".contains(extraFee.getAreaName())) {
                    zhouShanExtra[0] = extraFee.getFee();
                }
            }

            // 批量处理地区加收（Lambda 中引用数组元素，规避 final 限制）
            exportList.forEach(dto -> {
                // 普通地区加收
                BigDecimal extra = extraFeeMap.get(dto.getProvince());
                if (extra != null) {
                    dto.setExpense(dto.getExpense().add(extra));
                }
                // 深圳特殊处理（引用数组的0号元素）
                if (shenZhenExtra[0].compareTo(BigDecimal.ZERO) > 0
                        && "广东省".contains(dto.getProvince())
                        && dto.getDestination().contains("深圳")) {
                    dto.setExpense(dto.getExpense().add(shenZhenExtra[0]));
                }
                // 舟山特殊处理
                if (zhouShanExtra[0].compareTo(BigDecimal.ZERO) > 0
                        && "浙江省".contains(dto.getProvince())
                        && dto.getDestination().contains("舟山")) {
                    dto.setExpense(dto.getExpense().add(zhouShanExtra[0]));
                }
            });
        }

        // 10. 处理4区超比例加收（保留原逻辑）
        dealFourRateFee(exportList, customer);
        log.info("结算计算完成，最终返回数据量：{}", exportList.size());
        return exportList;
    }

//    private List<ContractShopExcelDTO> doCalculation(List<ContractShopExcelDTO> list) {
//        ContractShopExcelDTO anyone = list.stream().findFirst().get();
//        List<ContractShopExcelDTO> dataList = new ArrayList<>(list);
//        int count = list.size();
//        /*
//         * 1.根据k码找到客户信息用于判断海南是否三区
//         * 2.根据k码找到预付款，固定重量区间，续重费用
//         * 3.根据时间先将所有的数据区分开来
//         * 4.把所有时间段内的固定重量区间的费用算出来
//         */
//        String kCode = anyone.getKCode();
//        // 客户信息
//        QueryWrapper<Customer> cqWrapper = new QueryWrapper<>();
//        cqWrapper.eq("k_code", kCode);
//        Customer customer = customerService.getOne(cqWrapper);
//
//        // 预付款
//        QueryWrapper<Prepayment> pqWrapper = new QueryWrapper<>();
//        pqWrapper.eq("k_code", kCode);
//        List<Prepayment> prepaymentList = prepaymentService.list(pqWrapper);
//
//        // 固定重量区间价格
//        QueryWrapper<FixedFee> fixWrapper = new QueryWrapper<>();
//        fixWrapper.eq("k_code", kCode);
//        List<FixedFee> fixedFeeList = fixedFeeService.list(fixWrapper);
//
//        // 续重费用
//        QueryWrapper<OverFee> overWrapper = new QueryWrapper<>();
//        overWrapper.eq("k_code", kCode);
//        List<OverFee> overFeeList = overFeeService.list(overWrapper);
//
//        // 地区加收
//        QueryWrapper<ExtraFee> eqWrapper = new QueryWrapper<>();
//        eqWrapper.eq("k_code", kCode);
//        List<ExtraFee> extraFeeList = extraFeeService.list(eqWrapper);
//
//        // 对数据的修改时间去重做一个分组方便做一个重量段的赋值  一般一个月最多改1-3次价格也就是说分组最多为3-4组
//        Map<String, BigDecimal> prepaymentMap = new HashMap<>(4);
//        for (Prepayment prepayment : prepaymentList) {
//            String key = prepayment.getStartTime() + "-" + prepayment.getEndTime();
//            prepaymentMap.put(key, prepayment.getPreFee());
//        }
//
//        // 费用分组根据 开始时间-结束时间-区域-重量上限
//        List<FeeGroupDTO> feeGroupList = new ArrayList<>(fixedFeeList.size());
//        for (FixedFee fixedFee : fixedFeeList) {
//            FeeGroupDTO dto = new FeeGroupDTO();
//            BeanUtils.copyProperties(fixedFee, dto);
//            String ppKey = fixedFee.getStartTime() + "-" + fixedFee.getEndTime();
//            BigDecimal prepayment = prepaymentMap.get(ppKey);
//            dto.setFee(fixedFee.getFee().subtract(prepayment));
//            feeGroupList.add(dto);
//        }
//
//        List<ContractShopExcelDTO> exportList = new ArrayList<>(list.size());
//        // 找出符合条件的数据并赋值
//        for (FeeGroupDTO feeGroupDTO : feeGroupList) {
//            log.info("条件判断:{}", feeGroupDTO);
//            List<ContractShopExcelDTO> subList = list.stream()
//                    .filter(e -> (e.getScanDate().isAfter(feeGroupDTO.getStartTime()) || e.getScanDate()
//                            .equals(feeGroupDTO.getStartTime()))
//                            && e.getScanDate().isBefore(feeGroupDTO.getEndTime())
//                            && isThisArea(e.getProvince(), feeGroupDTO.getArea(), customer.getThreeFlag())
//                            && feeGroupDTO.getWeight().compareTo(e.getWeight()) >= 0).collect(Collectors.toList());
//            if (CollectionUtils.isEmpty(subList)) {
//                continue;
//            }
//            subList.forEach(e -> e.setExpense(
//                    feeGroupDTO.getFee().add(e.getOfficeExtra() == null ? BigDecimal.ZERO : e.getOfficeExtra())));
//            log.info("符合条件的数量:{}", subList.size());
//            list.removeAll(subList);
//            exportList.addAll(subList);
//        }
//        if (dataList.size() != exportList.size()) {
//            // 数量不相等代表有超重的
//            for (OverFee overFee : overFeeList) {
//                for (ContractShopExcelDTO dto : list) {
//                    if (judgeOver(dto, overFee, customer.getThreeFlag())) {
//                        BigDecimal over = dto.getWeight().setScale(0, RoundingMode.CEILING)
//                                .subtract(overFee.getFirstWeight()).multiply(overFee.getFee())
//                                .add(overFee.getFirstFee())
//                                .add(dto.getOfficeExtra() == null ? BigDecimal.ZERO : dto.getOfficeExtra());
//                        BigDecimal pre = prepaymentList.stream()
//                                .filter(e -> (dto.getScanDate().isAfter(e.getStartTime()) || dto.getScanDate()
//                                        .equals(e.getStartTime())) && dto.getScanDate().isBefore(e.getEndTime()))
//                                .findAny()
//                                .orElse(Prepayment.builder().preFee(BigDecimal.ZERO).build()).getPreFee();
//                        dto.setExpense(over.subtract(pre));
//                        dto.setOverFlag(true);
//                        exportList.add(dto);
//                    }
//                }
//            }
//        }
//        if (count != exportList.size()) {
//            Map<String,String> map = new HashMap<>();
//            if (exportList.size() > count) {
//                for (ContractShopExcelDTO dto : exportList) {
//                    if (map.containsKey(dto.getId())){
//                        log.info("重复id,{}", dto.getId());
//                    } else {
//                        map.put(dto.getId(), dto.getProvince());
//                    }
//                }
//            } else {
//                dataList.removeAll(exportList);
//            }
//            // TODO 需要校验是哪些没有符合条件
//            throw new RuntimeException("结算数量不一致，请检查");
//        }
//        // 地区加收
//        if (CollectionUtils.isNotEmpty(extraFeeList)) {
//            for (ExtraFee extraFee : extraFeeList) {
//                exportList.stream().filter(e -> extraFee.getAreaName().equals(e.getProvince()))
//                        .forEach(e -> e.setExpense(e.getExpense().add(extraFee.getFee())));
//                if ("深圳".contains(extraFee.getAreaName())) {
//                    exportList.stream().filter(e -> e.getProvince().contains("广东") && e.getDestination()
//                            .contains(extraFee.getAreaName()))
//                            .forEach(e -> e.setExpense(e.getExpense().add(extraFee.getFee())));
//                }
//                if ("舟山".contains(extraFee.getAreaName())) {
//                    exportList.stream().filter(e -> e.getProvince().contains("浙江") && e.getDestination()
//                            .contains(extraFee.getAreaName()))
//                            .forEach(e -> e.setExpense(e.getExpense().add(extraFee.getFee())));
//                }
//            }
//        }
//        // TODO 4区超比例加收，暂时简易写法，后续修改
//        dealFourRateFee(exportList, customer);
//        return exportList;
//    }

    private void dealFourRateFee(List<ContractShopExcelDTO> exportList, Customer customer) {
        long fourCount = exportList.stream().filter(e -> judgeFourRate(e, customer.getThreeFlag())).count();
        BigDecimal rate = BigDecimal.valueOf(fourCount)
                .divide(BigDecimal.valueOf(exportList.size()), 3, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        if ("ceo南山及趣多多".contains(customer.getKName())) {
            rate = BigDecimal.valueOf(fourCount)
                    .divide(RuBinBinExcelHandler.count, 3, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        if (rate.compareTo(customer.getFourRate()) > 0) {
            switch (FourModelEnum.getModeCodeByName(customer.getFourModel())) {
                case 1: // 全量加收
                    for (ContractShopExcelDTO dto : exportList) {
                        if (judgeFourRate(dto, customer.getThreeFlag())) {
                            dto.setExpense(dto.getExpense().add(customer.getFourFee()));
                        }
                    }
                    break;
                case 2: // 超出部分加收
                    int count = rate.subtract(customer.getFourRate())
                            .divide(BigDecimal.valueOf(100), 3, RoundingMode.CEILING)
                            .multiply(BigDecimal.valueOf(exportList.size())).setScale(0, RoundingMode.CEILING)
                            .intValue();
                    for (ContractShopExcelDTO dto : exportList) {
                        if (judgeFourRate(dto, customer.getThreeFlag())) {
                            dto.setExpense(dto.getExpense().add(customer.getFourFee()));
                            count--;
                        }
                        if (count <= 0) {
                            break;
                        }
                    }
                    break;
                default: // 不加收
            }
        }
    }

    /**
     * 判断是否是4区
     *
     * @param dto       数据
     * @param threeFlag 海南是否三区
     * @return
     */
    private boolean judgeFourRate(ContractShopExcelDTO dto, Boolean threeFlag) {
        if ("海南省".equals(dto.getProvince()) && threeFlag) {
            return false;
        }
        return AREA_4.contains(dto.getProvince());
    }



}
