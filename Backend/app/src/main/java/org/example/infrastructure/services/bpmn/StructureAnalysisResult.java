package org.example.infrastructure.services.bpmn;

import java.util.ArrayList;
import java.util.List;

/**
 * Результат анализа структуры BPMN процесса
 */
public class StructureAnalysisResult {
    
    private int elementCount;
    private int flowCount;
    private List<String> structuralIssues;
    private int complexityScore;
    private boolean hasStartEvent;
    private boolean hasEndEvent;
    private int orphanElementsCount;
    private List<String> unconnectedElements;
    
    public StructureAnalysisResult() {
        this.structuralIssues = new ArrayList<>();
        this.unconnectedElements = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getElementCount() { return elementCount; }
    public void setElementCount(int elementCount) { this.elementCount = elementCount; }
    
    public int getFlowCount() { return flowCount; }
    public void setFlowCount(int flowCount) { this.flowCount = flowCount; }
    
    public List<String> getStructuralIssues() { return structuralIssues; }
    public void setStructuralIssues(List<String> structuralIssues) { this.structuralIssues = structuralIssues; }
    
    public int getComplexityScore() { return complexityScore; }
    public void setComplexityScore(int complexityScore) { this.complexityScore = complexityScore; }
    
    public boolean isHasStartEvent() { return hasStartEvent; }
    public void setHasStartEvent(boolean hasStartEvent) { this.hasStartEvent = hasStartEvent; }
    
    public boolean isHasEndEvent() { return hasEndEvent; }
    public void setHasEndEvent(boolean hasEndEvent) { this.hasEndEvent = hasEndEvent; }
    
    public int getOrphanElementsCount() { return orphanElementsCount; }
    public void setOrphanElementsCount(int orphanElementsCount) { this.orphanElementsCount = orphanElementsCount; }
    
    public List<String> getUnconnectedElements() { return unconnectedElements; }
    public void setUnconnectedElements(List<String> unconnectedElements) { this.unconnectedElements = unconnectedElements; }
    
    // Business methods
    public void addStructuralIssue(String issue) {
        this.structuralIssues.add(issue);
    }
    
    public void addUnconnectedElement(String elementId) {
        this.unconnectedElements.add(elementId);
    }
    
    public boolean isValidStructure() {
        return hasStartEvent && hasEndEvent && orphanElementsCount == 0;
    }
}