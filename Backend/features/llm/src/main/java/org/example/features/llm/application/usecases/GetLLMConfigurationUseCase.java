package org.example.features.llm.application.usecases;

import org.example.features.llm.presentation.dto.llm.LLMConfigResponse;
import org.example.features.llm.domain.services.LLMConfigurationService;

import java.util.Map;

import java.util.Map;

/**
 * Use case for retrieving LLM configuration
 */
public class GetLLMConfigurationUseCase {

    private final LLMConfigurationService configurationService;

    public GetLLMConfigurationUseCase(LLMConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Executes the use case to get current LLM configuration
     * @return The current LLM configuration
     */
    public LLMConfigResponse execute() {
        var domainResponse = configurationService.getConfiguration();
        return convertToPresentationResponse(domainResponse);
    }

    private LLMConfigResponse convertToPresentationResponse(org.example.features.llm.domain.dto.LLMConfigResponse domainResponse) {
        var response = new LLMConfigResponse();
        // For now, just set current provider from domain response
        // This would need to be expanded based on actual presentation requirements
        response.setCurrentProvider(domainResponse.getProvider());
        return response;
    }
}