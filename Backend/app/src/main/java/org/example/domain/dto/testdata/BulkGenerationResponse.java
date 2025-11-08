package org.example.domain.dto.testdata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for bulk test data generation response
 * Used for POST /api/test-data/generate/bulk
 */
public class BulkGenerationResponse {
    
    private String batchId;
    private String batchName;
    private boolean accepted;
    private String message;
    private BatchStatus status;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime estimatedCompletionAt;
    private LocalDateTime completedAt;
    
    // Progress tracking
    private int totalRequests;
    private int completedRequests;
    private int failedRequests;
    private int pendingRequests;
    private double progressPercentage;
    private long elapsedTimeMs;
    private long estimatedRemainingTimeMs;
    
    // Request details
    private List<BatchItemStatus> items;
    private Map<String, Object> batchMetadata;
    private List<String> warnings;
    private List<String> errors;
    
    // Performance metrics
    private int totalRecordsGenerated;
    private long totalExecutionTimeMs;
    private double averageQualityScore;
    private int recordsPerSecond;
    private long memoryUsedMB;
    private int peakParallelThreads;
    
    // Configuration snapshot
    private BulkGenerationRequest originalRequest;
    private String processingMode;
    private int parallelThreads;
    
    // Results summary
    private Map<String, Integer> dataTypeSummary;
    private Map<String, Integer> scopeSummary;
    private List<String> generatedFileIds;
    private String downloadUrl;
    private String reportUrl;
    
    // Resource allocation
    private String allocatedResources;
    private int cpuCores;
    private long memoryGB;
    private String executionEnvironment;
    
    // Constructors
    public BulkGenerationResponse() {
        this.batchId = "batch_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.acceptedAt = LocalDateTime.now();
        this.status = BatchStatus.PENDING;
        this.items = new java.util.ArrayList<>();
        this.batchMetadata = new java.util.HashMap<>();
        this.warnings = new java.util.ArrayList<>();
        this.errors = new java.util.ArrayList<>();
        this.dataTypeSummary = new java.util.HashMap<>();
        this.scopeSummary = new java.util.HashMap<>();
        this.generatedFileIds = new java.util.ArrayList<>();
    }
    
    public BulkGenerationResponse(String batchId, String batchName) {
        this();
        this.batchId = batchId;
        this.batchName = batchName;
    }
    
    // Getters and Setters
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public BatchStatus getStatus() { return status; }
    public void setStatus(BatchStatus status) { this.status = status; }
    
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getEstimatedCompletionAt() { return estimatedCompletionAt; }
    public void setEstimatedCompletionAt(LocalDateTime estimatedCompletionAt) { this.estimatedCompletionAt = estimatedCompletionAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public int getTotalRequests() { return totalRequests; }
    public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }
    
    public int getCompletedRequests() { return completedRequests; }
    public void setCompletedRequests(int completedRequests) { this.completedRequests = completedRequests; }
    
    public int getFailedRequests() { return failedRequests; }
    public void setFailedRequests(int failedRequests) { this.failedRequests = failedRequests; }
    
    public int getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(int pendingRequests) { this.pendingRequests = pendingRequests; }
    
    public double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public long getElapsedTimeMs() { return elapsedTimeMs; }
    public void setElapsedTimeMs(long elapsedTimeMs) { this.elapsedTimeMs = elapsedTimeMs; }
    
    public long getEstimatedRemainingTimeMs() { return estimatedRemainingTimeMs; }
    public void setEstimatedRemainingTimeMs(long estimatedRemainingTimeMs) { this.estimatedRemainingTimeMs = estimatedRemainingTimeMs; }
    
    public List<BatchItemStatus> getItems() { return items; }
    public void setItems(List<BatchItemStatus> items) { this.items = items; }
    
    public Map<String, Object> getBatchMetadata() { return batchMetadata; }
    public void setBatchMetadata(Map<String, Object> batchMetadata) { this.batchMetadata = batchMetadata; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public int getTotalRecordsGenerated() { return totalRecordsGenerated; }
    public void setTotalRecordsGenerated(int totalRecordsGenerated) { this.totalRecordsGenerated = totalRecordsGenerated; }
    
    public long getTotalExecutionTimeMs() { return totalExecutionTimeMs; }
    public void setTotalExecutionTimeMs(long totalExecutionTimeMs) { this.totalExecutionTimeMs = totalExecutionTimeMs; }
    
    public double getAverageQualityScore() { return averageQualityScore; }
    public void setAverageQualityScore(double averageQualityScore) { this.averageQualityScore = averageQualityScore; }
    
    public int getRecordsPerSecond() { return recordsPerSecond; }
    public void setRecordsPerSecond(int recordsPerSecond) { this.recordsPerSecond = recordsPerSecond; }
    
    public long getMemoryUsedMB() { return memoryUsedMB; }
    public void setMemoryUsedMB(long memoryUsedMB) { this.memoryUsedMB = memoryUsedMB; }
    
    public int getPeakParallelThreads() { return peakParallelThreads; }
    public void setPeakParallelThreads(int peakParallelThreads) { this.peakParallelThreads = peakParallelThreads; }
    
    public BulkGenerationRequest getOriginalRequest() { return originalRequest; }
    public void setOriginalRequest(BulkGenerationRequest originalRequest) { this.originalRequest = originalRequest; }
    
    public String getProcessingMode() { return processingMode; }
    public void setProcessingMode(String processingMode) { this.processingMode = processingMode; }
    
    public int getParallelThreads() { return parallelThreads; }
    public void setParallelThreads(int parallelThreads) { this.parallelThreads = parallelThreads; }
    
    public Map<String, Integer> getDataTypeSummary() { return dataTypeSummary; }
    public void setDataTypeSummary(Map<String, Integer> dataTypeSummary) { this.dataTypeSummary = dataTypeSummary; }
    
    public Map<String, Integer> getScopeSummary() { return scopeSummary; }
    public void setScopeSummary(Map<String, Integer> scopeSummary) { this.scopeSummary = scopeSummary; }
    
    public List<String> getGeneratedFileIds() { return generatedFileIds; }
    public void setGeneratedFileIds(List<String> generatedFileIds) { this.generatedFileIds = generatedFileIds; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }
    
    public String getAllocatedResources() { return allocatedResources; }
    public void setAllocatedResources(String allocatedResources) { this.allocatedResources = allocatedResources; }
    
    public int getCpuCores() { return cpuCores; }
    public void setCpuCores(int cpuCores) { this.cpuCores = cpuCores; }
    
    public long getMemoryGB() { return memoryGB; }
    public void setMemoryGB(long memoryGB) { this.memoryGB = memoryGB; }
    
    public String getExecutionEnvironment() { return executionEnvironment; }
    public void setExecutionEnvironment(String executionEnvironment) { this.executionEnvironment = executionEnvironment; }
    
    // Helper methods
    public void markStarted() {
        this.startedAt = LocalDateTime.now();
        this.status = BatchStatus.PROCESSING;
    }
    
    public void markCompleted() {
        this.completedAt = LocalDateTime.now();
        this.status = BatchStatus.COMPLETED;
        if (this.startedAt != null) {
            this.totalExecutionTimeMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
        updateProgress();
    }
    
    public void markFailed(String errorMessage) {
        this.completedAt = LocalDateTime.now();
        this.status = BatchStatus.FAILED;
        this.addError(errorMessage);
    }
    
    public void updateProgress() {
        if (totalRequests > 0) {
            this.progressPercentage = (completedRequests * 100.0) / totalRequests;
            this.pendingRequests = totalRequests - completedRequests - failedRequests;
        }
        
        // Calculate estimated remaining time
        if (startedAt != null && progressPercentage > 0 && progressPercentage < 100) {
            long elapsed = java.time.Duration.between(startedAt, LocalDateTime.now()).toMillis();
            long estimatedTotal = (long) (elapsed / (progressPercentage / 100.0));
            this.estimatedRemainingTimeMs = estimatedTotal - elapsed;
            
            // Estimate completion time
            this.estimatedCompletionAt = LocalDateTime.now().plus(java.time.Duration.ofMillis(estimatedRemainingTimeMs));
        }
    }
    
    public void addItemStatus(BatchItemStatus item) {
        if (items != null && item != null) {
            items.add(item);
            updateProgress();
        }
    }
    
    public void addWarning(String warning) {
        if (warnings != null && warning != null) {
            warnings.add(warning);
        }
    }
    
    public void addError(String error) {
        if (errors != null && error != null) {
            errors.add(error);
        }
    }
    
    public void addDataTypeSummary(String dataType, int count) {
        if (dataTypeSummary != null && dataType != null) {
            dataTypeSummary.put(dataType, dataTypeSummary.getOrDefault(dataType, 0) + count);
        }
    }
    
    public void addScopeSummary(String scope, int count) {
        if (scopeSummary != null && scope != null) {
            scopeSummary.put(scope, scopeSummary.getOrDefault(scope, 0) + count);
        }
    }
    
    public void addGeneratedFileId(String fileId) {
        if (generatedFileIds != null && fileId != null && !generatedFileIds.contains(fileId)) {
            generatedFileIds.add(fileId);
        }
    }
    
    public void addMetadata(String key, Object value) {
        if (batchMetadata != null && key != null) {
            batchMetadata.put(key, value);
        }
    }
    
    // Status checking methods
    public boolean isCompleted() {
        return status == BatchStatus.COMPLETED;
    }
    
    public boolean isFailed() {
        return status == BatchStatus.FAILED;
    }
    
    public boolean isProcessing() {
        return status == BatchStatus.PROCESSING;
    }
    
    public boolean isPending() {
        return status == BatchStatus.PENDING;
    }
    
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    public boolean isHighQuality() {
        return averageQualityScore >= 80.0;
    }
    
    public boolean hasPartialFailures() {
        return failedRequests > 0 && completedRequests > 0;
    }
    
    public boolean isFullySuccessful() {
        return isCompleted() && failedRequests == 0 && !hasErrors();
    }
    
    // Static factory methods
    public static BulkGenerationResponse accepted(String batchId, BulkGenerationRequest request) {
        BulkGenerationResponse response = new BulkGenerationResponse(batchId, request.getBatchName());
        response.setAccepted(true);
        response.setMessage("Batch generation request accepted and queued for processing");
        response.setStatus(BatchStatus.PENDING);
        response.setOriginalRequest(request);
        response.setTotalRequests(request.getGenerationRequests().size());
        response.setProcessingMode(request.getProcessingMode().name());
        response.setParallelThreads(request.getParallelThreads());
        return response;
    }
    
    public static BulkGenerationResponse rejected(String batchId, String reason) {
        BulkGenerationResponse response = new BulkGenerationResponse(batchId, "Rejected Batch");
        response.setAccepted(false);
        response.setMessage("Batch generation request rejected: " + reason);
        response.setStatus(BatchStatus.REJECTED);
        response.addError(reason);
        return response;
    }
    
    // Inner class for tracking individual batch items
    public static class BatchItemStatus {
        private String requestId;
        private String generationId;
        private BatchItemStatus status;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private int recordsGenerated;
        private String dataType;
        private String errorMessage;
        private double qualityScore;
        
        // Constructors
        public BatchItemStatus() {}
        
        public BatchItemStatus(String requestId) {
            this.requestId = requestId;
            this.startedAt = LocalDateTime.now();
            this.status = BatchItemStatus.PENDING;
        }
        
        // Getters and Setters
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        
        public String getGenerationId() { return generationId; }
        public void setGenerationId(String generationId) { this.generationId = generationId; }
        
        public BatchItemStatus getStatus() { return status; }
        public void setStatus(BatchItemStatus status) { this.status = status; }
        
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public int getRecordsGenerated() { return recordsGenerated; }
        public void setRecordsGenerated(int recordsGenerated) { this.recordsGenerated = recordsGenerated; }
        
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public double getQualityScore() { return qualityScore; }
        public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
        
        // Status enum
        public enum BatchItemStatus {
            PENDING,
            PROCESSING,
            COMPLETED,
            FAILED,
            RETRYING
        }
    }
    
    // Status enum
    public enum BatchStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REJECTED,
        PAUSED
    }
    
    @Override
    public String toString() {
        return "BulkGenerationResponse{" +
                "batchId='" + batchId + '\'' +
                ", status=" + status +
                ", progress=" + String.format("%.1f%%", progressPercentage) +
                ", completed=" + completedRequests +
                "/" + totalRequests +
                '}';
    }
}