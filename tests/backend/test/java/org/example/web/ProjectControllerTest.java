package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.dto.test.ApiResponse;
import org.example.domain.dto.test.TestProjectCreateRequest;
import org.example.domain.entities.TestProject;
import org.example.domain.valueobjects.Version;
import org.example.infrastructure.services.ProjectService;
import org.example.web.controllers.ProjectController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController
 */
@WebMvcTest(ProjectController.class)
@ActiveProfiles("test")
@DisplayName("ProjectController Integration Tests")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    private TestProject testProject;
    private TestProjectCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        // Setup test project
        testProject = new TestProject();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setOwner("test@example.com");
        testProject.setVersion("1.0.0");
        testProject.setEnvironment("test");
        testProject.setStatus(TestProject.ProjectStatus.ACTIVE);
        testProject.setIsActive(true);
        testProject.setProjectId("project-123");

        // Setup create request
        createRequest = new TestProjectCreateRequest();
        createRequest.setName("New Test Project");
        createRequest.setDescription("New Test Description");
        createRequest.setOwner("new@example.com");
        createRequest.setVersion("1.0.0");
        createRequest.setEnvironment("dev");
    }

    @Test
    @DisplayName("Should create project successfully")
    void shouldCreateProjectSuccessfully() throws Exception {
        // Given
        when(projectService.createProject(any(TestProjectCreateRequest.class)))
                .thenReturn(ApiResponse.success("Project created successfully", testProject));

        // When & Then
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Project"));

        verify(projectService, times(1)).createProject(any(TestProjectCreateRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when creating project with validation errors")
    void shouldReturn400WhenCreatingProjectWithValidationErrors() throws Exception {
        // Given - invalid request with missing name
        TestProjectCreateRequest invalidRequest = new TestProjectCreateRequest();
        invalidRequest.setDescription("Description without name");

        // When & Then
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any(TestProjectCreateRequest.class));
    }

    @Test
    @DisplayName("Should return 500 when project creation fails")
    void shouldReturn500WhenProjectCreationFails() throws Exception {
        // Given
        when(projectService.createProject(any(TestProjectCreateRequest.class)))
                .thenReturn(ApiResponse.error("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Database connection failed"));

        verify(projectService, times(1)).createProject(any(TestProjectCreateRequest.class));
    }

    @Test
    @DisplayName("Should get all projects successfully")
    void shouldGetAllProjectsSuccessfully() throws Exception {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(projectService.getAllProjects())
                .thenReturn(ApiResponse.success(projects));

        // When & Then
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("Test Project"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    @DisplayName("Should filter inactive projects when includeInactive is false")
    void shouldFilterInactiveProjectsWhenIncludeInactiveIsFalse() throws Exception {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(projectService.getAllProjects())
                .thenReturn(ApiResponse.success(projects));

        // When & Then
        mockMvc.perform(get("/api/projects?includeInactive=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1L));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    @DisplayName("Should get project by ID successfully")
    void shouldGetProjectByIdSuccessfully() throws Exception {
        // Given
        when(projectService.getProjectById(1L))
                .thenReturn(ApiResponse.success(testProject));

        // When & Then
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Project"));

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    @DisplayName("Should return 404 when project not found by ID")
    void shouldReturn404WhenProjectNotFoundById() throws Exception {
        // Given
        when(projectService.getProjectById(999L))
                .thenReturn(ApiResponse.error("Project not found"));

        // When & Then
        mockMvc.perform(get("/api/projects/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Project not found"));

        verify(projectService, times(1)).getProjectById(999L);
    }

    @Test
    @DisplayName("Should get project by projectId successfully")
    void shouldGetProjectByProjectIdSuccessfully() throws Exception {
        // Given
        when(projectService.getProjectByProjectId("project-123"))
                .thenReturn(ApiResponse.success(testProject));

        // When & Then
        mockMvc.perform(get("/api/projects/projectId/project-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.projectId").value("project-123"));

        verify(projectService, times(1)).getProjectByProjectId("project-123");
    }

    @Test
    @DisplayName("Should return 404 when project not found by projectId")
    void shouldReturn404WhenProjectNotFoundByProjectId() throws Exception {
        // Given
        when(projectService.getProjectByProjectId("nonexistent"))
                .thenReturn(ApiResponse.error("Project not found"));

        // When & Then
        mockMvc.perform(get("/api/projects/projectId/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Project not found"));

        verify(projectService, times(1)).getProjectByProjectId("nonexistent");
    }

    @Test
    @DisplayName("Should update project successfully")
    void shouldUpdateProjectSuccessfully() throws Exception {
        // Given
        when(projectService.updateProject(eq(1L), any(TestProjectCreateRequest.class)))
                .thenReturn(ApiResponse.success("Project updated successfully", testProject));

        // When & Then
        mockMvc.perform(put("/api/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project updated successfully"));

        verify(projectService, times(1)).updateProject(eq(1L), any(TestProjectCreateRequest.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent project")
    void shouldReturn404WhenUpdatingNonExistentProject() throws Exception {
        // Given
        when(projectService.updateProject(eq(999L), any(TestProjectCreateRequest.class)))
                .thenReturn(ApiResponse.error("Project not found"));

        // When & Then
        mockMvc.perform(put("/api/projects/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Project not found"));

        verify(projectService, times(1)).updateProject(eq(999L), any(TestProjectCreateRequest.class));
    }

    @Test
    @DisplayName("Should delete project successfully")
    void shouldDeleteProjectSuccessfully() throws Exception {
        // Given
        when(projectService.deleteProject(1L))
                .thenReturn(ApiResponse.success("Project deleted successfully"));

        // When & Then
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project deleted successfully"));

        verify(projectService, times(1)).deleteProject(1L);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent project")
    void shouldReturn404WhenDeletingNonExistentProject() throws Exception {
        // Given
        when(projectService.deleteProject(999L))
                .thenReturn(ApiResponse.error("Project not found"));

        // When & Then
        mockMvc.perform(delete("/api/projects/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Project not found"));

        verify(projectService, times(1)).deleteProject(999L);
    }

    @Test
    @DisplayName("Should get projects by status successfully")
    void shouldGetProjectsByStatusSuccessfully() throws Exception {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(projectService.getProjectsByStatus(TestProject.ProjectStatus.ACTIVE))
                .thenReturn(ApiResponse.success(projects));

        // When & Then
        mockMvc.perform(get("/api/projects/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));

        verify(projectService, times(1)).getProjectsByStatus(TestProject.ProjectStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return 400 for invalid status")
    void shouldReturn400ForInvalidStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/projects/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(projectService, never()).getProjectsByStatus(any());
    }

    @Test
    @DisplayName("Should get projects by owner successfully")
    void shouldGetProjectsByOwnerSuccessfully() throws Exception {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(projectService.getProjectsByOwner("test@example.com"))
                .thenReturn(ApiResponse.success(projects));

        // When & Then
        mockMvc.perform(get("/api/projects/owner/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].owner").value("test@example.com"));

        verify(projectService, times(1)).getProjectsByOwner("test@example.com");
    }

    @Test
    @DisplayName("Should get active projects successfully")
    void shouldGetActiveProjectsSuccessfully() throws Exception {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(projectService.getActiveProjects())
                .thenReturn(ApiResponse.success(projects));

        // When & Then
        mockMvc.perform(get("/api/projects/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].isActive").value(true));

        verify(projectService, times(1)).getActiveProjects();
    }

    @Test
    @DisplayName("Should get project statistics successfully")
    void shouldGetProjectStatisticsSuccessfully() throws Exception {
        // Given
        when(projectService.countActiveProjects())
                .thenReturn(ApiResponse.success(5L));

        // When & Then
        mockMvc.perform(get("/api/projects/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.activeProjects").value(5))
                .andExpect(jsonPath("$.data.totalProjects").value(0));

        verify(projectService, times(1)).countActiveProjects();
    }

    @Test
    @DisplayName("Should return 500 when getting project statistics fails")
    void shouldReturn500WhenGettingProjectStatisticsFails() throws Exception {
        // Given
        when(projectService.countActiveProjects())
                .thenReturn(ApiResponse.error("Database error"));

        // When & Then
        mockMvc.perform(get("/api/projects/stats"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));

        verify(projectService, times(1)).countActiveProjects();
    }

    @Test
    @DisplayName("Should handle search functionality not implemented")
    void shouldHandleSearchFunctionalityNotImplemented() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/projects/search?query=test&searchBy=name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Search functionality not implemented yet"));
    }

    @Test
    @DisplayName("Should handle recent projects functionality not implemented")
    void shouldHandleRecentProjectsFunctionalityNotImplemented() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/projects/recent?hours=24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Recent projects functionality not implemented yet"));
    }

    @Test
    @DisplayName("Should handle dashboard functionality not implemented")
    void shouldHandleDashboardFunctionalityNotImplemented() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/projects/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(projectService, times(1)).getActiveProjects();
    }
}