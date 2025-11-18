package org.example.features.llm.domain.entities;

import org.example.features.llm.domain.valueobjects.ModelId;
import org.example.features.llm.domain.valueobjects.ModelStatus;

import java.time.LocalDateTime;

/**
 * Domain entity representing an LLM model
 */
public class LLMModel {

    private ModelId id;
    private String name;
    private String provider;
    private String modelType;
    private String version;
    private ModelStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public LLMModel() {}

    // Constructor with essential fields
    public LLMModel(ModelId id, String name, String provider, String modelType) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.modelType = modelType;
        this.status = ModelStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public void activate() {
        this.status = ModelStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = ModelStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return ModelStatus.ACTIVE.equals(this.status);
    }

    // Getters and setters
    public ModelId getId() {
        return id;
    }

    public void setId(ModelId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
        this.updatedAt = LocalDateTime.now();
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
        this.updatedAt = LocalDateTime.now();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        this.updatedAt = LocalDateTime.now();
    }

    public ModelStatus getStatus() {
        return status;
    }

    public void setStatus(ModelStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
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