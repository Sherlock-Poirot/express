package com.express.yto.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * 角色菜单分配输入
 */
@Data
public class RoleMenuInput {

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("菜单ID列表")
    private List<Long> menuIds;
}
