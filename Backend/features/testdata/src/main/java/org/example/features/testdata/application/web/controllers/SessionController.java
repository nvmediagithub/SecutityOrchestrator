package org.example.features.testdata.application.web.controllers;

import org.example.shared.common.ApiResponse;
import org.example.shared.domain.entities.TestSession;
import org.example.shared.domain.dto.test.TestSessionCreateRequest;
import org.example.shared.infrastructure.services.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing test sessions
 * Provides CRUD operations and session lifecycle management
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // ==================== SESSION CRUD OPERATIONS ====================

    /**
     * Create a new test session
     * POST /api/sessions
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TestSession>> createSession(@Valid @RequestBody TestSessionCreateRequest request) {
        try {
            ApiResponse<TestSession> response = sessionService.createSession(request);
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create session: " + e.getMessage()));
        }
    }

    /**
     * Get all sessions
     * GET /api/sessions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestSession>>> getAllSessions(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            ApiResponse<List<TestSession>> response = sessionService.getAllSessions();
            if (response.isSuccess()) {
                // Filter inactive sessions if requested
                if (!includeInactive) {
                    List<TestSession> activeSessions = response.getData().stream()
                        .filter(TestSession::isActive)
                        .collect(java.util.stream.Collectors.toList());
                    response.setData(activeSessions);
                }
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch sessions: " + e.getMessage()));
        }
    }

    /**
     * Get session by ID
     * GET /api/sessions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSession>> getSessionById(@PathVariable Long id) {
        try {
            ApiResponse<TestSession> response = sessionService.getSessionById(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                if (response.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch session: " + e.getMessage()));
        }
    }

    /**
     * Get session by sessionId
     * GET /api/sessions/sessionId/{sessionId}
     */
    @GetMapping("/sessionId/{sessionId}")
    public ResponseEntity<ApiResponse<TestSession>> getSessionBySessionId(@PathVariable String sessionId) {
        try {
            ApiResponse<TestSession> response = sessionService.getSessionBySessionId(sessionId);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                if (response.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch session: " + e.getMessage()));
        }
    }

    /**
     * Update session
     * PUT /api/sessions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSession>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody TestSessionCreateRequest request) {
        try {
            ApiResponse<TestSession> response = sessionService.updateSession(id, request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                if (response.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update session: " + e.getMessage()));
        }
    }

    /**
     * Delete session
     * DELETE /api/sessions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSession(@PathVariable Long id) {
        try {
            ApiResponse<String> response = sessionService.deleteSession(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                if (response.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete session: " + e.getMessage()));
        }
    }

    // ==================== SESSION LIFECYCLE ====================

    /**
     * Start session
     * POST /api/sessions/{id}/start
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<TestSession>> startSession(@PathVariable Long id) {
        try {
            ApiResponse<TestSession> response = sessionService.startSession(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                if (response.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to start session: " + e.getMessage()));
        }
    }

    /**
     * Stop session
     * POST /api/sessions/{id}/stop
     */
    @PostMapping("/{id}/stop")
    public ResponseEntity<ApiResponse<TestSession>> stopSession(@PathVariable Long id) {
        try {
            ApiResponse<TestSession> response = sessionService.stopSession(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                if (response.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to stop session: " + e.getMessage()));
        }
    }

    /**
     * Pause session
     * POST /api/sessions/{id}/pause
     */
    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<TestSession>> pauseSession(@PathVariable Long id) {
        try {
            // This would require a new method in SessionService
            return ResponseEntity.ok(ApiResponse.error("Pause functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to pause session: " + e.getMessage()));
        }
    }

    /**
     * Resume session
     * POST /api/sessions/{id}/resume
     */
    @PostMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<TestSession>> resumeSession(@PathVariable Long id) {
        try {
            // This would require a new method in SessionService
            return ResponseEntity.ok(ApiResponse.error("Resume functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to resume session: " + e.getMessage()));
        }
    }

    /**
     * Cancel session
     * POST /api/sessions/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<TestSession>> cancelSession(@PathVariable Long id) {
        try {
            // This would require a new method in SessionService
            return ResponseEntity.ok(ApiResponse.error("Cancel functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to cancel session: " + e.getMessage()));
        }
    }

    // ==================== SESSION FILTERING AND SEARCH ====================

    /**
     * Get sessions by status
     * GET /api/sessions/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TestSession>>> getSessionsByStatus(@PathVariable String status) {
        try {
            TestSession.SessionStatus sessionStatus = TestSession.SessionStatus.valueOf(status.toUpperCase());
            ApiResponse<List<TestSession>> response = sessionService.getSessionsByStatus(sessionStatus);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid status: " + status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch sessions by status: " + e.getMessage()));
        }
    }

    /**
     * Get sessions by executor
     * GET /api/sessions/executor/{executor}
     */
    @GetMapping("/executor/{executor}")
    public ResponseEntity<ApiResponse<List<TestSession>>> getSessionsByExecutor(@PathVariable String executor) {
        try {
            ApiResponse<List<TestSession>> response = sessionService.getSessionsByExecutor(executor);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch sessions by executor: " + e.getMessage()));
        }
    }

    /**
     * Get running sessions
     * GET /api/sessions/running
     */
    @GetMapping("/running")
    public ResponseEntity<ApiResponse<List<TestSession>>> getRunningSessions() {
        try {
            ApiResponse<List<TestSession>> response = sessionService.getRunningSessions();
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch running sessions: " + e.getMessage()));
        }
    }

    /**
     * Get completed sessions
     * GET /api/sessions/completed
     */
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<List<TestSession>>> getCompletedSessions() {
        try {
            ApiResponse<List<TestSession>> response = sessionService.getCompletedSessions();
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch completed sessions: " + e.getMessage()));
        }
    }

    /**
     * Get active sessions
     * GET /api/sessions/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TestSession>>> getActiveSessions() {
        try {
            ApiResponse<List<TestSession>> response = sessionService.getActiveSessions();
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch active sessions: " + e.getMessage()));
        }
    }

    /**
     * Get sessions by project
     * GET /api/sessions/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TestSession>>> getSessionsByProject(@PathVariable Long projectId) {
        try {
            ApiResponse<List<TestSession>> response = sessionService.getSessionsByProjectId(projectId);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch sessions by project: " + e.getMessage()));
        }
    }

    /**
     * Search sessions
     * GET /api/sessions/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TestSession>>> searchSessions(
            @RequestParam String query,
            @RequestParam(defaultValue = "name") String searchBy) {
        try {
            // This would require a new method in SessionService
            return ResponseEntity.ok(ApiResponse.error("Search functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to search sessions: " + e.getMessage()));
        }
    }

    // ==================== SESSION MONITORING ====================

    /**
     * Update session progress
     * POST /api/sessions/{id}/progress
     */
    @PostMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<TestSession>> updateSessionProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> progress) {
        try {
            Integer passed = progress.get("passed");
            Integer failed = progress.get("failed");
            Integer skipped = progress.get("skipped");
            Integer total = progress.get("total");

            if (passed == null || failed == null || skipped == null || total == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Missing required progress parameters"));
            }

            ApiResponse<TestSession> response = sessionService.updateSessionProgress(id, passed, failed, skipped, total);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                if (response.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update session progress: " + e.getMessage()));
        }
    }

    /**
     * Get session logs
     * GET /api/sessions/{id}/logs
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<String>> getSessionLogs(@PathVariable Long id) {
        try {
            // This would require a new method in SessionService
            return ResponseEntity.ok(ApiResponse.error("Session logs functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get session logs: " + e.getMessage()));
        }
    }

    /**
     * Get session results
     * GET /api/sessions/{id}/results
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<ApiResponse<String>> getSessionResults(@PathVariable Long id) {
        try {
            // This would require a new method in SessionService
            return ResponseEntity.ok(ApiResponse.error("Session results functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get session results: " + e.getMessage()));
        }
    }

    // ==================== SESSION STATISTICS ====================

    /**
     * Get session statistics
     * GET /api/sessions/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessionStats() {
        try {
            ApiResponse<Long> activeCountResponse = sessionService.countActiveSessions();
            long activeCount = activeCountResponse.isSuccess() ? activeCountResponse.getData() : 0L;

            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("activeSessions", activeCount);
            stats.put("totalSessions", 0L); // This would need a new method
            stats.put("runningSessions", 0L); // This would need a new method
            stats.put("completedSessions", 0L); // This would need a new method
            stats.put("byStatus", new java.util.HashMap<String, Long>()); // This would need a new method
            stats.put("byExecutor", new java.util.HashMap<String, Long>()); // This would need a new method

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get session statistics: " + e.getMessage()));
        }
    }

    /**
     * Get session dashboard data
     * GET /api/sessions/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessionDashboard() {
        try {
            Map<String, Object> dashboard = new java.util.HashMap<>();

            // Get active sessions
            ApiResponse<List<TestSession>> activeResponse = sessionService.getActiveSessions();
            if (activeResponse.isSuccess()) {
                dashboard.put("activeSessions", activeResponse.getData().size());
            }

            // Get running sessions
            ApiResponse<List<TestSession>> runningResponse = sessionService.getRunningSessions();
            if (runningResponse.isSuccess()) {
                dashboard.put("runningSessions", runningResponse.getData().size());
            }

            // Get completed sessions
            ApiResponse<List<TestSession>> completedResponse = sessionService.getCompletedSessions();
            if (completedResponse.isSuccess()) {
                dashboard.put("completedSessions", completedResponse.getData().size());
            }

            // Session trends (last 30 days)
            Map<String, Object> trends = new java.util.HashMap<>();
            trends.put("created", 0);
            trends.put("started", 0);
            trends.put("completed", 0);
            trends.put("failed", 0);
            dashboard.put("trends", trends);

            // Success rate
            dashboard.put("successRate", 0.0); // This would need calculation

            return ResponseEntity.ok(ApiResponse.success(dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get session dashboard: " + e.getMessage()));
        }
    }
}