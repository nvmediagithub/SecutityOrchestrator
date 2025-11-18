package org.example.features.llm.domain.repositories;

import org.example.features.llm.domain.entities.LLMModel;
import org.example.features.llm.domain.valueobjects.ModelId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LLM model operations
 */
public interface LLMModelRepository {

    /**
     * Find an LLM model by its ID
     * @param id The model ID
     * @return Optional containing the model if found
     */
    Optional<LLMModel> findById(ModelId id);

    /**
     * Find all LLM models
     * @return List of all LLM models
     */
    List<LLMModel> findAll();

    /**
     * Find LLM models by provider
     * @param provider The provider name
     * @return List of LLM models for the provider
     */
    List<LLMModel> findByProvider(String provider);

    /**
     * Find LLM models by model type
     * @param modelType The model type
     * @return List of LLM models of the specified type
     */
    List<LLMModel> findByModelType(String modelType);

    /**
     * Save an LLM model
     * @param model The model to save
     * @return The saved model
     */
    LLMModel save(LLMModel model);

    /**
     * Delete an LLM model by its ID
     * @param id The model ID to delete
     */
    void deleteById(ModelId id);

    /**
     * Check if an LLM model exists by its ID
     * @param id The model ID
     * @return true if the model exists, false otherwise
     */
    boolean existsById(ModelId id);
}