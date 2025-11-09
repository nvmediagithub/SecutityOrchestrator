package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.valueobjects.TestCaseId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a test case
 */
@Entity
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @NotBlank
    @Column(name = "test_type", nullable = false)
    private String testType;

    @Column(name = "test_case_id", nullable = false, unique = true)
    private String testCaseId;

    @Column(name = "category")
    private String category;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private TestPriority priority = TestPriority.MEDIUM;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TestStatus status = TestStatus.DRAFT;

    @Column(name = "is_automated")
    private boolean automated = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private TestProject project;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestScenario> scenarios;

    public Test() {}

    public Test(String name, String description, String testType, String testCaseId) {
        this.name = name;
        this.description = description;
        this.testType = testType;
        this.testCaseId = testCaseId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TestPriority getPriority() {
        return priority;
    }

    public void setPriority(TestPriority priority) {
        this.priority = priority;
    }

    public TestStatus getStatus() {
        return status;
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }

    public boolean isAutomated() {
        return automated;
    }

    public void setAutomated(boolean automated) {
        this.automated = automated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastExecutedAt() {
        return lastExecutedAt;
    }

    public void setLastExecutedAt(LocalDateTime lastExecutedAt) {
        this.lastExecutedAt = lastExecutedAt;
    }

    public TestProject getProject() {
        return project;
    }

    public void setProject(TestProject project) {
        this.project = project;
    }

    public List<TestScenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<TestScenario> scenarios) {
        this.scenarios = scenarios;
    }

    /**
     * Test priority enumeration
     */
    public enum TestPriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Test status enumeration
     */
    public enum TestStatus {
        DRAFT,
        READY,
        RUNNING,
        PASSED,
        FAILED,
        BLOCKED,
        SKIPPED
    }
}