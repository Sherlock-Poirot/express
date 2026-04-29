package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/20
 */
@Data
public class OverFeeExcelDTO {

    @ExcelProperty("预付金额")
    private BigDecimal prePayment;

    @ExcelProperty("客户名称")
    private String name;

    @ExcelProperty("客户编码")
    private String code;

    @ExcelProperty("一区首重")
    private BigDecimal areaOneFee;

    @ExcelProperty("一区续重")
    private BigDecimal areaOneExtraFee;

    @ExcelProperty("二区首重")
    private BigDecimal areaTwoFee;

    @ExcelProperty("二区续重")
    private BigDecimal areaTwoExtraFee;

    @ExcelProperty("三区首重")
    private BigDecimal areaThreeFee;

    @ExcelProperty("三区续重")
    private BigDecimal areaThreeExtraFee;

    @ExcelProperty("四区首重")
    private BigDecimal areaFourFee;

    @ExcelProperty("四区续重")
    private BigDecimal areaFourExtraFee;

    @ExcelProperty("五区首重")
    private BigDecimal areaFiveFee;

    @ExcelProperty("五区续重")
    private BigDecimal areaFiveExtraFee;

    @ExcelProperty("开始时间")
    private LocalDate startTime;

    @ExcelProperty("结束时间")
    private LocalDate endTime;
}
