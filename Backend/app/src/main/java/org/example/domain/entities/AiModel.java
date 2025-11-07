package org.example.domain.entities;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class AiModel {
    private final org.example.domain.valueobjects.ModelId id;
    private final String name;
    private final org.example.domain.valueobjects.ModelType type;
    private final Path modelPath;
    private final Map<String, Object> metadata;
    private final org.example.domain.valueobjects.ModelStatus status;

    public AiModel(org.example.domain.valueobjects.ModelId id, String name,
                   org.example.domain.valueobjects.ModelType type, Path modelPath,
                   Map<String, Object> metadata, org.example.domain.valueobjects.ModelStatus status) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.modelPath = modelPath; // Can be null for mock models
        this.metadata = metadata; // Can be null
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    // Getters
    public org.example.domain.valueobjects.ModelId getId() { return id; }
    public String getName() { return name; }
    public org.example.domain.valueobjects.ModelType getType() { return type; }
    public Path getModelPath() { return modelPath; }
    public Map<String, Object> getMetadata() { return metadata; }
    public org.example.domain.valueobjects.ModelStatus getStatus() { return status; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AiModel aiModel = (AiModel) obj;
        return Objects.equals(id, aiModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AiModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }

    // Mock data for testing
    public static AiModel createMockModel() {
        return new AiModel(
            new org.example.domain.valueobjects.ModelId("mock-model-1"),
            "Mock AI Model",
            org.example.domain.valueobjects.ModelType.ONNX,
            null, // No model path for mock
            null, // No metadata for mock
            org.example.domain.valueobjects.ModelStatus.LOADED
        );
    }
}