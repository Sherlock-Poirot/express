package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/3/17
 */
@Data
public class FixedExcelDTO {

    @ExcelProperty("客户编码")
    private String code;

    @ExcelProperty("区域")
    private Integer area;

    @ExcelProperty("重量上限")
    private BigDecimal weight;

    @ExcelProperty("价格")
    private BigDecimal fee;

    @ExcelProperty("开始时间")
    private LocalDate startTime;

    @ExcelProperty("结束时间")
    private LocalDate endTime;
}
