package com.express.yto.service.impl;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.service.CalculationService;
import com.express.yto.service.ExcelFileHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2026/5/21
 */
@Component
@Order(5)
public class ChenLiZhiHandler implements ExcelFileHandler {

    @Autowired
    private CalculationService calculationService;

    @Override
    public List<ContractShopExcelDTO> handle(List<ContractShopExcelDTO> list, String companyId) {
        return calculationService.calculation(list, "ChenLiZhi", false);
    }

    @Override
    public boolean supports(String fileName) {
        return fileName.contains("陈丽芝");
    }

    @Override
    public boolean supportsByCustomer(String customerName) {
        return supports(customerName);
    }
}
