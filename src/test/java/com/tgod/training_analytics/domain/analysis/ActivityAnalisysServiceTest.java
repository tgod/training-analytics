package com.tgod.training_analytics.domain.analysis;

import com.tgod.training_analytics.domain.activities.exception.TokenNotFoundException;
import com.tgod.training_analytics.domain.activities.model.AccessToken;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.usecase.AccessTokenUC;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.openai.PromptBuilderService;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.ports.openai.LLMPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityAnalisysServiceTest {

    @Mock
    private SportActivitiesDataPort stravaPort;
    @Mock
    private LLMPort llmPort;
    @Mock
    private PromptBuilderService promptBuilder;
    @Mock
    private AccessTokenUC accessTokenUC;
    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private ActivityAnalisysService service;

    private static final String USERNAME = "admin";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String SYSTEM_PROMPT = "You are a coach.";
    private static final Instant NOW = Instant.parse("2026-06-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "systemPromptResource",
                new ByteArrayResource(SYSTEM_PROMPT.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void analyzeDelegatesToCollaboratorsAndReturnsResult() {
        //given
        var token = new AccessToken(USERNAME, 42L, ACCESS_TOKEN, "refresh", Instant.parse("2026-12-01T00:00:00Z"));
        List<Activity> activities = List.of();
        var userPrompt = "Analyze 4 weeks of training...";
        var expected = new TrainingAnalysis("moderate", List.of("Good volume"), "Keep it up", "Watch HR");

        when(timeProvider.getTime()).thenReturn(NOW);
        when(accessTokenUC.getByUsername(USERNAME)).thenReturn(token);
        when(stravaPort.getActivities(ACCESS_TOKEN, NOW.minus(4*7, ChronoUnit.DAYS))).thenReturn(activities);
        when(promptBuilder.buildUserPrompt(activities, 4)).thenReturn(userPrompt);
        when(llmPort.analyze(SYSTEM_PROMPT, userPrompt)).thenReturn(expected);

        //when
        TrainingAnalysis result = service.analyze(USERNAME, 4);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void analyzePropagatesTokenNotFoundExceptionWhenTokenMissing() {
        //given
        when(accessTokenUC.getByUsername(USERNAME)).thenThrow(new TokenNotFoundException(USERNAME));

        //when / then
        assertThatThrownBy(() -> service.analyze(USERNAME, 4))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining(USERNAME);
    }
}
