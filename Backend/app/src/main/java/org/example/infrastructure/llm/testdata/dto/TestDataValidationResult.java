package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result of test data validation
 */
public class TestDataValidationResult {
    
    private String validationId;
    private boolean valid;
    private List<String> violations;
    private List<String> warnings;
    private List<String> suggestions;
    private List<String> passedRules;
    private List<String> failedRules;
    private double validationScore;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private LocalDateTime validatedAt;
    private String validator;
    
    // Constructors
    public TestDataValidationResult() {}
    
    // Getters and Setters
    public String getValidationId() { return validationId; }
    public void setValidationId(String validationId) { this.validationId = validationId; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public List<String> getViolations() { return violations; }
    public void setViolations(List<String> violations) { this.violations = violations; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    
    public List<String> getPassedRules() { return passedRules; }
    public void setPassedRules(List<String> passedRules) { this.passedRules = passedRules; }
    
    public List<String> getFailedRules() { return failedRules; }
    public void setFailedRules(List<String> failedRules) { this.failedRules = failedRules; }
    
    public double getValidationScore() { return validationScore; }
    public void setValidationScore(double validationScore) { this.validationScore = validationScore; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    public String getValidator() { return validator; }
    public void setValidator(String validator) { this.validator = validator; }
}