package org.example.infrastructure.repositories;

import org.example.domain.entities.TestArtifact;
import org.example.domain.entities.TestProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for TestArtifact entities
 * Provides CRUD operations and custom search methods
 */
@Repository
public interface TestArtifactRepository extends JpaRepository<TestArtifact, Long> {
    
    /**
     * Find by artifact ID
     */
    Optional<TestArtifact> findByArtifactId(String artifactId);
    
    /**
     * Find by test project
     */
    List<TestArtifact> findByTestProject(TestProject testProject);
    
    /**
     * Find by test project ID
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.testProject.id = :projectId")
    List<TestArtifact> findByTestProjectId(@Param("projectId") Long projectId);
    
    /**
     * Find by test project project ID
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.testProject.projectId = :projectId")
    List<TestArtifact> findByTestProjectProjectId(@Param("projectId") String projectId);
    
    /**
     * Find by artifact type
     */
    List<TestArtifact> findByArtifactType(TestArtifact.ArtifactType artifactType);
    
    /**
     * Find by status
     */
    List<TestArtifact> findByStatus(TestArtifact.ArtifactStatus status);
    
    /**
     * Find by uploaded by
     */
    List<TestArtifact> findByUploadedBy(String uploadedBy);
    
    /**
     * Find all active artifacts
     */
    List<TestArtifact> findByIsActiveTrue();
    
    /**
     * Find primary artifacts
     */
    List<TestArtifact> findByIsPrimaryTrue();
    
    /**
     * Find by OpenAPI spec
     */
    List<TestArtifact> findByOpenApiSpecIsNotNull();
    
    /**
     * Find by BPMN diagram
     */
    List<TestArtifact> findByBpmnDiagramIsNotNull();
    
    /**
     * Find by artifact name containing (case-insensitive)
     */
    List<TestArtifact> findByArtifactNameContainingIgnoreCase(String name);
    
    /**
     * Find by version
     */
    List<TestArtifact> findByVersion(String version);
    
    /**
     * Find artifacts created after date
     */
    List<TestArtifact> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find artifacts created before date
     */
    List<TestArtifact> findByCreatedAtBefore(LocalDateTime date);
    
    /**
     * Find artifacts within date range
     */
    List<TestArtifact> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find artifacts used after date
     */
    List<TestArtifact> findByLastUsedAtAfter(LocalDateTime date);
    
    /**
     * Find by tag
     */
    @Query("SELECT a FROM TestArtifact a WHERE :tag MEMBER OF a.tags")
    List<TestArtifact> findByTag(@Param("tag") String tag);
    
    /**
     * Find by multiple tags
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.tags LIKE %:tag%")
    List<TestArtifact> findByTagsContaining(@Param("tag") String tag);
    
    /**
     * Find artifacts with usage count greater than
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.usageCount > :count")
    List<TestArtifact> findByUsageCountGreaterThan(@Param("count") int count);
    
    /**
     * Find artifacts with test session count greater than
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.testSessionCount > :count")
    List<TestArtifact> findByTestSessionCountGreaterThan(@Param("count") int count);
    
    /**
     * Find by description keyword
     */
    @Query("SELECT a FROM TestArtifact a WHERE LOWER(a.artifactDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TestArtifact> findByDescriptionContaining(@Param("keyword") String keyword);
    
    /**
     * Count active artifacts
     */
    @Query("SELECT COUNT(a) FROM TestArtifact a WHERE a.isActive = true")
    long countActiveArtifacts();
    
    /**
     * Count artifacts by type
     */
    @Query("SELECT COUNT(a) FROM TestArtifact a WHERE a.artifactType = :type")
    long countByArtifactType(@Param("type") TestArtifact.ArtifactType type);
    
    /**
     * Count artifacts by status
     */
    @Query("SELECT COUNT(a) FROM TestArtifact a WHERE a.status = :status")
    long countByStatus(@Param("status") TestArtifact.ArtifactStatus status);
    
    /**
     * Count artifacts by project
     */
    @Query("SELECT COUNT(a) FROM TestArtifact a WHERE a.testProject.id = :projectId")
    long countByProjectId(@Param("projectId") Long projectId);
    
    /**
     * Find recently created artifacts
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<TestArtifact> findRecentlyCreated(@Param("since") LocalDateTime since);
    
    /**
     * Find recently updated artifacts
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.updatedAt >= :since ORDER BY a.updatedAt DESC")
    List<TestArtifact> findRecentlyUpdated(@Param("since") LocalDateTime since);
    
    /**
     * Find most used artifacts
     */
    @Query("SELECT a FROM TestArtifact a ORDER BY a.usageCount DESC")
    List<TestArtifact> findMostUsedArtifacts(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Find by project and type
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.testProject.id = :projectId AND a.artifactType = :type")
    List<TestArtifact> findByProjectIdAndType(@Param("projectId") Long projectId, @Param("type") TestArtifact.ArtifactType type);
    
    /**
     * Find by project and status
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.testProject.id = :projectId AND a.status = :status")
    List<TestArtifact> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TestArtifact.ArtifactStatus status);
    
    /**
     * Find artifacts with no recent usage
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.lastUsedAt IS NULL OR a.lastUsedAt < :since")
    List<TestArtifact> findWithNoRecentUsage(@Param("since") LocalDateTime since);
    
    /**
     * Find by type and status
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.artifactType = :type AND a.status = :status")
    List<TestArtifact> findByTypeAndStatus(@Param("type") TestArtifact.ArtifactType type, @Param("status") TestArtifact.ArtifactStatus status);
    
    /**
     * Check if artifact exists by name
     */
    boolean existsByArtifactName(String artifactName);
    
    /**
     * Check if artifact exists by artifact ID
     */
    boolean existsByArtifactId(String artifactId);
    
    /**
     * Find artifacts ordered by creation date
     */
    List<TestArtifact> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find artifacts ordered by update date
     */
    List<TestArtifact> findAllByOrderByUpdatedAtDesc();
    
    /**
     * Find artifacts ordered by name
     */
    List<TestArtifact> findAllByOrderByArtifactNameAsc();
    
    /**
     * Find artifacts ordered by usage count
     */
    List<TestArtifact> findAllByOrderByUsageCountDesc();
    
    /**
     * Find artifacts ordered by test session count
     */
    List<TestArtifact> findAllByOrderByTestSessionCountDesc();
    
    /**
     * Find artifacts with pagination
     */
    @Query("SELECT a FROM TestArtifact a ORDER BY a.createdAt DESC")
    List<TestArtifact> findAllWithPagination(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Find primary artifacts for project
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.testProject.id = :projectId AND a.isPrimary = true")
    List<TestArtifact> findPrimaryArtifactsByProject(@Param("projectId") Long projectId);
    
    /**
     * Find OpenAPI artifacts with specific version
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.artifactType = 'OPENAPI_SPEC' AND a.version = :version")
    List<TestArtifact> findOpenApiArtifactsByVersion(@Param("version") String version);
    
    /**
     * Find BPMN artifacts with specific process engine
     */
    @Query("SELECT a FROM TestArtifact a WHERE a.artifactType = 'BPMN_DIAGRAM' AND a.bpmnDiagram.processEngine = :engine")
    List<TestArtifact> findBpmnArtifactsByProcessEngine(@Param("engine") String engine);
}