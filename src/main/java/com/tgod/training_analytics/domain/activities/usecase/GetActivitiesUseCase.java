package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.ports.activities.SportActivitiesService;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.repository.StravaTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GetActivitiesUseCase {
    @Autowired
    private SportActivitiesService service; //TODO: interface
    @Autowired
    private StravaTokenRepository repository;

    public List<Activity> getActivities(String username) throws IOException {
        //TODO: custom exception
        var token = repository.findByUsername(username).orElseThrow(RuntimeException::new);

        //TODO: refreshing token
       return service.getActivities(token.getAccessToken());
    }

}
