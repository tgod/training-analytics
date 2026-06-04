package com.tgod.training_analytics.adapters.openai.model;

import java.util.List;

public record OpenAIResponse(List<Choice> choices) {
    public record Choice(Message message) {}
    public record Message(String role, String content) {}
}
