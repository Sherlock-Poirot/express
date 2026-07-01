package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class KpiSignRecordImportDTO {

    @ExcelProperty("数据日期")
    private String dataDate;

    @ExcelProperty("运单号")
    private String waybillNo;

    @ExcelProperty("催查/上报/投诉时间")
    private String reportTime;

    @ExcelProperty("虚假签收大类")
    private String fakeSignType;

    @ExcelProperty("签收时间")
    private String signTime;

    @ExcelProperty("精准派")
    private String precisionDelivery;

    @ExcelProperty("签前电联")
    private String preSignCall;

    @ExcelProperty("小件员编码")
    private String courierCode;

    @ExcelProperty("小件员名称")
    private String courierName;
}