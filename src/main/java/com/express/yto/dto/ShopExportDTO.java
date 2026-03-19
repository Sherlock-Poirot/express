package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/10/31
 */
@Data
public class ShopExportDTO {

    @ExcelProperty("客户编码")
    private String kCode;

    /**
     * 平台
     */
    @ExcelProperty(value = "平台")
    private String platform;

    /**
     * 客户名称
     */
    @ExcelProperty(value = "客户名称")
    private String kName;


    /**
     * 店铺名称
     */
    @ExcelProperty(value = "店铺名称")
    private String shopName;

}
