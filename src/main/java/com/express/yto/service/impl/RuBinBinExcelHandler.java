package com.express.yto.service.impl;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.service.CalculationService;
import com.express.yto.service.ExcelFileHandler;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
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
        List<ContractShopExcelDTO> exportList = calculationService.calculation(list, companyId);
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
        for (ContractShopExcelDTO dto : list) {
            BigDecimal weight = dto.getWeight().subtract(BigDecimal.ONE);
            BigDecimal units = weight.divide(WEIGHT_UNIT, 0, RoundingMode.CEILING);

            if (dto.getScanDate().isAfter(LocalDate.of(2026, 3, 3))) {
                // 总费用 = 单位数量 × 单价
                units = units.multiply(PRICE_PER_0_1KG).add(BigDecimal.valueOf(3)).subtract(BigDecimal.valueOf(3));
            } else {
                units = units.multiply(PRICE_PER_0_1KG).add(BigDecimal.valueOf(3.2)).subtract(BigDecimal.valueOf(3.1));
            }
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
}
