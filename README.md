# Training Load Analyst

Fetches the last N weeks of Strava activities, sends structured data (distance, elevation, heart rate, pace trends) to an LLM, and returns a human-friendly overview of the training load assessment and next-week recommendations.

## How it works

1. User authenticates via Strava OAuth2.
2. The app pulls recent activities from the Strava API and aggregates training metrics.
3. The structured summary is sent to OpenAI (GPT-4o-mini by default).
4. The LLM responds with a load level, observations, a next-week recommendation, and a watch-out note.

**Example response from `GET /api/trainings/insights`:**
```json
{
  "loadLevel": "high",
  "observations": [
    "Training load has significantly increased in the last week with a total of 47.3 km in 4 sessions.",
    "The intensity of workouts appears elevated, especially with suffer scores above 100 for some sessions.",
    "Consistency in training is excellent with 7.5 average training days per week."
  ],
  "nextWeekRecommendation": "Aim to taper slightly by reducing volume to 30-35 km with a mix of easy and moderate-intensity sessions to promote recovery.",
  "watchOut": "Monitor for signs of fatigue and overtraining, particularly in high-intensity sessions, to avoid burnout."
}
```

## Tech stack

|          | Technology |
|----------|---|
| Language | Java 26 |
| Framework | Spring Boot 4.0.6 |
| Database | MySQL 8.0 |
| Migrations | Liquibase |
| API design | Schema-first (OpenAPI 3 → code generation) |
| API docs | Springdoc / Swagger UI |
| LLM      | OpenAI API |
| Tests    | JUnit 5, Testcontainers, JaCoCo |

## Prerequisites

- Java 26+
- Docker (for the database)
- A [Strava API application](https://www.strava.com/settings/api) (client ID + secret)
- An [OpenAI API key](https://platform.openai.com/api-keys)

## Configuration

API keys are read from environment variables. Set them before starting the app:

```bash
export OPENAI_API_KEY=sk-...
export STRAVA_CLIENT_SECRET=your-strava-secret
```

Configuration: [`src/main/resources/application.yml`](src/main/resources/application.yml).

## Running locally

**1. Start the database:**
```bash
docker-compose up -d
```

**2. Build and run:**
```bash
./gradlew bootRun
```

The app starts at `http://localhost:8080`.

**Swagger UI:** `http://localhost:8080/api/swagger-ui.html`

## API

The API contract is defined in [`src/main/resources/openapi.yaml`](src/main/resources/openapi.yaml). Spring controller interfaces are generated from it at build time via the OpenAPI Generator Gradle plugin.

| Endpoint                       | Description                                         |
|--------------------------------|-----------------------------------------------------|
| `GET /api/strava/login`        | Login to strava and store the token                 |
| `GET /api/strava/activities`   | Fetch the latest activities from Strava.            |
| `GET /api/trainings/insights`  | Get insights about the activities in the last weeks |


## Database migrations

Schema changes are managed with Liquibase. Migration files is here: `src/main/resources/db/changelog/changes/`.

## Tests

```bash
./gradlew test
```

Tests use Testcontainers to spin up a MySQL instance. JaCoCo coverage reports are written to `build/reports/jacoco/`.

## LLM system prompt

The LLM system prompt can be found here:  [`src/main/resources/prompts/analysis-system-prompt.txt`](src/main/resources/prompts/analysis-system-prompt.txt).

The user prompt is generated in [`src/main/java/com/tgod/training_analytics/domain/openai/PromptBuilderService.java`](src/main/java/com/tgod/training_analytics/domain/openai/PromptBuilderService.java)
