package org.example.domain.dto.dependency;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a data flow graph connecting API, BPMN, and business rule dependencies
 */
public class DataFlowGraph {
    private String graphId;
    private String graphName;
    private String description;
    private List<ApiDependency> apiDependencies;
    private List<BpmnDependency> bpmnDependencies;
    private List<BusinessRuleDependency> businessRuleDependencies;
    private Map<String, Set<String>> nodeConnections;
    private Map<String, DataFlowNode> nodes;
    private Set<String> masterDataSources;
    private Set<String> referenceDataSources;
    private String flowType;
    private String systemBoundary;
    private List<String> endToEndFlows;
    private String optimizationRecommendations;
    private String riskAssessment;
    private Map<String, String> crossSystemDependencies;
    private Set<String> dataIntegrityConstraints;
    private List<String> workflowSequences;
    private String dataLifecycle;
    
    public enum FlowType {
        REAL_TIME,
        BATCH,
        EVENT_DRIVEN,
        REQUEST_RESPONSE,
        PUBLISH_SUBSCRIBE,
        STREAMING,
        HYBRID
    }
    
    public static class DataFlowNode {
        private String nodeId;
        private String nodeName;
        private String nodeType;
        private String nodeDescription;
        private Set<String> inputData;
        private Set<String> outputData;
        private String processingLogic;
        private String dataFormat;
        private String protocol;
        private Set<String> dependencies;
        private Set<String> dependents;
        private String performanceCharacteristics;
        private String scalability;
        private String reliability;
        
        public DataFlowNode() {}
        
        public DataFlowNode(String nodeId, String nodeName, String nodeType) {
            this.nodeId = nodeId;
            this.nodeName = nodeName;
            this.nodeType = nodeType;
        }
        
        // Getters and Setters
        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }
        
        public String getNodeName() { return nodeName; }
        public void setNodeName(String nodeName) { this.nodeName = nodeName; }
        
        public String getNodeType() { return nodeType; }
        public void setNodeType(String nodeType) { this.nodeType = nodeType; }
        
        public String getNodeDescription() { return nodeDescription; }
        public void setNodeDescription(String nodeDescription) { this.nodeDescription = nodeDescription; }
        
        public Set<String> getInputData() { return inputData; }
        public void setInputData(Set<String> inputData) { this.inputData = inputData; }
        
        public Set<String> getOutputData() { return outputData; }
        public void setOutputData(Set<String> outputData) { this.outputData = outputData; }
        
        public String getProcessingLogic() { return processingLogic; }
        public void setProcessingLogic(String processingLogic) { this.processingLogic = processingLogic; }
        
        public String getDataFormat() { return dataFormat; }
        public void setDataFormat(String dataFormat) { this.dataFormat = dataFormat; }
        
        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        
        public Set<String> getDependencies() { return dependencies; }
        public void setDependencies(Set<String> dependencies) { this.dependencies = dependencies; }
        
        public Set<String> getDependents() { return dependents; }
        public void setDependents(Set<String> dependents) { this.dependents = dependents; }
        
        public String getPerformanceCharacteristics() { return performanceCharacteristics; }
        public void setPerformanceCharacteristics(String performanceCharacteristics) { this.performanceCharacteristics = performanceCharacteristics; }
        
        public String getScalability() { return scalability; }
        public void setScalability(String scalability) { this.scalability = scalability; }
        
        public String getReliability() { return reliability; }
        public void setReliability(String reliability) { this.reliability = reliability; }
    }
    
    public DataFlowGraph() {
        this.apiDependencies = new ArrayList<>();
        this.bpmnDependencies = new ArrayList<>();
        this.businessRuleDependencies = new ArrayList<>();
        this.nodeConnections = new HashMap<>();
        this.nodes = new HashMap<>();
        this.masterDataSources = new HashSet<>();
        this.referenceDataSources = new HashSet<>();
        this.endToEndFlows = new ArrayList<>();
        this.crossSystemDependencies = new HashMap<>();
        this.dataIntegrityConstraints = new HashSet<>();
        this.workflowSequences = new ArrayList<>();
    }
    
    public DataFlowGraph(String graphId, String graphName) {
        this();
        this.graphId = graphId;
        this.graphName = graphName;
    }
    
    // Getters and Setters
    public String getGraphId() {
        return graphId;
    }
    
    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }
    
    public String getGraphName() {
        return graphName;
    }
    
    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public Map<String, Set<String>> getNodeConnections() {
        return nodeConnections;
    }
    
    public void setNodeConnections(Map<String, Set<String>> nodeConnections) {
        this.nodeConnections = nodeConnections;
    }
    
    public Map<String, DataFlowNode> getNodes() {
        return nodes;
    }
    
    public void setNodes(Map<String, DataFlowNode> nodes) {
        this.nodes = nodes;
    }
    
    public Set<String> getMasterDataSources() {
        return masterDataSources;
    }
    
    public void setMasterDataSources(Set<String> masterDataSources) {
        this.masterDataSources = masterDataSources;
    }
    
    public Set<String> getReferenceDataSources() {
        return referenceDataSources;
    }
    
    public void setReferenceDataSources(Set<String> referenceDataSources) {
        this.referenceDataSources = referenceDataSources;
    }
    
    public String getFlowType() {
        return flowType;
    }
    
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }
    
    public String getSystemBoundary() {
        return systemBoundary;
    }
    
    public void setSystemBoundary(String systemBoundary) {
        this.systemBoundary = systemBoundary;
    }
    
    public List<String> getEndToEndFlows() {
        return endToEndFlows;
    }
    
    public void setEndToEndFlows(List<String> endToEndFlows) {
        this.endToEndFlows = endToEndFlows;
    }
    
    public String getOptimizationRecommendations() {
        return optimizationRecommendations;
    }
    
    public void setOptimizationRecommendations(String optimizationRecommendations) {
        this.optimizationRecommendations = optimizationRecommendations;
    }
    
    public String getRiskAssessment() {
        return riskAssessment;
    }
    
    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }
    
    public Map<String, String> getCrossSystemDependencies() {
        return crossSystemDependencies;
    }
    
    public void setCrossSystemDependencies(Map<String, String> crossSystemDependencies) {
        this.crossSystemDependencies = crossSystemDependencies;
    }
    
    public Set<String> getDataIntegrityConstraints() {
        return dataIntegrityConstraints;
    }
    
    public void setDataIntegrityConstraints(Set<String> dataIntegrityConstraints) {
        this.dataIntegrityConstraints = dataIntegrityConstraints;
    }
    
    public List<String> getWorkflowSequences() {
        return workflowSequences;
    }
    
    public void setWorkflowSequences(List<String> workflowSequences) {
        this.workflowSequences = workflowSequences;
    }
    
    public String getDataLifecycle() {
        return dataLifecycle;
    }
    
    public void setDataLifecycle(String dataLifecycle) {
        this.dataLifecycle = dataLifecycle;
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
    
    public void addNode(DataFlowNode node) {
        this.nodes.put(node.getNodeId(), node);
    }
    
    public void addConnection(String fromNode, String toNode) {
        this.nodeConnections.computeIfAbsent(fromNode, k -> new HashSet<>()).add(toNode);
    }
    
    public void addMasterDataSource(String source) {
        this.masterDataSources.add(source);
    }
    
    public void addReferenceDataSource(String source) {
        this.referenceDataSources.add(source);
    }
    
    public Set<String> getConnectedNodes(String nodeId) {
        return nodeConnections.getOrDefault(nodeId, new HashSet<>());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFlowGraph that = (DataFlowGraph) o;
        return Objects.equals(graphId, that.graphId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(graphId);
    }
    
    @Override
    public String toString() {
        return "DataFlowGraph{" +
                "graphId='" + graphId + '\'' +
                ", graphName='" + graphName + '\'' +
                ", flowType='" + flowType + '\'' +
                ", nodesCount=" + nodes.size() +
                ", apiDependencies=" + apiDependencies.size() +
                ", bpmnDependencies=" + bpmnDependencies.size() +
                ", businessRuleDependencies=" + businessRuleDependencies.size() +
                '}';
    }
}