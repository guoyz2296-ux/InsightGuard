package com.bupt.insightguard.repository;

import com.bupt.insightguard.entity.HealthRecord;
import com.bupt.insightguard.entity.PatientInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findTop10ByPatientIdOrderByRecordTimeDesc(Long patientId);


}
