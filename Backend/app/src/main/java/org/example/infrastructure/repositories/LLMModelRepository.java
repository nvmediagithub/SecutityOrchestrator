package org.example.infrastructure.repositories;

import org.example.features.llm.domain.entities.LLMModel;
import org.example.llm.domain.LLMProvider;
import org.example.features.llm.domain.valueobjects.ModelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for LLMModel entities
 */
@Repository
public interface LLMModelRepository extends JpaRepository<LLMModel, org.example.features.llm.domain.valueobjects.ModelId> {

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
    @Query("SELECT m FROM LLMModel m WHERE m.provider = 'LOCAL'")
    List<LLMModel> findLocalModels();

    /**
     * Find OpenRouter models
     */
    @Query("SELECT m FROM LLMModel m WHERE m.provider = 'OPENROUTER'")
    List<LLMModel> findOpenRouterModels();

    /**
     * Find loaded models
     */
    @Query("SELECT m FROM LLMModel m WHERE m.status = 'LOADED'")
    List<LLMModel> findLoadedModels();

    /**
     * Find available models
     */
    @Query("SELECT m FROM LLMModel m WHERE m.status = 'AVAILABLE'")
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
    @Query("SELECT m FROM LLMModel m WHERE m.sizeGB > :sizeGB")
    List<LLMModel> findBySizeGBGreaterThan(@Param("sizeGB") Double sizeGB);

    /**
     * Find models used after date
     */
    @Query("SELECT m FROM LLMModel m WHERE m.lastUsed >= :date")
    List<LLMModel> findRecentlyUsed(@Param("date") LocalDateTime date);

    /**
     * Check if model exists by name
     */
    boolean existsByModelName(String modelName);

    /**
     * Find models with specific temperature
     */
    List<LLMModel> findByTemperature(Double temperature);
}
