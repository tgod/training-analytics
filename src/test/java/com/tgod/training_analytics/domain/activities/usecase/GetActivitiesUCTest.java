package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.exception.TokenNotFoundException;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetActivitiesUCTest {

    @Mock
    private SportActivitiesDataPort service;

    @Mock
    private AccessTokenUC accessTokenUC;

    @InjectMocks
    private GetActivitiesUC uc;

    private static final String USERNAME = "admin";
    private static final String ACCESS_TOKEN = "access-token";

    @Test
    void returnsActivitiesForUser() throws IOException {
        //given
        var token = new AccessToken(USERNAME, 42L, ACCESS_TOKEN, "refresh", Instant.parse("2026-12-01T00:00:00Z"));
        var activities = List.of(
                new Activity(1L, "Morning Run", 10000, 3600, 3600, "Run", "Run",
                        "2026-01-01T07:00:00Z", "2026-01-01T08:00:00Z", "UTC",
                        100, 2.8, 4.0, 145.0, 170.0, null, null, null, null, null,
                        false, false, 0, 3, null, 50, 10)
        );
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token);
        when(service.getActivities(ACCESS_TOKEN, 30)).thenReturn(activities);

        //when
        List<Activity> result = uc.getActivities(USERNAME);

        //then
        assertThat(result).isEqualTo(activities);
    }

    @Test
    void propagatesTokenNotFoundExceptionWhenTokenMissing() {
        //given
        when(accessTokenUC.getByUsername(USERNAME)).thenThrow(new TokenNotFoundException(USERNAME));

        //when / then
        assertThatThrownBy(() -> uc.getActivities(USERNAME))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining(USERNAME);
    }
}
