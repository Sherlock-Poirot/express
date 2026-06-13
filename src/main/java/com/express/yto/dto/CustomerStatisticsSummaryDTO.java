package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户统计汇总DTO
 * 用于展示客户统计的汇总信息
 */
@Data
public class CustomerStatisticsSummaryDTO {
    /** 客户总数 */
    private Integer customerCount;
    
    /** 总发货量 */
    private Integer totalCount;
    
    /** 平均均重 */
    private BigDecimal avgWeight;
    
    /** 加权平均日环比 */
    private BigDecimal avgDayOnDayRatio;
    
    /** 总成本 */
    private BigDecimal totalAmount;
    
    /** 总中转费 */
    private BigDecimal totalCustomerFee;
    
    /** 总返利 */
    private BigDecimal totalRebateAmount;
    
    /** 总盈利 */
    private BigDecimal totalProfit;
    
    /** 固定政策收费（政策类型为2-固定收费的金额总和） */
    private BigDecimal fixedPolicyFee;
}
