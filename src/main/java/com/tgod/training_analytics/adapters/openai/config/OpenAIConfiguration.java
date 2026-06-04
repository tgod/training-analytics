package com.tgod.training_analytics.adapters.openai.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenAIProperties.class)
public class OpenAIConfiguration {
}
