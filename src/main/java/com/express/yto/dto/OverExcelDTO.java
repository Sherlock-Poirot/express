package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/3/17
 */
@Data
public class OverExcelDTO{

    @ExcelProperty("客户编码")
    private String code;

    @ExcelProperty("区域")
    private Integer area;

    @ExcelProperty("价格")
    private BigDecimal fee;

    @ExcelProperty("开始时间")
    private LocalDate startTime;

    @ExcelProperty("结束时间")
    private LocalDate endTime;

    @ExcelProperty("首重重量")
    private BigDecimal firstWeight;

    @ExcelProperty("首重价格")
    private BigDecimal firstFee;
}
