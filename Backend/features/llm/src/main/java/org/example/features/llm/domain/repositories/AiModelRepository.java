package org.example.features.llm.domain.repositories;

import org.example.features.llm.domain.entities.AiModel;
import org.example.features.llm.domain.valueobjects.ModelId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AI model operations
 */
public interface AiModelRepository {

    /**
     * Find an AI model by its ID
     * @param id The model ID
     * @return Optional containing the model if found
     */
    Optional<AiModel> findById(ModelId id);

    /**
     * Find all AI models
     * @return List of all AI models
     */
    List<AiModel> findAll();

    /**
     * Find AI models by provider
     * @param provider The provider name
     * @return List of AI models for the provider
     */
    List<AiModel> findByProvider(String provider);

    /**
     * Save an AI model
     * @param model The model to save
     * @return The saved model
     */
    AiModel save(AiModel model);

    /**
     * Delete an AI model by its ID
     * @param id The model ID to delete
     */
    void deleteById(ModelId id);

    /**
     * Check if an AI model exists by its ID
     * @param id The model ID
     * @return true if the model exists, false otherwise
     */
    boolean existsById(ModelId id);
}