package com.tgod.training_analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class TrainingAnalyticsApplicationTests {


	@Container
	@ServiceConnection
	static MySQLContainer<?> mysql =
			new MySQLContainer<>("mysql:8.4");

	@Autowired
	TrainingAnalyticsApplication app;

	@Test
	void contextLoads() {
		// test
	}
}