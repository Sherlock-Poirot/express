package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
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
@TableName(value = "t_over_fee")
public class OverFee {
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
     * 地区
     */
    @TableField(value = "area")
    private Integer area;

    /**
     * 续重费用
     */
    @TableField(value = "fee")
    private BigDecimal fee;

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


    /**
     * 首重上限
     */
    @TableField(value = "first_weight")
    private BigDecimal firstWeight;

    /**
     * 首重费用
     */
    @TableField(value = "first_fee")
    private BigDecimal firstFee;

}