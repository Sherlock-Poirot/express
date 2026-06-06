package com.express.yto.service;

import com.express.yto.dto.MenuTreeDTO;
import java.util.List;

/**
 * 角色菜单关联服务接口
 */
public interface RoleMenuService {

    /**
     * 获取角色已分配的菜单ID列表
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 获取角色已分配的菜单树
     * @param roleId 角色ID
     * @return 菜单树列表
     */
    List<MenuTreeDTO> getMenusByRoleId(Long roleId);

    /**
     * 给角色分配菜单（覆盖式）
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 给角色添加菜单（追加式）
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void addMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 移除角色的菜单
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void removeMenusFromRole(Long roleId, List<Long> menuIds);
}
