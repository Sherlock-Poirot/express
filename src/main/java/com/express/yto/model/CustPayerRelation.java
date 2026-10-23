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
 * 客户-付款方映射表
 * 自动匹配时通过资金流水的付款方名称（cust_name）锁定系统客户；
 * 数据来源：手动匹配成功时自动沉淀 + 手动维护
 * @author Detective
 * @date Created in 2026/10/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_cust_payer_relation")
public class CustPayerRelation {

    public static final String COL_ID = "id";
    public static final String COL_CUST_CODE = "cust_code";
    public static final String COL_PAYER_NAME = "payer_name";
    public static final String COL_ACCOUNT_ID = "account_id";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 客户编码（面单明细to_cust_code） */
    @TableField(value = "cust_code")
    private String custCode;

    /** 付款方名称（归一化后的稳定标识，前缀已剥离；剥离失败存原文） */
    @TableField(value = "payer_name")
    private String payerName;

    /** 常用收款账户id（可选，付款方名称歧义时辅助收敛） */
    @TableField(value = "account_id")
    private Long accountId;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
