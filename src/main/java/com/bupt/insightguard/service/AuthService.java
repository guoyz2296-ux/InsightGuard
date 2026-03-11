package com.bupt.insightguard.service;

import com.bupt.insightguard.entity.PatientInfo;
import com.bupt.insightguard.repository.PatientInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PatientInfoRepository patientRepo;

    // 实例化加密器
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 登录校验逻辑
    public String login(String username, String rawPassword) {
        return patientRepo.findByUsername(username)
                .map(patient -> {
                    // 使用 BCrypt 比对明文和哈希值
                    if (encoder.matches(rawPassword, patient.getPassword())) {
                        return "登录成功！欢迎家属：" + patient.getName();
                    } else {
                        return "密码错误";
                    }
                })
                .orElse("用户不存在");
    }
}