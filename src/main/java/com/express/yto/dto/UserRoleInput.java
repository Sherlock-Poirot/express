package com.express.yto.dto;


import java.util.List;
import lombok.Data;

/**
 * 用户角色分配输入
 */
@Data
public class UserRoleInput {

    private Long userId;

    private List<Long> roleIds;
}
