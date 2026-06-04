package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.exception.TokenNotFoundException;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.activities.repository.AccessTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenUCTest {

    @Mock
    private AccessTokenRepository repository;

    @InjectMocks
    private AccessTokenUC uc;

    private static final String USERNAME = "admin";

    @Test
    void getByUsernameReturnsTokenWhenFound() {
        //given
        AccessToken token = new AccessToken(USERNAME, 42L, "access", "refresh", Instant.parse("2026-12-01T00:00:00Z"));
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(token));

        //when
        AccessToken result = uc.getByUsername(USERNAME);

        //then
        assertThat(result).isEqualTo(token);
    }

    @Test
    void getByUsernameThrowsTokenNotFoundExceptionWhenMissing() {
        //given
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        //when / then
        assertThatThrownBy(() -> uc.getByUsername(USERNAME))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining(USERNAME);
    }

    @Test
    void upsertSavesNewTokenWhenNoneExistsForUser() {
        //given
        AccessToken token = new AccessToken(USERNAME, 42L, "access", "refresh", Instant.parse("2026-12-01T00:00:00Z"));
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        //when
        uc.upsert(token);

        //then
        verify(repository).save(token);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void upsertUpdatesAndSavesExistingTokenWhenOneExistsForUser() {
        //given
        var existing = new AccessToken(USERNAME, 42L, "old-access", "old-refresh", Instant.parse("2025-01-01T00:00:00Z"));
        var incoming = new AccessToken(USERNAME, 42L, "new-access", "new-refresh", Instant.parse("2026-12-01T00:00:00Z"));
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(existing));

        //when
        uc.upsert(incoming);

        //then
        ArgumentCaptor<AccessToken> captor = ArgumentCaptor.forClass(AccessToken.class);
        verify(repository).save(captor.capture());
        AccessToken saved = captor.getValue();
        assertThat(saved.getAccessToken()).isEqualTo(incoming.getAccessToken());
        assertThat(saved.getRefreshToken()).isEqualTo(incoming.getRefreshToken());
        assertThat(saved.getExpiresAt()).isEqualTo(incoming.getExpiresAt());
        verifyNoMoreInteractions(repository);
    }
}
