package com.tgod.training_analytics.adapters.activities.controller;

import com.tgod.training_analytics.adapters.activities.config.StravaProperties;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.usecase.AuthenticationCallbackUC;
import com.tgod.training_analytics.domain.activities.usecase.GetActivitiesUseCase;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
public class StravaController {

    @Autowired
    AuthenticationCallbackUC authCallbackUC;

    @Autowired
    GetActivitiesUseCase getActivitiesUseCase;

    private final StravaProperties properties;

    public StravaController(
            StravaProperties properties
    ) {
        this.properties = properties;
    }

    @GetMapping("/strava/login")
    public ResponseEntity<Void> login(Principal principal) {

        var url = HttpUrl.parse(
                        "https://www.strava.com/oauth/authorize"
                ).newBuilder()
                .addQueryParameter(
                        "client_id",
                        properties.clientId()
                )
                .addQueryParameter(
                        "response_type",
                        "code"
                )
                .addQueryParameter(
                        "redirect_uri",
                        getRedirectUri(principal)
                )
                .addQueryParameter(
                        "scope",
                        "read,activity:read_all"
                )
                .addQueryParameter(
                        "approval_prompt",
                        "auto"
                )
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.toString()))
                .build();
    }

    @NotNull
    private String getRedirectUri(Principal principal) {
        return "%s/%s".formatted(properties.redirectUri(), principal.getName());
    }

    @GetMapping("/strava/callback/{username}")
    public String callback(
            @RequestParam String code,
            @PathVariable String username
    ) throws IOException {
        authCallbackUC.handleCallback(code, username);
        return "Authenticated";
    }


    @GetMapping("/strava/activities")
    public List<Activity> getActivities(Principal principal) throws IOException {
        return getActivitiesUseCase.getActivities(principal.getName());
    }

}
