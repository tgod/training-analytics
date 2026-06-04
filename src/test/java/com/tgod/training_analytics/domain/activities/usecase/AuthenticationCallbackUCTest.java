package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.activities.repository.AccessTokenRepository;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesTokenPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationCallbackUCTest {

    @Mock
    private SportActivitiesTokenPort service;

    @Mock
    private AccessTokenRepository repository;

    @InjectMocks
    private AuthenticationCallbackUC uc;

    private static final String USERNAME = "admin";
    private static final String CODE = "auth-code-123";

    @Test
    void savesNewTokenWhenNoneExistsForUser() {
        //given
        AccessToken newToken = new AccessToken(USERNAME, 42L, "new-access", "new-refresh", Instant.parse("2026-12-01T00:00:00Z"));
        when(service.getToken(USERNAME, CODE)).thenReturn(newToken);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        //when
        uc.handleCallback(CODE, USERNAME);

        //then
        verify(repository).save(newToken);
    }

    @Test
    void updatesAndSavesExistingTokenWhenOneExistsForUser() {
        //given
        var existing = new AccessToken(USERNAME, 42L, "old-access", "old-refresh", Instant.parse("2025-01-01T00:00:00Z"));
        AccessToken newToken = new AccessToken(USERNAME, 42L, "new-access", "new-refresh", Instant.parse("2026-12-01T00:00:00Z"));
        when(service.getToken(USERNAME, CODE)).thenReturn(newToken);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(existing));

        //when
        uc.handleCallback(CODE, USERNAME);

        //then
        ArgumentCaptor<AccessToken> captor = ArgumentCaptor.forClass(AccessToken.class);
        verify(repository).save(captor.capture());
        AccessToken saved = captor.getValue();
        assertThat(saved.getAccessToken()).isEqualTo(newToken.getAccessToken());
        assertThat(saved.getRefreshToken()).isEqualTo(newToken.getRefreshToken());
        assertThat(saved.getExpiresAt()).isEqualTo(newToken.getExpiresAt());

        verifyNoMoreInteractions(repository);
    }
}
