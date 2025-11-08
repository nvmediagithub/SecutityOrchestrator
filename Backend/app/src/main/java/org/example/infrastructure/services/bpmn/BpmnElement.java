package org.example.infrastructure.services.bpmn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Представляет элемент BPMN диаграммы
 */
public class BpmnElement {
    
    private String id;
    private String name;
    private String type;
    private String description;
    private Map<String, String> attributes;
    
    // Связи с потоками
    private List<String> incomingFlows;
    private List<String> outgoingFlows;
    
    public BpmnElement() {
        this.incomingFlows = new ArrayList<>();
        this.outgoingFlows = new ArrayList<>();
    }
    
    public BpmnElement(String id, String name, String type) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }
    
    public List<String> getIncomingFlows() { return incomingFlows; }
    public void setIncomingFlows(List<String> incomingFlows) { this.incomingFlows = incomingFlows; }
    
    public List<String> getOutgoingFlows() { return outgoingFlows; }
    public void setOutgoingFlows(List<String> outgoingFlows) { this.outgoingFlows = outgoingFlows; }
    
    // Business methods
    public boolean isStartEvent() {
        return "StartEvent".equals(type);
    }
    
    public boolean isEndEvent() {
        return "EndEvent".equals(type);
    }
    
    public boolean isTask() {
        return type != null && type.endsWith("Task");
    }
    
    public boolean isGateway() {
        return type != null && type.endsWith("Gateway");
    }
    
    public boolean isSubProcess() {
        return "SubProcess".equals(type) || "CallActivity".equals(type);
    }
    
    public int getIncomingFlowCount() {
        return incomingFlows != null ? incomingFlows.size() : 0;
    }
    
    public int getOutgoingFlowCount() {
        return outgoingFlows != null ? outgoingFlows.size() : 0;
    }
    
    public boolean hasIncomingFlows() {
        return getIncomingFlowCount() > 0;
    }
    
    public boolean hasOutgoingFlows() {
        return getOutgoingFlowCount() > 0;
    }
    
    public String getAttributeValue(String key) {
        return attributes != null ? attributes.get(key) : null;
    }
    
    public boolean hasAttribute(String key) {
        return attributes != null && attributes.containsKey(key);
    }
    
    @Override
    public String toString() {
        return "BpmnElement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BpmnElement that = (BpmnElement) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}