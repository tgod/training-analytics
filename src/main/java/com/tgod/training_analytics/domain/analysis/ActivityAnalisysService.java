package com.tgod.training_analytics.domain.analysis;

import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.usecase.AccessTokenUC;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import com.tgod.training_analytics.domain.common.TimeProvider;
import com.tgod.training_analytics.domain.openai.PromptBuilderService;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.ports.openai.LLMPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ActivityAnalisysService {

    @Value("classpath:prompts/analysis-system-prompt.txt")
    private Resource systemPromptResource;

    @Autowired
    private SportActivitiesDataPort stravaPort;
    @Autowired
    private LLMPort llmPort;
    @Autowired
    private PromptBuilderService promptBuilder;
    @Autowired
    private AccessTokenUC accessTokenUC;
    @Autowired
    private TimeProvider timeProvider;

    public TrainingAnalysis analyze(String username, int weeks) {
        List<Activity> activities = getActivities(username, weeks*7);
        String systemPrompt = loadSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(activities, weeks);
        return llmPort.analyze(systemPrompt, userPrompt);
    }

    private List<Activity> getActivities(String username, int days) {
        var token = accessTokenUC.getByUsername(username);
        var after = timeProvider.getTime().minus(days, ChronoUnit.DAYS);
        return stravaPort.getActivities(token.getAccessToken(), after);
    }

    private String loadSystemPrompt() {
        try {
            return systemPromptResource.getContentAsString(UTF_8);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Failed to load system prompt", e);
        }
    }

}
