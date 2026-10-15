package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应收明细-面单流水明细表
 * @author Detective
 * @date Created in 2026/10/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_recharge_flow_detail")
public class RechargeFlowDetail {

    public static final String COL_ID = "id";
    public static final String COL_FROM_SHOP_ID = "from_shop_id";
    public static final String COL_FROM_SHOP_NAME = "from_shop_name";
    public static final String COL_FROM_CUST_CODE = "from_cust_code";
    public static final String COL_FROM_CUST_NAME = "from_cust_name";
    public static final String COL_TO_SHOP_ID = "to_shop_id";
    public static final String COL_TO_SHOP_NAME = "to_shop_name";
    public static final String COL_TO_CUST_CODE = "to_cust_code";
    public static final String COL_TO_CUST_NAME = "to_cust_name";
    public static final String COL_OPERATE_TYPE = "operate_type";
    public static final String COL_RECHARGE_PLATFORM = "recharge_platform";
    public static final String COL_STOCK = "stock";
    public static final String COL_OPERATE_QTY = "operate_qty";
    public static final String COL_UNIT_PRICE = "unit_price";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_PAY_TYPE = "pay_type";
    public static final String COL_SPECIAL_REMARK = "special_remark";
    public static final String COL_AFTER_STOCK = "after_stock";
    public static final String COL_OPERATE_TIME = "operate_time";
    public static final String COL_OPERATOR = "operator";
    public static final String COL_REMARK = "remark";
    public static final String COL_MATCH_STATUS = "match_status";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 来源店铺id */
    @TableField(value = "from_shop_id")
    private String fromShopId;

    /** 来源店铺名称 */
    @TableField(value = "from_shop_name")
    private String fromShopName;

    /** 来源客户编码 */
    @TableField(value = "from_cust_code")
    private String fromCustCode;

    /** 来源客户名称 */
    @TableField(value = "from_cust_name")
    private String fromCustName;

    /** 目的店铺id */
    @TableField(value = "to_shop_id")
    private String toShopId;

    /** 目的店铺名称 */
    @TableField(value = "to_shop_name")
    private String toShopName;

    /** 目的客户编码 */
    @TableField(value = "to_cust_code")
    private String toCustCode;

    /** 目的客户名称 */
    @TableField(value = "to_cust_name")
    private String toCustName;

    /** 操作类型 */
    @TableField(value = "operate_type")
    private String operateType;

    /** 充值平台 */
    @TableField(value = "recharge_platform")
    private String rechargePlatform;

    /** 库存 */
    @TableField(value = "stock")
    private Integer stock;

    /** 操作数量 */
    @TableField(value = "operate_qty")
    private Integer operateQty;

    /** 单价 */
    @TableField(value = "unit_price")
    private BigDecimal unitPrice;

    /** 金额 */
    @TableField(value = "amount")
    private BigDecimal amount;

    /** 支付方式 */
    @TableField(value = "pay_type")
    private String payType;

    /** 特殊备注 */
    @TableField(value = "special_remark")
    private String specialRemark;

    /** 操作后库存 */
    @TableField(value = "after_stock")
    private Integer afterStock;

    /** 操作时间 */
    @TableField(value = "operate_time")
    private LocalDateTime operateTime;

    /** 操作人 */
    @TableField(value = "operator")
    private String operator;

    /** 备注 */
    @TableField(value = "remark")
    private String remark;

    /** 匹配状态（0未匹配，1已匹配） */
    @TableField(value = "match_status")
    private Integer matchStatus;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
