package com.bupt.insightguard.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * 愈见项目 - 健康监测实体类
 * 对应数据库表：health_data
 */

@Entity
@TableName("health_data")
public class HealthData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                // 主键ID
    private long userId;            // 患者ID
    private Integer heartRate;      // 心率 (BPM)
    private Integer breathRate;     // 呼吸频率 (次/分)
    private Integer status;         // 状态：0-正常，1-异常风险
    private LocalDateTime recordTime; // 记录产生的时间

    public HealthData() {           //MyBatis 反射需要

    }
    public HealthData(Long userId, Integer heartRate, Integer breathRate, Integer status, LocalDateTime recordTime) {
        this.userId = userId;
        this.heartRate = heartRate;
        this.breathRate = breathRate;
        this.status = status;
        this.recordTime = recordTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getBreathRate() {
        return breathRate;
    }

    public void setBreathRate(Integer breathRate) {
        this.breathRate = breathRate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "HealthData{" +
                "id=" + id +
                ", userId=" + userId +
                ", heartRate=" + heartRate +
                ", breathRate=" + breathRate +
                ", status=" + status +
                ", recordTime=" + recordTime +
                '}';

    }
}
