package org.example.features.monitoring.monitoring.application.usecases.impl;

import org.example.features.llm.infrastructure.services.LlmProviderRegistry;
import org.example.features.monitoring.monitoring.application.dto.LlmAnalyticsResponse;
import org.example.features.monitoring.monitoring.application.mappers.LlmAnalyticsMapper;
import org.example.features.monitoring.monitoring.application.usecases.SwitchLlmProviderUseCase;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SwitchLlmProviderUseCaseImpl implements SwitchLlmProviderUseCase {

    private final LlmProviderRegistry registry;
    private final LlmAnalyticsMapper mapper;

    public SwitchLlmProviderUseCaseImpl(LlmProviderRegistry registry, LlmAnalyticsMapper mapper) {
        this.registry = registry;
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<LlmAnalyticsResponse> execute(String providerId) {
        if (!registry.switchTo(providerId)) {
            CompletableFuture<LlmAnalyticsResponse> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(
                new IllegalArgumentException("Unknown LLM provider: " + providerId));
            return failedFuture;
        }
        return CompletableFuture.completedFuture(mapper.map(registry.snapshot()));
    }
}
