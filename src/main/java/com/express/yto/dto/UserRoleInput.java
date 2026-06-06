package com.express.yto.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * 用户角色分配输入
 */
@Data
public class UserRoleInput {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("角色ID列表")
    private List<Long> roleIds;
}
