package com.tgod.training_analytics.adapters.strava.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgod.training_analytics.adapters.strava.model.StravaTokenResponse;
import com.tgod.training_analytics.domain.activities.model.Activity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class StravaApiClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    private static final String TOKEN_URL =
            "https://www.strava.com/oauth/token";
    private static final String ACTIVITIES_URL =
            "https://www.strava.com/api/v3/athlete/activities";

    public StravaApiClient(
            OkHttpClient client,
            ObjectMapper mapper
    ) {
        this.client = client;
        this.mapper = mapper;
    }

    private <T> T get(
            String url,
            String token,
            Class<T> responseType
    ) throws IOException {

        var request = new Request.Builder()
                .url(url)
                .header("Authorization",
                        "Bearer " + token)
                .build();

        try (var response =
                     client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IllegalStateException(
                        "HTTP " + response.code()
                );
            }
            return mapper.readValue(
                    response.body().string(),
                    responseType
            );
        }
    }

    public List<Activity> getActivities(String token) throws IOException {
       return  Arrays.stream(get(
                ACTIVITIES_URL,
                token,
                Activity[].class
        )).toList();
    }

    public StravaTokenResponse exchangeCode(
            String code,
            String clientId,
            String clientSecret
    ) throws IOException {

        var body = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", code)
                .add("grant_type", "authorization_code")
                .build();

        var request = new Request.Builder()
                .url(TOKEN_URL)
                .post(body)
                .build();

        try (var response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IllegalStateException(
                        "Strava token request failed: "
                                + response.code()
                );
            }

            var json = response.body().string();
            return mapper.readValue(
                    json,
                    StravaTokenResponse.class
            );
        }
    }
}
