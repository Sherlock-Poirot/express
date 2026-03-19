package com.express.yto.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.express.yto.dto.ContractShopExcelDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Detective
 * @date Created in 2025/10/11
 */
@Slf4j
public class ReadLastRowListener extends AnalysisEventListener<ContractShopExcelDTO> {

    ContractShopExcelDTO lastRow;

    @Override
    public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
        lastRow = dto;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (lastRow == null) {
            log.info("Excel中没有数据");
        }
    }

    public ContractShopExcelDTO getLastRow() {
        return lastRow;
    }
}
