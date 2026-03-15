package com.bupt.insightguard.controller;


import com.bupt.insightguard.entity.HealthRecord;
import com.bupt.insightguard.repository.HealthRecordRepository;
import org.springframework.web.bind.annotation.*;

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


}
