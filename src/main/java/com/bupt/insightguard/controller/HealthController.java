package com.bupt.insightguard.controller;


import com.bupt.insightguard.entity.HealthData;
import com.bupt.insightguard.repository.HealthDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")   // 允许跨域,便于和前端vue连接
public class HealthController {
    private final HealthDataRepository healthDataRepository;

    public HealthController (HealthDataRepository healthDataRepository) {
        this.healthDataRepository = healthDataRepository;
    }

    @GetMapping("/latest")
    public List<HealthData> getLatestData() {
        return healthDataRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "recordTime"))
            )        .getContent();


    }


}
