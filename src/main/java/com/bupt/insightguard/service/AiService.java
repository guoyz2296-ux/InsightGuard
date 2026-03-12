package com.bupt.insightguard.service;

import com.bupt.insightguard.entity.AiAdviceLog;
import com.bupt.insightguard.repository.AiAdviceRepository;
import com.bupt.insightguard.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    // 从 application.yaml 中读取配置
    @Value("${doubao.api.key}")
    private String apiKey;

    @Value("${doubao.model.id}")
    private String modelId;

    @Value("${doubao.api.url}")
    private String apiUrl;

    private final AiAdviceRepository aiAdviceRepo;
    private final HealthRecordRepository healthRepo;
    private final RestTemplate restTemplate;

    // 构造注入
    public AiService(AiAdviceRepository aiAdviceRepo,
                     HealthRecordRepository healthRepo,
                     RestTemplate restTemplate) {
        this.aiAdviceRepo = aiAdviceRepo;
        this.healthRepo = healthRepo;
        this.restTemplate = restTemplate;
    }

    /**
     * 核心业务：获取 AI 建议并双向保存
     * @param patientId 患者ID
     * @param recordId 当前健康记录的ID（用于更新 ai_insight）
     * @param heartRate 当前心率
     */
    public String getAndSaveHealthAdvice(Long patientId, Long recordId, int heartRate) {
        // 1. 构造 Prompt（要求 AI 返回特定格式，方便我们手动拆分）
        String prompt = String.format(
                "患者当前心率 %d bpm。请严格按以下格式回答：\n" +
                        "即时评价：[5字以内]\n" +
                        "深度建议：[30字以内]", heartRate);

        // 2. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 3. 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelId);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "你是一个专业的老年人健康护理助手。"),
                Map.of("role", "user", "content", prompt)
        ));

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            // 4. 解析豆包返回的 JSON
            Map body = response.getBody();
            List choices = (List) body.get("choices");
            Map message = (Map) ((Map) choices.get(0)).get("message");
            String fullContent = (String) message.get("content");

            // 5. 简单的字符串拆分逻辑（混合模式核心）
            String shortAdvice = "心率正常"; // 默认值
            String longAdvice = fullContent;

            if (fullContent.contains("即时评价：") && fullContent.contains("深度建议：")) {
                shortAdvice = fullContent.substring(fullContent.indexOf("即时评价：") + 5, fullContent.indexOf("深度建议：")).trim();
                longAdvice = fullContent.substring(fullContent.indexOf("深度建议：") + 5).trim();
            }

            // 6. 存入新表 ai_advice_logs
            AiAdviceLog log = new AiAdviceLog();
            log.setPatientId(patientId);
            log.setHeart_rate_avg(heartRate);
            log.setAdviceContent(longAdvice);
            aiAdviceRepo.save(log);

            // 7. 更新旧表 health_records 的 ai_insight 字段
            // 注意：需要在 HealthRecordRepository 里写一个自定义 update 方法
            healthRepo.updateAiInsightById(recordId, shortAdvice);

            return longAdvice;

        } catch (Exception e) {
            System.err.println("AI 调用失败: " + e.getMessage());
            return "建议家属继续观察，如有不适请及时就医。";
        }
    }
}