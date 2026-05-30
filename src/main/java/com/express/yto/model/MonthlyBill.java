package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_monthly_bill")
public class MonthlyBill {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "bill_month")
    private String billMonth;

    @TableField(value = "cust_name")
    private String custName;

    @TableField(value = "code")
    private String code;

    @TableField(value = "receive_count")
    private Integer receiveCount;

    @TableField(value = "avg_weight")
    private BigDecimal avgWeight;

    @TableField(value = "receivable_amount")
    private BigDecimal receivableAmount;

    @TableField(value = "special_remark")
    private String specialRemark;

    @TableField(value = "adjust_amount")
    private BigDecimal adjustAmount;

    @TableField(value = "transfer_type")
    private String transferType;

    @TableField(value = "actual_amount")
    private BigDecimal actualAmount;

    @TableField(value = "transfer_date")
    private LocalDate transferDate;

    @TableField(value = "remark")
    private String remark;

    @TableField(value = "verify_sign")
    private String verifySign;

    @TableField(value = "type")
    private Integer type;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}