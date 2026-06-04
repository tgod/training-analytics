package com.tgod.training_analytics.domain.activities.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Activity(
        Long id,
        String name,
        double distance,
        int movingTime,
        int elapsedTime,
        String type,
        String sportType,
        String startDate,
        String startDateLocal,
        String timezone,
        double totalElevationGain,
        double averageSpeed,
        double maxSpeed,
        Double averageHeartrate,
        Double maxHeartrate,
        Double averageCadence,
        Double averageWatts,
        Double weightedAverageWatts,
        Integer averageTemp,
        Double kilojoules,
        boolean trainer,
        boolean commute,
        int achievementCount,
        int kudosCount,
        Double sufferScore,
        double elevHigh,
        double elevLow
) {}
