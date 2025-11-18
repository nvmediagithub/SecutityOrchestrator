package org.example.features.openapi.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Domain entity representing a single validation error in OpenAPI specification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {
    private String path;
    private String message;
    private String severity;
    private String errorType;
    private Integer lineNumber;
    private Integer columnNumber;

    /**
     * Creates a new validation error with specified path and message
     */
    public static ValidationError of(String path, String message) {
        return ValidationError.builder()
                .path(path)
                .message(message)
                .severity("ERROR")
                .build();
    }

    /**
     * Creates a warning validation error
     */
    public static ValidationError warning(String path, String message) {
        return ValidationError.builder()
                .path(path)
                .message(message)
                .severity("WARNING")
                .build();
    }
}