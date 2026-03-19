package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_prepayment")
public class Prepayment {

    /**
     * id id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 客户K码
     */
    @TableField(value = "k_code")
    private String kCode;

    /**
     * 预付款
     */
    @TableField(value = "pre_fee")
    private BigDecimal preFee;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private LocalDate endTime;

    public static final String COL_ID = "id";

    public static final String COL_K_CODE = "k_code";

    public static final String COL_PRE_FEE = "pre_fee";

    public static final String COL_START_TIME = "start_time";

    public static final String COL_END_TIME = "end_time";
}