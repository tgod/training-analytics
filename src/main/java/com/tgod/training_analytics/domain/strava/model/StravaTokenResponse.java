package com.tgod.training_analytics.domain.strava.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
