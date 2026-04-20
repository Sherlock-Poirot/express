package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/1/9
 */
@Data
public class BillCompileDTO {

    @ExcelProperty("客户名称")
    private String name;

    @ExcelProperty("数量")
    private Integer amount;

    @ExcelProperty("金额合计")
    private BigDecimal count;
}
