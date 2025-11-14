package org.example.features.llm.domain.repositories;

import org.example.features.llm.domain.entities.AiModel;
import org.example.features.llm.domain.valueobjects.ModelStatus;
import org.example.features.llm.domain.valueobjects.ModelId;
import org.example.domain.valueobjects.ModelType;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for AiModel entities
 */
public interface AiModelRepository {

    /**
     * Find models by status
     */
    List<AiModel> findByStatus(ModelStatus status);

    /**
     * Find models by type
     */
    List<AiModel> findByType(ModelType type);

    /**
     * Find models by name containing
     */
    List<AiModel> findByNameContainingIgnoreCase(String name);

    /**
     * Find loaded models
     */
    List<AiModel> findLoadedModels();

    /**
     * Find available models
     */
    List<AiModel> findAvailableModels();

    /**
     * Find models with specific metadata key
     */
    List<AiModel> findByMetadataKey(String key);

    /**
     * Check if model exists by name
     */
    boolean existsByName(String name);

    /**
     * Find models by path
     */
    Optional<AiModel> findByModelPath(java.nio.file.Path modelPath);

    /**
     * Save a model
     */
    AiModel save(AiModel model);

    /**
     * Find by ID
     */
    Optional<AiModel> findById(ModelId id);

    /**
     * Find all models
     */
    List<AiModel> findAll();

    /**
     * Delete a model
     */
    void delete(AiModel model);
}