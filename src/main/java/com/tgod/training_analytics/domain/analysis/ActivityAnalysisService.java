package com.tgod.training_analytics.domain.analysis;

import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.model.ActivityEntity;
import com.tgod.training_analytics.domain.activities.repository.ActivityRepository;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.openai.PromptBuilderService;
import com.tgod.training_analytics.domain.ports.openai.LLMPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ActivityAnalysisService {

    @Value("classpath:prompts/analysis-system-prompt.txt")
    private Resource systemPromptResource;

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private LLMPort llmPort;
    @Autowired
    private PromptBuilderService promptBuilder;
    @Autowired
    private TimeProvider timeProvider;

    public TrainingAnalysis analyze(String username, int weeks) {
        var after = timeProvider.getTime().minus(weeks * 7L, ChronoUnit.DAYS).toString();
        List<Activity> activities = activityRepository
                .findAllByUsernameAndStartDateGreaterThan(username, after).stream()
                .map(ActivityEntity::toActivity)
                .toList();
        String systemPrompt = loadSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(activities, weeks);
        return llmPort.analyze(systemPrompt, userPrompt);
    }

    private String loadSystemPrompt() {
        try {
            return systemPromptResource.getContentAsString(UTF_8);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Failed to load system prompt", e);
        }
    }

}
