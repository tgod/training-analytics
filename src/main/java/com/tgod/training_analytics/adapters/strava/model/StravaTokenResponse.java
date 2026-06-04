package com.tgod.training_analytics.adapters.strava.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tgod.training_analytics.domain.activities.model.Athlete;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StravaTokenResponse(
        String token_type,
        String access_token,
        String refresh_token,
        long expires_at,
        long expires_in,
        Athlete athlete
) {
}
