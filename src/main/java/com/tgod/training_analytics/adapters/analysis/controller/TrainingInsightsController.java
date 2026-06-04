package com.tgod.training_analytics.adapters.analysis.controller;

import com.example.api.InsightsApi;
import com.example.model.InsightDto;
import com.tgod.training_analytics.domain.analysis.ActivityAnalisysService;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TrainingInsightsController implements InsightsApi {

    @Autowired
    private ActivityAnalisysService service;

    @Override
    public ResponseEntity<InsightDto> getInsight(Long id) {
        return null;
    }

    @GetMapping("/trainings/insights")
    public ResponseEntity<TrainingAnalysis> getInsights(Principal principal) {
        return ResponseEntity.ok(service.analyze(principal.getName(), 4));
    }

}
