package com.express.yto.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.express.yto.dao.SysMenuMapper;
import com.express.yto.dao.SysRoleMapper;
import com.express.yto.dao.SysUserMapper;
import com.express.yto.dto.LoginInput;
import com.express.yto.dto.LoginOutput;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.SysUser;
import com.express.yto.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginOutput login(LoginInput input) {
        // 1. 查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, input.getUsername())
        );

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 验证密码（开发测试时支持明文密码 admin123）
        boolean passwordValid = passwordEncoder.matches(input.getPassword(), user.getPassword());
        // 临时测试用：如果是 admin 用户且密码是 admin123，也允许登录
        if (!passwordValid && "admin".equals(input.getUsername()) && "admin123".equals(input.getPassword())) {
            passwordValid = true;
        }
        if (!passwordValid) {
            throw new BusinessException("密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 4. Sa-Token 登录
        StpUtil.login(user.getId());

        // 5. 查询用户角色和权限
        List<String> roles = sysRoleMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = sysMenuMapper.selectPermissionsByUserId(user.getId());

        // 6. 构建响应
        return LoginOutput.builder()
                .token(StpUtil.getTokenValue())
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public LoginOutput getCurrentUser() {
        // 1. 获取当前登录用户ID
        Long userId = Long.valueOf(StpUtil.getLoginId().toString());

        // 2. 查询用户信息
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 查询角色和权限
        List<String> roles = sysRoleMapper.selectRoleCodesByUserId(userId);
        List<String> permissions = sysMenuMapper.selectPermissionsByUserId(userId);

        // 4. 构建响应
        return LoginOutput.builder()
                .token(StpUtil.getTokenValue())
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}