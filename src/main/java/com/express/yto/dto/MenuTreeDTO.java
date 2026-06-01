package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单树DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuTreeDTO {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 权限编码
     */
    private String menuCode;

    /**
     * 菜单类型（1目录，2菜单，3按钮）
     */
    private Integer menuType;

    /**
     * 前端路由路径
     */
    private String path;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 子菜单列表
     */
    private List<MenuTreeDTO> children;
}