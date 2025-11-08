package org.example.domain.dto.context;

import java.util.List;

/**
 * DTO representing an end-to-end business scenario
 */
public class EndToEndScenario {
    private String scenarioId;
    private String scenarioName;
    private String description;
    private List<String> bpmnSteps;
    private List<String> apiCalls;
    private String businessObjective;
    private String successCriteria;
    private String status;
    private Double complexityScore;
    private List<String> stakeholders;
    private List<String> prerequisites;
    private String estimatedDuration;

    public EndToEndScenario() {}

    public EndToEndScenario(String scenarioId, String scenarioName) {
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
    }

    // Getters and Setters
    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String scenarioId) { this.scenarioId = scenarioId; }

    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getBpmnSteps() { return bpmnSteps; }
    public void setBpmnSteps(List<String> bpmnSteps) { this.bpmnSteps = bpmnSteps; }

    public List<String> getApiCalls() { return apiCalls; }
    public void setApiCalls(List<String> apiCalls) { this.apiCalls = apiCalls; }

    public String getBusinessObjective() { return businessObjective; }
    public void setBusinessObjective(String businessObjective) { this.businessObjective = businessObjective; }

    public String getSuccessCriteria() { return successCriteria; }
    public void setSuccessCriteria(String successCriteria) { this.successCriteria = successCriteria; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getComplexityScore() { return complexityScore; }
    public void setComplexityScore(Double complexityScore) { this.complexityScore = complexityScore; }

    public List<String> getStakeholders() { return stakeholders; }
    public void setStakeholders(List<String> stakeholders) { this.stakeholders = stakeholders; }

    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }

    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
}