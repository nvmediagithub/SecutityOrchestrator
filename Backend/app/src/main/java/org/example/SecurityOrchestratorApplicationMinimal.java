package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.infrastructure.config.*;
import org.example.infrastructure.services.*;
import org.example.web.controllers.*;

/**
 * Минимальная версия SecurityOrchestrator для тестирования LLM интеграции
 * Включает полную LLM функциональность с Ollama + OpenRouter поддержкой
 * Полностью избегает JPA entity и repository для быстрого запуска
 */
@SpringBootApplication
@RestController
@EntityScan(basePackages = {}) // Disable entity scanning completely
@EnableJpaRepositories(basePackages = {}) // Disable JPA repository scanning
@ComponentScan(
    basePackages = {
        "org.example.config",
        "org.example.infrastructure.services",
        "org.example.web.controllers",
        "org.example.domain.dto"
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "org/example/infrastructure/repositories/.*"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "org/example/domain/entities/.*"
        ),
        @ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            pattern = "org/springframework/data/jpa/repository/.*"
        ),
        @ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            pattern = "jakarta/persistence/.*"
        )
    }
)
public class SecurityOrchestratorApplicationMinimal {

    public static void main(String[] args) {
        System.setProperty("server.port", "8090"); // Set port to 8090 to avoid conflicts
        SpringApplication.run(SecurityOrchestratorApplicationMinimal.class, args);
    }

    @GetMapping("/api/health")
    public String health() {
        return "SecurityOrchestrator LLM Service is running on port 8090!";
    }

    @GetMapping("/api/llm/status")
    public String llmStatus() {
        return "LLM Integration Test Endpoint - Ready for Ollama testing!";
    }

    @GetMapping("/api/llm/test")
    public String llmTest() {
        return "LLM Test Endpoint - SecurityOrchestrator is ready!";
    }
}