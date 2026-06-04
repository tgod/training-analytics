package com.tgod.training_analytics.domain.activities.repository;

import com.tgod.training_analytics.domain.activities.model.ActivityEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends CrudRepository<ActivityEntity, Long> {

    Optional<ActivityEntity> findByStravaId(Long stravaId);

    Optional<ActivityEntity> findTopByUsernameOrderByStartDateDesc(String username);

    List<ActivityEntity> findAllByUsername(String username);
}
