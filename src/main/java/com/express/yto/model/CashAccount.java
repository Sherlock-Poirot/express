package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资金账户表
 * 同一平台（银行/微信/支付宝）可有多个账户（如私户、公户），流水按账户维度分类
 * @author Detective
 * @date Created in 2026/10/18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_cash_account")
public class CashAccount {

    public static final String COL_ID = "id";
    public static final String COL_ACCOUNT_NAME = "account_name";
    public static final String COL_ACCOUNT_NO = "account_no";
    public static final String COL_PLATFORM = "platform";
    public static final String COL_REMARK = "remark";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 账户名称 */
    @TableField(value = "account_name")
    private String accountName;

    /** 账号 */
    @TableField(value = "account_no")
    private String accountNo;

    /** 平台类型（1微信，2支付宝，3台州银行，4工商银行，5农商银行） */
    @TableField(value = "platform")
    private Integer platform;

    /** 备注 */
    @TableField(value = "remark")
    private String remark;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
