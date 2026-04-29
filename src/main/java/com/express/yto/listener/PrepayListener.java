package com.express.yto.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.express.yto.dto.PrepayExcelDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Detective
 * @date Created in 2026/3/18
 */
@Slf4j
public class PrepayListener extends AnalysisEventListener<PrepayExcelDTO> {

    private List<PrepayExcelDTO> prepayExcelList = new ArrayList<>();

    @Override
    public void invoke(PrepayExcelDTO data, AnalysisContext context) {
        if (data.getCode() == null || data.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行客户ID为不合法");
        }
        if (data.getPreFee() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行价格不合法");
        }
        if (data.getStartTime() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行开始时间不合法");
        }
        if (data.getEndTime() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行结束时间不合法");
        }
        prepayExcelList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("预付款Sheet读取完成，共" + prepayExcelList.size() + "条数据");
    }

    public List<PrepayExcelDTO> getPrepayExcelList() {
        return prepayExcelList;
    }
}
