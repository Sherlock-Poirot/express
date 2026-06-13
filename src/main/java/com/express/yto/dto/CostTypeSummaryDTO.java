package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 成本类型汇总DTO
 * 用于展示按成本类型汇总的统计信息
 */
@Data
public class CostTypeSummaryDTO {

    /** 成本类型：1-场地成本，2-人工成本，3-操作成本，4-运能成本，5-折旧成本 */
    private Integer costType;

    /** 成本类型名称 */
    private String costTypeName;

    /** 该类型总金额 */
    private BigDecimal totalAmount;
}
