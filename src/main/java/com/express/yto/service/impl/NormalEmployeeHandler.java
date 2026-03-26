package com.express.yto.service.impl;

import static com.express.yto.util.AreaUtil.isThisArea;
import static com.express.yto.util.BillDealUtil.isDateInRange;
import static com.express.yto.util.BillDealUtil.judgeOver;
import static com.express.yto.util.ExcelUtil.getBorderStyle;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.FeeGroupDTO;
import com.express.yto.listener.BillExcelListener;
import com.express.yto.model.Area;
import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.service.AreaService;
import com.express.yto.service.EmployeeBillHandler;
import com.express.yto.service.ExtraFeeService;
import com.express.yto.service.FixedFeeService;
import com.express.yto.service.OverFeeService;
import com.express.yto.service.PrepaymentService;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2026/3/20
 */
@Component
@Slf4j
public class NormalEmployeeHandler implements EmployeeBillHandler {

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

    private static String EXCEL_EXTENSIONS = ".xlsx";

    @Override
    public void handle(DealDataInput input) {
        File folder = new File(input.getReadPath());
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            log.info("读取到excel【{}】", file.getName());
            if (!file.getName().contains(EXCEL_EXTENSIONS)) {
                continue;
            }
            // 处理文件计算账单导出文件
            dealBill(input, file);
        }
    }

    @Override
    public boolean supports(String companyId) {
        // TODO 返回默认的通用算法，如果以后不适配通用的改为台州圆通和中通算法
        return true;
    }

    private void dealBill(DealDataInput input, File file) {
        List<ContractShopExcelDTO> aliList = new ArrayList<>();
        List<ContractShopExcelDTO> looseList = new ArrayList<>();
        List<ContractShopExcelDTO> limitedList = new ArrayList<>();
        List<ReadSheet> sheets;
        try (ExcelReader excelReader = EasyExcel.read(file).build()) {
            sheets = excelReader.excelExecutor().sheetList();
        }
        for (ReadSheet sheet : sheets) {
            try (ExcelReader sheetReader = EasyExcel.read(file).build()) {
                BillExcelListener listener = new BillExcelListener();
                ReadSheet readSheet = EasyExcel.readSheet(sheet.getSheetNo())
                        .head(ContractShopExcelDTO.class)  // 指定数据模型类
                        .registerReadListener(listener)  // 注册监听器
                        .build();
                sheetReader.read(readSheet);
                List<ContractShopExcelDTO> list = listener.getList();
                if (sheet.getSheetName().contains("淘宝")) {
                    aliList = aliAndLoose(list, input.getCompanyId(), false);
                }
                if (sheet.getSheetName().contains("散件")) {
                    looseList = aliAndLoose(list, input.getCompanyId(), false);
                }
                if (sheet.getSheetName().contains("限定")) {
                    limitedList = aliAndLoose(list, input.getCompanyId() + "_limit", true);
                }
            }
        }

        appendSum(aliList);
        appendSum(looseList);
        appendSum(limitedList);
        ExcelWriter writer = EasyExcel.write(input.getExportPath() + "/导出" + file.getName())
                .registerWriteHandler(getBorderStyle()).build();
        WriteSheet ali = EasyExcel.writerSheet(0, "淘宝").head(ContractShopExcelDTO.class).build();
        WriteSheet loose = EasyExcel.writerSheet(1, "散件").head(ContractShopExcelDTO.class).build();
        WriteSheet limit = EasyExcel.writerSheet(2, "限定").head(ContractShopExcelDTO.class).build();
        writer.write(aliList, ali);
        writer.write(looseList, loose);
        writer.write(limitedList, limit);
        writer.finish();
        log.info("{}导出完成", file.getName());
    }

    private void appendSum(List<ContractShopExcelDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 增加一行总计
        BigDecimal amount = list.stream().map(ContractShopExcelDTO::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal label = BigDecimal.valueOf(list.size() * 3);
        list.add(ContractShopExcelDTO.builder().expense(amount).build());
        list.add(ContractShopExcelDTO.builder().shopType("面单预付").expense(label).build());
        list.add(ContractShopExcelDTO.builder().shopType("应付合计").expense(amount.subtract(label)).build());
    }

    private List<ContractShopExcelDTO> aliAndLoose(List<ContractShopExcelDTO> list, String companyId, Boolean limit) {
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
        List<FixedFee> fixedFeeList = fixedFeeService.list(new QueryWrapper<FixedFee>().eq("k_code", companyId));
        List<OverFee> overFeeList = overFeeService.list(new QueryWrapper<OverFee>().eq("k_code", companyId));
        List<ExtraFee> extraFeeList = extraFeeService.list(new QueryWrapper<ExtraFee>().eq("k_code", companyId));
        List<Area> areaList = areaService
                .list(new QueryWrapper<Area>().eq("company_id", companyId.replaceAll("_limit", "")));

        // 4. 预缓存：预付款（时间区间→金额）- 核心优化：避免重复过滤
        // 封装时间区间匹配工具，替代字符串拼接key
        Map<String, Integer> areaMap = new HashMap<>(areaList.size());
        for (Area area : areaList) {
            String[] cityArr = area.getAreaCity().split("、");
            for (String city : cityArr) {
                areaMap.put(city, area.getAreaNum());
            }
        }

        // 5. 预缓存：固定费用分组（提前计算扣减预付款后的值）
        List<FeeGroupDTO> feeGroupList = fixedFeeList.stream()
                .map(fixedFee -> {
                    FeeGroupDTO dto = new FeeGroupDTO();
                    BeanUtils.copyProperties(fixedFee, dto);
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
                            && isThisArea(areaMap, dto.getProvince(), feeGroupDTO.getArea(), false)
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
                        .filter(overFee -> judgeOver(areaMap, dto, overFee, false))
                        .findFirst().orElse(null);
                if (matchOverFee == null) {
                    continue;
                }
                // 计算超重费用（提前处理向上取整，减少临时变量）
                // TODO 五区算法：实重*15+3（面单费）五区不足6元算6元  四区算法：4乘凑整重量
                BigDecimal overFee;
                if (limit) {
                    overFee = dto.getWeight().multiply(matchOverFee.getFee()).add(matchOverFee.getFirstFee())
                            .add(dto.getOfficeExtra());
                } else {
                    if (4 == areaMap.get(dto.getProvince())) {
                        BigDecimal overWeight = dto.getWeight().setScale(0, RoundingMode.CEILING)
                                .subtract(BigDecimal.ONE);
                        overFee = overWeight.multiply(matchOverFee.getFee()).add(matchOverFee.getFirstFee())
                                .add(dto.getOfficeExtra());
                    } else if (5 == areaMap.get(dto.getProvince())) {
                        overFee = dto.getWeight().multiply(matchOverFee.getFee()).add(matchOverFee.getFirstFee())
                                .add(dto.getOfficeExtra());
                        if (overFee.compareTo(BigDecimal.valueOf(6)) < 0) {
                            overFee = BigDecimal.valueOf(6);
                        }
                    } else {
                        overFee = dto.getWeight().multiply(matchOverFee.getFee()).add(matchOverFee.getFirstFee())
                                .add(dto.getOfficeExtra());
                    }
                }

                dto.setExpense(overFee);
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

            for (ExtraFee extraFee : extraFeeList) {
                extraFeeMap.put(extraFee.getAreaName(), extraFee.getFee());
            }

            // 批量处理地区加收（Lambda 中引用数组元素，规避 final 限制）
            exportList.forEach(dto -> {
                // 普通地区加收
                BigDecimal extra = extraFeeMap.get(dto.getProvince());
                if (extra != null) {
                    dto.setExpense(dto.getExpense().add(extra));
                }
            });
        }

        log.info("结算计算完成，最终返回数据量：{}", exportList.size());
        return exportList;
    }

}
