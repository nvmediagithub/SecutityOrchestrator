package org.example.infrastructure.repositories;

import org.example.domain.entities.OpenApiSpec;
import org.example.domain.valueobjects.OpenApiVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for OpenApiSpec entities
 * Provides CRUD operations and custom search methods
 */
@Repository
public interface OpenApiSpecRepository extends JpaRepository<OpenApiSpec, Long> {
    
    /**
     * Find by specification ID
     */
    Optional<OpenApiSpec> findBySpecificationIdValue(String specificationId);
    
    /**
     * Find all active specifications
     */
    List<OpenApiSpec> findByIsActiveTrue();
    
    /**
     * Find by title containing (case-insensitive)
     */
    List<OpenApiSpec> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find by OpenAPI version
     */
    List<OpenApiSpec> findByOpenApiVersion(OpenApiVersion version);
    
    /**
     * Find by file name pattern
     */
    List<OpenApiSpec> findByFileNameContainingIgnoreCase(String fileName);
    
    /**
     * Find specifications created after date
     */
    List<OpenApiSpec> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find specifications created before date
     */
    List<OpenApiSpec> findByCreatedAtBefore(LocalDateTime date);
    
    /**
     * Find specifications within date range
     */
    List<OpenApiSpec> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find by content type
     */
    List<OpenApiSpec> findByContentType(String contentType);
    
    /**
     * Find specifications with specific server URL
     */
    @Query("SELECT o FROM OpenApiSpec o WHERE o.serverUrls LIKE %:url%")
    List<OpenApiSpec> findByServerUrlContaining(@Param("url") String url);
    
    /**
     * Find specifications with path count greater than specified
     */
    @Query("SELECT o FROM OpenApiSpec o WHERE SIZE(o.paths) > :pathCount")
    List<OpenApiSpec> findByPathCountGreaterThan(@Param("pathCount") int pathCount);
    
    /**
     * Find specifications by description keyword
     */
    @Query("SELECT o FROM OpenApiSpec o WHERE LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<OpenApiSpec> findByDescriptionContaining(@Param("keyword") String keyword);
    
    /**
     * Count active specifications
     */
    @Query("SELECT COUNT(o) FROM OpenApiSpec o WHERE o.isActive = true")
    long countActiveSpecifications();
    
    /**
     * Find recently updated specifications
     */
    @Query("SELECT o FROM OpenApiSpec o WHERE o.updatedAt >= :since ORDER BY o.updatedAt DESC")
    List<OpenApiSpec> findRecentlyUpdated(@Param("since") LocalDateTime since);
    
    /**
     * Find specifications by version prefix
     */
    @Query("SELECT o FROM OpenApiSpec o WHERE o.version.major = :major AND o.version.minor = :minor")
    List<OpenApiSpec> findByVersion(@Param("major") int major, @Param("minor") int minor);
    
    /**
     * Find specifications with specific content type pattern
     */
    @Query("SELECT o FROM OpenApiSpec o WHERE o.contentType LIKE :pattern")
    List<OpenApiSpec> findByContentTypePattern(@Param("pattern") String pattern);
    
    /**
     * Check if specification exists by title
     */
    boolean existsByTitle(String title);
    
    /**
     * Check if specification exists by file name
     */
    boolean existsByFileName(String fileName);
    
    /**
     * Find specifications ordered by creation date
     */
    List<OpenApiSpec> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find specifications ordered by update date
     */
    List<OpenApiSpec> findAllByOrderByUpdatedAtDesc();
    
    /**
     * Find specifications with pagination
     */
    @Query("SELECT o FROM OpenApiSpec o ORDER BY o.createdAt DESC")
    List<OpenApiSpec> findAllWithPagination(org.springframework.data.domain.Pageable pageable);
}