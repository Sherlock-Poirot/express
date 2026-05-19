package com.express.yto.service.impl;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.AreaService;
import com.express.yto.service.ExcelFileHandler;
import com.express.yto.service.OverFeeService;
import com.express.yto.service.PrepaymentService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2026/5/19
 */
@Component
@Order(3)
public class LiangRuiYangHandler implements ExcelFileHandler {

    @Autowired
    private OverFeeService overFeeService;

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private AreaService areaService;

    private static final String ARE_FIVE = "新疆维吾尔自治区、西藏自治区";

    private static final BigDecimal AVG_WEIGHT = BigDecimal.valueOf(0.4);

    @Override
    public List<ContractShopExcelDTO> handle(List<ContractShopExcelDTO> list, String companyId) {
        String customerCode = list.get(0).getCode();
        Map<String, Object> map = new HashMap<>();
        map.put("code", customerCode);
        List<OverFee> overFeeList = overFeeService.listByMap(map);
        OverFee overFee = overFeeList.stream().filter(e -> e.getArea() == 5).findAny().get();
        List<Prepayment> prepaymentList = prepaymentService.listByMap(map);
        BigDecimal sumWeight = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        for (ContractShopExcelDTO dto : list) {
            if (ARE_FIVE.contains(dto.getProvince())) {
                for (Prepayment prepayment : prepaymentList) {
                    if ((dto.getScanDate().isAfter(prepayment.getStartTime())
                            || dto.getScanDate().isEqual(prepayment.getStartTime()) && dto.getScanDate()
                            .isBefore(prepayment.getEndTime()))) {
                        BigDecimal fee = dto.getWeight().subtract(BigDecimal.ONE).setScale(0, BigDecimal.ROUND_CEILING)
                                .multiply(overFee.getFee()).subtract(prepayment.getPreFee()).add(overFee.getFirstFee());
                        dto.setExpense(fee);
                    }
                }
            } else {
                sumWeight = sumWeight.add(dto.getWeight());
                count = count.add(BigDecimal.ONE);
            }
        }
        BigDecimal avg = sumWeight.divide(count, 1, RoundingMode.CEILING);
        for (ContractShopExcelDTO dto : list) {
            if (!ARE_FIVE.contains(dto.getProvince())) {
                dto.setExpense(avg.subtract(AVG_WEIGHT));
            }
        }
        return list;
    }

    @Override
    public boolean supports(String fileName) {
        return fileName.contains("梁瑞阳");
    }

    @Override
    public boolean supportsByCustomer(String customerName) {
        return supports(customerName);
    }

}
