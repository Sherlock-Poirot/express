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
@TableName(value = "t_fixed_fee")
public class FixedFee {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 客户K码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 地区 1，2，3，4，5区
     */
    @TableField(value = "area")
    private Integer area;

    /**
     * 重量
     */
    @TableField(value = "weight")
    private BigDecimal weight;

    /**
     * 费用
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

}