package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.express.yto.dao.RoleMenuMapper;
import com.express.yto.dao.SysMenuMapper;
import com.express.yto.dto.MenuTreeDTO;
import com.express.yto.model.RoleMenu;
import com.express.yto.model.SysMenu;
import com.express.yto.service.RoleMenuService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuTreeDTO> getMenusByRoleId(Long roleId) {
        List<Long> menuIds = getMenuIdsByRoleId(roleId);
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getId, menuIds)
               .orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> menus = sysMenuMapper.selectList(wrapper);
        
        return buildMenuTree(menus);
    }

    @Override
    @Transactional
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        // 先删除角色原有的菜单
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);
        
        // 批量插入新分配的菜单
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> RoleMenu.builder()
                            .roleId(roleId)
                            .menuId(menuId)
                            .createTime(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            
            for (RoleMenu roleMenu : roleMenus) {
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    @Transactional
    public void addMenusToRole(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        
        // 获取已分配的菜单ID
        List<Long> existMenuIds = getMenuIdsByRoleId(roleId);
        
        // 过滤掉已存在的菜单
        List<Long> newMenuIds = menuIds.stream()
                .filter(id -> !existMenuIds.contains(id))
                .collect(Collectors.toList());
        
        if (!newMenuIds.isEmpty()) {
            List<RoleMenu> roleMenus = newMenuIds.stream()
                    .map(menuId -> RoleMenu.builder()
                            .roleId(roleId)
                            .menuId(menuId)
                            .createTime(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            
            for (RoleMenu roleMenu : roleMenus) {
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    @Transactional
    public void removeMenusFromRole(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId)
               .in(RoleMenu::getMenuId, menuIds);
        roleMenuMapper.delete(wrapper);
    }

    private List<MenuTreeDTO> buildMenuTree(List<SysMenu> menus) {
        return menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .map(menu -> buildTreeNode(menu, menus))
                .collect(Collectors.toList());
    }

    private MenuTreeDTO buildTreeNode(SysMenu menu, List<SysMenu> allMenus) {
        List<MenuTreeDTO> children = allMenus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> buildTreeNode(m, allMenus))
                .collect(Collectors.toList());

        return MenuTreeDTO.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .menuName(menu.getMenuName())
                .menuCode(menu.getMenuCode())
                .menuType(menu.getMenuType())
                .path(menu.getPath())
                .component(menu.getComponent())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .children(children.isEmpty() ? null : children)
                .build();
    }
}
