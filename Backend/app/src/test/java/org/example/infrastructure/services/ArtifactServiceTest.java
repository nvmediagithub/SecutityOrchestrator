package org.example.infrastructure.services;

import org.example.domain.dto.openapi.OpenApiAnalysisResponse;
import org.example.domain.dto.test.ApiResponse;
import org.example.domain.entities.TestArtifact;
import org.example.infrastructure.repositories.BpmnDiagramRepository;
import org.example.infrastructure.repositories.OpenApiSpecRepository;
import org.example.infrastructure.repositories.TestArtifactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ArtifactService
 */
@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    private OpenApiSpecRepository openApiSpecRepository;

    @Mock
    private BpmnDiagramRepository bpmnDiagramRepository;

    @Mock
    private TestArtifactRepository testArtifactRepository;

    @Mock
    private org.example.infrastructure.services.llm.OpenApiAnalysisService openApiAnalysisService;

    @Mock
    private org.example.infrastructure.services.openapi.OpenApiParserService openApiParserService;

    @InjectMocks
    private ArtifactService artifactService;

    private TestArtifact testArtifact;
    private Long testArtifactId;

    @BeforeEach
    void setUp() {
        testArtifactId = 1L;
        
        // Create test artifact
        testArtifact = new TestArtifact();
        testArtifact.setId(testArtifactId);
        testArtifact.setArtifactName("Test OpenAPI Specification");
        testArtifact.setArtifactDescription("A test API specification");
        testArtifact.setActive(true);
    }

    @Test
    void getAllArtifacts_ShouldReturnAllArtifacts() {
        // Arrange
        List<TestArtifact> expectedArtifacts = List.of(testArtifact);
        when(testArtifactRepository.findAllByOrderByCreatedAtDesc())
            .thenReturn(expectedArtifacts);

        // Act
        ApiResponse<List<TestArtifact>> result = artifactService.getAllArtifacts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(expectedArtifacts, result.getData());
        verify(testArtifactRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getArtifactById_WithExistingId_ShouldReturnArtifact() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenReturn(Optional.of(testArtifact));

        // Act
        ApiResponse<TestArtifact> result = artifactService.getArtifactById(testArtifactId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(testArtifact, result.getData());
        verify(testArtifactRepository).findById(testArtifactId);
    }

    @Test
    void getArtifactById_WithNonExistingId_ShouldReturnError() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenReturn(Optional.empty());

        // Act
        ApiResponse<TestArtifact> result = artifactService.getArtifactById(testArtifactId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(testArtifactRepository).findById(testArtifactId);
    }

    @Test
    void deleteArtifact_WithExistingId_ShouldReturnSuccess() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenReturn(Optional.of(testArtifact));

        // Act
        ApiResponse<String> result = artifactService.deleteArtifact(testArtifactId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().contains("successfully"));
        verify(testArtifactRepository).findById(testArtifactId);
        verify(testArtifactRepository).deleteById(testArtifactId);
    }

    @Test
    void deleteArtifact_WithNonExistingId_ShouldReturnError() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenReturn(Optional.empty());

        // Act
        ApiResponse<String> result = artifactService.deleteArtifact(testArtifactId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(testArtifactRepository).findById(testArtifactId);
        verify(testArtifactRepository, never()).deleteById(any());
    }

    @Test
    void startOpenApiAnalysis_WithExistingArtifact_ShouldStartAnalysis() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenReturn(Optional.of(testArtifact));

        // Act
        ApiResponse<OpenApiAnalysisResponse> result = artifactService.startOpenApiAnalysis(testArtifactId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getAnalysisId());
        assertTrue(result.getData().getAnalysisId().contains("analysis_"));
        verify(testArtifactRepository).findById(testArtifactId);
    }

    @Test
    void startOpenApiAnalysis_WithNonExistingArtifact_ShouldReturnError() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenReturn(Optional.empty());

        // Act
        ApiResponse<OpenApiAnalysisResponse> result = artifactService.startOpenApiAnalysis(testArtifactId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(testArtifactRepository).findById(testArtifactId);
    }

    @Test
    void getAnalysisStatus_WithValidSpecId_ShouldReturnStatus() {
        // Arrange
        String specId = testArtifactId.toString();

        // Act
        ApiResponse<java.util.Map<String, Object>> result = artifactService.getAnalysisStatus(specId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(specId, result.getData().get("specId"));
        assertEquals("IN_PROGRESS", result.getData().get("status"));
        assertNotNull(result.getData().get("lastUpdated"));
    }

    @Test
    void getAnalysisResults_WithValidSpecId_ShouldReturnResults() {
        // Arrange
        String specId = testArtifactId.toString();

        // Act
        ApiResponse<OpenApiAnalysisResponse> result = artifactService.getAnalysisResults(specId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getAnalysisId());
        assertTrue(result.getData().getAnalysisId().contains(specId));
    }

    @Test
    void regenerateAnalysis_WithValidSpecId_ShouldReturnNewAnalysis() {
        // Arrange
        String specId = testArtifactId.toString();

        // Act
        ApiResponse<OpenApiAnalysisResponse> result = artifactService.regenerateAnalysis(specId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getAnalysisId());
        assertTrue(result.getData().getAnalysisId().contains("Regenerated"));
    }

    @Test
    void getOpenApiSpecs_ShouldReturnAllSpecs() {
        // Arrange
        org.example.domain.entities.OpenApiSpec mockSpec = new org.example.domain.entities.OpenApiSpec();
        List<org.example.domain.entities.OpenApiSpec> expectedSpecs = List.of(mockSpec);
        when(openApiSpecRepository.findAllByOrderByCreatedAtDesc())
            .thenReturn(expectedSpecs);

        // Act
        ApiResponse<List<org.example.domain.entities.OpenApiSpec>> result = artifactService.getOpenApiSpecs();

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(expectedSpecs, result.getData());
        verify(openApiSpecRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getBpmnDiagrams_ShouldReturnAllDiagrams() {
        // Arrange
        org.example.domain.entities.BpmnDiagram mockDiagram = new org.example.domain.entities.BpmnDiagram();
        List<org.example.domain.entities.BpmnDiagram> expectedDiagrams = List.of(mockDiagram);
        when(bpmnDiagramRepository.findAllByOrderByCreatedAtDesc())
            .thenReturn(expectedDiagrams);

        // Act
        ApiResponse<List<org.example.domain.entities.BpmnDiagram>> result = artifactService.getBpmnDiagrams();

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(expectedDiagrams, result.getData());
        verify(bpmnDiagramRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getAllArtifacts_WithException_ShouldReturnError() {
        // Arrange
        when(testArtifactRepository.findAllByOrderByCreatedAtDesc())
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<List<TestArtifact>> result = artifactService.getAllArtifacts();

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch"));
    }

    @Test
    void getArtifactById_WithException_ShouldReturnError() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<TestArtifact> result = artifactService.getArtifactById(testArtifactId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch"));
    }

    @Test
    void deleteArtifact_WithException_ShouldReturnError() {
        // Arrange
        when(testArtifactRepository.findById(testArtifactId))
            .thenReturn(Optional.of(testArtifact));
        doThrow(new RuntimeException("Delete error"))
            .when(testArtifactRepository).deleteById(testArtifactId);

        // Act
        ApiResponse<String> result = artifactService.deleteArtifact(testArtifactId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to delete"));
    }

    @Test
    void convertToOpenApiSpecification_ShouldConvertArtifact() {
        // Act
        var result = artifactService.convertToOpenApiSpecification(testArtifact);

        // Assert
        assertNotNull(result);
        assertEquals(testArtifact.getArtifactName(), result.getTitle());
        assertTrue(result.getDescription().contains(testArtifact.getArtifactName()));
    }
}