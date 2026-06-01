package com.express.yto.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.express.yto.dao.SysMenuMapper;
import com.express.yto.dto.MenuTreeDTO;
import com.express.yto.model.SysMenu;
import com.express.yto.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<MenuTreeDTO> getCurrentUserMenuTree() {
        // 1. 获取当前用户ID
        Long userId = Long.valueOf(StpUtil.getLoginId().toString());

        // 2. 查询用户的菜单列表
        List<SysMenu> menus = sysMenuMapper.selectMenuTreeByUserId(userId);

        // 3. 转换为菜单树
        return buildMenuTree(menus);
    }

    @Override
    public List<MenuTreeDTO> getAllMenuTree() {
        // 1. 查询所有菜单
        List<SysMenu> menus = sysMenuMapper.selectAllMenuTree();

        // 2. 转换为菜单树
        return buildMenuTree(menus);
    }

    /**
     * 构建菜单树
     */
    private List<MenuTreeDTO> buildMenuTree(List<SysMenu> menus) {
        // 1. 按父ID分组
        Map<Long, List<SysMenu>> groupedByParentId = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 2. 递归构建树
        return buildTree(0L, groupedByParentId);
    }

    /**
     * 递归构建子树
     */
    private List<MenuTreeDTO> buildTree(Long parentId, Map<Long, List<SysMenu>> groupedByParentId) {
        List<SysMenu> children = groupedByParentId.get(parentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }

        return children.stream()
                .map(menu -> MenuTreeDTO.builder()
                        .id(menu.getId())
                        .parentId(menu.getParentId())
                        .menuName(menu.getMenuName())
                        .menuCode(menu.getMenuCode())
                        .menuType(menu.getMenuType())
                        .path(menu.getPath())
                        .component(menu.getComponent())
                        .icon(menu.getIcon())
                        .sortOrder(menu.getSortOrder())
                        .children(buildTree(menu.getId(), groupedByParentId))
                        .build())
                .collect(Collectors.toList());
    }
}