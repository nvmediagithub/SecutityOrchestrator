package org.example.domain.exception;

/**
 * Базовое исключение для Security Orchestrator
 */
public class SecurityOrchestratorException extends RuntimeException {
    
    private final String errorCode;
    private final String[] parameters;
    
    public SecurityOrchestratorException(String message) {
        super(message);
        this.errorCode = "SECURITY_ORCHESTRATOR_ERROR";
        this.parameters = new String[0];
    }
    
    public SecurityOrchestratorException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SECURITY_ORCHESTRATOR_ERROR";
        this.parameters = new String[0];
    }
    
    public SecurityOrchestratorException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = new String[0];
    }
    
    public SecurityOrchestratorException(String errorCode, String message, String... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }
    
    public SecurityOrchestratorException(String errorCode, String message, Throwable cause, String... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String[] getParameters() {
        return parameters;
    }
    
    @Override
    public String toString() {
        return String.format("SecurityOrchestratorException[code=%s, message=%s]", errorCode, getMessage());
    }
}