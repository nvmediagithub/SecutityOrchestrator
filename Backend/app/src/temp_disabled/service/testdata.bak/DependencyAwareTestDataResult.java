package org.example.application.service.testdata;

import org.example.domain.model.testdata.DataFlowChain;
import org.example.domain.model.testdata.DataDependency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Result class for dependency-aware test data generation
 */
public class DependencyAwareTestDataResult {
    
    private String resultId;
    private String requestId;
    private LocalDateTime generatedAt;
    private boolean success;
    private String errorMessage;
    private int totalDataItems;
    private int successfullyGenerated;
    private int failedGenerations;
    private double successRate;
    private List<String> generatedDataIds;
    private List<String> failedDataIds;
    private List<DataFlowChain> completedChains;
    private List<String> dependencyRelationships;
    private Map<String, Object> generationMetadata;
    private long processingTimeMs;

    public DependencyAwareTestDataResult() {
        this.resultId = UUID.randomUUID().toString();
        this.generatedAt = LocalDateTime.now();
        this.totalDataItems = 0;
        this.successfullyGenerated = 0;
        this.failedGenerations = 0;
        this.successRate = 0.0;
    }

    public DependencyAwareTestDataResult(String requestId) {
        this();
        this.requestId = requestId;
    }

    // Getters and Setters
    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getTotalDataItems() {
        return totalDataItems;
    }

    public void setTotalDataItems(int totalDataItems) {
        this.totalDataItems = totalDataItems;
        this.updateSuccessRate();
    }

    public int getSuccessfullyGenerated() {
        return successfullyGenerated;
    }

    public void setSuccessfullyGenerated(int successfullyGenerated) {
        this.successfullyGenerated = successfullyGenerated;
        this.updateSuccessRate();
    }

    public int getFailedGenerations() {
        return failedGenerations;
    }

    public void setFailedGenerations(int failedGenerations) {
        this.failedGenerations = failedGenerations;
        this.updateSuccessRate();
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public List<String> getGeneratedDataIds() {
        return generatedDataIds;
    }

    public void setGeneratedDataIds(List<String> generatedDataIds) {
        this.generatedDataIds = generatedDataIds;
    }

    public List<String> getFailedDataIds() {
        return failedDataIds;
    }

    public void setFailedDataIds(List<String> failedDataIds) {
        this.failedDataIds = failedDataIds;
    }

    public List<DataFlowChain> getCompletedChains() {
        return completedChains;
    }

    public void setCompletedChains(List<DataFlowChain> completedChains) {
        this.completedChains = completedChains;
    }

    public List<String> getDependencyRelationships() {
        return dependencyRelationships;
    }

    public void setDependencyRelationships(List<String> dependencyRelationships) {
        this.dependencyRelationships = dependencyRelationships;
    }

    public Map<String, Object> getGenerationMetadata() {
        return generationMetadata;
    }

    public void setGenerationMetadata(Map<String, Object> generationMetadata) {
        this.generationMetadata = generationMetadata;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    private void updateSuccessRate() {
        if (this.totalDataItems > 0) {
            this.successRate = (double) this.successfullyGenerated / this.totalDataItems;
        } else {
            this.successRate = 0.0;
        }
    }

    public void addGeneratedItem(String dataId) {
        if (this.generatedDataIds == null) {
            this.generatedDataIds = new java.util.ArrayList<>();
        }
        this.generatedDataIds.add(dataId);
        this.totalDataItems++;
        this.successfullyGenerated++;
        this.updateSuccessRate();
    }

    public void addFailedItem(String dataId) {
        if (this.failedDataIds == null) {
            this.failedDataIds = new java.util.ArrayList<>();
        }
        this.failedDataIds.add(dataId);
        this.totalDataItems++;
        this.failedGenerations++;
        this.updateSuccessRate();
    }
}