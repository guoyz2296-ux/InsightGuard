package com.bupt.insightguard.config;

import com.bupt.insightguard.entity.PatientInfo;
import com.bupt.insightguard.repository.PatientInfoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(PatientInfoRepository repository) {
        return args -> {
            // 1. 检查是否已经初始化过，防止重复插入
            if (repository.findByUsername("test_family").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                PatientInfo testUser = new PatientInfo();
                testUser.setUsername("test_family"); // 登录账号
                testUser.setName("测试家属"); // 患者姓名
                testUser.setAge(20);
                testUser.setEmergencyContact("13800000000");

                // 关键点：存入数据库的必须是加密后的哈希值
                testUser.setPassword(encoder.encode("180323")); // 这里设置初始密码

                repository.save(testUser);
                System.out.println(">>> [InsightGuard] 测试账号初始化成功：test_family / 180323");
            }
        };
    }
}
