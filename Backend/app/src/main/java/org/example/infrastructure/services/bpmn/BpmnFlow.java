package org.example.infrastructure.services.bpmn;

/**
 * Представляет поток в BPMN диаграмме (Sequence Flow, Message Flow, etc.)
 */
public class BpmnFlow {
    
    private String id;
    private String name;
    private String flowType; // sequenceFlow, messageFlow, association
    private String sourceElement;
    private String targetElement;
    private String description;
    private String conditionExpression;
    
    public BpmnFlow() {}
    
    public BpmnFlow(String id, String sourceElement, String targetElement) {
        this.id = id;
        this.sourceElement = sourceElement;
        this.targetElement = targetElement;
        this.flowType = "sequenceFlow";
    }
    
    public BpmnFlow(String id, String name, String flowType, String sourceElement, String targetElement) {
        this.id = id;
        this.name = name;
        this.flowType = flowType;
        this.sourceElement = sourceElement;
        this.targetElement = targetElement;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getFlowType() { return flowType; }
    public void setFlowType(String flowType) { this.flowType = flowType; }
    
    public String getSourceElement() { return sourceElement; }
    public void setSourceElement(String sourceElement) { this.sourceElement = sourceElement; }
    
    public String getTargetElement() { return targetElement; }
    public void setTargetElement(String targetElement) { this.targetElement = targetElement; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }
    
    // Business methods
    public boolean isSequenceFlow() {
        return "sequenceFlow".equals(flowType);
    }
    
    public boolean isMessageFlow() {
        return "messageFlow".equals(flowType);
    }
    
    public boolean isConditional() {
        return conditionExpression != null && !conditionExpression.trim().isEmpty();
    }
    
    public boolean isDefaultFlow() {
        return hasAttribute("isDefault");
    }
    
    public boolean hasAttribute(String key) {
        // Здесь можно добавить логику для проверки атрибутов из XML
        return false;
    }
    
    public String getAttribute(String key) {
        // Здесь можно добавить логику для получения атрибутов из XML
        return null;
    }
    
    public boolean connectsTo(String elementId) {
        return elementId.equals(sourceElement) || elementId.equals(targetElement);
    }
    
    public boolean startsFrom(String elementId) {
        return elementId.equals(sourceElement);
    }
    
    public boolean endsAt(String elementId) {
        return elementId.equals(targetElement);
    }
    
    @Override
    public String toString() {
        return "BpmnFlow{" +
                "id='" + id + '\'' +
                ", flowType='" + flowType + '\'' +
                ", sourceElement='" + sourceElement + '\'' +
                ", targetElement='" + targetElement + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BpmnFlow bpmnFlow = (BpmnFlow) o;
        return id != null ? id.equals(bpmnFlow.id) : bpmnFlow.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}