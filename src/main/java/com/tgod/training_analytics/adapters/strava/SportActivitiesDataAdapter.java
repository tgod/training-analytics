package com.tgod.training_analytics.adapters.strava;

import com.tgod.training_analytics.adapters.strava.client.StravaApiClient;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.activities.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SportActivitiesDataAdapter implements SportActivitiesDataPort {
    @Autowired
    private StravaApiClient client;

    @Override
    public List<Activity> getActivities(String token, int weeks) {
        try {
            return client.getActivities(token);
        } catch (IOException e) {
            // TODO: error handling
            throw new RuntimeException(e);
        }
    }
}
