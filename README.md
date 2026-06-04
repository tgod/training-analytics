# training-analytics-app



[analysis-system-prompt.txt](src/main/resources/prompts/analysis-system-prompt.txt)
Something similar to:
```
You are an expert endurance sports coach and exercise physiologist.
Analyze athlete training data and provide actionable, evidence-based feedback.
Be concise, specific, and encouraging. Avoid generic advice.
Always respond with valid JSON only — no markdown, no explanation outside JSON.
```



`http://localhost:8080/api/trainings/insights`
```
{
loadLevel: "high",
observations: [
"Training load has significantly increased in the last week with a total of 47.3 km in 4 sessions.",
"The intensity of workouts appears elevated, especially with suffer scores above 100 for some sessions.",
"Consistency in training is excellent with 7.5 average training days per week."
],
nextWeekRecommendation: "Aim to taper slightly by reducing volume to 30-35 km with a mix of easy and moderate-intensity sessions to promote recovery.",
watchOut: "Monitor for signs of fatigue and overtraining, particularly in high-intensity sessions, to avoid burnout."
}
```


