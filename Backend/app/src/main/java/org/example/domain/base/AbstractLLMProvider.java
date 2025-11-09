package org.example.domain.base;

import org.example.domain.interfaces.ILLMProvider;
import org.example.domain.dto.llm.ChatCompletionRequest;
import org.example.domain.dto.llm.ChatCompletionResponse;
import org.example.domain.entities.LLMProvider;
import org.example.infrastructure.services.llm.ExponentialBackoffRetryPolicy;
import org.example.infrastructure.services.llm.LLMCircuitBreaker;
import org.example.infrastructure.config.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Abstract base class for LLM providers
 * Provides common functionality and template methods
 */
public abstract class AbstractLLMProvider implements ILLMProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractLLMProvider.class);
    
    protected final RetryConfig retryConfig;
    protected final LLMCircuitBreaker circuitBreaker;
    protected final ExponentialBackoffRetryPolicy retryPolicy;
    
    // Metrics tracking
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong totalCost = new AtomicLong(0);
    private final AtomicLong lastRequestAt = new AtomicLong(0);
    
    protected AbstractLLMProvider(String providerName, RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
        this.circuitBreaker = new LLMCircuitBreaker(providerName);
        this.retryPolicy = new ExponentialBackoffRetryPolicy(retryConfig);
    }
    
    /**
     * Execute operation with circuit breaker and retry logic
     */
    protected <T> CompletableFuture<T> executeWithRetryAndCircuitBreaker(
            Function<ChatCompletionRequest, T> operation,
            ChatCompletionRequest request,
            String operationName) {
        
        totalRequests.incrementAndGet();
        long startTime = System.currentTimeMillis();
        
        try {
            // Execute with circuit breaker protection
            LLMCircuitBreaker.ExecutionResult<T> result = circuitBreaker.executeOperation(() -> {
                // Execute with retry policy
                ExponentialBackoffRetryPolicy.RetryResult<T> retryResult = 
                    retryPolicy.executeWithRetry(
                        () -> operation.apply(request), 
                        operationName
                    );
                return retryResult.getResult();
            });
            
            if (result.isFailed()) {
                failedRequests.incrementAndGet();
                throw new RuntimeException("Operation failed in circuit breaker state: " + result.getState());
            }
            
            successfulRequests.incrementAndGet();
            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            lastRequestAt.set(System.currentTimeMillis());
            
            return CompletableFuture.completedFuture(result.getResult());
            
        } catch (Exception e) {
            failedRequests.incrementAndGet();
            logger.error("Operation {} failed for provider {}", operationName, getProviderName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Check if exception is retryable
     */
    protected boolean isRetryableException(Exception exception) {
        if (exception instanceof HttpClientErrorException) {
            HttpClientErrorException httpEx = (HttpClientErrorException) exception;
            int statusCode = httpEx.getStatusCode().value();
            // Retry on 5xx server errors and 429 rate limit
            return (statusCode >= 500 && statusCode <= 599) || statusCode == 429;
        }
        
        if (exception instanceof ResourceAccessException) {
            // Retry on network issues
            return true;
        }
        
        return false;
    }
    
    /**
     * Default implementation for provider capabilities
     */
    @Override
    public ILLMProvider.LLMCapabilities getCapabilities() {
        return new ILLMProvider.LLMCapabilities(
            supportsStreaming(),
            supportsFunctionCalling(),
            supportsVision(),
            getMaxContextLength(),
            getSupportedFormats()
        );
    }
    
    /**
     * Default implementation for provider status
     */
    @Override
    public ILLMProvider.ProviderStatus getStatus() {
        long lastChecked = lastRequestAt.get();
        double avgResponseTime = totalRequests.get() > 0 ? 
            (double) totalResponseTime.get() / totalRequests.get() : 0.0;
        
        return new ILLMProvider.ProviderStatus(
            isAvailable(),
            isAvailable(), // Healthy if available
            isAvailable() ? "Provider is operational" : "Provider is not available",
            lastChecked,
            avgResponseTime > 0 ? avgResponseTime : null
        );
    }
    
    /**
     * Default implementation for provider metrics
     */
    @Override
    public ILLMProvider.ProviderMetrics getMetrics() {
        return new ILLMProvider.ProviderMetrics(
            totalRequests.get(),
            successfulRequests.get(),
            failedRequests.get(),
            totalRequests.get() > 0 ? (double) totalResponseTime.get() / totalRequests.get() : 0.0,
            totalCost.get() / 100.0, // Convert to decimal dollars
            lastRequestAt.get()
        );
    }
    
    /**
     * Default configuration update
     */
    @Override
    public void updateConfiguration(ILLMProvider.ProviderConfig config) {
        // Template method - subclasses should override if they need to handle config updates
        logger.info("Configuration update requested for provider {}", getProviderName());
    }
    
    // Abstract methods to be implemented by subclasses
    protected abstract boolean supportsStreaming();
    protected abstract boolean supportsFunctionCalling();
    protected abstract boolean supportsVision();
    protected abstract int getMaxContextLength();
    protected abstract List<String> getSupportedFormats();
    
    /**
     * Utility method to create standardized error messages
     */
    protected String createErrorMessage(String operation, Exception e) {
        return String.format("Provider %s failed for operation %s: %s", 
            getProviderName(), operation, e.getMessage());
    }
    
    /**
     * Utility method to create standardized success messages
     */
    protected String createSuccessMessage(String operation, String model) {
        return String.format("Provider %s completed operation %s for model %s", 
            getProviderName(), operation, model);
    }
}