package com.tgod.training_analytics.domain.ports.activities;

import com.tgod.training_analytics.domain.activities.model.Activity;

import java.util.List;

public interface SportActivitiesService {

    List<Activity> getActivities(String token);

}
