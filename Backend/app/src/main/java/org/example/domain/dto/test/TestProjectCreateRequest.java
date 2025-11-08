package org.example.domain.dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.entities.TestProject;

import java.util.List;

/**
 * DTO for creating test projects
 */
public class TestProjectCreateRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;
    
    private String description;
    
    private String owner;
    
    private String version;
    
    private String environment;
    
    private List<String> tags;
    
    private String configuration;
    
    private String settings;
    
    private String testFramework;
    
    private String baseUrl;
    
    private Integer timeoutMs;
    
    private Integer retryCount;
    
    private Boolean parallelExecution;
    
    // Constructors
    public TestProjectCreateRequest() {
    }
    
    public TestProjectCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public TestProjectCreateRequest(String name, String description, String owner) {
        this(name, description);
        this.owner = owner;
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
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
    
    public String getSettings() {
        return settings;
    }
    
    public void setSettings(String settings) {
        this.settings = settings;
    }
    
    public String getTestFramework() {
        return testFramework;
    }
    
    public void setTestFramework(String testFramework) {
        this.testFramework = testFramework;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public Integer getTimeoutMs() {
        return timeoutMs;
    }
    
    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Boolean getParallelExecution() {
        return parallelExecution;
    }
    
    public void setParallelExecution(Boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
    }
    
    @Override
    public String toString() {
        return "TestProjectCreateRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner='" + owner + '\'' +
                ", version='" + version + '\'' +
                ", environment='" + environment + '\'' +
                ", tags=" + tags +
                ", testFramework='" + testFramework + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", timeoutMs=" + timeoutMs +
                ", retryCount=" + retryCount +
                ", parallelExecution=" + parallelExecution +
                '}';
    }
}