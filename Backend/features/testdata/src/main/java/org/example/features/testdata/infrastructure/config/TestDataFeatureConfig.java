package org.example.features.testdata.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the TestData feature module.
 * This class wires together domain services, infrastructure adapters, and application use cases.
 */
@Configuration
@ComponentScan(basePackages = {
    "org.example.features.testdata.application",
    "org.example.features.testdata.domain",
    "org.example.features.testdata.infrastructure",
    "org.example.features.testdata.presentation"
})
public class TestDataFeatureConfig {

    // Bean definitions will be added here as testdata components are implemented
    // For now, component scanning will handle automatic registration of @Component, @Service, @Repository classes

}