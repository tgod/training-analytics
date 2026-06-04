package com.tgod.training_analytics.adapters.strava.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "strava")
public record StravaProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String baseUrl
) {
}
