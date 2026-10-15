package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应收明细-面单流水明细实体
 */
@Data
@TableName("t_recharge_flow_detail")
public class RechargeFlowDetail {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("from_shop_id")
    private String fromShopId;

    @TableField("from_shop_name")
    private String fromShopName;

    @TableField("from_cust_code")
    private String fromCustCode;

    @TableField("from_cust_name")
    private String fromCustName;

    @TableField("to_shop_id")
    private String toShopId;

    @TableField("to_shop_name")
    private String toShopName;

    @TableField("to_cust_code")
    private String toCustCode;

    @TableField("to_cust_name")
    private String toCustName;

    @TableField("operate_type")
    private String operateType;

    @TableField("recharge_platform")
    private String rechargePlatform;

    @TableField("stock")
    private Integer stock;

    @TableField("operate_qty")
    private Integer operateQty;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("pay_type")
    private String payType;

    @TableField("special_remark")
    private String specialRemark;

    @TableField("after_stock")
    private Integer afterStock;

    @TableField("operate_time")
    private LocalDateTime operateTime;

    @TableField("operator")
    private String operator;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
