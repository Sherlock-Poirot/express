package com.express.yto.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/20
 */
@Data
public class CustomerInput {

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户K码
     */
    private String code;

    /**
     * 海南是否三区
     */
    private Boolean threeFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 4区加收比例
     */
    private BigDecimal fourRate;


    /**
     * 4区加收模式
     */
    private String fourModel;

    /**
     * 4区加收金额
     */
    private BigDecimal fourFee;

    /**
     * 算法类型，1.续重算法，2.凑整算法，3.实重算法
     * 续重重量=（重量-1）例：3.85=（4-1）=3公斤
     * 凑整重量=实际重量凑整 例：3.85=4公斤
     * 实重重量=实际重量 例：3.85=3.85公斤
     */
    private Integer type;
}
