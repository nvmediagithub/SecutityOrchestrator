package org.example.infrastructure.services;

import org.example.domain.dto.test.ApiResponse;
import org.example.domain.dto.test.TestProjectCreateRequest;
import org.example.domain.entities.TestProject;
import org.example.infrastructure.repositories.TestProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing test projects
 */
@Service
@Transactional
public class ProjectService {
    
    private final TestProjectRepository testProjectRepository;
    
    public ProjectService(TestProjectRepository testProjectRepository) {
        this.testProjectRepository = testProjectRepository;
    }
    
    /**
     * Create a new test project
     */
    public ApiResponse<TestProject> createProject(TestProjectCreateRequest request) {
        try {
            TestProject project = new TestProject(request.getName(), request.getDescription(), request.getOwner());
            
            // Set additional properties
            if (request.getVersion() != null) project.setVersion(request.getVersion());
            if (request.getEnvironment() != null) project.setEnvironment(request.getEnvironment());
            if (request.getTags() != null) project.getTags().addAll(request.getTags());
            if (request.getConfiguration() != null) project.setConfiguration(request.getConfiguration());
            if (request.getSettings() != null) project.setSettings(request.getSettings());
            if (request.getTestFramework() != null) project.setTestFramework(request.getTestFramework());
            if (request.getBaseUrl() != null) project.setBaseUrl(request.getBaseUrl());
            if (request.getTimeoutMs() != null) project.setTimeoutMs(request.getTimeoutMs());
            if (request.getRetryCount() != null) project.setRetryCount(request.getRetryCount());
            if (request.getParallelExecution() != null) project.setParallelExecution(request.getParallelExecution());
            
            TestProject saved = testProjectRepository.save(project);
            return ApiResponse.success("Project created successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Failed to create project: " + e.getMessage());
        }
    }
    
    /**
     * Get all projects
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestProject>> getAllProjects() {
        try {
            List<TestProject> projects = testProjectRepository.findAllByOrderByCreatedAtDesc();
            return ApiResponse.success(projects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects: " + e.getMessage());
        }
    }
    
    /**
     * Get project by ID
     */
    @Transactional(readOnly = true)
    public ApiResponse<TestProject> getProjectById(Long id) {
        try {
            Optional<TestProject> projectOpt = testProjectRepository.findById(id);
            if (projectOpt.isEmpty()) {
                return ApiResponse.error("Project not found");
            }
            return ApiResponse.success(projectOpt.get());
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch project: " + e.getMessage());
        }
    }
    
    /**
     * Get project by projectId
     */
    @Transactional(readOnly = true)
    public ApiResponse<TestProject> getProjectByProjectId(String projectId) {
        try {
            Optional<TestProject> projectOpt = testProjectRepository.findByProjectId(projectId);
            if (projectOpt.isEmpty()) {
                return ApiResponse.error("Project not found");
            }
            return ApiResponse.success(projectOpt.get());
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch project: " + e.getMessage());
        }
    }
    
    /**
     * Update project
     */
    public ApiResponse<TestProject> updateProject(Long id, TestProjectCreateRequest request) {
        try {
            Optional<TestProject> projectOpt = testProjectRepository.findById(id);
            if (projectOpt.isEmpty()) {
                return ApiResponse.error("Project not found");
            }
            
            TestProject project = projectOpt.get();
            project.setName(request.getName());
            project.setDescription(request.getDescription());
            if (request.getOwner() != null) project.setOwner(request.getOwner());
            if (request.getVersion() != null) project.setVersion(request.getVersion());
            if (request.getEnvironment() != null) project.setEnvironment(request.getEnvironment());
            if (request.getConfiguration() != null) project.setConfiguration(request.getConfiguration());
            if (request.getSettings() != null) project.setSettings(request.getSettings());
            if (request.getTestFramework() != null) project.setTestFramework(request.getTestFramework());
            if (request.getBaseUrl() != null) project.setBaseUrl(request.getBaseUrl());
            if (request.getTimeoutMs() != null) project.setTimeoutMs(request.getTimeoutMs());
            if (request.getRetryCount() != null) project.setRetryCount(request.getRetryCount());
            if (request.getParallelExecution() != null) project.setParallelExecution(request.getParallelExecution());
            
            // Update tags
            if (request.getTags() != null) {
                project.getTags().clear();
                project.getTags().addAll(request.getTags());
            }
            
            TestProject updated = testProjectRepository.save(project);
            return ApiResponse.success("Project updated successfully", updated);
        } catch (Exception e) {
            return ApiResponse.error("Failed to update project: " + e.getMessage());
        }
    }
    
    /**
     * Delete project
     */
    public ApiResponse<String> deleteProject(Long id) {
        try {
            Optional<TestProject> projectOpt = testProjectRepository.findById(id);
            if (projectOpt.isEmpty()) {
                return ApiResponse.error("Project not found");
            }
            
            testProjectRepository.deleteById(id);
            return ApiResponse.success("Project deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete project: " + e.getMessage());
        }
    }
    
    /**
     * Get projects by status
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestProject>> getProjectsByStatus(TestProject.ProjectStatus status) {
        try {
            List<TestProject> projects = testProjectRepository.findByStatus(status);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects by status: " + e.getMessage());
        }
    }
    
    /**
     * Get projects by owner
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestProject>> getProjectsByOwner(String owner) {
        try {
            List<TestProject> projects = testProjectRepository.findByOwner(owner);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects by owner: " + e.getMessage());
        }
    }
    
    /**
     * Get active projects
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestProject>> getActiveProjects() {
        try {
            List<TestProject> projects = testProjectRepository.findByIsActiveTrue();
            return ApiResponse.success(projects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch active projects: " + e.getMessage());
        }
    }
    
    /**
     * Get projects by environment
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestProject>> getProjectsByEnvironment(String environment) {
        try {
            List<TestProject> projects = testProjectRepository.findByEnvironment(environment);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch projects by environment: " + e.getMessage());
        }
    }
    
    /**
     * Count active projects
     */
    @Transactional(readOnly = true)
    public ApiResponse<Long> countActiveProjects() {
        try {
            long count = testProjectRepository.countActiveProjects();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error("Failed to count active projects: " + e.getMessage());
        }
    }
}