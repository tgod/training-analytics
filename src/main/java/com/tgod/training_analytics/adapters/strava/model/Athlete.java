package com.tgod.training_analytics.adapters.strava.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Athlete(
        long id,
        String username,

        @JsonProperty("resource_state")
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

        @JsonProperty("created_at")
        Instant createdAt,

        @JsonProperty("updated_at")
        Instant updatedAt,

        @JsonProperty("badge_type_id")
        int badgeTypeId,

        @JsonProperty("profile_medium")
        String profileMedium,

        String profile,

        Object friend,
        Object follower
) {
}
