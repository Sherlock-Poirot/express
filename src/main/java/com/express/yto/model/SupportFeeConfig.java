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

/**
 * 扶持派费配置表
 * @author Detective
 * @date Created in 2026/9/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_support_fee_config")
public class SupportFeeConfig {

    public static final String COL_ID = "id";
    public static final String COL_PROVINCE = "province";
    public static final String COL_CITY = "city";
    public static final String COL_EXTRA_FEE = "extra_fee";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";
    public static final String COL_REMARK = "remark";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 省份（精确匹配运单 province 字段） */
    @TableField(value = "province")
    private String province;

    /** 目的地市（模糊匹配运单 destination；为空表示该省全部加收） */
    @TableField(value = "city")
    private String city;

    /** 加收金额（元/单） */
    @TableField(value = "extra_fee")
    private BigDecimal extraFee;

    /** 生效开始日期（含当天） */
    @TableField(value = "start_time")
    private LocalDate startTime;

    /** 生效结束日期（不含当天） */
    @TableField(value = "end_time")
    private LocalDate endTime;

    /** 备注 */
    @TableField(value = "remark")
    private String remark;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
