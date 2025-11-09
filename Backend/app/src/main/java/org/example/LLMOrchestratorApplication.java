package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.infrastructure.config.*;
import org.example.infrastructure.services.*;
import org.example.web.controllers.*;

/**
 * Dedicated LLM Orchestrator Application
 * Focuses only on LLM integration without JPA dependencies
 * Runs independently on port 8090
 */
@SpringBootApplication
@RestController
@ComponentScan(
    basePackages = {
        "org.example.config",
        "org.example.infrastructure.services", 
        "org.example.web.controllers",
        "org.example.domain.dto",
        "org.example.domain.entities"
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "org/example/infrastructure/repositories/.*"
        ),
        @ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            pattern = "org/springframework/data/jpa/repository/.*"
        )
    }
)
public class LLMOrchestratorApplication {

    public static void main(String[] args) {
        System.setProperty("server.port", "8090");
        SpringApplication.run(LLMOrchestratorApplication.class, args);
    }

    @GetMapping("/api/health")
    public String health() {
        return "LLM Orchestrator Service is running on port 8090!";
    }

    @GetMapping("/api/llm/test")
    public String llmTest() {
        return "LLM Test Endpoint - Ready for Ollama integration!";
    }
}