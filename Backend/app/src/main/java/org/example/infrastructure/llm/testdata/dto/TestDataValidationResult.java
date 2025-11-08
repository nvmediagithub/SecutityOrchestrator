package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for test data validation result
 */
public class TestDataValidationResult {
    
    private String validationId;
    private LocalDateTime validatedAt;
    private boolean valid;
    private List<String> violations;
    private List<String> suggestions;
    private ValidationMetrics metrics;
    
    public static class ValidationMetrics {
        private int totalFields;    // Total number of fields validated
        private int validFields;    // Number of fields that passed validation
        private int invalidFields;  // Number of fields that failed validation
        private int missingFields;  // Number of fields that are missing
        private int formatErrors;   // Number of format-related errors
        private int businessRuleErrors; // Number of business rule violations
        
        // Constructors
        public ValidationMetrics() {
            this.totalFields = 0;
            this.validFields = 0;
            this.invalidFields = 0;
            this.missingFields = 0;
            this.formatErrors = 0;
            this.businessRuleErrors = 0;
        }
        
        // Getters and Setters
        public int getTotalFields() { return totalFields; }
        public void setTotalFields(int totalFields) { this.totalFields = totalFields; }
        
        public int getValidFields() { return validFields; }
        public void setValidFields(int validFields) { this.validFields = validFields; }
        
        public int getInvalidFields() { return invalidFields; }
        public void setInvalidFields(int invalidFields) { this.invalidFields = invalidFields; }
        
        public int getMissingFields() { return missingFields; }
        public void setMissingFields(int missingFields) { this.missingFields = missingFields; }
        
        public int getFormatErrors() { return formatErrors; }
        public void setFormatErrors(int formatErrors) { this.formatErrors = formatErrors; }
        
        public int getBusinessRuleErrors() { return businessRuleErrors; }
        public void setBusinessRuleErrors(int businessRuleErrors) { this.businessRuleErrors = businessRuleErrors; }
        
        // Helper methods
        public double getValidationRate() {
            return totalFields > 0 ? (double) validFields / totalFields * 100 : 0;
        }
        
        public boolean isFullyValid() {
            return invalidFields == 0 && missingFields == 0;
        }
        
        public boolean hasCriticalErrors() {
            return businessRuleErrors > 0 || missingFields > 0;
        }
    }
    
    // Constructors
    public TestDataValidationResult() {
        this.metrics = new ValidationMetrics();
        this.violations = new java.util.ArrayList<>();
        this.suggestions = new java.util.ArrayList<>();
    }
    
    // Getters and Setters
    public String getValidationId() { return validationId; }
    public void setValidationId(String validationId) { this.validationId = validationId; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public List<String> getViolations() { return violations; }
    public void setViolations(List<String> violations) { this.violations = violations; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    
    public ValidationMetrics getMetrics() { return metrics; }
    public void setMetrics(ValidationMetrics metrics) { this.metrics = metrics; }
    
    // Helper methods
    public boolean hasViolations() {
        return violations != null && !violations.isEmpty();
    }
    
    public boolean hasSuggestions() {
        return suggestions != null && !suggestions.isEmpty();
    }
    
    public boolean isPartiallyValid() {
        return valid && metrics.getValidFields() < metrics.getTotalFields();
    }
    
    public void addViolation(String violation) {
        if (violations != null && violation != null) {
            violations.add(violation);
        }
    }
    
    public void addSuggestion(String suggestion) {
        if (suggestions != null && suggestion != null) {
            suggestions.add(suggestion);
        }
    }
    
    public void markAsInvalid() {
        this.valid = false;
    }
    
    public void markAsValid() {
        this.valid = true;
        this.violations.clear();
    }
    
    /**
     * Returns validation score as percentage (0-100)
     */
    public double getValidationScore() {
        return metrics.getValidationRate();
    }
    
    /**
     * Returns overall quality score combining validation and completeness
     */
    public double getOverallScore() {
        double validationScore = getValidationScore();
        double completenessScore = metrics.getTotalFields() > 0 ?
            (double) (metrics.getTotalFields() - metrics.getMissingFields()) / metrics.getTotalFields() * 100 : 100;
        
        return (validationScore + completenessScore) / 2.0;
    }
    
    @Override
    public String toString() {
        return "TestDataValidationResult{" +
                "validationId='" + validationId + '\'' +
                ", valid=" + valid +
                ", validationRate=" + String.format("%.1f%%", getValidationScore()) +
                ", violations=" + (violations != null ? violations.size() : 0) +
                '}';
    }
}