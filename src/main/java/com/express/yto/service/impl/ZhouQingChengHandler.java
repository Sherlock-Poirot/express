package com.express.yto.service.impl;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.service.EmployeeService;
import org.springframework.context.annotation.Lazy;
import com.express.yto.service.ExcelFileHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2026/5/19
 */
@Component
@Order(4)
public class ZhouQingChengHandler implements ExcelFileHandler {

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Override
    public List<ContractShopExcelDTO> handle(List<ContractShopExcelDTO> list, String companyId) {
        return employeeService.aliAndLoose(list,"yto_576017", false);
    }

    @Override
    public boolean supports(String fileName) {
        return fileName.contains("周清成");
    }

    @Override
    public boolean supportsByCustomer(String customerName) {
        return supports(customerName);
    }
}
