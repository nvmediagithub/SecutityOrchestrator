package org.example.domain.exception;

/**
 * Base exception for OpenRouter API operations
 */
public class OpenRouterException extends LLMException {
    
    private final int httpStatus;
    private final String apiResponse;
    
    public OpenRouterException(String message) {
        super("OPENROUTER_ERROR", message, "OpenRouter", ErrorCategory.UNKNOWN, false);
        this.httpStatus = 0;
        this.apiResponse = null;
    }
    
    public OpenRouterException(String message, Throwable cause) {
        super("OPENROUTER_ERROR", message, "OpenRouter", ErrorCategory.UNKNOWN, false);
        this.httpStatus = 0;
        this.apiResponse = null;
    }
    
    public OpenRouterException(String message, String provider, LLMException.ErrorCategory category, boolean retryable) {
        super("OPENROUTER_ERROR", message, provider, category, retryable);
        this.httpStatus = 0;
        this.apiResponse = null;
    }
    
    public OpenRouterException(String errorCode, String message, String provider, 
                              LLMException.ErrorCategory category, boolean retryable, 
                              int httpStatus, String apiResponse) {
        super(errorCode, message, provider, category, retryable);
        this.httpStatus = httpStatus;
        this.apiResponse = apiResponse;
    }
    
    public OpenRouterException(String errorCode, String message, Throwable cause, String provider,
                              LLMException.ErrorCategory category, boolean retryable,
                              int httpStatus, String apiResponse) {
        super(errorCode, message, provider, category, retryable);
        this.httpStatus = httpStatus;
        this.apiResponse = apiResponse;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
    
    public String getApiResponse() {
        return apiResponse;
    }
    
    @Override
    public String toString() {
        return String.format("OpenRouterException[code=%s, status=%d, category=%s, retryable=%s, message=%s]", 
                getErrorCode(), httpStatus, getCategory(), isRetryable(), getMessage());
    }
}