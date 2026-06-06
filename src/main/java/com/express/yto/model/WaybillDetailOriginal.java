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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_waybill_detail_original")
public class WaybillDetailOriginal {

    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    @TableField(value = "waybill_no")
    @ExcelProperty("运单号码")
    private String waybillNo;

    @TableField(value = "scan_time")
    @ExcelProperty("扫描时间")
    private LocalDate scanTime;

    @TableField(value = "weight")
    @ExcelProperty("计费重量（kg）")
    private BigDecimal weight;

    @TableField(value = "province")
    @ExcelProperty("计费省份")
    private String province;

    @TableField(value = "destination")
    @ExcelProperty("计费目的地名称")
    private String destination;

    @TableField(value = "salesman_name")
    @ExcelProperty("物料业务员名称")
    private String salesmanName;

    @TableField(value = "send_customer")
    @ExcelProperty("物料发放客户")
    private String sendCustomer;

    @TableField(value = "send_customer_name")
    @ExcelProperty("物料发放客户名称")
    private String sendCustomerName;

    @TableField(value = "settle_code")
    @ExcelProperty("物料结算编码")
    private String settleCode;

    @TableField(value = "settle_name")
    @ExcelProperty("物料结算名称")
    private String settleName;

    @TableField(value = "material_type")
    @ExcelProperty("物料类型")
    private String materialType;

    @TableField(value = "extra_fee")
    @ExcelProperty("加收")
    private BigDecimal extraFee;

    @TableField(value = "express_fee")
    @ExcelProperty("快递费")
    private BigDecimal expressFee;

    @TableField(value = "bill_month")
    @ExcelIgnore
    private String billMonth;

    @TableField(value = "create_time")
    @ExcelIgnore
    private LocalDateTime createTime;

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