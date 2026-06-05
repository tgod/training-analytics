package com.tgod.training_analytics.domain.analysis;

import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.model.ActivityEntity;
import com.tgod.training_analytics.domain.activities.repository.ActivityRepository;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.openai.PromptBuilderService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityAnalysisServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private LLMPort llmPort;
    @Mock
    private PromptBuilderService promptBuilder;
    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private ActivityAnalysisService service;

    private static final String USERNAME = "admin";
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
        var activity = new Activity(1L, "Morning Run", 10000, 3600, 3600, "Run", "Run",
                "2026-05-01T07:00:00Z", "2026-05-01T08:00:00Z", "UTC",
                100, 2.8, 4.0, 145.0, 170.0, null, null, null, null, null,
                false, false, 0, 3, null, 50, 10);
        var entity = new ActivityEntity(activity, USERNAME);
        var after = NOW.minus(4 * 7L, ChronoUnit.DAYS).toString();
        var userPrompt = "Analyze 4 weeks of training...";
        var expected = new TrainingAnalysis("moderate", List.of("Good volume"), "Keep it up", "Watch HR");

        when(timeProvider.getTime()).thenReturn(NOW);
        when(activityRepository.findAllByUsernameAndStartDateGreaterThan(USERNAME, after)).thenReturn(List.of(entity));
        when(promptBuilder.buildUserPrompt(List.of(activity), 4)).thenReturn(userPrompt);
        when(llmPort.analyze(SYSTEM_PROMPT, userPrompt)).thenReturn(expected);

        //when
        TrainingAnalysis result = service.analyze(USERNAME, 4);

        //then
        assertThat(result).isEqualTo(expected);
    }
}
