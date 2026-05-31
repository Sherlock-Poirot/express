package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyBillExportDTO {

    @ExcelProperty("核实签字")
    private String verifySign;

    @ExcelProperty("日期")
    private String billMonth;

    @ExcelProperty("客户名称")
    private String custName;

    @ExcelProperty("收件量")
    private Integer receiveCount;

    @ExcelProperty("分界线")
    private String divideLine;

    @ExcelProperty("实际应付金额")
    private BigDecimal receivableAmount;

    @ExcelProperty("特殊备注")
    private String specialRemark;

    @ExcelProperty("转账方式")
    private String transferType;

    @ExcelProperty("已转金额")
    private BigDecimal actualAmount;

    @ExcelProperty("日期")
    private String transferDate;

    @ExcelProperty("备注")
    private String remark;
}