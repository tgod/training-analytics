package com.tgod.training_analytics.adapters.openai;

import com.tgod.training_analytics.adapters.openai.config.OpenAIProperties;
import com.tgod.training_analytics.adapters.openai.model.OpenAIResponse;
import com.tgod.training_analytics.domain.analysis.exception.LLMException;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import com.tgod.training_analytics.domain.ports.openai.LLMPort;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIAdapter implements LLMPort {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final OpenAIProperties properties;
    private final String baseUrl;

    public OpenAIAdapter(OkHttpClient httpClient,
                         ObjectMapper objectMapper,
                         OpenAIProperties properties,
                         @Value("${openai.base-url:https://api.openai.com}")
                         String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.baseUrl = baseUrl;
    }

    @Override
    public TrainingAnalysis analyze(String systemPrompt, String userPrompt) {
        var requestBody = buildRequestBody(systemPrompt, userPrompt);

        var request = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")
                .header("Authorization", "Bearer " + properties.apiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            var body = response.body();
            if (!response.isSuccessful()) {
                String errorBody = body != null ? body.string() : "(no body)";
                throw new LLMException("OpenAI call failed with HTTP " + response.code() + ": " + errorBody);
            }
            if (body == null) {
                throw new LLMException("OpenAI returned an empty response body");
            }
            var parsed = objectMapper.readValue(body.string(), OpenAIResponse.class);
            if (parsed.choices() == null || parsed.choices().isEmpty()) {
                throw new LLMException("OpenAI returned no choices");
            }
            var content = parsed.choices().get(0).message().content();
            return parseResponse(content);
        } catch (IOException e) {
            throw new LLMException("Network error calling OpenAI", e);
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
