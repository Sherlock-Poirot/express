package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginInput {

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}