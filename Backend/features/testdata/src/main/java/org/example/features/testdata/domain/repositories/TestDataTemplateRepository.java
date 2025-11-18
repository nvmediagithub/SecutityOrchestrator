package org.example.features.testdata.domain.repositories;

import org.example.shared.domain.entities.TestDataTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for TestDataTemplate entities
 */
public interface TestDataTemplateRepository {

    /**
     * Find template by template ID
     */
    Optional<TestDataTemplate> findByTemplateId(String templateId);

    /**
     * Find templates by name containing
     */
    List<TestDataTemplate> findByNameContainingIgnoreCase(String name);

    /**
     * Find templates by data type
     */
    List<TestDataTemplate> findByDataType(String dataType);

    /**
     * Find active templates
     */
    List<TestDataTemplate> findActiveTemplates();

    /**
     * Find templates by status
     */
    List<TestDataTemplate> findByStatus(TestDataTemplate.TemplateStatus status);

    /**
     * Find templates by created by
     */
    List<TestDataTemplate> findByCreatedBy(String createdBy);

    /**
     * Count templates by data type
     */
    long countByDataType(String dataType);

    /**
     * Find templates with description containing
     */
    List<TestDataTemplate> findByDescriptionContaining(String keyword);

    /**
     * Save a template
     */
    TestDataTemplate save(TestDataTemplate template);

    /**
     * Find by ID
     */
    Optional<TestDataTemplate> findById(Long id);

    /**
     * Find all templates
     */
    List<TestDataTemplate> findAll();

    /**
     * Delete a template
     */
    void delete(TestDataTemplate template);
}