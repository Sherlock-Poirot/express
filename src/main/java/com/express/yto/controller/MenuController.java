package com.express.yto.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.express.yto.dto.MenuTreeDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单控制�?
 */
@RestController
@RequestMapping("/menu")
@SaCheckLogin
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取当前用户的菜单树
     */
    @GetMapping("/tree")
    public RestResult<List<MenuTreeDTO>> getMenuTree() {
        List<MenuTreeDTO> menuTree = menuService.getCurrentUserMenuTree();
        return RestResult.ok(menuTree);
    }

    /**
     * 获取所有菜单树（超级管理员�?
     */
    @GetMapping("/all-tree")
    @SaCheckRole("ADMIN")
    public RestResult<List<MenuTreeDTO>> getAllMenuTree() {
        List<MenuTreeDTO> menuTree = menuService.getAllMenuTree();
        return RestResult.ok(menuTree);
    }
}