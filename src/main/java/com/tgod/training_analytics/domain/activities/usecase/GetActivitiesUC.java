package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.model.ActivityEntity;
import com.tgod.training_analytics.domain.activities.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetActivitiesUC {

    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getActivities(String username) {
        return activityRepository.findAllByUsername(username).stream()
                .map(ActivityEntity::toActivity)
                .toList();
    }

}
