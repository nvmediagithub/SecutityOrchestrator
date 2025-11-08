package org.example.domain.dto.bpmn;

import org.example.infrastructure.services.bpmn.BpmnAnalysisService.BpmnAnalysisStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for BPMN analysis status with progress information
 * Used for GET /api/analysis/bpmn/{diagramId} endpoint
 */
public class BpmnAnalysisStatusResponse {
    
    private String analysisId;
    private String diagramId;
    private String diagramName;
    private String status;
    private String message;
    private LocalDateTime startedAt;
    private LocalDateTime lastUpdated;
    private LocalDateTime estimatedCompletion;
    private LocalDateTime completedAt;
    private ProgressInfo progress;
    private List<AnalysisStep> analysisSteps;
    private Map<String, Object> analysisMetadata;
    private String errorCode;
    private String errorMessage;
    private List<String> warnings;
    
    public BpmnAnalysisStatusResponse() {}
    
    public BpmnAnalysisStatusResponse(String analysisId, String diagramId, String diagramName, 
                                     String status, String message, LocalDateTime startedAt,
                                     LocalDateTime lastUpdated, LocalDateTime estimatedCompletion,
                                     LocalDateTime completedAt, ProgressInfo progress,
                                     List<AnalysisStep> analysisSteps, Map<String, Object> analysisMetadata,
                                     String errorCode, String errorMessage, List<String> warnings) {
        this.analysisId = analysisId;
        this.diagramId = diagramId;
        this.diagramName = diagramName;
        this.status = status;
        this.message = message;
        this.startedAt = startedAt;
        this.lastUpdated = lastUpdated;
        this.estimatedCompletion = estimatedCompletion;
        this.completedAt = completedAt;
        this.progress = progress;
        this.analysisSteps = analysisSteps;
        this.analysisMetadata = analysisMetadata;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.warnings = warnings;
    }
    
    /**
     * Creates a response from BpmnAnalysisStatus service object
     */
    public static BpmnAnalysisStatusResponse fromServiceStatus(String diagramId, BpmnAnalysisStatus serviceStatus) {
        BpmnAnalysisStatusResponse response = new BpmnAnalysisStatusResponse();
        response.setAnalysisId(serviceStatus.getAnalysisId());
        response.setDiagramId(diagramId);
        response.setDiagramName("Diagram " + diagramId);
        response.setStatus(serviceStatus.getStatus());
        response.setMessage(getStatusMessage(serviceStatus.getStatus()));
        response.setStartedAt(serviceStatus.getTimestamp());
        response.setLastUpdated(serviceStatus.getTimestamp());
        response.setProgress(calculateProgress(serviceStatus.getStatus()));
        response.setAnalysisSteps(getAnalysisSteps(serviceStatus.getStatus()));
        response.setAnalysisMetadata(Map.of("generatedAt", LocalDateTime.now()));
        
        if ("FAILED".equals(serviceStatus.getStatus()) && serviceStatus.getErrorMessage() != null) {
            response.setErrorCode("ANALYSIS_FAILED");
            response.setErrorMessage(serviceStatus.getErrorMessage());
        }
        
        return response;
    }
    
    /**
     * Creates a mock response for demonstration
     */
    public static BpmnAnalysisStatusResponse createMock(String diagramId, String diagramName) {
        BpmnAnalysisStatusResponse response = new BpmnAnalysisStatusResponse();
        response.setAnalysisId("bpmn_analysis_" + diagramId + "_" + System.currentTimeMillis());
        response.setDiagramId(diagramId);
        response.setDiagramName(diagramName);
        response.setStatus("COMPLETED");
        response.setMessage("Analysis completed successfully");
        response.setStartedAt(LocalDateTime.now().minusMinutes(5));
        response.setLastUpdated(LocalDateTime.now());
        response.setCompletedAt(LocalDateTime.now());
        response.setProgress(new ProgressInfo(100.0, 5, 5));
        response.setAnalysisSteps(getCompletedSteps());
        response.setAnalysisMetadata(Map.of(
            "totalElements", 15,
            "complexityScore", 7.5,
            "confidence", 0.89,
            "generatedAt", LocalDateTime.now()
        ));
        return response;
    }
    
    public static class ProgressInfo {
        private double percentage;
        private int completedSteps;
        private int totalSteps;
        private String currentStep;
        private String estimatedTimeRemaining;
        
        public ProgressInfo() {}
        
        public ProgressInfo(double percentage, int completedSteps, int totalSteps) {
            this.percentage = percentage;
            this.completedSteps = completedSteps;
            this.totalSteps = totalSteps;
        }
        
        public ProgressInfo(double percentage, int completedSteps, int totalSteps, 
                          String currentStep, String estimatedTimeRemaining) {
            this(percentage, completedSteps, totalSteps);
            this.currentStep = currentStep;
            this.estimatedTimeRemaining = estimatedTimeRemaining;
        }
        
        // Getters and setters
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
        
        public int getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
        
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        
        public String getEstimatedTimeRemaining() { return estimatedTimeRemaining; }
        public void setEstimatedTimeRemaining(String estimatedTimeRemaining) { this.estimatedTimeRemaining = estimatedTimeRemaining; }
    }
    
    public static class AnalysisStep {
        private String stepName;
        private String status;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String description;
        private Map<String, Object> stepData;
        
        public AnalysisStep() {}
        
        public AnalysisStep(String stepName, String status, LocalDateTime startedAt, 
                          LocalDateTime completedAt, String description, Map<String, Object> stepData) {
            this.stepName = stepName;
            this.status = status;
            this.startedAt = startedAt;
            this.completedAt = completedAt;
            this.description = description;
            this.stepData = stepData;
        }
        
        // Getters and setters
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getStepData() { return stepData; }
        public void setStepData(Map<String, Object> stepData) { this.stepData = stepData; }
    }
    
    // Utility methods
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    public boolean isInProgress() {
        return !isCompleted() && !isFailed() && !"PENDING".equals(status);
    }
    
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    public boolean hasErrors() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }
    
    private static ProgressInfo calculateProgress(String status) {
        return switch (status) {
            case "PENDING" -> new ProgressInfo(0.0, 0, 5, "Preparing analysis", "5-10 minutes");
            case "STARTED" -> new ProgressInfo(5.0, 0, 5, "Initializing", "4-8 minutes");
            case "STRUCTURE_ANALYSIS" -> new ProgressInfo(25.0, 1, 5, "Structure analysis", "3-6 minutes");
            case "SECURITY_ANALYSIS" -> new ProgressInfo(50.0, 2, 5, "Security analysis", "2-4 minutes");
            case "PERFORMANCE_ANALYSIS" -> new ProgressInfo(75.0, 3, 5, "Performance analysis", "1-2 minutes");
            case "COMPREHENSIVE_ANALYSIS" -> new ProgressInfo(90.0, 4, 5, "Comprehensive analysis", "30-60 seconds");
            case "COMPLETED" -> new ProgressInfo(100.0, 5, 5, "Completed", "0 seconds");
            case "FAILED" -> new ProgressInfo(0.0, 0, 5, "Failed", "N/A");
            default -> new ProgressInfo(0.0, 0, 5, "Unknown", "Unknown");
        };
    }
    
    private static List<AnalysisStep> getAnalysisSteps(String status) {
        return List.of(
            new AnalysisStep("Structure Analysis", getStepStatus(status, "STRUCTURE_ANALYSIS"), 
                           getStepStartTime(status, "STRUCTURE_ANALYSIS"), 
                           getStepCompleteTime(status, "STRUCTURE_ANALYSIS"),
                           "Analyzing BPMN structure and elements", Map.of("elementsFound", 15)),
            new AnalysisStep("Security Analysis", getStepStatus(status, "SECURITY_ANALYSIS"),
                           getStepStartTime(status, "SECURITY_ANALYSIS"),
                           getStepCompleteTime(status, "SECURITY_ANALYSIS"),
                           "Checking security vulnerabilities and compliance", Map.of("checks", 12)),
            new AnalysisStep("Performance Analysis", getStepStatus(status, "PERFORMANCE_ANALYSIS"),
                           getStepStartTime(status, "PERFORMANCE_ANALYSIS"),
                           getStepCompleteTime(status, "PERFORMANCE_ANALYSIS"),
                           "Analyzing performance bottlenecks and optimization", Map.of("metrics", 8)),
            new AnalysisStep("Comprehensive Analysis", getStepStatus(status, "COMPREHENSIVE_ANALYSIS"),
                           getStepStartTime(status, "COMPREHENSIVE_ANALYSIS"),
                           getStepCompleteTime(status, "COMPREHENSIVE_ANALYSIS"),
                           "Generating comprehensive report and recommendations", Map.of("recommendations", 6)),
            new AnalysisStep("Report Generation", getStepStatus(status, "COMPLETED"),
                           getStepStartTime(status, "COMPLETED"),
                           getStepCompleteTime(status, "COMPLETED"),
                           "Finalizing analysis report", Map.of("sections", 5))
        );
    }
    
    private static List<AnalysisStep> getCompletedSteps() {
        return getAnalysisSteps("COMPLETED");
    }
    
    private static String getStepStatus(String overallStatus, String stepName) {
        return switch (overallStatus) {
            case "PENDING" -> "PENDING";
            case "STARTED" -> stepName.equals("Structure Analysis") ? "COMPLETED" : "PENDING";
            case "STRUCTURE_ANALYSIS" -> stepName.equals("Structure Analysis") ? "COMPLETED" : 
                                        stepName.equals("Security Analysis") ? "IN_PROGRESS" : "PENDING";
            case "SECURITY_ANALYSIS" -> stepName.equals("Security Analysis") ? "COMPLETED" : "PENDING";
            case "PERFORMANCE_ANALYSIS" -> stepName.equals("Performance Analysis") ? "COMPLETED" : "PENDING";
            case "COMPREHENSIVE_ANALYSIS" -> stepName.equals("Comprehensive Analysis") ? "COMPLETED" : "PENDING";
            case "COMPLETED" -> "COMPLETED";
            case "FAILED" -> "FAILED";
            default -> "PENDING";
        };
    }
    
    private static LocalDateTime getStepStartTime(String status, String stepName) {
        LocalDateTime base = LocalDateTime.now().minusMinutes(5);
        return switch (status) {
            case "PENDING" -> base;
            case "STARTED" -> stepName.equals("Structure Analysis") ? base : null;
            case "STRUCTURE_ANALYSIS", "SECURITY_ANALYSIS", "PERFORMANCE_ANALYSIS", "COMPREHENSIVE_ANALYSIS", "COMPLETED" -> 
                base.plusMinutes(1);
            case "FAILED" -> base;
            default -> null;
        };
    }
    
    private static LocalDateTime getStepCompleteTime(String status, String stepName) {
        return switch (status) {
            case "COMPLETED" -> LocalDateTime.now();
            case "FAILED" -> null;
            default -> null;
        };
    }
    
    private static String getStatusMessage(String status) {
        return switch (status) {
            case "PENDING" -> "Analysis is pending";
            case "STARTED" -> "Analysis started";
            case "STRUCTURE_ANALYSIS" -> "Analyzing BPMN structure";
            case "SECURITY_ANALYSIS" -> "Performing security analysis";
            case "PERFORMANCE_ANALYSIS" -> "Analyzing performance metrics";
            case "COMPREHENSIVE_ANALYSIS" -> "Generating comprehensive analysis";
            case "COMPLETED" -> "Analysis completed successfully";
            case "FAILED" -> "Analysis failed";
            case "CANCELLED" -> "Analysis was cancelled";
            default -> "Unknown status";
        };
    }
    
    // Getters and setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getDiagramName() { return diagramName; }
    public void setDiagramName(String diagramName) { this.diagramName = diagramName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public LocalDateTime getEstimatedCompletion() { return estimatedCompletion; }
    public void setEstimatedCompletion(LocalDateTime estimatedCompletion) { this.estimatedCompletion = estimatedCompletion; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public ProgressInfo getProgress() { return progress; }
    public void setProgress(ProgressInfo progress) { this.progress = progress; }
    
    public List<AnalysisStep> getAnalysisSteps() { return analysisSteps; }
    public void setAnalysisSteps(List<AnalysisStep> analysisSteps) { this.analysisSteps = analysisSteps; }
    
    public Map<String, Object> getAnalysisMetadata() { return analysisMetadata; }
    public void setAnalysisMetadata(Map<String, Object> analysisMetadata) { this.analysisMetadata = analysisMetadata; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
}
