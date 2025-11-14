package org.example.infrastructure.repositories;

import org.example.features.llm.domain.entities.AiModel;
import org.example.features.llm.domain.valueobjects.ModelStatus;
import org.example.domain.valueobjects.ModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AiModel entities
 */
@Repository
public interface AiModelRepository extends JpaRepository<AiModel, org.example.features.llm.domain.valueobjects.ModelId> {

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
    @Query("SELECT m FROM AiModel m WHERE m.status = 'LOADED'")
    List<AiModel> findLoadedModels();

    /**
     * Find available models
     */
    @Query("SELECT m FROM AiModel m WHERE m.status = 'AVAILABLE'")
    List<AiModel> findAvailableModels();

    /**
     * Find models with specific metadata key
     */
    @Query("SELECT m FROM AiModel m WHERE m.metadata IS NOT NULL AND m.metadata LIKE %:key%")
    List<AiModel> findByMetadataKey(@Param("key") String key);

    /**
     * Check if model exists by name
     */
    boolean existsByName(String name);

    /**
     * Find models by path
     */
    Optional<AiModel> findByModelPath(java.nio.file.Path modelPath);
}