package com.tgod.training_analytics.adapters.strava;

import com.tgod.training_analytics.adapters.strava.client.StravaApiClient;
import com.tgod.training_analytics.adapters.strava.client.StravaApiException;
import com.tgod.training_analytics.domain.activities.exception.ActivitiesFetchException;
import com.tgod.training_analytics.domain.activities.model.Activity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SportActivitiesDataAdapterTest {

    @Mock
    private StravaApiClient client;

    @InjectMocks
    private SportActivitiesDataAdapter adapter;

    private static final String TOKEN = "access-token";
    private static final Instant AFTER = Instant.now().minus(30, ChronoUnit.DAYS);

    @Test
    void shouldReturnsActivitiesFromClient() throws IOException {
        //given
        List<Activity> activities = List.of(activity());
        when(client.getActivities(TOKEN, AFTER.getEpochSecond(), 1, 100)).thenReturn(activities);

        //when
        List<Activity> result = adapter.getActivities(TOKEN, AFTER);

        //then
        assertThat(result).isEqualTo(activities);
    }

    @Test
    void shouldWrapIOExceptionInActivitiesFetchException() throws IOException {
        //given
        when(client.getActivities(TOKEN, AFTER.getEpochSecond(), 1, 100)).thenThrow(new IOException("connection refused"));

        //when / then
        assertThatThrownBy(() -> adapter.getActivities(TOKEN, AFTER))
                .isInstanceOf(ActivitiesFetchException.class)
                .hasMessageContaining("Network error")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void shouldWrapStravaApiExceptionInActivitiesFetchException() throws IOException {
        //given
        when(client.getActivities(TOKEN, AFTER.getEpochSecond(), 1, 100)).thenThrow(new StravaApiException(401, "Unauthorized"));

        //when / then
        assertThatThrownBy(() -> adapter.getActivities(TOKEN, AFTER))
                .isInstanceOf(ActivitiesFetchException.class)
                .hasMessageContaining("Strava API returned an error")
                .hasCauseInstanceOf(StravaApiException.class);
    }

    private Activity activity() {
        return new Activity(1L, "Morning Run", 10000, 3600, 3700, "Run", "Run",
                "2026-01-01T07:00:00Z", "2026-01-01T08:00:00Z", "UTC",
                100, 2.8, 4.0, 145.0, 170.0, null, null, null, null, null,
                false, false, 0, 5, null, 50.0, 10.0);
    }
}
