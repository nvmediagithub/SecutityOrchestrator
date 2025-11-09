package org.example.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "application", "SecurityOrchestrator Backend - Minimal",
            "version", "1.0.0-minimal"
        );
    }

    @GetMapping("/")
    public String root() {
        return "SecurityOrchestrator Backend is running on port 8080";
    }
}