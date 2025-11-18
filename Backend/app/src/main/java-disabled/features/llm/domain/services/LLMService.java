package org.example.features.llm.domain.services;

import org.example.features.llm.presentation.dto.llm.ChatCompletionRequest;
import org.example.features.llm.presentation.dto.llm.ChatCompletionResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Domain service interface for LLM operations - Port
 */
public interface LLMService {

    /**
     * Send a chat completion request to the LLM
     * @param request The chat completion request
     * @return A future containing the chat completion response
     */
    CompletableFuture<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request);

    /**
     * Check if the LLM service is available
     * @return true if the service is available, false otherwise
     */
    boolean isAvailable();

    /**
     * Get the name of the current model
     * @return The model name
     */
    String getCurrentModel();

    /**
     * Test the LLM connection
     * @return A test response indicating connection status
     */
    String testConnection();
}