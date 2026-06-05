package com.tgod.training_analytics.adapters.strava.controller;

import com.tgod.training_analytics.adapters.strava.OAuthStateStore;
import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.api.StravaApi;
import com.tgod.training_analytics.api.model.ActivityDto;
import com.tgod.training_analytics.domain.activities.exception.InvalidOAuthStateException;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.usecase.AuthenticationCallbackUC;
import com.tgod.training_analytics.domain.activities.usecase.GetActivitiesUC;
import com.tgod.training_analytics.domain.activities.usecase.SyncActivitiesUC;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class StravaController implements StravaApi {

    @Autowired
    private AuthenticationCallbackUC authCallbackUC;

    @Autowired
    private GetActivitiesUC getActivitiesUC;

    @Autowired
    private SyncActivitiesUC syncActivitiesUC;

    @Autowired
    private StravaProperties properties;

    @Autowired
    private OAuthStateStore stateStore;

    @Override
    public ResponseEntity<Void> stravaLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String state = stateStore.generate(authentication.getName());
        var url = HttpUrl.parse(properties.baseUrl() + "/oauth/authorize")
                .newBuilder()
                .addQueryParameter("client_id", properties.clientId())
                .addQueryParameter("response_type", "code")
                .addQueryParameter("redirect_uri", properties.redirectUri())
                .addQueryParameter("scope", "read,activity:read_all")
                .addQueryParameter("approval_prompt", "auto")
                .addQueryParameter("state", state)
                .build();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.toString()))
                .build();
    }

    @Override
    public ResponseEntity<List<ActivityDto>> syncStravaActivities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Activity> activities = syncActivitiesUC.execute(authentication.getName());
        return ResponseEntity.ok(activities.stream().map(this::toDto).toList());
    }

    @Override
    public ResponseEntity<String> stravaCallback(String state, String code) {
        String username = stateStore.consumeUsername(state)
                .orElseThrow(InvalidOAuthStateException::new);
        authCallbackUC.handleCallback(code, username);
        return ResponseEntity.ok("Authenticated");
    }

    @Override
    public ResponseEntity<List<ActivityDto>> getStravaActivities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var activities = getActivitiesUC.getActivities(authentication.getName());
        return ResponseEntity.ok(activities.stream().map(this::toDto).toList());
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
