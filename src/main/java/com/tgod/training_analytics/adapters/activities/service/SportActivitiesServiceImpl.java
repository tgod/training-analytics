package com.tgod.training_analytics.adapters.activities.service;

import com.tgod.training_analytics.adapters.activities.client.StravaApiClient;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesService;
import com.tgod.training_analytics.domain.activities.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SportActivitiesServiceImpl implements SportActivitiesService {
    @Autowired
    private StravaApiClient client;

    @Override
    public List<Activity> getActivities(String token) {
        try {
            return client.getActivities(token);
        } catch (IOException e) {
            // TODO: error handling
            throw new RuntimeException(e);
        }
    }
}
