package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.activities.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class GetActivitiesUC {

    @Autowired
    private SportActivitiesDataPort service;

    @Autowired
    private AccessTokenUC accessTokenUC;

    @Autowired
    private TimeProvider timeProvider;

    public List<Activity> getActivities(String username) throws IOException {
        var token = accessTokenUC.getByUsername(username);
        var after = timeProvider.getTime().minus(30, ChronoUnit.DAYS);
        return service.getActivities(token.getAccessToken(), after);
    }

}
