package com.tgod.training_analytics.domain.activities.repository;

import com.tgod.training_analytics.domain.activities.model.StravaToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StravaTokenRepository
        extends CrudRepository<StravaToken, Long> {

    Optional<StravaToken> findByUsername(String username);

    Optional<StravaToken> findByAthleteId(Long athleteId);
}
