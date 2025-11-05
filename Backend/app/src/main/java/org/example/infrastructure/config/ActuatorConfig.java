package org.example.infrastructure.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public HealthIndicator applicationHealthIndicator() {
        return () -> Health.up()
                .withDetail("service", "SecurityOrchestrator")
                .withDetail("status", "All systems operational")
                .build();
    }
}