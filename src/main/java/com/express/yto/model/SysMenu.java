package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单权限实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_sys_menu")
public class SysMenu {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID（0为顶级）
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    private String menuName;

    /**
     * 权限编码（如：bill:list）
     */
    @TableField(value = "menu_code")
    private String menuCode;

    /**
     * 类型：1目录，2菜单，3按钮
     */
    @TableField(value = "menu_type")
    private Integer menuType;

    /**
     * 前端路由路径
     */
    @TableField(value = "path")
    private String path;

    /**
     * 前端组件路径
     */
    @TableField(value = "component")
    private String component;

    /**
     * 图标
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 后端接口URL
     */
    @TableField(value = "url")
    private String url;

    /**
     * HTTP方法（GET/POST/PUT/DELETE）
     */
    @TableField(value = "method")
    private String method;

    /**
     * 排序
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 状态：0禁用，1启用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    public static final String COL_ID = "id";
    public static final String COL_PARENT_ID = "parent_id";
    public static final String COL_MENU_NAME = "menu_name";
    public static final String COL_MENU_CODE = "menu_code";
    public static final String COL_MENU_TYPE = "menu_type";
    public static final String COL_PATH = "path";
    public static final String COL_COMPONENT = "component";
    public static final String COL_ICON = "icon";
    public static final String COL_URL = "url";
    public static final String COL_METHOD = "method";
    public static final String COL_SORT_ORDER = "sort_order";
    public static final String COL_STATUS = "status";
    public static final String COL_CREATE_TIME = "create_time";
}