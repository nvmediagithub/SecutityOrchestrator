package org.example.features.llm.domain.services;

import org.example.features.llm.domain.dto.LLMConfigResponse;

import java.util.Map;

/**
 * Domain service for LLM configuration operations
 */
public interface LLMConfigurationService {

    /**
     * Get the current LLM configuration
     * @return The current configuration
     */
    LLMConfigResponse getConfiguration();

    /**
     * Update the LLM configuration
     * @param configUpdates The configuration updates
     * @return The updated configuration
     */
    LLMConfigResponse updateConfiguration(Map<String, Object> configUpdates);

    /**
     * Validate configuration updates
     * @param configUpdates The updates to validate
     * @return true if valid, false otherwise
     */
    boolean validateConfiguration(Map<String, Object> configUpdates);

    /**
     * Reset configuration to defaults
     * @return The default configuration
     */
    LLMConfigResponse resetToDefaults();
}