package com.tgod.training_analytics.domain.ports.openai;

import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;

public interface LLMPort {
    TrainingAnalysis analyze(String systemPrompt, String userPrompt);
}
