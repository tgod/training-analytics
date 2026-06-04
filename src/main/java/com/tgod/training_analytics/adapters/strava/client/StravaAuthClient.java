package com.tgod.training_analytics.adapters.strava.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgod.training_analytics.domain.strava.model.StravaTokenResponse;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StravaAuthClient {

    private static final String TOKEN_URL =
            "https://www.strava.com/oauth/token";

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public StravaAuthClient(
            OkHttpClient client,
            ObjectMapper mapper
    ) {
        this.client = client;
        this.mapper = mapper;
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
