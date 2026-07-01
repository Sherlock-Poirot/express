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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_kpi_sign_record")
public class KpiSignRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "data_date")
    private LocalDate dataDate;

    @TableField(value = "waybill_no")
    private String waybillNo;

    @TableField(value = "report_time")
    private LocalDateTime reportTime;

    @TableField(value = "fake_sign_type")
    private String fakeSignType;

    @TableField(value = "sign_time")
    private LocalDateTime signTime;

    @TableField(value = "precision_delivery")
    private Integer precisionDelivery;

    @TableField(value = "pre_sign_call")
    private Integer preSignCall;

    @TableField(value = "courier_code")
    private String courierCode;

    @TableField(value = "courier_name")
    private String courierName;

    @TableField(value = "branch_name")
    private String branchName;

    @TableField(value = "is_qualified")
    private Integer isQualified;

    @TableField(value = "efficiency_hours")
    private BigDecimal efficiencyHours;

    @TableField(value = "month")
    private String month;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}