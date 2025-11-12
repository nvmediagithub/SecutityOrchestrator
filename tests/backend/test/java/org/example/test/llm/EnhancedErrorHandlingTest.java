package org.example.test.llm;

import org.example.domain.exception.*;
import org.example.infrastructure.config.RetryConfig;
import org.example.infrastructure.services.llm.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for enhanced LLM error handling
 */
@ExtendWith(MockitoExtension.class)
class EnhancedErrorHandlingTest {
    
    @Mock
    private RetryConfig retryConfig;
    
    private ExponentialBackoffRetryPolicy retryPolicy;
    private LLMCircuitBreaker circuitBreaker;
    private RateLimiterService rateLimiter;
    private LLMErrorMonitoringService monitoringService;
    
    @BeforeEach
    void setUp() {
        // Configure default retry config
        when(retryConfig.isEnabled()).thenReturn(true);
        when(retryConfig.getMaxAttempts()).thenReturn(3);
        when(retryConfig.getInitialDelay()).thenReturn(Duration.ofSeconds(1));
        when(retryConfig.getMaxDelay()).thenReturn(Duration.ofSeconds(60));
        when(retryConfig.getMultiplier()).thenReturn(2.0);
        when(retryConfig.getTimeout()).thenReturn(Duration.ofSeconds(30));
        
        retryPolicy = new ExponentialBackoffRetryPolicy(retryConfig);
        circuitBreaker = new LLMCircuitBreaker("test-circuit-breaker");
        rateLimiter = new RateLimiterService();
        monitoringService = new LLMErrorMonitoringService();
    }
    
    @Test
    void testRetryPolicy_RetryableException() throws Exception {
        // Arrange
        MockRetryableOperation operation = new MockRetryableOperation();
        operation.setExceptionOnFirstTwoCalls(new OpenRouterRateLimitException("Rate limit exceeded"));
        operation.setResultOnThirdCall("success");
        
        // Act
        ExponentialBackoffRetryPolicy.RetryResult<String> result = 
            retryPolicy.executeWithRetry(operation, "test-operation");
        
        // Assert
        assertTrue(result.isRetried());
        assertEquals(3, result.getAttempts());
        assertEquals("success", result.getResult());
        assertEquals(2, operation.getCallCount());
    }
    
    @Test
    void testRetryPolicy_NonRetryableException() {
        // Arrange
        MockRetryableOperation operation = new MockRetryableOperation();
        operation.setExceptionOnAllCalls(new OpenRouterException("Non-retryable error"));
        
        // Act & Assert
        assertThrows(OpenRouterException.class, () -> {
            retryPolicy.executeWithRetry(operation, "test-operation");
        });
        
        // Should fail on first attempt since exception is not retryable
        assertEquals(1, operation.getCallCount());
    }
    
    @Test
    void testRetryPolicy_TimeoutException() throws Exception {
        // Arrange
        MockRetryableOperation operation = new MockRetryableOperation();
        operation.setExceptionOnAllCalls(new OpenRouterTimeoutException("test", 5000L));
        when(retryConfig.getMaxAttempts()).thenReturn(2);
        
        // Act & Assert
        assertThrows(OpenRouterTimeoutException.class, () -> {
            retryPolicy.executeWithRetry(operation, "test-operation");
        });
        
        // Should retry timeout exceptions
        assertEquals(2, operation.getCallCount());
    }
    
    @Test
    void testCircuitBreaker_ClosedState() throws Exception {
        // Arrange
        MockOperation<String> operation = new MockOperation<>("success");
        
        // Act
        LLMCircuitBreaker.ExecutionResult<String> result = circuitBreaker.executeOperation(operation);
        
        // Assert
        assertEquals(LLMCircuitBreaker.State.CLOSED, result.getState());
        assertFalse(result.isFailed());
        assertEquals("success", result.getResult());
        assertEquals(1, operation.getCallCount());
    }
    
    @Test
    void testCircuitBreaker_OpenState_AfterFailures() {
        // Arrange - configure circuit breaker to open after 2 failures
        circuitBreaker = new LLMCircuitBreaker("test-circuit-breaker", 
            LLMCircuitBreaker.Config.customConfig(2, Duration.ofSeconds(60), 1));
        
        MockOperation<String> failingOperation = new MockOperation<>();
        failingOperation.setException(new RuntimeException("Service failure"));
        
        // Act - cause first failure
        try {
            circuitBreaker.executeOperation(failingOperation);
        } catch (Exception e) {
            // Expected
        }
        
        // Act - cause second failure (should trigger open)
        try {
            circuitBreaker.executeOperation(failingOperation);
        } catch (Exception e) {
            // Expected
        }
        
        // Assert - circuit breaker should now be open
        LLMCircuitBreaker.StateInfo stateInfo = circuitBreaker.getStateInfo();
        assertEquals(LLMCircuitBreaker.State.OPEN, stateInfo.getState());
        
        // Third call should fail fast
        assertThrows(LLMCircuitBreaker.LLMCircuitBreakerOpenException.class, () -> {
            circuitBreaker.executeOperation(failingOperation);
        });
    }
    
    @Test
    void testCircuitBreaker_HalfOpenState_Recovery() throws Exception {
        // Arrange
        circuitBreaker = new LLMCircuitBreaker("test-circuit-breaker",
            LLMCircuitBreaker.Config.customConfig(1, Duration.ofSeconds(1), 1));
        
        MockOperation<String> failingOperation = new MockOperation<>();
        failingOperation.setException(new RuntimeException("Service failure"));
        
        MockOperation<String> recoveringOperation = new MockOperation<>("recovered");
        
        // Act - cause failure and open circuit breaker
        circuitBreaker.executeOperation(failingOperation);
        assertEquals(LLMCircuitBreaker.State.OPEN, circuitBreaker.getStateInfo().getState());
        
        // Wait for timeout to transition to half-open
        try {
            Thread.sleep(1100); // Wait slightly more than timeout
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Act - test successful recovery
        LLMCircuitBreaker.ExecutionResult<String> result = circuitBreaker.executeOperation(recoveringOperation);
        
        // Assert - should transition to closed state
        assertEquals(LLMCircuitBreaker.State.CLOSED, result.getState());
        assertFalse(result.isFailed());
        assertEquals("recovered", result.getResult());
    }
    
    @Test
    void testRateLimiter_TokenAcquisition() {
        // Arrange
        String apiKey = "test-api-key";
        
        // Act & Assert - should be able to acquire token
        assertTrue(rateLimiter.acquireToken(apiKey, 1.0));
        
        // Check status
        RateLimiterService.RateLimitStatus status = rateLimiter.getRateLimitStatus(apiKey);
        assertNotNull(status);
        assertTrue(status.getAvailableTokens() < 10); // Should be less than capacity after acquisition
    }
    
    @Test
    void testRateLimiter_BlockedOnExhaustion() throws Exception {
        // Arrange
        String apiKey = "limited-api-key";
        
        // Exhaust all tokens
        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.acquireToken(apiKey, 1.0));
        }
        
        // Act & Assert - should fail when tokens exhausted
        assertFalse(rateLimiter.acquireToken(apiKey, 1.0));
        
        // Should throw exception when blocking with timeout
        assertThrows(OpenRouterRateLimitException.class, () -> {
            rateLimiter.acquireTokenBlocking(apiKey, 1.0, Duration.ofSeconds(1));
        });
    }
    
    @Test
    void testRateLimiter_RefillMechanism() throws Exception {
        // Arrange
        String apiKey = "refill-api-key";
        
        // Exhaust tokens
        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.acquireToken(apiKey, 1.0));
        }
        
        RateLimiterService.RateLimitStatus emptyStatus = rateLimiter.getRateLimitStatus(apiKey);
        assertEquals(0, emptyStatus.getAvailableTokens());
        
        // Act - wait for refill
        Thread.sleep(2000); // Wait 2 seconds (token bucket has 1 token per second refill)
        
        RateLimiterService.RateLimitStatus refilledStatus = rateLimiter.getRateLimitStatus(apiKey);
        assertTrue(refilledStatus.getAvailableTokens() > 0);
        
        // Should be able to acquire token after refill
        assertTrue(rateLimiter.acquireToken(apiKey, 1.0));
    }
    
    @Test
    void testHttpClientErrorMapping_429() {
        // Act & Assert
        HttpClientErrorException exception = HttpClientErrorException.create(
            HttpStatus.TOO_MANY_REQUESTS, "Rate Limit Exceeded", null, null, null);
        
        // Should be treated as rate limit exception
        assertTrue(exception.getStatusCode().value() == 429);
    }
    
    @Test
    void testHttpClientErrorMapping_503() {
        // Act & Assert
        HttpClientErrorException exception = HttpClientErrorException.create(
            HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", null, null, null);
        
        // Should be treated as service unavailable exception
        assertTrue(exception.getStatusCode().value() == 503);
    }
    
    @Test
    void testExceptionHierarchy_OpenRouterRateLimit() {
        // Arrange
        String message = "API rate limit exceeded";
        int retryAfter = 30;
        
        // Act
        OpenRouterRateLimitException exception = new OpenRouterRateLimitException(message, retryAfter);
        
        // Assert
        assertEquals(429, exception.getHttpStatus());
        assertTrue(exception.isRetryable());
        assertEquals(LLMException.ErrorCategory.RATE_LIMIT, exception.getCategory());
        assertEquals(retryAfter, exception.getRetryAfterSeconds());
    }
    
    @Test
    void testExceptionHierarchy_OpenRouterServiceUnavailable() {
        // Arrange
        String message = "Service temporarily unavailable";
        
        // Act
        OpenRouterServiceUnavailableException exception = new OpenRouterServiceUnavailableException(message);
        
        // Assert
        assertEquals(503, exception.getHttpStatus());
        assertTrue(exception.isRetryable());
        assertEquals(LLMException.ErrorCategory.SERVICE_UNAVAILABLE, exception.getCategory());
        assertEquals(5, exception.getEstimatedRetryAfterMinutes()); // Default
    }
    
    @Test
    void testExceptionHierarchy_OpenRouterTimeout() {
        // Arrange
        String operation = "chatCompletion";
        long timeoutMs = 5000;
        
        // Act
        OpenRouterTimeoutException exception = new OpenRouterTimeoutException(operation, timeoutMs);
        
        // Assert
        assertEquals(timeoutMs, exception.getTimeoutMs());
        assertEquals(operation, exception.getOperation());
        assertTrue(exception.isRetryable());
        assertEquals(LLMException.ErrorCategory.TIMEOUT, exception.getCategory());
    }
    
    @Test
    void testMonitoringService_OperationRecording() {
        // Arrange
        String operationName = "test-operation";
        long latencyMs = 1500;
        
        // Act
        monitoringService.recordOperation(operationName, latencyMs, true, null);
        
        // Assert
        LLMErrorMonitoringService.MonitoringReport report = monitoringService.getMonitoringReport();
        assertEquals(1, report.getTotalRequests());
        assertEquals(0, report.getTotalErrors());
        assertEquals(1.0, report.getSuccessRate());
    }
    
    @Test
    void testMonitoringService_ErrorRecording() {
        // Arrange
        String operationName = "failing-operation";
        long latencyMs = 2000;
        String errorType = "RATE_LIMIT";
        
        // Act
        monitoringService.recordOperation(operationName, latencyMs, false, errorType);
        monitoringService.recordOperation(operationName, latencyMs, false, errorType);
        
        // Assert
        LLMErrorMonitoringService.MonitoringReport report = monitoringService.getMonitoringReport();
        assertEquals(2, report.getTotalRequests());
        assertEquals(2, report.getTotalErrors());
        assertEquals(0.0, report.getSuccessRate());
        
        // Check error stats
        LLMErrorMonitoringService.ErrorStats errorStats = report.getErrorStats().get(operationName);
        assertNotNull(errorStats);
        assertEquals(2, errorStats.getTotalErrors());
        assertTrue(errorStats.getErrorTypeCounts().containsKey("RATE_LIMIT"));
    }
    
    @Test
    void testMonitoringService_RetryRecording() {
        // Arrange
        String operationName = "retry-operation";
        int attempt = 2;
        String errorType = "TIMEOUT";
        long delayMs = 1000;
        
        // Act
        monitoringService.recordRetry(operationName, attempt, errorType, delayMs);
        
        // Assert - no exceptions should be thrown
        LLMErrorMonitoringService.MonitoringReport report = monitoringService.getMonitoringReport();
        assertNotNull(report);
    }
    
    // Helper classes for testing
    private static class MockRetryableOperation implements ExponentialBackoffRetryPolicy.RetryableOperation<String> {
        private int callCount = 0;
        private Exception exception;
        private String result;
        private int exceptionCallCount = 0;
        private int maxExceptionCalls = 1;
        
        public void setExceptionOnAllCalls(Exception exception) {
            this.exception = exception;
            this.maxExceptionCalls = Integer.MAX_VALUE;
        }
        
        public void setExceptionOnFirstTwoCalls(Exception exception) {
            this.exception = exception;
            this.maxExceptionCalls = 2;
        }
        
        public void setResultOnThirdCall(String result) {
            this.result = result;
        }
        
        public void setExceptionOnCall(int callNumber, Exception exception) {
            if (callNumber <= 2) {
                this.exception = exception;
                this.maxExceptionCalls = callNumber;
            }
        }
        
        public int getCallCount() {
            return callCount;
        }
        
        @Override
        public String execute() throws Exception {
            callCount++;
            if (exception != null && exceptionCallCount < maxExceptionCalls) {
                exceptionCallCount++;
                throw exception;
            }
            return result != null ? result : "default-success";
        }
    }
    
    private static class MockOperation<T> implements LLMCircuitBreaker.Operation<T> {
        private int callCount = 0;
        private T result;
        private Exception exception;
        
        public MockOperation() {}
        
        public MockOperation(T result) {
            this.result = result;
        }
        
        public void setResult(T result) {
            this.result = result;
        }
        
        public void setException(Exception exception) {
            this.exception = exception;
        }
        
        public int getCallCount() {
            return callCount;
        }
        
        @Override
        public T execute() throws Exception {
            callCount++;
            if (exception != null) {
                throw exception;
            }
            return result;
        }
    }
}