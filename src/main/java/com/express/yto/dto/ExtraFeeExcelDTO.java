package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/24
 */
@Data
public class ExtraFeeExcelDTO {

    @ExcelProperty(value = "客户编码")
    private String kCode;

    @ExcelProperty(value = "客户名称")
    private String kName;

    @ExcelProperty(value = "地域名称")
    private String areaName;

    @ExcelProperty(value = "加收费用")
    private BigDecimal fee;
}
