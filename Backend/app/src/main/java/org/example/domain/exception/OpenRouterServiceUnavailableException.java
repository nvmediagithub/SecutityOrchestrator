package org.example.domain.exception;

/**
 * Exception for OpenRouter API service unavailable (503) errors
 */
public class OpenRouterServiceUnavailableException extends OpenRouterException {
    
    private final int estimatedRetryAfterMinutes;
    
    public OpenRouterServiceUnavailableException(String message) {
        super("OPENROUTER_SERVICE_UNAVAILABLE", message, "OpenRouter", 
              LLMException.ErrorCategory.SERVICE_UNAVAILABLE, true, 503, null);
        this.estimatedRetryAfterMinutes = 5;
    }
    
    public OpenRouterServiceUnavailableException(String message, int estimatedRetryAfterMinutes) {
        super("OPENROUTER_SERVICE_UNAVAILABLE", message, "OpenRouter", 
              LLMException.ErrorCategory.SERVICE_UNAVAILABLE, true, 503, null);
        this.estimatedRetryAfterMinutes = estimatedRetryAfterMinutes;
    }
    
    public OpenRouterServiceUnavailableException(String message, Throwable cause) {
        super("OPENROUTER_SERVICE_UNAVAILABLE", message, cause, "OpenRouter", 
              LLMException.ErrorCategory.SERVICE_UNAVAILABLE, true, 503, null);
        this.estimatedRetryAfterMinutes = 5;
    }
    
    public OpenRouterServiceUnavailableException(String message, Throwable cause, int estimatedRetryAfterMinutes) {
        super("OPENROUTER_SERVICE_UNAVAILABLE", message, cause, "OpenRouter", 
              LLMException.ErrorCategory.SERVICE_UNAVAILABLE, true, 503, null);
        this.estimatedRetryAfterMinutes = estimatedRetryAfterMinutes;
    }
    
    public int getEstimatedRetryAfterMinutes() {
        return estimatedRetryAfterMinutes;
    }
    
    @Override
    public String toString() {
        return String.format("OpenRouterServiceUnavailableException[message=%s, retryAfter=%dmin]", 
                getMessage(), estimatedRetryAfterMinutes);
    }
}