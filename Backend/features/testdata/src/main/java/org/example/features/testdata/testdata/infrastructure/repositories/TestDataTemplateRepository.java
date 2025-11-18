package org.example.features.testdata.testdata.infrastructure.repositories;

import org.example.features.testdata.testdata.domain.TestDataTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TestDataTemplate entity
 */
@Repository
public interface TestDataTemplateRepository extends JpaRepository<TestDataTemplate, Long> {

    /**
     * Find template by template ID
     */
    Optional<TestDataTemplate> findByTemplateId(@Param("templateId") String templateId);

    /**
     * Find templates by name containing
     */
    List<TestDataTemplate> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find templates by data type
     */
    List<TestDataTemplate> findByDataType(@Param("dataType") String dataType);

    /**
     * Find active templates
     */
    @Query("SELECT t FROM TestDataTemplate t WHERE t.status = 'ACTIVE'")
    List<TestDataTemplate> findActiveTemplates();

    /**
     * Find templates by status
     */
    List<TestDataTemplate> findByStatus(TestDataTemplate.TemplateStatus status);

    /**
     * Find templates by created by
     */
    List<TestDataTemplate> findByCreatedBy(@Param("createdBy") String createdBy);

    /**
     * Count templates by data type
     */
    @Query("SELECT COUNT(t) FROM TestDataTemplate t WHERE t.dataType = :dataType")
    long countByDataType(@Param("dataType") String dataType);

    /**
     * Find templates with description containing
     */
    @Query("SELECT t FROM TestDataTemplate t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TestDataTemplate> findByDescriptionContaining(@Param("keyword") String keyword);
}