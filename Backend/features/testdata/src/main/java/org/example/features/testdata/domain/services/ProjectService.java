package org.example.features.testdata.domain.services;

import org.example.shared.common.ApiResponse;
import org.example.shared.domain.entities.TestProject;
import org.example.shared.domain.dto.test.TestProjectCreateRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Domain service for managing test projects in test data scenarios.
 * Provides CRUD operations and business logic for TestProject entities.
 */
@Service
public class ProjectService {

    // In-memory storage for test data scenarios - in production this would use a repository
    private final Map<Long, TestProject> projects = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Create a new test project
     */
    public ApiResponse<TestProject> createProject(TestProjectCreateRequest request) {
        try {
            validateCreateRequest(request);

            TestProject project = new TestProject(request.getName(), request.getDescription());
            project.setId(idGenerator.getAndIncrement());
            project.setOwner(request.getOwner());

            // Set optional properties
            if (request.getEnvironment() != null) {
                project.setEnvironment(request.getEnvironment());
            }
            if (request.getTestFramework() != null) {
                project.setTestFramework(request.getTestFramework());
            }
            if (request.getBaseUrl() != null) {
                project.setBaseUrl(request.getBaseUrl());
            }
            if (request.getTimeoutMs() != null) {
                project.setTimeoutMs(request.getTimeoutMs());
            }
            if (request.getRetryCount() != null) {
                project.setRetryCount(request.getRetryCount());
            }
            project.setParallelExecution(request.isParallelExecution());
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                project.getTags().addAll(request.getTags());
            }

            projects.put(project.getId(), project);
            return ApiResponse.success("Project created successfully", project);

        } catch (IllegalArgumentException e) {
            return ApiResponse.error("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to create project: " + e.getMessage());
        }
    }

    /**
     * Get all projects
     */
    public ApiResponse<List<TestProject>> getAllProjects() {
        try {
            List<TestProject> projectList = new ArrayList<>(projects.values());
            // Sort by creation date descending
            projectList.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
            return ApiResponse.success(projectList);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects: " + e.getMessage());
        }
    }

    /**
     * Get project by ID
     */
    public ApiResponse<TestProject> getProjectById(Long id) {
        try {
            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            return ApiResponse.success(project);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch project: " + e.getMessage());
        }
    }

    /**
     * Get project by projectId
     */
    public ApiResponse<TestProject> getProjectByProjectId(String projectId) {
        try {
            Optional<TestProject> project = projects.values().stream()
                .filter(p -> projectId.equals(p.getProjectId()))
                .findFirst();

            if (project.isEmpty()) {
                return ApiResponse.error("Project not found");
            }
            return ApiResponse.success(project.get());
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch project: " + e.getMessage());
        }
    }

    /**
     * Update project
     */
    public ApiResponse<TestProject> updateProject(Long id, TestProjectCreateRequest request) {
        try {
            validateCreateRequest(request);

            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }

            project.setName(request.getName());
            project.setDescription(request.getDescription());

            if (request.getOwner() != null) {
                project.setOwner(request.getOwner());
            }
            if (request.getEnvironment() != null) {
                project.setEnvironment(request.getEnvironment());
            }
            if (request.getTestFramework() != null) {
                project.setTestFramework(request.getTestFramework());
            }
            if (request.getBaseUrl() != null) {
                project.setBaseUrl(request.getBaseUrl());
            }
            if (request.getTimeoutMs() != null) {
                project.setTimeoutMs(request.getTimeoutMs());
            }
            if (request.getRetryCount() != null) {
                project.setRetryCount(request.getRetryCount());
            }
            project.setParallelExecution(request.isParallelExecution());

            // Update tags
            if (request.getTags() != null) {
                project.getTags().clear();
                project.getTags().addAll(request.getTags());
            }

            return ApiResponse.success("Project updated successfully", project);

        } catch (IllegalArgumentException e) {
            return ApiResponse.error("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to update project: " + e.getMessage());
        }
    }

    /**
     * Delete project
     */
    public ApiResponse<String> deleteProject(Long id) {
        try {
            TestProject project = projects.remove(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            return ApiResponse.success("Project deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete project: " + e.getMessage());
        }
    }

    /**
     * Get projects by status
     */
    public ApiResponse<List<TestProject>> getProjectsByStatus(TestProject.ProjectStatus status) {
        try {
            List<TestProject> filteredProjects = projects.values().stream()
                .filter(p -> status.equals(p.getStatus()))
                .collect(Collectors.toList());
            return ApiResponse.success(filteredProjects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects by status: " + e.getMessage());
        }
    }

    /**
     * Get projects by owner
     */
    public ApiResponse<List<TestProject>> getProjectsByOwner(String owner) {
        try {
            List<TestProject> filteredProjects = projects.values().stream()
                .filter(p -> owner.equals(p.getOwner()))
                .collect(Collectors.toList());
            return ApiResponse.success(filteredProjects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects by owner: " + e.getMessage());
        }
    }

    /**
     * Get projects by environment
     */
    public ApiResponse<List<TestProject>> getProjectsByEnvironment(String environment) {
        try {
            List<TestProject> filteredProjects = projects.values().stream()
                .filter(p -> environment.equals(p.getEnvironment()))
                .collect(Collectors.toList());
            return ApiResponse.success(filteredProjects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects by environment: " + e.getMessage());
        }
    }

    /**
     * Get active projects
     */
    public ApiResponse<List<TestProject>> getActiveProjects() {
        try {
            List<TestProject> activeProjects = projects.values().stream()
                .filter(TestProject::isActive)
                .collect(Collectors.toList());
            return ApiResponse.success(activeProjects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch active projects: " + e.getMessage());
        }
    }

    /**
     * Count active projects
     */
    public ApiResponse<Long> countActiveProjects() {
        try {
            long count = projects.values().stream()
                .filter(TestProject::isActive)
                .count();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error("Failed to count active projects: " + e.getMessage());
        }
    }

    /**
     * Activate project
     */
    public ApiResponse<TestProject> activateProject(Long id) {
        try {
            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            project.activate();
            return ApiResponse.success("Project activated successfully", project);
        } catch (Exception e) {
            return ApiResponse.error("Failed to activate project: " + e.getMessage());
        }
    }

    /**
     * Deactivate project
     */
    public ApiResponse<TestProject> deactivateProject(Long id) {
        try {
            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            project.deactivate();
            return ApiResponse.success("Project deactivated successfully", project);
        } catch (Exception e) {
            return ApiResponse.error("Failed to deactivate project: " + e.getMessage());
        }
    }

    /**
     * Archive project
     */
    public ApiResponse<TestProject> archiveProject(Long id) {
        try {
            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            project.archive();
            return ApiResponse.success("Project archived successfully", project);
        } catch (Exception e) {
            return ApiResponse.error("Failed to archive project: " + e.getMessage());
        }
    }

    /**
     * Clone project
     */
    public ApiResponse<TestProject> cloneProject(Long id, String newName) {
        try {
            TestProject originalProject = projects.get(id);
            if (originalProject == null) {
                return ApiResponse.error("Original project not found");
            }

            if (newName == null || newName.trim().isEmpty()) {
                return ApiResponse.error("New project name is required");
            }

            TestProject clonedProject = new TestProject(newName, originalProject.getDescription());
            clonedProject.setId(idGenerator.getAndIncrement());
            clonedProject.setOwner(originalProject.getOwner());
            clonedProject.setEnvironment(originalProject.getEnvironment());
            clonedProject.setTestFramework(originalProject.getTestFramework());
            clonedProject.setBaseUrl(originalProject.getBaseUrl());
            clonedProject.setTimeoutMs(originalProject.getTimeoutMs());
            clonedProject.setRetryCount(originalProject.getRetryCount());
            clonedProject.setParallelExecution(originalProject.isParallelExecution());
            clonedProject.getTags().addAll(originalProject.getTags());

            projects.put(clonedProject.getId(), clonedProject);
            return ApiResponse.success("Project cloned successfully", clonedProject);

        } catch (Exception e) {
            return ApiResponse.error("Failed to clone project: " + e.getMessage());
        }
    }

    /**
     * Update project last test execution
     */
    public ApiResponse<TestProject> updateLastTestExecution(Long id) {
        try {
            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            project.updateLastTestExecution();
            return ApiResponse.success("Last test execution updated successfully", project);
        } catch (Exception e) {
            return ApiResponse.error("Failed to update last test execution: " + e.getMessage());
        }
    }

    /**
     * Add tag to project
     */
    public ApiResponse<TestProject> addTagToProject(Long id, String tag) {
        try {
            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            project.addTag(tag);
            return ApiResponse.success("Tag added successfully", project);
        } catch (Exception e) {
            return ApiResponse.error("Failed to add tag: " + e.getMessage());
        }
    }

    /**
     * Remove tag from project
     */
    public ApiResponse<TestProject> removeTagFromProject(Long id, String tag) {
        try {
            TestProject project = projects.get(id);
            if (project == null) {
                return ApiResponse.error("Project not found");
            }
            project.removeTag(tag);
            return ApiResponse.success("Tag removed successfully", project);
        } catch (Exception e) {
            return ApiResponse.error("Failed to remove tag: " + e.getMessage());
        }
    }

    private void validateCreateRequest(TestProjectCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (request.getOwner() == null || request.getOwner().trim().isEmpty()) {
            throw new IllegalArgumentException("Project owner is required");
        }
    }
}