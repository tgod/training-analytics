package com.tgod.training_analytics.adapters.analysis.controller;

import com.tgod.training_analytics.adapters.main.config.SecurityConfiguration;
import com.tgod.training_analytics.domain.analysis.ActivityAnalysisService;
import com.tgod.training_analytics.domain.analysis.model.TrainingAnalysis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingInsightsController.class)
@Import(SecurityConfiguration.class)
class TrainingInsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityAnalysisService service;

    @Test
    void shouldReturnAnalysisForAuthenticatedUser() throws Exception {
        //given
        var analysis = new TrainingAnalysis("moderate", List.of("Good volume"), "Keep it up", "Watch HR");
        when(service.analyze("admin", 4)).thenReturn(analysis);

        //when / then
        mockMvc.perform(get("/trainings/insights").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loadLevel").value("moderate"))
                .andExpect(jsonPath("$.observations[0]").value("Good volume"))
                .andExpect(jsonPath("$.nextWeekRecommendation").value("Keep it up"))
                .andExpect(jsonPath("$.watchOut").value("Watch HR"));
    }

    @Test
    void shouldReturn401ForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/trainings/insights"))
                .andExpect(status().isUnauthorized());
    }
}
