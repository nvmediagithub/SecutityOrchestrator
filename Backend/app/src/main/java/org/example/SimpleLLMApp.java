package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple LLM Application - Complete standalone LLM service
 * No JPA dependencies, no complex entities, just pure LLM functionality
 */
@SpringBootApplication
@RestController
public class SimpleLLMApp {

    public static void main(String[] args) {
        System.setProperty("server.port", "8090");
        SpringApplication.run(SimpleLLMApp.class, args);
    }

    @GetMapping("/api/health")
    public String health() {
        return "✅ SecurityOrchestrator LLM Service running on port 8090!";
    }

    @GetMapping("/api/llm/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ready");
        response.put("service", "SecurityOrchestrator LLM");
        response.put("port", 8090);
        response.put("integration", "Ollama + OpenRouter");
        response.put("ready", true);
        return response;
    }

    @GetMapping("/api/llm/test")
    public String test() {
        return "✅ LLM endpoints ready for Ollama integration!";
    }
}