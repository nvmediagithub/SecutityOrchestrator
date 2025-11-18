package org.example.features.testdata.application.web.controllers;

import org.example.shared.common.ApiResponse;
import org.example.shared.domain.entities.TestProject;
import org.example.shared.domain.dto.test.TestProjectCreateRequest;
import org.example.shared.core.valueobjects.Version;
import org.example.features.testdata.domain.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing test projects
 * Provides CRUD operations for test projects
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ==================== PROJECT CRUD OPERATIONS ====================

    /**
     * Create a new test project
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TestProject>> createProject(@Valid @RequestBody TestProjectCreateRequest request) {
        try {
            ApiResponse<TestProject> response = projectService.createProject(request);
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create project: " + e.getMessage()));
        }
    }

    /**
     * Get all projects
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestProject>>> getAllProjects(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            ApiResponse<List<TestProject>> response = projectService.getAllProjects();
            if (response.isSuccess()) {
                // Filter inactive projects if requested
                if (!includeInactive) {
                    List<TestProject> activeProjects = response.getData().stream()
                        .filter(TestProject::isActive)
                        .collect(java.util.stream.Collectors.toList());
                    response.setData(activeProjects);
                }
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch projects: " + e.getMessage()));
        }
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestProject>> getProjectById(@PathVariable Long id) {
        try {
            ApiResponse<TestProject> response = projectService.getProjectById(id);
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
                .body(ApiResponse.error("Failed to fetch project: " + e.getMessage()));
        }
    }

    /**
     * Get project by projectId
     * GET /api/projects/projectId/{projectId}
     */
    @GetMapping("/projectId/{projectId}")
    public ResponseEntity<ApiResponse<TestProject>> getProjectByProjectId(@PathVariable String projectId) {
        try {
            ApiResponse<TestProject> response = projectService.getProjectByProjectId(projectId);
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
                .body(ApiResponse.error("Failed to fetch project: " + e.getMessage()));
        }
    }

    /**
     * Update project
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestProject>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody TestProjectCreateRequest request) {
        try {
            ApiResponse<TestProject> response = projectService.updateProject(id, request);
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
                .body(ApiResponse.error("Failed to update project: " + e.getMessage()));
        }
    }

    /**
     * Delete project
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long id) {
        try {
            ApiResponse<String> response = projectService.deleteProject(id);
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
                .body(ApiResponse.error("Failed to delete project: " + e.getMessage()));
        }
    }

    // ==================== PROJECT FILTERING AND SEARCH ====================

    /**
     * Get projects by status
     * GET /api/projects/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TestProject>>> getProjectsByStatus(@PathVariable String status) {
        try {
            TestProject.ProjectStatus projectStatus = TestProject.ProjectStatus.valueOf(status.toUpperCase());
            ApiResponse<List<TestProject>> response = projectService.getProjectsByStatus(projectStatus);
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
                .body(ApiResponse.error("Failed to fetch projects by status: " + e.getMessage()));
        }
    }

    /**
     * Get projects by owner
     * GET /api/projects/owner/{owner}
     */
    @GetMapping("/owner/{owner}")
    public ResponseEntity<ApiResponse<List<TestProject>>> getProjectsByOwner(@PathVariable String owner) {
        try {
            ApiResponse<List<TestProject>> response = projectService.getProjectsByOwner(owner);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch projects by owner: " + e.getMessage()));
        }
    }

    /**
     * Get projects by environment
     * GET /api/projects/environment/{environment}
     */
    @GetMapping("/environment/{environment}")
    public ResponseEntity<ApiResponse<List<TestProject>>> getProjectsByEnvironment(@PathVariable String environment) {
        try {
            ApiResponse<List<TestProject>> response = projectService.getProjectsByEnvironment(environment);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch projects by environment: " + e.getMessage()));
        }
    }

    /**
     * Get active projects
     * GET /api/projects/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TestProject>>> getActiveProjects() {
        try {
            ApiResponse<List<TestProject>> response = projectService.getActiveProjects();
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch active projects: " + e.getMessage()));
        }
    }

    /**
     * Search projects by name
     * GET /api/projects/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TestProject>>> searchProjects(
            @RequestParam String query,
            @RequestParam(defaultValue = "name") String searchBy) {
        try {
            // This would require a new method in ProjectService
            // For now, return a mock response
            return ResponseEntity.ok(ApiResponse.error("Search functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to search projects: " + e.getMessage()));
        }
    }

    /**
     * Get projects with recent activity
     * GET /api/projects/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TestProject>>> getRecentProjects(
            @RequestParam(defaultValue = "24") int hours) {
        try {
            // This would require a new method in ProjectService
            // For now, return a mock response
            return ResponseEntity.ok(ApiResponse.error("Recent projects functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch recent projects: " + e.getMessage()));
        }
    }

    // ==================== PROJECT STATISTICS ====================

    /**
     * Get project statistics
     * GET /api/projects/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProjectStats() {
        try {
            ApiResponse<Long> activeCountResponse = projectService.countActiveProjects();
            long activeCount = activeCountResponse.isSuccess() ? activeCountResponse.getData() : 0L;

            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("activeProjects", activeCount);
            stats.put("totalProjects", 0L); // This would need a new method
            stats.put("byStatus", new java.util.HashMap<String, Long>()); // This would need a new method
            stats.put("byEnvironment", new java.util.HashMap<String, Long>()); // This would need a new method
            stats.put("byOwner", new java.util.HashMap<String, Long>()); // This would need a new method

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get project statistics: " + e.getMessage()));
        }
    }

    /**
     * Get project dashboard data
     * GET /api/projects/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProjectDashboard() {
        try {
            Map<String, Object> dashboard = new java.util.HashMap<>();

            // Get active projects
            ApiResponse<List<TestProject>> activeResponse = projectService.getActiveProjects();
            if (activeResponse.isSuccess()) {
                dashboard.put("activeProjects", activeResponse.getData().size());
            }

            // Get recent projects (last 7 days)
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            // This would require a new method in ProjectService
            dashboard.put("recentProjects", 0);

            // Get projects by status
            Map<String, Long> byStatus = new java.util.HashMap<>();
            byStatus.put("DRAFT", 0L);
            byStatus.put("ACTIVE", 0L);
            byStatus.put("ON_HOLD", 0L);
            byStatus.put("COMPLETED", 0L);
            byStatus.put("ARCHIVED", 0L);
            dashboard.put("byStatus", byStatus);

            // Project trends (last 30 days)
            Map<String, Object> trends = new java.util.HashMap<>();
            trends.put("created", 0);
            trends.put("completed", 0);
            trends.put("archived", 0);
            dashboard.put("trends", trends);

            return ResponseEntity.ok(ApiResponse.success(dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get project dashboard: " + e.getMessage()));
        }
    }

    // ==================== PROJECT ACTIONS ====================

    /**
     * Activate project
     * POST /api/projects/{id}/activate
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<TestProject>> activateProject(@PathVariable Long id) {
        try {
            // This would require a new method in ProjectService
            return ResponseEntity.ok(ApiResponse.error("Activate functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to activate project: " + e.getMessage()));
        }
    }

    /**
     * Deactivate project
     * POST /api/projects/{id}/deactivate
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<TestProject>> deactivateProject(@PathVariable Long id) {
        try {
            // This would require a new method in ProjectService
            return ResponseEntity.ok(ApiResponse.error("Deactivate functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to deactivate project: " + e.getMessage()));
        }
    }

    /**
     * Archive project
     * POST /api/projects/{id}/archive
     */
    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<TestProject>> archiveProject(@PathVariable Long id) {
        try {
            // This would require a new method in ProjectService
            return ResponseEntity.ok(ApiResponse.error("Archive functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to archive project: " + e.getMessage()));
        }
    }

    /**
     * Clone project
     * POST /api/projects/{id}/clone
     */
    @PostMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<TestProject>> cloneProject(
            @PathVariable Long id,
            @RequestParam String newName) {
        try {
            // This would require a new method in ProjectService
            return ResponseEntity.ok(ApiResponse.error("Clone functionality not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to clone project: " + e.getMessage()));
        }
    }
}