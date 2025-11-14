package org.example.features.llm.infrastructure.adapters;

import org.example.features.llm.domain.entities.LLMModel;
import org.example.features.llm.domain.valueobjects.ModelStatus;
import org.example.features.llm.domain.valueobjects.ModelId;
import org.example.llm.domain.LLMProvider;
import org.example.features.llm.domain.services.LLMModelRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Infrastructure adapter implementing the LLMModelRepository domain port
 * Adapts the existing JPA repository to domain interface
 */
@Component
public class LLMModelRepositoryAdapter implements LLMModelRepository {

    // TODO: Inject the actual JPA repository
    // private final LLMModelRepository jpaRepository;

    public LLMModelRepositoryAdapter() {
        // TODO: Initialize with actual JPA repository
    }

    @Override
    public List<LLMModel> findByProvider(LLMProvider provider) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find by provider not yet implemented");
    }

    @Override
    public List<LLMModel> findByStatus(ModelStatus status) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find by status not yet implemented");
    }

    @Override
    public List<LLMModel> findByModelNameContainingIgnoreCase(String name) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find by model name not yet implemented");
    }

    @Override
    public List<LLMModel> findLocalModels() {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find local models not yet implemented");
    }

    @Override
    public List<LLMModel> findOpenRouterModels() {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find OpenRouter models not yet implemented");
    }

    @Override
    public List<LLMModel> findLoadedModels() {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find loaded models not yet implemented");
    }

    @Override
    public List<LLMModel> findAvailableModels() {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find available models not yet implemented");
    }

    @Override
    public List<LLMModel> findByContextWindow(Integer contextWindow) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find by context window not yet implemented");
    }

    @Override
    public List<LLMModel> findByMaxTokens(Integer maxTokens) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find by max tokens not yet implemented");
    }

    @Override
    public List<LLMModel> findBySizeGBGreaterThan(Double sizeGB) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find by size GB not yet implemented");
    }

    @Override
    public List<LLMModel> findRecentlyUsed(LocalDateTime date) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find recently used not yet implemented");
    }

    @Override
    public boolean existsByModelName(String modelName) {
        // TODO: Delegate to JPA repository
        return false;
    }

    @Override
    public LLMModel save(LLMModel model) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Save not yet implemented");
    }

    @Override
    public Optional<LLMModel> findById(ModelId id) {
        // TODO: Delegate to JPA repository
        return Optional.empty();
    }

    @Override
    public List<LLMModel> findAll() {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Find all not yet implemented");
    }

    @Override
    public void delete(LLMModel model) {
        // TODO: Delegate to JPA repository
        throw new UnsupportedOperationException("Delete not yet implemented");
    }
}