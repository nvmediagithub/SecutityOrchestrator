package org.example.infrastructure.services.bpmn.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextMetrics {
    
    private UUID id;
    
    private String processId;
    
    private String taskId;
    
    private double complexityScore;
    
    private int taskCount;
    
    private int gatewayCount;
    
    private int eventCount;
    
    private Map<String, Integer> dataFlowMapping;
    
    private double executionTime;
    
    private double throughput;
    
    private double resourceUtilization;
    
    private double businessValue;
    
    private LocalDateTime calculatedAt;
    
    public ContextMetrics(String processId, String taskId, double complexityScore) {
        this.id = UUID.randomUUID();
        this.processId = processId;
        this.taskId = taskId;
        this.complexityScore = complexityScore;
        this.calculatedAt = LocalDateTime.now();
    }
}