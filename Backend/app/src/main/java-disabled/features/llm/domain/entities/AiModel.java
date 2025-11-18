package org.example.features.llm.domain.entities;

import org.example.shared.core.valueobjects.ModelId;
import org.example.shared.core.valueobjects.ModelStatus;

/**
 * Domain entity representing an AI model in the LLM feature module.
 * This class encapsulates the core properties and behavior of an AI model.
 */
public class AiModel {
    private final ModelId id;
    private final String name;
    private final String version;
    private ModelStatus status;
    private final PerformanceMetrics performanceMetrics;

    public AiModel(ModelId id, String name, String version, ModelStatus status, PerformanceMetrics performanceMetrics) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.status = status;
        this.performanceMetrics = performanceMetrics;
    }

    public ModelId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public ModelStatus getStatus() {
        return status;
    }

    public void setStatus(ModelStatus status) {
        this.status = status;
    }

    public PerformanceMetrics getPerformanceMetrics() {
        return performanceMetrics;
    }
}