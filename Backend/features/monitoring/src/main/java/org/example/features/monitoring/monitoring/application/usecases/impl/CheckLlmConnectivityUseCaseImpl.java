package org.example.features.monitoring.monitoring.application.usecases.impl;

import org.example.features.llm.infrastructure.services.LlmConnectivityProbe;
import org.example.features.llm.infrastructure.services.LlmProviderRegistry;
import org.example.features.monitoring.monitoring.application.dto.LlmConnectivityResponse;
import org.example.features.monitoring.monitoring.application.usecases.CheckLlmConnectivityUseCase;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CheckLlmConnectivityUseCaseImpl implements CheckLlmConnectivityUseCase {

    private final LlmProviderRegistry registry;
    private final LlmConnectivityProbe probe;

    public CheckLlmConnectivityUseCaseImpl(
        LlmProviderRegistry registry,
        LlmConnectivityProbe probe
    ) {
        this.registry = registry;
        this.probe = probe;
    }

    @Override
    public CompletableFuture<LlmConnectivityResponse> execute() {
        return registry.getActiveProvider()
            .map(provider -> {
                LlmConnectivityProbe.ProbeResult result = probe.probe(provider);
                return new LlmConnectivityResponse(
                    provider.id(),
                    provider.displayName(),
                    result.success(),
                    result.statusCode(),
                    result.latencyMs(),
                    result.endpoint(),
                    result.message()
                );
            })
            .map(CompletableFuture::completedFuture)
            .orElseGet(() -> {
                CompletableFuture<LlmConnectivityResponse> failed = new CompletableFuture<>();
                failed.completeExceptionally(new IllegalStateException("No active LLM provider configured"));
                return failed;
            });
    }
}
