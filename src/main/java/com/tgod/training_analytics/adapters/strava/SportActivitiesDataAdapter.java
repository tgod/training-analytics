package com.tgod.training_analytics.adapters.strava;

import com.tgod.training_analytics.adapters.strava.client.StravaApiClient;
import com.tgod.training_analytics.adapters.strava.client.StravaApiException;
import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.activities.exception.ActivitiesFetchException;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.activities.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SportActivitiesDataAdapter implements SportActivitiesDataPort {
    @Autowired
    private StravaApiClient client;

    @Autowired
    private TimeProvider timeProvider;

    @Override
    public List<Activity> getActivities(String token, int days) {
        try {
            var epoch = timeProvider.getTime().minus(days, ChronoUnit.DAYS).getEpochSecond();
            return client.getActivities(token, epoch, 1, 100);
        } catch (IOException e) {
            throw new ActivitiesFetchException("Network error while fetching activities from Strava", e);
        } catch (StravaApiException e) {
            throw new ActivitiesFetchException("Strava API returned an error: " + e.getMessage(), e);
        }
    }
}
