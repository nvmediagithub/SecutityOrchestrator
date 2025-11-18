package org.example.features.llm.domain.services;

import org.example.features.llm.domain.entities.LLMModel;
import org.example.features.llm.domain.valueobjects.ModelStatus;
import org.example.features.llm.domain.valueobjects.ModelId;
import org.example.llm.domain.LLMProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LLMModel entities - Domain Service Port
 */
public interface LLMModelRepository {

    /**
     * Find models by provider
     */
    List<LLMModel> findByProvider(LLMProvider provider);

    /**
     * Find models by status
     */
    List<LLMModel> findByStatus(ModelStatus status);

    /**
     * Find models by name containing
     */
    List<LLMModel> findByModelNameContainingIgnoreCase(String name);

    /**
     * Find local models
     */
    List<LLMModel> findLocalModels();

    /**
     * Find OpenRouter models
     */
    List<LLMModel> findOpenRouterModels();

    /**
     * Find loaded models
     */
    List<LLMModel> findLoadedModels();

    /**
     * Find available models
     */
    List<LLMModel> findAvailableModels();

    /**
     * Find models by context window
     */
    List<LLMModel> findByContextWindow(Integer contextWindow);

    /**
     * Find models by max tokens
     */
    List<LLMModel> findByMaxTokens(Integer maxTokens);

    /**
     * Find models with size greater than specified
     */
    List<LLMModel> findBySizeGBGreaterThan(Double sizeGB);

    /**
     * Find models used after date
     */
    List<LLMModel> findRecentlyUsed(LocalDateTime date);

    /**
     * Check if model exists by name
     */
    boolean existsByModelName(String modelName);

    /**
     * Save a model
     */
    LLMModel save(LLMModel model);

    /**
     * Find by ID
     */
    Optional<LLMModel> findById(ModelId id);

    /**
     * Find all models
     */
    List<LLMModel> findAll();

    /**
     * Delete a model
     */
    void delete(LLMModel model);
}