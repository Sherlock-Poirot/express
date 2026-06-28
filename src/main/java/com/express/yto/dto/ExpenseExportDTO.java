package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报销导出DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseExportDTO {

    @ExcelProperty("报销类型")
    private String expenseTypeName;

    @ExcelProperty("报销名称")
    private String expenseName;

    @ExcelProperty("报销金额")
    private BigDecimal amount;

    @ExcelProperty("申请人")
    private String applicant;

    @ExcelProperty("报销日期")
    private LocalDate expenseDate;

    @ExcelProperty("所属月份")
    private String month;

    @ExcelProperty("审核状态")
    private String statusName;

    @ExcelProperty("转账时间")
    private LocalDate transferTime;

    @ExcelProperty("转账方式")
    private String transferTypeName;

    @ExcelProperty("备注")
    private String remark;
}
