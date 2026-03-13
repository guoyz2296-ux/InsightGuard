package com.bupt.insightguard.service;

import com.bupt.insightguard.entity.AiAdviceLog;
import com.bupt.insightguard.repository.AiAdviceRepository;
import com.bupt.insightguard.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    /**
     * 获取全指标 AI 建议并更新数据库
     * 涵盖心率、呼吸、血氧、体动、噪音 5 项输入，以及状态、等级、评价 3 项输出
     */
    public String getAndSaveHealthAdvice(Long patientId, Long recordId,
                                         int heartRate, int respRate, int spo2,
                                         float motionScale, float noise) {

        // 1. 构造 Prompt：明确要求输出数据库对应的三个字段
        String prompt = String.format(
                "请根据PTSD监护数据给出精准判定。数据：心率%d, 呼吸%d, 血氧%d%%, 体动%.2f, 噪音%.1f dB\n" +
                        "必须且仅按此格式返回（不要有开头语）：\n" +
                        "STATUS: [NORMAL/STRESS/ATTACK]\n" +
                        "LEVEL: [1/2/3/4]\n" +
                        "INSIGHT: [包含原因分析和具体护理建议的完整段落]",
                heartRate, respRate, spo2, motionScale, noise);

        // 2. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 3. 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelId);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "你是一个专业的PTSD健康监护助手，负责对生理和环境指标进行深度判定。"),
                Map.of("role", "user", "content", prompt)
        ));

        try {

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            // 4. 安全解析层：解决 Object 报红问题
            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("choices")) {
                return "AI 响应为空";
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            String fullContent = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            // 5. 正则提取判定值
            String statusCode = extractValue(fullContent, "STATUS");
            String levelRaw = extractValue(fullContent, "LEVEL");
            String levelClean = levelRaw.replaceAll("[^0-9]", "");
            int stressLevel = levelClean.isEmpty() ? 1 : Integer.parseInt(levelClean);

            String aiInsight = extractValue(fullContent, "INSIGHT");

            // 6. 存入新表日志作为备份记录
            AiAdviceLog log = new AiAdviceLog();
            log.setPatientId(patientId);
            log.setHeart_rate_avg(heartRate);
            log.setAdviceContent(aiInsight);
            aiAdviceRepo.save(log);

            // 7. 更新旧表 health_records 的全量判定字段
            healthRepo.updateAiFullAnalysis(recordId, statusCode, stressLevel, aiInsight);

            return aiInsight;

        } catch (Exception e) {
            System.err.println("AI 全链路调用失败: " + e.getMessage());
            return "生理指标存在波动，建议手动核实。";
        }
    }

    /**
     * 正则提取方法:直到看到换行或者下一个关键词为止，中间的内容全抓出来
     */
    private String extractValue(String text, String key) {
        // 使用非贪婪匹配抓取 key 之后的内容，直到遇到下一个关键词或结尾
        Pattern pattern = Pattern.compile(key + ":\\s*([\\s\\S]*?)(\\n[A-Z]+:|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        // 默认保底值，防止 Level 转换失败
        return (key.equals("LEVEL")) ? "1" : "UNKNOWN";
    }
}