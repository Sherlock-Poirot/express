package com.express.yto.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.express.yto.dto.FixedExcelDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Detective
 * @date Created in 2026/3/17
 */
@Slf4j
@NoArgsConstructor
public class FixedFeeListener extends AnalysisEventListener<FixedExcelDTO> {

    private List<FixedExcelDTO>  fixedFeeList = new ArrayList<>();

    @Override
    public void invoke(FixedExcelDTO data, AnalysisContext context) {
        if (data.getCode() == null || data.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行客户ID为不合法");
        }
        if (data.getArea() == null) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行区域不合法");
        }
        if (data.getWeight() == null || data.getWeight().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("第" + context.readRowHolder().getRowIndex() + "行重量上限不合法");
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
        fixedFeeList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("固定重量价格Sheet读取完成，共" + fixedFeeList.size() + "条数据");
    }

    // 获取读取到的数据
    public List<FixedExcelDTO> getFixedFeeList() {
        return fixedFeeList;
    }
}
