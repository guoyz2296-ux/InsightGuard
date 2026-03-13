package com.bupt.insightguard.controller;

import com.bupt.insightguard.entity.AiAdviceLog;
import com.bupt.insightguard.repository.AiAdviceRepository;
import com.bupt.insightguard.service.AiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiAdviceController {

    private final AiService aiService;
    private final AiAdviceRepository aiAdviceRepo;

    public AiAdviceController(AiService aiService, AiAdviceRepository aiAdviceRepo) {
        this.aiService = aiService;
        this.aiAdviceRepo = aiAdviceRepo;
    }

    /**
     * 手动触发 AI 生成建议（联调用）
     * 访问地址示例：localhost:8080/api/ai/generate?patientId=1&recordId=10&hr=120
     */
    @GetMapping("/generate")
    public String generateAdvice(@RequestParam Long patientId,
                                 @RequestParam Long recordId,
                                 @RequestParam int heartRate,
                                 @RequestParam int respRate,
                                 @RequestParam int spo2,
                                 @RequestParam float motionScale,
                                 @RequestParam float noise
                                 ) {
        // 调用 Service，它会自动存入新表并更新旧表
        return aiService.getAndSaveHealthAdvice(patientId, recordId, heartRate, respRate, spo2, motionScale, noise);
    }

    /**
     * 获取某个患者的所有 AI 历史建议（给李思辰前端展示用）
     * 访问地址示例：localhost:8080/api/ai/history/1
     */
    @GetMapping("/history/{patientId}")
    public List<AiAdviceLog> getHistory(@PathVariable Long patientId) {
        return aiAdviceRepo.findByPatientIdOrderByCreateTimeDesc(patientId);
    }
}