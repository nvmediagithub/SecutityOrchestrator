package org.example.domain.exception;

/**
 * Exception for OpenRouter API timeout errors
 */
public class OpenRouterTimeoutException extends OpenRouterException {
    
    private final long timeoutMs;
    private final String operation;
    
    public OpenRouterTimeoutException(String operation, long timeoutMs) {
        super("OPENROUTER_TIMEOUT", String.format("Operation '%s' timed out after %dms", operation, timeoutMs), 
              "OpenRouter", LLMException.ErrorCategory.TIMEOUT, true, 0, null);
        this.timeoutMs = timeoutMs;
        this.operation = operation;
    }
    
    public OpenRouterTimeoutException(String operation, long timeoutMs, Throwable cause) {
        super("OPENROUTER_TIMEOUT", String.format("Operation '%s' timed out after %dms", operation, timeoutMs), 
              cause, "OpenRouter", LLMException.ErrorCategory.TIMEOUT, true, 0, null);
        this.timeoutMs = timeoutMs;
        this.operation = operation;
    }
    
    public long getTimeoutMs() {
        return timeoutMs;
    }
    
    public String getOperation() {
        return operation;
    }
    
    @Override
    public String toString() {
        return String.format("OpenRouterTimeoutException[operation=%s, timeout=%dms]", operation, timeoutMs);
    }
}