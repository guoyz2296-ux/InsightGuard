package com.bupt.insightguard.task;

import com.bupt.insightguard.entity.HealthRecord;
import com.bupt.insightguard.repository.HealthRecordRepository;
import com.bupt.insightguard.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class HealthDataSimulator {

    // 1. 在模拟器里注入 AiService
    @Autowired
    private AiService aiService;

    private final long startTime = System.currentTimeMillis(); // 记录模拟器启动时间（毫秒）

    // 剧本时间线（单位：秒）: {开始秒, 结束秒, 状态序号}
    private static final int[][] SCRIPT = {
            {0, 30, State.NORMAL.ordinal()},        // 0-30秒  正常
            {30, 50, State.NIGHTMARE.ordinal()},     // 30-50秒 梦魇
            {50, 80, State.NORMAL.ordinal()},        // 50-80秒 正常
            {80, 110, State.PANIC_ATTACK.ordinal()}, // 80-110秒 惊恐发作
            {110, 140, State.NORMAL.ordinal()}       // 110-140秒 正常
    };

    // 状态枚举（与生理参数范围绑定）
    private enum State {
        NORMAL(60, 80, 12, 18, 95, 100, 0.0f, 0.2f),
        NIGHTMARE(90, 120, 20, 30, 92, 96, 0.5f, 0.9f),
        PANIC_ATTACK(110, 150, 25, 40, 90, 94, 0.7f, 1.0f);

        final int hrMin, hrMax;       // 心率范围
        final int rrMin, rrMax;       // 呼吸范围
        final int spo2Min, spo2Max;   // 血氧范围
        final float motionMin, motionMax; // 体动范围

        State(int hrMin, int hrMax, int rrMin, int rrMax,
              int spo2Min, int spo2Max, float motionMin, float motionMax) {
            this.hrMin = hrMin; this.hrMax = hrMax;
            this.rrMin = rrMin; this.rrMax = rrMax;
            this.spo2Min = spo2Min; this.spo2Max = spo2Max;
            this.motionMin = motionMin; this.motionMax = motionMax;
        }
    }


    @Scheduled(fixedRate = 10000) // 每秒执行一次
    public void generateMockData() {
        long now = System.currentTimeMillis();
        long elapsedSeconds = (now - startTime) / 1000;

        // 1. 根据剧本时间线确定当前状态
        State currentState = State.NORMAL;
        for (int[] segment : SCRIPT) {
            if (elapsedSeconds >= segment[0] && elapsedSeconds < segment[1]) {
                currentState = State.values()[segment[2]];
                break;
            }
        }

        // 2. 在当前状态下随机生成生理数据
        int hr = randomInRange(currentState.hrMin, currentState.hrMax);
        int rr = randomInRange(currentState.rrMin, currentState.rrMax);
        int spo2 = randomInRange(currentState.spo2Min, currentState.spo2Max);
        float motion = currentState.motionMin + (float) Math.random() *
                (currentState.motionMax - currentState.motionMin);
        float noise = 30.0f + (float) Math.random() * 60.0f; // 30~90 dB




        // 3. 保存到数据库
        String aiAdvice = aiService.getAndSaveHealthAdvice(
                1001L,         // patientId
                null,          // recordId 传 null，AiService 里的 orElse(new HealthRecord()) 会起作用
                hr, rr, spo2, motion, noise
        );

        // 6. 可选：打印日志以便观察
        System.out.printf("[模拟器] 状态=%-12s 心率=%3d 呼吸=%2d 血氧=%3d 体动=%.2f 噪音=%.1f 时间=%s%n",
                currentState, hr, rr, spo2, motion, noise,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private int randomInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}