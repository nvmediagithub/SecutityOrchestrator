package org.example.domain.exception;

/**
 * Base exception class for LLM-related errors.
 * Provides structured error handling for different LLM providers and operations.
 */
public class LLMException extends Exception {

    /**
     * Error categories for LLM operations
     */
    public enum ErrorCategory {
        AUTHENTICATION,
        RATE_LIMIT,
        TIMEOUT,
        SERVICE_UNAVAILABLE,
        INVALID_REQUEST,
        INSUFFICIENT_PERMISSIONS,
        MODEL_NOT_FOUND,
        MODEL_LOADING_ERROR,
        CONFIGURATION_ERROR,
        INTERNAL_ERROR,
        NETWORK_ERROR,
        UNKNOWN
    }

    private final String errorCode;
    private final String provider;
    private final ErrorCategory category;
    private final boolean retryable;
    private final int httpStatusCode;

    public LLMException(String errorCode, String message, String provider,
                       ErrorCategory category, boolean retryable) {
        super(message);
        this.errorCode = errorCode;
        this.provider = provider;
        this.category = category;
        this.retryable = retryable;
        this.httpStatusCode = 0;
    }

    public LLMException(String errorCode, String message, String provider,
                       ErrorCategory category, boolean retryable, int httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.provider = provider;
        this.category = category;
        this.retryable = retryable;
        this.httpStatusCode = httpStatusCode;
    }

    public LLMException(String errorCode, String message, Throwable cause, String provider,
                       ErrorCategory category, boolean retryable, int httpStatusCode,
                       String apiResponse) {
        super(message, cause);
        this.errorCode = errorCode;
        this.provider = provider;
        this.category = category;
        this.retryable = retryable;
        this.httpStatusCode = httpStatusCode;
    }

    // Getters
    public String getErrorCode() {
        return errorCode;
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

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public String toString() {
        return String.format("LLMException{errorCode='%s', provider='%s', category=%s, retryable=%s, httpStatusCode=%d, message='%s'}",
                errorCode, provider, category, retryable, httpStatusCode, getMessage());
    }
}