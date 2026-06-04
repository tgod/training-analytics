package com.tgod.training_analytics.adapters.strava.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgod.training_analytics.adapters.strava.model.StravaTokenResponse;
import com.tgod.training_analytics.domain.activities.model.Activity;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StravaApiClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public StravaApiClient(
            OkHttpClient client,
            ObjectMapper mapper,
            @Value("${strava.base-url}") String baseUrl
    ) {
        this.client = client;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    private <T> T get(
            String url,
            Map<String, String> params,
            String token,
            Class<T> responseType
    ) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        params.forEach(urlBuilder::addQueryParameter);
        var request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        try (var response = client.newCall(request).execute()) {
            checkResponse(response);
            return mapper.readValue(bodyString(response), responseType);
        }
    }

    private void checkResponse(okhttp3.Response response) {
        if (!response.isSuccessful()) {
            throw StravaApiException.forStatus(response.code());
        }
    }

    private String bodyString(okhttp3.Response response) throws IOException {
        var body = response.body();
        if (body == null) {
            throw new IOException("Empty response body");
        }
        return body.string();
    }

    public List<Activity> getActivities(String token, long afterTimestamp, int page, int per_page) throws IOException {
        return Arrays.stream(get(
                baseUrl + "/api/v3/athlete/activities",
                Map.of("after", String.valueOf(afterTimestamp),
                        "page", String.valueOf(page),
                        "per_page", String.valueOf(per_page)),
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
                .url(baseUrl + "/oauth/token")
                .post(body)
                .build();

        try (var response = client.newCall(request).execute()) {
            checkResponse(response);
            return mapper.readValue(bodyString(response), StravaTokenResponse.class);
        }
    }

    public StravaTokenResponse refreshToken(
            String refreshToken,
            String clientId,
            String clientSecret
    ) throws IOException {

        var body = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
                .build();

        var request = new Request.Builder()
                .url(baseUrl + "/oauth/token")
                .post(body)
                .build();

        try (var response = client.newCall(request).execute()) {
            checkResponse(response);
            return mapper.readValue(bodyString(response), StravaTokenResponse.class);
        }
    }
}
