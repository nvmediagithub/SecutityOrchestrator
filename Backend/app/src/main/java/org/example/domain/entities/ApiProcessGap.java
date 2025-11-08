package org.example.domain.entities;

import org.example.domain.valueobjects.BpmnProcessId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing gaps between APIs and business processes
 */
public class ApiProcessGap {
    private final String gapId;
    private final BpmnProcessId processId;
    private final String gapType;
    private final String description;
    private final String processElement;
    private final String missingApi;
    private final String suggestedApi;
    private final String impact;
    private final String recommendation;
    private final LocalDateTime detectedAt;

    public ApiProcessGap(String gapId, BpmnProcessId processId, String gapType, String description,
                        String processElement, String missingApi, String suggestedApi,
                        String impact, String recommendation, LocalDateTime detectedAt) {
        this.gapId = Objects.requireNonNull(gapId, "Gap ID cannot be null");
        this.processId = Objects.requireNonNull(processId, "Process ID cannot be null");
        this.gapType = Objects.requireNonNull(gapType, "Gap type cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.processElement = processElement;
        this.missingApi = missingApi;
        this.suggestedApi = suggestedApi;
        this.impact = impact;
        this.recommendation = recommendation;
        this.detectedAt = Objects.requireNonNull(detectedAt, "Detected timestamp cannot be null");
    }

    /**
     * Creates a new API-Process gap
     */
    public static ApiProcessGap createGap(BpmnProcessId processId, String gapType, String description) {
        return new ApiProcessGap(
            "gap-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000),
            processId, gapType, description, null, null, null, null, null, LocalDateTime.now()
        );
    }

    /**
     * Creates a detailed API-Process gap
     */
    public static ApiProcessGap createDetailedGap(BpmnProcessId processId, String gapType, String description,
                                                 String processElement, String missingApi, String suggestedApi,
                                                 String impact, String recommendation) {
        return new ApiProcessGap(
            "gap-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000),
            processId, gapType, description, processElement, missingApi, suggestedApi,
            impact, recommendation, LocalDateTime.now()
        );
    }

    // Getters
    public String getGapId() { return gapId; }
    public BpmnProcessId getProcessId() { return processId; }
    public String getGapType() { return gapType; }
    public String getDescription() { return description; }
    public String getProcessElement() { return processElement; }
    public String getMissingApi() { return missingApi; }
    public String getSuggestedApi() { return suggestedApi; }
    public String getImpact() { return impact; }
    public String getRecommendation() { return recommendation; }
    public LocalDateTime getDetectedAt() { return detectedAt; }

    // Business logic methods
    public boolean hasProcessElement() {
        return processElement != null && !processElement.trim().isEmpty();
    }

    public boolean hasMissingApi() {
        return missingApi != null && !missingApi.trim().isEmpty();
    }

    public boolean hasSuggestedApi() {
        return suggestedApi != null && !suggestedApi.trim().isEmpty();
    }

    public boolean hasImpact() {
        return impact != null && !impact.trim().isEmpty();
    }

    public boolean hasRecommendation() {
        return recommendation != null && !recommendation.trim().isEmpty();
    }

    public boolean isMissingEndpoint() {
        return "missing_endpoint".equalsIgnoreCase(gapType);
    }

    public boolean isIncorrectMapping() {
        return "incorrect_mapping".equalsIgnoreCase(gapType);
    }

    public boolean isDataMismatch() {
        return "data_mismatch".equalsIgnoreCase(gapType);
    }

    public boolean isPerformanceGap() {
        return "performance_gap".equalsIgnoreCase(gapType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiProcessGap that = (ApiProcessGap) o;
        return Objects.equals(gapId, that.gapId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gapId);
    }

    @Override
    public String toString() {
        return "ApiProcessGap{" +
                "gapId='" + gapId + '\'' +
                ", processId=" + processId +
                ", gapType='" + gapType + '\'' +
                ", description='" + description + '\'' +
                ", processElement='" + processElement + '\'' +
                '}';
    }
}