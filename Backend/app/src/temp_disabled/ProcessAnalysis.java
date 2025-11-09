package org.example.domain.entities.bpmn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing analysis results for a business process
 */
@Entity
@Table(name = "process_analyses")
public class ProcessAnalysis {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private BusinessProcess process;

    @NotBlank
    @Column(name = "analysis_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AnalysisType analysisType;

    @NotNull
    @Column(name = "analysis_date", nullable = false)
    private LocalDateTime analysisDate;

    @NotBlank
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AnalysisStatus status = AnalysisStatus.PENDING;

    @Size(max = 1000)
    @Column(length = 1000)
    private String summary;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "issues_found", columnDefinition = "INTEGER DEFAULT 0")
    private Integer issuesFound = 0;

    @Column(name = "warnings_found", columnDefinition = "INTEGER DEFAULT 0")
    private Integer warningsFound = 0;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // Analysis results stored as JSON
    @ElementCollection
    @CollectionTable(name = "process_analysis_results", 
                    joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "result_data")
    private List<String> resultData;

    // Issues and recommendations
    @ElementCollection
    @CollectionTable(name = "process_analysis_issues", 
                    joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "issue_data")
    private List<String> issues;

    @ElementCollection
    @CollectionTable(name = "process_analysis_recommendations", 
                    joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "recommendation_data")
    private List<String> recommendations;

    // Structural analysis
    @Column(name = "task_count")
    private Integer taskCount;

    @Column(name = "gateway_count")
    private Integer gatewayCount;

    @Column(name = "event_count")
    private Integer eventCount;

    @Column(name = "sequence_count")
    private Integer sequenceCount;

    @Column(name = "has_loops")
    private boolean hasLoops = false;

    @Column(name = "has_dead_ends")
    private boolean hasDeadEnds = false;

    @Column(name = "complexity_score")
    private Double complexityScore;

    // API mapping results
    @Column(name = "api_mapped_count")
    private Integer apiMappedCount;

    @Column(name = "unmapped_tasks_count")
    private Integer unmappedTasksCount;

    @Column(name = "api_coverage_percentage")
    private Double apiCoveragePercentage;

    // Test scenario generation
    @Column(name = "test_scenarios_generated")
    private Integer testScenariosGenerated;

    @Column(name = "test_scenarios_count")
    private Integer testScenariosCount;

    public ProcessAnalysis() {}

    public ProcessAnalysis(BusinessProcess process, AnalysisType analysisType) {
        this.process = process;
        this.analysisType = analysisType;
        this.analysisDate = LocalDateTime.now();
        this.status = AnalysisStatus.PENDING;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BusinessProcess getProcess() {
        return process;
    }

    public void setProcess(BusinessProcess process) {
        this.process = process;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public Integer getIssuesFound() {
        return issuesFound;
    }

    public void setIssuesFound(Integer issuesFound) {
        this.issuesFound = issuesFound;
    }

    public Integer getWarningsFound() {
        return warningsFound;
    }

    public void setWarningsFound(Integer warningsFound) {
        this.warningsFound = warningsFound;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getResultData() {
        return resultData;
    }

    public void setResultData(List<String> resultData) {
        this.resultData = resultData;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Integer getGatewayCount() {
        return gatewayCount;
    }

    public void setGatewayCount(Integer gatewayCount) {
        this.gatewayCount = gatewayCount;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public Integer getSequenceCount() {
        return sequenceCount;
    }

    public void setSequenceCount(Integer sequenceCount) {
        this.sequenceCount = sequenceCount;
    }

    public boolean isHasLoops() {
        return hasLoops;
    }

    public void setHasLoops(boolean hasLoops) {
        this.hasLoops = hasLoops;
    }

    public boolean isHasDeadEnds() {
        return hasDeadEnds;
    }

    public void setHasDeadEnds(boolean hasDeadEnds) {
        this.hasDeadEnds = hasDeadEnds;
    }

    public Double getComplexityScore() {
        return complexityScore;
    }

    public void setComplexityScore(Double complexityScore) {
        this.complexityScore = complexityScore;
    }

    public Integer getApiMappedCount() {
        return apiMappedCount;
    }

    public void setApiMappedCount(Integer apiMappedCount) {
        this.apiMappedCount = apiMappedCount;
    }

    public Integer getUnmappedTasksCount() {
        return unmappedTasksCount;
    }

    public void setUnmappedTasksCount(Integer unmappedTasksCount) {
        this.unmappedTasksCount = unmappedTasksCount;
    }

    public Double getApiCoveragePercentage() {
        return apiCoveragePercentage;
    }

    public void setApiCoveragePercentage(Double apiCoveragePercentage) {
        this.apiCoveragePercentage = apiCoveragePercentage;
    }

    public Integer getTestScenariosGenerated() {
        return testScenariosGenerated;
    }

    public void setTestScenariosGenerated(Integer testScenariosGenerated) {
        this.testScenariosGenerated = testScenariosGenerated;
    }

    public Integer getTestScenariosCount() {
        return testScenariosCount;
    }

    public void setTestScenariosCount(Integer testScenariosCount) {
        this.testScenariosCount = testScenariosCount;
    }

    /**
     * Analysis type enumeration
     */
    public enum AnalysisType {
        STRUCTURE_ANALYSIS,
        BUSINESS_LOGIC_ANALYSIS,
        SECURITY_ANALYSIS,
        PERFORMANCE_ANALYSIS,
        API_MAPPING_ANALYSIS,
        TEST_SCENARIO_ANALYSIS,
        INTEGRATED_ANALYSIS,
        VALIDATION_ANALYSIS
    }

    /**
     * Analysis status enumeration
     */
    public enum AnalysisStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}