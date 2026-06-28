package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报销管理实体
 */
@Data
@TableName("t_expense")
public class Expense {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("expense_type")
    private Integer expenseType;

    @TableField("expense_name")
    private String expenseName;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("applicant")
    private String applicant;

    @TableField("expense_date")
    private LocalDate expenseDate;

    @TableField("month")
    private String month;

    @TableField("status")
    private Integer status;

    @TableField("transfer_time")
    private LocalDate transferTime;

    @TableField("transfer_type")
    private Integer transferType;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public static final String COL_ID = "id";
    public static final String COL_EXPENSE_TYPE = "expense_type";
    public static final String COL_EXPENSE_NAME = "expense_name";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_APPLICANT = "applicant";
    public static final String COL_EXPENSE_DATE = "expense_date";
    public static final String COL_MONTH = "month";
    public static final String COL_STATUS = "status";
    public static final String COL_TRANSFER_TIME = "transfer_time";
    public static final String COL_TRANSFER_TYPE = "transfer_type";
    public static final String COL_REMARK = "remark";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";
}
