package org.example.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced HTTP client configuration for LLM services
 */
@Configuration
public class HttpClientConfig {
    
    @Bean
    public RestTemplate enhancedRestTemplate() {
        // Create custom request factory with timeouts
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(30).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(60).toMillis());
        
        // Create RestTemplate with custom configuration
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Add error handlers
        restTemplate.setErrorHandler(new EnhancedRestTemplateErrorHandler());
        
        return restTemplate;
    }
    
    /**
     * Custom error handler with detailed error information
     */
    private static class EnhancedRestTemplateErrorHandler implements org.springframework.web.client.ResponseErrorHandler {
        
        @Override
        public boolean hasError(org.springframework.http.client.ClientHttpResponse response) {
            try {
                int statusCode = response.getStatusCode().value();
                return statusCode >= 400;
            } catch (java.io.IOException e) {
                return true; // Treat IO errors as errors
            }
        }
        
        @Override
        public void handleError(org.springframework.http.client.ClientHttpResponse response) {
            try {
                int statusCode = response.getStatusCode().value();
                String statusText = response.getStatusText();
                String body = new String(response.getBody().readAllBytes());
                
                // Create appropriate exception based on status code
                switch (statusCode) {
                    case 429:
                        throw new org.example.domain.exception.OpenRouterRateLimitException(
                            "Rate limit exceeded: " + statusText + " - " + body);
                    case 502:
                    case 503:
                    case 504:
                        throw new org.example.domain.exception.OpenRouterServiceUnavailableException(
                            "Service unavailable: " + statusText + " - " + body);
                    case 401:
                    case 403:
                        throw new org.example.domain.exception.OpenRouterException(
                            "Authentication failed: " + statusText + " - " + body, "OpenRouter",
                            org.example.domain.exception.LLMException.ErrorCategory.AUTHENTICATION, false);
                    default:
                        throw new org.example.domain.exception.OpenRouterException(
                            "HTTP error " + statusCode + ": " + statusText + " - " + body, "OpenRouter",
                            org.example.domain.exception.LLMException.ErrorCategory.INTERNAL_ERROR, false);
                }
            } catch (java.io.IOException e) {
                // If we can't read the body, create a basic exception
                throw new org.example.domain.exception.OpenRouterException(
                    "Failed to process HTTP error response: " + e.getMessage(), "OpenRouter",
                    org.example.domain.exception.LLMException.ErrorCategory.INTERNAL_ERROR, false);
            }
        }
    }
    
    /**
     * HTTP client metrics bean
     */
    @Bean
    public HttpClientMetrics httpClientMetrics() {
        return new HttpClientMetrics();
    }
    
    /**
     * Metrics collection for HTTP client
     */
    public static class HttpClientMetrics {
        private final java.util.concurrent.atomic.AtomicLong totalRequests = new java.util.concurrent.atomic.AtomicLong(0);
        private final java.util.concurrent.atomic.AtomicLong successfulRequests = new java.util.concurrent.atomic.AtomicLong(0);
        private final java.util.concurrent.atomic.AtomicLong failedRequests = new java.util.concurrent.atomic.AtomicLong(0);
        private final java.util.concurrent.atomic.AtomicLong totalLatency = new java.util.concurrent.atomic.AtomicLong(0);
        private final java.util.concurrent.atomic.AtomicLong rateLimitExceptions = new java.util.concurrent.atomic.AtomicLong(0);
        private final java.util.concurrent.atomic.AtomicLong timeoutExceptions = new java.util.concurrent.atomic.AtomicLong(0);
        
        public void recordRequest(long latencyMs) {
            totalRequests.incrementAndGet();
            totalLatency.addAndGet(latencyMs);
        }
        
        public void recordSuccess() {
            successfulRequests.incrementAndGet();
        }
        
        public void recordFailure() {
            failedRequests.incrementAndGet();
        }
        
        public void recordRateLimitException() {
            rateLimitExceptions.incrementAndGet();
        }
        
        public void recordTimeoutException() {
            timeoutExceptions.incrementAndGet();
        }
        
        public java.util.Map<String, Object> getMetrics() {
            return java.util.Map.of(
                "totalRequests", totalRequests.get(),
                "successfulRequests", successfulRequests.get(),
                "failedRequests", failedRequests.get(),
                "successRate", totalRequests.get() > 0 ? 
                    (double) successfulRequests.get() / totalRequests.get() : 0.0,
                "averageLatency", totalRequests.get() > 0 ?
                    (double) totalLatency.get() / totalRequests.get() : 0.0,
                "rateLimitExceptions", rateLimitExceptions.get(),
                "timeoutExceptions", timeoutExceptions.get()
            );
        }
        
        public void reset() {
            totalRequests.set(0);
            successfulRequests.set(0);
            failedRequests.set(0);
            totalLatency.set(0);
            rateLimitExceptions.set(0);
            timeoutExceptions.set(0);
        }
    }
}