package org.example.domain.dto.bpmn;

import org.example.domain.dto.bpmn.ApiProcessGapResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for gaps between BPMN processes and APIs
 * Used for GET /api/analysis/bpmn/{diagramId}/gaps endpoint
 */
public class BpmnGapsResponse {
    
    private String diagramId;
    private String apiSpecId;
    private String analysisId;
    private List<ProcessGap> gaps;
    private GapSummary summary;
    private Map<String, Object> analysisMetadata;
    private LocalDateTime analyzedAt;
    private String analysisMethod; // "auto", "manual", "hybrid"
    private int totalGaps;
    private int criticalGaps;
    private int highPriorityGaps;
    private int mediumPriorityGaps;
    private int lowPriorityGaps;
    private List<String> recommendations;
    private List<String> warnings;
    
    public BpmnGapsResponse() {}
    
    public BpmnGapsResponse(String diagramId, String apiSpecId, String analysisId, List<ProcessGap> gaps,
                           GapSummary summary, Map<String, Object> analysisMetadata, LocalDateTime analyzedAt,
                           String analysisMethod, int totalGaps, int criticalGaps, int highPriorityGaps,
                           int mediumPriorityGaps, int lowPriorityGaps, List<String> recommendations, List<String> warnings) {
        this.diagramId = diagramId;
        this.apiSpecId = apiSpecId;
        this.analysisId = analysisId;
        this.gaps = gaps;
        this.summary = summary;
        this.analysisMetadata = analysisMetadata;
        this.analyzedAt = analyzedAt;
        this.analysisMethod = analysisMethod;
        this.totalGaps = totalGaps;
        this.criticalGaps = criticalGaps;
        this.highPriorityGaps = highPriorityGaps;
        this.mediumPriorityGaps = mediumPriorityGaps;
        this.lowPriorityGaps = lowPriorityGaps;
        this.recommendations = recommendations;
        this.warnings = warnings;
    }
    
    /**
     * Creates a mock response for demonstration
     */
    public static BpmnGapsResponse createMock(String diagramId, String apiSpecId) {
        BpmnGapsResponse response = new BpmnGapsResponse();
        response.setDiagramId(diagramId);
        response.setApiSpecId(apiSpecId);
        response.setAnalysisId("gap_analysis_" + System.currentTimeMillis());
        response.setGaps(createMockGaps());
        response.setSummary(createMockSummary());
        response.setAnalysisMetadata(Map.of(
            "totalProcessElements", 25,
            "analyzedApiEndpoints", 12,
            "analysisConfidence", 0.87,
            "processingTimeMs", 3240
        ));
        response.setAnalyzedAt(LocalDateTime.now().minusHours(1));
        response.setAnalysisMethod("auto");
        response.setTotalGaps(8);
        response.setCriticalGaps(2);
        response.setHighPriorityGaps(3);
        response.setMediumPriorityGaps(2);
        response.setLowPriorityGaps(1);
        response.setRecommendations(createMockRecommendations());
        response.setWarnings(List.of(
            "Some critical gaps may impact business processes",
            "API documentation may be incomplete for certain endpoints"
        ));
        return response;
    }
    
    public static class ProcessGap {
        private String gapId;
        private String gapType; // "missing_endpoint", "incorrect_mapping", "data_mismatch", "performance_gap"
        private String description;
        private String processElement;
        private String processElementType; // "UserTask", "ServiceTask", "Gateway", etc.
        private String missingApi;
        private String suggestedApi;
        private String impact; // "HIGH", "MEDIUM", "LOW", "CRITICAL"
        private String severity; // "CRITICAL", "HIGH", "MEDIUM", "LOW"
        private String recommendation;
        private LocalDateTime detectedAt;
        private Map<String, Object> gapData;
        private List<String> affectedFlows;
        private String confidence;
        
        public ProcessGap() {}
        
        public ProcessGap(String gapId, String gapType, String description, String processElement,
                         String processElementType, String missingApi, String suggestedApi, String impact,
                         String severity, String recommendation, LocalDateTime detectedAt,
                         Map<String, Object> gapData, List<String> affectedFlows, String confidence) {
            this.gapId = gapId;
            this.gapType = gapType;
            this.description = description;
            this.processElement = processElement;
            this.processElementType = processElementType;
            this.missingApi = missingApi;
            this.suggestedApi = suggestedApi;
            this.impact = impact;
            this.severity = severity;
            this.recommendation = recommendation;
            this.detectedAt = detectedAt;
            this.gapData = gapData;
            this.affectedFlows = affectedFlows;
            this.confidence = confidence;
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
        
        public String getProcessElementType() { return processElementType; }
        public void setProcessElementType(String processElementType) { this.processElementType = processElementType; }
        
        public String getMissingApi() { return missingApi; }
        public void setMissingApi(String missingApi) { this.missingApi = missingApi; }
        
        public String getSuggestedApi() { return suggestedApi; }
        public void setSuggestedApi(String suggestedApi) { this.suggestedApi = suggestedApi; }
        
        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public LocalDateTime getDetectedAt() { return detectedAt; }
        public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
        
        public Map<String, Object> getGapData() { return gapData; }
        public void setGapData(Map<String, Object> gapData) { this.gapData = gapData; }
        
        public List<String> getAffectedFlows() { return affectedFlows; }
        public void setAffectedFlows(List<String> affectedFlows) { this.affectedFlows = affectedFlows; }
        
        public String getConfidence() { return confidence; }
        public void setConfidence(String confidence) { this.confidence = confidence; }
        
        // Utility methods
        public boolean isCritical() {
            return "CRITICAL".equals(severity);
        }
        
        public boolean isHighPriority() {
            return "HIGH".equals(severity);
        }
        
        public boolean isMissingEndpoint() {
            return "missing_endpoint".equalsIgnoreCase(gapType);
        }
        
        public boolean isDataMismatch() {
            return "data_mismatch".equalsIgnoreCase(gapType);
        }
    }
    
    public static class GapSummary {
        private int totalGaps;
        private int criticalGaps;
        private int highPriorityGaps;
        private int mediumPriorityGaps;
        private int lowPriorityGaps;
        private int missingEndpoints;
        private int incorrectMappings;
        private int dataMismatches;
        private int performanceGaps;
        private double averageConfidence;
        private List<String> gapTypes;
        private List<String> mostAffectedProcessElements;
        
        public GapSummary() {}
        
        public GapSummary(int totalGaps, int criticalGaps, int highPriorityGaps, int mediumPriorityGaps,
                         int lowPriorityGaps, int missingEndpoints, int incorrectMappings, int dataMismatches,
                         int performanceGaps, double averageConfidence, List<String> gapTypes,
                         List<String> mostAffectedProcessElements) {
            this.totalGaps = totalGaps;
            this.criticalGaps = criticalGaps;
            this.highPriorityGaps = highPriorityGaps;
            this.mediumPriorityGaps = mediumPriorityGaps;
            this.lowPriorityGaps = lowPriorityGaps;
            this.missingEndpoints = missingEndpoints;
            this.incorrectMappings = incorrectMappings;
            this.dataMismatches = dataMismatches;
            this.performanceGaps = performanceGaps;
            this.averageConfidence = averageConfidence;
            this.gapTypes = gapTypes;
            this.mostAffectedProcessElements = mostAffectedProcessElements;
        }
        
        // Getters and setters
        public int getTotalGaps() { return totalGaps; }
        public void setTotalGaps(int totalGaps) { this.totalGaps = totalGaps; }
        
        public int getCriticalGaps() { return criticalGaps; }
        public void setCriticalGaps(int criticalGaps) { this.criticalGaps = criticalGaps; }
        
        public int getHighPriorityGaps() { return highPriorityGaps; }
        public void setHighPriorityGaps(int highPriorityGaps) { this.highPriorityGaps = highPriorityGaps; }
        
        public int getMediumPriorityGaps() { return mediumPriorityGaps; }
        public void setMediumPriorityGaps(int mediumPriorityGaps) { this.mediumPriorityGaps = mediumPriorityGaps; }
        
        public int getLowPriorityGaps() { return lowPriorityGaps; }
        public void setLowPriorityGaps(int lowPriorityGaps) { this.lowPriorityGaps = lowPriorityGaps; }
        
        public int getMissingEndpoints() { return missingEndpoints; }
        public void setMissingEndpoints(int missingEndpoints) { this.missingEndpoints = missingEndpoints; }
        
        public int getIncorrectMappings() { return incorrectMappings; }
        public void setIncorrectMappings(int incorrectMappings) { this.incorrectMappings = incorrectMappings; }
        
        public int getDataMismatches() { return dataMismatches; }
        public void setDataMismatches(int dataMismatches) { this.dataMismatches = dataMismatches; }
        
        public int getPerformanceGaps() { return performanceGaps; }
        public void setPerformanceGaps(int performanceGaps) { this.performanceGaps = performanceGaps; }
        
        public double getAverageConfidence() { return averageConfidence; }
        public void setAverageConfidence(double averageConfidence) { this.averageConfidence = averageConfidence; }
        
        public List<String> getGapTypes() { return gapTypes; }
        public void setGapTypes(List<String> gapTypes) { this.gapTypes = gapTypes; }
        
        public List<String> getMostAffectedProcessElements() { return mostAffectedProcessElements; }
        public void setMostAffectedProcessElements(List<String> mostAffectedProcessElements) { this.mostAffectedProcessElements = mostAffectedProcessElements; }
    }
    
    // Utility methods
    public boolean hasCriticalGaps() {
        return criticalGaps > 0;
    }
    
    public boolean hasHighPriorityGaps() {
        return highPriorityGaps > 0;
    }
    
    public boolean hasMissingEndpoints() {
        return summary != null && summary.getMissingEndpoints() > 0;
    }
    
    public boolean isHighQualityAnalysis() {
        return summary != null && summary.getAverageConfidence() >= 0.8;
    }
    
    private static List<ProcessGap> createMockGaps() {
        return List.of(
            new ProcessGap(
                "gap_001", "missing_endpoint", 
                "User registration process requires email verification API but none found",
                "Email Verification Task", "ServiceTask", "/api/email/verify", "/api/email/verify-send",
                "HIGH", "CRITICAL", "Implement email verification endpoint or use external service",
                LocalDateTime.now().minusHours(2), Map.of("elementId", "task_003"), 
                List.of("user_registration_flow"), "0.95"
            ),
            new ProcessGap(
                "gap_002", "data_mismatch",
                "User data structure mismatch between process and API",
                "User Data Processing", "ServiceTask", "/api/users/create", "/api/users/register",
                "MEDIUM", "HIGH", "Align data schemas or implement data transformation layer",
                LocalDateTime.now().minusHours(1), Map.of("elementId", "task_007"),
                List.of("user_management_flow", "data_sync_flow"), "0.82"
            ),
            new ProcessGap(
                "gap_003", "performance_gap",
                "Payment processing timeout may occur with current API limits",
                "Payment Processing", "ServiceTask", null, "/api/payments/optimize",
                "LOW", "MEDIUM", "Implement async payment processing or increase timeout limits",
                LocalDateTime.now().minusMinutes(30), Map.of("elementId", "task_012"),
                List.of("payment_flow", "order_processing_flow"), "0.75"
            )
        );
    }
    
    private static GapSummary createMockSummary() {
        return new GapSummary(8, 2, 3, 2, 1, 3, 2, 2, 1, 0.84,
                            List.of("missing_endpoint", "data_mismatch", "performance_gap", "incorrect_mapping"),
                            List.of("User Registration Task", "Payment Processing", "Email Verification Task"));
    }
    
    private static List<String> createMockRecommendations() {
        return List.of(
            "Implement missing email verification API endpoint",
            "Review and update data transformation mappings",
            "Consider implementing async processing for long-running operations",
            "Add proper error handling and retry mechanisms",
            "Update API documentation to reflect actual implementation"
        );
    }
    
    // Getters and setters
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getApiSpecId() { return apiSpecId; }
    public void setApiSpecId(String apiSpecId) { this.apiSpecId = apiSpecId; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public List<ProcessGap> getGaps() { return gaps; }
    public void setGaps(List<ProcessGap> gaps) { this.gaps = gaps; }
    
    public GapSummary getSummary() { return summary; }
    public void setSummary(GapSummary summary) { this.summary = summary; }
    
    public Map<String, Object> getAnalysisMetadata() { return analysisMetadata; }
    public void setAnalysisMetadata(Map<String, Object> analysisMetadata) { this.analysisMetadata = analysisMetadata; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public String getAnalysisMethod() { return analysisMethod; }
    public void setAnalysisMethod(String analysisMethod) { this.analysisMethod = analysisMethod; }
    
    public int getTotalGaps() { return totalGaps; }
    public void setTotalGaps(int totalGaps) { this.totalGaps = totalGaps; }
    
    public int getCriticalGaps() { return criticalGaps; }
    public void setCriticalGaps(int criticalGaps) { this.criticalGaps = criticalGaps; }
    
    public int getHighPriorityGaps() { return highPriorityGaps; }
    public void setHighPriorityGaps(int highPriorityGaps) { this.highPriorityGaps = highPriorityGaps; }
    
    public int getMediumPriorityGaps() { return mediumPriorityGaps; }
    public void setMediumPriorityGaps(int mediumPriorityGaps) { this.mediumPriorityGaps = mediumPriorityGaps; }
    
    public int getLowPriorityGaps() { return lowPriorityGaps; }
    public void setLowPriorityGaps(int lowPriorityGaps) { this.lowPriorityGaps = lowPriorityGaps; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
}