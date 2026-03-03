package com.bupt.insightguard.task;

import com.bupt.insightguard.entity.HealthData;
import com.bupt.insightguard.repository.HealthDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class HeartbeatSimulator {
    private final HealthDataRepository healthDataRepository;
    private final Random random = new Random();
    public HeartbeatSimulator(HealthDataRepository healthDataRepository) {
        this.healthDataRepository = healthDataRepository;
    }
    // 每隔 1000 毫秒（1秒）执行一次
    @Scheduled(fixedRate = 1000)
    public void generateMockData() {
        // 生成随机生理指标
        int heartRate = 70 + random.nextInt(41); // 70-110 BPM
        int breathRate = 12 + random.nextInt(9);  // 12-20 次/分

        // 简单的风险判定逻辑 (PTSD 预警初步模型)
        // 如果心率超过 100，暂定为异常状态 1
        int status = (heartRate > 100) ? 1 : 0;

        // 创建实体对象 (手动设置数据)
        HealthData data = new HealthData();
        data.setUserId(1L); // 暂时模拟 1 号患者
        data.setHeartRate(heartRate);
        data.setBreathRate(breathRate);
        data.setStatus(status);
        data.setRecordTime(LocalDateTime.now());

        healthDataRepository.save(data);              // 执行保存


        // 打印日志，方便我们在控制台观察
        System.out.println("成功模拟一条数据 -> 心率: " + heartRate + ", 呼吸: " + breathRate + ", 状态: " + (status == 1 ? "【预警】" : "正常"));
    }
}
