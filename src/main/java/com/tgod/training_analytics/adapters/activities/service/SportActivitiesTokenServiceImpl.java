package com.tgod.training_analytics.adapters.activities.service;

import com.tgod.training_analytics.adapters.activities.client.StravaApiClient;
import com.tgod.training_analytics.adapters.activities.config.StravaProperties;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
public class SportActivitiesTokenServiceImpl implements SportActivitiesTokenService {

    @Autowired
    private StravaApiClient client;

    @Autowired
    private StravaProperties properties;

    @Override
    public AccessToken getToken(String username, String code) {
        try {
            var response = client.exchangeCode(
                    code,
                    properties.clientId(),
                    properties.clientSecret()
            );

            return new AccessToken(
                    username,
                    response.athlete().id(),
                    response.access_token(),
                    response.refresh_token(),
                    Instant.ofEpochSecond(response.expires_at())
            );
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: error handling
        }
    }
}
