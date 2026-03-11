package com.bupt.insightguard.repository;

import com.bupt.insightguard.entity.AiAdviceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiAdivceRepository extends JpaRepository<AiAdviceLog, Long> {
    List<AiAdviceLog> findByPatientIdOrderByCreateTimeDesc(Long patientId);
}
