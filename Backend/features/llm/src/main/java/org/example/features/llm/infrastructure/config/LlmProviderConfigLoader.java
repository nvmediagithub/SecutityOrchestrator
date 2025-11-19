package org.example.features.llm.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Loads LLM provider configuration from a YAML resource.
 */
@Component
public class LlmProviderConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmProviderConfigLoader.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper yamlMapper;
    private final String configLocation;

    public LlmProviderConfigLoader(
        ResourceLoader resourceLoader,
        @Value("${llm.config-file:classpath:config/llm-providers.yml}") String configLocation
    ) {
        this.resourceLoader = resourceLoader;
        this.configLocation = configLocation;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.findAndRegisterModules();
    }

    public LlmProviderConfigurationProperties loadConfiguration() {
        Resource resource = resourceLoader.getResource(configLocation);
        try {
            LlmProviderConfigurationProperties properties =
                yamlMapper.readValue(resource.getInputStream(), LlmProviderConfigurationProperties.class);
            if (properties.getProviders() == null || properties.getProviders().isEmpty()) {
                throw new IllegalStateException("LLM provider configuration must declare at least one provider");
            }
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load LLM provider configuration from " + configLocation, e);
        }
    }
}
