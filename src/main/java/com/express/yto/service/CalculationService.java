package com.express.yto.service;

import com.express.yto.dto.ContractShopExcelDTO;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2025/9/23
 */
public interface CalculationService {

    List<ContractShopExcelDTO> calculation(List<ContractShopExcelDTO> list, String companyId);
}
