package com.bupt.insightguard.repository;

import com.bupt.insightguard.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthDataRepository extends JpaRepository<HealthData, Long> {

}