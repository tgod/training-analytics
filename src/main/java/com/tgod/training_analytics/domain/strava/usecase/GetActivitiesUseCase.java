package com.tgod.training_analytics.domain.strava.usecase;

import com.tgod.training_analytics.adapters.strava.client.StravaApiClient;
import com.tgod.training_analytics.domain.strava.model.Activity;
import com.tgod.training_analytics.domain.strava.repository.StravaTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GetActivitiesUseCase {
    @Autowired
    private StravaApiClient apiClient; //TODO: interface
    @Autowired
    private StravaTokenRepository repository;

    public Activity[] getActivities(String username) throws IOException {
        //TODO: custom exception
        var token = repository.findByUsername(username).orElseThrow(RuntimeException::new);

        //TODO: refreshing token
        var activities = apiClient.get(
                "https://www.strava.com/api/v3/athlete/activities",
                token.getAccessToken(),
                Activity[].class
        );

        return activities;
    }

}
