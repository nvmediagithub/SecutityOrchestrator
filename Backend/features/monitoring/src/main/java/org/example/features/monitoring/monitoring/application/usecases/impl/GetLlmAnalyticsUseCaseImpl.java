package org.example.features.monitoring.monitoring.application.usecases.impl;

import org.example.features.llm.infrastructure.services.LlmProviderRegistry;
import org.example.features.monitoring.monitoring.application.dto.LlmAnalyticsResponse;
import org.example.features.monitoring.monitoring.application.mappers.LlmAnalyticsMapper;
import org.example.features.monitoring.monitoring.application.usecases.GetLlmAnalyticsUseCase;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class GetLlmAnalyticsUseCaseImpl implements GetLlmAnalyticsUseCase {

    private final LlmProviderRegistry registry;
    private final LlmAnalyticsMapper mapper;

    public GetLlmAnalyticsUseCaseImpl(LlmProviderRegistry registry, LlmAnalyticsMapper mapper) {
        this.registry = registry;
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<LlmAnalyticsResponse> execute() {
        return CompletableFuture.completedFuture(mapper.map(registry.snapshot()));
    }
}
