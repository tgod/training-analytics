package com.tgod.training_analytics.adapters.trainings.controller;

import com.example.api.InsightsApi;
import com.example.model.InsightDto;
import com.tgod.training_analytics.domain.strava.usecase.GetActivitiesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController implements InsightsApi {
    private GetActivitiesUseCase getActivitiesUC;

    @Override
    public ResponseEntity<InsightDto> getInsight(Long id) {
//        try {
//            var activities =  getActivitiesUC.getActivities();
//            return null;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }


}
