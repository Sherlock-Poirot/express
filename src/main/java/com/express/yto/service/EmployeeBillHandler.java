package com.express.yto.service;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.DealDataInput;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2026/3/20
 */
public interface EmployeeBillHandler {

    void handle(DealDataInput input);

    boolean supports(String companyId);
}
