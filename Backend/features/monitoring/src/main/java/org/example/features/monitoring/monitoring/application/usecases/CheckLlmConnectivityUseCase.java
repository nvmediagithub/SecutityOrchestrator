package org.example.features.monitoring.monitoring.application.usecases;

import org.example.features.monitoring.monitoring.application.dto.LlmConnectivityResponse;

import java.util.concurrent.CompletableFuture;

public interface CheckLlmConnectivityUseCase {
    CompletableFuture<LlmConnectivityResponse> execute();
}
