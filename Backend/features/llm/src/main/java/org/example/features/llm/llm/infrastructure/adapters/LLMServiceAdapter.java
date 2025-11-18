package org.example.features.llm.infrastructure.adapters;

import org.example.features.llm.presentation.dto.llm.ChatCompletionRequest;
import org.example.features.llm.presentation.dto.llm.ChatCompletionResponse;
import org.example.features.llm.domain.services.LLMService;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Infrastructure adapter implementing the LLMService domain port
 * Adapts infrastructure services to domain interfaces
 */
@Component
public class LLMServiceAdapter implements LLMService {

    // TODO: Inject actual LLM infrastructure services here
    // private final OpenRouterService openRouterService;
    // private final LocalLLMService localLLMService;

    public LLMServiceAdapter() {
        // TODO: Initialize with actual services
    }

    @Override
    public CompletableFuture<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request) {
        // TODO: Implement actual LLM service call
        // This would delegate to the appropriate infrastructure service
        // based on the model/provider specified in the request

        throw new UnsupportedOperationException("Chat completion not yet implemented");
    }

    @Override
    public boolean isAvailable() {
        // TODO: Check if any LLM service is available
        return false;
    }

    @Override
    public String getCurrentModel() {
        // TODO: Get the currently active model
        return "unknown";
    }

    @Override
    public String testConnection() {
        // TODO: Test connection to LLM services
        return "Connection test not implemented";
    }
}