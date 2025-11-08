package org.example.domain.exception;

/**
 * Исключение для ошибок проверки консистентности
 */
public class ConsistencyCheckException extends SecurityOrchestratorException {
    
    public ConsistencyCheckException(String message) {
        super("CONSISTENCY_CHECK_ERROR", message);
    }
    
    public ConsistencyCheckException(String message, Throwable cause) {
        super("CONSISTENCY_CHECK_ERROR", message, cause);
    }
    
    public ConsistencyCheckException(String checkType, String message, String... parameters) {
        super("CONSISTENCY_CHECK_ERROR_" + checkType, message, parameters);
    }
    
    public ConsistencyCheckException(String checkType, String message, Throwable cause, String... parameters) {
        super("CONSISTENCY_CHECK_ERROR_" + checkType, message, cause, parameters);
    }
}