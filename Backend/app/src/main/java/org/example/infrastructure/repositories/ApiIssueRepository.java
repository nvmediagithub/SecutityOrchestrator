package org.example.infrastructure.repositories;

import org.example.domain.entities.ApiIssue;
import org.example.domain.valueobjects.SeverityLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ApiIssue entities
 */
@Repository
public interface ApiIssueRepository extends JpaRepository<ApiIssue, Long> {
    
    /**
     * Find issues by specification ID
     */
    List<ApiIssue> findBySpecificationId(String specificationId);
    
    /**
     * Find issues by analysis ID
     */
    List<ApiIssue> findByAnalysisId(String analysisId);
    
    /**
     * Find issues by severity
     */
    List<ApiIssue> findBySeverity(SeverityLevel severity);
    
    /**
     * Find issues by category
     */
    List<ApiIssue> findByCategory(String category);
    
    /**
     * Find issues by status
     */
    List<ApiIssue> findByStatus(String status);
    
    /**
     * Find open issues
     */
    List<ApiIssue> findByStatusAndIsActiveTrue(String status);
    
    /**
     * Find critical issues
     */
    List<ApiIssue> findBySeverityAndIsActiveTrue(SeverityLevel severity);
    
    /**
     * Find issues by specification ID and status
     */
    List<ApiIssue> findBySpecificationIdAndStatus(String specificationId, String status);
    
    /**
     * Find issues by confidence threshold
     */
    List<ApiIssue> findByConfidenceGreaterThanEqualAndIsActiveTrue(Double minConfidence);
    
    /**
     * Find recent issues
     */
    List<ApiIssue> findByCreatedAtAfterAndIsActiveTrue(LocalDateTime date);
    
    /**
     * Find issues with specific rule ID
     */
    List<ApiIssue> findByRuleIdAndIsActiveTrue(String ruleId);
    
    /**
     * Count issues by severity
     */
    @Query("SELECT i.severity, COUNT(i) FROM ApiIssue i WHERE i.isActive = true GROUP BY i.severity")
    List<Object[]> countIssuesBySeverity();
    
    /**
     * Count issues by category
     */
    @Query("SELECT i.category, COUNT(i) FROM ApiIssue i WHERE i.isActive = true GROUP BY i.category")
    List<Object[]> countIssuesByCategory();
    
    /**
     * Count open issues by specification
     */
    @Query("SELECT i.specificationId, COUNT(i) FROM ApiIssue i WHERE i.status = 'OPEN' AND i.isActive = true GROUP BY i.specificationId")
    List<Object[]> countOpenIssuesBySpecification();
    
    /**
     * Find high confidence issues
     */
    @Query("SELECT i FROM ApiIssue i WHERE i.confidence >= 0.8 AND i.isActive = true ORDER BY i.severity DESC, i.createdAt DESC")
    List<ApiIssue> findHighConfidenceIssues();
    
    /**
     * Find security issues
     */
    @Query("SELECT i FROM ApiIssue i WHERE i.category = 'security' AND i.isActive = true ORDER BY i.severity DESC")
    List<ApiIssue> findSecurityIssues();
    
    /**
     * Find validation issues
     */
    @Query("SELECT i FROM ApiIssue i WHERE i.category = 'validation' AND i.isActive = true ORDER BY i.severity DESC")
    List<ApiIssue> findValidationIssues();
    
    /**
     * Find consistency issues
     */
    @Query("SELECT i FROM ApiIssue i WHERE i.category = 'consistency' AND i.isActive = true ORDER BY i.severity DESC")
    List<ApiIssue> findConsistencyIssues();
    
    /**
     * Find issues by location
     */
    List<ApiIssue> findByLocationAndIsActiveTrue(String location);
    
    /**
     * Find duplicate issues (same specificationId, category, title)
     */
    @Query("SELECT i1 FROM ApiIssue i1 WHERE i1.id IN (" +
           "SELECT MIN(i2.id) FROM ApiIssue i2 WHERE i2.isActive = true GROUP BY i2.specificationId, i2.category, i2.title)")
    List<ApiIssue> findDuplicateIssues();
    
    /**
     * Find issues with recommendations
     */
    List<ApiIssue> findByRecommendationIsNotNullAndIsActiveTrue();
    
    /**
     * Find unresolved critical issues
     */
    @Query("SELECT i FROM ApiIssue i WHERE i.severity = 'CRITICAL' AND i.status != 'RESOLVED' AND i.isActive = true")
    List<ApiIssue> findUnresolvedCriticalIssues();
    
    /**
     * Get issue statistics
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN i.status = 'OPEN' THEN 1 ELSE 0 END) as openCount, " +
           "SUM(CASE WHEN i.severity = 'CRITICAL' AND i.status != 'RESOLVED' THEN 1 ELSE 0 END) as criticalOpen, " +
           "SUM(CASE WHEN i.severity = 'HIGH' AND i.status != 'RESOLVED' THEN 1 ELSE 0 END) as highOpen " +
           "FROM ApiIssue i WHERE i.isActive = true")
    Object getIssueStatistics();
    
    /**
     * Find issues by multiple criteria with pagination
     */
    @Query("SELECT i FROM ApiIssue i WHERE " +
           "(:specificationId IS NULL OR i.specificationId = :specificationId) AND " +
           "(:severity IS NULL OR i.severity = :severity) AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:minConfidence IS NULL OR i.confidence >= :minConfidence) AND " +
           "i.isActive = true")
    Page<ApiIssue> findByCriteria(
        @Param("specificationId") String specificationId,
        @Param("severity") SeverityLevel severity,
        @Param("category") String category,
        @Param("status") String status,
        @Param("minConfidence") Double minConfidence,
        Pageable pageable
    );
}