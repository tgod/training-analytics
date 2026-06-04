package com.tgod.training_analytics.adapters.strava.client;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    void getActivitiesReturnsParsedList() throws IOException {
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
    void getActivitiesThrowsOnUnauthorized() {
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
    void getActivitiesThrowsOnRateLimit() {
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
    void getActivitiesThrowsOnServerError() {
        //given
        server.enqueue(new MockResponse.Builder().code(500).build());

        //when / then
        assertThatThrownBy(() -> client.getActivities("token"))
                .isInstanceOf(StravaApiException.class)
                .hasMessageContaining("Strava server error")
                .extracting(e -> ((StravaApiException) e).getHttpStatus())
                .isEqualTo(500);
    }
}
