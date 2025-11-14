package org.example.features.llm.domain.entities;

import jakarta.persistence.*;
import org.example.features.llm.domain.valueobjects.ModelId;
import org.example.features.llm.domain.valueobjects.ModelStatus;
import org.example.llm.domain.LLMProvider;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Domain entity representing a Large Language Model in the LLM feature module.
 * This class encapsulates all properties and behavior of an LLM model.
 */
@Entity
@Table(name = "llm_models")
public class LLMModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ModelId modelId;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private LLMProvider provider;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelStatus status;

    @Column(name = "context_window")
    private Integer contextWindow;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "size_gb")
    private Double sizeGB;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public LLMModel() {}

    // Constructor with essential fields
    public LLMModel(ModelId modelId, String modelName, LLMProvider provider, ModelStatus status) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.provider = provider;
        this.status = status;
    }

    // Full constructor
    public LLMModel(ModelId modelId, String modelName, LLMProvider provider, ModelStatus status,
                   Integer contextWindow, Integer maxTokens, Double sizeGB, Double temperature,
                   String metadata, LocalDateTime lastUsed) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.provider = provider;
        this.status = status;
        this.contextWindow = contextWindow;
        this.maxTokens = maxTokens;
        this.sizeGB = sizeGB;
        this.temperature = temperature;
        this.metadata = metadata;
        this.lastUsed = lastUsed;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModelId getModelId() {
        return modelId;
    }

    public void setModelId(ModelId modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public LLMProvider getProvider() {
        return provider;
    }

    public void setProvider(LLMProvider provider) {
        this.provider = provider;
    }

    public ModelStatus getStatus() {
        return status;
    }

    public void setStatus(ModelStatus status) {
        this.status = status;
    }

    public Integer getContextWindow() {
        return contextWindow;
    }

    public void setContextWindow(Integer contextWindow) {
        this.contextWindow = contextWindow;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getSizeGB() {
        return sizeGB;
    }

    public void setSizeGB(Double sizeGB) {
        this.sizeGB = sizeGB;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LLMModel llmModel = (LLMModel) o;
        return modelId != null ? modelId.equals(llmModel.modelId) : llmModel.modelId == null;
    }

    @Override
    public int hashCode() {
        return modelId != null ? modelId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LLMModel{" +
                "modelId=" + modelId +
                ", modelName='" + modelName + '\'' +
                ", provider=" + provider +
                ", status=" + status +
                '}';
    }
}