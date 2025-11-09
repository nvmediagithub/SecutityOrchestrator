package org.example.domain.interfaces;

import org.example.domain.dto.llm.ChatCompletionRequest;
import org.example.domain.dto.llm.ChatCompletionResponse;
import org.example.domain.entities.LLMProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Common interface for all LLM providers
 * Provides standardized methods for LLM operations
 */
public interface ILLMProvider {
    
    /**
     * Get the provider type
     */
    LLMProvider getProviderType();
    
    /**
     * Get provider display name
     */
    String getProviderName();
    
    /**
     * Check if provider is configured and available
     */
    boolean isAvailable();
    
    /**
     * Test connection to the provider
     */
    CompletableFuture<Boolean> testConnection();
    
    /**
     * List available models
     */
    CompletableFuture<List<String>> listModels();
    
    /**
     * Execute chat completion
     */
    CompletableFuture<ChatCompletionResponse> executeCompletion(ChatCompletionRequest request);
    
    /**
     * Get provider capabilities
     */
    ILLMProvider.LLMCapabilities getCapabilities();
    
    /**
     * Get current configuration status
     */
    ILLMProvider.ProviderStatus getStatus();
    
    /**
     * Update provider configuration
     */
    void updateConfiguration(ILLMProvider.ProviderConfig config);
    
    /**
     * Get metrics for this provider
     */
    ILLMProvider.ProviderMetrics getMetrics();
    
    /**
     * LLM Provider capabilities
     */
    class LLMCapabilities {
        private final boolean supportsStreaming;
        private final boolean supportsFunctionCalling;
        private final boolean supportsVision;
        private final int maxContextLength;
        private final List<String> supportedFormats;
        
        public LLMCapabilities(boolean supportsStreaming, boolean supportsFunctionCalling,
                             boolean supportsVision, int maxContextLength, List<String> supportedFormats) {
            this.supportsStreaming = supportsStreaming;
            this.supportsFunctionCalling = supportsFunctionCalling;
            this.supportsVision = supportsVision;
            this.maxContextLength = maxContextLength;
            this.supportedFormats = supportedFormats;
        }
        
        public boolean isSupportsStreaming() { return supportsStreaming; }
        public boolean isSupportsFunctionCalling() { return supportsFunctionCalling; }
        public boolean isSupportsVision() { return supportsVision; }
        public int getMaxContextLength() { return maxContextLength; }
        public List<String> getSupportedFormats() { return supportedFormats; }
    }
    
    /**
     * Provider status information
     */
    class ProviderStatus {
        private final boolean available;
        private final boolean healthy;
        private final String statusMessage;
        private final long lastCheckedAt;
        private final Double responseTimeMs;
        
        public ProviderStatus(boolean available, boolean healthy, String statusMessage,
                            long lastCheckedAt, Double responseTimeMs) {
            this.available = available;
            this.healthy = healthy;
            this.statusMessage = statusMessage;
            this.lastCheckedAt = lastCheckedAt;
            this.responseTimeMs = responseTimeMs;
        }
        
        public boolean isAvailable() { return available; }
        public boolean isHealthy() { return healthy; }
        public String getStatusMessage() { return statusMessage; }
        public long getLastCheckedAt() { return lastCheckedAt; }
        public Double getResponseTimeMs() { return responseTimeMs; }
    }
    
    /**
     * Provider configuration
     */
    class ProviderConfig {
        private final String apiKey;
        private final String baseUrl;
        private final Integer timeout;
        private final Integer maxRetries;
        private final Boolean enableCache;
        
        public ProviderConfig(String apiKey, String baseUrl, Integer timeout,
                            Integer maxRetries, Boolean enableCache) {
            this.apiKey = apiKey;
            this.baseUrl = baseUrl;
            this.timeout = timeout;
            this.maxRetries = maxRetries;
            this.enableCache = enableCache;
        }
        
        public String getApiKey() { return apiKey; }
        public String getBaseUrl() { return baseUrl; }
        public Integer getTimeout() { return timeout; }
        public Integer getMaxRetries() { return maxRetries; }
        public Boolean isEnableCache() { return enableCache; }
    }
    
    /**
     * Provider metrics
     */
    class ProviderMetrics {
        private final long totalRequests;
        private final long successfulRequests;
        private final long failedRequests;
        private final double averageResponseTime;
        private final double totalCost;
        private final long lastRequestAt;
        
        public ProviderMetrics(long totalRequests, long successfulRequests, long failedRequests,
                             double averageResponseTime, double totalCost, long lastRequestAt) {
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
            this.averageResponseTime = averageResponseTime;
            this.totalCost = totalCost;
            this.lastRequestAt = lastRequestAt;
        }
        
        public long getTotalRequests() { return totalRequests; }
        public long getSuccessfulRequests() { return successfulRequests; }
        public long getFailedRequests() { return failedRequests; }
        public double getAverageResponseTime() { return averageResponseTime; }
        public double getTotalCost() { return totalCost; }
        public long getLastRequestAt() { return lastRequestAt; }
        
        public double getSuccessRate() {
            return totalRequests > 0 ? (double) successfulRequests / totalRequests : 0.0;
        }
    }
}