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

        // Convert presentation DTO to domain DTO
        var domainRequest = convertToDomainRequest(request);

        // Execute the chat completion
        return llmService.chatCompletion(domainRequest)
            .thenApply(this::convertToPresentationResponse);
    }

    private org.example.features.llm.domain.dto.ChatCompletionRequest convertToDomainRequest(ChatCompletionRequest request) {
        var domainRequest = new org.example.features.llm.domain.dto.ChatCompletionRequest();
        domainRequest.setModel(request.getModel());
        domainRequest.setTemperature(request.getTemperature());
        domainRequest.setMaxTokens(request.getMaxTokens());
        domainRequest.setTopP(request.getTopP());
        domainRequest.setFrequencyPenalty(request.getFrequencyPenalty() != null ? request.getFrequencyPenalty().intValue() : null);
        domainRequest.setPresencePenalty(request.getPresencePenalty() != null ? request.getPresencePenalty().intValue() : null);
        domainRequest.setStop(request.getStop());
        domainRequest.setUser(request.getUser());

        // Convert messages
        if (request.getMessages() != null) {
            var domainMessages = request.getMessages().stream()
                .map(msg -> new org.example.features.llm.domain.dto.ChatCompletionRequest.Message(msg.getRole(), msg.getContent()))
                .toList();
            domainRequest.setMessages(domainMessages);
        }

        return domainRequest;
    }

    private ChatCompletionResponse convertToPresentationResponse(org.example.features.llm.domain.dto.ChatCompletionResponse domainResponse) {
        var response = new ChatCompletionResponse();
        response.setId(domainResponse.getId());
        response.setObject(domainResponse.getObject());
        response.setCreated(domainResponse.getCreated());
        response.setModel(domainResponse.getModel());

        // Convert choices
        if (domainResponse.getChoices() != null) {
            var choices = domainResponse.getChoices().stream()
                .map(choice -> {
                    var c = new ChatCompletionResponse.Choice();
                    c.setIndex(choice.getIndex());
                    c.setFinishReason(choice.getFinishReason());
                    if (choice.getMessage() != null) {
                        var msg = new org.example.features.llm.presentation.dto.llm.ChatCompletionRequest.Message(choice.getMessage().getRole(), choice.getMessage().getContent());
                        c.setMessage(msg);
                    }
                    return c;
                })
                .toList();
            response.setChoices(choices);
        }

        return response;
    }
}