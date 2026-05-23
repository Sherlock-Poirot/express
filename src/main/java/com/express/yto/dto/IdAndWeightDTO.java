package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/5/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdAndWeightDTO {

    @ExcelProperty("运单号")
    private String billId;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("复盘计费重量")
    private BigDecimal weight;
}
