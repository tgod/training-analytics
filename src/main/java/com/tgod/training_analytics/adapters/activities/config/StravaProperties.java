package com.tgod.training_analytics.adapters.activities.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "strava")
public record StravaProperties(
        String clientId,
        String clientSecret,
        String redirectUri
) {
}
