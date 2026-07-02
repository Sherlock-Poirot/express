package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollectionRecordImportDTO {

    @ExcelProperty("运单号")
    private String waybillNo;

    @ExcelProperty("应揽收时间")
    private LocalDateTime expectedCollectionTime;

    @ExcelProperty("实际揽收时间")
    private LocalDateTime actualCollectionTime;

    @ExcelProperty("订单号")
    private String orderNo;

    @ExcelProperty("业务员姓名")
    private String salesmanName;
}