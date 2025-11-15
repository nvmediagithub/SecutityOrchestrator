package org.example.infrastructure.services.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Generic retry template for reusable retry logic across the application
 * Provides consistent retry patterns with configurable backoff strategies
 */
@Component
public class GenericRetryTemplate {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericRetryTemplate.class);
    
    private final Executor taskExecutor;
    
    public GenericRetryTemplate(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    /**
     * Execute operation with exponential backoff retry
     */
    public <T> CompletableFuture<T> executeWithRetry(
            SupplierWithException<T> supplier,
            RetryConfig retryConfig) {
        return executeWithRetry(supplier, retryConfig, retryConfig.getMaxAttempts());
    }
    
    /**
     * Execute operation with custom retry logic
     */
    public <T> CompletableFuture<T> executeWithRetry(
            SupplierWithException<T> supplier,
            RetryConfig retryConfig,
            int maxAttempts) {
        
        return CompletableFuture.supplyAsync(() -> {
            Exception lastException = null;
            int attempt = 0;
            
            while (attempt < maxAttempts) {
                try {
                    attempt++;
                    logger.debug("Attempting operation (attempt {}/{})", attempt, maxAttempts);
                    
                    T result = supplier.get();
                    if (attempt > 1) {
                        logger.info("Operation succeeded on attempt {}", attempt);
                    }
                    return result;
                    
                } catch (Exception e) {
                    lastException = e;
                    
                    // Check if exception is retryable
                    if (!isRetryableException(e, retryConfig)) {
                        logger.error("Operation failed with non-retryable exception: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                    
                    // Check if we have more attempts
                    if (attempt >= maxAttempts) {
                        logger.error("Operation failed after {} attempts. Last exception: {}", attempt, e.getMessage());
                        break;
                    }
                    
                    // Calculate delay for next attempt
                    long delayMs = calculateDelay(attempt, retryConfig);
                    
                    logger.warn("Operation failed on attempt {} with retryable exception: {}. Retrying in {}ms", 
                              attempt, e.getClass().getSimpleName(), delayMs);
                    
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted during retry delay", ie);
                    }
                }
            }
            
            throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
        }, taskExecutor);
    }
    
    /**
     * Execute operation with circuit breaker protection
     */
    public <T> CompletableFuture<T> executeWithCircuitBreaker(
            SupplierWithException<T> supplier,
            CircuitBreakerConfig circuitBreakerConfig) {
        
        return CompletableFuture.supplyAsync(() -> {
            CircuitBreaker circuitBreaker = new CircuitBreaker(circuitBreakerConfig);
            
            try {
                return circuitBreaker.executeOperation(supplier);
            } catch (Exception e) {
                throw new RuntimeException("Circuit breaker operation failed", e);
            }
        }, taskExecutor);
    }
    
    /**
     * Execute operation with both retry and circuit breaker
     */
    public <T> CompletableFuture<T> executeWithRetryAndCircuitBreaker(
            SupplierWithException<T> supplier,
            RetryConfig retryConfig,
            CircuitBreakerConfig circuitBreakerConfig) {
        
        return CompletableFuture.supplyAsync(() -> {
            CircuitBreaker circuitBreaker = new CircuitBreaker(circuitBreakerConfig);
            Exception lastException = null;
            int attempt = 0;
            
            while (attempt < retryConfig.getMaxAttempts()) {
                try {
                    attempt++;
                    return circuitBreaker.executeOperation(supplier);
                    
                } catch (Exception e) {
                    lastException = e;
                    
                    if (!isRetryableException(e, retryConfig) || attempt >= retryConfig.getMaxAttempts()) {
                        break;
                    }
                    
                    long delayMs = calculateDelay(attempt, retryConfig);
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted during retry delay", ie);
                    }
                }
            }
            
            throw new RuntimeException("Operation failed after " + retryConfig.getMaxAttempts() + " attempts", lastException);
        }, taskExecutor);
    }
    
    /**
     * Check if exception is retryable
     */
    private boolean isRetryableException(Exception exception, RetryConfig config) {
        // Check custom retryable exception checker
        if (config.getRetryableExceptionChecker() != null) {
            Boolean result = config.getRetryableExceptionChecker().test(exception);
            if (result != null) {
                return result;
            }
        }
        
        // Use default implementation
        return isDefaultRetryable(exception);
    }
    
    /**
     * Default retryable exception check
     */
    private boolean isDefaultRetryable(Exception exception) {
        // Network-related exceptions
        if (exception instanceof java.net.SocketTimeoutException ||
            exception instanceof java.net.ConnectException ||
            exception instanceof java.net.UnknownHostException) {
            return true;
        }
        
        // HTTP status codes that are typically retryable
        if (exception instanceof org.springframework.web.client.HttpClientErrorException) {
            org.springframework.web.client.HttpClientErrorException httpEx = 
                (org.springframework.web.client.HttpClientErrorException) exception;
            int statusCode = httpEx.getStatusCode().value();
            
            // 429 (Too Many Requests), 502 (Bad Gateway), 503 (Service Unavailable), 504 (Gateway Timeout)
            return statusCode == 429 || statusCode == 502 || statusCode == 503 || statusCode == 504;
        }
        
        return false;
    }
    
    /**
     * Calculate exponential backoff delay
     */
    private long calculateDelay(int attempt, RetryConfig config) {
        long baseDelay = config.getBaseDelay().toMillis();
        long maxDelay = config.getMaxDelay().toMillis();
        double multiplier = config.getMultiplier();
        
        // Apply exponential backoff
        long delayMs = (long) (baseDelay * Math.pow(multiplier, attempt - 1));
        
        // Cap at max delay
        delayMs = Math.min(delayMs, maxDelay);
        
        // Add jitter to prevent thundering herd
        if (config.isEnableJitter()) {
            long jitterMs = (long) (delayMs * 0.1 * Math.random());
            delayMs += jitterMs;
        }
        
        return delayMs;
    }
    
    /**
     * Configuration for retry operations
     */
    public static class RetryConfig {
        private int maxAttempts = 3;
        private java.time.Duration baseDelay = java.time.Duration.ofMillis(1000);
        private java.time.Duration maxDelay = java.time.Duration.ofSeconds(30);
        private double multiplier = 2.0;
        private boolean enableJitter = true;
        private Predicate<Exception> retryableExceptionChecker;
        
        // Getters and setters
        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
        
        public java.time.Duration getBaseDelay() { return baseDelay; }
        public void setBaseDelay(java.time.Duration baseDelay) { this.baseDelay = baseDelay; }
        
        public java.time.Duration getMaxDelay() { return maxDelay; }
        public void setMaxDelay(java.time.Duration maxDelay) { this.maxDelay = maxDelay; }
        
        public double getMultiplier() { return multiplier; }
        public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
        
        public boolean isEnableJitter() { return enableJitter; }
        public void setEnableJitter(boolean enableJitter) { this.enableJitter = enableJitter; }
        
        public Predicate<Exception> getRetryableExceptionChecker() { return retryableExceptionChecker; }
        public void setRetryableExceptionChecker(Predicate<Exception> retryableExceptionChecker) { 
            this.retryableExceptionChecker = retryableExceptionChecker; 
        }
        
        // Builder pattern
        public static RetryConfig builder() {
            return new RetryConfig();
        }
        
        public RetryConfig maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }
        
        public RetryConfig baseDelay(java.time.Duration baseDelay) {
            this.baseDelay = baseDelay;
            return this;
        }
        
        public RetryConfig maxDelay(java.time.Duration maxDelay) {
            this.maxDelay = maxDelay;
            return this;
        }
        
        public RetryConfig multiplier(double multiplier) {
            this.multiplier = multiplier;
            return this;
        }
        
        public RetryConfig enableJitter(boolean enableJitter) {
            this.enableJitter = enableJitter;
            return this;
        }
    }
    
    /**
     * Configuration for circuit breaker
     */
    public static class CircuitBreakerConfig {
        private int failureThreshold = 5;
        private java.time.Duration timeout = java.time.Duration.ofSeconds(60);
        private int successThreshold = 3;
        
        public int getFailureThreshold() { return failureThreshold; }
        public void setFailureThreshold(int failureThreshold) { this.failureThreshold = failureThreshold; }
        
        public java.time.Duration getTimeout() { return timeout; }
        public void setTimeout(java.time.Duration timeout) { this.timeout = timeout; }
        
        public int getSuccessThreshold() { return successThreshold; }
        public void setSuccessThreshold(int successThreshold) { this.successThreshold = successThreshold; }
        
        public static CircuitBreakerConfig defaultConfig() {
            return new CircuitBreakerConfig();
        }
    }
    
    /**
     * Simple circuit breaker implementation
     */
    private static class CircuitBreaker {
        private final CircuitBreakerConfig config;
        private State state = State.CLOSED;
        private int failureCount = 0;
        private long lastFailureTime = 0;
        private int successCount = 0;
        
        public CircuitBreaker(CircuitBreakerConfig config) {
            this.config = config;
        }
        
        public <T> T executeOperation(SupplierWithException<T> operation) throws Exception {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime >= config.getTimeout().toMillis()) {
                    state = State.HALF_OPEN;
                } else {
                    throw new RuntimeException("Circuit breaker is OPEN");
                }
            }
            
            try {
                T result = operation.get();
                onSuccess();
                return result;
            } catch (Exception e) {
                onFailure();
                throw e;
            }
        }
        
        private void onSuccess() {
            if (state == State.HALF_OPEN) {
                successCount++;
                if (successCount >= config.getSuccessThreshold()) {
                    state = State.CLOSED;
                    failureCount = 0;
                    successCount = 0;
                }
            } else {
                failureCount = 0;
            }
        }
        
        private void onFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            
            if (failureCount >= config.getFailureThreshold()) {
                state = State.OPEN;
            }
        }
        
        private enum State {
            CLOSED, OPEN, HALF_OPEN
        }
    }
    
    /**
     * Functional interface for operations that can throw exceptions
     */
    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}