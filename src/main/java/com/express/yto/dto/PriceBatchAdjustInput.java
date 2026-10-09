package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 批量调整客户价格入参
 * 逻辑：对客户当前生效价格（end_time=2999-12-31）截断至开始时间，并新增调整后的价格数据（保留历史价格轨迹）
 * @author Detective
 * @date Created in 2026/9/27
 */
@Data
public class PriceBatchAdjustInput {

    /**
     * 生效开始日期（旧价格截断为 [原start, 该日期)，新价格从该日期起生效）
     */
    private LocalDate startTime;

    /**
     * 客户K码
     */
    private String code;

    /**
     * 价格增减额（正数涨价、负数降价；作用于固定费 fee 与续重表首重 first_fee）
     */
    private BigDecimal priceDelta;

    /**
     * 预付款增减额（正负数；作用于预付款 pre_fee）
     */
    private BigDecimal prepayDelta;

    /**
     * 五区（area=5）是否同步调价：true=五区一起调，false=五区保持不变
     */
    private Boolean fiveAreaFlag;
}
