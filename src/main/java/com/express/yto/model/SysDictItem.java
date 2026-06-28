package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典项表实体
 * 用于管理字典值（如：报销类型下的交通费、餐饮费等）
 */
@Data
@TableName("t_sys_dict_item")
public class SysDictItem {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("dict_code")
    private String dictCode;

    @TableField("dict_value")
    private String dictValue;

    @TableField("dict_label")
    private String dictLabel;

    @TableField("description")
    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public static final String COL_ID = "id";
    public static final String COL_DICT_CODE = "dict_code";
    public static final String COL_DICT_VALUE = "dict_value";
    public static final String COL_DICT_LABEL = "dict_label";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_SORT_ORDER = "sort_order";
    public static final String COL_STATUS = "status";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_TIME = "update_time";
}
