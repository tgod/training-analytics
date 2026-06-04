package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.exception.TokenNotFoundException;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.activities.repository.AccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenUC {

    @Autowired
    private AccessTokenRepository repository;

    public AccessToken getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new TokenNotFoundException(username));
    }

    public void upsert(AccessToken token) {
        var existingOpt = repository.findByUsername(token.getUsername());
        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            existing.updateTokens(token.getAccessToken(), token.getRefreshToken(), token.getExpiresAt());
            repository.save(existing);
        } else {
            repository.save(token);
        }
    }
}
