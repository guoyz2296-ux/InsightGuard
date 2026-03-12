package com.bupt.insightguard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. 保留你需要的 BCrypt，这样你的加密逻辑不会崩
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 核心：定义一个过滤器链，允许所有请求（Permit All）
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF (Lambda 风格)
                .csrf(csrf -> csrf.disable())

                // 2. 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 允许所有请求，跳过登录弹窗
                )

                // 3. 禁用默认登录页
                .formLogin(form -> form.disable())

                // 4. 禁用 Basic 认证
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}