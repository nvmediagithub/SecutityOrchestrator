package org.example.features.llm.domain.services;

import org.example.features.llm.domain.dto.ChatCompletionRequest;
import org.example.features.llm.domain.dto.ChatCompletionResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Domain service for LLM operations
 */
public interface LLMService {

    /**
     * Check if the LLM service is available
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Perform a chat completion
     * @param request The chat completion request
     * @return A future containing the response
     */
    CompletableFuture<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request);

    /**
     * Get the service provider name
     * @return The provider name
     */
    String getProviderName();

    /**
     * Get the service status
     * @return The service status
     */
    String getStatus();

    /**
     * Get supported models
     * @return Array of supported model names
     */
    String[] getSupportedModels();
}