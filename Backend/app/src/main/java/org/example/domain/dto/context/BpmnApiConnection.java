package org.example.domain.dto.context;

import java.util.List;
import java.util.Map;

/**
 * DTO representing a connection between BPMN process and API endpoint
 */
public class BpmnApiConnection {
    private String connectionId;
    private String bpmnElementId;
    private String bpmnElementName;
    private String apiEndpointId;
    private String apiEndpointPath;
    private String httpMethod;
    private Double similarityScore;
    private ConnectionType connectionType;
    private String description;
    private Map<String, Object> properties;
    private List<String> dataFlows;
    private String status;

    public BpmnApiConnection() {}

    public BpmnApiConnection(String connectionId, String bpmnElementId, String apiEndpointId) {
        this.connectionId = connectionId;
        this.bpmnElementId = bpmnElementId;
        this.apiEndpointId = apiEndpointId;
    }

    // Getters and Setters
    public String getConnectionId() { return connectionId; }
    public void setConnectionId(String connectionId) { this.connectionId = connectionId; }

    public String getBpmnElementId() { return bpmnElementId; }
    public void setBpmnElementId(String bpmnElementId) { this.bpmnElementId = bpmnElementId; }

    public String getBpmnElementName() { return bpmnElementName; }
    public void setBpmnElementName(String bpmnElementName) { this.bpmnElementName = bpmnElementName; }

    public String getApiEndpointId() { return apiEndpointId; }
    public void setApiEndpointId(String apiEndpointId) { this.apiEndpointId = apiEndpointId; }

    public String getApiEndpointPath() { return apiEndpointPath; }
    public void setApiEndpointPath(String apiEndpointPath) { this.apiEndpointPath = apiEndpointPath; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }

    public ConnectionType getConnectionType() { return connectionType; }
    public void setConnectionType(ConnectionType connectionType) { this.connectionType = connectionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }

    public List<String> getDataFlows() { return dataFlows; }
    public void setDataFlows(List<String> dataFlows) { this.dataFlows = dataFlows; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public enum ConnectionType {
        DIRECT,
        INFERRED,
        EXPLICIT,
        DEPENDENCY
    }
}