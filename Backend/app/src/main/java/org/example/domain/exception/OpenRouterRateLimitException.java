package org.example.domain.exception;

/**
 * Exception for OpenRouter API rate limit (429) errors
 */
public class OpenRouterRateLimitException extends OpenRouterException {
    
    private final int retryAfterSeconds;
    private final String rateLimitPolicy;
    
    public OpenRouterRateLimitException(String message) {
        super("OPENROUTER_RATE_LIMIT", message, "OpenRouter", 
              LLMException.ErrorCategory.RATE_LIMIT, true, 429, null);
        this.retryAfterSeconds = 0;
        this.rateLimitPolicy = "unknown";
    }
    
    public OpenRouterRateLimitException(String message, int retryAfterSeconds) {
        super("OPENROUTER_RATE_LIMIT", message, "OpenRouter", 
              LLMException.ErrorCategory.RATE_LIMIT, true, 429, null);
        this.retryAfterSeconds = retryAfterSeconds;
        this.rateLimitPolicy = "retry-after";
    }
    
    public OpenRouterRateLimitException(String message, String rateLimitPolicy) {
        super("OPENROUTER_RATE_LIMIT", message, "OpenRouter", 
              LLMException.ErrorCategory.RATE_LIMIT, true, 429, null);
        this.retryAfterSeconds = 0;
        this.rateLimitPolicy = rateLimitPolicy;
    }
    
    public OpenRouterRateLimitException(String message, int retryAfterSeconds, String rateLimitPolicy, String apiResponse) {
        super("OPENROUTER_RATE_LIMIT", message, "OpenRouter", 
              LLMException.ErrorCategory.RATE_LIMIT, true, 429, apiResponse);
        this.retryAfterSeconds = retryAfterSeconds;
        this.rateLimitPolicy = rateLimitPolicy;
    }
    
    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
    
    public String getRateLimitPolicy() {
        return rateLimitPolicy;
    }
    
    @Override
    public String toString() {
        return String.format("OpenRouterRateLimitException[message=%s, retryAfter=%ds, policy=%s]", 
                getMessage(), retryAfterSeconds, rateLimitPolicy);
    }
}