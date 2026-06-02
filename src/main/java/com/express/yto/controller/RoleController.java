package com.express.yto.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dao.SysRoleMapper;
import com.express.yto.dto.RestResult;
import com.express.yto.model.SysRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/role")
@SaCheckLogin
public class RoleController {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @ApiOperation("获取所有角色列表")
    @GetMapping("/list")
    public RestResult<List<SysRole>> list() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getSortOrder);
        return RestResult.ok(sysRoleMapper.selectList(wrapper));
    }

    @ApiOperation("分页查询角色")
    @GetMapping("/page")
    public RestResult<Page<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleName) {
        
        Page<SysRole> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        
        wrapper.orderByAsc(SysRole::getSortOrder);
        return RestResult.ok(sysRoleMapper.selectPage(page, wrapper));
    }

    @ApiOperation("获取角色详情")
    @GetMapping("/{id}")
    public RestResult<SysRole> detail(@PathVariable Long id) {
        return RestResult.ok(sysRoleMapper.selectById(id));
    }

    @ApiOperation("新增角色")
    @PostMapping("/add")
    @SaCheckRole("ADMIN")
    public RestResult<String> add(@RequestBody SysRole role) {
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);
        return RestResult.ok("新增成功");
    }

    @ApiOperation("更新角色")
    @PostMapping("/update")
    @SaCheckRole("ADMIN")
    public RestResult<String> update(@RequestBody SysRole role) {
        sysRoleMapper.updateById(role);
        return RestResult.ok("更新成功");
    }

    @ApiOperation("删除角色")
    @PostMapping("/delete")
    @SaCheckRole("ADMIN")
    public RestResult<String> delete(@RequestBody Long id) {
        sysRoleMapper.deleteById(id);
        return RestResult.ok("删除成功");
    }
}
