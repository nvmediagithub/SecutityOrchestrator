package org.example.features.openapi.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Domain entity representing a summary of OpenAPI specification validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationSummary {
    private int totalChecks;
    private int passedChecks;
    private int failedChecks;
    private int warningChecks;
    private long validationTimeMs;

    /**
     * Calculates the success rate as a percentage
     */
    public double getSuccessRate() {
        if (totalChecks == 0) return 0.0;
        return (double) passedChecks / totalChecks * 100.0;
    }

    /**
     * Checks if validation passed with acceptable thresholds
     */
    public boolean isAcceptable() {
        return failedChecks == 0 && getSuccessRate() >= 80.0;
    }
}