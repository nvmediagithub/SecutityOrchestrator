package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

/**
 * Minimal LLM Application - Pure LLM integration without JPA
 * Focuses only on Ollama + OpenRouter LLM functionality
 */
@SpringBootApplication
@RestController
public class MinimalLLMApplication {

    public static void main(String[] args) {
        System.setProperty("server.port", "8090");
        SpringApplication.run(MinimalLLMApplication.class, args);
    }

    @GetMapping("/api/health")
    public String health() {
        return "✅ SecurityOrchestrator LLM Service is running on port 8090!";
    }

    @GetMapping("/api/llm/test")
    public String llmTest() {
        return "✅ LLM Test Endpoint - SecurityOrchestrator is ready!";
    }

    @GetMapping("/api/llm/status")
    public Map<String, Object> llmStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ready");
        response.put("message", "LLM Service is operational");
        response.put("port", 8090);
        response.put("integration", "Ollama + OpenRouter");
        return response;
    }

    @GetMapping("/api/ollama/test")
    public Map<String, Object> testOllama() {
        Map<String, Object> response = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:11434/api/tags";
            
            // Test Ollama connection
            String ollamaResponse = restTemplate.getForObject(url, String.class);
            
            response.put("ollama_status", "connected");
            response.put("ollama_response", ollamaResponse);
            response.put("status", "success");
            response.put("message", "Ollama is responding");
        } catch (Exception e) {
            response.put("ollama_status", "error");
            response.put("error", e.getMessage());
            response.put("status", "failed");
            response.put("message", "Ollama is not responding - check if Ollama is running");
        }
        return response;
    }
}