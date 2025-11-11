package org.example.infrastructure.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class LocalLLMService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:11434";

    public String generateCompletion(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                "model", "codellama:7b-instruct-q4_0",
                "prompt", prompt,
                "stream", false,
                "options", Map.of(
                    "temperature", 0.7,
                    "num_predict", 4096
                )
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/generate",
                request,
                String.class
            );

            return response.getBody();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String analyzeSecurity(String code) {
        String prompt = "You are a security expert. Analyze this code for vulnerabilities:\n" + code;
        return generateCompletion(prompt);
    }
}
