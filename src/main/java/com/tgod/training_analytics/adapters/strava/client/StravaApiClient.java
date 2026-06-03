package com.tgod.training_analytics.adapters.strava.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StravaApiClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public StravaApiClient(
            OkHttpClient client,
            ObjectMapper mapper
    ) {
        this.client = client;
        this.mapper = mapper;
    }

    public <T> T get(
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
}