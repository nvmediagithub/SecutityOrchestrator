package org.example.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for retry policies
 */
@Configuration
@ConfigurationProperties(prefix = "llm.retry")
public class RetryConfig {
    
    private boolean enabled = true;
    private int maxAttempts = 3;
    private Duration initialDelay = Duration.ofSeconds(1);
    private Duration maxDelay = Duration.ofSeconds(60);
    private double multiplier = 2.0;
    private Duration timeout = Duration.ofSeconds(30);
    
    // Exception-specific configurations
    private Map<String, ExceptionConfig> exceptions = new HashMap<>();
    
    public static class ExceptionConfig {
        private boolean retryable = true;
        private int maxAttempts = 3;
        private Duration delay = Duration.ofSeconds(1);
        private double multiplier = 2.0;
        private Duration maxDelay = Duration.ofSeconds(60);
        
        // Getters and setters
        public boolean isRetryable() { return retryable; }
        public void setRetryable(boolean retryable) { this.retryable = retryable; }
        
        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
        
        public Duration getDelay() { return delay; }
        public void setDelay(Duration delay) { this.delay = delay; }
        
        public double getMultiplier() { return multiplier; }
        public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
        
        public Duration getMaxDelay() { return maxDelay; }
        public void setMaxDelay(Duration maxDelay) { this.maxDelay = maxDelay; }
    }
    
    // Getters and setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public Duration getInitialDelay() { return initialDelay; }
    public void setInitialDelay(Duration initialDelay) { this.initialDelay = initialDelay; }
    
    public Duration getMaxDelay() { return maxDelay; }
    public void setMaxDelay(Duration maxDelay) { this.maxDelay = maxDelay; }
    
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
    
    public Duration getTimeout() { return timeout; }
    public void setTimeout(Duration timeout) { this.timeout = timeout; }
    
    public Map<String, ExceptionConfig> getExceptions() { return exceptions; }
    public void setExceptions(Map<String, ExceptionConfig> exceptions) { this.exceptions = exceptions; }
    
    // Helper methods
    public ExceptionConfig getExceptionConfig(String exceptionType) {
        return exceptions.getOrDefault(exceptionType, new ExceptionConfig());
    }
}