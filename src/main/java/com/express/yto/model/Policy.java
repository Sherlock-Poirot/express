package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 政策实体类
 * 用于存储政策管理相关数据
 */
@Data
@TableName("t_policy")
public class Policy {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 政策类型（必填）
     * 1-基数返利，2-固定收费，3-动态返利
     */
    @TableField("policy_type")
    private Integer policyType;

    /**
     * 重量左区间（kg）
     * 默认值：0
     */
    @TableField("weight_left")
    private BigDecimal weightLeft;

    /**
     * 重量右区间（kg）
     * 默认值：99999
     */
    @TableField("weight_right")
    private BigDecimal weightRight;

    /**
     * 基数（元）
     * 用于基数返利等政策类型的计算
     */
    @TableField("base_amount")
    private BigDecimal baseAmount;

    /**
     * 金额（元）（必填）
     * 政策对应的金额值
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
