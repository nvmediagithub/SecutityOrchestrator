package org.example.domain.dto.testdata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for test data validation response
 * Used for POST /api/data/validate
 */
public class ValidationResponse {
    
    private String requestId;
    private String validationId;
    private boolean isValid;
    private String message;
    private LocalDateTime validatedAt;
    private long executionTimeMs;
    
    // Validation summary
    private int totalRecords;
    private int validRecords;
    private int invalidRecords;
    private double validityPercentage;
    
    // Detailed results
    private List<ValidationResult> recordResults;
    private List<ValidationIssue> issues;
    private List<ValidationWarning> warnings;
    
    // Compliance results
    private ComplianceResult complianceResult;
    private List<ComplianceViolation> complianceViolations;
    
    // Statistics
    private ValidationStatistics statistics;
    private Map<String, Object> metrics;
    
    // Recommendations
    private List<String> recommendations;
    private List<String> suggestedFixes;
    
    // Quality assessment
    private DataQualityReport qualityReport;
    private double overallScore;
    
    // Context
    private String dataType;
    private String scope;
    private String appliedRules;
    private Map<String, Object> context;
    
    // Constructors
    public ValidationResponse() {
        this.validationId = "val_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.validatedAt = LocalDateTime.now();
        this.recordResults = new java.util.ArrayList<>();
        this.issues = new java.util.ArrayList<>();
        this.warnings = new java.util.ArrayList<>();
        this.complianceViolations = new java.util.ArrayList<>();
        this.recommendations = new java.util.ArrayList<>();
        this.suggestedFixes = new java.util.ArrayList<>();
        this.metrics = new java.util.HashMap<>();
    }
    
    public ValidationResponse(String requestId) {
        this();
        this.requestId = requestId;
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getValidationId() { return validationId; }
    public void setValidationId(String validationId) { this.validationId = validationId; }
    
    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
    
    public int getValidRecords() { return validRecords; }
    public void setValidRecords(int validRecords) { this.validRecords = validRecords; }
    
    public int getInvalidRecords() { return invalidRecords; }
    public void setInvalidRecords(int invalidRecords) { this.invalidRecords = invalidRecords; }
    
    public double getValidityPercentage() { return validityPercentage; }
    public void setValidityPercentage(double validityPercentage) { this.validityPercentage = validityPercentage; }
    
    public List<ValidationResult> getRecordResults() { return recordResults; }
    public void setRecordResults(List<ValidationResult> recordResults) { this.recordResults = recordResults; }
    
    public List<ValidationIssue> getIssues() { return issues; }
    public void setIssues(List<ValidationIssue> issues) { this.issues = issues; }
    
    public List<ValidationWarning> getWarnings() { return warnings; }
    public void setWarnings(List<ValidationWarning> warnings) { this.warnings = warnings; }
    
    public ComplianceResult getComplianceResult() { return complianceResult; }
    public void setComplianceResult(ComplianceResult complianceResult) { this.complianceResult = complianceResult; }
    
    public List<ComplianceViolation> getComplianceViolations() { return complianceViolations; }
    public void setComplianceViolations(List<ComplianceViolation> complianceViolations) { this.complianceViolations = complianceViolations; }
    
    public ValidationStatistics getStatistics() { return statistics; }
    public void setStatistics(ValidationStatistics statistics) { this.statistics = statistics; }
    
    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public List<String> getSuggestedFixes() { return suggestedFixes; }
    public void setSuggestedFixes(List<String> suggestedFixes) { this.suggestedFixes = suggestedFixes; }
    
    public DataQualityReport getQualityReport() { return qualityReport; }
    public void setQualityReport(DataQualityReport qualityReport) { this.qualityReport = qualityReport; }
    
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    
    public String getAppliedRules() { return appliedRules; }
    public void setAppliedRules(String appliedRules) { this.appliedRules = appliedRules; }
    
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    
    // Helper methods
    public void markCompleted(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
        this.validatedAt = LocalDateTime.now();
        calculateValidityPercentage();
    }
    
    public void calculateValidityPercentage() {
        if (totalRecords > 0) {
            this.validityPercentage = (validRecords * 100.0) / totalRecords;
            this.isValid = validityPercentage >= 80.0; // 80% threshold
        }
    }
    
    public void addRecordResult(ValidationResult result) {
        if (recordResults != null && result != null) {
            recordResults.add(result);
        }
    }
    
    public void addIssue(ValidationIssue issue) {
        if (issues != null && issue != null) {
            issues.add(issue);
        }
    }
    
    public void addWarning(ValidationWarning warning) {
        if (warnings != null && warning != null) {
            warnings.add(warning);
        }
    }
    
    public void addComplianceViolation(ComplianceViolation violation) {
        if (complianceViolations != null && violation != null) {
            complianceViolations.add(violation);
        }
    }
    
    public void addRecommendation(String recommendation) {
        if (recommendations != null && recommendation != null) {
            recommendations.add(recommendation);
        }
    }
    
    public void addSuggestedFix(String fix) {
        if (suggestedFixes != null && fix != null) {
            suggestedFixes.add(fix);
        }
    }
    
    public void addMetric(String key, Object value) {
        if (metrics != null && key != null) {
            metrics.put(key, value);
        }
    }
    
    public boolean hasIssues() {
        return issues != null && !issues.isEmpty();
    }
    
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    public boolean hasComplianceViolations() {
        return complianceViolations != null && !complianceViolations.isEmpty();
    }
    
    public boolean isHighQuality() {
        return overallScore >= 85.0;
    }
    
    public boolean needsAttention() {
        return hasIssues() || hasComplianceViolations() || overallScore < 70.0;
    }
    
    // Static factory methods
    public static ValidationResponse success(String requestId, int validRecords, int totalRecords) {
        ValidationResponse response = new ValidationResponse(requestId);
        response.setValid(true);
        response.setMessage("Validation completed successfully");
        response.setValidRecords(validRecords);
        response.setTotalRecords(totalRecords);
        response.setInvalidRecords(totalRecords - validRecords);
        response.setOverallScore(90.0);
        response.calculateValidityPercentage();
        return response;
    }
    
    public static ValidationResponse failure(String requestId, String errorMessage) {
        ValidationResponse response = new ValidationResponse(requestId);
        response.setValid(false);
        response.setMessage("Validation failed: " + errorMessage);
        response.setTotalRecords(0);
        response.setValidRecords(0);
        response.setInvalidRecords(0);
        response.setOverallScore(0.0);
        response.addIssue(new ValidationIssue("VALIDATION_FAILED", errorMessage, ValidationIssue.Severity.CRITICAL));
        return response;
    }
    
    // Inner classes
    public static class ValidationResult {
        private String recordId;
        private boolean isValid;
        private String status; // VALID, INVALID, WARNING
        private List<ValidationIssue> issues;
        private List<ValidationWarning> warnings;
        private Map<String, Object> fieldResults;
        
        public ValidationResult() {}
        
        public ValidationResult(String recordId) {
            this.recordId = recordId;
            this.issues = new java.util.ArrayList<>();
            this.warnings = new java.util.ArrayList<>();
            this.fieldResults = new java.util.HashMap<>();
        }
        
        // Getters and Setters
        public String getRecordId() { return recordId; }
        public void setRecordId(String recordId) { this.recordId = recordId; }
        
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public List<ValidationIssue> getIssues() { return issues; }
        public void setIssues(List<ValidationIssue> issues) { this.issues = issues; }
        
        public List<ValidationWarning> getWarnings() { return warnings; }
        public void setWarnings(List<ValidationWarning> warnings) { this.warnings = warnings; }
        
        public Map<String, Object> getFieldResults() { return fieldResults; }
        public void setFieldResults(Map<String, Object> fieldResults) { this.fieldResults = fieldResults; }
    }
    
    public static class ValidationIssue {
        private String code;
        private String message;
        private String field;
        private Severity severity;
        private String suggestion;
        
        public enum Severity {
            INFO, WARNING, ERROR, CRITICAL
        }
        
        public ValidationIssue() {}
        
        public ValidationIssue(String code, String message, Severity severity) {
            this.code = code;
            this.message = message;
            this.severity = severity;
        }
        
        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public Severity getSeverity() { return severity; }
        public void setSeverity(Severity severity) { this.severity = severity; }
        
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    }
    
    public static class ValidationWarning {
        private String code;
        private String message;
        private String field;
        private String recommendation;
        
        public ValidationWarning() {}
        
        public ValidationWarning(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    public static class ComplianceResult {
        private String standard;
        private boolean isCompliant;
        private double complianceScore;
        private List<String> requirements;
        private List<String> metRequirements;
        private List<String> failedRequirements;
        
        public ComplianceResult() {}
        
        public ComplianceResult(String standard) {
            this.standard = standard;
            this.requirements = new java.util.ArrayList<>();
            this.metRequirements = new java.util.ArrayList<>();
            this.failedRequirements = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public String getStandard() { return standard; }
        public void setStandard(String standard) { this.standard = standard; }
        
        public boolean isCompliant() { return isCompliant; }
        public void setCompliant(boolean compliant) { isCompliant = compliant; }
        
        public double getComplianceScore() { return complianceScore; }
        public void setComplianceScore(double complianceScore) { this.complianceScore = complianceScore; }
        
        public List<String> getRequirements() { return requirements; }
        public void setRequirements(List<String> requirements) { this.requirements = requirements; }
        
        public List<String> getMetRequirements() { return metRequirements; }
        public void setMetRequirements(List<String> metRequirements) { this.metRequirements = metRequirements; }
        
        public List<String> getFailedRequirements() { return failedRequirements; }
        public void setFailedRequirements(List<String> failedRequirements) { this.failedRequirements = failedRequirements; }
    }
    
    public static class ComplianceViolation {
        private String requirement;
        private String description;
        private String severity;
        private String remediation;
        
        public ComplianceViolation() {}
        
        public ComplianceViolation(String requirement, String description) {
            this.requirement = requirement;
            this.description = description;
        }
        
        // Getters and Setters
        public String getRequirement() { return requirement; }
        public void setRequirement(String requirement) { this.requirement = requirement; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getRemediation() { return remediation; }
        public void setRemediation(String remediation) { this.remediation = remediation; }
    }
    
    public static class ValidationStatistics {
        private int totalChecks;
        private int passedChecks;
        private int failedChecks;
        private int warningChecks;
        private double passRate;
        private Map<String, Integer> checksByType;
        
        public ValidationStatistics() {
            this.checksByType = new java.util.HashMap<>();
        }
        
        // Getters and Setters
        public int getTotalChecks() { return totalChecks; }
        public void setTotalChecks(int totalChecks) { this.totalChecks = totalChecks; }
        
        public int getPassedChecks() { return passedChecks; }
        public void setPassedChecks(int passedChecks) { this.passedChecks = passedChecks; }
        
        public int getFailedChecks() { return failedChecks; }
        public void setFailedChecks(int failedChecks) { this.failedChecks = failedChecks; }
        
        public int getWarningChecks() { return warningChecks; }
        public void setWarningChecks(int warningChecks) { this.warningChecks = warningChecks; }
        
        public double getPassRate() { return passRate; }
        public void setPassRate(double passRate) { this.passRate = passRate; }
        
        public Map<String, Integer> getChecksByType() { return checksByType; }
        public void setChecksByType(Map<String, Integer> checksByType) { this.checksByType = checksByType; }
    }
    
    public static class DataQualityReport {
        private double completeness;
        private double accuracy;
        private double consistency;
        private double uniqueness;
        private double validity;
        private double overallQuality;
        private Map<String, Double> qualityDimensions;
        
        public DataQualityReport() {
            this.qualityDimensions = new java.util.HashMap<>();
        }
        
        // Getters and Setters
        public double getCompleteness() { return completeness; }
        public void setCompleteness(double completeness) { this.completeness = completeness; }
        
        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
        
        public double getConsistency() { return consistency; }
        public void setConsistency(double consistency) { this.consistency = consistency; }
        
        public double getUniqueness() { return uniqueness; }
        public void setUniqueness(double uniqueness) { this.uniqueness = uniqueness; }
        
        public double getValidity() { return validity; }
        public void setValidity(double validity) { this.validity = validity; }
        
        public double getOverallQuality() { return overallQuality; }
        public void setOverallQuality(double overallQuality) { this.overallQuality = overallQuality; }
        
        public Map<String, Double> getQualityDimensions() { return qualityDimensions; }
        public void setQualityDimensions(Map<String, Double> qualityDimensions) { this.qualityDimensions = qualityDimensions; }
    }
    
    @Override
    public String toString() {
        return "ValidationResponse{" +
                "requestId='" + requestId + '\'' +
                ", validationId='" + validationId + '\'' +
                ", isValid=" + isValid +
                ", validityPercentage=" + String.format("%.1f%%", validityPercentage) +
                ", validRecords=" + validRecords +
                "/" + totalRecords +
                ", overallScore=" + String.format("%.1f", overallScore) +
                '}';
    }
}