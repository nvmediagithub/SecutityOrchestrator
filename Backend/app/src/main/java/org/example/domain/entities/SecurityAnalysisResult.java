package org.example.domain.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Security Analysis Result entity for OWASP API Security Top 10 analysis
 */
public class SecurityAnalysisResult {
    private String id;
    private String apiEndpoint;
    private String analysisType;
    private OWASPCategory owaspCategory;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String description;
    private String recommendation;
    private String codeSnippet;
    private LocalDateTime analyzedAt;
    private Map<String, Object> context;
    private List<SecurityTest> generatedTests;
    private double confidenceScore;
    private String llmModel;
    private boolean isFalsePositive;

    public enum OWASPCategory {
        API1_BROKEN_OBJECT_LEVEL_AUTHORIZATION,
        API2_BROKEN_USER_AUTHENTICATION,
        API3_EXCESSIVE_DATA_EXPOSURE,
        API4_LACK_OF_RESOURCES_AND_RATE_LIMITING,
        API5_BROKEN_FUNCTION_LEVEL_AUTHORIZATION,
        API6_MASS_ASSIGNMENT,
        API7_SECURITY_MISCONFIGURATION,
        API8_INJECTION,
        API9_IMPROPER_ASSETS_MANAGEMENT,
        API10_INSUFFICIENT_LOGGING_AND_MONITORING
    }

    public SecurityAnalysisResult() {
        this.id = generateId();
        this.analyzedAt = LocalDateTime.now();
        this.context = new java.util.HashMap<>();
        this.generatedTests = new java.util.ArrayList<>();
    }

    public SecurityAnalysisResult(String apiEndpoint, OWASPCategory category) {
        this();
        this.apiEndpoint = apiEndpoint;
        this.owaspCategory = category;
    }

    private String generateId() {
        return "SEC_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }

    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    public OWASPCategory getOwaspCategory() { return owaspCategory; }
    public void setOwaspCategory(OWASPCategory owaspCategory) { this.owaspCategory = owaspCategory; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }

    public List<SecurityTest> getGeneratedTests() { return generatedTests; }
    public void setGeneratedTests(List<SecurityTest> generatedTests) { this.generatedTests = generatedTests; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getLlmModel() { return llmModel; }
    public void setLlmModel(String llmModel) { this.llmModel = llmModel; }

    public boolean isFalsePositive() { return isFalsePositive; }
    public void setFalsePositive(boolean falsePositive) { isFalsePositive = falsePositive; }

    @Override
    public String toString() {
        return "SecurityAnalysisResult{" +
                "id='" + id + '\'' +
                ", apiEndpoint='" + apiEndpoint + '\'' +
                ", owaspCategory=" + owaspCategory +
                ", severity='" + severity + '\'' +
                ", confidenceScore=" + confidenceScore +
                '}';
    }
}