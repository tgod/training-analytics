package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.ports.activities.SportActivitiesTokenPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationCallbackUC {

    @Autowired
    private SportActivitiesTokenPort service;

    @Autowired
    private AccessTokenUC accessTokenUC;

    public void handleCallback(String code, String username) {
        var token = service.getToken(username, code);
        accessTokenUC.upsert(token);
    }

}
