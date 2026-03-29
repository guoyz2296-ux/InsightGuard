package com.bupt.insightguard.service;

import com.bupt.insightguard.entity.AiAdviceLog;
import com.bupt.insightguard.entity.HealthRecord;
import com.bupt.insightguard.repository.AiAdviceRepository;
import com.bupt.insightguard.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiService {

    @Value("${doubao.api.key}")
    private String apiKey;

    @Value("${doubao.model.id}")
    private String modelId;

    @Value("${doubao.api.url}")
    private String apiUrl;

    private final AiAdviceRepository aiAdviceRepo;
    private final HealthRecordRepository healthRepo;
    private final RestTemplate restTemplate;

    public AiService(AiAdviceRepository aiAdviceRepo,
                     HealthRecordRepository healthRepo,
                     RestTemplate restTemplate) {
        this.aiAdviceRepo = aiAdviceRepo;
        this.healthRepo = healthRepo;
        this.restTemplate = restTemplate;
    }

    public String getAndSaveHealthAdvice(Long patientId, Long recordId,
                                         int heartRate, int respRate, int spo2,
                                         float motionScale, float noise) {

        //构造提示词
        int hour = LocalDateTime.now().getHour();
        String timePeriod = (hour >= 8 && hour < 22) ? "白天 (Daytime)" : "夜间 (Nighttime)";

        String prompt = String.format(
                "### 角色\n" +
                        "你是一个专业的 PTSD 健康监护助手。当前监测时间点：[%s]\n\n" +
                        "### 输入数据\n" +
                        "- 生理指标：心率 %d bpm, 呼吸 %d 次/分, 血氧 %d%%\n" +
                        "- 环境/行为：体动权重 %.2f, 噪音 %.1f dB\n\n" +
                        "### 核心任务\n" +
                        "1. 判定状态 (STATUS)：NORMAL / STRESS / ATTACK\n" +
                        "2. 判定等级 (LEVEL)：1(正常), 2(轻度), 3(中度), 4(重度)\n" +
                        "   - ATTACK 必须对应 LEVEL 4\n" +
                        "3. 护理建议 (INSIGHT)：\n" +
                        "   - 必须基于当前 [%s] 的环境给出建议。\n" +
                        "   - 内容需包含：原因简析 + 针对性干预动作。\n\n" +
                        "### 输出规范（严格遵守，严禁废话）\n" +
                        "STATUS: [状态词]\n" +
                        "LEVEL: [数字]\n" +
                        "INSIGHT: [建议正文]",
                timePeriod, heartRate, respRate, spo2, motionScale, noise, timePeriod
        );
        // 2. 构造请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelId);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "你是一个专业的PTSD监护助手。"),
                Map.of("role", "user", "content", prompt)
        ));

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            String fullContent = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            // 3. 提取 AI 判定
            String statusCode = extractValue(fullContent, "STATUS");
            String levelRaw = extractValue(fullContent, "LEVEL").replaceAll("[^0-9]", "");
            int stressLevel = levelRaw.isEmpty() ? 1 : Integer.parseInt(levelRaw);
            String aiInsight = extractValue(fullContent, "INSIGHT");

            // 4. 【关键：双表同步】
            // 第一张表：日志表记录备份
            AiAdviceLog log = new AiAdviceLog();
            log.setPatientId(patientId);
            log.setHeart_rate_avg(heartRate);
            log.setAdviceContent(aiInsight);
            aiAdviceRepo.save(log);

            // 第二张表：主记录表更新（使用 save 自动判断 INSERT/UPDATE）
            HealthRecord record = healthRepo.findById(recordId).orElse(new HealthRecord());
            if (record.getId() == null) record.setId(recordId); // 保证主键对应

            record.setPatientId(patientId);
            record.setHeartRate(heartRate);
            record.setRespRate(respRate);
            record.setSpo2(spo2);
            record.setMotionScale(motionScale);
            record.setNoise(noise);
            record.setStatusCode(statusCode);
            record.setStressLevel(stressLevel);
            record.setAiInsight(aiInsight);
            record.setRecordTime(LocalDateTime.now());

            healthRepo.save(record); // 这一行就够了，删掉那个爆红的 update 方法调用

            return aiInsight;

        } catch (Exception e) {
            return "分析异常，请参考生理数据。";
        }
    }

    private String extractValue(String text, String key) {
        Pattern pattern = Pattern.compile(key + ":\\s*([\\s\\S]*?)(\\n[A-Z]+:|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "UNKNOWN";
    }
}