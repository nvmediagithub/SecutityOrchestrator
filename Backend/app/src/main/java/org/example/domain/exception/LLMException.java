package org.example.domain.exception;

/**
 * Base exception for all LLM-related operations
 */
public class LLMException extends SecurityOrchestratorException {
    
    private final String provider;
    private final ErrorCategory category;
    private final boolean retryable;
    
    public LLMException(String message) {
        super("LLM_ERROR", message);
        this.provider = "UNKNOWN";
        this.category = ErrorCategory.UNKNOWN;
        this.retryable = false;
    }
    
    public LLMException(String message, Throwable cause) {
        super("LLM_ERROR", message, cause);
        this.provider = "UNKNOWN";
        this.category = ErrorCategory.UNKNOWN;
        this.retryable = false;
    }
    
    public LLMException(String errorCode, String message, String provider, ErrorCategory category, boolean retryable) {
        super(errorCode, message);
        this.provider = provider;
        this.category = category;
        this.retryable = retryable;
    }
    
    public LLMException(String errorCode, String message, Throwable cause, String provider, ErrorCategory category, boolean retryable) {
        super(errorCode, message, cause);
        this.provider = provider;
        this.category = category;
        this.retryable = retryable;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public ErrorCategory getCategory() {
        return category;
    }
    
    public boolean isRetryable() {
        return retryable;
    }
    
    @Override
    public String toString() {
        return String.format("LLMException[code=%s, provider=%s, category=%s, retryable=%s, message=%s]", 
                getErrorCode(), provider, category, retryable, getMessage());
    }
    
    /**
     * Categories of LLM errors
     */
    public enum ErrorCategory {
        CONFIGURATION,      // Configuration errors
        AUTHENTICATION,     // API key/authentication issues
        RATE_LIMIT,         // Rate limiting (429 errors)
        NETWORK,           // Network connectivity issues
        TIMEOUT,           // Request timeouts
        SERVICE_UNAVAILABLE, // Service down (503 errors)
        QUOTA_EXCEEDED,    // Usage limits exceeded
        MODEL_UNAVAILABLE, // Model not available
        INVALID_REQUEST,   // Malformed requests
        PARSING,          // Response parsing errors
        INTERNAL_ERROR,   // Provider internal errors
        UNKNOWN           // Uncategorized errors
    }
}