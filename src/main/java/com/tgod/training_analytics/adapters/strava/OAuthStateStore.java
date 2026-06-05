package com.tgod.training_analytics.adapters.strava;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OAuthStateStore {

    private static final int TTL_SECONDS = 600;

    private record Entry(String username, Instant expiresAt) {}

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    public String generate(String username) {
        String state = UUID.randomUUID().toString();
        store.put(state, new Entry(username, Instant.now().plusSeconds(TTL_SECONDS)));
        return state;
    }

    public Optional<String> consumeUsername(String state) {
        Entry entry = store.remove(state);
        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            return Optional.empty();
        }
        return Optional.of(entry.username());
    }
}
