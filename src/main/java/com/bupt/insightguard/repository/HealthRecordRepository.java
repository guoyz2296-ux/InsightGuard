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

    @Modifying
    @Transactional
    @Query("UPDATE HealthRecord h SET h.aiInsight = :insight WHERE h.id = :id")
    void updateAiInsightById(@Param("id") Long id, @Param("insight") String insight);

}
