package com.tgod.training_analytics.domain.activities.usecase;

import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.model.ActivityEntity;
import com.tgod.training_analytics.domain.activities.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetActivitiesUCTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private GetActivitiesUC uc;

    private static final String USERNAME = "admin";

    @Test
    void returnsActivitiesForUser() {
        //given
        var activity = new Activity(1L, "Morning Run", 10000, 3600, 3600, "Run", "Run",
                "2026-01-01T07:00:00Z", "2026-01-01T08:00:00Z", "UTC",
                100, 2.8, 4.0, 145.0, 170.0, null, null, null, null, null,
                false, false, 0, 3, null, 50, 10);
        var entity = new ActivityEntity(activity, USERNAME);
        when(activityRepository.findAllByUsername(USERNAME)).thenReturn(List.of(entity));

        //when
        List<Activity> result = uc.getActivities(USERNAME);

        //then
        assertThat(result).containsExactly(activity);
    }

    @Test
    void returnsEmptyListWhenNoActivities() {
        //given
        when(activityRepository.findAllByUsername(USERNAME)).thenReturn(List.of());

        //when
        List<Activity> result = uc.getActivities(USERNAME);

        //then
        assertThat(result).isEmpty();
    }
}
