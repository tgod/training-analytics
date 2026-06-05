package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.event.ActivityCreated;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.model.ActivityEntity;
import com.tgod.training_analytics.domain.activities.repository.ActivityRepository;
import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class SyncActivitiesUC {

    @Autowired
    private SportActivitiesDataPort service;

    @Autowired
    private AccessTokenUC accessTokenUC;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<Activity> execute(String username) {
        var token = accessTokenUC.getByUsername(username);
        var after = activityRepository.findTopByUsernameOrderByStartDateDesc(username)
                .map(a -> Instant.parse(a.getStartDate()))
                .orElseGet(() -> timeProvider.getTime().minus(30, ChronoUnit.DAYS));
        List<Activity> activities = service.getActivities(token.getAccessToken(), after);
        List<Activity> newActivities = new ArrayList<>();
        for (Activity activity : activities) {
            var maybeExisting = activityRepository.findByStravaId(activity.id());
            var entity = maybeExisting.orElse(new ActivityEntity(activity, username));
            entity.updateFrom(activity);
            activityRepository.save(entity);
            if (maybeExisting.isEmpty()) {
                newActivities.add(activity);
                eventPublisher.publishEvent(new ActivityCreated(username, activity.id(), activity.name()));
            }
        }
        return newActivities;
    }

}
