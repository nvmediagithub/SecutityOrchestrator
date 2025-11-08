package org.example.infrastructure.repositories;

import org.example.domain.entities.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {
    
    Optional<TestExecution> findByExecutionId(String executionId);
    
    List<TestExecution> findByTestScenario_ScenarioId(String scenarioId);
    
    List<TestExecution> findByStatus(String status);
    
    List<TestExecution> findByEnvironment(String environment);
    
    List<TestExecution> findByInitiatedBy(String initiatedBy);
    
    @Query("SELECT te FROM TestExecution te WHERE te.startedAt >= :since ORDER BY te.startedAt DESC")
    List<TestExecution> findRecentExecutions(@Param("since") java.time.LocalDateTime since);
    
    @Query("SELECT te FROM TestExecution te WHERE te.status = 'PASSED'")
    List<TestExecution> findSuccessfulExecutions();
    
    @Query("SELECT te FROM TestExecution te WHERE te.status IN ('FAILED', 'ERROR')")
    List<TestExecution> findFailedExecutions();
    
    boolean existsByExecutionId(String executionId);
    
    void deleteByExecutionId(String executionId);
}