package com.express.yto.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_shop")
public class Shop {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    /**
     * 客户K码
     */
    @TableField(value = "k_code")
    @ExcelProperty("客户编码")
    private String kCode;

    /**
     * 客户名称
     */
    @TableField(value = "k_name")
    @ExcelProperty(value = "客户名称")
    private String kName;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    @ExcelIgnore
    private String shopId;

    /**
     * 店铺名称
     */
    @TableField(value = "shop_name")
    @ExcelProperty(value = "店铺名称")
    private String shopName;

    /**
     * 平台
     */
    @TableField(value = "platform")
    @ExcelProperty(value = "平台")
    private String platform;

}