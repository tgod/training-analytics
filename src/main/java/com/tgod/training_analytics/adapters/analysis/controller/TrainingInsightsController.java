package com.tgod.training_analytics.adapters.analysis.controller;

import com.tgod.training_analytics.api.InsightsApi;
import com.tgod.training_analytics.api.model.TrainingAnalysisDto;
import com.tgod.training_analytics.domain.analysis.ActivityAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainingInsightsController implements InsightsApi {

    @Autowired
    private ActivityAnalysisService service;

    @Override
    public ResponseEntity<TrainingAnalysisDto> getInsights() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var analysis = service.analyze(authentication.getName(), 4);
        var dto = new TrainingAnalysisDto()
                .loadLevel(analysis.loadLevel())
                .observations(analysis.observations())
                .nextWeekRecommendation(analysis.nextWeekRecommendation())
                .watchOut(analysis.watchOut());
        return ResponseEntity.ok(dto);
    }

}
