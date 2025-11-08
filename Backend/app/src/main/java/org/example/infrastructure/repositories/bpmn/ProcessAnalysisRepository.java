package org.example.infrastructure.repositories.bpmn;

import org.example.domain.entities.bpmn.ProcessAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProcessAnalysis entities
 */
@Repository
public interface ProcessAnalysisRepository extends JpaRepository<ProcessAnalysis, UUID> {

    /**
     * Find analyses by process
     */
    List<ProcessAnalysis> findByProcessId(UUID processId);

    /**
     * Find analyses by analysis type
     */
    List<ProcessAnalysis> findByAnalysisType(ProcessAnalysis.AnalysisType analysisType);

    /**
     * Find analyses by status
     */
    List<ProcessAnalysis> findByStatus(ProcessAnalysis.AnalysisStatus status);

    /**
     * Find latest analysis for process
     */
    @Query("SELECT pa FROM ProcessAnalysis pa WHERE pa.process.id = :processId ORDER BY pa.analysisDate DESC LIMIT 1")
    Optional<ProcessAnalysis> findLatestByProcessId(@Param("processId") UUID processId);

    /**
     * Find completed analyses
     */
    @Query("SELECT pa FROM ProcessAnalysis pa WHERE pa.status = 'COMPLETED'")
    List<ProcessAnalysis> findCompletedAnalyses();

    /**
     * Find analyses by date range
     */
    @Query("SELECT pa FROM ProcessAnalysis pa WHERE pa.analysisDate BETWEEN :startDate AND :endDate")
    List<ProcessAnalysis> findByAnalysisDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Count analyses by type
     */
    @Query("SELECT pa.analysisType, COUNT(pa) FROM ProcessAnalysis pa GROUP BY pa.analysisType")
    List<Object[]> countByAnalysisType();

    /**
     * Count analyses by status
     */
    @Query("SELECT pa.status, COUNT(pa) FROM ProcessAnalysis pa GROUP BY pa.status")
    List<Object[]> countByStatus();

    /**
     * Get average processing time by type
     */
    @Query("SELECT pa.analysisType, AVG(pa.processingTimeMs) FROM ProcessAnalysis pa WHERE pa.processingTimeMs IS NOT NULL GROUP BY pa.analysisType")
    List<Object[]> getAverageProcessingTimeByType();

    /**
     * Find analyses with issues
     */
    @Query("SELECT pa FROM ProcessAnalysis pa WHERE pa.issuesFound > 0")
    List<ProcessAnalysis> findAnalysesWithIssues();

    /**
     * Find high-complexity processes
     */
    @Query("SELECT pa FROM ProcessAnalysis pa WHERE pa.complexityScore > 0.7")
    List<ProcessAnalysis> findHighComplexityAnalyses();
}