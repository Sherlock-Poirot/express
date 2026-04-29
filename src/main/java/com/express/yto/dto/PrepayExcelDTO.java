package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/3/18
 */
@Data
public class PrepayExcelDTO {

    @ExcelProperty("客户编码")
    private String code;

    @ExcelProperty("价格")
    private BigDecimal preFee;

    @ExcelProperty("开始时间")
    private LocalDate startTime;

    @ExcelProperty("结束时间")
    private LocalDate endTime;
}
