package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentLoopMerge;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.express.yto.util.AreaConverter;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/10/28
 */
@Data
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER )
public class OverFeeExcelMergeDTO {

    @ExcelIgnore
    private String kCode;

//    @ContentLoopMerge(eachRow = 5)
    @ExcelProperty("客户名称")
    private String kName;

//    @ContentLoopMerge(eachRow = 5)
    @ExcelProperty("开始时间")
    private LocalDate startTime;

//    @ContentLoopMerge(eachRow = 5)
    @ExcelProperty("结束时间")
    private LocalDate endTime;

//    @ContentLoopMerge(eachRow = 5)
    @ExcelProperty("预付款")
    private BigDecimal prePayment;

    @ExcelProperty(value = "区域", converter = AreaConverter.class)
    private Integer area;

    @ExcelProperty("续重费用")
    private BigDecimal fee;

    @ExcelProperty("首重费用")
    private BigDecimal firstFee;
}
