package com.express.yto.service;

import java.util.List;

/**
 * 用户角色关联服务接口
 */
public interface UserRoleService {

    /**
     * 获取用户已分配的角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 给用户分配角色（覆盖式）
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 给用户添加角色（追加式）
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void addRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 移除用户的角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void removeRolesFromUser(Long userId, List<Long> roleIds);
}
