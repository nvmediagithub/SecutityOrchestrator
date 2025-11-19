package org.example.features.openapi.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.features.openapi.domain.entities.ValidationSummary;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenApiValidationSummaryResponse(
    int totalChecks,
    int passedChecks,
    int failedChecks,
    int warningChecks,
    long validationTimeMs,
    double successRate
) {

    public static OpenApiValidationSummaryResponse from(ValidationSummary summary) {
        if (summary == null) {
            return null;
        }
        return new OpenApiValidationSummaryResponse(
            summary.getTotalChecks(),
            summary.getPassedChecks(),
            summary.getFailedChecks(),
            summary.getWarningChecks(),
            summary.getValidationTimeMs(),
            summary.getSuccessRate()
        );
    }
}
