package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyBillExcelDTO {
    
    @ExcelProperty("运单号")
    private String waybillNo;
    
    @ExcelProperty("客户编码")
    private String customerCode;
    
    @ExcelProperty("客户名称")
    private String customerName;
    
    @ExcelProperty("计费重量")
    private BigDecimal chargeWeight;
    
    @ExcelProperty("总金额")
    private BigDecimal totalAmount;
    
    @ExcelProperty("商家编码")
    private String merchantCode;
    
    @ExcelProperty("商家名称")
    private String merchantName;
    
    @ExcelProperty("结算目的地省份")
    private String settleProvince;
    
    @ExcelProperty("成功计费时间")
    private String chargeDate;
}