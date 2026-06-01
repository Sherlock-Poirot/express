package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginOutput {

    /**
     * Token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 角色列表
     */
    private java.util.List<String> roles;

    /**
     * 权限列表
     */
    private java.util.List<String> permissions;
}