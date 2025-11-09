package org.example.infrastructure.services.bpmn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Данные, извлеченные из BPMN диаграммы
 */
public class BpmnParsedData {
    
    private String processId;
    private String processName;
    private String processDescription;
    private String version;
    private Map<String, String> processAttributes;
    
    private List<BpmnElement> elements;
    private List<BpmnFlow> flows;
    
    public BpmnParsedData() {
        this.elements = new ArrayList<>();
        this.flows = new ArrayList<>();
        this.processAttributes = new HashMap<>();
    }
    
    // Getters and Setters
    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
    
    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }
    
    public String getProcessDescription() { return processDescription; }
    public void setProcessDescription(String processDescription) { this.processDescription = processDescription; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public Map<String, String> getProcessAttributes() { return processAttributes; }
    public void setProcessAttributes(Map<String, String> processAttributes) { this.processAttributes = processAttributes; }
    
    public List<BpmnElement> getElements() { return elements; }
    public void setElements(List<BpmnElement> elements) { this.elements = elements; }
    
    public List<BpmnFlow> getFlows() { return flows; }
    public void setFlows(List<BpmnFlow> flows) { this.flows = flows; }
    
    // Business methods
    public void addElement(BpmnElement element) {
        this.elements.add(element);
    }
    
    public void addFlow(BpmnFlow flow) {
        this.flows.add(flow);
    }
    
    public BpmnElement findElementById(String id) {
        return elements.stream()
            .filter(element -> id.equals(element.getId()))
            .findFirst()
            .orElse(null);
    }
    
    public BpmnFlow findFlowById(String id) {
        return flows.stream()
            .filter(flow -> id.equals(flow.getId()))
            .findFirst()
            .orElse(null);
    }
}