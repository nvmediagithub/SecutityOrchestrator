package org.example.features.testdata.infrastructure.repositories;

import org.example.shared.domain.entities.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for TestSession entities in the testdata feature module.
 * Provides database operations for test session management.
 */
@Repository
public interface TestSessionRepositoryAdapter extends JpaRepository<TestSession, Long> {

    /**
     * Find a test session by its unique session ID
     */
    TestSession findBySessionId(String sessionId);

    /**
     * Get all sessions ordered by creation date descending
     */
    List<TestSession> findAllByOrderByCreatedAtDesc();

    /**
     * Find sessions by status
     */
    List<TestSession> findByStatus(TestSession.SessionStatus status);

    /**
     * Find sessions by executor
     */
    List<TestSession> findByExecutor(String executor);

    /**
     * Find active sessions
     */
    List<TestSession> findByIsActiveTrue();

    /**
     * Find running sessions
     */
    @Query("SELECT s FROM TestSession s WHERE s.status = :status")
    List<TestSession> findRunningSessions(@Param("status") TestSession.SessionStatus status);

    /**
     * Find completed sessions
     */
    @Query("SELECT s FROM TestSession s WHERE s.status = :status")
    List<TestSession> findCompletedSessions(@Param("status") TestSession.SessionStatus status);

    /**
     * Count active sessions
     */
    @Query("SELECT COUNT(s) FROM TestSession s WHERE s.isActive = true")
    long countActiveSessions();
}