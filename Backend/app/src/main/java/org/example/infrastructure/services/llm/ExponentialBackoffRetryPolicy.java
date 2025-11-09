package org.example.infrastructure.services.llm;

import org.example.domain.exception.LLMException;
import org.example.infrastructure.config.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Exponential backoff retry policy implementation
 */
public class ExponentialBackoffRetryPolicy {
    
    private static final Logger logger = LoggerFactory.getLogger(ExponentialBackoffRetryPolicy.class);
    
    private final RetryConfig config;
    private final Function<Exception, Boolean> retryableExceptionChecker;
    
    public ExponentialBackoffRetryPolicy(RetryConfig config) {
        this(config, null);
    }
    
    public ExponentialBackoffRetryPolicy(RetryConfig config, Function<Exception, Boolean> retryableExceptionChecker) {
        this.config = config;
        this.retryableExceptionChecker = retryableExceptionChecker != null ? 
            retryableExceptionChecker : this::isDefaultRetryable;
    }
    
    /**
     * Execute operation with exponential backoff retry
     */
    public <T> RetryResult<T> executeWithRetry(RetryableOperation<T> operation, String operationName) throws Exception {
        if (!config.isEnabled()) {
            return new RetryResult<>(operation.execute(), 0, false);
        }
        
        Exception lastException = null;
        int attempt = 0;
        
        while (attempt < config.getMaxAttempts()) {
            try {
                attempt++;
                logger.debug("Attempting {} (attempt {}/{})", operationName, attempt, config.getMaxAttempts());
                
                T result = operation.execute();
                if (attempt > 1) {
                    logger.info("Operation {} succeeded on attempt {}", operationName, attempt);
                }
                
                return new RetryResult<>(result, attempt, attempt > 1);
                
            } catch (Exception e) {
                lastException = e;
                
                // Check if exception is retryable
                if (!isRetryableException(e)) {
                    logger.error("Operation {} failed with non-retryable exception: {}", operationName, e.getMessage());
                    throw e;
                }
                
                // Check if we have more attempts
                if (attempt >= config.getMaxAttempts()) {
                    logger.error("Operation {} failed after {} attempts. Last exception: {}", 
                               operationName, attempt, e.getMessage());
                    break;
                }
                
                // Calculate delay for next attempt
                Duration delay = calculateDelay(attempt, e);
                
                logger.warn("Operation {} failed on attempt {} with retryable exception: {}. Retrying in {}ms", 
                          operationName, attempt, e.getClass().getSimpleName(), delay.toMillis());
                
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new LLMException("Operation interrupted during retry delay", ie);
                }
            }
        }
        
        throw lastException;
    }
    
    /**
     * Calculate exponential backoff delay
     */
    private Duration calculateDelay(int attempt, Exception exception) {
        RetryConfig.ExceptionConfig exceptionConfig = getExceptionConfig(exception);
        
        // Use base delay
        Duration baseDelay = exceptionConfig.getDelay();
        
        // Apply exponential backoff
        long delayMs = (long) (baseDelay.toMillis() * Math.pow(exceptionConfig.getMultiplier(), attempt - 1));
        
        // Apply jitter to prevent thundering herd
        long jitterMs = (long) (delayMs * 0.1 * ThreadLocalRandom.current().nextDouble());
        delayMs += jitterMs;
        
        // Cap at max delay
        delayMs = Math.min(delayMs, exceptionConfig.getMaxDelay().toMillis());
        
        // Special handling for rate limit exceptions
        if (exception instanceof org.example.domain.exception.OpenRouterRateLimitException) {
            org.example.domain.exception.OpenRouterRateLimitException rateLimitEx = 
                (org.example.domain.exception.OpenRouterRateLimitException) exception;
            if (rateLimitEx.getRetryAfterSeconds() > 0) {
                delayMs = Math.max(delayMs, rateLimitEx.getRetryAfterSeconds() * 1000L);
            }
        }
        
        return Duration.ofMillis(delayMs);
    }
    
    /**
     * Check if exception is retryable
     */
    private boolean isRetryableException(Exception exception) {
        // Check custom exception checker first
        if (retryableExceptionChecker != null) {
            Boolean result = retryableExceptionChecker.apply(exception);
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
        if (exception instanceof LLMException) {
            return ((LLMException) exception).isRetryable();
        }
        
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
     * Get exception-specific configuration
     */
    private RetryConfig.ExceptionConfig getExceptionConfig(Exception exception) {
        String exceptionType = exception.getClass().getSimpleName();
        return config.getExceptionConfig(exceptionType);
    }
    
    /**
     * Functional interface for retryable operations
     */
    @FunctionalInterface
    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Result of retry operation
     */
    public static class RetryResult<T> {
        private final T result;
        private final int attempts;
        private final boolean retried;
        
        public RetryResult(T result, int attempts, boolean retried) {
            this.result = result;
            this.attempts = attempts;
            this.retried = retried;
        }
        
        public T getResult() { return result; }
        public int getAttempts() { return attempts; }
        public boolean isRetried() { return retried; }
    }
}