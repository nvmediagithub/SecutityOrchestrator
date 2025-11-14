package org.example.features.llm.domain.services;

import org.example.features.llm.presentation.dto.llm.LLMConfigResponse;
import org.example.features.llm.presentation.dto.llm.LLMProviderSettings;
import org.example.features.llm.presentation.dto.llm.LLMModelConfig;

import java.util.List;
import java.util.Map;

/**
 * Domain service interface for LLM configuration operations - Port
 */
public interface LLMConfigurationService {

    /**
     * Get current LLM configuration
     * @return Current configuration response
     */
    LLMConfigResponse getConfiguration();

    /**
     * Update LLM configuration
     * @param configUpdates Map of configuration updates
     * @return Updated configuration response
     */
    LLMConfigResponse updateConfiguration(Map<String, Object> configUpdates);

    /**
     * Get provider settings for a specific provider
     * @param provider The provider name
     * @return Provider settings
     */
    LLMProviderSettings getProviderSettings(String provider);

    /**
     * Update provider settings
     * @param provider The provider name
     * @param settings The new settings
     * @return Updated provider settings
     */
    LLMProviderSettings updateProviderSettings(String provider, LLMProviderSettings settings);

    /**
     * Get model configuration
     * @param modelName The model name
     * @return Model configuration
     */
    LLMModelConfig getModelConfig(String modelName);

    /**
     * Update model configuration
     * @param modelName The model name
     * @param config The new configuration
     * @return Updated model configuration
     */
    LLMModelConfig updateModelConfig(String modelName, LLMModelConfig config);

    /**
     * Get available providers
     * @return List of available provider names
     */
    List<String> getAvailableProviders();

    /**
     * Test LLM configuration
     * @param provider The provider to test
     * @return Test result
     */
    String testConfiguration(String provider);

    /**
     * Reset configuration to defaults
     * @return Default configuration response
     */
    LLMConfigResponse resetToDefaults();
}