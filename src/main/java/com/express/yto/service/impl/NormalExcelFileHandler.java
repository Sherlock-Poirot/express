package com.express.yto.service.impl;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.service.CalculationService;
import com.express.yto.service.ExcelFileHandler;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2025/9/23
 */
@Component
@Order(100)
@Slf4j
public class NormalExcelFileHandler implements ExcelFileHandler {

    @Autowired
    private CalculationService calculationService;

    @Override
    public List<ContractShopExcelDTO> handle(List<ContractShopExcelDTO> list, String companyId) {
        return calculationService.calculation(list, companyId);
    }

    @Override
    public boolean supports(String fileName) {
        return fileName.contains(".xlsx") || fileName.contains(".xls");
    }

}
