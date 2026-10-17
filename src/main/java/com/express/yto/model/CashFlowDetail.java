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
 * 实收流水表
 * @author Detective
 * @date Created in 2026/10/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_cash_flow_detail")
public class CashFlowDetail {

    public static final String COL_ID = "id";
    public static final String COL_OPERATE_TIME = "operate_time";
    public static final String COL_CUST_NAME = "cust_name";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_ACTUAL_TYPE = "actual_type";
    public static final String COL_MARK_TYPE = "mark_type";
    public static final String COL_PLATFORM = "platform";
    public static final String COL_CATEGORY = "category";
    public static final String COL_REMARK = "remark";
    public static final String COL_MATCH_STATUS = "match_status";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 操作时间 */
    @TableField(value = "operate_time")
    private LocalDateTime operateTime;

    /** 客户名称 */
    @TableField(value = "cust_name")
    private String custName;

    /** 金额 */
    @TableField(value = "amount")
    private BigDecimal amount;

    /** 实际收支类型（-1支出，1收入） */
    @TableField(value = "actual_type")
    private Integer actualType;

    /** 标记收支类型（-1支出，0不计收支，1收入） */
    @TableField(value = "mark_type")
    private Integer markType;

    /** 平台（关联字典platform） */
    @TableField(value = "platform")
    private Integer platform;

    /** 类目 */
    @TableField(value = "category")
    private String category;

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
