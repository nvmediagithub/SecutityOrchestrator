package org.example.features.testdata.infrastructure.adapters;

import org.example.shared.common.ApiResponse;
import org.example.shared.domain.entities.TestSession;
import org.example.shared.domain.dto.test.TestSessionCreateRequest;
import org.example.shared.infrastructure.services.SessionService;
import org.example.features.testdata.infrastructure.repositories.TestSessionRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Adapter implementation of SessionService for the testdata feature module.
 * Provides session management functionality using JPA repository.
 */
@Service
@Transactional
public class SessionServiceAdapter implements SessionService {

    private final TestSessionRepositoryAdapter testSessionRepository;

    public SessionServiceAdapter(TestSessionRepositoryAdapter testSessionRepository) {
        this.testSessionRepository = testSessionRepository;
    }

    @Override
    public ApiResponse<TestSession> createSession(TestSessionCreateRequest request) {
        try {
            TestSession session = new TestSession(request.getName(), request.getDescription(), request.getExecutor());

            // Set additional properties
            if (request.getEnvironment() != null) session.setEnvironment(request.getEnvironment());
            if (request.getTestType() != null) session.setTestType(request.getTestType());
            if (request.getTags() != null) session.getTags().addAll(request.getTags());
            if (request.getTimeoutMinutes() != null) session.setTimeoutMinutes(request.getTimeoutMinutes());
            session.setRetryOnFailure(request.isRetryOnFailure());
            session.setParallelExecution(request.isParallelExecution());

            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session created successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to create session: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TestSession> getSessionById(Long id) {
        try {
            TestSession session = testSessionRepository.findById(id).orElse(null);
            if (session == null) {
                return ApiResponse.error("Session not found");
            }
            return ApiResponse.success(session);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch session: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TestSession> getSessionBySessionId(String sessionId) {
        try {
            TestSession session = testSessionRepository.findBySessionId(sessionId);
            if (session == null) {
                return ApiResponse.error("Session not found");
            }
            return ApiResponse.success(session);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch session: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<TestSession> updateSession(Long id, TestSessionCreateRequest request) {
        try {
            TestSession session = testSessionRepository.findById(id).orElse(null);
            if (session == null) {
                return ApiResponse.error("Session not found");
            }

            session.setName(request.getName());
            session.setDescription(request.getDescription());
            if (request.getExecutor() != null) session.setExecutor(request.getExecutor());
            if (request.getEnvironment() != null) session.setEnvironment(request.getEnvironment());
            if (request.getTestType() != null) session.setTestType(request.getTestType());
            if (request.getTimeoutMinutes() != null) session.setTimeoutMinutes(request.getTimeoutMinutes());
            session.setRetryOnFailure(request.isRetryOnFailure());
            session.setParallelExecution(request.isParallelExecution());

            // Update tags
            if (request.getTags() != null) {
                session.getTags().clear();
                session.getTags().addAll(request.getTags());
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

    @Override
    public ApiResponse<String> deleteSession(Long id) {
        try {
            TestSession session = testSessionRepository.findById(id).orElse(null);
            if (session == null) {
                return ApiResponse.error("Session not found");
            }

            testSessionRepository.deleteById(id);
            return ApiResponse.success("Session deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete session: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<TestSession> startSession(Long id) {
        try {
            TestSession session = testSessionRepository.findById(id).orElse(null);
            if (session == null) {
                return ApiResponse.error("Session not found");
            }

            session.start();
            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session started successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to start session: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<TestSession> stopSession(Long id) {
        try {
            TestSession session = testSessionRepository.findById(id).orElse(null);
            if (session == null) {
                return ApiResponse.error("Session not found");
            }

            if (session.isActive()) {
                session.stop();
            }
            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session stopped successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to stop session: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getAllSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findAllByOrderByCreatedAtDesc();
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getSessionsByProjectId(Long projectId) {
        try {
            // This would need to be implemented with proper project-session relationship
            // For now, return empty list
            return ApiResponse.success(List.of());
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions by project: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getSessionsByStatus(TestSession.SessionStatus status) {
        try {
            List<TestSession> sessions = testSessionRepository.findByStatus(status);
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions by status: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getSessionsByExecutor(String executor) {
        try {
            List<TestSession> sessions = testSessionRepository.findByExecutor(executor);
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch sessions by executor: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getRunningSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findRunningSessions(TestSession.SessionStatus.RUNNING);
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch running sessions: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getCompletedSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findCompletedSessions(TestSession.SessionStatus.COMPLETED);
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch completed sessions: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TestSession>> getActiveSessions() {
        try {
            List<TestSession> sessions = testSessionRepository.findByIsActiveTrue();
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch active sessions: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Long> countActiveSessions() {
        try {
            long count = testSessionRepository.countActiveSessions();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error("Failed to count active sessions: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<TestSession> updateSessionProgress(Long id, int passed, int failed, int skipped, int total) {
        try {
            TestSession session = testSessionRepository.findById(id).orElse(null);
            if (session == null) {
                return ApiResponse.error("Session not found");
            }

            session.updateProgress(passed, failed, skipped, total);
            TestSession saved = testSessionRepository.save(session);
            return ApiResponse.success("Session progress updated", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to update session progress: " + e.getMessage());
        }
    }
}