package com.express.yto.service;

import com.express.yto.dto.MenuTreeDTO;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService {

    /**
     * 获取当前用户的菜单树
     */
    List<MenuTreeDTO> getCurrentUserMenuTree();

    /**
     * 获取所有菜单树（超级管理员）
     */
    List<MenuTreeDTO> getAllMenuTree();
}