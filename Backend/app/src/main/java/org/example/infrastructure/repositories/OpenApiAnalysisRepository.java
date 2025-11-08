package org.example.infrastructure.repositories;

import org.example.domain.entities.OpenApiAnalysis;
import org.example.domain.valueobjects.OpenApiAnalysisId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OpenApiAnalysis entities
 */
@Repository
public interface OpenApiAnalysisRepository extends JpaRepository<OpenApiAnalysis, Long> {
    
    /**
     * Find analysis by analysis ID
     */
    Optional<OpenApiAnalysis> findByAnalysisId(OpenApiAnalysisId analysisId);
    
    /**
     * Find all analyses by specification ID
     */
    List<OpenApiAnalysis> findBySpecificationId(String specificationId);
    
    /**
     * Find all active analyses
     */
    List<OpenApiAnalysis> findByIsActiveTrue();
    
    /**
     * Find analyses by status
     */
    List<OpenApiAnalysis> findByStatus(org.example.domain.valueobjects.AnalysisStatus status);
    
    /**
     * Find analyses created after a specific date
     */
    List<OpenApiAnalysis> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find analyses by specification title containing pattern
     */
    List<OpenApiAnalysis> findBySpecificationTitleContainingIgnoreCase(String title);
    
    /**
     * Find analyses with issues of specific severity
     */
    @Query("SELECT a FROM OpenApiAnalysis a WHERE a.criticalIssues > 0")
    List<OpenApiAnalysis> findAnalysesWithCriticalIssues();
    
    /**
     * Find analyses with high issue count
     */
    @Query("SELECT a FROM OpenApiAnalysis a WHERE a.totalIssues > :threshold")
    List<OpenApiAnalysis> findAnalysesWithHighIssueCount(@Param("threshold") int threshold);
    
    /**
     * Count analyses by status
     */
    @Query("SELECT a.status, COUNT(a) FROM OpenApiAnalysis a GROUP BY a.status")
    List<Object[]> countAnalysesByStatus();
    
    /**
     * Find recently completed analyses
     */
    @Query("SELECT a FROM OpenApiAnalysis a WHERE a.status = 'COMPLETED' AND a.completedAt > :since ORDER BY a.completedAt DESC")
    List<OpenApiAnalysis> findRecentlyCompletedAnalyses(@Param("since") LocalDateTime since);
}