package org.example.features.openapi.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.features.openapi.domain.entities.ValidationError;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenApiValidationErrorResponse(
    String path,
    String message,
    String severity,
    String errorType,
    Integer lineNumber,
    Integer columnNumber
) {

    public static OpenApiValidationErrorResponse from(ValidationError error) {
        if (error == null) {
            return null;
        }
        return new OpenApiValidationErrorResponse(
            error.getPath(),
            error.getMessage(),
            error.getSeverity(),
            error.getErrorType(),
            error.getLineNumber(),
            error.getColumnNumber()
        );
    }
}
