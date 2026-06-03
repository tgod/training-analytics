plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "7.7.0"
}

group = "com.tgod"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(26)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-liquibase")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
	implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
	implementation("com.mysql:mysql-connector-j:9.5.0")
	implementation("org.openapitools:jackson-databind-nullable:0.2.10")
	implementation("com.squareup.okhttp3:okhttp:5.3.2")
	implementation("com.fasterxml.jackson.core:jackson-databind")

	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter:1.21.3")
	testImplementation("org.testcontainers:mysql:1.21.3")


	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$rootDir/src/main/resources/openapi.yaml")
	outputDir.set("$buildDir/generated")

	apiPackage.set("com.example.api")
	modelPackage.set("com.example.model")

	configOptions.set(
		mapOf(
			"interfaceOnly" to "true",
			"useSpringBoot3" to "true",
			"useBeanValidation" to "true",
			"skipDefaultInterface" to "true",
			"useTags" to "true"
		)
	)
}
sourceSets {
	main {
		java {
			srcDir("$buildDir/generated/src/main/java")
		}
	}
}

tasks.compileJava {
	dependsOn(tasks.openApiGenerate)
}

tasks.clean {
	delete("$buildDir/generated")
}