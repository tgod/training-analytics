package com.tgod.training_analytics.adapters.strava.controller;

import com.tgod.training_analytics.adapters.main.config.SecurityConfiguration;
import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.domain.activities.usecase.AuthenticationCallbackUC;
import com.tgod.training_analytics.domain.activities.usecase.GetActivitiesUC;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StravaController.class)
@Import(SecurityConfiguration.class)
class StravaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationCallbackUC authCallbackUC;

    @MockitoBean
    private GetActivitiesUC getActivitiesUC;

    @MockitoBean
    private StravaProperties stravaProperties;

    @Test
    void shouldHandleCallback() throws Exception {
        mockMvc.perform(get("/strava/callback/admin").param("code", "test-code"))
                .andExpect(status().isOk());
        verify(authCallbackUC).handleCallback("test-code", "admin");
    }
}
