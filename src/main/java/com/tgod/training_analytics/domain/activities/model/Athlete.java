package com.tgod.training_analytics.domain.activities.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Athlete(
        long id,
        String username,
        int resourceState,
        String firstname,
        String lastname,
        String bio,
        String city,
        String state,
        String country,
        String sex,
        boolean premium,
        boolean summit,
        Instant createdAt,
        Instant updatedAt,
        int badgeTypeId,
        String profileMedium,
        String profile,
        Object friend,
        Object follower
) {
}
