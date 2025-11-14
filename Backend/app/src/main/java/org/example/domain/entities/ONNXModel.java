package org.example.domain.entities;

import org.example.shared.core.valueobjects.ModelId;
import org.example.shared.core.valueobjects.ModelStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * ONNX Model entity representing a local ONNX language model
 */
public class ONNXModel {
    private final ModelId modelId;
    private final String name;
    private final String version;
    private final String filePath;
    private final ModelCapabilities capabilities;
    private final ModelStatus status;
    private final LocalDateTime loadedAt;
    private final long memoryUsage;
    private final Double sizeGB;
    private final Integer contextWindow;
    private final String modelType;
    private final Map<String, Object> metadata;
    private final boolean isQuantized;
    private final String hardwareAcceleration;

    public ONNXModel(ModelId modelId, String name, String version, String filePath,
                     ModelCapabilities capabilities, ModelStatus status, LocalDateTime loadedAt,
                     long memoryUsage, Double sizeGB, Integer contextWindow, String modelType,
                     Map<String, Object> metadata, boolean isQuantized, String hardwareAcceleration) {
        this.modelId = Objects.requireNonNull(modelId, "ModelId cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.version = version;
        this.filePath = Objects.requireNonNull(filePath, "FilePath cannot be null");
        this.capabilities = Objects.requireNonNull(capabilities, "Capabilities cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.loadedAt = loadedAt;
        this.memoryUsage = memoryUsage;
        this.sizeGB = sizeGB;
        this.contextWindow = contextWindow;
        this.modelType = modelType;
        this.metadata = metadata;
        this.isQuantized = isQuantized;
        this.hardwareAcceleration = hardwareAcceleration;
    }

    public static ONNXModel create(String name, String filePath) {
        return new ONNXModel(
            ModelId.generate(),
            name,
            "1.0.0",
            filePath,
            ModelCapabilities.defaultCapabilities(),
            ModelStatus.AVAILABLE,
            null,
            0L,
            null,
            4096,
            "text-generation",
            null,
            false,
            "CPU"
        );
    }

    public static ONNXModel createWithMetadata(String name, String filePath, 
                                              Map<String, Object> metadata) {
        return new ONNXModel(
            ModelId.generate(),
            name,
            "1.0.0",
            filePath,
            ModelCapabilities.defaultCapabilities(),
            ModelStatus.AVAILABLE,
            null,
            0L,
            null,
            4096,
            "text-generation",
            metadata,
            false,
            "CPU"
        );
    }

    // Getters
    public ModelId getModelId() { return modelId; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getFilePath() { return filePath; }
    public ModelCapabilities getCapabilities() { return capabilities; }
    public ModelStatus getStatus() { return status; }
    public LocalDateTime getLoadedAt() { return loadedAt; }
    public long getMemoryUsage() { return memoryUsage; }
    public Double getSizeGB() { return sizeGB; }
    public Integer getContextWindow() { return contextWindow; }
    public String getModelType() { return modelType; }
    public Map<String, Object> getMetadata() { return metadata; }
    public boolean isQuantized() { return isQuantized; }
    public String getHardwareAcceleration() { return hardwareAcceleration; }

    public boolean isLoaded() {
        return status == ModelStatus.LOADED;
    }

    public boolean isAvailable() {
        return status == ModelStatus.AVAILABLE || status == ModelStatus.LOADED;
    }

    public ONNXModel markAsLoaded() {
        return new ONNXModel(
            modelId, name, version, filePath, capabilities,
            ModelStatus.LOADED, LocalDateTime.now(), memoryUsage,
            sizeGB, contextWindow, modelType, metadata, isQuantized, hardwareAcceleration
        );
    }

    public ONNXModel markAsUnloaded() {
        return new ONNXModel(
            modelId, name, version, filePath, capabilities,
            ModelStatus.AVAILABLE, null, 0L,
            sizeGB, contextWindow, modelType, metadata, isQuantized, hardwareAcceleration
        );
    }

    public ONNXModel markAsLoading() {
        return new ONNXModel(
            modelId, name, version, filePath, capabilities,
            ModelStatus.LOADING, null, 0L,
            sizeGB, contextWindow, modelType, metadata, isQuantized, hardwareAcceleration
        );
    }

    public ONNXModel updateMemoryUsage(long memoryUsage) {
        return new ONNXModel(
            modelId, name, version, filePath, capabilities,
            status, loadedAt, memoryUsage, sizeGB,
            contextWindow, modelType, metadata, isQuantized, hardwareAcceleration
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ONNXModel onnxModel = (ONNXModel) o;
        return Objects.equals(modelId, onnxModel.modelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelId);
    }

    @Override
    public String toString() {
        return "ONNXModel{" +
                "modelId=" + modelId +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", status=" + status +
                ", memoryUsage=" + memoryUsage +
                '}';
    }

    /**
     * Model capabilities for ONNX models
     */
    public static class ModelCapabilities {
        private final boolean supportsTextGeneration;
        private final boolean supportsEmbeddings;
        private final boolean supportsClassification;
        private final boolean supportsStreaming;
        private final int maxContextLength;
        private final int maxTokens;

        public ModelCapabilities(boolean supportsTextGeneration, boolean supportsEmbeddings,
                               boolean supportsClassification, boolean supportsStreaming,
                               int maxContextLength, int maxTokens) {
            this.supportsTextGeneration = supportsTextGeneration;
            this.supportsEmbeddings = supportsEmbeddings;
            this.supportsClassification = supportsClassification;
            this.supportsStreaming = supportsStreaming;
            this.maxContextLength = maxContextLength;
            this.maxTokens = maxTokens;
        }

        public static ModelCapabilities defaultCapabilities() {
            return new ModelCapabilities(true, true, false, false, 4096, 2048);
        }

        public static ModelCapabilities textGenerationCapabilities() {
            return new ModelCapabilities(true, false, false, false, 8192, 4096);
        }

        public static ModelCapabilities embeddingCapabilities() {
            return new ModelCapabilities(false, true, false, false, 2048, 1024);
        }

        // Getters
        public boolean isSupportsTextGeneration() { return supportsTextGeneration; }
        public boolean isSupportsEmbeddings() { return supportsEmbeddings; }
        public boolean isSupportsClassification() { return supportsClassification; }
        public boolean isSupportsStreaming() { return supportsStreaming; }
        public int getMaxContextLength() { return maxContextLength; }
        public int getMaxTokens() { return maxTokens; }
    }
}