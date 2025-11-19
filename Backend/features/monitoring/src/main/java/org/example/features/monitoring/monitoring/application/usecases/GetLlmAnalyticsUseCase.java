package org.example.features.monitoring.monitoring.application.usecases;

import org.example.features.monitoring.monitoring.application.dto.LlmAnalyticsResponse;

import java.util.concurrent.CompletableFuture;

public interface GetLlmAnalyticsUseCase {
    CompletableFuture<LlmAnalyticsResponse> execute();
}
