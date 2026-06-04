package com.tgod.training_analytics.adapters.strava;

import com.tgod.training_analytics.adapters.strava.client.StravaApiClient;
import com.tgod.training_analytics.adapters.strava.client.StravaApiException;
import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.adapters.strava.model.StravaTokenResponse;
import com.tgod.training_analytics.domain.activities.exception.TokenExchangeException;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.activities.model.Athlete;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SportActivitiesTokenAdapterTest {

    @Mock
    private StravaApiClient client;

    @Mock
    private StravaProperties properties;

    @InjectMocks
    private SportActivitiesTokenAdapter adapter;

    private static final String USERNAME = "admin";
    private static final String CODE = "auth-code-123";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_SECRET = "client-secret";

    @Test
    void shouldMapTokenResponseToAccessToken() throws IOException {
        //given
        long athleteId = 42L;
        long expiresAt = 1893456000L;
        var athlete = new Athlete(athleteId, USERNAME, 1, "John", "Doe",
                null, null, null, null, null,
                false, false, null, null, 0, null, null, null, null);
        var response = new StravaTokenResponse("Bearer", "new-access", "new-refresh", expiresAt, 21600, athlete);

        when(properties.clientId()).thenReturn(CLIENT_ID);
        when(properties.clientSecret()).thenReturn(CLIENT_SECRET);
        when(client.exchangeCode(CODE, CLIENT_ID, CLIENT_SECRET)).thenReturn(response);

        //when
        AccessToken token = adapter.getToken(USERNAME, CODE);

        //then
        assertThat(token.getUsername()).isEqualTo(USERNAME);
        assertThat(token.getAthleteId()).isEqualTo(athleteId);
        assertThat(token.getAccessToken()).isEqualTo("new-access");
        assertThat(token.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(token.getExpiresAt()).isEqualTo(Instant.ofEpochSecond(expiresAt));
    }

    @Test
    void shouldWrapIOExceptionInTokenExchangeException() throws IOException {
        //given
        when(properties.clientId()).thenReturn(CLIENT_ID);
        when(properties.clientSecret()).thenReturn(CLIENT_SECRET);
        when(client.exchangeCode(CODE, CLIENT_ID, CLIENT_SECRET)).thenThrow(new IOException("connection refused"));

        //when / then
        assertThatThrownBy(() -> adapter.getToken(USERNAME, CODE))
                .isInstanceOf(TokenExchangeException.class)
                .hasMessageContaining("Network error")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void shouldWrapStravaApiExceptionInTokenExchangeException() throws IOException {
        //given
        when(properties.clientId()).thenReturn(CLIENT_ID);
        when(properties.clientSecret()).thenReturn(CLIENT_SECRET);
        when(client.exchangeCode(CODE, CLIENT_ID, CLIENT_SECRET))
                .thenThrow(new StravaApiException(401, "Unauthorized — access token is invalid or expired"));

        //when / then
        assertThatThrownBy(() -> adapter.getToken(USERNAME, CODE))
                .isInstanceOf(TokenExchangeException.class)
                .hasMessageContaining("Strava rejected token exchange")
                .hasCauseInstanceOf(StravaApiException.class);
    }

    @Test
    void shouldMapRefreshResponseToNewAccessToken() throws IOException {
        //given
        long athleteId = 42L;
        long expiresAt = 1893456000L;
        var staleToken = new AccessToken(USERNAME, athleteId, "old-access", "old-refresh", Instant.now().plusSeconds(1800));
        var response = new StravaTokenResponse("Bearer", "new-access", "new-refresh", expiresAt, 21600, null);

        when(properties.clientId()).thenReturn(CLIENT_ID);
        when(properties.clientSecret()).thenReturn(CLIENT_SECRET);
        when(client.refreshToken("old-refresh", CLIENT_ID, CLIENT_SECRET)).thenReturn(response);

        //when
        AccessToken result = adapter.refreshToken(staleToken);

        //then
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getAthleteId()).isEqualTo(athleteId);
        assertThat(result.getAccessToken()).isEqualTo("new-access");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(result.getExpiresAt()).isEqualTo(Instant.ofEpochSecond(expiresAt));
    }

    @Test
    void shouldWrapIOExceptionDuringRefreshInTokenExchangeException() throws IOException {
        //given
        var staleToken = new AccessToken(USERNAME, 42L, "old-access", "old-refresh", Instant.now().plusSeconds(1800));
        when(properties.clientId()).thenReturn(CLIENT_ID);
        when(properties.clientSecret()).thenReturn(CLIENT_SECRET);
        when(client.refreshToken("old-refresh", CLIENT_ID, CLIENT_SECRET))
                .thenThrow(new IOException("connection refused"));

        //when / then
        assertThatThrownBy(() -> adapter.refreshToken(staleToken))
                .isInstanceOf(TokenExchangeException.class)
                .hasMessageContaining("Network error during token refresh")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void shouldWrapStravaApiExceptionDuringRefreshInTokenExchangeException() throws IOException {
        //given
        var staleToken = new AccessToken(USERNAME, 42L, "old-access", "old-refresh", Instant.now().plusSeconds(1800));
        when(properties.clientId()).thenReturn(CLIENT_ID);
        when(properties.clientSecret()).thenReturn(CLIENT_SECRET);
        when(client.refreshToken("old-refresh", CLIENT_ID, CLIENT_SECRET))
                .thenThrow(new StravaApiException(401, "Unauthorized"));

        //when / then
        assertThatThrownBy(() -> adapter.refreshToken(staleToken))
                .isInstanceOf(TokenExchangeException.class)
                .hasMessageContaining("Strava rejected token refresh")
                .hasCauseInstanceOf(StravaApiException.class);
    }
}
