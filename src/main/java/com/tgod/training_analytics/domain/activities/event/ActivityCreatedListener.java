package com.tgod.training_analytics.domain.activities.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(ActivityCreatedListener.class);

    @EventListener
    public void onActivityCreated(ActivityCreated event) {
        log.info("New activity created: username={}, stravaId={}, name={}",
                event.username(), event.stravaId(), event.name());
    }
}
