package com.tgod.training_analytics.adapters.strava.controller;

import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.api.StravaApi;
import com.tgod.training_analytics.api.model.ActivityDto;
import com.tgod.training_analytics.domain.activities.exception.ActivitiesFetchException;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.usecase.AuthenticationCallbackUC;
import com.tgod.training_analytics.domain.activities.usecase.GetActivitiesUC;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
public class StravaController implements StravaApi {

    @Autowired
    private AuthenticationCallbackUC authCallbackUC;

    @Autowired
    private GetActivitiesUC getActivitiesUC;

    @Autowired
    private StravaProperties properties;

    @Override
    public ResponseEntity<Void> stravaLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var url = HttpUrl.parse(properties.baseUrl() + "/oauth/authorize")
                .newBuilder()
                .addQueryParameter("client_id", properties.clientId())
                .addQueryParameter("response_type", "code")
                .addQueryParameter("redirect_uri", getRedirectUri(authentication.getName()))
                .addQueryParameter("scope", "read,activity:read_all")
                .addQueryParameter("approval_prompt", "auto")
                .build();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.toString()))
                .build();
    }

    @Override
    public ResponseEntity<Void> stravaCallback(String username, String code) {
        authCallbackUC.handleCallback(code, username);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ActivityDto>> getStravaActivities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            var activities = getActivitiesUC.getActivities(authentication.getName());
            return ResponseEntity.ok(activities.stream().map(this::toDto).toList());
        } catch (IOException e) {
            throw new ActivitiesFetchException("Failed to fetch activities", e);
        }
    }

    private String getRedirectUri(String username) {
        return "%s/%s".formatted(properties.redirectUri(), username);
    }

    private ActivityDto toDto(Activity a) {
        return new ActivityDto()
                .id(a.id())
                .name(a.name())
                .distance(a.distance())
                .movingTime(a.movingTime())
                .elapsedTime(a.elapsedTime())
                .type(a.type())
                .sportType(a.sportType())
                .startDate(a.startDate())
                .startDateLocal(a.startDateLocal())
                .timezone(a.timezone())
                .totalElevationGain(a.totalElevationGain())
                .averageSpeed(a.averageSpeed())
                .maxSpeed(a.maxSpeed())
                .averageHeartrate(a.averageHeartrate())
                .maxHeartrate(a.maxHeartrate())
                .averageCadence(a.averageCadence())
                .averageWatts(a.averageWatts())
                .weightedAverageWatts(a.weightedAverageWatts())
                .averageTemp(a.averageTemp())
                .kilojoules(a.kilojoules())
                .trainer(a.trainer())
                .commute(a.commute())
                .achievementCount(a.achievementCount())
                .kudosCount(a.kudosCount())
                .sufferScore(a.sufferScore())
                .elevHigh(a.elevHigh())
                .elevLow(a.elevLow());
    }
}
