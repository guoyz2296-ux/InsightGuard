package com.bupt.insightguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId; // 关联患者ID

    @Column(name = "heart_rate", nullable = false)
    private Integer heartRate; // 心率 BPM

    @Column(name = "resp_rate", nullable = false)
    private Integer respRate; // 呼吸频率 次/分

    @Column(nullable = false)
    private Integer spo2; // 血氧饱和度 %

    @Column(name = "motion_scale")
    private Float motionScale; // 体动强度 0.0-1.0

    @Column(name = "noise")
    private Float noise; // 环境噪音强度

    @Column(name = "status_code")
    private String statusCode; // NORMAL, STRESS, ATTACK

    @Column(name = "stress_level")
    private Integer stressLevel; // 1,2,3,4

    public Float getNoise() {
        return noise;
    }

    public void setNoise(Float noise) {
        this.noise = noise;
    }

    @Column(name = "ai_insight")
    private String aiInsight; // NORMAL, STRESS, ATTACK

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getRespRate() {
        return respRate;
    }

    public void setRespRate(Integer respRate) {
        this.respRate = respRate;
    }

    public Integer getSpo2() {
        return spo2;
    }

    public void setSpo2(Integer spo2) {
        this.spo2 = spo2;
    }

    public Float getMotionScale() {
        return motionScale;
    }

    public void setMotionScale(Float motionScale) {
        this.motionScale = motionScale;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }

    public String getAiInsight() {
        return aiInsight;
    }

    public void setAiInsight(String aiInsight) {
        this.aiInsight = aiInsight;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }



    @Column(name = "record_time", updatable = false)
    private LocalDateTime recordTime; // 监测时间

    // --- 无参构造函数 (JPA 必须) ---
    public HealthRecord() {
    }

    // --- 自动设置记录时间 ---
    @PrePersist
    protected void onCreate() {
        this.recordTime = LocalDateTime.now();
    }
}