package com.tgod.training_analytics.adapters.openai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record OpenAIProperties(String apiKey, String model) {
}
