package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成本汇总DTO
 * 用于展示成本的总汇总信息
 */
@Data
public class CostSummaryDTO {

    /** 月份 */
    private String month;

    /** 总成本金额 */
    private BigDecimal totalAmount;

    /** 按类型分类汇总列表 */
    private List<CostTypeSummaryDTO> typeSummaryList;
}