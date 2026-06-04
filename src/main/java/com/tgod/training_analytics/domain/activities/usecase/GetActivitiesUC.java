package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.activities.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GetActivitiesUC {

    @Autowired
    private SportActivitiesDataPort service;

    @Autowired
    private AccessTokenUC accessTokenUC;

    public List<Activity> getActivities(String username) throws IOException {
        var token = accessTokenUC.getByUsername(username);
        return service.getActivities(token.getAccessToken(), 4);
    }

}
