package org.example.infrastructure.repositories.llm;

import org.example.llm.domain.entities.ConsistencyAnalysis;
import org.example.domain.valueobjects.AnalysisId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ConsistencyAnalysis entities
 */
@Repository
public interface ConsistencyAnalysisRepository extends JpaRepository<ConsistencyAnalysis, AnalysisId> {

    /**
     * Find analyses by type
     */
    List<ConsistencyAnalysis> findByAnalysisType(ConsistencyAnalysis.AnalysisType analysisType);

    /**
     * Find analyses by status
     */
    List<ConsistencyAnalysis> findByStatus(ConsistencyAnalysis.ConsistencyStatus status);

    /**
     * Find analyses by OpenAPI spec ID
     */
    List<ConsistencyAnalysis> findByOpenApiSpecId(String openApiSpecId);

    /**
     * Find analyses by BPMN process ID
     */
    List<ConsistencyAnalysis> findByBpmnProcessId(String bpmnProcessId);

    /**
     * Find analyses by scope
     */
    List<ConsistencyAnalysis> findByScope(ConsistencyAnalysis.AnalysisScope scope);

    /**
     * Find analyses with consistency score range
     */
    @Query("SELECT ca FROM ConsistencyAnalysis ca WHERE ca.consistencyScore BETWEEN :minScore AND :maxScore")
    List<ConsistencyAnalysis> findByConsistencyScoreBetween(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    /**
     * Find analyses with high consistency score
     */
    @Query("SELECT ca FROM ConsistencyAnalysis ca WHERE ca.consistencyScore >= :threshold")
    List<ConsistencyAnalysis> findByConsistencyScoreGreaterThan(@Param("threshold") Double threshold);

    /**
     * Find analyses started after date
     */
    List<ConsistencyAnalysis> findByStartedAtAfter(LocalDateTime date);

    /**
     * Find analyses completed after date
     */
    List<ConsistencyAnalysis> findByCompletedAtAfter(LocalDateTime date);

    /**
     * Find completed analyses
     */
    @Query("SELECT ca FROM ConsistencyAnalysis ca WHERE ca.status = 'COMPLETED'")
    List<ConsistencyAnalysis> findCompletedAnalyses();

    /**
     * Find failed analyses
     */
    @Query("SELECT ca FROM ConsistencyAnalysis ca WHERE ca.status = 'FAILED'")
    List<ConsistencyAnalysis> findFailedAnalyses();

    /**
     * Count analyses by status
     */
    @Query("SELECT COUNT(ca) FROM ConsistencyAnalysis ca WHERE ca.status = :status")
    long countByStatus(@Param("status") ConsistencyAnalysis.ConsistencyStatus status);

    /**
     * Find analyses with inconsistencies
     */
    @Query("SELECT ca FROM ConsistencyAnalysis ca WHERE ca.inconsistencies IS NOT EMPTY")
    List<ConsistencyAnalysis> findAnalysesWithInconsistencies();

    /**
     * Find analyses with high inconsistencies count
     */
    @Query("SELECT ca FROM ConsistencyAnalysis ca WHERE SIZE(ca.inconsistencies) >= :threshold")
    List<ConsistencyAnalysis> findAnalysesWithHighInconsistenciesCount(@Param("threshold") Integer threshold);
}
