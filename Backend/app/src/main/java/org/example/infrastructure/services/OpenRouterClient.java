package org.example.infrastructure.services;

import org.example.domain.entities.LLMProvider;
import org.example.infrastructure.config.LLMConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * OpenRouter API client for managing LLM providers and models
 * Replicates the functionality of the Python OpenRouterClient
 */
@Service
public class OpenRouterClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenRouterClient.class);
    private final LLMConfig config;
    private final RestTemplate restTemplate;
    private final Executor taskExecutor;
    
    private volatile String currentApiKey;
    private volatile String currentBaseUrl;
    private volatile int timeoutSeconds;
    
    public OpenRouterClient(LLMConfig config, ThreadPoolTaskExecutor taskExecutor) {
        this.config = config;
        this.restTemplate = new RestTemplate();
        this.taskExecutor = taskExecutor;
        this.currentApiKey = config.getOpenRouterApiKey();
        this.currentBaseUrl = config.getOpenRouterBaseUrl();
        this.timeoutSeconds = config.getOpenRouterTimeout();
    }
    
    // Exception classes
    public static class OpenRouterException extends Exception {
        public OpenRouterException(String message) { super(message); }
        public OpenRouterException(String message, Throwable cause) { super(message, cause); }
    }
    
    public static class OpenRouterConfigurationException extends OpenRouterException {
        public OpenRouterConfigurationException(String message) { super(message); }
    }
    
    public static class OpenRouterAuthenticationException extends OpenRouterException {
        public OpenRouterAuthenticationException(String message) { super(message); }
    }
    
    // API key management
    public void updateApiKey(String apiKey) {
        this.currentApiKey = apiKey;
        logger.info("OpenRouter API key updated");
    }
    
    public boolean hasApiKey() {
        return currentApiKey != null && !currentApiKey.trim().isEmpty();
    }
    
    public void updateBaseUrl(String baseUrl) {
        this.currentBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        logger.info("OpenRouter base URL updated to: {}", this.currentBaseUrl);
    }
    
    public void updateTimeout(int timeout) {
        this.timeoutSeconds = timeout;
        logger.info("OpenRouter timeout updated to: {} seconds", timeout);
    }
    
    // Core API methods
    @Async
    public CompletableFuture<List<String>> listModels() throws OpenRouterException {
        if (!hasApiKey()) {
            throw new OpenRouterConfigurationException("OpenRouter API key is not configured");
        }
        
        try {
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            String url = currentBaseUrl + "/models";
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");
                
                List<String> modelNames = new ArrayList<>();
                for (Map<String, Object> model : data) {
                    String modelName = (String) model.get("id");
                    if (modelName == null) {
                        modelName = (String) model.get("name");
                    }
                    if (modelName != null) {
                        modelNames.add(modelName);
                    }
                }
                
                return CompletableFuture.completedFuture(modelNames);
            } else {
                throw new OpenRouterException("Failed to fetch models: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new OpenRouterAuthenticationException("OpenRouter rejected the provided API key");
            }
            throw new OpenRouterException(extractErrorMessage(e), e);
        } catch (ResourceAccessException e) {
            throw new OpenRouterException("Network error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new OpenRouterException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    @Async
    public CompletableFuture<Boolean> checkConnection() throws OpenRouterException {
        try {
            listModels().get(10, java.util.concurrent.TimeUnit.SECONDS); // 10 second timeout
        } catch (Exception e) {
            throw new OpenRouterException("Connection check failed: " + e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(true);
    }
    
    @Async
    public CompletableFuture<ChatCompletionResponse> chatCompletion(String model, String prompt,
                                                                   int maxTokens, double temperature) 
            throws OpenRouterException {
        if (!hasApiKey()) {
            throw new OpenRouterConfigurationException("OpenRouter API key is not configured");
        }
        
        try {
            ChatCompletionRequest request = new ChatCompletionRequest(model, prompt, maxTokens, temperature);
            
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
            
            String url = currentBaseUrl + "/chat/completions";
            long startTime = System.currentTimeMillis();
            
            ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, ChatCompletionResponse.class);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ChatCompletionResponse completionResponse = response.getBody();
                completionResponse.setResponseTimeMs((double)responseTime);
                return CompletableFuture.completedFuture(completionResponse);
            } else {
                throw new OpenRouterException("Chat completion failed: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new OpenRouterAuthenticationException("OpenRouter rejected the provided API key");
            }
            throw new OpenRouterException(extractErrorMessage(e), e);
        } catch (ResourceAccessException e) {
            throw new OpenRouterException("Network error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new OpenRouterException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    // Helper methods
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + currentApiKey);
        headers.set("Content-Type", "application/json");
        
        if (config.getOpenRouterReferer() != null) {
            headers.set("HTTP-Referer", config.getOpenRouterReferer());
            headers.set("Referer", config.getOpenRouterReferer());
        }
        
        if (config.getOpenRouterAppName() != null) {
            headers.set("X-Title", config.getOpenRouterAppName());
        }
        
        return headers;
    }
    
    private String extractErrorMessage(HttpClientErrorException e) {
        try {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isEmpty()) {
                // Try to parse JSON error response manually
                if (responseBody.contains("\"error\"")) {
                    // Simple JSON parsing for error message
                    int errorStart = responseBody.indexOf("\"error\"");
                    if (errorStart != -1) {
                        int messageStart = responseBody.indexOf("\"message\"", errorStart);
                        if (messageStart != -1) {
                            int colonIndex = responseBody.indexOf(":", messageStart);
                            int quoteStart = responseBody.indexOf("\"", colonIndex);
                            int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);
                            if (quoteStart != -1 && quoteEnd != -1 && quoteEnd > quoteStart + 1) {
                                return responseBody.substring(quoteStart + 1, quoteEnd);
                            }
                        }
                    }
                }
                
                // If not JSON or parsing failed, return the raw response
                return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody;
            }
        } catch (Exception ex) {
            logger.warn("Failed to parse error response", ex);
        }
        
        return "OpenRouter request failed with status " + e.getStatusCode() + " - " + e.getMessage();
    }
    
    // Request/Response classes
    public static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private int max_tokens;
        private double temperature;
        
        public ChatCompletionRequest(String model, String prompt, int maxTokens, double temperature) {
            this.model = model;
            this.max_tokens = maxTokens;
            this.temperature = temperature;
            this.messages = Arrays.asList(new Message("user", prompt));
        }
        
        // Getters and setters
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
        
        public int getMax_tokens() { return max_tokens; }
        public void setMax_tokens(int max_tokens) { this.max_tokens = max_tokens; }
        
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
    }
    
    public static class Message {
        private String role;
        private String content;
        
        public Message() {}
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class ChatCompletionResponse {
        private String response;
        private Integer tokensUsed;
        private Double cost;
        private Double responseTimeMs;
        
        public ChatCompletionResponse() {}
        
        public ChatCompletionResponse(String response, Integer tokensUsed, Double cost) {
            this.response = response;
            this.tokensUsed = tokensUsed;
            this.cost = cost;
        }
        
        // Getters and setters
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
        
        public Integer getTokensUsed() { return tokensUsed; }
        public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
        
        public Double getCost() { return cost; }
        public void setCost(Double cost) { this.cost = cost; }
        
        public Double getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(Double responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    }
}