package org.example.domain.entities;

import org.example.domain.valueobjects.ModelId;
import org.example.domain.valueobjects.ModelStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * LLM Model entity representing a language model configuration
 */
public class LLMModel {
    private final ModelId modelId;
    private final String modelName;
    private final LLMProvider provider;
    private final Integer contextWindow;
    private final Integer maxTokens;
    private final Double temperature;
    private final Double topP;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final ModelStatus status;
    private final LocalDateTime lastUsed;
    private final Double sizeGB;

    public LLMModel(ModelId modelId, String modelName, LLMProvider provider,
                    Integer contextWindow, Integer maxTokens, Double temperature,
                    Double topP, Double frequencyPenalty, Double presencePenalty,
                    ModelStatus status, LocalDateTime lastUsed, Double sizeGB) {
        this.modelId = Objects.requireNonNull(modelId, "ModelId cannot be null");
        this.modelName = Objects.requireNonNull(modelName, "Model name cannot be null");
        this.provider = Objects.requireNonNull(provider, "Provider cannot be null");
        this.contextWindow = contextWindow;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.topP = topP;
        this.frequencyPenalty = frequencyPenalty;
        this.presencePenalty = presencePenalty;
        this.status = status != null ? status : ModelStatus.AVAILABLE;
        this.lastUsed = lastUsed;
        this.sizeGB = sizeGB;
    }

    public static LLMModel createLocalModel(String modelName, Double sizeGB) {
        return new LLMModel(
            ModelId.generate(),
            modelName,
            LLMProvider.LOCAL,
            4096,
            2048,
            0.7,
            0.9,
            0.0,
            0.0,
            ModelStatus.AVAILABLE,
            null,
            sizeGB
        );
    }

    public static LLMModel createOpenRouterModel(String modelName) {
        return new LLMModel(
            ModelId.generate(),
            modelName,
            LLMProvider.OPENROUTER,
            4096,
            2048,
            0.7,
            0.9,
            0.0,
            0.0,
            ModelStatus.AVAILABLE,
            null,
            null
        );
    }

    // Getters
    public ModelId getModelId() { return modelId; }
    public String getModelName() { return modelName; }
    public LLMProvider getProvider() { return provider; }
    public Integer getContextWindow() { return contextWindow; }
    public Integer getMaxTokens() { return maxTokens; }
    public Double getTemperature() { return temperature; }
    public Double getTopP() { return topP; }
    public Double getFrequencyPenalty() { return frequencyPenalty; }
    public Double getPresencePenalty() { return presencePenalty; }
    public ModelStatus getStatus() { return status; }
    public LocalDateTime getLastUsed() { return lastUsed; }
    public Double getSizeGB() { return sizeGB; }

    public boolean isLocal() {
        return provider == LLMProvider.LOCAL;
    }

    public boolean isLoaded() {
        return status == ModelStatus.LOADED;
    }

    public LLMModel markAsLoaded() {
        return new LLMModel(
            modelId, modelName, provider, contextWindow, maxTokens,
            temperature, topP, frequencyPenalty, presencePenalty,
            ModelStatus.LOADED, LocalDateTime.now(), sizeGB
        );
    }

    public LLMModel markAsUnloaded() {
        return new LLMModel(
            modelId, modelName, provider, contextWindow, maxTokens,
            temperature, topP, frequencyPenalty, presencePenalty,
            ModelStatus.AVAILABLE, lastUsed, sizeGB
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LLMModel llmModel = (LLMModel) o;
        return Objects.equals(modelId, llmModel.modelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelId);
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