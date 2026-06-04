package com.tgod.training_analytics.domain.ports.activities;

import com.tgod.training_analytics.domain.activities.model.AccessToken;

public interface SportActivitiesTokenPort {

    AccessToken getToken(String username, String code);

}
