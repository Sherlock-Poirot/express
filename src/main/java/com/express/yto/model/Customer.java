package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2025/9/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_customer")
public class Customer {
    /**
     * id 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户名称
     */
    @TableField(value = "cust_name")
    private String custName;

    /**
     * 客户K码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 海南是否三区
     */
    @TableField(value = "is_three")
    private Boolean threeFlag;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 4区加收比例
     */
    @TableField(value = "four_rate")
    private BigDecimal fourRate;


    /**
     * 4区加收比例
     */
    @TableField(value = "four_model")
    private String fourModel;

    /**
     * 4区加收金额
     */
    @TableField(value = "four_fee")
    private BigDecimal fourFee;

    /**
     * 算法类型，1.续重算法，2.凑整算法，3.实重算法
     * 续重重量=（重量-1）例：3.85=（4-1）=3公斤
     * 凑整重量=实际重量凑整 例：3.85=4公斤
     * 实重重量=实际重量 例：3.85=3.85公斤
     */
    @TableField(value = "type")
    private Integer type;

}