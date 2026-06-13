package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户统计数据DTO
 * 用于展示客户维度的发货统计信息
 */
@Data
public class CustomerStatisticsDTO {
    /** 客户名称 */
    private String customerName;
    
    /** 客户编码 */
    private String customerCode;
    
    /** 发货量（总单数） */
    private Integer totalCount;
    
    /** 均重（平均重量） */
    private BigDecimal avgWeight;
    
    /** 占比（该客户发货量占总发货量的比例） */
    private BigDecimal proportion;
    
    /** 0.5kg以内发货量占比 */
    private BigDecimal weight05Percent;
    
    /** 0.5-1kg发货量占比 */
    private BigDecimal weight10Percent;
    
    /** 1-1.5kg发货量占比 */
    private BigDecimal weight15Percent;
    
    /** 1.5-2kg发货量占比 */
    private BigDecimal weight20Percent;
    
    /** 2-3kg发货量占比 */
    private BigDecimal weight30Percent;
    
    /** 超过3kg发货量占比 */
    private BigDecimal weightOver30Percent;
    
    /** 一区发货量占比（基于t_area表company_id='yto_576017'的区域配置） */
    private BigDecimal area1Percent;
    
    /** 二区发货量占比 */
    private BigDecimal area2Percent;
    
    /** 三区发货量占比 */
    private BigDecimal area3Percent;
    
    /** 四区发货量占比 */
    private BigDecimal area4Percent;
    
    /** 五区发货量占比 */
    private BigDecimal area5Percent;
    
    /** 日环比（与前一日相比的增长率，百分比） */
    private BigDecimal dayOnDayRatio;
    
    /** 成本（total_amount字段求和） */
    private BigDecimal totalAmount;
    
    /** 客户中转费（customer_fee字段求和） */
    private BigDecimal customerFee;
    
    /** 返利（暂未实现，默认0） */
    private BigDecimal rebateAmount;
    
    /** 盈利（客户中转费 + 返利 - 成本） */
    private BigDecimal profit;
}
