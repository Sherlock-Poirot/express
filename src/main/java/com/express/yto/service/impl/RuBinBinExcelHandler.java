package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.CalculationService;
import com.express.yto.service.ExcelFileHandler;
import com.express.yto.util.BillDealUtil;
import com.express.yto.util.LocalDateRange;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2025/9/23
 */
@Component
@Order(2)
public class RuBinBinExcelHandler implements ExcelFileHandler {

    // 单价：每0.1kg的费用
    private static final BigDecimal PRICE_PER_0_1KG = new BigDecimal("0.1");
    // 重量单位：0.1kg
    private static final BigDecimal WEIGHT_UNIT = new BigDecimal("0.1");

    private static final String AREA_EXCLUDE = "海南省，宁夏回族自治区，内蒙古自治区，甘肃省，青海省";

    public static BigDecimal count = BigDecimal.ONE;

    @Autowired
    private OverFeeMapper overFeeMapper;

    @Autowired
    private PrepaymentMapper prepaymentMapper;

    @Autowired
    private CalculationService calculationService;

    @Override
    public List<ContractShopExcelDTO> handle(List<ContractShopExcelDTO> list, String companyId) {
        count = BigDecimal.valueOf(list.size());
        // 茹彬彬账单结算
        // 茹彬彬账单1-3kg的特殊处理其他的正常计算
        List<ContractShopExcelDTO> subList = list.stream().filter(e -> e.getWeight().compareTo(BigDecimal.ONE) > 0
                && e.getWeight().compareTo(BigDecimal.valueOf(3)) <= 0 && !AREA_EXCLUDE.contains(e.getProvince()))
                .collect(Collectors.toList());
        list.removeAll(subList);
        List<ContractShopExcelDTO> exportList = calculationService.calculation(list, companyId, false);
        // 1-3kg特殊处理
        List<ContractShopExcelDTO> subExportList = special(subList);
        exportList.addAll(subExportList);
        return exportList;
    }

    /**
     * 1-3kg特殊处理
     *
     * @param list
     * @return
     */
    private List<ContractShopExcelDTO> special(List<ContractShopExcelDTO> list) {
        if (list.isEmpty()) {
            return list;
        }
        String code = list.get(0).getCode();

        // 按code一次性加载：area=1的续重规则（取firstFee）+ 预付款规则（取preFee）
        List<OverFee> overFeeList = overFeeMapper.selectList(
                new QueryWrapper<OverFee>().eq("code", code).eq("area", 1));
        List<Prepayment> prepaymentList = prepaymentMapper.selectList(
                new QueryWrapper<Prepayment>().eq("code", code));

        // 组合费用区间：相同[start_time, end_time)的overFee与prepayment归为一组
        // value[0]=firstFee，value[1]=preFee（该区间无预付款时为null，按0处理）
        Map<LocalDateRange, BigDecimal[]> feeRangeMap = new HashMap<>(overFeeList.size());
        for (OverFee overFee : overFeeList) {
            feeRangeMap.computeIfAbsent(
                    new LocalDateRange(overFee.getStartTime(), overFee.getEndTime()),
                    k -> new BigDecimal[2])[0] = overFee.getFirstFee();
        }
        for (Prepayment prepayment : prepaymentList) {
            BigDecimal[] feePair = feeRangeMap.get(
                    new LocalDateRange(prepayment.getStartTime(), prepayment.getEndTime()));
            if (feePair != null) {
                feePair[1] = prepayment.getPreFee();
            }
        }

        for (ContractShopExcelDTO dto : list) {
            BigDecimal weight = dto.getWeight().subtract(BigDecimal.ONE);
            BigDecimal units = weight.divide(WEIGHT_UNIT, 0, RoundingMode.CEILING);

            // 按扫描日期命中费用区间（区间口径[start_time, end_time)与主计算一致），支持一月多次调价
            BigDecimal[] feePair = feeRangeMap.entrySet().stream()
                    .filter(e -> BillDealUtil.isDateInRange(
                            dto.getScanDate(), e.getKey().getStart(), e.getKey().getEnd()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
            if (feePair == null || feePair[0] == null) {
                throw new BusinessException("客户[" + code + "]在" + dto.getScanDate()
                        + "无生效的area=1费用规则(first_fee)，无法计算1-3kg费用");
            }
            BigDecimal preFee = feePair[1] != null ? feePair[1] : BigDecimal.ZERO;
            units = units.multiply(PRICE_PER_0_1KG).add(feePair[0]).subtract(preFee);

            if ("北京，上海".contains(dto.getProvince())) {
                units = units.add(BigDecimal.ONE);
            }
            if (dto.getOfficeExtra() != null) {
                units = units.add(dto.getOfficeExtra());
            }
            dto.setExpense(units);
        }
        return list;
    }

    @Override
    public boolean supports(String fileName) {
        return fileName.contains("ceo南山及趣多多");
    }

    @Override
    public boolean supportsByCustomer(String customerName) {
        return supports(customerName);
    }
}
