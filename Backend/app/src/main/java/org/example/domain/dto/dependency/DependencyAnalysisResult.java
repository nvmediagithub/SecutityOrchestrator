package org.example.domain.dto.dependency;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Objects;
import java.time.LocalDateTime;

/**
 * Comprehensive result of dependency analysis across API, BPMN, and business rules
 */
public class DependencyAnalysisResult {
    private String analysisId;
    private String analysisName;
    private String description;
    private LocalDateTime analysisTimestamp;
    private AnalysisStatus status;
    private String systemBoundary;
    private String analysisScope;
    private DataFlowGraph dataFlowGraph;
    private List<ApiDependency> apiDependencies;
    private List<BpmnDependency> bpmnDependencies;
    private List<BusinessRuleDependency> businessRuleDependencies;
    private Set<String> identifiedRisks;
    private Set<String> optimizationOpportunities;
    private Set<String> recommendations;
    private Map<String, String> dependencyMatrix;
    private Set<String> criticalPaths;
    private Set<String> bottlenecks;
    private Set<String> dataInconsistencyRisks;
    private Set<String> performanceImpacts;
    private Set<String> securityConcerns;
    private Map<String, String> testDataRequirements;
    private Set<String> masterDataDependencies;
    private Set<String> referenceDataDependencies;
    private Set<String> workflowDependencies;
    private String llmAnalysisInsights;
    private Set<String> hiddenDependencies;
    private Map<String, String> businessImpactAssessment;
    private Set<String> complianceRisks;
    private String integrationPoints;
    private Set<String> dataGovernanceRules;
    private String overallRiskScore;
    private Set<String> mitigationStrategies;
    private String nextAnalysisRecommendations;
    private Map<String, String> qualityMetrics;
    
    public enum AnalysisStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        PARTIAL
    }
    
    public DependencyAnalysisResult() {
        this.analysisTimestamp = LocalDateTime.now();
        this.apiDependencies = new ArrayList<>();
        this.bpmnDependencies = new ArrayList<>();
        this.businessRuleDependencies = new ArrayList<>();
        this.identifiedRisks = new HashSet<>();
        this.optimizationOpportunities = new HashSet<>();
        this.recommendations = new HashSet<>();
        this.dependencyMatrix = new HashMap<>();
        this.criticalPaths = new HashSet<>();
        this.bottlenecks = new HashSet<>();
        this.dataInconsistencyRisks = new HashSet<>();
        this.performanceImpacts = new HashSet<>();
        this.securityConcerns = new HashSet<>();
        this.testDataRequirements = new HashMap<>();
        this.masterDataDependencies = new HashSet<>();
        this.referenceDataDependencies = new HashSet<>();
        this.workflowDependencies = new HashSet<>();
        this.hiddenDependencies = new HashSet<>();
        this.businessImpactAssessment = new HashMap<>();
        this.complianceRisks = new HashSet<>();
        this.dataGovernanceRules = new HashSet<>();
        this.mitigationStrategies = new HashSet<>();
        this.qualityMetrics = new HashMap<>();
    }
    
    public DependencyAnalysisResult(String analysisId, String analysisName) {
        this();
        this.analysisId = analysisId;
        this.analysisName = analysisName;
    }
    
    // Getters and Setters
    public String getAnalysisId() {
        return analysisId;
    }
    
    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }
    
    public String getAnalysisName() {
        return analysisName;
    }
    
    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getAnalysisTimestamp() {
        return analysisTimestamp;
    }
    
    public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) {
        this.analysisTimestamp = analysisTimestamp;
    }
    
    public AnalysisStatus getStatus() {
        return status;
    }
    
    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }
    
    public String getSystemBoundary() {
        return systemBoundary;
    }
    
    public void setSystemBoundary(String systemBoundary) {
        this.systemBoundary = systemBoundary;
    }
    
    public String getAnalysisScope() {
        return analysisScope;
    }
    
    public void setAnalysisScope(String analysisScope) {
        this.analysisScope = analysisScope;
    }
    
    public DataFlowGraph getDataFlowGraph() {
        return dataFlowGraph;
    }
    
    public void setDataFlowGraph(DataFlowGraph dataFlowGraph) {
        this.dataFlowGraph = dataFlowGraph;
    }
    
    public List<ApiDependency> getApiDependencies() {
        return apiDependencies;
    }
    
    public void setApiDependencies(List<ApiDependency> apiDependencies) {
        this.apiDependencies = apiDependencies;
    }
    
    public List<BpmnDependency> getBpmnDependencies() {
        return bpmnDependencies;
    }
    
    public void setBpmnDependencies(List<BpmnDependency> bpmnDependencies) {
        this.bpmnDependencies = bpmnDependencies;
    }
    
    public List<BusinessRuleDependency> getBusinessRuleDependencies() {
        return businessRuleDependencies;
    }
    
    public void setBusinessRuleDependencies(List<BusinessRuleDependency> businessRuleDependencies) {
        this.businessRuleDependencies = businessRuleDependencies;
    }
    
    public Set<String> getIdentifiedRisks() {
        return identifiedRisks;
    }
    
    public void setIdentifiedRisks(Set<String> identifiedRisks) {
        this.identifiedRisks = identifiedRisks;
    }
    
    public Set<String> getOptimizationOpportunities() {
        return optimizationOpportunities;
    }
    
    public void setOptimizationOpportunities(Set<String> optimizationOpportunities) {
        this.optimizationOpportunities = optimizationOpportunities;
    }
    
    public Set<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(Set<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public Map<String, String> getDependencyMatrix() {
        return dependencyMatrix;
    }
    
    public void setDependencyMatrix(Map<String, String> dependencyMatrix) {
        this.dependencyMatrix = dependencyMatrix;
    }
    
    public Set<String> getCriticalPaths() {
        return criticalPaths;
    }
    
    public void setCriticalPaths(Set<String> criticalPaths) {
        this.criticalPaths = criticalPaths;
    }
    
    public Set<String> getBottlenecks() {
        return bottlenecks;
    }
    
    public void setBottlenecks(Set<String> bottlenecks) {
        this.bottlenecks = bottlenecks;
    }
    
    public Set<String> getDataInconsistencyRisks() {
        return dataInconsistencyRisks;
    }
    
    public void setDataInconsistencyRisks(Set<String> dataInconsistencyRisks) {
        this.dataInconsistencyRisks = dataInconsistencyRisks;
    }
    
    public Set<String> getPerformanceImpacts() {
        return performanceImpacts;
    }
    
    public void setPerformanceImpacts(Set<String> performanceImpacts) {
        this.performanceImpacts = performanceImpacts;
    }
    
    public Set<String> getSecurityConcerns() {
        return securityConcerns;
    }
    
    public void setSecurityConcerns(Set<String> securityConcerns) {
        this.securityConcerns = securityConcerns;
    }
    
    public Map<String, String> getTestDataRequirements() {
        return testDataRequirements;
    }
    
    public void setTestDataRequirements(Map<String, String> testDataRequirements) {
        this.testDataRequirements = testDataRequirements;
    }
    
    public Set<String> getMasterDataDependencies() {
        return masterDataDependencies;
    }
    
    public void setMasterDataDependencies(Set<String> masterDataDependencies) {
        this.masterDataDependencies = masterDataDependencies;
    }
    
    public Set<String> getReferenceDataDependencies() {
        return referenceDataDependencies;
    }
    
    public void setReferenceDataDependencies(Set<String> referenceDataDependencies) {
        this.referenceDataDependencies = referenceDataDependencies;
    }
    
    public Set<String> getWorkflowDependencies() {
        return workflowDependencies;
    }
    
    public void setWorkflowDependencies(Set<String> workflowDependencies) {
        this.workflowDependencies = workflowDependencies;
    }
    
    public String getLlmAnalysisInsights() {
        return llmAnalysisInsights;
    }
    
    public void setLlmAnalysisInsights(String llmAnalysisInsights) {
        this.llmAnalysisInsights = llmAnalysisInsights;
    }
    
    public Set<String> getHiddenDependencies() {
        return hiddenDependencies;
    }
    
    public void setHiddenDependencies(Set<String> hiddenDependencies) {
        this.hiddenDependencies = hiddenDependencies;
    }
    
    public Map<String, String> getBusinessImpactAssessment() {
        return businessImpactAssessment;
    }
    
    public void setBusinessImpactAssessment(Map<String, String> businessImpactAssessment) {
        this.businessImpactAssessment = businessImpactAssessment;
    }
    
    public Set<String> getComplianceRisks() {
        return complianceRisks;
    }
    
    public void setComplianceRisks(Set<String> complianceRisks) {
        this.complianceRisks = complianceRisks;
    }
    
    public String getIntegrationPoints() {
        return integrationPoints;
    }
    
    public void setIntegrationPoints(String integrationPoints) {
        this.integrationPoints = integrationPoints;
    }
    
    public Set<String> getDataGovernanceRules() {
        return dataGovernanceRules;
    }
    
    public void setDataGovernanceRules(Set<String> dataGovernanceRules) {
        this.dataGovernanceRules = dataGovernanceRules;
    }
    
    public String getOverallRiskScore() {
        return overallRiskScore;
    }
    
    public void setOverallRiskScore(String overallRiskScore) {
        this.overallRiskScore = overallRiskScore;
    }
    
    public Set<String> getMitigationStrategies() {
        return mitigationStrategies;
    }
    
    public void setMitigationStrategies(Set<String> mitigationStrategies) {
        this.mitigationStrategies = mitigationStrategies;
    }
    
    public String getNextAnalysisRecommendations() {
        return nextAnalysisRecommendations;
    }
    
    public void setNextAnalysisRecommendations(String nextAnalysisRecommendations) {
        this.nextAnalysisRecommendations = nextAnalysisRecommendations;
    }
    
    public Map<String, String> getQualityMetrics() {
        return qualityMetrics;
    }
    
    public void setQualityMetrics(Map<String, String> qualityMetrics) {
        this.qualityMetrics = qualityMetrics;
    }
    
    // Utility methods
    public void addApiDependency(ApiDependency dependency) {
        this.apiDependencies.add(dependency);
    }
    
    public void addBpmnDependency(BpmnDependency dependency) {
        this.bpmnDependencies.add(dependency);
    }
    
    public void addBusinessRuleDependency(BusinessRuleDependency dependency) {
        this.businessRuleDependencies.add(dependency);
    }
    
    public void addRisk(String risk) {
        this.identifiedRisks.add(risk);
    }
    
    public void addOptimizationOpportunity(String opportunity) {
        this.optimizationOpportunities.add(opportunity);
    }
    
    public void addRecommendation(String recommendation) {
        this.recommendations.add(recommendation);
    }
    
    public void addCriticalPath(String path) {
        this.criticalPaths.add(path);
    }
    
    public void addBottleneck(String bottleneck) {
        this.bottlenecks.add(bottleneck);
    }
    
    public int getTotalDependencies() {
        return apiDependencies.size() + bpmnDependencies.size() + businessRuleDependencies.size();
    }
    
    public int getTotalRisks() {
        return identifiedRisks.size();
    }
    
    public int getTotalRecommendations() {
        return recommendations.size();
    }
    
    public boolean isHighRisk() {
        return identifiedRisks.size() > 10 || 
               dataInconsistencyRisks.size() > 5 || 
               performanceImpacts.size() > 3;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyAnalysisResult that = (DependencyAnalysisResult) o;
        return Objects.equals(analysisId, that.analysisId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(analysisId);
    }
    
    @Override
    public String toString() {
        return "DependencyAnalysisResult{" +
                "analysisId='" + analysisId + '\'' +
                ", analysisName='" + analysisName + '\'' +
                ", status=" + status +
                ", totalDependencies=" + getTotalDependencies() +
                ", totalRisks=" + getTotalRisks() +
                ", analysisTimestamp=" + analysisTimestamp +
                '}';
    }
}