package org.example.features.openapi_module;

import org.example.features.openapi_module.infrastructure.adapters.OpenAPISpecificationAnalyzer;
import org.example.features.openapi_module.infrastructure.adapters.OpenAPISpecificationParser;
import org.example.features.openapi_module.infrastructure.adapters.OpenAPISpecificationValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the OpenAPI feature.
 * Defines beans for dependency injection following hexagonal architecture.
 */
@Configuration
public class OpenAPIFeatureConfiguration {

    @Bean
    public OpenAPISpecificationParser openAPISpecificationParser() {
        return new OpenAPISpecificationParser();
    }

    @Bean
    public OpenAPISpecificationValidator openAPISpecificationValidator(OpenAPISpecificationParser parser) {
        return new OpenAPISpecificationValidator(parser);
    }

    @Bean
    public OpenAPISpecificationAnalyzer openAPISpecificationAnalyzer(OpenAPISpecificationParser parser) {
        return new OpenAPISpecificationAnalyzer(parser);
    }
}