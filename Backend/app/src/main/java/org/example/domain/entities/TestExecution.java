package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность для хранения результатов выполнения тестовых сценариев
 */
@Entity
@Table(name = "test_executions")
public class TestExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String executionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_scenario_id", nullable = false)
    private TestScenario testScenario;
    
    @Column(nullable = false)
    private String testSuiteId;
    
    @Column(length = 1000)
    private String environment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status = ExecutionStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private ExecutionType executionType = ExecutionType.MANUAL;
    
    @Column(length = 2000)
    private String initiatedBy;
    
    @Column(length = 2000)
    private String executedBy;
    
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Long totalDurationMs;
    private Long preparationTimeMs;
    private Long executionTimeMs;
    private Long cleanupTimeMs;
    
    private Integer totalSteps = 0;
    private Integer passedSteps = 0;
    private Integer failedSteps = 0;
    private Integer skippedSteps = 0;
    private Integer errorSteps = 0;
    
    private Integer totalAsserted = 0;
    private Integer passedAssertions = 0;
    private Integer failedAssertions = 0;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel overallSeverity = SeverityLevel.INFO;
    
    @Column(length = 2000)
    private String executionSummary;
    
    @Column(length = 4000)
    private String detailedResults;
    
    @Column(length = 4000)
    private String errorDetails;
    
    @Column(length = 2000)
    private String failureReason;
    
    @Column(length = 2000)
    private String performanceMetrics;
    
    @Column(length = 2000)
    private String securityViolations;
    
    @Column(length = 2000)
    private String complianceIssues;
    
    @ElementCollection
    private List<String> executedSteps = new ArrayList<>();
    
    @ElementCollection
    private List<String> stepResults = new ArrayList<>();
    
    @ElementCollection
    private List<String> assertionResults = new ArrayList<>();
    
    @ElementCollection
    private List<String> warnings = new ArrayList<>();
    
    @ElementCollection
    private List<String> capturedLogs = new ArrayList<>();
    
    @ElementCollection
    private List<String> generatedArtifacts = new ArrayList<>();
    
    private Boolean isParallelExecution = false;
    private Boolean isAutomatedExecution = false;
    private Boolean isScheduledExecution = false;
    private Boolean isDebugMode = false;
    
    @Column(length = 1000)
    private String executionConfig;
    
    @Column(length = 2000)
    private String dataSetUsed;
    
    @Column(length = 2000)
    private String variablesUsed;
    
    @Column(length = 4000)
    private String environmentVariables;
    
    @Column(length = 2000)
    private String systemInfo;
    
    @Column(length = 4000)
    private String metadata;
    
    @Column(length = 2000)
    private String notes;
    
    private String relatedExecutionId;
    private String retryOfExecutionId;
    
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    
    // Constructors
    public TestExecution() {
        this.executionId = "TE_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.executedSteps = new ArrayList<>();
        this.stepResults = new ArrayList<>();
        this.assertionResults = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.capturedLogs = new ArrayList<>();
        this.generatedArtifacts = new ArrayList<>();
    }
    
    public TestExecution(TestScenario testScenario, String initiatedBy) {
        this();
        this.testScenario = testScenario;
        this.initiatedBy = initiatedBy;
        this.executedBy = initiatedBy;
    }
    
    // Helper methods and getters/setters (abbreviated for brevity)
    public boolean isCompleted() {
        return status == ExecutionStatus.PASSED || 
               status == ExecutionStatus.FAILED || 
               status == ExecutionStatus.ERROR ||
               status == ExecutionStatus.SKIPPED;
    }
    
    public boolean isSuccess() {
        return status == ExecutionStatus.PASSED;
    }
    
    public boolean isFailure() {
        return status == ExecutionStatus.FAILED || status == ExecutionStatus.ERROR;
    }
    
    public double getSuccessRate() {
        if (totalSteps == 0) return 0.0;
        return (double) passedSteps / totalSteps * 100.0;
    }
    
    public void startExecution() {
        this.status = ExecutionStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void completeExecution(boolean success) {
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (success) {
            this.status = ExecutionStatus.PASSED;
        } else {
            this.status = ExecutionStatus.FAILED;
        }
        
        if (this.startedAt != null && this.completedAt != null) {
            this.totalDurationMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
    }
    
    // Enums
    public enum ExecutionStatus {
        PENDING,
        IN_PROGRESS,
        PASSED,
        FAILED,
        ERROR,
        SKIPPED,
        CANCELLED
    }
    
    public enum ExecutionType {
        MANUAL,
        AUTOMATED,
        SCHEDULED,
        CONTINUOUS,
        PARALLEL
    }
    
    // Getters and Setters (minimal for space)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public TestScenario getTestScenario() { return testScenario; }
    public void setTestScenario(TestScenario testScenario) { this.testScenario = testScenario; }
    
    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}