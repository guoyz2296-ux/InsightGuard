package com.bupt.insightguard.controller;


import com.bupt.insightguard.entity.HealthRecord;
import com.bupt.insightguard.repository.HealthRecordRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/health")

public class HealthController {

    private final HealthRecordRepository healthRecordRepository;

    public HealthController(HealthRecordRepository healthRecordRepository) {
        this.healthRecordRepository = healthRecordRepository;
    }


    @GetMapping("/latest")
    public List<HealthRecord> getLatestData(@RequestParam Long patientId) {
        return healthRecordRepository.findTop10ByPatientIdOrderByRecordTimeDesc(patientId);

    }
    @PostMapping
    public HealthRecord createRecord(@RequestBody HealthRecordDTO dto) {
        HealthRecord record = dto.toEntity();
        return healthRecordRepository.save(record);
    }

    // ---------- 内部 DTO，用于接收下划线 JSON 并转换为 HealthRecord 实体 ----------
    public static class HealthRecordDTO {
        @JsonProperty("user_id")
        private Long patientId;

        @JsonProperty("heart_rate")
        private Integer heartRate;

        @JsonProperty("resp_rate")
        private Integer respRate;

        @JsonProperty("spo2")
        private Integer spo2;

        @JsonProperty("motion_scale")
        private Float motionScale;

        @JsonProperty("noise")
        private Float noise;

        @JsonProperty("status_code")
        private String statusCode;

        @JsonProperty("stress_level")
        private Integer stressLevel;

        @JsonProperty("record_time")
        private String recordTime; // 可选，如果前端传了时间，则使用；否则由数据库自动生成

        // 转换为 HealthRecord 实体
        public HealthRecord toEntity() {
            HealthRecord record = new HealthRecord();
            record.setPatientId(this.patientId);
            record.setHeartRate(this.heartRate);
            record.setRespRate(this.respRate);
            record.setSpo2(this.spo2);
            record.setMotionScale(this.motionScale);
            record.setNoise(this.noise);
            record.setStatusCode(this.statusCode);
            record.setStressLevel(this.stressLevel);
            if (this.recordTime != null && !this.recordTime.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                record.setRecordTime(LocalDateTime.parse(this.recordTime, formatter));
            }
            // aiInsight 暂不设置
            return record;
        }

        // Getters and Setters (Jackson 通过字段反射，但提供 getter/setter 是好习惯)
        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public Integer getHeartRate() { return heartRate; }
        public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }
        public Integer getRespRate() { return respRate; }
        public void setRespRate(Integer respRate) { this.respRate = respRate; }
        public Integer getSpo2() { return spo2; }
        public void setSpo2(Integer spo2) { this.spo2 = spo2; }
        public Float getMotionScale() { return motionScale; }
        public void setMotionScale(Float motionScale) { this.motionScale = motionScale; }
        public Float getNoise() { return noise; }
        public void setNoise(Float noise) { this.noise = noise; }
        public String getStatusCode() { return statusCode; }
        public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
        public Integer getStressLevel() { return stressLevel; }
        public void setStressLevel(Integer stressLevel) { this.stressLevel = stressLevel; }
        public String getRecordTime() { return recordTime; }
        public void setRecordTime(String recordTime) { this.recordTime = recordTime; }
    }


}
