package com.express.yto.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.express.yto.dto.OverExcelDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Detective
 * @date Created in 2026/3/17
 */
@Slf4j
public class OverFeeListener  extends AnalysisEventListener<OverExcelDTO> {

    private List<OverExcelDTO> overExcelList = new ArrayList<>();

    @Override
    public void invoke(OverExcelDTO data, AnalysisContext context) {
        if (data.getKCode() == null || data.getKCode().trim().isEmpty()) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行客户ID为不合法");
        }
        if (data.getArea() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行区域不合法");
        }
        if (data.getFee() == null || data.getFee().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行价格不合法");
        }
        if (data.getStartTime() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行开始时间不合法");
        }
        if (data.getEndTime() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行结束时间不合法");
        }
        if (data.getFirstWeight() == null){
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行首重重量不合法");
        }
        if (data.getFirstFee() == null){
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行首重价格不合法");
        }
        overExcelList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("续重价格Sheet读取完成，共" + overExcelList.size() + "条数据");
    }

    public List<OverExcelDTO> getOverExcelList(){
        return overExcelList;
    }
}
