package org.example.infrastructure.repositories;

import org.example.domain.entities.TestProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for TestProject entities
 * Provides CRUD operations and custom search methods
 */
@Repository
public interface TestProjectRepository extends JpaRepository<TestProject, Long> {
    
    /**
     * Find by project ID
     */
    Optional<TestProject> findByProjectId(String projectId);
    
    /**
     * Find by name containing (case-insensitive)
     */
    List<TestProject> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find by owner
     */
    List<TestProject> findByOwner(String owner);
    
    /**
     * Find by status
     */
    List<TestProject> findByStatus(TestProject.ProjectStatus status);
    
    /**
     * Find by environment
     */
    List<TestProject> findByEnvironment(String environment);
    
    /**
     * Find by test framework
     */
    List<TestProject> findByTestFramework(String testFramework);
    
    /**
     * Find all active projects
     */
    List<TestProject> findByIsActiveTrue();
    
    /**
     * Find by base URL pattern
     */
    List<TestProject> findByBaseUrlContaining(String baseUrl);
    
    /**
     * Find projects with specific timeout
     */
    List<TestProject> findByTimeoutMs(Integer timeoutMs);
    
    /**
     * Find projects with specific retry count
     */
    List<TestProject> findByRetryCount(Integer retryCount);
    
    /**
     * Find projects with parallel execution enabled
     */
    List<TestProject> findByParallelExecutionTrue();
    
    /**
     * Find projects created after date
     */
    List<TestProject> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find projects created before date
     */
    List<TestProject> findByCreatedAtBefore(LocalDateTime date);
    
    /**
     * Find projects within date range
     */
    List<TestProject> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find projects with last test execution after date
     */
    List<TestProject> findByLastTestExecutionAfter(LocalDateTime date);
    
    /**
     * Find projects by tag
     */
    @Query("SELECT p FROM TestProject p WHERE :tag MEMBER OF p.tags")
    List<TestProject> findByTag(@Param("tag") String tag);
    
    /**
     * Find projects by multiple tags
     */
    @Query("SELECT p FROM TestProject p WHERE p.tags LIKE %:tag%")
    List<TestProject> findByTagsContaining(@Param("tag") String tag);
    
    /**
     * Find projects with artifact count greater than specified
     */
    @Query("SELECT p FROM TestProject p WHERE p.artifactCount > :count")
    List<TestProject> findByArtifactCountGreaterThan(@Param("count") int count);
    
    /**
     * Find projects with session count greater than specified
     */
    @Query("SELECT p FROM TestProject p WHERE p.sessionCount > :count")
    List<TestProject> findBySessionCountGreaterThan(@Param("count") int count);
    
    /**
     * Find projects by description keyword
     */
    @Query("SELECT p FROM TestProject p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TestProject> findByDescriptionContaining(@Param("keyword") String keyword);
    
    /**
     * Count active projects
     */
    @Query("SELECT COUNT(p) FROM TestProject p WHERE p.isActive = true")
    long countActiveProjects();
    
    /**
     * Count projects by status
     */
    @Query("SELECT COUNT(p) FROM TestProject p WHERE p.status = :status")
    long countByStatus(@Param("status") TestProject.ProjectStatus status);
    
    /**
     * Find recently updated projects
     */
    @Query("SELECT p FROM TestProject p WHERE p.updatedAt >= :since ORDER BY p.updatedAt DESC")
    List<TestProject> findRecentlyUpdated(@Param("since") LocalDateTime since);
    
    /**
     * Find projects with recent test execution
     */
    @Query("SELECT p FROM TestProject p WHERE p.lastTestExecution IS NOT NULL AND p.lastTestExecution >= :since ORDER BY p.lastTestExecution DESC")
    List<TestProject> findWithRecentTestExecution(@Param("since") LocalDateTime since);
    
    /**
     * Find projects by status and environment
     */
    @Query("SELECT p FROM TestProject p WHERE p.status = :status AND p.environment = :environment")
    List<TestProject> findByStatusAndEnvironment(@Param("status") TestProject.ProjectStatus status, @Param("environment") String environment);
    
    /**
     * Find projects with no recent test execution
     */
    @Query("SELECT p FROM TestProject p WHERE p.lastTestExecution IS NULL OR p.lastTestExecution < :since")
    List<TestProject> findWithNoRecentTestExecution(@Param("since") LocalDateTime since);
    
    /**
     * Find projects by owner and status
     */
    @Query("SELECT p FROM TestProject p WHERE p.owner = :owner AND p.status = :status")
    List<TestProject> findByOwnerAndStatus(@Param("owner") String owner, @Param("status") TestProject.ProjectStatus status);
    
    /**
     * Check if project exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Check if project exists by project ID
     */
    boolean existsByProjectId(String projectId);
    
    /**
     * Find projects ordered by creation date
     */
    List<TestProject> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find projects ordered by update date
     */
    List<TestProject> findAllByOrderByUpdatedAtDesc();
    
    /**
     * Find projects ordered by name
     */
    List<TestProject> findAllByOrderByNameAsc();
    
    /**
     * Find projects ordered by artifact count
     */
    List<TestProject> findAllByOrderByArtifactCountDesc();
    
    /**
     * Find projects ordered by session count
     */
    List<TestProject> findAllByOrderBySessionCountDesc();
    
    /**
     * Find projects with pagination
     */
    @Query("SELECT p FROM TestProject p ORDER BY p.createdAt DESC")
    List<TestProject> findAllWithPagination(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Find projects with specific base URL
     */
    List<TestProject> findByBaseUrl(String baseUrl);
}