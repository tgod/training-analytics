package com.tgod.training_analytics.adapters.openai;

import com.tgod.training_analytics.adapters.openai.config.OpenAIProperties;
import com.tgod.training_analytics.adapters.openai.model.OpenAIResponse;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import com.tgod.training_analytics.domain.ports.openai.LLMPort;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIAdapter implements LLMPort {

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenAIProperties properties;

    @Override
    public TrainingAnalysis analyze(String systemPrompt, String userPrompt) {
        var requestBody = buildRequestBody(systemPrompt, userPrompt);

        var request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + properties.apiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("OpenAI call failed: " + response.code() + " " + response.body().string());
            }
            var parsed = objectMapper.readValue(response.body().string(), OpenAIResponse.class);
            var content = parsed.choices().get(0).message().content();
            return parseResponse(content);
        } catch (IOException e) {
            //TODO: Error handling
            throw new RuntimeException("Failed to call OpenAI", e);
        }
    }

    private String buildRequestBody(String systemPrompt, String userPrompt) {
        var messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );
        return objectMapper.writeValueAsString(Map.of(
                "model", properties.model(),
                "messages", messages
        ));
    }

    private TrainingAnalysis parseResponse(String json) {
        String cleaned = json.strip()
                .replaceAll("^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .strip();
        return objectMapper.readValue(cleaned, TrainingAnalysis.class);
    }
}
