package org.example.infrastructure.services;

import org.example.domain.interfaces.ILLMProvider;
import org.example.domain.dto.llm.ChatCompletionRequest;
import org.example.domain.dto.llm.ChatCompletionResponse;
import org.example.domain.dto.common.BaseResponse;
import org.example.domain.entities.LLMProvider;
import org.example.infrastructure.services.common.GenericRetryTemplate;
import org.example.infrastructure.services.caching.LLMCacheManager;
import org.example.infrastructure.services.metrics.LLMMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Optimized LLM Orchestrator Service
 * Consolidates functionality from multiple LLM services and provides unified interface
 * Implements caching, retry logic, circuit breaker, and performance monitoring
 */
@Service
public class OptimizedLLMOrchestrator {
    
    private static final Logger logger = LoggerFactory.getLogger(OptimizedLLMOrchestrator.class);
    
    private final Map<LLMProvider, ILLMProvider> providers = new ConcurrentHashMap<>();
    private final GenericRetryTemplate retryTemplate;
    private final LLMCacheManager cacheManager;
    private final LLMMetricsCollector metricsCollector;
    private LLMProvider activeProvider;
    
    @Autowired
    public OptimizedLLMOrchestrator(GenericRetryTemplate retryTemplate, 
                                  LLMCacheManager cacheManager, 
                                  LLMMetricsCollector metricsCollector) {
        this.retryTemplate = retryTemplate;
        this.cacheManager = cacheManager;
        this.metricsCollector = metricsCollector;
    }
    
    /**
     * Register LLM provider
     */
    public void registerProvider(LLMProvider providerType, ILLMProvider provider) {
        providers.put(providerType, provider);
        logger.info("Registered LLM provider: {}", providerType);
    }
    
    /**
     * Set active provider
     */
    public void setActiveProvider(LLMProvider provider) {
        if (!providers.containsKey(provider)) {
            throw new IllegalArgumentException("Provider not registered: " + provider);
        }
        this.activeProvider = provider;
        logger.info("Active LLM provider set to: {}", provider);
    }
    
    /**
     * Execute chat completion with optimized patterns
     */
    public CompletableFuture<OptimizedChatCompletionResponse> executeChatCompletion(
            ChatCompletionRequest request) {
        
        return executeWithFallbackProviders(request, getProviderList());
    }
    
    /**
     * Execute with specific provider
     */
    public CompletableFuture<OptimizedChatCompletionResponse> executeChatCompletion(
            ChatCompletionRequest request, LLMProvider provider) {
        
        if (!providers.containsKey(provider)) {
            return CompletableFuture.failedFuture(
                new IllegalArgumentException("Provider not registered: " + provider));
        }
        
        return executeWithRetryAndCache(request, provider);
    }
    
    /**
     * Get all available providers
     */
    public List<LLMProvider> getAvailableProviders() {
        return providers.entrySet().stream()
            .filter(entry -> entry.getValue().isAvailable())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Get provider status
     */
    public Map<LLMProvider, ILLMProvider.ProviderStatus> getProviderStatuses() {
        return providers.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getStatus()
            ));
    }
    
    /**
     * Get consolidated metrics
     */
    public LLMOrchestratorMetrics getMetrics() {
        Map<LLMProvider, ILLMProvider.ProviderMetrics> providerMetrics = 
            providers.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getMetrics()
                ));
        
        return new LLMOrchestratorMetrics(
            activeProvider,
            getAvailableProviders(),
            providerMetrics,
            cacheManager.getCacheStats(),
            metricsCollector.getAggregatedMetrics()
        );
    }
    
    /**
     * Execute with fallback providers
     */
    private CompletableFuture<OptimizedChatCompletionResponse> executeWithFallbackProviders(
            ChatCompletionRequest request, List<LLMProvider> providerList) {
        
        if (providerList.isEmpty()) {
            return CompletableFuture.failedFuture(
                new RuntimeException("No available LLM providers"));
        }
        
        // Try primary provider first
        return executeWithRetryAndCache(request, providerList.get(0))
            .thenCompose(response -> {
                if (response.isSuccessful()) {
                    return CompletableFuture.completedFuture(response);
                }
                
                // Try fallback providers
                if (providerList.size() > 1) {
                    return executeWithFallbackProviders(request, providerList.subList(1, providerList.size()));
                }
                
                return CompletableFuture.completedFuture(response);
            })
            .exceptionally(throwable -> {
                logger.error("All providers failed for request", throwable);
                return new OptimizedChatCompletionResponse(
                    BaseResponse.ResponseStatus.ERROR,
                    "All providers failed: " + throwable.getMessage()
                );
            });
    }
    
    /**
     * Execute with retry and caching
     */
    private CompletableFuture<OptimizedChatCompletionResponse> executeWithRetryAndCache(
            ChatCompletionRequest request, LLMProvider provider) {
        
        ILLMProvider llmProvider = providers.get(provider);
        String cacheKey = cacheManager.generateCacheKey(request, provider);
        
        // Try cache first
        return cacheManager.get(cacheKey)
            .map(cachedResponse -> CompletableFuture.completedFuture(cachedResponse))
            .orElseGet(() -> {
                // Execute with retry logic
                GenericRetryTemplate.RetryConfig retryConfig = GenericRetryTemplate.RetryConfig.builder()
                    .maxAttempts(3)
                    .baseDelay(java.time.Duration.ofSeconds(1))
                    .maxDelay(java.time.Duration.ofSeconds(10))
                    .enableJitter(true)
                    .build();
                
                return retryTemplate.executeWithRetry(
                    () -> executeProviderOperation(request, llmProvider),
                    retryConfig
                ).thenCompose(result -> {
                    if (result.isSuccessful()) {
                        // Cache successful responses
                        cacheManager.put(cacheKey, result);
                    }
                    return CompletableFuture.completedFuture(result);
                });
            });
    }
    
    /**
     * Execute provider operation
     */
    private OptimizedChatCompletionResponse executeProviderOperation(
            ChatCompletionRequest request, ILLMProvider provider) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Record metrics
            metricsCollector.recordRequestStart(provider.getProviderType());
            
            // Execute completion
            CompletableFuture<ChatCompletionResponse> completionFuture = 
                provider.executeCompletion(request);
            
            ChatCompletionResponse response = completionFuture.join();
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Create optimized response
            OptimizedChatCompletionResponse optimizedResponse = new OptimizedChatCompletionResponse(
                BaseResponse.ResponseStatus.SUCCESS,
                "Completion successful"
            );
            optimizedResponse.setProvider(provider.getProviderType());
            optimizedResponse.setOriginalResponse(response);
            optimizedResponse.setResponseTimeMs((double) responseTime);
            optimizedResponse.setCached(false);
            
            // Record metrics
            metricsCollector.recordRequestSuccess(provider.getProviderType(), responseTime);
            
            return optimizedResponse;
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            metricsCollector.recordRequestFailure(provider.getProviderType(), responseTime);
            
            logger.error("Provider operation failed: {}", provider.getProviderName(), e);
            
            return new OptimizedChatCompletionResponse(
                BaseResponse.ResponseStatus.ERROR,
                "Provider operation failed: " + e.getMessage()
            );
        }
    }
    
    /**
     * Get provider list in priority order
     */
    private List<LLMProvider> getProviderList() {
        if (activeProvider != null && providers.containsKey(activeProvider)) {
            List<LLMProvider> list = new java.util.ArrayList<>();
            list.add(activeProvider);
            
            // Add other available providers
            providers.keySet().stream()
                .filter(p -> p != activeProvider)
                .filter(providers::containsKey)
                .forEach(list::add);
            
            return list;
        }
        
        return getAvailableProviders();
    }
    
    /**
     * Optimized chat completion response
     */
    public static class OptimizedChatCompletionResponse extends BaseResponse {
        private ChatCompletionResponse originalResponse;
        private LLMProvider provider;
        private boolean cached;
        private Double responseTimeMs;
        private String executionStrategy;
        
        public OptimizedChatCompletionResponse(BaseResponse.ResponseStatus status, String message) {
            super(status, message);
        }
        
        public OptimizedChatCompletionResponse(ChatCompletionResponse originalResponse, 
                                             LLMProvider provider, boolean cached) {
            this(BaseResponse.ResponseStatus.SUCCESS, "Completion successful");
            this.originalResponse = originalResponse;
            this.provider = provider;
            this.cached = cached;
        }
        
        // Getters and setters
        public ChatCompletionResponse getOriginalResponse() { return originalResponse; }
        public void setOriginalResponse(ChatCompletionResponse originalResponse) { 
            this.originalResponse = originalResponse; 
        }
        
        public LLMProvider getProvider() { return provider; }
        public void setProvider(LLMProvider provider) { this.provider = provider; }
        
        public boolean isCached() { return cached; }
        public void setCached(boolean cached) { this.cached = cached; }
        
        public Double getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(Double responseTimeMs) { this.responseTimeMs = responseTimeMs; }
        
        public String getExecutionStrategy() { return executionStrategy; }
        public void setExecutionStrategy(String executionStrategy) { this.executionStrategy = executionStrategy; }
        
        // Helper methods
        public String getResponse() {
            return originalResponse != null ? originalResponse.getResponse() : null;
        }
        
        public Integer getTokensUsed() {
            return originalResponse != null ? originalResponse.getTokensUsed() : null;
        }
        
        public Double getCost() {
            return originalResponse != null ? originalResponse.getCost() : null;
        }
    }
    
    /**
     * LLM Orchestrator metrics
     */
    public static class LLMOrchestratorMetrics {
        private final LLMProvider activeProvider;
        private final List<LLMProvider> availableProviders;
        private final Map<LLMProvider, ILLMProvider.ProviderMetrics> providerMetrics;
        private final LLMCacheManager.CacheStats cacheStats;
        private final LLMMetricsCollector.AggregatedMetrics aggregatedMetrics;
        
        public LLMOrchestratorMetrics(LLMProvider activeProvider,
                                    List<LLMProvider> availableProviders,
                                    Map<LLMProvider, ILLMProvider.ProviderMetrics> providerMetrics,
                                    LLMCacheManager.CacheStats cacheStats,
                                    LLMMetricsCollector.AggregatedMetrics aggregatedMetrics) {
            this.activeProvider = activeProvider;
            this.availableProviders = availableProviders;
            this.providerMetrics = providerMetrics;
            this.cacheStats = cacheStats;
            this.aggregatedMetrics = aggregatedMetrics;
        }
        
        // Getters
        public LLMProvider getActiveProvider() { return activeProvider; }
        public List<LLMProvider> getAvailableProviders() { return availableProviders; }
        public Map<LLMProvider, ILLMProvider.ProviderMetrics> getProviderMetrics() { return providerMetrics; }
        public LLMCacheManager.CacheStats getCacheStats() { return cacheStats; }
        public LLMMetricsCollector.AggregatedMetrics getAggregatedMetrics() { return aggregatedMetrics; }
    }
}