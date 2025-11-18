package org.example.shared.domain.dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * DTO for creating a new test session.
 */
public class TestSessionCreateRequest {

    @NotBlank(message = "Session name is required")
    @Size(min = 2, max = 100, message = "Session name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Executor is required")
    private String executor;

    private String environment;
    private String testType;
    private Integer timeoutMinutes = 30;
    private boolean retryOnFailure = false;
    private boolean parallelExecution = false;
    private Set<String> tags;

    // Constructors
    public TestSessionCreateRequest() {}

    public TestSessionCreateRequest(String name, String description, String executor) {
        this.name = name;
        this.description = description;
        this.executor = executor;
    }

    // Getters and Setters
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

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public Integer getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(Integer timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }

    public boolean isRetryOnFailure() {
        return retryOnFailure;
    }

    public void setRetryOnFailure(boolean retryOnFailure) {
        this.retryOnFailure = retryOnFailure;
    }

    public boolean isParallelExecution() {
        return parallelExecution;
    }

    public void setParallelExecution(boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}