package com.tgod.training_analytics.domain.ports.activities;

import com.tgod.training_analytics.domain.activities.model.Activity;

import java.util.List;

public interface SportActivitiesDataPort {

    List<Activity> getActivities(String token, int weeks);

}
