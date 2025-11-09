package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpmn_gateways")
public class BpmnGateway {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "element_id", nullable = false)
    private String elementId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatewayType type;
    
    @Column(name = "gateway_direction", nullable = false)
    @Enumerated(EnumType.STRING)
    private GatewayDirection direction = GatewayDirection.UNSPECIFIED;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private BusinessProcess process;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public BpmnGateway() {}
    
    public BpmnGateway(String name, String elementId, GatewayType type) {
        this.name = name;
        this.elementId = elementId;
        this.type = type;
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
    
    public GatewayType getType() { return type; }
    public void setType(GatewayType type) { this.type = type; }
    
    public GatewayDirection getDirection() { return direction; }
    public void setDirection(GatewayDirection direction) { this.direction = direction; }
    
    public BusinessProcess getProcess() { return process; }
    public void setProcess(BusinessProcess process) { this.process = process; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public enum GatewayType {
        EXCLUSIVE,
        PARALLEL,
        INCLUSIVE,
        EVENT_BASED,
        COMPLEX
    }
    
    public enum GatewayDirection {
        UNSPECIFIED,
        CONVERGING,
        DIVERGING,
        MIXED
    }
}