package org.example.infrastructure.services;

import org.example.domain.dto.openapi.OpenApiAnalysisRequest;
import org.example.domain.dto.openapi.OpenApiAnalysisResponse;
import org.example.domain.dto.test.ApiResponse;
import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.OpenApiSpec;
import org.example.domain.entities.TestArtifact;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.infrastructure.repositories.BpmnDiagramRepository;
import org.example.infrastructure.repositories.OpenApiSpecRepository;
import org.example.infrastructure.repositories.TestArtifactRepository;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.infrastructure.services.openapi.OpenApiParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing test artifacts (OpenAPI specs and BPMN diagrams)
 */
@Service
@Transactional
public class ArtifactService {
    
    private static final Logger logger = LoggerFactory.getLogger(ArtifactService.class);
    
    private final OpenApiSpecRepository openApiSpecRepository;
    private final BpmnDiagramRepository bpmnDiagramRepository;
    private final TestArtifactRepository testArtifactRepository;
    
    @Autowired
    private OpenApiAnalysisService openApiAnalysisService;
    
    @Autowired
    private OpenApiParserService openApiParserService;
    
    public ArtifactService(OpenApiSpecRepository openApiSpecRepository,
                          BpmnDiagramRepository bpmnDiagramRepository,
                          TestArtifactRepository testArtifactRepository) {
        this.openApiSpecRepository = openApiSpecRepository;
        this.bpmnDiagramRepository = bpmnDiagramRepository;
        this.testArtifactRepository = testArtifactRepository;
    }
    
    /**
     * Get all artifacts
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<TestArtifact>> getAllArtifacts() {
        try {
            List<TestArtifact> artifacts = testArtifactRepository.findAllByOrderByCreatedAtDesc();
            return ApiResponse.success(artifacts);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch artifacts: " + e.getMessage());
        }
    }
    
    /**
     * Get artifact by ID
     */
    @Transactional(readOnly = true)
    public ApiResponse<TestArtifact> getArtifactById(Long id) {
        try {
            Optional<TestArtifact> artifactOpt = testArtifactRepository.findById(id);
            if (artifactOpt.isEmpty()) {
                return ApiResponse.error("Artifact not found");
            }
            return ApiResponse.success(artifactOpt.get());
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch artifact: " + e.getMessage());
        }
    }
    
    /**
     * Get OpenAPI specifications
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<OpenApiSpec>> getOpenApiSpecs() {
        try {
            List<OpenApiSpec> specs = openApiSpecRepository.findAllByOrderByCreatedAtDesc();
            return ApiResponse.success(specs);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch OpenAPI specifications: " + e.getMessage());
        }
    }
    
    /**
     * Get BPMN diagrams
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<BpmnDiagram>> getBpmnDiagrams() {
        try {
            List<BpmnDiagram> diagrams = bpmnDiagramRepository.findAllByOrderByCreatedAtDesc();
            return ApiResponse.success(diagrams);
        } catch (Exception e) {
            return ApiResponse.error("Failed to fetch BPMN diagrams: " + e.getMessage());
        }
    }
    
    /**
     * Delete artifact
     */
    public ApiResponse<String> deleteArtifact(Long id) {
        try {
            Optional<TestArtifact> artifactOpt = testArtifactRepository.findById(id);
            if (artifactOpt.isEmpty()) {
                return ApiResponse.error("Artifact not found");
            }
            
            testArtifactRepository.deleteById(id);
            return ApiResponse.success("Artifact deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete artifact: " + e.getMessage());
        }
    }
    
    /**
     * Start OpenAPI analysis for a specific artifact
     */
    @Transactional
    public ApiResponse<OpenApiAnalysisResponse> startOpenApiAnalysis(Long artifactId) {
        try {
            Optional<TestArtifact> artifactOpt = testArtifactRepository.findById(artifactId);
            if (artifactOpt.isEmpty()) {
                return ApiResponse.error("Artifact not found");
            }
            
            TestArtifact artifact = artifactOpt.get();
            
            // For now, we'll assume all artifacts can be analyzed
            // In a real implementation, you'd check artifact.getType() if it exists
            
            // Create analysis request
            OpenApiAnalysisRequest request = OpenApiAnalysisRequest.createBasic(
                artifactId.toString(), 
                artifact.getArtifactName() != null ? artifact.getArtifactName() : "Unknown Artifact"
            );
            
            // Create response with analysis ID
            OpenApiAnalysisResponse response = OpenApiAnalysisResponse.createMock(
                "analysis_" + System.currentTimeMillis(), 
                artifact.getArtifactName() != null ? artifact.getArtifactName() : "API Specification"
            );
            
            logger.info("Started OpenAPI analysis for artifact: {}", artifactId);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            logger.error("Failed to start OpenAPI analysis for artifact: {}", artifactId, e);
            return ApiResponse.error("Failed to start analysis: " + e.getMessage());
        }
    }
    
    /**
     * Get OpenAPI analysis status and results
     */
    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getAnalysisStatus(String specId) {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("specId", specId);
            status.put("status", "IN_PROGRESS");
            status.put("lastUpdated", LocalDateTime.now());
            
            return ApiResponse.success(status);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to get analysis status: " + e.getMessage());
        }
    }
    
    /**
     * Get analysis results
     */
    @Transactional(readOnly = true)
    public ApiResponse<OpenApiAnalysisResponse> getAnalysisResults(String specId) {
        try {
            OpenApiAnalysisResponse response = OpenApiAnalysisResponse.createMock(
                "analysis_" + specId, 
                "API Specification " + specId
            );
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to get analysis results: " + e.getMessage());
        }
    }
    
    /**
     * Regenerate analysis
     */
    @Transactional
    public ApiResponse<OpenApiAnalysisResponse> regenerateAnalysis(String specId) {
        try {
            // This would clear previous analysis and start a new one
            OpenApiAnalysisResponse response = OpenApiAnalysisResponse.createMock(
                "analysis_" + System.currentTimeMillis(), 
                "Regenerated Analysis for " + specId
            );
            
            logger.info("Regenerating analysis for spec: {}", specId);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to regenerate analysis: " + e.getMessage());
        }
    }
    
    // Helper methods
    
    private OpenApiSpecification convertToOpenApiSpecification(TestArtifact artifact) {
        // Convert TestArtifact to OpenApiSpecification for analysis
        OpenApiSpecification spec = new OpenApiSpecification();
        spec.setTitle(artifact.getArtifactName() != null ? artifact.getArtifactName() : "Untitled");
        spec.setDescription("Converted from TestArtifact: " + artifact.getArtifactName());
        
        // Set other properties as needed
        return spec;
    }
}