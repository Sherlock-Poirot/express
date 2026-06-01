package com.express.yto.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("明文密码: " + rawPassword);
        System.out.println("BCrypt加密: " + encodedPassword);
        System.out.println("\n验证匹配: " + encoder.matches(rawPassword, encodedPassword));
    }
}
