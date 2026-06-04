package com.tgod.training_analytics.domain.openai;

import com.tgod.training_analytics.domain.activities.model.Activity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromptBuilderService {

    public String buildUserPrompt(List<Activity> activities, int weeks) {
        Map<String, List<Activity>> byWeek = groupByWeek(activities);
        String sport = detectSport(activities);
        double avgDaysPerWeek = (double) activities.size() / weeks;
        var weeklyTrainingDays = String.format("%.1f", avgDaysPerWeek);
        String recentActivities = getRecentActivities(activities);
        String weeklySummaries = getWeeklySummaries(byWeek);

        String userPrompt = """
                Analyze the following training data for the past %s weeks and provide a training load assessment.

                Athlete profile:
                - Sport: %s
                - Weekly training days: %s

                Recent activities:
                %s
                Weekly summaries:
                %s
                Please provide:
                1. Current training load assessment (easy / moderate / high / overreaching)
                2. Key observations (2-3 bullet points)
                3. Recommendation for next week
                4. One thing to watch out for

                Respond in JSON with keys: loadLevel, observations, nextWeekRecommendation, watchOut
                """;
        return userPrompt.formatted(weeks, sport, weeklyTrainingDays, recentActivities, weeklySummaries);
    }

    @NotNull
    private String getWeeklySummaries(Map<String, List<Activity>> byWeek) {
        StringBuilder sb = new StringBuilder();
        byWeek.forEach((week, acts) -> sb.append(formatWeeklySummary(week, acts)).append("\n"));
        return sb.toString();
    }

    @NotNull
    private String getRecentActivities(List<Activity> activities) {
        StringBuilder sb = new StringBuilder();
        for (Activity a : activities) {
            sb.append(formatActivity(a)).append("\n");
        }
        return sb.toString();
    }

    private String formatActivity(Activity a) {
        return "- Date: %s | Type: %s | Distance: %.1f km | Duration: %d min | Avg HR: %.0f bpm | Elevation: %.0fm | Suffer score: %.0f"
                .formatted(
                        a.startDateLocal().substring(0, 10),
                        a.type(),
                        a.distance() / 1000,
                        a.movingTime() / 60,
                        a.averageHeartrate(),
                        a.totalElevationGain(),
                        a.sufferScore()
                );
    }

    private String formatWeeklySummary(String week, List<Activity> acts) {
        double totalKm = acts.stream().mapToDouble(a -> a.distance() / 1000).sum();
        int totalMin = acts.stream().mapToInt(a -> a.movingTime() / 60).sum();
        return "- Week %s: %.1f km, %d min, %d sessions".formatted(week, totalKm, totalMin, acts.size());
    }

    private String detectSport(List<Activity> activities) {
        Set<String> types = activities.stream().map(Activity::type).collect(Collectors.toSet());
        if (types.size() > 1) return "mixed";
        return types.iterator().next().toLowerCase();
    }

    private Map<String, List<Activity>> groupByWeek(List<Activity> activities) {
        return activities.stream().collect(Collectors.groupingBy(a -> {
            LocalDate date = LocalDate.parse(a.startDateLocal().substring(0, 10));
            return date.get(WeekFields.ISO.weekOfWeekBasedYear()) + "/" + date.getYear();
        }));
    }
}
