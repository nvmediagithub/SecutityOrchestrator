package org.example.features.monitoring.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the Monitoring feature module.
 * This class wires together domain services, infrastructure adapters, and application use cases.
 */
@Configuration
@ComponentScan(basePackages = {
    "org.example.features.monitoring.application",
    "org.example.features.monitoring.domain",
    "org.example.features.monitoring.infrastructure",
    "org.example.features.monitoring.presentation"
})
public class MonitoringFeatureConfig {

    // Bean definitions will be added here as monitoring components are implemented
    // For now, component scanning will handle automatic registration of @Component, @Service, @Repository classes

}