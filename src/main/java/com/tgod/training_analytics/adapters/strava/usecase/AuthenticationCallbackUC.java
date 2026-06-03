package com.tgod.training_analytics.adapters.strava.usecase;

import com.tgod.training_analytics.adapters.strava.client.StravaAuthClient;
import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.adapters.strava.model.StravaToken;
import com.tgod.training_analytics.adapters.strava.repository.StravaTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuthenticationCallbackUC {

    @Autowired
    private StravaAuthClient authClient;

    @Autowired
    private StravaTokenRepository repository;


    public void handleCallback(String code, String username, StravaProperties properties) throws IOException {
        var response = authClient.exchangeCode(
                code,
                properties.clientId(),
                properties.clientSecret()
        );

        var token = new StravaToken(
                username,
                response.athlete().id(),
                response.access_token(),
                response.refresh_token(),
                Instant.ofEpochSecond(response.expires_at())
        );

        repository.save(token);
    }

}
