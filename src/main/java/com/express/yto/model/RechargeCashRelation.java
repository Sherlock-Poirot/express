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
 * 应收-实收关联关系表
 * @author Detective
 * @date Created in 2026/10/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_recharge_cash_relation")
public class RechargeCashRelation {

    public static final String COL_ID = "id";
    public static final String COL_RECHARGE_ID = "recharge_id";
    public static final String COL_CASH_ID = "cash_id";
    public static final String COL_RELATE_AMOUNT = "relate_amount";
    public static final String COL_RELATE_TIME = "relate_time";
    public static final String COL_OPERATOR = "operator";
    public static final String COL_REMARK = "remark";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 应收流水id（关联t_recharge_flow_detail.id） */
    @TableField(value = "recharge_id")
    private Long rechargeId;

    /** 实收流水id（关联t_cash_flow_detail.id） */
    @TableField(value = "cash_id")
    private Long cashId;

    /** 本次关联金额（一笔实收可能拆分关联多笔应收） */
    @TableField(value = "relate_amount")
    private BigDecimal relateAmount;

    /** 关联时间 */
    @TableField(value = "relate_time")
    private LocalDateTime relateTime;

    /** 操作人 */
    @TableField(value = "operator")
    private String operator;

    /** 备注 */
    @TableField(value = "remark")
    private String remark;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
