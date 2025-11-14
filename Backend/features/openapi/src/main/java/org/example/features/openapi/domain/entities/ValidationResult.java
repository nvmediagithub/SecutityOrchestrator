package org.example.features.openapi.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * Domain entity representing the result of OpenAPI specification validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    private boolean valid;
    private List<ValidationError> errors;
    private ValidationSummary summary;

    /**
     * Checks if validation passed without errors
     */
    public boolean isValid() {
        return valid && (errors == null || errors.isEmpty());
    }

    /**
     * Gets the count of validation errors
     */
    public int getErrorCount() {
        return errors != null ? errors.size() : 0;
    }
}