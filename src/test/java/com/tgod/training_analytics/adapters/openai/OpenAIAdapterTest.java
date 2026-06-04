package com.tgod.training_analytics.adapters.openai;

import com.tgod.training_analytics.adapters.openai.config.OpenAIProperties;
import com.tgod.training_analytics.domain.analysis.exception.LLMException;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenAIAdapterTest {

    private MockWebServer server;
    private OpenAIAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();

        var properties = mock(OpenAIProperties.class);
        when(properties.apiKey()).thenReturn("test-key");
        when(properties.model()).thenReturn("gpt-4o");

        adapter = new OpenAIAdapter( new OkHttpClient(),
                new ObjectMapper(),
                properties,
                "http://localhost:" + server.getPort());
    }

    @AfterEach
    void tearDown() throws Exception {
        server.close();
    }

    @Test
    void shouldReturnTrainingAnalysisOnSuccess() {
        //given
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body(openAiResponse("""
                        {"loadLevel":"moderate","observations":["Good volume"],"nextWeekRecommendation":"Keep it up","watchOut":"Watch HR"}
                        """))
                .build());

        //when
        TrainingAnalysis result = adapter.analyze("You are a coach.", "Analyze my training.");

        //then
        assertThat(result.loadLevel()).isEqualTo("moderate");
        assertThat(result.observations()).isEqualTo(List.of("Good volume"));
        assertThat(result.nextWeekRecommendation()).isEqualTo("Keep it up");
        assertThat(result.watchOut()).isEqualTo("Watch HR");
    }

    @Test
    void shouldStringMessageBeforeParsing() {
        //given
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body(openAiResponse("""
                        ```json
                        {"loadLevel":"high","observations":["High load"],"nextWeekRecommendation":"Rest","watchOut":"Overtraining"}
                        ```
                        """))
                .build());

        //when
        TrainingAnalysis result = adapter.analyze("You are a coach.", "Analyze my training.");

        //then
        assertThat(result.loadLevel()).isEqualTo("high");
    }

    @Test
    void shouldThrowLLMExceptionOnNonSuccessfulHttpStatus() {
        //given
        server.enqueue(new MockResponse.Builder()
                .code(429)
                .body("Rate limit exceeded")
                .build());

        //when / then
        assertThatThrownBy(() -> adapter.analyze("You are a coach.", "Analyze my training."))
                .isInstanceOf(LLMException.class)
                .hasMessageContaining("429");
    }

    @Test
    void shouldThrowLLMExceptionOnEmptyChoices() {
        //given
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body("""
                        {"choices":[]}
                        """)
                .build());

        //when / then
        assertThatThrownBy(() -> adapter.analyze("You are a coach.", "Analyze my training."))
                .isInstanceOf(LLMException.class)
                .hasMessageContaining("no choices");
    }

    private String openAiResponse(String content) {
        return """
                {"choices":[{"message":{"role":"assistant","content":"%s"}}]}
                """.formatted(content.strip().replace("\"", "\\\"").replace("\n", "\\n"));
    }
}
