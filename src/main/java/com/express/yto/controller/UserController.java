package com.express.yto.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dao.SysUserMapper;
import com.express.yto.dto.RestResult;
import com.express.yto.model.SysUser;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
@SaCheckLogin
public class UserController {

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/page")
    public RestResult<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName) {
        
        Page<SysUser> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(SysUser::getRealName, realName);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        return RestResult.ok(sysUserMapper.selectPage(page, wrapper));
    }

    @GetMapping("/{id}")
    public RestResult<SysUser> detail(@PathVariable Long id) {
        return RestResult.ok(sysUserMapper.selectById(id));
    }

    @PostMapping("/add")
    @SaCheckRole("ADMIN")
    public RestResult<String> add(@RequestBody SysUser user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        return RestResult.ok("新增成功");
    }

    @PostMapping("/update")
    @SaCheckRole("ADMIN")
    public RestResult<String> update(@RequestBody SysUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserMapper.updateById(user);
        return RestResult.ok("更新成功");
    }

    @PostMapping("/delete")
    @SaCheckRole("ADMIN")
    public RestResult<String> delete(@RequestBody Long id) {
        sysUserMapper.deleteById(id);
        return RestResult.ok("删除成功");
    }

    @PostMapping("/status")
    @SaCheckRole("ADMIN")
    public RestResult<String> status(@RequestParam Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserMapper.updateById(user);
        return RestResult.ok("更新成功");
    }
}
