package com.bupt.insightguard.repository;

import com.bupt.insightguard.entity.HealthRecord;
import com.bupt.insightguard.entity.PatientInfo;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findTop10ByPatientIdOrderByRecordTimeDesc(Long patientId);

    @Modifying // 告诉 JPA 这是一个修改操作
    @Transactional // 开启事务，确保执行完后提交回数据库
    @Query("UPDATE HealthRecord h SET h.statusCode = :sc, h.stressLevel = :sl, h.aiInsight = :ai WHERE h.id = :id")
    void updateAiFullAnalysis(@Param("id") Long id,
                              @Param("sc") String sc,
                              @Param("sl") int sl,
                              @Param("ai") String ai);
}
