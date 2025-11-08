package org.example.domain.valueobjects;

/**
 * Enumeration of analysis statuses for OpenAPI analysis
 */
public enum AnalysisStatus {
    PENDING("Analysis is queued and waiting to start"),
    IN_PROGRESS("Analysis is currently running"),
    COMPLETED("Analysis has completed successfully"),
    FAILED("Analysis failed with errors"),
    CANCELLED("Analysis was cancelled by user"),
    TIMEOUT("Analysis timed out"),
    REVIEW_REQUIRED("Analysis completed but requires manual review");

    private final String description;

    AnalysisStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == TIMEOUT || this == REVIEW_REQUIRED;
    }

    public boolean isInProgress() {
        return this == IN_PROGRESS || this == PENDING;
    }
}