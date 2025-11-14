package org.example.features.llm.application.usecases;

import org.example.features.llm.presentation.dto.llm.ChatCompletionRequest;
import org.example.features.llm.presentation.dto.llm.ChatCompletionResponse;
import org.example.features.llm.domain.services.LLMService;

import java.util.concurrent.CompletableFuture;

/**
 * Use case for handling chat completions with LLMs
 */
public class ChatCompletionUseCase {

    private final LLMService llmService;

    public ChatCompletionUseCase(LLMService llmService) {
        this.llmService = llmService;
    }

    /**
     * Executes a chat completion request
     * @param request The chat completion request
     * @return A future containing the response
     */
    public CompletableFuture<ChatCompletionResponse> execute(ChatCompletionRequest request) {
        // Validate request
        if (request == null) {
            throw new IllegalArgumentException("Chat completion request cannot be null");
        }

        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            throw new IllegalArgumentException("Chat completion request must contain at least one message");
        }

        // Validate LLM service availability
        if (!llmService.isAvailable()) {
            throw new IllegalStateException("LLM service is not available");
        }

        // Execute the chat completion
        return llmService.chatCompletion(request);
    }
}