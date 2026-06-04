package com.tgod.training_analytics.domain.activities.event;

public record ActivityCreated(String username, Long stravaId, String name) {}
