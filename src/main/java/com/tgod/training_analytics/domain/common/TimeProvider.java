package com.tgod.training_analytics.domain.common;

import org.springframework.stereotype.Component;

import java.time.Instant;

public interface TimeProvider {

    default Instant getTime() {
        return Instant.now();
    }

}

