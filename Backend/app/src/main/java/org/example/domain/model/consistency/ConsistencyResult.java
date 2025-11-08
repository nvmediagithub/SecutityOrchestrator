package org.example.domain.model.consistency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Результат проверки консистентности
 */
public class ConsistencyResult {
    
    private String checkId;
    private String apiSpecId;
    private String bpmnDiagramId;
    private LocalDateTime checkStartTime;
    private LocalDateTime checkEndTime;
    private long processingTimeMs;
    private String status;
    private String validationLevel;
    private double overallConsistencyScore;
    
    // Результаты различных типов проверок
    private List<PartialCheckResult> partialResults;
    private Map<String, Object> apiBpmnConsistencyResults;
    private Map<String, Object> schemaAlignmentResults;
    private Map<String, Object> businessRuleConsistencyResults;
    private Map<String, Object> dataFlowValidationResults;
    private Map<String, Object> securityConsistencyResults;
    private Map<String, Object> testCoverageResults;
    
    // Метаданные и отчеты
    private DataCompletenessCheck dataCompletenessCheck;
    private RelationshipValidation relationshipValidation;
    private BusinessRuleCompliance businessRuleCompliance;
    private EndToEndValidation endToEndValidation;
    private ConsistencyReport summaryReport;
    private List<Object> qualityReports;
    private List<Object> gapAnalyses;
    private List<String> recommendations;
    
    // Статистика
    private ConsistencyStatistics statistics;
    
    public ConsistencyResult() {
        this.checkStartTime = LocalDateTime.now();
        this.status = "PENDING";
        this.partialResults = new java.util.ArrayList<>();
    }
    
    // Вложенные классы для результатов
    public static class DataCompletenessCheck {
        private boolean isComplete;
        private List<String> missingFields;
        private double completenessScore;
        
        public boolean isComplete() { return isComplete; }
        public void setComplete(boolean complete) { isComplete = complete; }
        
        public List<String> getMissingFields() { return missingFields; }
        public void setMissingFields(List<String> missingFields) { this.missingFields = missingFields; }
        
        public double getCompletenessScore() { return completenessScore; }
        public void setCompletenessScore(double completenessScore) { this.completenessScore = completenessScore; }
    }
    
    public static class RelationshipValidation {
        private boolean isValid;
        private List<String> invalidRelationships;
        private double validationScore;
        
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }
        
        public List<String> getInvalidRelationships() { return invalidRelationships; }
        public void setInvalidRelationships(List<String> invalidRelationships) { this.invalidRelationships = invalidRelationships; }
        
        public double getValidationScore() { return validationScore; }
        public void setValidationScore(double validationScore) { this.validationScore = validationScore; }
    }
    
    public static class BusinessRuleCompliance {
        private boolean isCompliant;
        private List<String> violations;
        private double complianceScore;
        
        public boolean isCompliant() { return isCompliant; }
        public void setCompliant(boolean compliant) { isCompliant = compliant; }
        
        public List<String> getViolations() { return violations; }
        public void setViolations(List<String> violations) { this.violations = violations; }
        
        public double getComplianceScore() { return complianceScore; }
        public void setComplianceScore(double complianceScore) { this.complianceScore = complianceScore; }
    }
    
    public static class EndToEndValidation {
        private boolean isValid;
        private List<String> validationErrors;
        private double endToEndScore;
        
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }
        
        public List<String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(List<String> validationErrors) { this.validationErrors = validationErrors; }
        
        public double getEndToEndScore() { return endToEndScore; }
        public void setEndToEndScore(double endToEndScore) { this.endToEndScore = endToEndScore; }
    }
    
    public static class ConsistencyReport {
        private String summary;
        private int totalIssues;
        private int criticalIssues;
        private int mediumIssues;
        private int lowIssues;
        private List<String> recommendations;
        
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        
        public int getTotalIssues() { return totalIssues; }
        public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
        
        public int getCriticalIssues() { return criticalIssues; }
        public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
        
        public int getMediumIssues() { return mediumIssues; }
        public void setMediumIssues(int mediumIssues) { this.mediumIssues = mediumIssues; }
        
        public int getLowIssues() { return lowIssues; }
        public void setLowIssues(int lowIssues) { this.lowIssues = lowIssues; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
    
    public static class ConsistencyStatistics {
        private int totalApiEndpoints;
        private int totalBpmnTasks;
        private int totalChecksPerformed;
        private int totalPassedChecks;
        private int totalFailedChecks;
        private int totalWarningChecks;
        private double averageConsistencyScore;
        private double apiCoveragePercentage;
        private double bpmnCoveragePercentage;
        private int criticalIssues;
        private int mediumIssues;
        private int lowIssues;
        
        // Getters and Setters
        public int getTotalApiEndpoints() { return totalApiEndpoints; }
        public void setTotalApiEndpoints(int totalApiEndpoints) { this.totalApiEndpoints = totalApiEndpoints; }
        
        public int getTotalBpmnTasks() { return totalBpmnTasks; }
        public void setTotalBpmnTasks(int totalBpmnTasks) { this.totalBpmnTasks = totalBpmnTasks; }
        
        public int getTotalChecksPerformed() { return totalChecksPerformed; }
        public void setTotalChecksPerformed(int totalChecksPerformed) { this.totalChecksPerformed = totalChecksPerformed; }
        
        public int getTotalPassedChecks() { return totalPassedChecks; }
        public void setTotalPassedChecks(int totalPassedChecks) { this.totalPassedChecks = totalPassedChecks; }
        
        public int getTotalFailedChecks() { return totalFailedChecks; }
        public void setTotalFailedChecks(int totalFailedChecks) { this.totalFailedChecks = totalFailedChecks; }
        
        public int getTotalWarningChecks() { return totalWarningChecks; }
        public void setTotalWarningChecks(int totalWarningChecks) { this.totalWarningChecks = totalWarningChecks; }
        
        public double getAverageConsistencyScore() { return averageConsistencyScore; }
        public void setAverageConsistencyScore(double averageConsistencyScore) { this.averageConsistencyScore = averageConsistencyScore; }
        
        public double getApiCoveragePercentage() { return apiCoveragePercentage; }
        public void setApiCoveragePercentage(double apiCoveragePercentage) { this.apiCoveragePercentage = apiCoveragePercentage; }
        
        public double getBpmnCoveragePercentage() { return bpmnCoveragePercentage; }
        public void setBpmnCoveragePercentage(double bpmnCoveragePercentage) { this.bpmnCoveragePercentage = bpmnCoveragePercentage; }
        
        public int getCriticalIssues() { return criticalIssues; }
        public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
        
        public int getMediumIssues() { return mediumIssues; }
        public void setMediumIssues(int mediumIssues) { this.mediumIssues = mediumIssues; }
        
        public int getLowIssues() { return lowIssues; }
        public void setLowIssues(int lowIssues) { this.lowIssues = lowIssues; }
    }
    
    // Getters and Setters
    public String getCheckId() { return checkId; }
    public void setCheckId(String checkId) { this.checkId = checkId; }
    
    public String getApiSpecId() { return apiSpecId; }
    public void setApiSpecId(String apiSpecId) { this.apiSpecId = apiSpecId; }
    
    public String getBpmnDiagramId() { return bpmnDiagramId; }
    public void setBpmnDiagramId(String bpmnDiagramId) { this.bpmnDiagramId = bpmnDiagramId; }
    
    public LocalDateTime getCheckStartTime() { return checkStartTime; }
    public void setCheckStartTime(LocalDateTime checkStartTime) { this.checkStartTime = checkStartTime; }
    
    public LocalDateTime getCheckEndTime() { return checkEndTime; }
    public void setCheckEndTime(LocalDateTime checkEndTime) { this.checkEndTime = checkEndTime; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getValidationLevel() { return validationLevel; }
    public void setValidationLevel(String validationLevel) { this.validationLevel = validationLevel; }
    
    public double getOverallConsistencyScore() { return overallConsistencyScore; }
    public void setOverallConsistencyScore(double overallConsistencyScore) { this.overallConsistencyScore = overallConsistencyScore; }
    
    public List<PartialCheckResult> getPartialResults() { return partialResults; }
    public void setPartialResults(List<PartialCheckResult> partialResults) { this.partialResults = partialResults; }
    
    public Map<String, Object> getApiBpmnConsistencyResults() { return apiBpmnConsistencyResults; }
    public void setApiBpmnConsistencyResults(Map<String, Object> apiBpmnConsistencyResults) { this.apiBpmnConsistencyResults = apiBpmnConsistencyResults; }
    
    public Map<String, Object> getSchemaAlignmentResults() { return schemaAlignmentResults; }
    public void setSchemaAlignmentResults(Map<String, Object> schemaAlignmentResults) { this.schemaAlignmentResults = schemaAlignmentResults; }
    
    public Map<String, Object> getBusinessRuleConsistencyResults() { return businessRuleConsistencyResults; }
    public void setBusinessRuleConsistencyResults(Map<String, Object> businessRuleConsistencyResults) { this.businessRuleConsistencyResults = businessRuleConsistencyResults; }
    
    public Map<String, Object> getDataFlowValidationResults() { return dataFlowValidationResults; }
    public void setDataFlowValidationResults(Map<String, Object> dataFlowValidationResults) { this.dataFlowValidationResults = dataFlowValidationResults; }
    
    public Map<String, Object> getSecurityConsistencyResults() { return securityConsistencyResults; }
    public void setSecurityConsistencyResults(Map<String, Object> securityConsistencyResults) { this.securityConsistencyResults = securityConsistencyResults; }
    
    public Map<String, Object> getTestCoverageResults() { return testCoverageResults; }
    public void setTestCoverageResults(Map<String, Object> testCoverageResults) { this.testCoverageResults = testCoverageResults; }
    
    public DataCompletenessCheck getDataCompletenessCheck() { return dataCompletenessCheck; }
    public void setDataCompletenessCheck(DataCompletenessCheck dataCompletenessCheck) { this.dataCompletenessCheck = dataCompletenessCheck; }
    
    public RelationshipValidation getRelationshipValidation() { return relationshipValidation; }
    public void setRelationshipValidation(RelationshipValidation relationshipValidation) { this.relationshipValidation = relationshipValidation; }
    
    public BusinessRuleCompliance getBusinessRuleCompliance() { return businessRuleCompliance; }
    public void setBusinessRuleCompliance(BusinessRuleCompliance businessRuleCompliance) { this.businessRuleCompliance = businessRuleCompliance; }
    
    public EndToEndValidation getEndToEndValidation() { return endToEndValidation; }
    public void setEndToEndValidation(EndToEndValidation endToEndValidation) { this.endToEndValidation = endToEndValidation; }
    
    public ConsistencyReport getSummaryReport() { return summaryReport; }
    public void setSummaryReport(ConsistencyReport summaryReport) { this.summaryReport = summaryReport; }
    
    public List<Object> getQualityReports() { return qualityReports; }
    public void setQualityReports(List<Object> qualityReports) { this.qualityReports = qualityReports; }
    
    public List<Object> getGapAnalyses() { return gapAnalyses; }
    public void setGapAnalyses(List<Object> gapAnalyses) { this.gapAnalyses = gapAnalyses; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public ConsistencyStatistics getStatistics() { return statistics; }
    public void setStatistics(ConsistencyStatistics statistics) { this.statistics = statistics; }
}