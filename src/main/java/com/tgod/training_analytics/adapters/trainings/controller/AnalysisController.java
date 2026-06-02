package com.tgod.training_analytics.adapters.trainings.controller;

import com.example.api.InsightsApi;
import com.example.model.InsightDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController implements InsightsApi {


    @Override
    public ResponseEntity<InsightDto> getInsight(Long id) {
        return null;
    }
}
