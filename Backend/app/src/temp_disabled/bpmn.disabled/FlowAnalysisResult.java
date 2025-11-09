package org.example.infrastructure.services.bpmn;

public class FlowAnalysisResult {
    private int totalFlows = 0;
    private int validFlows = 0;
    private int invalidFlows = 0;
    
    public FlowAnalysisResult() {}
    public int getTotalFlows() { return totalFlows; }
    public void setTotalFlows(int totalFlows) { this.totalFlows = totalFlows; }
    public int getValidFlows() { return validFlows; }
    public void setValidFlows(int validFlows) { this.validFlows = validFlows; }
    public int getInvalidFlows() { return invalidFlows; }
    public void setInvalidFlows(int invalidFlows) { this.invalidFlows = invalidFlows; }
}
