package org.example.shared.infrastructure.services;

import org.example.shared.common.ApiResponse;
import org.example.shared.domain.entities.TestSession;

import java.util.List;

/**
 * Service interface for TestSession operations
 */
public interface SessionService {

    /**
     * Create a new test session
     */
    ApiResponse<TestSession> createSession(TestSessionCreateRequest request);

    /**
     * Get session by ID
     */
    ApiResponse<TestSession> getSessionById(Long id);

    /**
     * Get session by session ID
     */
    ApiResponse<TestSession> getSessionBySessionId(String sessionId);

    /**
     * Update an existing session
     */
    ApiResponse<TestSession> updateSession(Long id, TestSessionCreateRequest request);

    /**
     * Delete a session
     */
    ApiResponse<String> deleteSession(Long id);

    /**
     * Start a session
     */
    ApiResponse<TestSession> startSession(Long id);

    /**
     * Stop a session
     */
    ApiResponse<TestSession> stopSession(Long id);

    /**
     * Get all sessions
     */
    ApiResponse<List<TestSession>> getAllSessions();

    /**
     * Get sessions by project ID
     */
    ApiResponse<List<TestSession>> getSessionsByProjectId(Long projectId);

    /**
     * Get sessions by status
     */
    ApiResponse<List<TestSession>> getSessionsByStatus(TestSession.SessionStatus status);

    /**
     * Get sessions by executor
     */
    ApiResponse<List<TestSession>> getSessionsByExecutor(String executor);

    /**
     * Get running sessions
     */
    ApiResponse<List<TestSession>> getRunningSessions();

    /**
     * Get completed sessions
     */
    ApiResponse<List<TestSession>> getCompletedSessions();

    /**
     * Get active sessions
     */
    ApiResponse<List<TestSession>> getActiveSessions();

    /**
     * Count active sessions
     */
    ApiResponse<Long> countActiveSessions();

    /**
     * Update session progress
     */
    ApiResponse<TestSession> updateSessionProgress(Long id, int passed, int failed, int skipped, int total);

    /**
     * Request DTO for session creation/update
     */
    class TestSessionCreateRequest {
        private String name;
        private String description;
        private String executor;

        public TestSessionCreateRequest() {}

        public TestSessionCreateRequest(String name, String description, String executor) {
            this.name = name;
            this.description = description;
            this.executor = executor;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getExecutor() { return executor; }
        public void setExecutor(String executor) { this.executor = executor; }
    }
}