package org.example.infrastructure.repositories;

import org.example.domain.entities.ApiSecurityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ApiSecurityCheck entities
 */
@Repository
public interface ApiSecurityCheckRepository extends JpaRepository<ApiSecurityCheck, Long> {
    
    /**
     * Find checks by specification ID
     */
    List<ApiSecurityCheck> findBySpecificationId(String specificationId);
    
    /**
     * Find checks by analysis ID
     */
    List<ApiSecurityCheck> findByAnalysisId(String analysisId);
    
    /**
     * Find checks by status
     */
    List<ApiSecurityCheck> findByStatus(String status); // PASSED, FAILED, WARNING
    
    /**
     * Find failed security checks
     */
    List<ApiSecurityCheck> findByStatusAndIsActiveTrue(String status);
    
    /**
     * Find checks by severity
     */
    List<ApiSecurityCheck> findBySeverity(String severity);
    
    /**
     * Find checks by category
     */
    List<ApiSecurityCheck> findByCategory(String category);
    
    /**
     * Find failed checks by specification
     */
    List<ApiSecurityCheck> findBySpecificationIdAndStatus(String specificationId, String status);
    
    /**
     * Find checks by rule ID
     */
    List<ApiSecurityCheck> findByRuleIdAndIsActiveTrue(String ruleId);
    
    /**
     * Find recent security checks
     */
    List<ApiSecurityCheck> findByCheckedAtAfterAndIsActiveTrue(LocalDateTime date);
    
    /**
     * Count checks by status
     */
    @Query("SELECT c.status, COUNT(c) FROM ApiSecurityCheck c WHERE c.isActive = true GROUP BY c.status")
    List<Object[]> countChecksByStatus();
    
    /**
     * Count checks by category
     */
    @Query("SELECT c.category, COUNT(c) FROM ApiSecurityCheck c WHERE c.isActive = true GROUP BY c.category")
    List<Object[]> countChecksByCategory();
    
    /**
     * Count failed checks by specification
     */
    @Query("SELECT c.specificationId, COUNT(c) FROM ApiSecurityCheck c WHERE c.status = 'FAILED' AND c.isActive = true GROUP BY c.specificationId")
    List<Object[]> countFailedChecksBySpecification();
    
    /**
     * Find authentication security checks
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE c.category = 'authentication' AND c.isActive = true ORDER BY c.severity DESC")
    List<ApiSecurityCheck> findAuthenticationChecks();
    
    /**
     * Find encryption security checks
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE c.category = 'encryption' AND c.isActive = true ORDER BY c.severity DESC")
    List<ApiSecurityCheck> findEncryptionChecks();
    
    /**
     * Find access control security checks
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE c.category = 'access_control' AND c.isActive = true ORDER BY c.severity DESC")
    List<ApiSecurityCheck> findAccessControlChecks();
    
    /**
     * Find protection security checks
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE c.category = 'protection' AND c.isActive = true ORDER BY c.severity DESC")
    List<ApiSecurityCheck> findProtectionChecks();
    
    /**
     * Find validation security checks
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE c.category = 'validation' AND c.isActive = true ORDER BY c.severity DESC")
    List<ApiSecurityCheck> findValidationChecks();
    
    /**
     * Find critical security issues
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE c.severity IN ('HIGH', 'CRITICAL') AND c.status = 'FAILED' AND c.isActive = true")
    List<ApiSecurityCheck> findCriticalSecurityIssues();
    
    /**
     * Get security check statistics
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN c.status = 'PASSED' THEN 1 ELSE 0 END) as passedCount, " +
           "SUM(CASE WHEN c.status = 'FAILED' THEN 1 ELSE 0 END) as failedCount, " +
           "SUM(CASE WHEN c.status = 'WARNING' THEN 1 ELSE 0 END) as warningCount, " +
           "SUM(CASE WHEN c.severity IN ('HIGH', 'CRITICAL') AND c.status = 'FAILED' THEN 1 ELSE 0 END) as criticalFailed " +
           "FROM ApiSecurityCheck c WHERE c.isActive = true")
    Object getSecurityCheckStatistics();
    
    /**
     * Calculate security score for specification
     */
    @Query("SELECT " +
           "CASE WHEN COUNT(*) = 0 THEN 0 " +
           "ELSE ROUND((SUM(CASE WHEN c.status = 'PASSED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) " +
           "END as score " +
           "FROM ApiSecurityCheck c WHERE c.specificationId = :specificationId AND c.isActive = true")
    Double getSecurityScoreForSpecification(@Param("specificationId") String specificationId);
    
    /**
     * Find checks by multiple criteria
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE " +
           "(:specificationId IS NULL OR c.specificationId = :specificationId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:severity IS NULL OR c.severity = :severity) AND " +
           "c.isActive = true")
    List<ApiSecurityCheck> findByCriteria(
        @Param("specificationId") String specificationId,
        @Param("status") String status,
        @Param("category") String category,
        @Param("severity") String severity
    );
    
    /**
     * Find failed checks that need attention
     */
    @Query("SELECT c FROM ApiSecurityCheck c WHERE c.status = 'FAILED' AND c.severity IN ('HIGH', 'CRITICAL') AND c.isActive = true ORDER BY c.checkedAt DESC")
    List<ApiSecurityCheck> findFailedHighSeverityChecks();
    
    /**
     * Find compliance status
     */
    @Query("SELECT c.specificationId, " +
           "CASE WHEN SUM(CASE WHEN c.status = 'FAILED' AND c.severity IN ('HIGH', 'CRITICAL') THEN 1 ELSE 0 END) = 0 " +
           "THEN 'COMPLIANT' " +
           "ELSE 'NON_COMPLIANT' END as complianceStatus " +
           "FROM ApiSecurityCheck c WHERE c.isActive = true " +
           "GROUP BY c.specificationId")
    List<Object[]> getComplianceStatusBySpecification();
}