package com.tgod.training_analytics.adapters.strava;

import com.tgod.training_analytics.adapters.strava.client.StravaApiClient;
import com.tgod.training_analytics.adapters.strava.client.StravaApiException;
import com.tgod.training_analytics.domain.activities.exception.ActivitiesFetchException;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.activities.model.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class SportActivitiesDataAdapter implements SportActivitiesDataPort {
    private static final Logger log = LoggerFactory.getLogger(SportActivitiesDataAdapter.class);

    @Autowired
    private StravaApiClient client;

    @Override
    public List<Activity> getActivities(String token, Instant after) {
        try {
            int maxCalls = 20;
            int page = 1;
            int perPage = 100;
            boolean lastPageReached = false;
            List<Activity> result = new ArrayList<>();
            while (maxCalls > 0 && !lastPageReached) {
                List<Activity> activities = client.getActivities(token, after.getEpochSecond(), page, perPage);
                result.addAll(activities);
                page++;
                maxCalls--;
                lastPageReached = activities.size() < perPage;
            }
            if (!lastPageReached) {
                log.warn("Can't fetch all strava activities: reached page limit of {} pages, more data may exist after {}",
                        page - 1, after);
            }
            return result;


        } catch (IOException e) {
            throw new ActivitiesFetchException("Network error while fetching activities from Strava", e);
        } catch (StravaApiException e) {
            throw new ActivitiesFetchException("Strava API returned an error: " + e.getMessage(), e);
        }
    }
}
