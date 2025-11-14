package org.example.bpmn.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.example.domain.entities.BusinessProcess;

@Entity
@Table(name = "bpmn_tasks")
public class BpmnTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BpmnTaskType type;
    
    @Column(name = "element_id", nullable = false)
    private String elementId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private BusinessProcess process;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public BpmnTask() {}
    
    public BpmnTask(String name, String description, BpmnTaskType type, String elementId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.elementId = elementId;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BpmnTaskType getType() { return type; }
    public void setType(BpmnTaskType type) { this.type = type; }
    
    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }
    
    public BusinessProcess getProcess() { return process; }
    public void setProcess(BusinessProcess process) { this.process = process; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public enum BpmnTaskType {
        USER_TASK,
        SERVICE_TASK,
        SCRIPT_TASK,
        BUSINESS_RULE_TASK,
        MANUAL_TASK,
        RECEIVE_TASK,
        SEND_TASK,
        CALL_ACTIVITY,
        SUB_PROCESS
    }
}