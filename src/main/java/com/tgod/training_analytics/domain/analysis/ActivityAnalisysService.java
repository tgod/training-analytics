package com.tgod.training_analytics.domain.analysis;

import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.repository.AccessTokenRepository;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import com.tgod.training_analytics.domain.openai.PromptBuilderService;
import com.tgod.training_analytics.domain.ports.activities.SportActivitiesDataPort;
import com.tgod.training_analytics.domain.ports.openai.LLMPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityAnalisysService {

    private static final String SYSTEM_PROMPT = """
        You are an expert endurance sports coach and exercise physiologist.
        Analyze athlete training data and provide actionable, evidence-based feedback.
        Be concise, specific, and encouraging. Avoid generic advice.
        Always respond with valid JSON only — no markdown, no explanation outside JSON.
        """;
    @Autowired
    private SportActivitiesDataPort stravaPort;
    @Autowired
    private LLMPort llmPort;
    @Autowired
    private PromptBuilderService promptBuilder;
    @Autowired
    private AccessTokenRepository accessTokenRepository;

    public TrainingAnalysis analyze(String username, int weeks) {
        List<Activity> activities = getActivities(username, weeks);
        String userPrompt = promptBuilder.buildUserPrompt(activities, weeks);
        return llmPort.analyze(SYSTEM_PROMPT, userPrompt);
    }

    private List<Activity> getActivities(String username, int weeks) {
        var token = accessTokenRepository.findByUsername(username);
        if(token.isEmpty()){
            //TODO: error handling
            throw new RuntimeException("no token");
        }
        return stravaPort.getActivities(token.get().getAccessToken(), weeks);
    }

}
