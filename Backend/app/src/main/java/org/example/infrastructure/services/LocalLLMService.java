package org.example.infrastructure.services;

import org.example.domain.dto.llm.LocalModelInfo;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Service for managing local LLM models using Ollama
 * Provides functionality for loading, unloading, and managing local models
 */
@Service
public class LocalLLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalLLMService.class);
    
    private final LLMConfig config;
    private final RestTemplate restTemplate;
    private final Executor taskExecutor;
    
    // Track loaded models
    private final Map<String, LocalModelInfo> loadedModels = new ConcurrentHashMap<>();
    private final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();
    
    private volatile String currentBaseUrl;
    private volatile int timeoutSeconds;
    
    public LocalLLMService(LLMConfig config, ThreadPoolTaskExecutor taskExecutor) {
        this.config = config;
        this.restTemplate = new RestTemplate();
        this.taskExecutor = taskExecutor;
        this.currentBaseUrl = config.getLocalServerUrl();
        this.timeoutSeconds = config.getLocalTimeout();
    }
    
    // Exception classes
    public static class LocalLLMException extends Exception {
        public LocalLLMException(String message) { super(message); }
        public LocalLLMException(String message, Throwable cause) { super(message, cause); }
    }
    
    public static class LocalLLMConfigurationException extends LocalLLMException {
        public LocalLLMConfigurationException(String message) { super(message); }
    }
    
    public static class LocalLLMNotRunningException extends LocalLLMException {
        public LocalLLMNotRunningException(String message) { super(message); }
    }
    
    // Configuration methods
    public void updateBaseUrl(String baseUrl) {
        this.currentBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        logger.info("Local LLM base URL updated to: {}", this.currentBaseUrl);
    }
    
    public void updateTimeout(int timeout) {
        this.timeoutSeconds = timeout;
        logger.info("Local LLM timeout updated to: {} seconds", timeout);
    }
    
    // Core methods
    @Async
    public CompletableFuture<List<LocalModelInfo>> listLocalModels() throws LocalLLMException {
        if (!isOllamaRunning()) {
            throw new LocalLLMNotRunningException("Ollama server is not running. Please start Ollama first.");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Ollama API endpoint for listing models
            Map<String, String> request = new HashMap<>();
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            String url = currentBaseUrl + "/api/tags";
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> models = (List<Map<String, Object>>) responseBody.get("models");
                
                List<LocalModelInfo> modelInfos = new ArrayList<>();
                if (models != null) {
                    for (Map<String, Object> model : models) {
                        String modelName = (String) model.get("name");
                        if (modelName != null) {
                            // Extract model size and details
                            String modelNameOnly = extractModelName(modelName);
                            Double sizeGB = extractModelSize(model);
                            
                            boolean isLoaded = loadedModels.containsKey(modelNameOnly);
                            LocalDateTime lastUsed = isLoaded ? LocalDateTime.now() : null;
                            
                            LocalModelInfo modelInfo = new LocalModelInfo(
                                modelNameOnly,
                                sizeGB,
                                isLoaded,
                                getContextWindow(modelNameOnly),
                                getMaxTokens(modelNameOnly),
                                lastUsed
                            );
                            modelInfos.add(modelInfo);
                        }
                    }
                }
                
                return CompletableFuture.completedFuture(modelInfos);
            } else {
                throw new LocalLLMException("Failed to list models: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new LocalLLMException(extractErrorMessage(e), e);
        } catch (ResourceAccessException e) {
            throw new LocalLLMException("Network error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new LocalLLMException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    @Async
    public CompletableFuture<Boolean> checkOllamaConnection() throws LocalLLMException {
        try {
            // Try to get model list to test connection
            listLocalModels().get(10, java.util.concurrent.TimeUnit.SECONDS);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new LocalLLMException("Ollama connection check failed: " + e.getMessage(), e);
        }
    }
    
    @Async
    public CompletableFuture<Boolean> loadModel(String modelName) throws LocalLLMException {
        if (!isOllamaRunning()) {
            throw new LocalLLMNotRunningException("Ollama server is not running");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> request = new HashMap<>();
            request.put("name", modelName);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            String url = currentBaseUrl + "/api/pull";
            
            // Note: Ollama's pull API is async, we just make the request
            restTemplate.postForEntity(url, entity, String.class);
            
            // Mark model as loaded (in real implementation, you'd wait for completion)
            LocalModelInfo modelInfo = new LocalModelInfo(
                modelName, 
                getModelSize(modelName), 
                true, 
                getContextWindow(modelName),
                getMaxTokens(modelName),
                LocalDateTime.now()
            );
            loadedModels.put(modelName, modelInfo);
            
            logger.info("Model {} loading initiated", modelName);
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            throw new LocalLLMException("Failed to load model " + modelName + ": " + e.getMessage(), e);
        }
    }
    
    @Async
    public CompletableFuture<Boolean> unloadModel(String modelName) throws LocalLLMException {
        try {
            loadedModels.remove(modelName);
            logger.info("Model {} unloaded", modelName);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new LocalLLMException("Failed to unload model " + modelName + ": " + e.getMessage(), e);
        }
    }
    
    @Async
    public CompletableFuture<ChatCompletionResponse> localChatCompletion(String model, String prompt,
                                                                       int maxTokens, double temperature) 
            throws LocalLLMException {
        if (!isOllamaRunning()) {
            throw new LocalLLMNotRunningException("Ollama server is not running");
        }
        
        try {
            // Create Ollama chat completion request
            Map<String, Object> request = new HashMap<>();
            request.put("model", model);
            request.put("prompt", prompt);
            request.put("stream", false);
            request.put("options", Map.of(
                "temperature", temperature,
                "num_predict", maxTokens
            ));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            String url = currentBaseUrl + "/api/generate";
            
            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class);
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                String responseText = (String) responseBody.get("response");
                Integer tokensUsed = (Integer) responseBody.get("eval_count");
                
                ChatCompletionResponse completionResponse = new ChatCompletionResponse();
                completionResponse.setResponse(responseText);
                completionResponse.setTokensUsed(tokensUsed);
                completionResponse.setResponseTimeMs((double) responseTime);
                completionResponse.setCost(0.0); // Local models are free
                
                // Update last used time
                LocalModelInfo modelInfo = loadedModels.get(model);
                if (modelInfo != null) {
                    modelInfo.setLastUsed(LocalDateTime.now());
                }
                
                return CompletableFuture.completedFuture(completionResponse);
            } else {
                throw new LocalLLMException("Local chat completion failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new LocalLLMException("Local chat completion error: " + e.getMessage(), e);
        }
    }
    
    // Helper methods
    private boolean isOllamaRunning() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> request = new HashMap<>();
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            restTemplate.postForEntity(currentBaseUrl + "/api/tags", entity, String.class);
            return true;
        } catch (Exception e) {
            logger.warn("Ollama server is not responding: {}", e.getMessage());
            return false;
        }
    }
    
    private String extractModelName(String fullModelName) {
        // Extract just the model name part before any tags
        int colonIndex = fullModelName.lastIndexOf(':');
        if (colonIndex > 0) {
            return fullModelName.substring(0, colonIndex);
        }
        return fullModelName;
    }
    
    private Double extractModelSize(Map<String, Object> model) {
        try {
            // Ollama returns size in bytes
            Object sizeObj = model.get("size");
            if (sizeObj instanceof Number) {
                return ((Number) sizeObj).doubleValue() / (1024.0 * 1024.0 * 1024.0); // Convert to GB
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private Double getModelSize(String modelName) {
        // Estimate model size based on name
        if (modelName.toLowerCase().contains("7b")) {
            return 4.0; // ~4GB
        } else if (modelName.toLowerCase().contains("13b")) {
            return 8.0; // ~8GB
        } else if (modelName.toLowerCase().contains("70b")) {
            return 40.0; // ~40GB
        }
        return 3.0; // Default estimate
    }
    
    private Integer getContextWindow(String modelName) {
        // Estimate context window based on model name
        if (modelName.toLowerCase().contains("8k")) {
            return 8000;
        } else if (modelName.toLowerCase().contains("16k")) {
            return 16000;
        } else if (modelName.toLowerCase().contains("32k")) {
            return 32000;
        }
        return 4096; // Default
    }
    
    private Integer getMaxTokens(String modelName) {
        // Estimate max tokens based on context window
        return getContextWindow(modelName) - 1000; // Reserve 1000 tokens for context
    }
    
    private String extractErrorMessage(HttpClientErrorException e) {
        try {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isEmpty()) {
                return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody;
            }
        } catch (Exception ex) {
            logger.warn("Failed to parse error response", ex);
        }
        return "Local LLM request failed with status " + e.getStatusCode() + " - " + e.getMessage();
    }
    
    // Chat completion response class
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