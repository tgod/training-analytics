package com.tgod.training_analytics.domain.activities.repository;

import com.tgod.training_analytics.domain.activities.model.AccessToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StravaTokenRepository
        extends CrudRepository<AccessToken, Long> {

    Optional<AccessToken> findByUsername(String username);

    Optional<AccessToken> findByAthleteId(Long athleteId);
}
