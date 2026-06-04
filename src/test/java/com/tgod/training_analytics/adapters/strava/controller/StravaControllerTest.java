package com.tgod.training_analytics.adapters.strava.controller;

import com.tgod.training_analytics.adapters.main.config.SecurityConfiguration;
import com.tgod.training_analytics.adapters.strava.config.StravaProperties;
import com.tgod.training_analytics.domain.activities.model.Activity;
import com.tgod.training_analytics.domain.activities.usecase.AuthenticationCallbackUC;
import com.tgod.training_analytics.domain.activities.usecase.GetActivitiesUC;
import com.tgod.training_analytics.domain.activities.usecase.SyncActivitiesUC;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private SyncActivitiesUC syncActivitiesUC;

    @MockitoBean
    private StravaProperties stravaProperties;

    @Test
    void shouldHandleCallback() throws Exception {
        mockMvc.perform(get("/strava/callback/admin").param("code", "test-code"))
                .andExpect(status().isOk());
        verify(authCallbackUC).handleCallback("test-code", "admin");
    }

    @Test
    void shouldReturnActivitiesForAuthenticatedUser() throws Exception {
        var activity = new Activity(1L, "Morning Run", 5000.0, 1800, 1900,
                "Run", "Run", "2024-01-01T08:00:00Z", "2024-01-01T08:00:00Z",
                "Europe/Warsaw", 100.0, 2.77, 3.5, 150.0, 175.0, 85.0,
                null, null, null, null, false, false, 0, 0, 42.0, 150.0, 120.0);
        when(getActivitiesUC.getActivities("admin")).thenReturn(List.of(activity));

        mockMvc.perform(get("/strava/activities").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Morning Run"))
                .andExpect(jsonPath("$[0].distance").value(5000.0));
    }

    @Test
    void shouldReturn401WhenFetchingActivitiesUnauthenticated() throws Exception {
        mockMvc.perform(get("/strava/activities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRedirectToStravaOAuthForAuthenticatedUser() throws Exception {
        when(stravaProperties.clientId()).thenReturn("12345");
        when(stravaProperties.redirectUri()).thenReturn("http://localhost/strava/callback");
        when(stravaProperties.baseUrl()).thenReturn("https://www.strava.com");

        mockMvc.perform(get("/strava/login").with(user("admin")))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", containsString("strava.com/oauth/authorize")))
                .andExpect(header().string("Location", containsString("client_id=12345")))
                .andExpect(header().string("Location", containsString("redirect_uri=http")));
    }

    @Test
    void shouldReturn401WhenLoginUnauthenticated() throws Exception {
        mockMvc.perform(get("/strava/login"))
                .andExpect(status().isUnauthorized());
    }
}
