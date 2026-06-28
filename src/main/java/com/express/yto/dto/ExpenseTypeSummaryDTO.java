package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 报销类型汇总DTO
 * 用于展示按报销类型汇总的统计信息
 */
@Data
public class ExpenseTypeSummaryDTO {

    /** 报销类型：1-交通费，2-餐饮费，3-住宿费，4-办公费，5-其他 */
    private Integer expenseType;

    /** 报销类型名称 */
    private String expenseTypeName;

    /** 该类型总金额 */
    private BigDecimal totalAmount;
}
