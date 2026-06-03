package com.tgod.training_analytics.adapters.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.tgod.training_analytics")
@EntityScan("com.tgod.training_analytics")
@EnableJpaRepositories("com.tgod.training_analytics")
public class TrainingAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingAnalyticsApplication.class, args);
    }

}
