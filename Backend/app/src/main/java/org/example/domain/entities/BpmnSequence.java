package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpmn_sequences")
public class BpmnSequence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sequence_id", nullable = false)
    private String sequenceId;
    
    @Column(name = "source_element_id", nullable = false)
    private String sourceElementId;
    
    @Column(name = "target_element_id", nullable = false)
    private String targetElementId;
    
    @Column(columnDefinition = "TEXT")
    private String condition;
    
    @Column(name = "sequence_flow_id", nullable = false)
    private String sequenceFlowId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private BusinessProcess process;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public BpmnSequence() {}
    
    public BpmnSequence(String sequenceId, String sourceElementId, String targetElementId, String sequenceFlowId) {
        this.sequenceId = sequenceId;
        this.sourceElementId = sourceElementId;
        this.targetElementId = targetElementId;
        this.sequenceFlowId = sequenceFlowId;
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
    
    public String getSequenceId() { return sequenceId; }
    public void setSequenceId(String sequenceId) { this.sequenceId = sequenceId; }
    
    public String getSourceElementId() { return sourceElementId; }
    public void setSourceElementId(String sourceElementId) { this.sourceElementId = sourceElementId; }
    
    public String getTargetElementId() { return targetElementId; }
    public void setTargetElementId(String targetElementId) { this.targetElementId = targetElementId; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getSequenceFlowId() { return sequenceFlowId; }
    public void setSequenceFlowId(String sequenceFlowId) { this.sequenceFlowId = sequenceFlowId; }
    
    public BusinessProcess getProcess() { return process; }
    public void setProcess(BusinessProcess process) { this.process = process; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}