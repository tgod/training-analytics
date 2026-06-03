package com.tgod.training_analytics.adapters.strava.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "strava_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_strava_tokens_username",
                        columnNames = "username"
                )
        }
)
public class StravaToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Column(name = "access_token", nullable = false, length = 1000)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, length = 1000)
    private String refreshToken;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected StravaToken() {
        // JPA
    }

    public StravaToken(
            String username,
            Long athleteId,
            String accessToken,
            String refreshToken,
            Instant expiresAt
    ) {
        this.username = username;
        this.athleteId = athleteId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    void prePersist() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateTokens(
            String accessToken,
            String refreshToken,
            Instant expiresAt
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }
}
