package com.tgod.training_analytics.domain.strava.usecase;

import com.tgod.training_analytics.adapters.strava.client.StravaAuthClient;
import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.domain.strava.model.StravaToken;
import com.tgod.training_analytics.domain.strava.repository.StravaTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuthenticationCallbackUC {

    @Autowired
    private StravaAuthClient authClient; //TODO: Interface

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
        var existingTokenOpt = repository.findByUsername(username);
        if(existingTokenOpt.isPresent()){
            var existingToken = existingTokenOpt.get();
            existingToken.updateTokens(token.getAccessToken(), token.getRefreshToken(), token.getExpiresAt());
            repository.save(existingToken);
        }
        else repository.save(token);
    }

}
