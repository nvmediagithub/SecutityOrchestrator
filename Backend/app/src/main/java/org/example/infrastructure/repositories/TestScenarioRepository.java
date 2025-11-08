package org.example.infrastructure.repositories;

import org.example.domain.entities.TestScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для тестовых сценариев
 */
@Repository
public interface TestScenarioRepository extends JpaRepository<TestScenario, Long> {
    
    Optional<TestScenario> findByScenarioId(String scenarioId);
    
    List<TestScenario> findByScenarioType(String scenarioType);
    
    List<TestScenario> findByOwaspCategory(String owaspCategory);
    
    List<TestScenario> findByAnalysisId(String analysisId);
    
    List<TestScenario> findByEnvironment(String environment);
    
    List<TestScenario> findByStatus(String status);
    
    List<TestScenario> findByIsSecurityTestTrue();
    
    List<TestScenario> findByIsFunctionalTestTrue();
    
    List<TestScenario> findByIsPerformanceTestTrue();
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.createdAt >= :since ORDER BY ts.createdAt DESC")
    List<TestScenario> findRecentScenarios(@Param("since") java.time.LocalDateTime since);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.owaspCategory = :category AND ts.status = 'ACTIVE'")
    List<TestScenario> findActiveScenariosByOwaspCategory(@Param("category") String category);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.priority >= :minPriority ORDER BY ts.priority DESC")
    List<TestScenario> findHighPriorityScenarios(@Param("minPriority") Integer minPriority);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.totalExecutions > :minExecutions ORDER BY ts.totalExecutions DESC")
    List<TestScenario> findMostExecutedScenarios(@Param("minExecutions") Integer minExecutions);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.successRate >= :minRate ORDER BY ts.successRate DESC")
    List<TestScenario> findMostSuccessfulScenarios(@Param("minRate") Double minRate);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.tags LIKE %:tag%")
    List<TestScenario> findByTag(@Param("tag") String tag);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.name LIKE %:name% OR ts.description LIKE %:name%")
    List<TestScenario> searchByNameOrDescription(@Param("name") String name);
    
    @Query("SELECT ts.owaspCategory, COUNT(ts) FROM TestScenario ts GROUP BY ts.owaspCategory")
    List<Object[]> countScenariosByOwaspCategory();
    
    @Query("SELECT ts.scenarioType, COUNT(ts) FROM TestScenario ts GROUP BY ts.scenarioType")
    List<Object[]> countScenariosByType();
    
    @Query("SELECT AVG(ts.successRate) FROM TestScenario ts WHERE ts.totalExecutions > 0")
    Double getAverageSuccessRate();
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.analysisId IN :analysisIds")
    List<TestScenario> findByAnalysisIds(@Param("analysisIds") List<String> analysisIds);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.targetEndpoints LIKE %:endpoint%")
    List<TestScenario> findByTargetEndpoint(@Param("endpoint") String endpoint);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.attackVectors LIKE %:vector%")
    List<TestScenario> findByAttackVector(@Param("vector") String vector);
    
    @Query("SELECT ts FROM TestScenario ts WHERE ts.isAutomated = true AND ts.status = 'ACTIVE'")
    List<TestScenario> findActiveAutomatedScenarios();
    
    boolean existsByScenarioId(String scenarioId);
    
    void deleteByScenarioId(String scenarioId);
}