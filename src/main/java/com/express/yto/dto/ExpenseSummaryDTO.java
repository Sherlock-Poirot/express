package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报销汇总DTO
 * 用于展示指定月份的报销汇总信息
 */
@Data
public class ExpenseSummaryDTO {

    /** 月份 */
    private String month;

    /** 总报销金额 */
    private BigDecimal totalAmount;

    /** 按类型分类汇总列表 */
    private List<ExpenseTypeSummaryDTO> typeSummaryList;
}
