package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/10/31
 */
@Data
public class ShopEmpExcelDTO {

    @ExcelProperty("客户编码")
    private String code;

    @ExcelProperty("客户名称")
    private String name;

    @ExcelProperty("店铺名称")
    private String shopName;

    @ExcelProperty("平台")
    private String platform;

    @ExcelProperty("承包区名称")
    private String empName;

    @ExcelProperty("承包区客户类型")
    private String empType;
}
