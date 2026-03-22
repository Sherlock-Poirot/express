package com.express.yto.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.express.yto.dto.ContractShopExcelDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Detective
 * @date Created in 2026/3/20
 */
@Slf4j
public class BillExcelListener extends AnalysisEventListener<ContractShopExcelDTO> {

    private List<ContractShopExcelDTO> billExcelList = new ArrayList<>();

    @Override
    public void invoke(ContractShopExcelDTO data, AnalysisContext context) {
        if (data.getId() == null || data.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "运单号码不合法");
        }
        if (data.getScanDate() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "扫描时间不合法");
        }
        if (data.getWeight() == null){
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "计费重量（kg）不合法");
        }
        if (data.getProvince() == null || data.getProvince().trim().isEmpty()) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "计费省份不合法");
        }

        billExcelList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("承包区" + analysisContext.readSheetHolder().getSheetName()
                + "读取完成，共" + billExcelList.size() + "条数据");
    }

    public List<ContractShopExcelDTO> getList() {
        return billExcelList;
    }
}
