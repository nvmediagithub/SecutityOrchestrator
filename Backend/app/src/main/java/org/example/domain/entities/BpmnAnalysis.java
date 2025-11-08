package org.example.domain.entities;

import org.example.domain.valueobjects.BpmnProcessId;
import org.example.domain.valueobjects.ProcessStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Entity representing the results of a BPMN diagram analysis
 */
public class BpmnAnalysis {
    private final BpmnProcessId processId;
    private final String processName;
    private final String processContent;
    private final ProcessStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;
    private final String analysisResult;
    private final List<String> warnings;
    private final List<String> suggestions;

    public BpmnAnalysis(BpmnProcessId processId, String processName, String processContent,
                       ProcessStatus status, LocalDateTime createdAt, LocalDateTime completedAt,
                       String analysisResult, List<String> warnings, List<String> suggestions) {
        this.processId = Objects.requireNonNull(processId, "Process ID cannot be null");
        this.processName = Objects.requireNonNull(processName, "Process name cannot be null");
        this.processContent = Objects.requireNonNull(processContent, "Process content cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
        this.completedAt = completedAt;
        this.analysisResult = analysisResult;
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
        this.suggestions = suggestions != null ? new ArrayList<>(suggestions) : new ArrayList<>();
    }

    /**
     * Creates a new BPMN analysis in PENDING status
     */
    public static BpmnAnalysis createPendingAnalysis(String processName, String processContent) {
        return new BpmnAnalysis(
            BpmnProcessId.generate(),
            processName,
            processContent,
            ProcessStatus.PENDING,
            LocalDateTime.now(),
            null,
            null,
            new ArrayList<>(),
            new ArrayList<>()
        );
    }

    /**
     * Creates a completed analysis
     */
    public static BpmnAnalysis createCompletedAnalysis(BpmnProcessId processId, String processName,
                                                      String processContent, String analysisResult,
                                                      List<String> warnings, List<String> suggestions) {
        return new BpmnAnalysis(
            processId,
            processName,
            processContent,
            ProcessStatus.COMPLETED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            analysisResult,
            warnings,
            suggestions
        );
    }

    /**
     * Creates a failed analysis
     */
    public static BpmnAnalysis createFailedAnalysis(BpmnProcessId processId, String processName,
                                                   String processContent, String errorMessage) {
        List<String> errorWarnings = new ArrayList<>();
        errorWarnings.add("Analysis failed: " + errorMessage);
        
        return new BpmnAnalysis(
            processId,
            processName,
            processContent,
            ProcessStatus.FAILED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            errorWarnings,
            new ArrayList<>()
        );
    }

    /**
     * Marks the analysis as analyzing
     */
    public BpmnAnalysis markAsAnalyzing() {
        return new BpmnAnalysis(
            processId, processName, processContent,
            ProcessStatus.ANALYZING, createdAt, null,
            analysisResult, warnings, suggestions
        );
    }

    /**
     * Marks the analysis as completed
     */
    public BpmnAnalysis markAsCompleted(String analysisResult, List<String> warnings, List<String> suggestions) {
        return new BpmnAnalysis(
            processId, processName, processContent,
            ProcessStatus.COMPLETED, createdAt, LocalDateTime.now(),
            analysisResult, warnings, suggestions
        );
    }

    /**
     * Marks the analysis as failed
     */
    public BpmnAnalysis markAsFailed(String errorMessage) {
        List<String> errorWarnings = new ArrayList<>(this.warnings);
        errorWarnings.add("Analysis failed: " + errorMessage);
        
        return new BpmnAnalysis(
            processId, processName, processContent,
            ProcessStatus.FAILED, createdAt, LocalDateTime.now(),
            null, errorWarnings, suggestions
        );
    }

    // Getters
    public BpmnProcessId getProcessId() { return processId; }
    public String getProcessName() { return processName; }
    public String getProcessContent() { return processContent; }
    public ProcessStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getAnalysisResult() { return analysisResult; }
    public List<String> getWarnings() { return new ArrayList<>(warnings); }
    public List<String> getSuggestions() { return new ArrayList<>(suggestions); }

    // Business logic methods
    public boolean isPending() {
        return status == ProcessStatus.PENDING;
    }

    public boolean isAnalyzing() {
        return status == ProcessStatus.ANALYZING;
    }

    public boolean isCompleted() {
        return status == ProcessStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == ProcessStatus.FAILED;
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BpmnAnalysis that = (BpmnAnalysis) o;
        return Objects.equals(processId, that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId);
    }

    @Override
    public String toString() {
        return "BpmnAnalysis{" +
                "processId=" + processId +
                ", processName='" + processName + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}