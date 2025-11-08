package org.example.services;

import org.example.domain.dto.test.ApiResponse;
import org.example.domain.dto.test.TestProjectCreateRequest;
import org.example.domain.entities.TestProject;
import org.example.infrastructure.repositories.TestProjectRepository;
import org.example.infrastructure.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProjectService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Unit Tests")
class ProjectServiceTest {

    @Mock
    private TestProjectRepository testProjectRepository;

    @InjectMocks
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
    void shouldCreateProjectSuccessfully() {
        // Given
        when(testProjectRepository.save(any(TestProject.class))).thenReturn(testProject);

        // When
        ApiResponse<TestProject> result = projectService.createProject(createRequest);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("Project created successfully", result.getMessage());
        verify(testProjectRepository, times(1)).save(any(TestProject.class));
    }

    @Test
    @DisplayName("Should return error when project creation fails")
    void shouldReturnErrorWhenProjectCreationFails() {
        // Given
        when(testProjectRepository.save(any(TestProject.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<TestProject> result = projectService.createProject(createRequest);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to create project"));
        verify(testProjectRepository, times(1)).save(any(TestProject.class));
    }

    @Test
    @DisplayName("Should get all projects successfully")
    void shouldGetAllProjectsSuccessfully() {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(testProjectRepository.findAllByOrderByCreatedAtDesc()).thenReturn(projects);

        // When
        ApiResponse<List<TestProject>> result = projectService.getAllProjects();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testProjectRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return error when getting all projects fails")
    void shouldReturnErrorWhenGettingAllProjectsFails() {
        // Given
        when(testProjectRepository.findAllByOrderByCreatedAtDesc())
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<List<TestProject>> result = projectService.getAllProjects();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch projects"));
        verify(testProjectRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should get project by ID successfully")
    void shouldGetProjectByIdSuccessfully() {
        // Given
        when(testProjectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // When
        ApiResponse<TestProject> result = projectService.getProjectById(1L);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        verify(testProjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return error when project not found by ID")
    void shouldReturnErrorWhenProjectNotFoundById() {
        // Given
        when(testProjectRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ApiResponse<TestProject> result = projectService.getProjectById(999L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Project not found", result.getMessage());
        verify(testProjectRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get project by projectId successfully")
    void shouldGetProjectByProjectIdSuccessfully() {
        // Given
        testProject.setProjectId("project-123");
        when(testProjectRepository.findByProjectId("project-123")).thenReturn(Optional.of(testProject));

        // When
        ApiResponse<TestProject> result = projectService.getProjectByProjectId("project-123");

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("project-123", result.getData().getProjectId());
        verify(testProjectRepository, times(1)).findByProjectId("project-123");
    }

    @Test
    @DisplayName("Should return error when project not found by projectId")
    void shouldReturnErrorWhenProjectNotFoundByProjectId() {
        // Given
        when(testProjectRepository.findByProjectId("nonexistent")).thenReturn(Optional.empty());

        // When
        ApiResponse<TestProject> result = projectService.getProjectByProjectId("nonexistent");

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Project not found", result.getMessage());
        verify(testProjectRepository, times(1)).findByProjectId("nonexistent");
    }

    @Test
    @DisplayName("Should update project successfully")
    void shouldUpdateProjectSuccessfully() {
        // Given
        when(testProjectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(testProjectRepository.save(any(TestProject.class))).thenReturn(testProject);

        // When
        ApiResponse<TestProject> result = projectService.updateProject(1L, createRequest);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("Project updated successfully", result.getMessage());
        verify(testProjectRepository, times(1)).findById(1L);
        verify(testProjectRepository, times(1)).save(any(TestProject.class));
    }

    @Test
    @DisplayName("Should return error when updating non-existent project")
    void shouldReturnErrorWhenUpdatingNonExistentProject() {
        // Given
        when(testProjectRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ApiResponse<TestProject> result = projectService.updateProject(999L, createRequest);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Project not found", result.getMessage());
        verify(testProjectRepository, times(1)).findById(999L);
        verify(testProjectRepository, never()).save(any(TestProject.class));
    }

    @Test
    @DisplayName("Should delete project successfully")
    void shouldDeleteProjectSuccessfully() {
        // Given
        when(testProjectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // When
        ApiResponse<String> result = projectService.deleteProject(1L);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("Project deleted successfully", result.getMessage());
        verify(testProjectRepository, times(1)).findById(1L);
        verify(testProjectRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return error when deleting non-existent project")
    void shouldReturnErrorWhenDeletingNonExistentProject() {
        // Given
        when(testProjectRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ApiResponse<String> result = projectService.deleteProject(999L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Project not found", result.getMessage());
        verify(testProjectRepository, times(1)).findById(999L);
        verify(testProjectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get projects by status successfully")
    void shouldGetProjectsByStatusSuccessfully() {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        TestProject.ProjectStatus status = TestProject.ProjectStatus.ACTIVE;
        when(testProjectRepository.findByStatus(status)).thenReturn(projects);

        // When
        ApiResponse<List<TestProject>> result = projectService.getProjectsByStatus(status);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testProjectRepository, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Should get projects by owner successfully")
    void shouldGetProjectsByOwnerSuccessfully() {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(testProjectRepository.findByOwner("test@example.com")).thenReturn(projects);

        // When
        ApiResponse<List<TestProject>> result = projectService.getProjectsByOwner("test@example.com");

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testProjectRepository, times(1)).findByOwner("test@example.com");
    }

    @Test
    @DisplayName("Should get active projects successfully")
    void shouldGetActiveProjectsSuccessfully() {
        // Given
        List<TestProject> projects = Arrays.asList(testProject);
        when(testProjectRepository.findByIsActiveTrue()).thenReturn(projects);

        // When
        ApiResponse<List<TestProject>> result = projectService.getActiveProjects();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testProjectRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Should count active projects successfully")
    void shouldCountActiveProjectsSuccessfully() {
        // Given
        when(testProjectRepository.countActiveProjects()).thenReturn(5L);

        // When
        ApiResponse<Long> result = projectService.countActiveProjects();

        // Then
        assertTrue(result.isSuccess());
        assertEquals(5L, result.getData());
        verify(testProjectRepository, times(1)).countActiveProjects();
    }

    @Test
    @DisplayName("Should handle exception when counting active projects")
    void shouldHandleExceptionWhenCountingActiveProjects() {
        // Given
        when(testProjectRepository.countActiveProjects())
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<Long> result = projectService.countActiveProjects();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to count active projects"));
        verify(testProjectRepository, times(1)).countActiveProjects();
    }
}