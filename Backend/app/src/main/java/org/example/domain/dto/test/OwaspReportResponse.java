package org.example.domain.dto.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OwaspReportResponse {
    private Long id;
    private String reportId;
    private String name;
    private String description;
    private String applicationName;
    private String applicationVersion;
    private String assessmentType;
    private String assessmentScope;
    private String overallStatus; // PENDING, COMPLIANT, PARTIALLY_COMPLIANT, NON_COMPLIANT, NOT_ASSESSED
    private String assessmentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastAssessment;
    private String assessor;
    private String owaspVersion;
    
    // OWASP Top 10 Categories Coverage
    private String a01_brokenAccessControl;
    private String a02_cryptographicFailures;
    private String a03_injection;
    private String a04_insecureDesign;
    private String a05_securityMisconfiguration;
    private String a06_vulnerableComponents;
    private String a07_identificationFailures;
    private String a08_softwareIntegrity;
    private String a09_loggingFailures;
    private String a10_serverSideRequestForgery;
    
    // API Security Top 10
    private String api01_brokenObjectLevelAuthorization;
    private String api02_brokenUserAuthentication;
    private String api03_excessiveDataExposure;
    private String api04_lackOfResourcesRateLimit;
    private String api05_brokenFunctionLevelAuthorization;
    private String api06_unrestrictedResourceConsumption;
    private String api07_brokenAuthentication;
    private String api08_improperAssetsManagement;
    private String api09_unsuccessfulSecurityConfiguration;
    private String api10_improperInventoryManagement;
    
    // Overall Metrics
    private Integer totalTests = 0;
    private Integer passedTests = 0;
    private Integer failedTests = 0;
    private Integer notTested = 0;
    private Integer warningTests = 0;
    
    private Double overallComplianceScore = 0.0;
    private Double riskScore = 0.0;
    private String riskLevel; // INFO, LOW, MEDIUM, HIGH, CRITICAL
    private String riskRating; // CRITICAL, HIGH, MEDIUM, LOW, MINIMAL
    
    // Detailed findings
    private String criticalFindings;
    private String highRiskFindings;
    private String mediumRiskFindings;
    private String lowRiskFindings;
    private String recommendations;
    private String executiveSummary;
    
    // Lists
    private List<String> testScenariosExecuted;
    private List<String> identifiedVulnerabilities;
    private List<String> securityControlsTested;
    private List<String> complianceGaps;
    private List<String> remediationActions;
    
    // Additional data
    private String reportFormat; // HTML, PDF, JSON, XML, CSV
    private String generatedBy;
    private Boolean isAutomatedReport = false;
    private Boolean isScheduledReport = false;
    private Boolean isPartialAssessment = false;
    private String environment;
    private String assessmentNotes;
    private String methodology;
    private String toolsUsed;
    private String standardsCompliance;
    private LocalDateTime nextAssessmentDate;
    private String reportStatus; // DRAFT, FINAL, ARCHIVED, SUPERSEDED
    private String supersededBy;
    
    public OwaspReportResponse() {}
    
    public OwaspReportResponse(String reportId, String name, String applicationName) {
        this.reportId = reportId;
        this.name = name;
        this.applicationName = applicationName;
    }
    
    // Helper methods
    public boolean isCompliant() {
        return "COMPLIANT".equals(overallStatus);
    }
    
    public boolean hasCriticalIssues() {
        return "CRITICAL".equals(riskLevel);
    }
    
    public boolean hasHighRisk() {
        return "HIGH".equals(riskLevel);
    }
    
    public void calculateComplianceScore() {
        int total = passedTests + failedTests + warningTests;
        if (total > 0) {
            this.overallComplianceScore = (double) passedTests / total * 100.0;
        }
    }
    
    public double getCompliancePercentage() {
        return overallComplianceScore;
    }
    
    public boolean isAssessmentComplete() {
        return totalTests > 0 && (passedTests + failedTests + warningTests) == totalTests;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
    
    public String getApplicationVersion() { return applicationVersion; }
    public void setApplicationVersion(String applicationVersion) { this.applicationVersion = applicationVersion; }
    
    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
    
    public String getAssessmentScope() { return assessmentScope; }
    public void setAssessmentScope(String assessmentScope) { this.assessmentScope = assessmentScope; }
    
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    
    public String getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(String assessmentDate) { this.assessmentDate = assessmentDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastAssessment() { return lastAssessment; }
    public void setLastAssessment(LocalDateTime lastAssessment) { this.lastAssessment = lastAssessment; }
    
    public String getAssessor() { return assessor; }
    public void setAssessor(String assessor) { this.assessor = assessor; }
    
    public String getOwaspVersion() { return owaspVersion; }
    public void setOwaspVersion(String owaspVersion) { this.owaspVersion = owaspVersion; }
    
    // OWASP Category Getters and Setters
    public String getA01_brokenAccessControl() { return a01_brokenAccessControl; }
    public void setA01_brokenAccessControl(String a01_brokenAccessControl) { this.a01_brokenAccessControl = a01_brokenAccessControl; }
    
    public String getA02_cryptographicFailures() { return a02_cryptographicFailures; }
    public void setA02_cryptographicFailures(String a02_cryptographicFailures) { this.a02_cryptographicFailures = a02_cryptographicFailures; }
    
    public String getA03_injection() { return a03_injection; }
    public void setA03_injection(String a03_injection) { this.a03_injection = a03_injection; }
    
    public String getA04_insecureDesign() { return a04_insecureDesign; }
    public void setA04_insecureDesign(String a04_insecureDesign) { this.a04_insecureDesign = a04_insecureDesign; }
    
    public String getA05_securityMisconfiguration() { return a05_securityMisconfiguration; }
    public void setA05_securityMisconfiguration(String a05_securityMisconfiguration) { this.a05_securityMisconfiguration = a05_securityMisconfiguration; }
    
    public String getA06_vulnerableComponents() { return a06_vulnerableComponents; }
    public void setA06_vulnerableComponents(String a06_vulnerableComponents) { this.a06_vulnerableComponents = a06_vulnerableComponents; }
    
    public String getA07_identificationFailures() { return a07_identificationFailures; }
    public void setA07_identificationFailures(String a07_identificationFailures) { this.a07_identificationFailures = a07_identificationFailures; }
    
    public String getA08_softwareIntegrity() { return a08_softwareIntegrity; }
    public void setA08_softwareIntegrity(String a08_softwareIntegrity) { this.a08_softwareIntegrity = a08_softwareIntegrity; }
    
    public String getA09_loggingFailures() { return a09_loggingFailures; }
    public void setA09_loggingFailures(String a09_loggingFailures) { this.a09_loggingFailures = a09_loggingFailures; }
    
    public String getA10_serverSideRequestForgery() { return a10_serverSideRequestForgery; }
    public void setA10_serverSideRequestForgery(String a10_serverSideRequestForgery) { this.a10_serverSideRequestForgery = a10_serverSideRequestForgery; }
    
    public String getApi01_brokenObjectLevelAuthorization() { return api01_brokenObjectLevelAuthorization; }
    public void setApi01_brokenObjectLevelAuthorization(String api01_brokenObjectLevelAuthorization) { this.api01_brokenObjectLevelAuthorization = api01_brokenObjectLevelAuthorization; }
    
    public String getApi02_brokenUserAuthentication() { return api02_brokenUserAuthentication; }
    public void setApi02_brokenUserAuthentication(String api02_brokenUserAuthentication) { this.api02_brokenUserAuthentication = api02_brokenUserAuthentication; }
    
    public String getApi03_excessiveDataExposure() { return api03_excessiveDataExposure; }
    public void setApi03_excessiveDataExposure(String api03_excessiveDataExposure) { this.api03_excessiveDataExposure = api03_excessiveDataExposure; }
    
    public String getApi04_lackOfResourcesRateLimit() { return api04_lackOfResourcesRateLimit; }
    public void setApi04_lackOfResourcesRateLimit(String api04_lackOfResourcesRateLimit) { this.api04_lackOfResourcesRateLimit = api04_lackOfResourcesRateLimit; }
    
    public String getApi05_brokenFunctionLevelAuthorization() { return api05_brokenFunctionLevelAuthorization; }
    public void setApi05_brokenFunctionLevelAuthorization(String api05_brokenFunctionLevelAuthorization) { this.api05_brokenFunctionLevelAuthorization = api05_brokenFunctionLevelAuthorization; }
    
    public String getApi06_unrestrictedResourceConsumption() { return api06_unrestrictedResourceConsumption; }
    public void setApi06_unrestrictedResourceConsumption(String api06_unrestrictedResourceConsumption) { this.api06_unrestrictedResourceConsumption = api06_unrestrictedResourceConsumption; }
    
    public String getApi07_brokenAuthentication() { return api07_brokenAuthentication; }
    public void setApi07_brokenAuthentication(String api07_brokenAuthentication) { this.api07_brokenAuthentication = api07_brokenAuthentication; }
    
    public String getApi08_improperAssetsManagement() { return api08_improperAssetsManagement; }
    public void setApi08_improperAssetsManagement(String api08_improperAssetsManagement) { this.api08_improperAssetsManagement = api08_improperAssetsManagement; }
    
    public String getApi09_unsuccessfulSecurityConfiguration() { return api09_unsuccessfulSecurityConfiguration; }
    public void setApi09_unsuccessfulSecurityConfiguration(String api09_unsuccessfulSecurityConfiguration) { this.api09_unsuccessfulSecurityConfiguration = api09_unsuccessfulSecurityConfiguration; }
    
    public String getApi10_improperInventoryManagement() { return api10_improperInventoryManagement; }
    public void setApi10_improperInventoryManagement(String api10_improperInventoryManagement) { this.api10_improperInventoryManagement = api10_improperInventoryManagement; }
    
    public Integer getTotalTests() { return totalTests; }
    public void setTotalTests(Integer totalTests) { this.totalTests = totalTests; }
    
    public Integer getPassedTests() { return passedTests; }
    public void setPassedTests(Integer passedTests) { this.passedTests = passedTests; }
    
    public Integer getFailedTests() { return failedTests; }
    public void setFailedTests(Integer failedTests) { this.failedTests = failedTests; }
    
    public Integer getNotTested() { return notTested; }
    public void setNotTested(Integer notTested) { this.notTested = notTested; }
    
    public Integer getWarningTests() { return warningTests; }
    public void setWarningTests(Integer warningTests) { this.warningTests = warningTests; }
    
    public Double getOverallComplianceScore() { return overallComplianceScore; }
    public void setOverallComplianceScore(Double overallComplianceScore) { this.overallComplianceScore = overallComplianceScore; }
    
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public String getRiskRating() { return riskRating; }
    public void setRiskRating(String riskRating) { this.riskRating = riskRating; }
    
    public String getCriticalFindings() { return criticalFindings; }
    public void setCriticalFindings(String criticalFindings) { this.criticalFindings = criticalFindings; }
    
    public String getHighRiskFindings() { return highRiskFindings; }
    public void setHighRiskFindings(String highRiskFindings) { this.highRiskFindings = highRiskFindings; }
    
    public String getMediumRiskFindings() { return mediumRiskFindings; }
    public void setMediumRiskFindings(String mediumRiskFindings) { this.mediumRiskFindings = mediumRiskFindings; }
    
    public String getLowRiskFindings() { return lowRiskFindings; }
    public void setLowRiskFindings(String lowRiskFindings) { this.lowRiskFindings = lowRiskFindings; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
    
    public List<String> getTestScenariosExecuted() { return testScenariosExecuted; }
    public void setTestScenariosExecuted(List<String> testScenariosExecuted) { this.testScenariosExecuted = testScenariosExecuted; }
    
    public List<String> getIdentifiedVulnerabilities() { return identifiedVulnerabilities; }
    public void setIdentifiedVulnerabilities(List<String> identifiedVulnerabilities) { this.identifiedVulnerabilities = identifiedVulnerabilities; }
    
    public List<String> getSecurityControlsTested() { return securityControlsTested; }
    public void setSecurityControlsTested(List<String> securityControlsTested) { this.securityControlsTested = securityControlsTested; }
    
    public List<String> getComplianceGaps() { return complianceGaps; }
    public void setComplianceGaps(List<String> complianceGaps) { this.complianceGaps = complianceGaps; }
    
    public List<String> getRemediationActions() { return remediationActions; }
    public void setRemediationActions(List<String> remediationActions) { this.remediationActions = remediationActions; }
    
    public String getReportFormat() { return reportFormat; }
    public void setReportFormat(String reportFormat) { this.reportFormat = reportFormat; }
    
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    
    public Boolean getIsAutomatedReport() { return isAutomatedReport; }
    public void setIsAutomatedReport(Boolean isAutomatedReport) { this.isAutomatedReport = isAutomatedReport; }
    
    public Boolean getIsScheduledReport() { return isScheduledReport; }
    public void setIsScheduledReport(Boolean isScheduledReport) { this.isScheduledReport = isScheduledReport; }
    
    public Boolean getIsPartialAssessment() { return isPartialAssessment; }
    public void setIsPartialAssessment(Boolean isPartialAssessment) { this.isPartialAssessment = isPartialAssessment; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getAssessmentNotes() { return assessmentNotes; }
    public void setAssessmentNotes(String assessmentNotes) { this.assessmentNotes = assessmentNotes; }
    
    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }
    
    public String getToolsUsed() { return toolsUsed; }
    public void setToolsUsed(String toolsUsed) { this.toolsUsed = toolsUsed; }
    
    public String getStandardsCompliance() { return standardsCompliance; }
    public void setStandardsCompliance(String standardsCompliance) { this.standardsCompliance = standardsCompliance; }
    
    public LocalDateTime getNextAssessmentDate() { return nextAssessmentDate; }
    public void setNextAssessmentDate(LocalDateTime nextAssessmentDate) { this.nextAssessmentDate = nextAssessmentDate; }
    
    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }
    
    public String getSupersededBy() { return supersededBy; }
    public void setSupersededBy(String supersededBy) { this.supersededBy = supersededBy; }
}