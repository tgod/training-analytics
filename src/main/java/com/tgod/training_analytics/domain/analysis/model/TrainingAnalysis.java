package com.tgod.training_analytics.domain.analysis.model;

import java.util.List;

public record TrainingAnalysis(
    String loadLevel,
    List<String> observations,
    String nextWeekRecommendation,
    String watchOut
) {}
