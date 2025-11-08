package org.example.infrastructure.repositories;

import org.example.domain.entities.TestExecutionSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TestExecutionSession entities
 */
@Repository
public interface TestExecutionSessionRepository extends JpaRepository<TestExecutionSession, Long> {

    /**
     * Find sessions by project ID
     */
    List<TestExecutionSession> findByProjectId(String projectId);

    /**
     * Find sessions by session status
     */
    List<TestExecutionSession> findBySessionStatus(TestExecutionSession.SessionStatus status);

    /**
     * Find sessions by test type
     */
    List<TestExecutionSession> findByTestType(String testType);

    /**
     * Find sessions by target ID
     */
    List<TestExecutionSession> findByTargetId(String targetId);

    /**
     * Find sessions by target type
     */
    List<TestExecutionSession> findByTargetType(String targetType);

    /**
     * Find active sessions
     */
    @Query("SELECT tes FROM TestExecutionSession tes WHERE tes.sessionStatus = 'ACTIVE'")
    List<TestExecutionSession> findActiveSessions();

    /**
     * Find completed sessions
     */
    @Query("SELECT tes FROM TestExecutionSession tes WHERE tes.sessionStatus = 'COMPLETED'")
   List<TestExecutionSession> findCompletedSessions();

    /**
     * Find failed sessions
     */
    @Query("SELECT tes FROM TestExecutionSession tes WHERE tes.sessionStatus = 'FAILED'")
    List<TestExecutionSession> findFailedSessions();

    /**
     * Find sessions started after date
     */
    List<TestExecutionSession> findByStartTimeAfter(LocalDateTime date);

    /**
     * Find sessions by duration range
     */
    @Query("SELECT tes FROM TestExecutionSession tes WHERE tes.durationMinutes BETWEEN :minDuration AND :maxDuration")
    List<TestExecutionSession> findByDurationMinutesBetween(@Param("minDuration") Integer minDuration, @Param("maxDuration") Integer maxDuration);

    /**
     * Find sessions by environment
     */
    List<TestExecutionSession> findByEnvironment(String environment);

    /**
     * Find sessions by user
     */
    List<TestExecutionSession> findByExecutedBy(String executedBy);

    /**
     * Find sessions with findings
     */
    @Query("SELECT tes FROM TestExecutionSession tes WHERE tes.findingCount > 0")
    List<TestExecutionSession> findSessionsWithFindings();

    /**
     * Count sessions by status
     */
    @Query("SELECT COUNT(tes) FROM TestExecutionSession tes WHERE tes.sessionStatus = :status")
    long countBySessionStatus(@Param("status") TestExecutionSession.SessionStatus status);

    /**
     * Find recent sessions
     */
    @Query("SELECT tes FROM TestExecutionSession tes WHERE tes.startTime >= :since ORDER BY tes.startTime DESC")
    List<TestExecutionSession> findRecentSessions(@Param("since") LocalDateTime since);

    /**
     * Find sessions with high severity findings
     */
    @Query("SELECT tes FROM TestExecutionSession tes WHERE tes.highSeverityFindings > 0")
    List<TestExecutionSession> findSessionsWithHighSeverityFindings();

    /**
     * Find sessions by test framework
     */
    List<TestExecutionSession> findByTestFramework(String testFramework);
}