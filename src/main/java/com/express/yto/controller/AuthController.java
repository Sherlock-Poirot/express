package com.express.yto.controller;
import cn.dev33.satoken.annotation.SaCheckLogin;
import com.express.yto.dto.LoginInput;
import com.express.yto.dto.LoginOutput;
import com.express.yto.dto.RestResult;
import com.express.yto.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 认证控制�? */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public RestResult<LoginOutput> login(@RequestBody LoginInput input) {
        LoginOutput output = authService.login(input);
        return RestResult.ok(output);
    }
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @SaCheckLogin
    public RestResult logout() {
        authService.logout();
        return RestResult.ok("登出成功");
    }
    /**
     * 获取当前登录用户信息
     */
    @PostMapping("/current")
    @SaCheckLogin
    public RestResult<LoginOutput> getCurrentUser() {
        LoginOutput output = authService.getCurrentUser();
        return RestResult.ok(output);
    }
}