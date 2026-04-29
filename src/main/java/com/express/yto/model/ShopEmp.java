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
 * @date Created in 2025/10/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_shop_emp")
public class ShopEmp {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    /**
     * 客户K码
     */
    @TableField(value = "code")
    @ExcelProperty("客户编码")
    private String code;

    /**
     * 客户名称
     */
    @TableField(value = "cust_name")
    @ExcelProperty("客户名称")
    private String custName;

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
    @ExcelProperty("店铺名称")
    private String shopName;

    /**
     * 平台
     */
    @TableField(value = "platform")
    @ExcelProperty("平台")
    private String platform;

    /**
     * 承包区名称
     */
    @TableField(value = "emp_name")
    @ExcelProperty("承包区名称")
    private String empName;

    /**
     * 承包区客户类型
     */
    @TableField(value = "emp_type")
    @ExcelProperty("承包区客户类型")
    private String empType;

    public static final String COL_ID = "id";

    public static final String COL_CODE = "code";

    public static final String COL_CUST_NAME = "cust_name";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_SHOP_NAME = "shop_name";

    public static final String COL_PLATFORM = "platform";

    public static final String COL_EMP_NAME = "emp_name";

    public static final String COL_EMP_TYPE = "emp_type";
}