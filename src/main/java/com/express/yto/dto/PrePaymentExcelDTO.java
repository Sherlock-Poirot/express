package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/24
 */
@Data
public class PrePaymentExcelDTO {

    /**
     * 客户K码
     */
    @ExcelProperty("客户编码")
    private String code;

    /**
     * 客户名称
     */
    @ExcelProperty("客户名称")
    private String name;

    /**
     * 预付款
     */
    @ExcelProperty(value = "预付款")
    private BigDecimal preFee;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private LocalDate endTime;
}
