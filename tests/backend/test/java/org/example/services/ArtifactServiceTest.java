package org.example.services;

import org.example.domain.dto.test.ApiResponse;
import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.OpenApiSpec;
import org.example.domain.entities.TestArtifact;
import org.example.domain.entities.TestProject;
import org.example.domain.valueobjects.OpenApiVersion;
import org.example.infrastructure.repositories.BpmnDiagramRepository;
import org.example.infrastructure.repositories.OpenApiSpecRepository;
import org.example.infrastructure.repositories.TestArtifactRepository;
import org.example.infrastructure.services.ArtifactService;
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
 * Unit tests for ArtifactService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArtifactService Unit Tests")
class ArtifactServiceTest {

    @Mock
    private OpenApiSpecRepository openApiSpecRepository;

    @Mock
    private BpmnDiagramRepository bpmnDiagramRepository;

    @Mock
    private TestArtifactRepository testArtifactRepository;

    @InjectMocks
    private ArtifactService artifactService;

    private TestArtifact testArtifact;
    private OpenApiSpec openApiSpec;
    private BpmnDiagram bpmnDiagram;
    private TestProject testProject;

    @BeforeEach
    void setUp() {
        // Setup test project
        testProject = new TestProject();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setOwner("test@example.com");

        // Setup OpenAPI spec
        openApiSpec = new OpenApiSpec();
        openApiSpec.setId(1L);
        openApiSpec.setTitle("Test API Spec");
        openApiSpec.setDescription("Test API Specification");
        openApiSpec.setOpenApiVersion(OpenApiVersion.V3_0_0);
        openApiSpec.setSpecificationContent("openapi: 3.0.0\ninfo:\n  title: Test API\n  version: 1.0.0");
        openApiSpec.setIsActive(true);

        // Setup BPMN diagram
        bpmnDiagram = new BpmnDiagram();
        bpmnDiagram.setId(1L);
        bpmnDiagram.setName("Test Process");
        bpmnDiagram.setDescription("Test BPMN Diagram");
        bpmnDiagram.setBpmnContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bpmnDiagram.setIsActive(true);

        // Setup test artifact
        testArtifact = new TestArtifact();
        testArtifact.setId(1L);
        testArtifact.setArtifactName("Test Artifact");
        testArtifact.setArtifactDescription("Test Artifact Description");
        testArtifact.setArtifactType(TestArtifact.ArtifactType.OPENAPI_SPEC);
        testArtifact.setStatus(TestArtifact.ArtifactStatus.ACTIVE);
        testArtifact.setIsActive(true);
        testArtifact.setTestProject(testProject);
        testArtifact.setOpenApiSpec(openApiSpec);
    }

    @Test
    @DisplayName("Should get all artifacts successfully")
    void shouldGetAllArtifactsSuccessfully() {
        // Given
        List<TestArtifact> artifacts = Arrays.asList(testArtifact);
        when(testArtifactRepository.findAllByOrderByCreatedAtDesc()).thenReturn(artifacts);

        // When
        ApiResponse<List<TestArtifact>> result = artifactService.getAllArtifacts();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testArtifactRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return error when getting all artifacts fails")
    void shouldReturnErrorWhenGettingAllArtifactsFails() {
        // Given
        when(testArtifactRepository.findAllByOrderByCreatedAtDesc())
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<List<TestArtifact>> result = artifactService.getAllArtifacts();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch artifacts"));
        verify(testArtifactRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should get artifact by ID successfully")
    void shouldGetArtifactByIdSuccessfully() {
        // Given
        when(testArtifactRepository.findById(1L)).thenReturn(Optional.of(testArtifact));

        // When
        ApiResponse<TestArtifact> result = artifactService.getArtifactById(1L);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        verify(testArtifactRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return error when artifact not found by ID")
    void shouldReturnErrorWhenArtifactNotFoundById() {
        // Given
        when(testArtifactRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ApiResponse<TestArtifact> result = artifactService.getArtifactById(999L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Artifact not found", result.getMessage());
        verify(testArtifactRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get OpenAPI specs successfully")
    void shouldGetOpenApiSpecsSuccessfully() {
        // Given
        List<OpenApiSpec> specs = Arrays.asList(openApiSpec);
        when(openApiSpecRepository.findAllByOrderByCreatedAtDesc()).thenReturn(specs);

        // When
        ApiResponse<List<OpenApiSpec>> result = artifactService.getOpenApiSpecs();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(openApiSpecRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return error when getting OpenAPI specs fails")
    void shouldReturnErrorWhenGettingOpenApiSpecsFails() {
        // Given
        when(openApiSpecRepository.findAllByOrderByCreatedAtDesc())
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<List<OpenApiSpec>> result = artifactService.getOpenApiSpecs();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch OpenAPI specifications"));
        verify(openApiSpecRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should get BPMN diagrams successfully")
    void shouldGetBpmnDiagramsSuccessfully() {
        // Given
        List<BpmnDiagram> diagrams = Arrays.asList(bpmnDiagram);
        when(bpmnDiagramRepository.findAllByOrderByCreatedAtDesc()).thenReturn(diagrams);

        // When
        ApiResponse<List<BpmnDiagram>> result = artifactService.getBpmnDiagrams();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(bpmnDiagramRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return error when getting BPMN diagrams fails")
    void shouldReturnErrorWhenGettingBpmnDiagramsFails() {
        // Given
        when(bpmnDiagramRepository.findAllByOrderByCreatedAtDesc())
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<List<BpmnDiagram>> result = artifactService.getBpmnDiagrams();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch BPMN diagrams"));
        verify(bpmnDiagramRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should delete artifact successfully")
    void shouldDeleteArtifactSuccessfully() {
        // Given
        when(testArtifactRepository.findById(1L)).thenReturn(Optional.of(testArtifact));

        // When
        ApiResponse<String> result = artifactService.deleteArtifact(1L);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("Artifact deleted successfully", result.getMessage());
        verify(testArtifactRepository, times(1)).findById(1L);
        verify(testArtifactRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return error when deleting non-existent artifact")
    void shouldReturnErrorWhenDeletingNonExistentArtifact() {
        // Given
        when(testArtifactRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ApiResponse<String> result = artifactService.deleteArtifact(999L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Artifact not found", result.getMessage());
        verify(testArtifactRepository, times(1)).findById(999L);
        verify(testArtifactRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should handle exception when deleting artifact")
    void shouldHandleExceptionWhenDeletingArtifact() {
        // Given
        when(testArtifactRepository.findById(1L)).thenReturn(Optional.of(testArtifact));
        doThrow(new RuntimeException("Database error"))
                .when(testArtifactRepository).deleteById(1L);

        // When
        ApiResponse<String> result = artifactService.deleteArtifact(1L);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to delete artifact"));
        verify(testArtifactRepository, times(1)).findById(1L);
        verify(testArtifactRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle exception when getting artifact by ID")
    void shouldHandleExceptionWhenGettingArtifactById() {
        // Given
        when(testArtifactRepository.findById(1L))
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<TestArtifact> result = artifactService.getArtifactById(1L);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch artifact"));
        verify(testArtifactRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should handle exception when getting BPMN diagrams")
    void shouldHandleExceptionWhenGettingBpmnDiagrams() {
        // Given
        when(bpmnDiagramRepository.findAllByOrderByCreatedAtDesc())
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<List<BpmnDiagram>> result = artifactService.getBpmnDiagrams();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch BPMN diagrams"));
        verify(bpmnDiagramRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return empty list when no artifacts found")
    void shouldReturnEmptyListWhenNoArtifactsFound() {
        // Given
        when(testArtifactRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        // When
        ApiResponse<List<TestArtifact>> result = artifactService.getAllArtifacts();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().size());
        verify(testArtifactRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return empty list when no OpenAPI specs found")
    void shouldReturnEmptyListWhenNoOpenApiSpecsFound() {
        // Given
        when(openApiSpecRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        // When
        ApiResponse<List<OpenApiSpec>> result = artifactService.getOpenApiSpecs();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().size());
        verify(openApiSpecRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return empty list when no BPMN diagrams found")
    void shouldReturnEmptyListWhenNoBpmnDiagramsFound() {
        // Given
        when(bpmnDiagramRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList());

        // When
        ApiResponse<List<BpmnDiagram>> result = artifactService.getBpmnDiagrams();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().size());
        verify(bpmnDiagramRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should handle null pointer exception gracefully")
    void shouldHandleNullPointerExceptionGracefully() {
        // Given
        when(testArtifactRepository.findAllByOrderByCreatedAtDesc())
                .thenThrow(new NullPointerException("Null pointer"));

        // When
        ApiResponse<List<TestArtifact>> result = artifactService.getAllArtifacts();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch artifacts"));
        verify(testArtifactRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException gracefully")
    void shouldHandleIllegalArgumentExceptionGracefully() {
        // Given
        when(testArtifactRepository.findById(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid ID"));

        // When
        ApiResponse<TestArtifact> result = artifactService.getArtifactById(-1L);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch artifact"));
        verify(testArtifactRepository, times(1)).findById(-1L);
    }
}