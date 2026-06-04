package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.event.ActivityCreated;
import com.tgod.training_analytics.domain.activities.exception.TokenNotFoundException;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.model.ActivityEntity;
import com.tgod.training_analytics.domain.activities.repository.ActivityRepository;
import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncActivitiesUCTest {

    @Mock
    private SportActivitiesDataPort service;

    @Mock
    private AccessTokenUC accessTokenUC;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SyncActivitiesUC uc;

    private static final String USERNAME = "admin";
    private static final String ACCESS_TOKEN = "access-token";
    private static final Instant NOW = Instant.parse("2026-06-01T00:00:00Z");

    private static final Activity EXISTING_ACTIVITY = new Activity(
            1L, "Old Run", 8000, 2800, 2800, "Run", "Run",
            "2026-05-01T07:00:00Z", "2026-05-01T08:00:00Z", "UTC",
            50, 2.5, 3.5, 140.0, 165.0, null, null, null, null, null,
            false, false, 0, 2, null, 40, 5
    );

    private static final Activity NEW_ACTIVITY = new Activity(
            2L, "Morning Run", 10000, 3600, 3600, "Run", "Run",
            "2026-05-31T07:00:00Z", "2026-05-31T08:00:00Z", "UTC",
            100, 2.8, 4.0, 145.0, 170.0, null, null, null, null, null,
            false, false, 0, 3, null, 50, 10
    );

    private AccessToken token() {
        return new AccessToken(USERNAME, 42L, ACCESS_TOKEN, "refresh", Instant.parse("2026-12-01T00:00:00Z"));
    }

    @Test
    void returnsOnlyNewActivities() throws IOException {
        //given
        var lastStored = new ActivityEntity(EXISTING_ACTIVITY, USERNAME);
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token());
        when(activityRepository.findTopByUsernameOrderByStartDateDesc(USERNAME)).thenReturn(Optional.of(lastStored));
        when(service.getActivities(ACCESS_TOKEN, Instant.parse(EXISTING_ACTIVITY.startDate()))).thenReturn(List.of(EXISTING_ACTIVITY, NEW_ACTIVITY));
        when(activityRepository.findByStravaId(EXISTING_ACTIVITY.id())).thenReturn(Optional.of(lastStored));
        when(activityRepository.findByStravaId(NEW_ACTIVITY.id())).thenReturn(Optional.empty());

        //when
        List<Activity> result = uc.execute(USERNAME);

        //then
        assertThat(result).containsExactly(NEW_ACTIVITY);
    }

    @Test
    void usesLastActivityStartDateAsAfterWhenActivitiesExist() throws IOException {
        //given
        var lastStored = new ActivityEntity(EXISTING_ACTIVITY, USERNAME);
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token());
        when(activityRepository.findTopByUsernameOrderByStartDateDesc(USERNAME)).thenReturn(Optional.of(lastStored));
        when(service.getActivities(ACCESS_TOKEN, Instant.parse(EXISTING_ACTIVITY.startDate()))).thenReturn(List.of());

        //when
        uc.execute(USERNAME);

        //then
        verify(service).getActivities(ACCESS_TOKEN, Instant.parse(EXISTING_ACTIVITY.startDate()));
    }

    @Test
    void usesDefaultLookbackWhenNoActivitiesStoredForUser() throws IOException {
        //given
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token());
        when(activityRepository.findTopByUsernameOrderByStartDateDesc(USERNAME)).thenReturn(Optional.empty());
        when(timeProvider.getTime()).thenReturn(NOW);
        when(service.getActivities(ACCESS_TOKEN, NOW.minus(30, ChronoUnit.DAYS))).thenReturn(List.of());

        //when
        uc.execute(USERNAME);

        //then
        verify(service).getActivities(ACCESS_TOKEN, NOW.minus(30, ChronoUnit.DAYS));
    }

    @Test
    void savesNewEntityWhenActivityNotInDb() throws IOException {
        //given
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token());
        when(activityRepository.findTopByUsernameOrderByStartDateDesc(USERNAME)).thenReturn(Optional.empty());
        when(timeProvider.getTime()).thenReturn(NOW);
        when(service.getActivities(ACCESS_TOKEN, NOW.minus(30, ChronoUnit.DAYS))).thenReturn(List.of(NEW_ACTIVITY));
        when(activityRepository.findByStravaId(NEW_ACTIVITY.id())).thenReturn(Optional.empty());

        //when
        uc.execute(USERNAME);

        //then
        ArgumentCaptor<ActivityEntity> captor = ArgumentCaptor.forClass(ActivityEntity.class);
        verify(activityRepository).save(captor.capture());
        assertThat(captor.getValue().getStravaId()).isEqualTo(NEW_ACTIVITY.id());
        assertThat(captor.getValue().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void updatesAndSavesExistingEntityWhenActivityAlreadyInDb() throws IOException {
        //given
        var existing = new ActivityEntity(EXISTING_ACTIVITY, USERNAME);
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token());
        when(activityRepository.findTopByUsernameOrderByStartDateDesc(USERNAME)).thenReturn(Optional.of(existing));
        when(service.getActivities(ACCESS_TOKEN, Instant.parse(EXISTING_ACTIVITY.startDate()))).thenReturn(List.of(EXISTING_ACTIVITY));
        when(activityRepository.findByStravaId(EXISTING_ACTIVITY.id())).thenReturn(Optional.of(existing));

        //when
        uc.execute(USERNAME);

        //then
        verify(activityRepository).save(existing);
    }

    @Test
    void publishesActivityCreatedEventForNewActivity() throws IOException {
        //given
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token());
        when(activityRepository.findTopByUsernameOrderByStartDateDesc(USERNAME)).thenReturn(Optional.empty());
        when(timeProvider.getTime()).thenReturn(NOW);
        when(service.getActivities(ACCESS_TOKEN, NOW.minus(30, ChronoUnit.DAYS))).thenReturn(List.of(NEW_ACTIVITY));
        when(activityRepository.findByStravaId(NEW_ACTIVITY.id())).thenReturn(Optional.empty());

        //when
        uc.execute(USERNAME);

        //then
        verify(eventPublisher).publishEvent(new ActivityCreated(USERNAME, NEW_ACTIVITY.id(), NEW_ACTIVITY.name()));
    }

    @Test
    void doesNotPublishActivityCreatedEventForExistingActivity() throws IOException {
        //given
        var existing = new ActivityEntity(EXISTING_ACTIVITY, USERNAME);
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token());
        when(activityRepository.findTopByUsernameOrderByStartDateDesc(USERNAME)).thenReturn(Optional.of(existing));
        when(service.getActivities(ACCESS_TOKEN, Instant.parse(EXISTING_ACTIVITY.startDate()))).thenReturn(List.of(EXISTING_ACTIVITY));
        when(activityRepository.findByStravaId(EXISTING_ACTIVITY.id())).thenReturn(Optional.of(existing));

        //when
        uc.execute(USERNAME);

        //then
        verify(eventPublisher, org.mockito.Mockito.never()).publishEvent(org.mockito.Mockito.any());
    }

    @Test
    void propagatesTokenNotFoundExceptionWhenTokenMissing() {
        //given
        when(accessTokenUC.getByUsername(USERNAME)).thenThrow(new TokenNotFoundException(USERNAME));

        //when / then
        assertThatThrownBy(() -> uc.execute(USERNAME))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining(USERNAME);
    }
}
