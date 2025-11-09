package org.example.infrastructure.services.llm;

import org.example.domain.exception.LLMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Circuit Breaker implementation for LLM services
 * Provides fail-fast behavior and automatic recovery
 */
public class LLMCircuitBreaker {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMCircuitBreaker.class);
    
    private final String name;
    private final Config config;
    
    // State management
    private volatile State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicReference<Instant> lastStateChange = new AtomicReference<>(Instant.now());
    
    public LLMCircuitBreaker(String name) {
        this(name, Config.defaultConfig());
    }
    
    public LLMCircuitBreaker(String name, Config config) {
        this.name = name;
        this.config = config;
        logger.info("Circuit breaker '{}' created with config: failureThreshold={}, timeout={}, successThreshold={}", 
                   name, config.failureThreshold, config.timeout, config.successThreshold);
    }
    
    /**
     * Execute operation with circuit breaker protection
     */
    public <T> ExecutionResult<T> executeOperation(Operation<T> operation) throws Exception {
        State currentState = state;
        
        switch (currentState) {
            case CLOSED:
                return executeInClosedState(operation);
            case OPEN:
                return executeInOpenState(operation);
            case HALF_OPEN:
                return executeInHalfOpenState(operation);
            default:
                throw new IllegalStateException("Unknown circuit breaker state: " + currentState);
        }
    }
    
    /**
     * Execute operation in CLOSED state (normal operation)
     */
    private <T> ExecutionResult<T> executeInClosedState(Operation<T> operation) throws Exception {
        try {
            T result = operation.execute();
            onSuccess();
            return new ExecutionResult<>(result, state, false);
        } catch (Exception e) {
            onFailure(e);
            return new ExecutionResult<>(null, state, true);
        }
    }
    
    /**
     * Execute operation in OPEN state (fail fast)
     */
    private <T> ExecutionResult<T> executeInOpenState(Operation<T> operation) throws Exception {
        if (shouldAttemptReset()) {
            logger.info("Circuit breaker '{}' transitioning from OPEN to HALF_OPEN", name);
            transitionToHalfOpen();
            return executeInHalfOpenState(operation);
        }
        
        // Still open, fail fast
        Duration timeInOpen = Duration.between(lastStateChange.get().plus(config.timeout), Instant.now());
        logger.warn("Circuit breaker '{}' is OPEN, failing fast. Time in open state: {}", name, timeInOpen);
        
        throw new LLMCircuitBreakerOpenException(
            String.format("Circuit breaker '%s' is OPEN, failing fast for %s", name, config.timeout), 
            name, state, timeInOpen);
    }
    
    /**
     * Execute operation in HALF_OPEN state (testing recovery)
     */
    private <T> ExecutionResult<T> executeInHalfOpenState(Operation<T> operation) throws Exception {
        try {
            T result = operation.execute();
            onSuccessInHalfOpen();
            return new ExecutionResult<>(result, state, false);
        } catch (Exception e) {
            onFailureInHalfOpen(e);
            return new ExecutionResult<>(null, state, true);
        }
    }
    
    /**
     * Handle successful operation
     */
    private void onSuccess() {
        failureCount.set(0);
        if (logger.isDebugEnabled()) {
            logger.debug("Circuit breaker '{}' recorded success", name);
        }
    }
    
    /**
     * Handle successful operation in HALF_OPEN state
     */
    private void onSuccessInHalfOpen() {
        successCount.incrementAndGet();
        
        if (successCount.get() >= config.successThreshold) {
            logger.info("Circuit breaker '{}' transitioning from HALF_OPEN to CLOSED after {} successful operations", 
                       name, successCount.get());
            transitionToClosed();
        } else {
            logger.debug("Circuit breaker '{}' recorded success in HALF_OPEN state ({}/{})", 
                        name, successCount.get(), config.successThreshold);
        }
    }
    
    /**
     * Handle failed operation
     */
    private void onFailure(Exception exception) {
        int currentFailures = failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        logger.warn("Circuit breaker '{}' recorded failure {} (threshold: {})", 
                   name, currentFailures, config.failureThreshold, exception);
        
        if (currentFailures >= config.failureThreshold) {
            logger.error("Circuit breaker '{}' failure threshold reached, transitioning to OPEN", name);
            transitionToOpen();
        }
    }
    
    /**
     * Handle failed operation in HALF_OPEN state
     */
    private void onFailureInHalfOpen(Exception exception) {
        logger.error("Circuit breaker '{}' recorded failure in HALF_OPEN state, transitioning back to OPEN", 
                    name, exception);
        transitionToOpen();
    }
    
    /**
     * Check if circuit breaker should attempt reset
     */
    private boolean shouldAttemptReset() {
        return System.currentTimeMillis() - lastFailureTime.get() >= config.timeout.toMillis();
    }
    
    /**
     * Transition to CLOSED state
     */
    private void transitionToClosed() {
        state = State.CLOSED;
        lastStateChange.set(Instant.now());
        failureCount.set(0);
        successCount.set(0);
        logger.info("Circuit breaker '{}' is now CLOSED", name);
    }
    
    /**
     * Transition to OPEN state
     */
    private void transitionToOpen() {
        state = State.OPEN;
        lastStateChange.set(Instant.now());
        lastFailureTime.set(System.currentTimeMillis());
        logger.warn("Circuit breaker '{}' is now OPEN", name);
    }
    
    /**
     * Transition to HALF_OPEN state
     */
    private void transitionToHalfOpen() {
        state = State.HALF_OPEN;
        lastStateChange.set(Instant.now());
        successCount.set(0);
        logger.info("Circuit breaker '{}' is now HALF_OPEN", name);
    }
    
    /**
     * Get current state information
     */
    public StateInfo getStateInfo() {
        return new StateInfo(name, state, failureCount.get(), successCount.get(), 
                           Duration.between(lastStateChange.get(), Instant.now()),
                           System.currentTimeMillis() - lastFailureTime.get());
    }
    
    /**
     * Circuit breaker states
     */
    public enum State {
        CLOSED,    // Normal operation
        OPEN,      // Fail fast
        HALF_OPEN  // Testing recovery
    }
    
    /**
     * Configuration for circuit breaker
     */
    public static class Config {
        private final int failureThreshold;
        private final Duration timeout;
        private final int successThreshold;
        
        private Config(int failureThreshold, Duration timeout, int successThreshold) {
            this.failureThreshold = failureThreshold;
            this.timeout = timeout;
            this.successThreshold = successThreshold;
        }
        
        public static Config defaultConfig() {
            return new Config(5, Duration.ofSeconds(60), 3);
        }
        
        public static Config customConfig(int failureThreshold, Duration timeout, int successThreshold) {
            return new Config(failureThreshold, timeout, successThreshold);
        }
    }
    
    /**
     * Operation interface
     */
    @FunctionalInterface
    public interface Operation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Execution result
     */
    public static class ExecutionResult<T> {
        private final T result;
        private final State state;
        private final boolean failed;
        
        public ExecutionResult(T result, State state, boolean failed) {
            this.result = result;
            this.state = state;
            this.failed = failed;
        }
        
        public T getResult() { return result; }
        public State getState() { return state; }
        public boolean isFailed() { return failed; }
    }
    
    /**
     * State information
     */
    public static class StateInfo {
        private final String name;
        private final State state;
        private final int failureCount;
        private final int successCount;
        private final Duration timeInState;
        private final long timeSinceLastFailure;
        
        public StateInfo(String name, State state, int failureCount, int successCount, 
                        Duration timeInState, long timeSinceLastFailure) {
            this.name = name;
            this.state = state;
            this.failureCount = failureCount;
            this.successCount = successCount;
            this.timeInState = timeInState;
            this.timeSinceLastFailure = timeSinceLastFailure;
        }
        
        // Getters
        public String getName() { return name; }
        public State getState() { return state; }
        public int getFailureCount() { return failureCount; }
        public int getSuccessCount() { return successCount; }
        public Duration getTimeInState() { return timeInState; }
        public long getTimeSinceLastFailure() { return timeSinceLastFailure; }
    }
    
    /**
     * Exception thrown when circuit breaker is open
     */
    public static class LLMCircuitBreakerOpenException extends LLMException {
        private final String circuitBreakerName;
        private final LLMCircuitBreaker.State state;
        private final Duration timeInOpenState;
        
        public LLMCircuitBreakerOpenException(String message, String circuitBreakerName, 
                                            LLMCircuitBreaker.State state, Duration timeInOpenState) {
            super("CIRCUIT_BREAKER_OPEN", message, circuitBreakerName, 
                  LLMException.ErrorCategory.SERVICE_UNAVAILABLE, true);
            this.circuitBreakerName = circuitBreakerName;
            this.state = state;
            this.timeInOpenState = timeInOpenState;
        }
        
        public String getCircuitBreakerName() { return circuitBreakerName; }
        public LLMCircuitBreaker.State getState() { return state; }
        public Duration getTimeInOpenState() { return timeInOpenState; }
    }
}