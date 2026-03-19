package com.express.yto.service;

import com.express.yto.dto.ContractShopExcelDTO;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2025/9/21
 */
public interface ExcelFileHandler {

    List<ContractShopExcelDTO> handle(List<ContractShopExcelDTO> list, String companyId);

    /**
     * 判断当前处理器是否支持该文件（由具体处理器实现）
     */
    boolean supports(String fileName);

}
