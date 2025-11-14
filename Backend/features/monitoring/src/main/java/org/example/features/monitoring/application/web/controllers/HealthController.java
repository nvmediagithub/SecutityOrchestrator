package org.example.features.monitoring.application.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * REST Controller for health monitoring
 * Provides health check endpoints for system monitoring
 */
@RestController
public class HealthController {

    /**
     * Health check endpoint
     * GET /api/health
     */
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "application", "SecurityOrchestrator Backend - Minimal",
            "version", "1.0.0-minimal"
        );
    }

    /**
     * Root endpoint
     * GET /
     */
    @GetMapping("/")
    public String root() {
        return "SecurityOrchestrator Backend is running on port 8080";
    }
}