package com.express.yto.service.impl;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.service.CalculationService;
import com.express.yto.service.ExcelFileHandler;
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
 * @date Created in 2025/9/23
 */
@Component
@Order(1)
public class PointOneExcelHandler implements ExcelFileHandler {

    @Autowired
    private CalculationService calculationService;

    @Override
    public List<ContractShopExcelDTO> handle(List<ContractShopExcelDTO> list, String companyId) {
        // 4kg以上超0.1才进位
        Map<String, BigDecimal> map = new HashMap<>();
        for (ContractShopExcelDTO dto : list) {
            if (dto.getWeight().compareTo(new BigDecimal("4")) > 0) {
                if (dto.getWeight().remainder(BigDecimal.ONE).compareTo(new BigDecimal("0.1")) > 0){
                    map.put(dto.getId(), dto.getWeight());
                    dto.setWeight(dto.getWeight().setScale(0, RoundingMode.CEILING));
                } else {
                    map.put(dto.getId(), dto.getWeight());
                    dto.setWeight(dto.getWeight().setScale(0, RoundingMode.FLOOR));
                }

            }
        }
        List<ContractShopExcelDTO> exportList = calculationService.calculation(list, companyId);
        exportList.forEach(e->{
            if (map.containsKey(e.getId())){
                e.setWeight(map.get(e.getId()));
            }
        });
        return exportList;
    }

    @Override
    public boolean supports(String fileName) {
        return fileName.contains("谢友国") || fileName.contains("舒翔") || fileName.contains("小商品洪丽萍") || fileName
                .contains("李玲玲") || fileName.contains("下里桥王建");
    }

}
