package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/20
 */
@Data
public class AreaExtraFeeExcelDTO {

    @ExcelProperty("客户编码")
    private String code;

    @ExcelProperty("北京")
    private BigDecimal beijing;

    @ExcelProperty("上海")
    private BigDecimal shanghai;

    @ExcelProperty("深圳")
    private BigDecimal shenzhen;

    @ExcelProperty("舟山")
    private BigDecimal zhoushan;

}
