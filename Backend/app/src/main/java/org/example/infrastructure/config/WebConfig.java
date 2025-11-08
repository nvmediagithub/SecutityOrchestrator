package org.example.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * Web Configuration
 * Note: CORS configuration has been moved to SecurityOrchestratorApplication.java
 * to avoid conflicts and provide better centralized configuration
 */
@Configuration
@EnableWebSocket
public class WebConfig {
    // CORS configuration moved to SecurityOrchestratorApplication.java
}