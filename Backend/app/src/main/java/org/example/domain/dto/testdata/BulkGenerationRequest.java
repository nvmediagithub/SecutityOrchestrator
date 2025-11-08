package org.example.domain.dto.testdata;

import org.example.domain.model.testdata.enums.GenerationScope;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

/**
 * DTO for bulk test data generation request
 * Used for POST /api/test-data/generate/bulk
 */
public class BulkGenerationRequest {
    
    @NotBlank(message = "Batch ID is required")
    private String batchId;
    
    @NotBlank(message = "Batch name is required")
    private String batchName;
    
    private String description;
    
    @NotEmpty(message = "Generation requests list cannot be empty")
    private List<GenerationRequest> generationRequests;
    
    @NotNull(message = "Batch processing mode is required")
    private BatchProcessingMode processingMode;
    
    @Min(value = 1, message = "Parallel threads must be at least 1")
    @Max(value = 20, message = "Parallel threads cannot exceed 20")
    private int parallelThreads = 4;
    
    @Min(value = 10, message = "Batch size must be at least 10")
    @Max(value = 1000, message = "Batch size cannot exceed 1000")
    private int batchSize = 100;
    
    @Min(value = 1, message = "Timeout must be at least 1 minute")
    @Max(value = 120, message = "Timeout cannot exceed 120 minutes")
    private int timeoutMinutes = 30;
    
    private boolean enableProgressNotification = true;
    private boolean enableRetryOnFailure = true;
    private int maxRetries = 3;
    
    @Min(value = 1000, message = "Retry delay must be at least 1000ms")
    private long retryDelayMs = 5000;
    
    private String notificationWebhook;
    private String callbackUrl;
    
    // Global constraints applied to all requests
    private Map<String, Object> globalConstraints;
    private List<String> globalRequiredFields;
    private List<String> globalExcludeFields;
    
    // Quality and validation settings
    private boolean enableGlobalValidation = true;
    private boolean enableCrossDataValidation = false;
    private double minQualityScore = 70.0;
    
    // Priority and scheduling
    private BatchPriority priority = BatchPriority.NORMAL;
    private String scheduledAt; // ISO 8601 format
    
    // Metadata
    private Map<String, Object> metadata;
    private String createdBy;
    private List<String> tags;
    
    // Constructors
    public BulkGenerationRequest() {
        this.batchId = "batch_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.generationRequests = new java.util.ArrayList<>();
        this.metadata = new java.util.HashMap<>();
        this.tags = new java.util.ArrayList<>();
    }
    
    public BulkGenerationRequest(String batchName, List<GenerationRequest> requests) {
        this();
        this.batchName = batchName;
        this.generationRequests = requests;
    }
    
    // Getters and Setters
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<GenerationRequest> getGenerationRequests() { return generationRequests; }
    public void setGenerationRequests(List<GenerationRequest> generationRequests) { this.generationRequests = generationRequests; }
    
    public BatchProcessingMode getProcessingMode() { return processingMode; }
    public void setProcessingMode(BatchProcessingMode processingMode) { this.processingMode = processingMode; }
    
    public int getParallelThreads() { return parallelThreads; }
    public void setParallelThreads(int parallelThreads) { this.parallelThreads = parallelThreads; }
    
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public boolean isEnableProgressNotification() { return enableProgressNotification; }
    public void setEnableProgressNotification(boolean enableProgressNotification) { this.enableProgressNotification = enableProgressNotification; }
    
    public boolean isEnableRetryOnFailure() { return enableRetryOnFailure; }
    public void setEnableRetryOnFailure(boolean enableRetryOnFailure) { this.enableRetryOnFailure = enableRetryOnFailure; }
    
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    
    public long getRetryDelayMs() { return retryDelayMs; }
    public void setRetryDelayMs(long retryDelayMs) { this.retryDelayMs = retryDelayMs; }
    
    public String getNotificationWebhook() { return notificationWebhook; }
    public void setNotificationWebhook(String notificationWebhook) { this.notificationWebhook = notificationWebhook; }
    
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    
    public Map<String, Object> getGlobalConstraints() { return globalConstraints; }
    public void setGlobalConstraints(Map<String, Object> globalConstraints) { this.globalConstraints = globalConstraints; }
    
    public List<String> getGlobalRequiredFields() { return globalRequiredFields; }
    public void setGlobalRequiredFields(List<String> globalRequiredFields) { this.globalRequiredFields = globalRequiredFields; }
    
    public List<String> getGlobalExcludeFields() { return globalExcludeFields; }
    public void setGlobalExcludeFields(List<String> globalExcludeFields) { this.globalExcludeFields = globalExcludeFields; }
    
    public boolean isEnableGlobalValidation() { return enableGlobalValidation; }
    public void setEnableGlobalValidation(boolean enableGlobalValidation) { this.enableGlobalValidation = enableGlobalValidation; }
    
    public boolean isEnableCrossDataValidation() { return enableCrossDataValidation; }
    public void setEnableCrossDataValidation(boolean enableCrossDataValidation) { this.enableCrossDataValidation = enableCrossDataValidation; }
    
    public double getMinQualityScore() { return minQualityScore; }
    public void setMinQualityScore(double minQualityScore) { this.minQualityScore = minQualityScore; }
    
    public BatchPriority getPriority() { return priority; }
    public void setPriority(BatchPriority priority) { this.priority = priority; }
    
    public String getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    // Helper methods
    public void addGenerationRequest(GenerationRequest request) {
        if (generationRequests != null && request != null) {
            generationRequests.add(request);
        }
    }
    
    public void addTag(String tag) {
        if (tags != null && tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addMetadata(String key, Object value) {
        if (metadata != null && key != null) {
            metadata.put(key, value);
        }
    }
    
    public int getTotalRequestedRecords() {
        if (generationRequests == null) return 0;
        return generationRequests.stream()
            .mapToInt(GenerationRequest::getRecordCount)
            .sum();
    }
    
    public boolean isHighPriority() {
        return priority == BatchPriority.HIGH || priority == BatchPriority.URGENT;
    }
    
    public boolean needsScheduling() {
        return scheduledAt != null && !scheduledAt.isEmpty();
    }
    
    public boolean hasWebhooks() {
        return (notificationWebhook != null && !notificationWebhook.isEmpty()) ||
               (callbackUrl != null && !callbackUrl.isEmpty());
    }
    
    // Enums
    public enum BatchProcessingMode {
        SEQUENTIAL,   // Process requests one by one
        PARALLEL,     // Process requests in parallel
        HYBRID        // Mix of sequential and parallel
    }
    
    public enum BatchPriority {
        LOW(1),
        NORMAL(2),
        HIGH(3),
        URGENT(4);
        
        private final int level;
        
        BatchPriority(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }
    
    @Override
    public String toString() {
        return "BulkGenerationRequest{" +
                "batchId='" + batchId + '\'' +
                ", batchName='" + batchName + '\'' +
                ", processingMode=" + processingMode +
                ", totalRequests=" + (generationRequests != null ? generationRequests.size() : 0) +
                ", totalRecords=" + getTotalRequestedRecords() +
                '}';
    }
}