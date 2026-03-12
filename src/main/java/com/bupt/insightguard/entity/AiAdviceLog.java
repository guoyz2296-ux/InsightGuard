package com.bupt.insightguard.entity;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;

import java.time.LocalDateTime;

@Entity
@Table (name = "ai_advice_logs")

public class AiAdviceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long patientId;
    private Integer heart_rate_avg;

    @Column(columnDefinition = "TEXT")
    private String adviceContent;

    private LocalDateTime createTime;

    // 构造方法
    public AiAdviceLog() {
        this.createTime = LocalDateTime.now();
    }


    //Getter和Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getAdviceContent() {
        return adviceContent;
    }

    public void setAdviceContent(String adviceContent) {
        this.adviceContent = adviceContent;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getHeart_rate_avg() {
        return heart_rate_avg;
    }

    public void setHeart_rate_avg(Integer heart_rate_avg) {
        this.heart_rate_avg = heart_rate_avg;
    }
}


