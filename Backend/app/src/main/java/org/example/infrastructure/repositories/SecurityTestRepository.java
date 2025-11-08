package org.example.infrastructure.repositories;

import org.example.domain.entities.SecurityTest;
import org.example.domain.valueobjects.OwaspTestCategory;
import org.example.domain.valueobjects.SeverityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for SecurityTest entities
 */
@Repository
public interface SecurityTestRepository extends JpaRepository<SecurityTest, Long> {

    /**
     * Find tests by OWASP category
     */
    List<SecurityTest> findByOwaspCategory(OwaspTestCategory owaspCategory);

    /**
     * Find tests by severity level
     */
    List<SecurityTest> findBySeverityLevel(SeverityLevel severityLevel);

    /**
     * Find tests by status
     */
    List<SecurityTest> findByStatus(String status);

    /**
     * Find tests by test type
     */
    List<SecurityTest> findByTestType(String testType);

    /**
     * Find tests by target OpenAPI ID
     */
    List<SecurityTest> findByTargetOpenApiId(String targetOpenApiId);

    /**
     * Find tests by target BPMN ID
     */
    List<SecurityTest> findByTargetBpmnId(String targetBpmnId);

    /**
     * Find tests by vulnerability type
     */
    List<SecurityTest> findByVulnerabilityType(String vulnerabilityType);

    /**
     * Find critical tests
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.isCritical = true")
    List<SecurityTest> findCriticalTests();

    /**
     * Find automated tests
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.isAutomated = true")
    List<SecurityTest> findAutomatedTests();

    /**
     * Find tests by priority range
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.testPriority >= :minPriority ORDER BY st.testPriority DESC")
    List<SecurityTest> findByMinPriority(@Param("minPriority") Integer minPriority);

    /**
     * Find tests created by user
     */
    List<SecurityTest> findByCreatedBy(String createdBy);

    /**
     * Find tests with manual review required
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.requiresManualReview = true")
    List<SecurityTest> findRequiringManualReview();

    /**
     * Find tests by tag
     */
    @Query("SELECT st FROM SecurityTest st WHERE :tag MEMBER OF st.tags")
    List<SecurityTest> findByTag(@Param("tag") String tag);

    /**
     * Find tests by target endpoint
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.targetEndpoint LIKE %:endpoint%")
    List<SecurityTest> findByTargetEndpointContaining(@Param("endpoint") String endpoint);

    /**
     * Find recent tests
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.createdAt >= :since ORDER BY st.createdAt DESC")
    List<SecurityTest> findRecentTests(@Param("since") LocalDateTime since);

    /**
     * Find tests by compliance standard
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.complianceStandard LIKE %:standard%")
    List<SecurityTest> findByComplianceStandardContaining(@Param("standard") String standard);

    /**
     * Count tests by OWASP category
     */
    @Query("SELECT COUNT(st) FROM SecurityTest st WHERE st.owaspCategory = :category")
    long countByOwaspCategory(@Param("category") OwaspTestCategory category);

    /**
     * Count tests by severity level
     */
    @Query("SELECT COUNT(st) FROM SecurityTest st WHERE st.severityLevel = :severity")
    long countBySeverityLevel(@Param("severity") SeverityLevel severity);

    /**
     * Find tests with findings
     */
    @Query("SELECT st FROM SecurityTest st WHERE st.findings IS NOT EMPTY")
    List<SecurityTest> findTestsWithFindings();

    /**
     * Find tests by HTTP method
     */
    List<SecurityTest> findByHttpMethod(String httpMethod);
}