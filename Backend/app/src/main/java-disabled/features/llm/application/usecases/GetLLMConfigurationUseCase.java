package org.example.features.llm.application.usecases;

import org.example.features.llm.presentation.dto.llm.LLMConfigResponse;
import org.example.features.llm.domain.services.LLMConfigurationService;

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
        return configurationService.getConfiguration();
    }
}