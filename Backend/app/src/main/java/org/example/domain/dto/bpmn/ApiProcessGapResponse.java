package org.example.domain.dto.bpmn;

import org.example.domain.entities.ApiProcessGap;

import java.time.LocalDateTime;

/**
 * Response DTO for ApiProcessGap entity
 */
public class ApiProcessGapResponse {
    private String gapId;
    private String gapType;
    private String description;
    private String processElement;
    private String missingApi;
    private String suggestedApi;
    private String impact;
    private String recommendation;
    private LocalDateTime detectedAt;

    public ApiProcessGapResponse() {}

    public ApiProcessGapResponse(String gapId, String gapType, String description,
                               String processElement, String missingApi, String suggestedApi,
                               String impact, String recommendation, LocalDateTime detectedAt) {
        this.gapId = gapId;
        this.gapType = gapType;
        this.description = description;
        this.processElement = processElement;
        this.missingApi = missingApi;
        this.suggestedApi = suggestedApi;
        this.impact = impact;
        this.recommendation = recommendation;
        this.detectedAt = detectedAt;
    }

    /**
     * Creates a response from an ApiProcessGap domain object
     */
    public static ApiProcessGapResponse fromDomain(ApiProcessGap gap) {
        return new ApiProcessGapResponse(
            gap.getGapId(),
            gap.getGapType(),
            gap.getDescription(),
            gap.getProcessElement(),
            gap.getMissingApi(),
            gap.getSuggestedApi(),
            gap.getImpact(),
            gap.getRecommendation(),
            gap.getDetectedAt()
        );
    }

    // Getters and setters
    public String getGapId() { return gapId; }
    public void setGapId(String gapId) { this.gapId = gapId; }

    public String getGapType() { return gapType; }
    public void setGapType(String gapType) { this.gapType = gapType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProcessElement() { return processElement; }
    public void setProcessElement(String processElement) { this.processElement = processElement; }

    public String getMissingApi() { return missingApi; }
    public void setMissingApi(String missingApi) { this.missingApi = missingApi; }

    public String getSuggestedApi() { return suggestedApi; }
    public void setSuggestedApi(String suggestedApi) { this.suggestedApi = suggestedApi; }

    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    // Utility methods
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
}