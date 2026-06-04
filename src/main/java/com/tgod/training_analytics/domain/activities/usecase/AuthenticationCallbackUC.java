package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.adapters.activities.client.StravaApiClient;
import com.tgod.training_analytics.adapters.activities.config.StravaProperties;
import com.tgod.training_analytics.domain.activities.model.StravaToken;
import com.tgod.training_analytics.domain.activities.repository.StravaTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuthenticationCallbackUC {

    @Autowired
    private StravaApiClient apiClient; //TODO: Interface

    @Autowired
    private StravaTokenRepository repository;


    public void handleCallback(String code, String username, StravaProperties properties) throws IOException {
        var response = apiClient.exchangeCode(
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
