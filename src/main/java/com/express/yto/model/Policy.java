package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_policy")
public class Policy {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("policy_type")
    private Integer policyType;

    @TableField("weight_left")
    private BigDecimal weightLeft;

    @TableField("weight_right")
    private BigDecimal weightRight;

    @TableField("base_amount")
    private BigDecimal baseAmount;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
