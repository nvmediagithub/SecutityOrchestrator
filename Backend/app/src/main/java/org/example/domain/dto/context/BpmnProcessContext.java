package org.example.domain.dto.context;

import org.example.infrastructure.services.bpmn.context.GatewayContext;
import org.example.infrastructure.services.bpmn.context.EventContext;
import java.util.List;
import java.util.Map;

/**
 * Контекст отдельного BPMN процесса
 */
public class BpmnProcessContext {
    
    private String diagramId;
    private String processName;
    private List<ProcessVariable> processVariables;
    private List<TaskDependency> taskDependencies;
    private Map<String, Object> workflowLogic;
    private List<GatewayContext> gateways;
    private List<EventContext> events;
    
    public BpmnProcessContext() {
        this.diagramId = "proc_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }
    
    public List<ProcessVariable> getProcessVariables() { return processVariables; }
    public void setProcessVariables(List<ProcessVariable> processVariables) { this.processVariables = processVariables; }
    
    public List<TaskDependency> getTaskDependencies() { return taskDependencies; }
    public void setTaskDependencies(List<TaskDependency> taskDependencies) { this.taskDependencies = taskDependencies; }
    
    public Map<String, Object> getWorkflowLogic() { return workflowLogic; }
    public void setWorkflowLogic(Map<String, Object> workflowLogic) { this.workflowLogic = workflowLogic; }
    
    public List<GatewayContext> getGateways() { return gateways; }
    public void setGateways(List<GatewayContext> gateways) { this.gateways = gateways; }
    
    public List<EventContext> getEvents() { return events; }
    public void setEvents(List<EventContext> events) { this.events = events; }
}