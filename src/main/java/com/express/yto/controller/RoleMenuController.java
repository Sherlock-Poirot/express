package com.express.yto.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.express.yto.dto.MenuTreeDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.dto.RoleMenuInput;
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

    @ApiOperation("获取角色已分配的菜单ID列表")
    @GetMapping("/menu-ids/{roleId}")
    public RestResult<List<Long>> getMenuIdsByRoleId(@PathVariable Long roleId) {
        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        return RestResult.ok(menuIds);
    }

    @ApiOperation("获取角色已分配的菜单树")
    @GetMapping("/menus/{roleId}")
    public RestResult<List<MenuTreeDTO>> getMenusByRoleId(@PathVariable Long roleId) {
        List<MenuTreeDTO> menus = roleMenuService.getMenusByRoleId(roleId);
        return RestResult.ok(menus);
    }

    @ApiOperation("给角色分配菜单（覆盖式）")
    @PostMapping("/assign")
    @SaCheckRole("ADMIN")
    public RestResult<String> assignMenusToRole(@RequestBody RoleMenuInput input) {
        roleMenuService.assignMenusToRole(input.getRoleId(), input.getMenuIds());
        return RestResult.ok("分配成功");
    }

    @ApiOperation("给角色添加菜单（追加式）")
    @PostMapping("/add")
    @SaCheckRole("ADMIN")
    public RestResult<String> addMenusToRole(@RequestBody RoleMenuInput input) {
        roleMenuService.addMenusToRole(input.getRoleId(), input.getMenuIds());
        return RestResult.ok("添加成功");
    }

    @ApiOperation("移除角色的菜单")
    @PostMapping("/remove")
    @SaCheckRole("ADMIN")
    public RestResult<String> removeMenusFromRole(@RequestBody RoleMenuInput input) {
        roleMenuService.removeMenusFromRole(input.getRoleId(), input.getMenuIds());
        return RestResult.ok("移除成功");
    }
}
