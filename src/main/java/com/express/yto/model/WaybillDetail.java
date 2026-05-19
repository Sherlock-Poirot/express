package com.express.yto.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_waybill_detail")
public class WaybillDetail {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    /**
     * 运单号码
     */
    @TableField(value = "waybill_no")
    @ExcelProperty("运单号码")
    private String waybillNo;

    /**
     * 扫描时间（yyyy-MM-dd）
     */
    @TableField(value = "scan_time")
    @ExcelProperty("扫描时间")
    private LocalDate scanTime;

    /**
     * 计费重量（kg）两位小数
     */
    @TableField(value = "weight")
    @ExcelProperty("计费重量（kg）")
    private BigDecimal weight;

    /**
     * 计费省份
     */
    @TableField(value = "province")
    @ExcelProperty("计费省份")
    private String province;

    /**
     * 计费目的地名称
     */
    @TableField(value = "destination")
    @ExcelProperty("计费目的地名称")
    private String destination;

    /**
     * 物料业务员名称
     */
    @TableField(value = "salesman_name")
    @ExcelProperty("物料业务员名称")
    private String salesmanName;

    /**
     * 物料发放客户
     */
    @TableField(value = "send_customer")
    @ExcelProperty("物料发放客户")
    private String sendCustomer;

    /**
     * 物料发放客户名称
     */
    @TableField(value = "send_customer_name")
    @ExcelProperty("物料发放客户名称")
    private String sendCustomerName;

    /**
     * 物料结算编码
     */
    @TableField(value = "settle_code")
    @ExcelProperty("物料结算编码")
    private String settleCode;

    /**
     * 物料结算名称
     */
    @TableField(value = "settle_name")
    @ExcelProperty("物料结算名称")
    private String settleName;

    /**
     * 物料类型
     */
    @TableField(value = "material_type")
    @ExcelProperty("物料类型")
    private String materialType;

    /**
     * 加收（金额）
     */
    @TableField(value = "extra_fee")
    @ExcelProperty("加收")
    private BigDecimal extraFee;

    /**
     * 快递费（金额）
     */
    @TableField(value = "express_fee")
    @ExcelProperty("快递费")
    private BigDecimal expressFee;

    /**
     * 账单月份 yyyy-MM
     */
    @TableField(value = "bill_month")
    @ExcelIgnore
    private String billMonth;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ExcelIgnore
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ExcelIgnore
    private LocalDateTime updateTime;

    public static final String COL_ID = "id";

    public static final String COL_WAYBILL_NO = "waybill_no";

    public static final String COL_SCAN_TIME = "scan_time";

    public static final String COL_WEIGHT = "weight";

    public static final String COL_PROVINCE = "province";

    public static final String COL_DESTINATION = "destination";

    public static final String COL_SALESMAN_NAME = "salesman_name";

    public static final String COL_SEND_CUSTOMER = "send_customer";

    public static final String COL_SEND_CUSTOMER_NAME = "send_customer_name";

    public static final String COL_SETTLE_CODE = "settle_code";

    public static final String COL_SETTLE_NAME = "settle_name";

    public static final String COL_MATERIAL_TYPE = "material_type";

    public static final String COL_EXTRA_FEE = "extra_fee";

    public static final String COL_EXPRESS_FEE = "express_fee";

    public static final String COL_BILL_MONTH = "bill_month";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}