package com.express.yto.dto;


import java.util.List;
import lombok.Data;

/**
 * 角色菜单分配输入
 */
@Data
public class RoleMenuInput {

    private Long roleId;

    private List<Long> menuIds;
}
