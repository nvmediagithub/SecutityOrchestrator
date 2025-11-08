package org.example.infrastructure.services;

import org.example.domain.dto.test.ApiResponse;
import org.example.domain.dto.test.TestSessionCreateRequest;
import org.example.domain.entities.TestArtifact;
import org.example.domain.entities.TestProject;
import org.example.domain.entities.TestSession;
import org.example.infrastructure.repositories.TestArtifactRepository;
import org.example.infrastructure.repositories.TestProjectRepository;
import org.example.infrastructure.repositories.TestSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing test sessions
 */
@Service
@Transactional
public class SessionService {
    
    private final TestSessionRepository testSessionRepository;
    private final TestProjectRepository testProjectRepository;
    private final TestArtifactRepository testArtifactRepository;
    
    public SessionService(TestSessionRepository testSessionRepository,
                         TestProjectRepository testProjectRepository,
                         TestArtifactRepository testArtifactRepository) {
        this.testSessionRepository = testSessionRepository;
        this.testProjectRepository = testProjectRepository;
        this.testArtifactRepository = testArtifactRepository;
    }
    
    /**
     * Create a new test session
     */
    public ApiResponse<TestSession> createSession(TestSessionCreateRequest request) {
        try {
            TestSession session = new TestSession(request.getName(), request.getDescription(), request.getExecutor());
            
            // Set additional properties
            if (request.getEnvironment() != null) session.setEnvironment(request.getEnvironment());
            if (request.getTestType() != null) session.setTestType(request.getTestType());
            if (request.getTestConfig() != null) session.setTestConfig(request.getTestConfig());
            if (request.getArtifactIds() != null) session.getArtifactIds().addAll(request.getArtifactIds());
            if (request.getTags() != null) session.getTags().addAll(request.getTags());
            if (request.getTimeoutMinutes() != null) session.setTimeoutMinutes(request.getTimeoutMinutes());
            if (request.getRetryOnFailure() != null) session.setRetryOnFailure(request.getRetryOnFailure());
            if (request.getParallelExecution() != null) session.setParallelExecution(request.getParallelExecution());
            
            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session created successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to create session: " + e.getMessage());
        }
    }
    
    /**
     * Get all sessions
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getAllSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findAllByOrderByCreatedAtDesc();
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions: " + e.getMessage());
        }
    }
    
    /**
     * Get session by ID
     */
    @Transactional(readOnly = true)
    public ApiResponse<TestSession> getSessionById(Long id) {
        try {
            Optional<TestSession> sessionOpt = testSessionRepository.findById(id);
            if (sessionOpt.isEmpty()) {
                return ApiResponse.error("Session not found");
            }
            return ApiResponse.success(sessionOpt.get());
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch session: " + e.getMessage());
        }
    }
    
    /**
     * Get session by sessionId
     */
    @Transactional(readOnly = true)
    public ApiResponse<TestSession> getSessionBySessionId(String sessionId) {
        try {
            Optional<TestSession> sessionOpt = testSessionRepository.findBySessionId(sessionId);
            if (sessionOpt.isEmpty()) {
                return ApiResponse.error("Session not found");
            }
            return ApiResponse.success(sessionOpt.get());
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch session: " + e.getMessage());
        }
    }
    
    /**
     * Update session
     */
    public ApiResponse<TestSession> updateSession(Long id, TestSessionCreateRequest request) {
        try {
            Optional<TestSession> sessionOpt = testSessionRepository.findById(id);
            if (sessionOpt.isEmpty()) {
                return ApiResponse.error("Session not found");
            }
            
            TestSession session = sessionOpt.get();
            session.setName(request.getName());
            session.setDescription(request.getDescription());
            if (request.getExecutor() != null) session.setExecutor(request.getExecutor());
            if (request.getEnvironment() != null) session.setEnvironment(request.getEnvironment());
            if (request.getTestType() != null) session.setTestType(request.getTestType());
            if (request.getTestConfig() != null) session.setTestConfig(request.getTestConfig());
            if (request.getTimeoutMinutes() != null) session.setTimeoutMinutes(request.getTimeoutMinutes());
            if (request.getRetryOnFailure() != null) session.setRetryOnFailure(request.getRetryOnFailure());
            if (request.getParallelExecution() != null) session.setParallelExecution(request.getParallelExecution());
            
            // Update artifact IDs
            if (request.getArtifactIds() != null) {
                session.getArtifactIds().clear();
                session.getArtifactIds().addAll(request.getArtifactIds());
            }
            
            // Update tags
            if (request.getTags() != null) {
                session.getTags().clear();
                session.getTags().addAll(request.getTags());
            }
            
            TestSession updated = testSessionRepository.save(session);
            return ApiResponse.success("Session updated successfully", updated);
        } catch (Exception e) {
            return ApiResponse.error("Failed to update session: " + e.getMessage());
        }
    }
    
    /**
     * Delete session
     */
    public ApiResponse<String> deleteSession(Long id) {
        try {
            Optional<TestSession> sessionOpt = testSessionRepository.findById(id);
            if (sessionOpt.isEmpty()) {
                return ApiResponse.error("Session not found");
            }
            
            testSessionRepository.deleteById(id);
            return ApiResponse.success("Session deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete session: " + e.getMessage());
        }
    }
    
    /**
     * Start session
     */
    public ApiResponse<TestSession> startSession(Long id) {
        try {
            Optional<TestSession> sessionOpt = testSessionRepository.findById(id);
            if (sessionOpt.isEmpty()) {
                return ApiResponse.error("Session not found");
            }
            
            TestSession session = sessionOpt.get();
            session.start();
            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session started successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to start session: " + e.getMessage());
        }
    }
    
    /**
     * Stop session
     */
    public ApiResponse<TestSession> stopSession(Long id) {
        try {
            Optional<TestSession> sessionOpt = testSessionRepository.findById(id);
            if (sessionOpt.isEmpty()) {
                return ApiResponse.error("Session not found");
            }
            
            TestSession session = sessionOpt.get();
            if (session.isRunning()) {
                session.complete();
            }
            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session stopped successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to stop session: " + e.getMessage());
        }
    }
    
    /**
     * Get sessions by project ID
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getSessionsByProjectId(Long projectId) {
        try {
            List<TestSession> sessions = testSessionRepository.findByTestTypeAndEnvironment("PROJECT_TESTING", projectId.toString());
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions by project: " + e.getMessage());
        }
    }
    
    /**
     * Get sessions by status
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getSessionsByStatus(TestSession.SessionStatus status) {
        try {
            List<TestSession> sessions = testSessionRepository.findByStatus(status);
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions by status: " + e.getMessage());
        }
    }
    
    /**
     * Get sessions by executor
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getSessionsByExecutor(String executor) {
        try {
            List<TestSession> sessions = testSessionRepository.findByExecutor(executor);
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions by executor: " + e.getMessage());
        }
    }
    
    /**
     * Get running sessions
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getRunningSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findRunningSessions();
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch running sessions: " + e.getMessage());
        }
    }
    
    /**
     * Get completed sessions
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getCompletedSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findCompletedSessions();
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch completed sessions: " + e.getMessage());
        }
    }
    
    /**
     * Get active sessions
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getActiveSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findByIsActiveTrue();
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch active sessions: " + e.getMessage());
        }
    }
    
    /**
     * Count active sessions
     */
    @Transactional(readOnly = true)
    public ApiResponse<Long> countActiveSessions() {
        try {
            long count = testSessionRepository.countActiveSessions();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error("Failed to count active sessions: " + e.getMessage());
        }
    }
    
    /**
     * Update session progress
     */
    public ApiResponse<TestSession> updateSessionProgress(Long id, int passed, int failed, int skipped, int total) {
        try {
            Optional<TestSession> sessionOpt = testSessionRepository.findById(id);
            if (sessionOpt.isEmpty()) {
                return ApiResponse.error("Session not found");
            }
            
            TestSession session = sessionOpt.get();
            session.updateProgress(passed, failed, skipped, total);
            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session progress updated", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to update session progress: " + e.getMessage());
        }
    }
}