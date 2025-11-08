import org.example.domain.dto.openapi.OpenApiAnalysisResponse;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.springframework.web.bind.annotation.PathVariable;
package org.example.web.controllers;

import org.example.domain.dto.test.ApiResponse;
import org.example.domain.dto.test.BpmnDiagramUploadRequest;
import org.example.domain.dto.test.OpenApiSpecUploadRequest;
import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.OpenApiSpec;
import org.example.domain.entities.TestArtifact;
import org.example.domain.entities.TestProject;
import org.example.domain.valueobjects.OpenApiVersion;
import org.example.infrastructure.services.ArtifactService;
import org.example.infrastructure.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.example.domain.dto.openapi.OpenApiAnalysisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing test artifacts
 * Provides endpoints for OpenAPI specs and BPMN diagrams
 */
@RestController
@RequestMapping("/api/artifacts")
public class ArtifactController {
    
    private final ArtifactService artifactService;
    private final ProjectService projectService;
    
    public ArtifactController(ArtifactService artifactService, ProjectService projectService) {
        this.artifactService = artifactService;
        this.projectService = projectService;
    }
    
    // ==================== OPENAPI SPEC MANAGEMENT ====================
    
    /**
     * Upload OpenAPI specification
     * POST /api/artifacts/openapi
     */
    @PostMapping("/openapi")
    public ResponseEntity<ApiResponse<TestArtifact>> uploadOpenApiSpec(
            @Valid @RequestPart("spec") OpenApiSpecUploadRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Create OpenAPI specification
            OpenApiSpec spec = new OpenApiSpec(
                request.getTitle(),
                OpenApiVersion.valueOf(request.getOpenApiVersion().toUpperCase()),
                request.getOpenApiContent()
            );
            
            // Set additional properties
            if (request.getDescription() != null) spec.setDescription(request.getDescription());
            if (request.getFileName() != null) spec.setFileName(request.getFileName());
            if (request.getVersion() != null) {
                try {
                    String[] parts = request.getVersion().split("\\.");
                    int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 1;
                    int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                    spec.setVersion(new org.example.domain.valueobjects.Version(major, minor, patch));
                } catch (NumberFormatException e) {
                    // Use default version if parsing fails
                    spec.setVersion(new org.example.domain.valueobjects.Version(1, 0, 0));
                }
            }
            if (request.getFileSize() != null) spec.setFileSize(request.getFileSize());
            if (request.getContentType() != null) spec.setContentType(request.getContentType());
            if (request.getServerUrls() != null) spec.setServerUrls(request.getServerUrls());
            if (request.getComponents() != null) spec.setComponents(request.getComponents());
            
            // Create artifact
            TestArtifact artifact = new TestArtifact();
            artifact.setOpenApiSpec(spec);
            artifact.setArtifactType(TestArtifact.ArtifactType.OPENAPI_SPEC);
            artifact.setArtifactName(request.getTitle());
            artifact.setArtifactDescription(request.getDescription());
            if (request.getUploadedBy() != null) artifact.setUploadedBy(request.getUploadedBy());
            
            // Associate with project if provided
            if (request.getProjectId() != null) {
                Long projectId = Long.parseLong(request.getProjectId());
                TestProject project = projectService.getProjectById(projectId).getData();
                if (project != null) {
                    artifact.setTestProject(project);
                }
            }
            
            // Here you would save the artifact through a service
            // For now, return a mock response
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("OpenAPI spec uploaded successfully", artifact));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to upload OpenAPI spec: " + e.getMessage()));
        }
    }
    
    /**
     * Get OpenAPI specifications
     * GET /api/artifacts/openapi
     */
    @GetMapping("/openapi")
    public ResponseEntity<ApiResponse<List<OpenApiSpec>>> getOpenApiSpecs() {
        ApiResponse<List<OpenApiSpec>> response = artifactService.getOpenApiSpecs();
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get OpenAPI specification by ID
     * GET /api/artifacts/openapi/{id}
     */
    @GetMapping("/openapi/{id}")
    public ResponseEntity<ApiResponse<OpenApiSpec>> getOpenApiSpecById(@PathVariable Long id) {
        // This would require a new method in ArtifactService
        // For now, return a mock response
        return ResponseEntity.ok(ApiResponse.error("Not implemented yet"));
    }
    
    // ==================== BPMN DIAGRAM MANAGEMENT ====================
    
    /**
     * Upload BPMN diagram
     * POST /api/artifacts/bpmn
     */
    @PostMapping("/bpmn")
    public ResponseEntity<ApiResponse<TestArtifact>> uploadBpmnDiagram(
            @Valid @RequestPart("diagram") BpmnDiagramUploadRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Create BPMN diagram
            BpmnDiagram diagram = new BpmnDiagram(request.getName(), request.getBpmnContent());
            
            // Set additional properties
            if (request.getDescription() != null) diagram.setDescription(request.getDescription());
            if (request.getFileName() != null) diagram.setFileName(request.getFileName());
            if (request.getVersion() != null) diagram.setVersion(request.getVersion());
            if (request.getDiagramType() != null) diagram.setDiagramType(request.getDiagramType());
            if (request.getFileSize() != null) diagram.setFileSize(request.getFileSize());
            if (request.getContentType() != null) diagram.setContentType(request.getContentType());
            if (request.getProcessDefinitionId() != null) diagram.setProcessDefinitionId(request.getProcessDefinitionId());
            if (request.getProcessEngine() != null) diagram.setProcessEngine(request.getProcessEngine());
            if (request.getTargetNamespace() != null) diagram.setTargetNamespace(request.getTargetNamespace());
            if (request.getExecutable() != null) diagram.setExecutable(request.getExecutable());
            
            // Create artifact
            TestArtifact artifact = new TestArtifact();
            artifact.setBpmnDiagram(diagram);
            artifact.setArtifactType(TestArtifact.ArtifactType.BPMN_DIAGRAM);
            artifact.setArtifactName(request.getName());
            artifact.setArtifactDescription(request.getDescription());
            if (request.getUploadedBy() != null) artifact.setUploadedBy(request.getUploadedBy());
            
            // Associate with project if provided
            if (request.getProjectId() != null) {
                Long projectId = Long.parseLong(request.getProjectId());
                TestProject project = projectService.getProjectById(projectId).getData();
                if (project != null) {
                    artifact.setTestProject(project);
                }
            }
            
            // Here you would save the artifact through a service
            // For now, return a mock response
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("BPMN diagram uploaded successfully", artifact));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to upload BPMN diagram: " + e.getMessage()));
        }
    }
    
    /**
     * Get BPMN diagrams
     * GET /api/artifacts/bpmn
     */
    @GetMapping("/bpmn")
    public ResponseEntity<ApiResponse<List<BpmnDiagram>>> getBpmnDiagrams() {
        ApiResponse<List<BpmnDiagram>> response = artifactService.getBpmnDiagrams();
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get BPMN diagram by ID
     * GET /api/artifacts/bpmn/{id}
     */
    @GetMapping("/bpmn/{id}")
    public ResponseEntity<ApiResponse<BpmnDiagram>> getBpmnDiagramById(@PathVariable Long id) {
        // This would require a new method in ArtifactService
        // For now, return a mock response
        return ResponseEntity.ok(ApiResponse.error("Not implemented yet"));
    }
    
    // ==================== GENERIC ARTIFACT MANAGEMENT ====================
    
    /**
     * Get all artifacts
     * GET /api/artifacts
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestArtifact>>> getAllArtifacts(
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        ApiResponse<List<TestArtifact>> response = artifactService.getAllArtifacts();
        if (response.isSuccess()) {
            // Filter inactive artifacts if requested
            if (!includeInactive) {
                List<TestArtifact> activeArtifacts = response.getData().stream()
                    .filter(TestArtifact::isActive)
                    .collect(java.util.stream.Collectors.toList());
                return ResponseEntity.ok(ApiResponse.success(activeArtifacts));
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get artifact by ID
     * GET /api/artifacts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestArtifact>> getArtifactById(@PathVariable Long id) {
        ApiResponse<TestArtifact> response = artifactService.getArtifactById(id);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }
    
    /**
     * Get artifacts by project
     * GET /api/artifacts/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TestArtifact>>> getArtifactsByProject(@PathVariable Long projectId) {
        try {
            // This would require a new method in ArtifactService
            // For now, return a mock response
            return ResponseEntity.ok(ApiResponse.error("Not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch artifacts by project: " + e.getMessage()));
        }
    }
    
    /**
     * Get artifacts by type
     * GET /api/artifacts/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<TestArtifact>>> getArtifactsByType(@PathVariable String type) {
        try {
            // This would require a new method in ArtifactService
            // For now, return a mock response
            return ResponseEntity.ok(ApiResponse.error("Not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to fetch artifacts by type: " + e.getMessage()));
        }
    }
    
    /**
     * Search artifacts
     * GET /api/artifacts/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TestArtifact>>> searchArtifacts(
            @RequestParam String query,
            @RequestParam(defaultValue = "name") String searchBy) {
        try {
            // This would require a new method in ArtifactService
            // For now, return a mock response
            return ResponseEntity.ok(ApiResponse.error("Not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to search artifacts: " + e.getMessage()));
        }
    }
    
    /**
     * Delete artifact
     * DELETE /api/artifacts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteArtifact(@PathVariable Long id) {
        ApiResponse<String> response = artifactService.deleteArtifact(id);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }
    
    /**
     * Update artifact metadata
     * PUT /api/artifacts/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestArtifact>> updateArtifact(
            @PathVariable Long id,
            @RequestBody TestArtifact updatedArtifact) {
        try {
            // This would require a new method in ArtifactService
            // For now, return a mock response
            return ResponseEntity.ok(ApiResponse.error("Not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update artifact: " + e.getMessage()));
        }
    }
    
    /**
     * Get artifact statistics
     * GET /api/artifacts/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getArtifactStats() {
        try {
            // This would require a new method in ArtifactService
            // For now, return a mock response
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("total", 0);
            stats.put("active", 0);
            stats.put("openapi_specs", 0);
            stats.put("bpmn_diagrams", 0);
            
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
    
    // ==================== OPENAPI ANALYSIS ENDPOINTS ====================
    
    /**
     * Start OpenAPI analysis for a specific artifact
     * POST /api/artifacts/openapi/{id}/analyze
     */
    @PostMapping("/openapi/{id}/analyze")
    public ResponseEntity<ApiResponse<OpenApiAnalysisResponse>> startOpenApiAnalysis(@PathVariable Long id) {
        try {
            ApiResponse<OpenApiAnalysisResponse> response = artifactService.startOpenApiAnalysis(id);
            if (response.isSuccess()) {
                return ResponseEntity.accepted().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to start OpenAPI analysis: " + e.getMessage()));
        }
    }
    
    /**
     * Get analysis status for OpenAPI artifact
     * GET /api/artifacts/openapi/{id}/analysis/status
     */
    @GetMapping("/openapi/{id}/analysis/status")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getAnalysisStatus(@PathVariable Long id) {
        try {
            ApiResponse<java.util.Map<String, Object>> response = artifactService.getAnalysisStatus(id.toString());
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get analysis status: " + e.getMessage()));
        }
    }
    
    /**
     * Get analysis results for OpenAPI artifact
     * GET /api/artifacts/openapi/{id}/analysis/results
     */
    @GetMapping("/openapi/{id}/analysis/results")
    public ResponseEntity<ApiResponse<OpenApiAnalysisResponse>> getAnalysisResults(@PathVariable Long id) {
        try {
            ApiResponse<OpenApiAnalysisResponse> response = artifactService.getAnalysisResults(id.toString());
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get analysis results: " + e.getMessage()));
        }
    }
    
    /**
     * Regenerate analysis for OpenAPI artifact
     * POST /api/artifacts/openapi/{id}/analysis/regenerate
     */
    @PostMapping("/openapi/{id}/analysis/regenerate")
    public ResponseEntity<ApiResponse<OpenApiAnalysisResponse>> regenerateAnalysis(@PathVariable Long id) {
        try {
            ApiResponse<OpenApiAnalysisResponse> response = artifactService.regenerateAnalysis(id.toString());
            if (response.isSuccess()) {
                return ResponseEntity.accepted().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to regenerate analysis: " + e.getMessage()));
        }
    }
}
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get artifact statistics: " + e.getMessage()));
        }
    }
}