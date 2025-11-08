package org.example.domain.dto.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Результат маппинга между API и BPMN
 */
public class CrossReferenceMappingResult {
    
    private String mappingId;
    private String apiSpecId;
    private String bpmnDiagramId;
    private LocalDateTime mappingStartTime;
    private LocalDateTime mappingEndTime;
    private long processingTimeMs;
    private String status; // SUCCESS, FAILED, PARTIAL
    private String errorMessage;
    
    // Маппинги
    private List<EndpointTaskMapping> endpointTaskMappings;
    private List<DataFlowCorrelation> dataFlowCorrelations;
    private List<BusinessScenarioAlignment> scenarioAlignments;
    private List<EndToEndProcessCoverage> processCoverages;
    
    // Unified context
    private UnifiedIntegrationContext unifiedContext;
    private Map<String, Object> crossSystemRelationships;
    private List<IntegratedBusinessLogic> integratedBusinessLogic;
    private List<ComprehensiveTestScenario> comprehensiveTestScenarios;
    
    // Статистика
    private MappingStatistics statistics;
    private List<UnmappedItem> unmappedApiItems;
    private List<UnmappedItem> unmappedBpmnItems;
    private List<MappingGap> mappingGaps;
    
    public CrossReferenceMappingResult() {
        this.mappingStartTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getMappingId() { return mappingId; }
    public void setMappingId(String mappingId) { this.mappingId = mappingId; }
    
    public String getApiSpecId() { return apiSpecId; }
    public void setApiSpecId(String apiSpecId) { this.apiSpecId = apiSpecId; }
    
    public String getBpmnDiagramId() { return bpmnDiagramId; }
    public void setBpmnDiagramId(String bpmnDiagramId) { this.bpmnDiagramId = bpmnDiagramId; }
    
    public LocalDateTime getMappingStartTime() { return mappingStartTime; }
    public void setMappingStartTime(LocalDateTime mappingStartTime) { this.mappingStartTime = mappingStartTime; }
    
    public LocalDateTime getMappingEndTime() { return mappingEndTime; }
    public void setMappingEndTime(LocalDateTime mappingEndTime) { this.mappingEndTime = mappingEndTime; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public List<EndpointTaskMapping> getEndpointTaskMappings() { return endpointTaskMappings; }
    public void setEndpointTaskMappings(List<EndpointTaskMapping> endpointTaskMappings) { this.endpointTaskMappings = endpointTaskMappings; }
    
    public List<DataFlowCorrelation> getDataFlowCorrelations() { return dataFlowCorrelations; }
    public void setDataFlowCorrelations(List<DataFlowCorrelation> dataFlowCorrelations) { this.dataFlowCorrelations = dataFlowCorrelations; }
    
    public List<BusinessScenarioAlignment> getScenarioAlignments() { return scenarioAlignments; }
    public void setScenarioAlignments(List<BusinessScenarioAlignment> scenarioAlignments) { this.scenarioAlignments = scenarioAlignments; }
    
    public List<EndToEndProcessCoverage> getProcessCoverages() { return processCoverages; }
    public void setProcessCoverages(List<EndToEndProcessCoverage> processCoverages) { this.processCoverages = processCoverages; }
    
    public UnifiedIntegrationContext getUnifiedContext() { return unifiedContext; }
    public void setUnifiedContext(UnifiedIntegrationContext unifiedContext) { this.unifiedContext = unifiedContext; }
    
    public Map<String, Object> getCrossSystemRelationships() { return crossSystemRelationships; }
    public void setCrossSystemRelationships(Map<String, Object> crossSystemRelationships) { this.crossSystemRelationships = crossSystemRelationships; }
    
    public List<IntegratedBusinessLogic> getIntegratedBusinessLogic() { return integratedBusinessLogic; }
    public void setIntegratedBusinessLogic(List<IntegratedBusinessLogic> integratedBusinessLogic) { this.integratedBusinessLogic = integratedBusinessLogic; }
    
    public List<ComprehensiveTestScenario> getComprehensiveTestScenarios() { return comprehensiveTestScenarios; }
    public void setComprehensiveTestScenarios(List<ComprehensiveTestScenario> comprehensiveTestScenarios) { this.comprehensiveTestScenarios = comprehensiveTestScenarios; }
    
    public MappingStatistics getStatistics() { return statistics; }
    public void setStatistics(MappingStatistics statistics) { this.statistics = statistics; }
    
    public List<UnmappedItem> getUnmappedApiItems() { return unmappedApiItems; }
    public void setUnmappedApiItems(List<UnmappedItem> unmappedApiItems) { this.unmappedApiItems = unmappedApiItems; }
    
    public List<UnmappedItem> getUnmappedBpmnItems() { return unmappedBpmnItems; }
    public void setUnmappedBpmnItems(List<UnmappedItem> unmappedBpmnItems) { this.unmappedBpmnItems = unmappedBpmnItems; }
    
    public List<MappingGap> getMappingGaps() { return mappingGaps; }
    public void setMappingGaps(List<MappingGap> mappingGaps) { this.mappingGaps = mappingGaps; }
    
    // Вложенные классы для структуры данных
    
    public static class EndpointTaskMapping {
        private String apiEndpoint;
        private String bpmnTask;
        private String mappingType; // DIRECT, INFERRED, PARTIAL
        private double confidenceScore;
        private String description;
        private List<String> sharedDataFields;
        private Map<String, Object> mappingMetadata;
        
        // Getters and Setters
        public String getApiEndpoint() { return apiEndpoint; }
        public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
        
        public String getBpmnTask() { return bpmnTask; }
        public void setBpmnTask(String bpmnTask) { this.bpmnTask = bpmnTask; }
        
        public String getMappingType() { return mappingType; }
        public void setMappingType(String mappingType) { this.mappingType = mappingType; }
        
        public double getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getSharedDataFields() { return sharedDataFields; }
        public void setSharedDataFields(List<String> sharedDataFields) { this.sharedDataFields = sharedDataFields; }
        
        public Map<String, Object> getMappingMetadata() { return mappingMetadata; }
        public void setMappingMetadata(Map<String, Object> mappingMetadata) { this.mappingMetadata = mappingMetadata; }
    }
    
    public static class DataFlowCorrelation {
        private String apiDataField;
        private String bpmnVariable;
        private String correlationType; // DIRECT, TRANSFORMED, AGGREGATED
        private String description;
        private Map<String, Object> transformationRules;
        private double correlationStrength;
        
        // Getters and Setters
        public String getApiDataField() { return apiDataField; }
        public void setApiDataField(String apiDataField) { this.apiDataField = apiDataField; }
        
        public String getBpmnVariable() { return bpmnVariable; }
        public void setBpmnVariable(String bpmnVariable) { this.bpmnVariable = bpmnVariable; }
        
        public String getCorrelationType() { return correlationType; }
        public void setCorrelationType(String correlationType) { this.correlationType = correlationType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getTransformationRules() { return transformationRules; }
        public void setTransformationRules(Map<String, Object> transformationRules) { this.transformationRules = transformationRules; }
        
        public double getCorrelationStrength() { return correlationStrength; }
        public void setCorrelationStrength(double correlationStrength) { this.correlationStrength = correlationStrength; }
    }
    
    public static class BusinessScenarioAlignment {
        private String scenarioId;
        private String scenarioName;
        private List<String> apiEndpoints;
        private List<String> bpmnTasks;
        private String alignmentType; // COMPLETE, PARTIAL, INFERRED
        private String description;
        private List<String> sharedBusinessRules;
        
        // Getters and Setters
        public String getScenarioId() { return scenarioId; }
        public void setScenarioId(String scenarioId) { this.scenarioId = scenarioId; }
        
        public String getScenarioName() { return scenarioName; }
        public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }
        
        public List<String> getApiEndpoints() { return apiEndpoints; }
        public void setApiEndpoints(List<String> apiEndpoints) { this.apiEndpoints = apiEndpoints; }
        
        public List<String> getBpmnTasks() { return bpmnTasks; }
        public void setBpmnTasks(List<String> bpmnTasks) { this.bpmnTasks = bpmnTasks; }
        
        public String getAlignmentType() { return alignmentType; }
        public void setAlignmentType(String alignmentType) { this.alignmentType = alignmentType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getSharedBusinessRules() { return sharedBusinessRules; }
        public void setSharedBusinessRules(List<String> sharedBusinessRules) { this.sharedBusinessRules = sharedBusinessRules; }
    }
    
    public static class EndToEndProcessCoverage {
        private String processId;
        private String processName;
        private List<String> coveredApiEndpoints;
        private List<String> coveredBpmnTasks;
        private double coveragePercentage;
        private String description;
        private List<String> coverageGaps;
        
        // Getters and Setters
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        
        public String getProcessName() { return processName; }
        public void setProcessName(String processName) { this.processName = processName; }
        
        public List<String> getCoveredApiEndpoints() { return coveredApiEndpoints; }
        public void setCoveredApiEndpoints(List<String> coveredApiEndpoints) { this.coveredApiEndpoints = coveredApiEndpoints; }
        
        public List<String> getCoveredBpmnTasks() { return coveredBpmnTasks; }
        public void setCoveredBpmnTasks(List<String> coveredBpmnTasks) { this.coveredBpmnTasks = coveredBpmnTasks; }
        
        public double getCoveragePercentage() { return coveragePercentage; }
        public void setCoveragePercentage(double coveragePercentage) { this.coveragePercentage = coveragePercentage; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getCoverageGaps() { return coverageGaps; }
        public void setCoverageGaps(List<String> coverageGaps) { this.coverageGaps = coverageGaps; }
    }
    
    public static class UnifiedIntegrationContext {
        private String contextId;
        private String name;
        private Map<String, Object> apiContext;
        private Map<String, Object> bpmnContext;
        private Map<String, Object> crossSystemData;
        private List<String> sharedBusinessConcepts;
        private String description;
        
        // Getters and Setters
        public String getContextId() { return contextId; }
        public void setContextId(String contextId) { this.contextId = contextId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Map<String, Object> getApiContext() { return apiContext; }
        public void setApiContext(Map<String, Object> apiContext) { this.apiContext = apiContext; }
        
        public Map<String, Object> getBpmnContext() { return bpmnContext; }
        public void setBpmnContext(Map<String, Object> bpmnContext) { this.bpmnContext = bpmnContext; }
        
        public Map<String, Object> getCrossSystemData() { return crossSystemData; }
        public void setCrossSystemData(Map<String, Object> crossSystemData) { this.crossSystemData = crossSystemData; }
        
        public List<String> getSharedBusinessConcepts() { return sharedBusinessConcepts; }
        public void setSharedBusinessConcepts(List<String> sharedBusinessConcepts) { this.sharedBusinessConcepts = sharedBusinessConcepts; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class IntegratedBusinessLogic {
        private String logicId;
        private String name;
        private String type; // RULE, WORKFLOW, VALIDATION
        private List<String> apiComponents;
        private List<String> bpmnComponents;
        private String description;
        private Map<String, Object> logicDefinition;
        
        // Getters and Setters
        public String getLogicId() { return logicId; }
        public void setLogicId(String logicId) { this.logicId = logicId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public List<String> getApiComponents() { return apiComponents; }
        public void setApiComponents(List<String> apiComponents) { this.apiComponents = apiComponents; }
        
        public List<String> getBpmnComponents() { return bpmnComponents; }
        public void setBpmnComponents(List<String> bpmnComponents) { this.bpmnComponents = bpmnComponents; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getLogicDefinition() { return logicDefinition; }
        public void setLogicDefinition(Map<String, Object> logicDefinition) { this.logicDefinition = logicDefinition; }
    }
    
    public static class ComprehensiveTestScenario {
        private String scenarioId;
        private String name;
        private String type; // END_TO_END, INTEGRATION, UNIT
        private List<String> apiSteps;
        private List<String> bpmnSteps;
        private String description;
        private List<String> testData;
        private Map<String, Object> expectedResults;
        
        // Getters and Setters
        public String getScenarioId() { return scenarioId; }
        public void setScenarioId(String scenarioId) { this.scenarioId = scenarioId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public List<String> getApiSteps() { return apiSteps; }
        public void setApiSteps(List<String> apiSteps) { this.apiSteps = apiSteps; }
        
        public List<String> getBpmnSteps() { return bpmnSteps; }
        public void setBpmnSteps(List<String> bpmnSteps) { this.bpmnSteps = bpmnSteps; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getTestData() { return testData; }
        public void setTestData(List<String> testData) { this.testData = testData; }
        
        public Map<String, Object> getExpectedResults() { return expectedResults; }
        public void setExpectedResults(Map<String, Object> expectedResults) { this.expectedResults = expectedResults; }
    }
    
    public static class MappingStatistics {
        private int totalApiEndpoints;
        private int totalBpmnTasks;
        private int mappedEndpoints;
        private int mappedTasks;
        private int unmappedEndpoints;
        private int unmappedTasks;
        private double mappingAccuracy;
        private double coveragePercentage;
        private int totalScenarios;
        private int alignedScenarios;
        
        // Getters and Setters
        public int getTotalApiEndpoints() { return totalApiEndpoints; }
        public void setTotalApiEndpoints(int totalApiEndpoints) { this.totalApiEndpoints = totalApiEndpoints; }
        
        public int getTotalBpmnTasks() { return totalBpmnTasks; }
        public void setTotalBpmnTasks(int totalBpmnTasks) { this.totalBpmnTasks = totalBpmnTasks; }
        
        public int getMappedEndpoints() { return mappedEndpoints; }
        public void setMappedEndpoints(int mappedEndpoints) { this.mappedEndpoints = mappedEndpoints; }
        
        public int getMappedTasks() { return mappedTasks; }
        public void setMappedTasks(int mappedTasks) { this.mappedTasks = mappedTasks; }
        
        public int getUnmappedEndpoints() { return unmappedEndpoints; }
        public void setUnmappedEndpoints(int unmappedEndpoints) { this.unmappedEndpoints = unmappedEndpoints; }
        
        public int getUnmappedTasks() { return unmappedTasks; }
        public void setUnmappedTasks(int unmappedTasks) { this.unmappedTasks = unmappedTasks; }
        
        public double getMappingAccuracy() { return mappingAccuracy; }
        public void setMappingAccuracy(double mappingAccuracy) { this.mappingAccuracy = mappingAccuracy; }
        
        public double getCoveragePercentage() { return coveragePercentage; }
        public void setCoveragePercentage(double coveragePercentage) { this.coveragePercentage = coveragePercentage; }
        
        public int getTotalScenarios() { return totalScenarios; }
        public void setTotalScenarios(int totalScenarios) { this.totalScenarios = totalScenarios; }
        
        public int getAlignedScenarios() { return alignedScenarios; }
        public void setAlignedScenarios(int alignedScenarios) { this.alignedScenarios = alignedScenarios; }
    }
    
    public static class UnmappedItem {
        private String itemId;
        private String name;
        private String type; // API_ENDPOINT, BPMN_TASK, etc.
        private String reason; // NO_MATCH, AMBIGUOUS, IGNORED
        private String description;
        private Map<String, Object> metadata;
        
        // Getters and Setters
        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class MappingGap {
        private String gapId;
        private String name;
        private String gapType; // MISSING_ENDPOINT, MISSING_TASK, DATA_MISMATCH
        private String description;
        private List<String> affectedComponents;
        private String recommendation;
        private String severity; // HIGH, MEDIUM, LOW
        
        // Getters and Setters
        public String getGapId() { return gapId; }
        public void setGapId(String gapId) { this.gapId = gapId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getGapType() { return gapType; }
        public void setGapType(String gapType) { this.gapType = gapType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getAffectedComponents() { return affectedComponents; }
        public void setAffectedComponents(List<String> affectedComponents) { this.affectedComponents = affectedComponents; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }
}