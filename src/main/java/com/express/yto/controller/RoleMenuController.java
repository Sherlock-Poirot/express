package com.express.yto.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.express.yto.dto.MenuTreeDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.service.RoleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色菜单关联控制器
 */
@Api(tags = "角色菜单管理")
@RestController
@RequestMapping("/role-menu")
@SaCheckLogin
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 获取角色已分配的菜单ID列表
     */
    @ApiOperation("获取角色已分配的菜单ID列表")
    @GetMapping("/menu-ids/{roleId}")
    public RestResult<List<Long>> getMenuIdsByRoleId(@PathVariable Long roleId) {
        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        return RestResult.ok(menuIds);
    }

    /**
     * 获取角色已分配的菜单树
     */
    @ApiOperation("获取角色已分配的菜单树")
    @GetMapping("/menus/{roleId}")
    public RestResult<List<MenuTreeDTO>> getMenusByRoleId(@PathVariable Long roleId) {
        List<MenuTreeDTO> menus = roleMenuService.getMenusByRoleId(roleId);
        return RestResult.ok(menus);
    }

    /**
     * 给角色分配菜单（覆盖式，会删除原有菜单后重新分配）
     */
    @ApiOperation("给角色分配菜单（覆盖式）")
    @PostMapping("/assign/{roleId}")
    @SaCheckRole("ADMIN")
    public RestResult<String> assignMenusToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        roleMenuService.assignMenusToRole(roleId, menuIds);
        return RestResult.ok("分配成功");
    }

    /**
     * 给角色添加菜单（追加式，只添加不存在的菜单）
     */
    @ApiOperation("给角色添加菜单（追加式）")
    @PostMapping("/add/{roleId}")
    @SaCheckRole("ADMIN")
    public RestResult<String> addMenusToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        roleMenuService.addMenusToRole(roleId, menuIds);
        return RestResult.ok("添加成功");
    }

    /**
     * 移除角色的菜单
     */
    @ApiOperation("移除角色的菜单")
    @PostMapping("/remove/{roleId}")
    @SaCheckRole("ADMIN")
    public RestResult<String> removeMenusFromRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        roleMenuService.removeMenusFromRole(roleId, menuIds);
        return RestResult.ok("移除成功");
    }
}
