package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpmn_events")
public class BpmnEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "element_id", nullable = false)
    private String elementId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventSubType eventType;
    
    @Column(columnDefinition = "TEXT")
    private String messageName;
    
    @Column(columnDefinition = "TEXT")
    private String messagePayload;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private BusinessProcess process;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public BpmnEvent() {}
    
    public BpmnEvent(String name, String elementId, EventType type, EventSubType eventType) {
        this.name = name;
        this.elementId = elementId;
        this.type = type;
        this.eventType = eventType;
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
    
    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }
    
    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }
    
    public EventSubType getEventType() { return eventType; }
    public void setEventType(EventSubType eventType) { this.eventType = eventType; }
    
    public String getMessageName() { return messageName; }
    public void setMessageName(String messageName) { this.messageName = messageName; }
    
    public String getMessagePayload() { return messagePayload; }
    public void setMessagePayload(String messagePayload) { this.messagePayload = messagePayload; }
    
    public BusinessProcess getProcess() { return process; }
    public void setProcess(BusinessProcess process) { this.process = process; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public enum EventType {
        START,
        END,
        INTERMEDIATE_THROW,
        INTERMEDIATE_CATCH
    }
    
    public enum EventSubType {
        MESSAGE,
        TIMER,
        SIGNAL,
        ERROR,
        ESCALATION,
        COMPENSATION,
        CONDITIONAL,
        PARALLEL_MULTIPLE,
        CANCEL,
        MULTIPLE,
        NON_INTERRUPTING_MULTIPLE,
        TERMINATE
    }
}