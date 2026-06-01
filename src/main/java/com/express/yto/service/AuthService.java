package com.express.yto.service;

import com.express.yto.dto.LoginInput;
import com.express.yto.dto.LoginOutput;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginOutput login(LoginInput input);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     */
    LoginOutput getCurrentUser();
}