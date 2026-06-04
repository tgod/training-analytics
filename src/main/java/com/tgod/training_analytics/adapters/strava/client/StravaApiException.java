package com.tgod.training_analytics.adapters.strava.client;

public class StravaApiException extends RuntimeException {

    private final int httpStatus;

    public StravaApiException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    static StravaApiException forStatus(int status) {
        String message = switch (status) {
            case 401 -> "Unauthorized — access token is invalid or expired";
            case 403 -> "Forbidden — insufficient permissions";
            case 429 -> "Rate limit exceeded";
            default -> status >= 500
                    ? "Strava server error (HTTP " + status + ")"
                    : "Unexpected HTTP status " + status;
        };
        return new StravaApiException(status, message);
    }
}
