package com.express.yto.service;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.DealDataInput;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2025/9/25
 */
public interface EmployeeService {

    void dealEmployeeBill(DealDataInput input);

    List<ContractShopExcelDTO> aliAndLoose(List<ContractShopExcelDTO> list, String companyId, Boolean limit);

    List<ContractShopExcelDTO> dealSpecial(List<ContractShopExcelDTO> special);
}
