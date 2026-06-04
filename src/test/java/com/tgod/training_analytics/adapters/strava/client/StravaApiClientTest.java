package com.tgod.training_analytics.adapters.strava.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgod.training_analytics.adapters.strava.model.StravaTokenResponse;
import com.tgod.training_analytics.domain.activities.model.Activity;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StravaApiClientTest {

    private MockWebServer server;
    private StravaApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        String baseUrl = "http://localhost:" + server.getPort();
        client = new StravaApiClient(new OkHttpClient(), new ObjectMapper(), baseUrl);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.close();
    }

    @Test
    void shouldReturnParsedListInGetActivities() throws IOException {
        //given
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body("""
                        [{
                          "id": 1,
                          "name": "Morning Run",
                          "distance": 10000.0,
                          "moving_time": 3600,
                          "elapsed_time": 3700,
                          "type": "Run",
                          "sport_type": "Run",
                          "start_date": "2026-01-01T07:00:00Z",
                          "start_date_local": "2026-01-01T08:00:00Z",
                          "timezone": "UTC",
                          "total_elevation_gain": 100.0,
                          "average_speed": 2.8,
                          "max_speed": 4.0,
                          "elev_high": 50.0,
                          "elev_low": 10.0
                        }]
                        """)
                .build());

        //when
        List<Activity> activities = client.getActivities("token");

        //then
        assertThat(activities).hasSize(1);
        assertThat(activities.getFirst().name()).isEqualTo("Morning Run");
        assertThat(activities.getFirst().distance()).isEqualTo(10000.0);
    }

    @Test
    void shouldThrowOnUnauthorizedInGetActivities() {
        //given
        server.enqueue(new MockResponse.Builder().code(401).build());

        //when / then
        assertThatThrownBy(() -> client.getActivities("expired-token"))
                .isInstanceOf(StravaApiException.class)
                .hasMessageContaining("invalid or expired")
                .extracting(e -> ((StravaApiException) e).getHttpStatus())
                .isEqualTo(401);
    }

    @Test
    void shouldThrowOnRateLimitInGetActivities() {
        //given
        server.enqueue(new MockResponse.Builder().code(429).build());

        //when / then
        assertThatThrownBy(() -> client.getActivities("token"))
                .isInstanceOf(StravaApiException.class)
                .hasMessageContaining("Rate limit exceeded")
                .extracting(e -> ((StravaApiException) e).getHttpStatus())
                .isEqualTo(429);
    }

    @Test
    void shouldThrowOnServerErrorInGetActivities() {
        //given
        server.enqueue(new MockResponse.Builder().code(500).build());

        //when / then
        assertThatThrownBy(() -> client.getActivities("token"))
                .isInstanceOf(StravaApiException.class)
                .hasMessageContaining("Strava server error")
                .extracting(e -> ((StravaApiException) e).getHttpStatus())
                .isEqualTo(500);
    }

    @Test
    void shouldReturnParsedTokenInExchangeCode() throws Exception {
        //given
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body("""
                        {
                          "token_type": "Bearer",
                          "access_token": "new-access",
                          "refresh_token": "new-refresh",
                          "expires_at": 1893456000,
                          "expires_in": 21600,
                          "athlete": {"id": 42}
                        }
                        """)
                .build());

        //when
        StravaTokenResponse response = client.exchangeCode("auth-code", "client-id", "client-secret");

        //then
        assertThat(response.access_token()).isEqualTo("new-access");
        assertThat(response.refresh_token()).isEqualTo("new-refresh");
        assertThat(response.expires_at()).isEqualTo(1893456000L);
        assertThat(response.athlete().id()).isEqualTo(42L);
    }

    @Test
    void shouldSendCorrectResponseInExchangeCode() throws Exception {
        //given
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body("""
                        {
                          "token_type": "Bearer",
                          "access_token": "a",
                          "refresh_token": "r",
                          "expires_at": 1000,
                          "expires_in": 3600,
                          "athlete": {"id": 1}
                        }
                        """)
                .build());

        //when
        client.exchangeCode("auth-code", "my-client-id", "my-client-secret");

        //then
        var recorded = server.takeRequest();
        var body = recorded.getBody().utf8();
        assertThat(body)
                .contains("code=auth-code")
                .contains("client_id=my-client-id")
                .contains("client_secret=my-client-secret")
                .contains("grant_type=authorization_code");
    }

    @Test
    void shouldThrowUnauthorizedOnExchangeCodeWhen401() {
        //given
        server.enqueue(new MockResponse.Builder().code(401).build());

        //when / then
        assertThatThrownBy(() -> client.exchangeCode("bad-code", "client-id", "secret"))
                .isInstanceOf(StravaApiException.class)
                .hasMessageContaining("invalid or expired")
                .extracting(e -> ((StravaApiException) e).getHttpStatus())
                .isEqualTo(401);
    }
}
