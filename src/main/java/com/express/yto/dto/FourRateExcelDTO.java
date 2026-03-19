package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2025/10/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FourRateExcelDTO {

    @ExcelProperty("客户")
    private String kName;

    @ExcelProperty("总票数")
    private BigDecimal amount;

    @ExcelProperty("四区票数")
    private BigDecimal fourCount;

    @ExcelProperty("四区占比")
    private String fourRate;
}
