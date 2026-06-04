package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.repository.StravaTokenRepository;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationCallbackUC {

    @Autowired
    private SportActivitiesTokenService service;

    @Autowired
    private StravaTokenRepository repository;

    public void handleCallback(String code, String username) {
        var token = service.getToken(username, code);
        var existingTokenOpt = repository.findByUsername(username);
        if (existingTokenOpt.isPresent()) {
            var existingToken = existingTokenOpt.get();
            existingToken.updateTokens(token.getAccessToken(), token.getRefreshToken(), token.getExpiresAt());
            repository.save(existingToken);
        } else repository.save(token);
    }

}
