package com.express.yto.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.express.yto.dto.RestResult;
import com.express.yto.dto.UserRoleInput;
import com.express.yto.service.UserRoleService;
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
 * 用户角色关联控制器
 */
@Api(tags = "用户角色管理")
@RestController
@RequestMapping("/user-role")
@SaCheckLogin
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @ApiOperation("获取用户已分配的角色ID列表")
    @GetMapping("/role-ids/{userId}")
    public RestResult<List<Long>> getRoleIdsByUserId(@PathVariable Long userId) {
        List<Long> roleIds = userRoleService.getRoleIdsByUserId(userId);
        return RestResult.ok(roleIds);
    }

    @ApiOperation("给用户分配角色（覆盖式）")
    @PostMapping("/assign")
    @SaCheckRole("ADMIN")
    public RestResult<String> assignRolesToUser(@RequestBody UserRoleInput input) {
        userRoleService.assignRolesToUser(input.getUserId(), input.getRoleIds());
        return RestResult.ok("分配成功");
    }

    @ApiOperation("给用户添加角色（追加式）")
    @PostMapping("/add")
    @SaCheckRole("ADMIN")
    public RestResult<String> addRolesToUser(@RequestBody UserRoleInput input) {
        userRoleService.addRolesToUser(input.getUserId(), input.getRoleIds());
        return RestResult.ok("添加成功");
    }

    @ApiOperation("移除用户的角色")
    @PostMapping("/remove")
    @SaCheckRole("ADMIN")
    public RestResult<String> removeRolesFromUser(@RequestBody UserRoleInput input) {
        userRoleService.removeRolesFromUser(input.getUserId(), input.getRoleIds());
        return RestResult.ok("移除成功");
    }
}
