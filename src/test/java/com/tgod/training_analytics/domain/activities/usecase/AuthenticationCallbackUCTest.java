package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesTokenPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationCallbackUCTest {

    @Mock
    private SportActivitiesTokenPort service;

    @Mock
    private AccessTokenUC accessTokenUC;

    @InjectMocks
    private AuthenticationCallbackUC uc;

    private static final String USERNAME = "admin";
    private static final String CODE = "auth-code-123";

    @Test
    void shouldUpsertNewTokenForUser() {
        //given
        AccessToken newToken = new AccessToken(USERNAME, 42L, "new-access", "new-refresh", Instant.parse("2026-12-01T00:00:00Z"));
        when(service.getToken(USERNAME, CODE)).thenReturn(newToken);

        //when
        uc.handleCallback(CODE, USERNAME);

        //then
        verify(accessTokenUC).upsert(newToken);
    }

}
