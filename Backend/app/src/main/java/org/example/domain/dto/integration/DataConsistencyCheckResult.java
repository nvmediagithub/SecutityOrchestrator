package org.example.domain.dto.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Результат проверки согласованности данных
 */
public class DataConsistencyCheckResult {
    
    private String checkId;
    private String apiSpecId;
    private String bpmnDiagramId;
    private LocalDateTime checkStartTime;
    private LocalDateTime checkEndTime;
    private long processingTimeMs;
    private String status; // SUCCESS, FAILED, PARTIAL, WARNING
    private String errorMessage;
    private double overallConsistencyScore;
    private String validationLevel;
    
    // Результаты проверки
    private List<ApiBpmnConsistencyCheck> apiBpmnConsistencyResults;
    private List<SchemaAlignmentCheck> schemaAlignmentResults;
    private List<BusinessRuleConsistencyCheck> businessRuleConsistencyResults;
    private List<DataFlowValidation> dataFlowValidationResults;
    private List<SecurityConsistencyCheck> securityConsistencyResults;
    private List<TestCoverageAnalysis> testCoverageResults;
    
    // Качественные показатели
    private DataCompletenessCheck dataCompletenessCheck;
    private RelationshipValidation relationshipValidation;
    private BusinessRuleCompliance businessRuleCompliance;
    private EndToEndValidation endToEndValidation;
    
    // Отчеты
    private ConsistencyReport summaryReport;
    private List<QualityAssuranceReport> qualityReports;
    private List<GapAnalysis> gapAnalyses;
    private List<ConsistencyRecommendation> recommendations;
    
    // Статистика
    private ConsistencyStatistics statistics;
    
    public DataConsistencyCheckResult() {
        this.checkStartTime = LocalDateTime.now();
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
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public double getOverallConsistencyScore() { return overallConsistencyScore; }
    public void setOverallConsistencyScore(double overallConsistencyScore) { this.overallConsistencyScore = overallConsistencyScore; }
    
    public String getValidationLevel() { return validationLevel; }
    public void setValidationLevel(String validationLevel) { this.validationLevel = validationLevel; }
    
    public List<ApiBpmnConsistencyCheck> getApiBpmnConsistencyResults() { return apiBpmnConsistencyResults; }
    public void setApiBpmnConsistencyResults(List<ApiBpmnConsistencyCheck> apiBpmnConsistencyResults) { this.apiBpmnConsistencyResults = apiBpmnConsistencyResults; }
    
    public List<SchemaAlignmentCheck> getSchemaAlignmentResults() { return schemaAlignmentResults; }
    public void setSchemaAlignmentResults(List<SchemaAlignmentCheck> schemaAlignmentResults) { this.schemaAlignmentResults = schemaAlignmentResults; }
    
    public List<BusinessRuleConsistencyCheck> getBusinessRuleConsistencyResults() { return businessRuleConsistencyResults; }
    public void setBusinessRuleConsistencyResults(List<BusinessRuleConsistencyCheck> businessRuleConsistencyResults) { this.businessRuleConsistencyResults = businessRuleConsistencyResults; }
    
    public List<DataFlowValidation> getDataFlowValidationResults() { return dataFlowValidationResults; }
    public void setDataFlowValidationResults(List<DataFlowValidation> dataFlowValidationResults) { this.dataFlowValidationResults = dataFlowValidationResults; }
    
    public List<SecurityConsistencyCheck> getSecurityConsistencyResults() { return securityConsistencyResults; }
    public void setSecurityConsistencyResults(List<SecurityConsistencyCheck> securityConsistencyResults) { this.securityConsistencyResults = securityConsistencyResults; }
    
    public List<TestCoverageAnalysis> getTestCoverageResults() { return testCoverageResults; }
    public void setTestCoverageResults(List<TestCoverageAnalysis> testCoverageResults) { this.testCoverageResults = testCoverageResults; }
    
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
    
    public List<QualityAssuranceReport> getQualityReports() { return qualityReports; }
    public void setQualityReports(List<QualityAssuranceReport> qualityReports) { this.qualityReports = qualityReports; }
    
    public List<GapAnalysis> getGapAnalyses() { return gapAnalyses; }
    public void setGapAnalyses(List<GapAnalysis> gapAnalyses) { this.gapAnalyses = gapAnalyses; }
    
    public List<ConsistencyRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<ConsistencyRecommendation> recommendations) { this.recommendations = recommendations; }
    
    public ConsistencyStatistics getStatistics() { return statistics; }
    public void setStatistics(ConsistencyStatistics statistics) { this.statistics = statistics; }
    
    // Вложенные классы для структуры данных
    
    public static class ApiBpmnConsistencyCheck {
        private String checkId;
        private String name;
        private String checkType; // ENDPOINT_TASK_MATCH, DATA_FIELD_CONSISTENCY, etc.
        private String status; // PASS, FAIL, WARNING
        private double score;
        private String description;
        private List<String> affectedApiItems;
        private List<String> affectedBpmnItems;
        private String recommendation;
        
        // Getters and Setters
        public String getCheckId() { return checkId; }
        public void setCheckId(String checkId) { this.checkId = checkId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCheckType() { return checkType; }
        public void setCheckType(String checkType) { this.checkType = checkType; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getAffectedApiItems() { return affectedApiItems; }
        public void setAffectedApiItems(List<String> affectedApiItems) { this.affectedApiItems = affectedApiItems; }
        
        public List<String> getAffectedBpmnItems() { return affectedBpmnItems; }
        public void setAffectedBpmnItems(List<String> affectedBpmnItems) { this.affectedBpmnItems = affectedBpmnItems; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    public static class SchemaAlignmentCheck {
        private String checkId;
        private String apiSchemaName;
        private String bpmnDataType;
        private String alignmentStatus; // ALIGNED, PARTIALLY_ALIGNED, MISALIGNED
        private double alignmentScore;
        private List<String> commonFields;
        private List<String> mismatchedFields;
        private String description;
        
        // Getters and Setters
        public String getCheckId() { return checkId; }
        public void setCheckId(String checkId) { this.checkId = checkId; }
        
        public String getApiSchemaName() { return apiSchemaName; }
        public void setApiSchemaName(String apiSchemaName) { this.apiSchemaName = apiSchemaName; }
        
        public String getBpmnDataType() { return bpmnDataType; }
        public void setBpmnDataType(String bpmnDataType) { this.bpmnDataType = bpmnDataType; }
        
        public String getAlignmentStatus() { return alignmentStatus; }
        public void setAlignmentStatus(String alignmentStatus) { this.alignmentStatus = alignmentStatus; }
        
        public double getAlignmentScore() { return alignmentScore; }
        public void setAlignmentScore(double alignmentScore) { this.alignmentScore = alignmentScore; }
        
        public List<String> getCommonFields() { return commonFields; }
        public void setCommonFields(List<String> commonFields) { this.commonFields = commonFields; }
        
        public List<String> getMismatchedFields() { return mismatchedFields; }
        public void setMismatchedFields(List<String> mismatchedFields) { this.mismatchedFields = mismatchedFields; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class BusinessRuleConsistencyCheck {
        private String checkId;
        private String ruleName;
        private String ruleType;
        private String apiImplementation;
        private String bpmnImplementation;
        private String consistencyStatus; // CONSISTENT, INCONSISTENT, PARTIAL
        private double consistencyScore;
        private String description;
        private List<String> discrepancies;
        
        // Getters and Setters
        public String getCheckId() { return checkId; }
        public void setCheckId(String checkId) { this.checkId = checkId; }
        
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        
        public String getRuleType() { return ruleType; }
        public void setRuleType(String ruleType) { this.ruleType = ruleType; }
        
        public String getApiImplementation() { return apiImplementation; }
        public void setApiImplementation(String apiImplementation) { this.apiImplementation = apiImplementation; }
        
        public String getBpmnImplementation() { return bpmnImplementation; }
        public void setBpmnImplementation(String bpmnImplementation) { this.bpmnImplementation = bpmnImplementation; }
        
        public String getConsistencyStatus() { return consistencyStatus; }
        public void setConsistencyStatus(String consistencyStatus) { this.consistencyStatus = consistencyStatus; }
        
        public double getConsistencyScore() { return consistencyScore; }
        public void setConsistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getDiscrepancies() { return discrepancies; }
        public void setDiscrepancies(List<String> discrepancies) { this.discrepancies = discrepancies; }
    }
    
    public static class DataFlowValidation {
        private String validationId;
        private String flowName;
        private String flowType; // API_TO_BPMN, BPMN_TO_API, BIDIRECTIONAL
        private String status; // VALID, INVALID, PARTIAL
        private double validationScore;
        private List<String> validationSteps;
        private List<String> failedValidations;
        private String description;
        
        // Getters and Setters
        public String getValidationId() { return validationId; }
        public void setValidationId(String validationId) { this.validationId = validationId; }
        
        public String getFlowName() { return flowName; }
        public void setFlowName(String flowName) { this.flowName = flowName; }
        
        public String getFlowType() { return flowType; }
        public void setFlowType(String flowType) { this.flowType = flowType; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public double getValidationScore() { return validationScore; }
        public void setValidationScore(double validationScore) { this.validationScore = validationScore; }
        
        public List<String> getValidationSteps() { return validationSteps; }
        public void setValidationSteps(List<String> validationSteps) { this.validationSteps = validationSteps; }
        
        public List<String> getFailedValidations() { return failedValidations; }
        public void setFailedValidations(List<String> failedValidations) { this.failedValidations = failedValidations; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class SecurityConsistencyCheck {
        private String checkId;
        private String securityAspect; // AUTHENTICATION, AUTHORIZATION, DATA_PROTECTION
        private String apiSecurityLevel;
        private String bpmnSecurityLevel;
        private String consistencyStatus; // CONSISTENT, INCONSISTENT, MISSING
        public double consistencyScore;
        private String description;
        private List<String> securityGaps;
        
        // Getters and Setters
        public String getCheckId() { return checkId; }
        public void setCheckId(String checkId) { this.checkId = checkId; }
        
        public String getSecurityAspect() { return securityAspect; }
        public void setSecurityAspect(String securityAspect) { this.securityAspect = securityAspect; }
        
        public String getApiSecurityLevel() { return apiSecurityLevel; }
        public void setApiSecurityLevel(String apiSecurityLevel) { this.apiSecurityLevel = apiSecurityLevel; }
        
        public String getBpmnSecurityLevel() { return bpmnSecurityLevel; }
        public void setBpmnSecurityLevel(String bpmnSecurityLevel) { this.bpmnSecurityLevel = bpmnSecurityLevel; }
        
        public String getConsistencyStatus() { return consistencyStatus; }
        public void setConsistencyStatus(String consistencyStatus) { this.consistencyStatus = consistencyStatus; }
        
        public double getConsistencyScore() { return consistencyScore; }
        public void setConsistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getSecurityGaps() { return securityGaps; }
        public void setSecurityGaps(List<String> securityGaps) { this.securityGaps = securityGaps; }
    }
    
    public static class TestCoverageAnalysis {
        private String analysisId;
        private String coverageType; // API_COVERAGE, BPMN_COVERAGE, INTEGRATION_COVERAGE
        private double coveragePercentage;
        private int totalItems;
        private int coveredItems;
        private List<String> coveredScenarios;
        private List<String> uncoveredScenarios;
        private String description;
        
        // Getters and Setters
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
        
        public String getCoverageType() { return coverageType; }
        public void setCoverageType(String coverageType) { this.coverageType = coverageType; }
        
        public double getCoveragePercentage() { return coveragePercentage; }
        public void setCoveragePercentage(double coveragePercentage) { this.coveragePercentage = coveragePercentage; }
        
        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
        
        public int getCoveredItems() { return coveredItems; }
        public void setCoveredItems(int coveredItems) { this.coveredItems = coveredItems; }
        
        public List<String> getCoveredScenarios() { return coveredScenarios; }
        public void setCoveredScenarios(List<String> coveredScenarios) { this.coveredScenarios = coveredScenarios; }
        
        public List<String> getUncoveredScenarios() { return uncoveredScenarios; }
        public void setUncoveredScenarios(List<String> uncoveredScenarios) { this.uncoveredScenarios = uncoveredScenarios; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class DataCompletenessCheck {
        private String checkId;
        private double completenessScore;
        private int totalFields;
        private int populatedFields;
        private List<String> missingFields;
        private List<String> partiallyPopulatedFields;
        private String description;
        
        // Getters and Setters
        public String getCheckId() { return checkId; }
        public void setCheckId(String checkId) { this.checkId = checkId; }
        
        public double getCompletenessScore() { return completenessScore; }
        public void setCompletenessScore(double completenessScore) { this.completenessScore = completenessScore; }
        
        public int getTotalFields() { return totalFields; }
        public void setTotalFields(int totalFields) { this.totalFields = totalFields; }
        
        public int getPopulatedFields() { return populatedFields; }
        public void setPopulatedFields(int populatedFields) { this.populatedFields = populatedFields; }
        
        public List<String> getMissingFields() { return missingFields; }
        public void setMissingFields(List<String> missingFields) { this.missingFields = missingFields; }
        
        public List<String> getPartiallyPopulatedFields() { return partiallyPopulatedFields; }
        public void setPartiallyPopulatedFields(List<String> partiallyPopulatedFields) { this.partiallyPopulatedFields = partiallyPopulatedFields; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class RelationshipValidation {
        private String validationId;
        private String relationshipType;
        private double validationScore;
        private List<String> validRelationships;
        private List<String> invalidRelationships;
        private List<String> missingRelationships;
        private String description;
        
        // Getters and Setters
        public String getValidationId() { return validationId; }
        public void setValidationId(String validationId) { this.validationId = validationId; }
        
        public String getRelationshipType() { return relationshipType; }
        public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
        
        public double getValidationScore() { return validationScore; }
        public void setValidationScore(double validationScore) { this.validationScore = validationScore; }
        
        public List<String> getValidRelationships() { return validRelationships; }
        public void setValidRelationships(List<String> validRelationships) { this.validRelationships = validRelationships; }
        
        public List<String> getInvalidRelationships() { return invalidRelationships; }
        public void setInvalidRelationships(List<String> invalidRelationships) { this.invalidRelationships = invalidRelationships; }
        
        public List<String> getMissingRelationships() { return missingRelationships; }
        public void setMissingRelationships(List<String> missingRelationships) { this.missingRelationships = missingRelationships; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class BusinessRuleCompliance {
        private String complianceId;
        private String ruleName;
        private String complianceStatus; // COMPLIANT, NON_COMPLIANT, PARTIAL
        private double complianceScore;
        private List<String> compliantAspects;
        private List<String> nonCompliantAspects;
        private String description;
        
        // Getters and Setters
        public String getComplianceId() { return complianceId; }
        public void setComplianceId(String complianceId) { this.complianceId = complianceId; }
        
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        
        public String getComplianceStatus() { return complianceStatus; }
        public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
        
        public double getComplianceScore() { return complianceScore; }
        public void setComplianceScore(double complianceScore) { this.complianceScore = complianceScore; }
        
        public List<String> getCompliantAspects() { return compliantAspects; }
        public void setCompliantAspects(List<String> compliantAspects) { this.compliantAspects = compliantAspects; }
        
        public List<String> getNonCompliantAspects() { return nonCompliantAspects; }
        public void setNonCompliantAspects(List<String> nonCompliantAspects) { this.nonCompliantAspects = nonCompliantAspects; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class EndToEndValidation {
        private String validationId;
        private String scenarioName;
        private String validationStatus; // VALID, INVALID, PARTIAL
        private double validationScore;
        private List<String> validSteps;
        private List<String> invalidSteps;
        private String description;
        
        // Getters and Setters
        public String getValidationId() { return validationId; }
        public void setValidationId(String validationId) { this.validationId = validationId; }
        
        public String getScenarioName() { return scenarioName; }
        public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }
        
        public String getValidationStatus() { return validationStatus; }
        public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
        
        public double getValidationScore() { return validationScore; }
        public void setValidationScore(double validationScore) { this.validationScore = validationScore; }
        
        public List<String> getValidSteps() { return validSteps; }
        public void setValidSteps(List<String> validSteps) { this.validSteps = validSteps; }
        
        public List<String> getInvalidSteps() { return invalidSteps; }
        public void setInvalidSteps(List<String> invalidSteps) { this.invalidSteps = invalidSteps; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class ConsistencyReport {
        private String reportId;
        private String title;
        private String summary;
        private double overallScore;
        private int totalChecks;
        private int passedChecks;
        private int failedChecks;
        private int warningChecks;
        private List<String> keyFindings;
        private List<String> recommendations;
        
        // Getters and Setters
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        
        public int getTotalChecks() { return totalChecks; }
        public void setTotalChecks(int totalChecks) { this.totalChecks = totalChecks; }
        
        public int getPassedChecks() { return passedChecks; }
        public void setPassedChecks(int passedChecks) { this.passedChecks = passedChecks; }
        
        public int getFailedChecks() { return failedChecks; }
        public void setFailedChecks(int failedChecks) { this.failedChecks = failedChecks; }
        
        public int getWarningChecks() { return warningChecks; }
        public void setWarningChecks(int warningChecks) { this.warningChecks = warningChecks; }
        
        public List<String> getKeyFindings() { return keyFindings; }
        public void setKeyFindings(List<String> keyFindings) { this.keyFindings = keyFindings; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
    
    public static class QualityAssuranceReport {
        private String reportId;
        private String category; // DATA_QUALITY, CONSISTENCY, COMPLIANCE
        private String title;
        private double qualityScore;
        private List<String> findings;
        private List<String> issues;
        private List<String> recommendations;
        private String description;
        
        // Getters and Setters
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public double getQualityScore() { return qualityScore; }
        public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
        
        public List<String> getFindings() { return findings; }
        public void setFindings(List<String> findings) { this.findings = findings; }
        
        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class GapAnalysis {
        private String gapId;
        private String gapType; // DATA_GAP, LOGIC_GAP, COVERAGE_GAP
        private String description;
        private List<String> affectedComponents;
        private String impact; // HIGH, MEDIUM, LOW
        private String recommendation;
        private String priority; // CRITICAL, HIGH, MEDIUM, LOW
        
        // Getters and Setters
        public String getGapId() { return gapId; }
        public void setGapId(String gapId) { this.gapId = gapId; }
        
        public String getGapType() { return gapType; }
        public void setGapType(String gapType) { this.gapType = gapType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getAffectedComponents() { return affectedComponents; }
        public void setAffectedComponents(List<String> affectedComponents) { this.affectedComponents = affectedComponents; }
        
        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
    
    public static class ConsistencyRecommendation {
        private String recommendationId;
        private String title;
        private String description;
        private String category; // IMPROVEMENT, FIX, ENHANCEMENT
        private String priority; // HIGH, MEDIUM, LOW
        private List<String> steps;
        private String expectedBenefit;
        
        // Getters and Setters
        public String getRecommendationId() { return recommendationId; }
        public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public List<String> getSteps() { return steps; }
        public void setSteps(List<String> steps) { this.steps = steps; }
        
        public String getExpectedBenefit() { return expectedBenefit; }
        public void setExpectedBenefit(String expectedBenefit) { this.expectedBenefit = expectedBenefit; }
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
}