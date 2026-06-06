package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.express.yto.dao.UserRoleMapper;
import com.express.yto.model.UserRole;
import com.express.yto.service.UserRoleService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> UserRole.builder()
                            .userId(userId)
                            .roleId(roleId)
                            .createTime(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            
            for (UserRole userRole : userRoles) {
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @Transactional
    public void addRolesToUser(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        
        List<Long> existRoleIds = getRoleIdsByUserId(userId);
        
        List<Long> newRoleIds = roleIds.stream()
                .filter(id -> !existRoleIds.contains(id))
                .collect(Collectors.toList());
        
        if (!newRoleIds.isEmpty()) {
            List<UserRole> userRoles = newRoleIds.stream()
                    .map(roleId -> UserRole.builder()
                            .userId(userId)
                            .roleId(roleId)
                            .createTime(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            
            for (UserRole userRole : userRoles) {
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @Transactional
    public void removeRolesFromUser(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId)
               .in(UserRole::getRoleId, roleIds);
        userRoleMapper.delete(wrapper);
    }
}
