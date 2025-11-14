package org.example.features.llm.infrastructure.config;

import org.example.features.llm.application.usecases.ChatCompletionUseCase;
import org.example.features.llm.application.usecases.GetLLMConfigurationUseCase;
import org.example.features.llm.application.usecases.UpdateLLMConfigurationUseCase;
import org.example.features.llm.domain.services.LLMConfigurationService;
import org.example.features.llm.domain.services.LLMModelRepository;
import org.example.features.llm.domain.services.LLMService;
import org.example.features.llm.infrastructure.adapters.LLMConfigurationServiceAdapter;
import org.example.features.llm.infrastructure.adapters.LLMModelRepositoryAdapter;
import org.example.features.llm.infrastructure.adapters.LLMServiceAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the LLM feature module.
 * This class wires together domain services, infrastructure adapters, and application use cases.
 */
@Configuration
@ComponentScan(basePackages = {
    "org.example.features.llm.application",
    "org.example.features.llm.domain",
    "org.example.features.llm.infrastructure.adapters",
    "org.example.features.llm.presentation"
})
public class LLMFeatureConfig {

    /**
     * Configures the LLM service bean.
     * @return LLMService implementation
     */
    @Bean
    public LLMService llmService() {
        return new LLMServiceAdapter();
    }

    /**
     * Configures the LLM configuration service bean.
     * @return LLMConfigurationService implementation
     */
    @Bean
    public LLMConfigurationService llmConfigurationService() {
        return new LLMConfigurationServiceAdapter();
    }

    /**
     * Configures the LLM model repository bean.
     * @return LLMModelRepository implementation
     */
    @Bean
    public LLMModelRepository llmModelRepository() {
        return new LLMModelRepositoryAdapter();
    }

    /**
     * Configures the chat completion use case bean.
     * @param llmService the LLM service dependency
     * @return ChatCompletionUseCase instance
     */
    @Bean
    public ChatCompletionUseCase chatCompletionUseCase(LLMService llmService) {
        return new ChatCompletionUseCase(llmService);
    }

    /**
     * Configures the get LLM configuration use case bean.
     * @param llmConfigurationService the LLM configuration service dependency
     * @return GetLLMConfigurationUseCase instance
     */
    @Bean
    public GetLLMConfigurationUseCase getLLMConfigurationUseCase(LLMConfigurationService llmConfigurationService) {
        return new GetLLMConfigurationUseCase(llmConfigurationService);
    }

    /**
     * Configures the update LLM configuration use case bean.
     * @param llmConfigurationService the LLM configuration service dependency
     * @return UpdateLLMConfigurationUseCase instance
     */
    @Bean
    public UpdateLLMConfigurationUseCase updateLLMConfigurationUseCase(LLMConfigurationService llmConfigurationService) {
        return new UpdateLLMConfigurationUseCase(llmConfigurationService);
    }
}