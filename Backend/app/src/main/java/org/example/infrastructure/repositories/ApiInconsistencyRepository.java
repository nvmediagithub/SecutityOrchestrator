package org.example.infrastructure.repositories;

import org.example.domain.entities.ApiInconsistency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ApiInconsistency entities
 */
@Repository
public interface ApiInconsistencyRepository extends JpaRepository<ApiInconsistency, Long> {
    
    /**
     * Find inconsistencies by specification ID
     */
    List<ApiInconsistency> findBySpecificationId(String specificationId);
    
    /**
     * Find inconsistencies by analysis ID
     */
    List<ApiInconsistency> findByAnalysisId(String analysisId);
    
    /**
     * Find inconsistencies by type
     */
    List<ApiInconsistency> findByType(String type); // naming, version, documentation, structure
    
    /**
     * Find inconsistencies by severity
     */
    List<ApiInconsistency> findBySeverity(String severity); // LOW, MEDIUM, HIGH
    
    /**
     * Find active inconsistencies
     */
    List<ApiInconsistency> findByIsActiveTrue();
    
    /**
     * Find inconsistencies by specification and type
     */
    List<ApiInconsistency> findBySpecificationIdAndType(String specificationId, String type);
    
    /**
     * Find inconsistencies by rule ID
     */
    List<ApiInconsistency> findByRuleIdAndIsActiveTrue(String ruleId);
    
    /**
     * Find recent inconsistencies
     */
    List<ApiInconsistency> findByDetectedAtAfterAndIsActiveTrue(LocalDateTime date);
    
    /**
     * Count inconsistencies by type
     */
    @Query("SELECT i.type, COUNT(i) FROM ApiInconsistency i WHERE i.isActive = true GROUP BY i.type")
    List<Object[]> countInconsistenciesByType();
    
    /**
     * Count inconsistencies by severity
     */
    @Query("SELECT i.severity, COUNT(i) FROM ApiInconsistency i WHERE i.isActive = true GROUP BY i.severity")
    List<Object[]> countInconsistenciesBySeverity();
    
    /**
     * Count inconsistencies by specification
     */
    @Query("SELECT i.specificationId, COUNT(i) FROM ApiInconsistency i WHERE i.isActive = true GROUP BY i.specificationId")
    List<Object[]> countInconsistenciesBySpecification();
    
    /**
     * Find naming inconsistencies
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE i.type = 'naming' AND i.isActive = true ORDER BY i.severity DESC")
    List<ApiInconsistency> findNamingInconsistencies();
    
    /**
     * Find version inconsistencies
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE i.type = 'version' AND i.isActive = true ORDER BY i.severity DESC")
    List<ApiInconsistency> findVersionInconsistencies();
    
    /**
     * Find documentation inconsistencies
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE i.type = 'documentation' AND i.isActive = true ORDER BY i.severity DESC")
    List<ApiInconsistency> findDocumentationInconsistencies();
    
    /**
     * Find structure inconsistencies
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE i.type = 'structure' AND i.isActive = true ORDER BY i.severity DESC")
    List<ApiInconsistency> findStructureInconsistencies();
    
    /**
     * Find high severity inconsistencies
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE i.severity IN ('HIGH', 'MEDIUM') AND i.isActive = true ORDER BY i.severity DESC, i.detectedAt DESC")
    List<ApiInconsistency> findHighSeverityInconsistencies();
    
    /**
     * Get inconsistency statistics
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN i.type = 'naming' THEN 1 ELSE 0 END) as namingCount, " +
           "SUM(CASE WHEN i.type = 'version' THEN 1 ELSE 0 END) as versionCount, " +
           "SUM(CASE WHEN i.type = 'documentation' THEN 1 ELSE 0 END) as documentationCount, " +
           "SUM(CASE WHEN i.type = 'structure' THEN 1 ELSE 0 END) as structureCount, " +
           "SUM(CASE WHEN i.severity = 'HIGH' THEN 1 ELSE 0 END) as highSeverityCount " +
           "FROM ApiInconsistency i WHERE i.isActive = true")
    Object getInconsistencyStatistics();
    
    /**
     * Calculate inconsistency score for specification
     */
    @Query("SELECT " +
           "CASE WHEN COUNT(*) = 0 THEN 100 " +
           "ELSE ROUND(100.0 - (SUM(CASE WHEN i.severity = 'HIGH' THEN 30 WHEN i.severity = 'MEDIUM' THEN 15 WHEN i.severity = 'LOW' THEN 5 ELSE 0 END) * 100.0 / (COUNT(*) * 30)), 2) " +
           "END as score " +
           "FROM ApiInconsistency i WHERE i.specificationId = :specificationId AND i.isActive = true")
    Double getInconsistencyScoreForSpecification(@Param("specificationId") String specificationId);
    
    /**
     * Find inconsistencies by affected elements
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE :element MEMBER OF i.affectedElements AND i.isActive = true")
    List<ApiInconsistency> findByAffectedElementsContaining(@Param("element") String element);
    
    /**
     * Find inconsistencies by multiple criteria
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE " +
           "(:specificationId IS NULL OR i.specificationId = :specificationId) AND " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:severity IS NULL OR i.severity = :severity) AND " +
           "(:detectedAfter IS NULL OR i.detectedAt >= :detectedAfter) AND " +
           "i.isActive = true")
    List<ApiInconsistency> findByCriteria(
        @Param("specificationId") String specificationId,
        @Param("type") String type,
        @Param("severity") String severity,
        @Param("detectedAfter") LocalDateTime detectedAfter
    );
    
    /**
     * Find consistency quality metrics
     */
    @Query("SELECT i.specificationId, " +
           "COUNT(*) as totalInconsistencies, " +
           "SUM(CASE WHEN i.severity = 'HIGH' THEN 1 ELSE 0 END) as highSeverityCount, " +
           "AVG(CASE WHEN i.severity = 'HIGH' THEN 30 WHEN i.severity = 'MEDIUM' THEN 15 WHEN i.severity = 'LOW' THEN 5 ELSE 0 END) as avgSeverityScore " +
           "FROM ApiInconsistency i WHERE i.isActive = true " +
           "GROUP BY i.specificationId")
    List<Object[]> getConsistencyQualityMetrics();
    
    /**
     * Find most common inconsistency patterns
     */
    @Query("SELECT i.description, COUNT(*) as frequency " +
           "FROM ApiInconsistency i WHERE i.isActive = true " +
           "GROUP BY i.description ORDER BY frequency DESC")
    List<Object[]> findMostCommonInconsistencyPatterns();
    
    /**
     * Find inconsistencies by impacted components
     */
    @Query("SELECT i FROM ApiInconsistency i WHERE " +
           "(:component MEMBER OF i.impactedComponents OR :component MEMBER OF i.affectedElements) " +
           "AND i.isActive = true")
    List<ApiInconsistency> findByImpactedComponentsContaining(@Param("component") String component);
    
    /**
     * Get consistency trend analysis
     */
    @Query("SELECT DATE_TRUNC('month', i.detectedAt) as month, " +
           "COUNT(*) as inconsistencyCount, " +
           "COUNT(DISTINCT i.specificationId) as affectedSpecs " +
           "FROM ApiInconsistency i WHERE i.detectedAt >= :startDate AND i.isActive = true " +
           "GROUP BY DATE_TRUNC('month', i.detectedAt) " +
           "ORDER BY month DESC")
    List<Object[]> getConsistencyTrends(@Param("startDate") LocalDateTime startDate);
}