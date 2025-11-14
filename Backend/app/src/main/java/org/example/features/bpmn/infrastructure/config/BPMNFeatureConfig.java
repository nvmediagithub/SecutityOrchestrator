package org.example.features.bpmn.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for the BPMN feature module.
 * This class wires together domain services, infrastructure adapters, and application use cases.
 */
@Configuration
@ComponentScan(basePackages = {
    "org.example.features.bpmn.application",
    "org.example.features.bpmn.domain",
    "org.example.features.bpmn.infrastructure",
    "org.example.features.bpmn.presentation"
})
public class BPMNFeatureConfig {

    // Bean definitions will be added here as BPMN components are implemented
    // For now, component scanning will handle automatic registration of @Component, @Service, @Repository classes

}