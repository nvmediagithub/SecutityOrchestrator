package org.example.infrastructure.repositories;

import org.example.domain.entities.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for TestSession entities
 * Provides CRUD operations and custom search methods
 */
@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
    
    /**
     * Find by session ID
     */
    Optional<TestSession> findBySessionId(String sessionId);
    
    /**
     * Find by name containing (case-insensitive)
     */
    List<TestSession> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find by executor
     */
    List<TestSession> findByExecutor(String executor);
    
    /**
     * Find by environment
     */
    List<TestSession> findByEnvironment(String environment);
    
    /**
     * Find by test type
     */
    List<TestSession> findByTestType(String testType);
    
    /**
     * Find by status
     */
    List<TestSession> findByStatus(TestSession.SessionStatus status);
    
    /**
     * Find by artifact ID
     */
    @Query("SELECT s FROM TestSession s WHERE :artifactId MEMBER OF s.artifactIds")
    List<TestSession> findByArtifactId(@Param("artifactId") String artifactId);
    
    /**
     * Find all active sessions
     */
    List<TestSession> findByIsActiveTrue();
    
    /**
     * Find running sessions
     */
    @Query("SELECT s FROM TestSession s WHERE s.status = 'RUNNING'")
    List<TestSession> findRunningSessions();
    
    /**
     * Find completed sessions
     */
    @Query("SELECT s FROM TestSession s WHERE s.status = 'COMPLETED'")
    List<TestSession> findCompletedSessions();
    
    /**
     * Find failed sessions
     */
    @Query("SELECT s FROM TestSession s WHERE s.status = 'FAILED'")
    List<TestSession> findFailedSessions();
    
    /**
     * Find sessions with specific timeout
     */
    List<TestSession> findByTimeoutMinutes(Integer timeoutMinutes);
    
    /**
     * Find sessions with retry on failure enabled
     */
    List<TestSession> findByRetryOnFailureTrue();
    
    /**
     * Find sessions with parallel execution enabled
     */
    List<TestSession> findByParallelExecutionTrue();
    
    /**
     * Find sessions created after date
     */
    List<TestSession> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find sessions created before date
     */
    List<TestSession> findByCreatedAtBefore(LocalDateTime date);
    
    /**
     * Find sessions within date range
     */
    List<TestSession> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find sessions started after date
     */
    List<TestSession> findByStartTimeAfter(LocalDateTime date);
    
    /**
     * Find sessions ended after date
     */
    List<TestSession> findByEndTimeAfter(LocalDateTime date);
    
    /**
     * Find sessions by tag
     */
    @Query("SELECT s FROM TestSession s WHERE :tag MEMBER OF s.tags")
    List<TestSession> findByTag(@Param("tag") String tag);
    
    /**
     * Find sessions by multiple tags
     */
    @Query("SELECT s FROM TestSession s WHERE s.tags LIKE %:tag%")
    List<TestSession> findByTagsContaining(@Param("tag") String tag);
    
    /**
     * Find sessions with progress percentage greater than
     */
    @Query("SELECT s FROM TestSession s WHERE s.progressPercentage > :percentage")
    List<TestSession> findByProgressPercentageGreaterThan(@Param("percentage") int percentage);
    
    /**
     * Find sessions with total tests greater than
     */
    @Query("SELECT s FROM TestSession s WHERE s.testsTotal > :total")
    List<TestSession> findByTestsTotalGreaterThan(@Param("total") int total);
    
    /**
     * Find sessions with passed tests count
     */
    @Query("SELECT s FROM TestSession s WHERE s.testsPassed > :passed")
    List<TestSession> findByTestsPassedGreaterThan(@Param("passed") int passed);
    
    /**
     * Find sessions with failed tests count
     */
    @Query("SELECT s FROM TestSession s WHERE s.testsFailed > :failed")
    List<TestSession> findByTestsFailedGreaterThan(@Param("failed") int failed);
    
    /**
     * Find sessions with execution duration greater than
     */
    @Query("SELECT s FROM TestSession s WHERE s.executionDurationMs > :duration")
    List<TestSession> findByExecutionDurationGreaterThan(@Param("duration") Long duration);
    
    /**
     * Find sessions by description keyword
     */
    @Query("SELECT s FROM TestSession s WHERE LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TestSession> findByDescriptionContaining(@Param("keyword") String keyword);
    
    /**
     * Count active sessions
     */
    @Query("SELECT COUNT(s) FROM TestSession s WHERE s.isActive = true")
    long countActiveSessions();
    
    /**
     * Count sessions by status
     */
    @Query("SELECT COUNT(s) FROM TestSession s WHERE s.status = :status")
    long countByStatus(@Param("status") TestSession.SessionStatus status);
    
    /**
     * Count sessions by executor
     */
    @Query("SELECT COUNT(s) FROM TestSession s WHERE s.executor = :executor")
    long countByExecutor(@Param("executor") String executor);
    
    /**
     * Find recently created sessions
     */
    @Query("SELECT s FROM TestSession s WHERE s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<TestSession> findRecentlyCreated(@Param("since") LocalDateTime since);
    
    /**
     * Find recently updated sessions
     */
    @Query("SELECT s FROM TestSession s WHERE s.updatedAt >= :since ORDER BY s.updatedAt DESC")
    List<TestSession> findRecentlyUpdated(@Param("since") LocalDateTime since);
    
    /**
     * Find sessions by executor and status
     */
    @Query("SELECT s FROM TestSession s WHERE s.executor = :executor AND s.status = :status")
    List<TestSession> findByExecutorAndStatus(@Param("executor") String executor, @Param("status") TestSession.SessionStatus status);
    
    /**
     * Find sessions by environment and status
     */
    @Query("SELECT s FROM TestSession s WHERE s.environment = :environment AND s.status = :status")
    List<TestSession> findByEnvironmentAndStatus(@Param("environment") String environment, @Param("status") TestSession.SessionStatus status);
    
    /**
     * Find sessions with no end time (still running)
     */
    @Query("SELECT s FROM TestSession s WHERE s.endTime IS NULL")
    List<TestSession> findActiveRunningSessions();
    
    /**
     * Find sessions with execution duration between
     */
    @Query("SELECT s FROM TestSession s WHERE s.executionDurationMs BETWEEN :minDuration AND :maxDuration")
    List<TestSession> findByExecutionDurationBetween(@Param("minDuration") Long minDuration, @Param("maxDuration") Long maxDuration);
    
    /**
     * Check if session exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Check if session exists by session ID
     */
    boolean existsBySessionId(String sessionId);
    
    /**
     * Find sessions ordered by creation date
     */
    List<TestSession> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find sessions ordered by update date
     */
    List<TestSession> findAllByOrderByUpdatedAtDesc();
    
    /**
     * Find sessions ordered by start time
     */
    List<TestSession> findAllByOrderByStartTimeDesc();
    
    /**
     * Find sessions ordered by progress percentage
     */
    List<TestSession> findAllByOrderByProgressPercentageDesc();
    
    /**
     * Find sessions ordered by execution duration
     */
    List<TestSession> findAllByOrderByExecutionDurationMsDesc();
    
    /**
     * Find sessions with pagination
     */
    @Query("SELECT s FROM TestSession s ORDER BY s.createdAt DESC")
    List<TestSession> findAllWithPagination(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Find sessions by test type and environment
     */
    @Query("SELECT s FROM TestSession s WHERE s.testType = :testType AND s.environment = :environment")
    List<TestSession> findByTestTypeAndEnvironment(@Param("testType") String testType, @Param("environment") String environment);
}