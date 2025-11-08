package org.example.web.controllers;

import org.example.domain.dto.bpmn.*;
import org.example.domain.entities.BpmnDiagram;
import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * REST controller for BPMN analysis endpoints
 * Following the pattern of OpenApiAnalysisController
 */
@RestController
@RequestMapping("/api/analysis/bpmn")
@CrossOrigin(origins = "*")
public class BpmnAnalysisController {
    
    @Autowired
    private BpmnAnalysisService analysisService;
    
    @Autowired
    private BpmnApiMapper apiMapper;
    
    /**
     * POST /api/analysis/bpmn/{diagramId} - Запуск анализа BPMN диаграммы
     * Initiates BPMN diagram analysis with various analysis types
     */
    @PostMapping("/{diagramId}")
    public CompletableFuture<ResponseEntity<BpmnAnalysisStatusResponse>> startAnalysis(
            @PathVariable String diagramId,
            @Valid @RequestBody BpmnAnalysisRequestDto request) {
        
        try {
            // Validation
            if (request == null || !request.isValid() || !diagramId.equals(request.getDiagramId())) {
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().build()
                );
            }
            
            // Create BPMN diagram from request
            BpmnDiagram diagram = createDiagramFromRequest(request);
            
            // Start analysis asynchronously
            CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture = 
                analysisService.analyzeBpmnDiagram(diagramId, diagram);
            
            // Return immediate response with analysis ID
            BpmnAnalysisStatusResponse response = BpmnAnalysisStatusResponse.createMock(
                diagramId, request.getDiagramName()
            );
            
            return CompletableFuture.completedFuture(
                ResponseEntity.accepted().body(response)
            );
            
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            );
        }
    }
    
    /**
     * GET /api/analysis/bpmn/{diagramId} - Получение результатов анализа
     * Returns analysis status and results with long polling support
     */
    @GetMapping("/{diagramId}")
    public ResponseEntity<BpmnAnalysisStatusResponse> getAnalysisResults(
            @PathVariable String diagramId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "false") boolean includeMetadata) {
        
        try {
            // Check if analysis is in progress
            String analysisId = "bpmn_analysis_" + diagramId + "_" + System.currentTimeMillis();
            BpmnAnalysisService.BpmnAnalysisStatus serviceStatus = analysisService.getAnalysisStatus(analysisId);
            
            if (serviceStatus != null) {
                BpmnAnalysisStatusResponse response = 
                    BpmnAnalysisStatusResponse.fromServiceStatus(diagramId, serviceStatus);
                return ResponseEntity.ok(response);
            }
            
            // Return mock completed analysis
            BpmnAnalysisStatusResponse response = BpmnAnalysisStatusResponse.createMock(
                diagramId, "BPMN Diagram " + diagramId
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/analysis/bpmn/{diagramId}/mappings - Получение связей с API
     * Returns BPMN to API mappings
     */
    @GetMapping("/{diagramId}/mappings")
    public ResponseEntity<BpmnMappingsResponseDto> getApiMappings(
            @PathVariable String diagramId,
            @RequestParam(required = false) String apiSpecId,
            @RequestParam(required = false, defaultValue = "all") String filter,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size) {
        
        try {
            // For now, return mock data - in real implementation, would query BpmnApiMapper
            BpmnMappingsResponseDto response = BpmnMappingsResponseDto.createMock(
                diagramId, apiSpecId != null ? apiSpecId : "default-api-spec"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/analysis/bpmn/{diagramId}/issues - Получение проблем процесса
     * Returns process issues and problems
     */
    @GetMapping("/{diagramId}/issues")
    public ResponseEntity<List<Map<String, Object>>> getProcessIssues(
            @PathVariable String diagramId,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "all") String status) {
        
        try {
            List<Map<String, Object>> issues = Arrays.asList(
                createIssue("STRUCTURE_001", "Некорректная структура процесса", "HIGH", "structure", 
                    "Процесс содержит элементы с неправильными связями"),
                createIssue("SECURITY_001", "Отсутствует валидация входных данных", "MEDIUM", "security",
                    "Процесс не содержит проверки безопасности в точках ввода"),
                createIssue("PERFORMANCE_001", "Потенциальное узкое место", "LOW", "performance",
                    "Синхронная обработка может вызвать задержки"),
                createIssue("COMPLIANCE_001", "Несоответствие бизнес-правилам", "HIGH", "compliance",
                    "Процесс не учитывает требования комплаенса")
            );
            
            // Filter by severity if specified
            if (severity != null && !severity.equals("all")) {
                issues = issues.stream()
                    .filter(issue -> severity.equalsIgnoreCase((String) issue.get("severity")))
                    .collect(Collectors.toList());
            }
            
            // Filter by type if specified
            if (type != null && !type.equals("all")) {
                issues = issues.stream()
                    .filter(issue -> type.equalsIgnoreCase((String) issue.get("type")))
                    .collect(Collectors.toList());
            }
            
            return ResponseEntity.ok(issues);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/analysis/bpmn/{diagramId}/map-api - Создание/обновление маппинга BPMN-API
     * Creates or updates BPMN to API mappings
     */
    @PostMapping("/{diagramId}/map-api")
    public ResponseEntity<Map<String, Object>> createOrUpdateApiMapping(
            @PathVariable String diagramId,
            @Valid @RequestBody BpmnApiMappingRequestDto request) {
        
        try {
            // Validation
            if (request == null || !request.isValid() || !diagramId.equals(request.getDiagramId())) {
                return ResponseEntity.badRequest().build();
            }
            
            // In real implementation, would use BpmnApiMapper to create mappings
            Map<String, Object> response = new HashMap<>();
            response.put("mappingId", "mapping_" + diagramId + "_" + System.currentTimeMillis());
            response.put("diagramId", diagramId);
            response.put("apiSpecId", request.getApiSpecId());
            response.put("status", "CREATED");
            response.put("mappingsCreated", request.getMappings().size());
            response.put("createdAt", new Date());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/analysis/bpmn/{diagramId}/gaps - Получение разрывов между API и процессом
     * Returns gaps between BPMN processes and available APIs
     */
    @GetMapping("/{diagramId}/gaps")
    public ResponseEntity<BpmnGapsResponse> getProcessApiGaps(
            @PathVariable String diagramId,
            @RequestParam(required = false) String apiSpecId,
            @RequestParam(required = false, defaultValue = "all") String gapType,
            @RequestParam(required = false, defaultValue = "all") String severity) {
        
        try {
            // Return mock gap analysis
            BpmnGapsResponse response = BpmnGapsResponse.createMock(
                diagramId, apiSpecId != null ? apiSpecId : "default-api-spec"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/analysis/bpmn/{diagramId}/regenerate - Перезапуск анализа
     * Regenerates BPMN analysis with new parameters
     */
    @PostMapping("/{diagramId}/regenerate")
    public ResponseEntity<BpmnAnalysisStatusResponse> regenerateAnalysis(
            @PathVariable String diagramId,
            @Valid @RequestBody BpmnRegenerateRequestDto request) {
        
        try {
            // Validation
            if (request == null || !request.isValid() || !diagramId.equals(request.getDiagramId())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Start new analysis
            BpmnAnalysisStatusResponse response = BpmnAnalysisStatusResponse.createMock(
                diagramId, "Regenerated Analysis for " + diagramId
            );
            
            // Add regeneration metadata
            if (response.getAnalysisMetadata() != null) {
                response.getAnalysisMetadata().put("regenerated", true);
                response.getAnalysisMetadata().put("regenerationReason", request.getReason());
                response.getAnalysisMetadata().put("regeneratedAt", new Date());
            }
            
            return ResponseEntity.accepted().body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/analysis/bpmn/{diagramId}/security - Проверки безопасности
     * Returns security analysis report
     */
    @GetMapping("/{diagramId}/security")
    public ResponseEntity<BpmnSecurityReportResponse> getSecurityAnalysis(
            @PathVariable String diagramId,
            @RequestParam(required = false, defaultValue = "comprehensive") String analysisLevel,
            @RequestParam(required = false) List<String> securityStandards,
            @RequestParam(required = false, defaultValue = "all") String vulnerabilityType) {
        
        try {
            // Return mock security report
            BpmnSecurityReportResponse response = BpmnSecurityReportResponse.createMock(diagramId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/analysis/bpmn/{diagramId}/performance - Метрики производительности
     * Returns performance analysis report
     */
    @GetMapping("/{diagramId}/performance")
    public ResponseEntity<BpmnPerformanceReportResponse> getPerformanceAnalysis(
            @PathVariable String diagramId,
            @RequestParam(required = false, defaultValue = "comprehensive") String analysisLevel,
            @RequestParam(required = false) List<String> metrics,
            @RequestParam(required = false, defaultValue = "all") String bottleneckType) {
        
        try {
            // Return mock performance report
            BpmnPerformanceReportResponse response = BpmnPerformanceReportResponse.createMock(diagramId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/analysis/bpmn/{diagramId}/summary - Краткая сводка анализа
     * Returns analysis summary with key metrics
     */
    @GetMapping("/{diagramId}/summary")
    public ResponseEntity<Map<String, Object>> getAnalysisSummary(@PathVariable String diagramId) {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("diagramId", diagramId);
            summary.put("totalElements", 15);
            summary.put("analyzedElements", 15);
            summary.put("totalIssues", 6);
            summary.put("criticalIssues", 1);
            summary.put("highIssues", 2);
            summary.put("mediumIssues", 2);
            summary.put("lowIssues", 1);
            summary.put("securityScore", 7.2);
            summary.put("performanceScore", 7.3);
            summary.put("overallScore", 7.25);
            summary.put("grade", "B");
            summary.put("analysisTime", "3.2s");
            summary.put("lastAnalyzed", new Date());
            
            List<String> recommendations = Arrays.asList(
                "Implement input validation for all user tasks",
                "Add database connection pooling for better performance",
                "Review and optimize complex JOIN operations",
                "Implement proper error handling and retry mechanisms",
                "Add security checks for external API calls"
            );
            summary.put("topRecommendations", recommendations);
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Private helper methods
    
    private BpmnDiagram createDiagramFromRequest(BpmnAnalysisRequestDto request) {
        BpmnDiagram diagram = new BpmnDiagram();
        diagram.setDiagramId(request.getDiagramId());
        diagram.setName(request.getDiagramName());
        diagram.setBpmnContent(request.getBpmnContent());
        diagram.setDescription(request.getDescription());
        diagram.setVersion(request.getVersion());
        diagram.setCreatedAt(new Date());
        diagram.setLastModified(new Date());
        return diagram;
    }
    
    private Map<String, Object> createIssue(String id, String title, String severity, String type, String description) {
        Map<String, Object> issue = new HashMap<>();
        issue.put("id", id);
        issue.put("title", title);
        issue.put("severity", severity);
        issue.put("type", type);
        issue.put("description", description);
        issue.put("category", "bpmn_analysis");
        issue.put("confidence", 0.85);
        issue.put("location", "Process: " + "diagram_" + System.currentTimeMillis());
        issue.put("detectedAt", new Date());
        return issue;
    }
}