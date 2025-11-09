package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing test data
 */
@Entity
@Table(name = "test_data")
public class TestData {
    
    @Id
    private UUID id;
    
    @Column(name = "data_set_id", nullable = false)
    private String dataSetId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "data_type", nullable = false)
    private String dataType;
    
    @Column(name = "data_value", columnDefinition = "TEXT", nullable = false)
    private String dataValue;
    
    @Column(name = "is_sensitive", nullable = false)
    private boolean isSensitive = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public TestData() {
    }
    
    public TestData(String dataSetId, String name, String dataType, String dataValue, boolean isSensitive) {
        this.dataSetId = dataSetId;
        this.name = name;
        this.dataType = dataType;
        this.dataValue = dataValue;
        this.isSensitive = isSensitive;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getDataSetId() {
        return dataSetId;
    }
    
    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getDataValue() {
        return dataValue;
    }
    
    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
    
    public boolean isSensitive() {
        return isSensitive;
    }
    
    public void setSensitive(boolean sensitive) {
        isSensitive = sensitive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}