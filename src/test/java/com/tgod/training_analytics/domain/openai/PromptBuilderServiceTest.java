package com.tgod.training_analytics.domain.openai;

import com.tgod.training_analytics.domain.activities.model.Activity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromptBuilderServiceTest {

    private final PromptBuilderService service = new PromptBuilderService();

    @Test
    void shouldIncludeWeekCountHeader() {
        String prompt = service.buildUserPrompt(List.of(run("2026-01-01T08:00:00Z", 10000, 3600)), 4);

        assertThat(prompt).contains("past 4 weeks");
    }

    @Test
    void shouldIncludeSingleSportLowercased() {
        String prompt = service.buildUserPrompt(List.of(run("2026-01-01T08:00:00Z", 10000, 3600)), 1);

        assertThat(prompt).contains("- Sport: run");
    }

    @Test
    void shouldIncludeMixedSportWhenMultipleTypes() {
        var activities = List.of(
                activity("Run", "2026-01-01T08:00:00Z", 10000, 3600),
                activity("Ride", "2026-01-02T08:00:00Z", 30000, 4200)
        );

        String prompt = service.buildUserPrompt(activities, 1);

        assertThat(prompt).contains("- Sport: mixed");
    }

    @Test
    void shouldIncludeFormattedActivityLine() {
        var act = activity("Run", "2026-01-05T08:00:00Z", 10000, 3600);

        String prompt = service.buildUserPrompt(List.of(act), 1);

        assertThat(prompt)
                .contains("2026-01-05")
                .contains("Run")
                .contains("10.0 km")
                .contains("60 min")
                .contains("150 bpm")
                .contains("100m")
                .contains("42");
    }

    @Test
    void shouldIncludeWeeklySummaryWithTotals() {
        var activities = List.of(
                activity("Run", "2026-01-01T08:00:00Z", 10000, 3600),
                activity("Run", "2026-01-02T08:00:00Z", 5000, 1800)
        );

        String prompt = service.buildUserPrompt(activities, 1);

        // both activities land in ISO week 1/2026 → one summary entry with combined totals
        assertThat(prompt)
                .contains("1/2026")
                .contains("15.0 km")
                .contains("90 min")
                .contains("2 sessions");
    }

    @Test
    void shouldIncludeSeparateSummariesForDifferentWeeks() {
        // 2026-01-01 is ISO week 1/2026; 2026-01-05 is ISO week 2/2026
        var activities = List.of(
                activity("Run", "2026-01-01T08:00:00Z", 10000, 3600),
                activity("Run", "2026-01-05T08:00:00Z", 8000, 2700)
        );

        String prompt = service.buildUserPrompt(activities, 2);

        assertThat(prompt)
                .contains("1/2026")
                .contains("2/2026");
    }

    @Test
    void shouldIncludeJsonKeysFooter() {
        String prompt = service.buildUserPrompt(List.of(run("2026-01-01T08:00:00Z", 10000, 3600)), 1);

        assertThat(prompt).contains("loadLevel", "observations", "nextWeekRecommendation", "watchOut");
    }

    @Test
    void shouldProduceFullPrompt() {
        var activity = activity("Run", "2026-01-05T08:00:00Z", 10000, 3600);

        String prompt = service.buildUserPrompt(List.of(activity), 1);

        assertThat(prompt).isEqualTo("""
                Analyze the following training data for the past 1 weeks and provide a training load assessment.

                Athlete profile:
                - Sport: run
                - Weekly training days: 1.0

                Recent activities:
                - Date: 2026-01-05 | Type: Run | Distance: 10.0 km | Duration: 60 min | Avg HR: 150 bpm | Elevation: 100m | Suffer score: 42

                Weekly summaries:
                - Week 2/2026: 10.0 km, 60 min, 1 sessions

                Please provide:
                1. Current training load assessment (easy / moderate / high / overreaching)
                2. Key observations (2-3 bullet points)
                3. Recommendation for next week
                4. One thing to watch out for

                Respond in JSON with keys: loadLevel, observations, nextWeekRecommendation, watchOut
                """);
    }

    private Activity run(String startDateLocal, double distance, int movingTime) {
        return activity("Run", startDateLocal, distance, movingTime);
    }

    private Activity activity(String type, String startDateLocal, double distance, int movingTime) {
        return new Activity(1L, "Test activity", distance, movingTime, movingTime,
                type, type, startDateLocal, startDateLocal, "UTC",
                100.0, distance / movingTime, 5.0,
                150.0, 180.0, null, null, null, null, 42.0,
                false, false, 0, 0, 42.0, 100.0, 0.0);
    }
}
