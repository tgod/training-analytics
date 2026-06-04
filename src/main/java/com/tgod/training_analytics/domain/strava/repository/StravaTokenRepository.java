package com.tgod.training_analytics.domain.strava.repository;

import com.tgod.training_analytics.domain.strava.model.StravaToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StravaTokenRepository
        extends CrudRepository<StravaToken, Long> {

    Optional<StravaToken> findByUsername(String username);

    Optional<StravaToken> findByAthleteId(Long athleteId);
}
