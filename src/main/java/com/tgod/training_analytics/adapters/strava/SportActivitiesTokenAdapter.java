package com.tgod.training_analytics.adapters.strava;

import com.tgod.training_analytics.adapters.strava.client.StravaApiClient;
import com.tgod.training_analytics.adapters.strava.client.StravaApiException;
import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.domain.activities.exception.TokenExchangeException;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesTokenPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
public class SportActivitiesTokenAdapter implements SportActivitiesTokenPort {

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
            throw new TokenExchangeException("Network error during token exchange", e);
        } catch (StravaApiException e) {
            throw new TokenExchangeException("Strava rejected token exchange: " + e.getMessage(), e);
        }
    }
}
