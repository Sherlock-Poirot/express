package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("t_daily_bill")
public class DailyBill {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("waybill_no")
    private String waybillNo;

    @TableField("customer_code")
    private String customerCode;

    @TableField("customer_name")
    private String customerName;

    @TableField("charge_weight")
    private BigDecimal chargeWeight;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("merchant_code")
    private String merchantCode;

    @TableField("merchant_name")
    private String merchantName;

    @TableField("settle_province")
    private String settleProvince;

    @TableField("charge_date")
    private LocalDate chargeDate;

    @TableField("rebate_amount")
    private BigDecimal rebateAmount;

    @TableField("customer_fee")
    private BigDecimal customerFee;

    @TableField("import_time")
    private LocalDateTime importTime;

    @TableField("bill_month")
    private String billMonth;
}