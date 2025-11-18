package org.example.features.llm.application.usecases;

import org.example.features.llm.presentation.dto.llm.LLMConfigResponse;
import org.example.features.llm.domain.services.LLMConfigurationService;

import java.util.Map;

/**
 * Use case for updating LLM configuration
 */
public class UpdateLLMConfigurationUseCase {

    private final LLMConfigurationService configurationService;

    public UpdateLLMConfigurationUseCase(LLMConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Executes the use case to update LLM configuration
     * @param configUpdates The configuration updates to apply
     * @return The updated LLM configuration
     */
    public LLMConfigResponse execute(Map<String, Object> configUpdates) {
        if (configUpdates == null || configUpdates.isEmpty()) {
            throw new IllegalArgumentException("Configuration updates cannot be null or empty");
        }

        // Validate updates
        validateConfigUpdates(configUpdates);

        // Apply the updates
        return configurationService.updateConfiguration(configUpdates);
    }

    /**
     * Validates the configuration updates
     * @param configUpdates The updates to validate
     */
    private void validateConfigUpdates(Map<String, Object> configUpdates) {
        // Add validation logic as needed
        // For example, check for required fields, valid ranges, etc.
    }
}