package org.example.web.controllers;

import org.example.domain.dto.reports.*;
import org.example.domain.valueobjects.BpmnProcessId;
import org.example.infrastructure.services.reporting.ReportingService;
import org.example.infrastructure.services.reporting.VisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for BPMN reporting and visualization endpoints
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReportController {
    
    @Autowired
    private ReportingService reportingService;
    
    @Autowired
    private VisualizationService visualizationService;
    
    /**
     * Generate BPMN analysis report
     * POST /api/reports/bpmn/{diagramId}
     */
    @PostMapping("/reports/bpmn/{diagramId}")
    public CompletableFuture<ResponseEntity<ReportResponse>> generateReport(
            @PathVariable String diagramId,
            @RequestBody ReportRequest request) {
        
        try {
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            request.setDiagramId(processId);
            
            ReportResponse response = null;
            
            switch (request.getReportType()) {
                case PROCESS_API_MAPPING:
                    response = reportingService.generateProcessApiMappingReport(processId, request);
                    break;
                case BUSINESS_PROCESS_ANALYSIS:
                    response = reportingService.generateBusinessProcessAnalysisReport(processId, request);
                    break;
                case API_PROCESS_COMPLIANCE:
                    response = reportingService.generateApiProcessComplianceReport(processId, request);
                    break;
                case PROCESS_SECURITY_ASSESSMENT:
                    response = reportingService.generateProcessSecurityAssessmentReport(processId, request);
                    break;
                case COMPLIANCE_REPORT:
                    response = reportingService.generateComplianceReport(processId, request);
                    break;
                case EXECUTIVE_SUMMARY:
                    response = reportingService.generateExecutiveSummaryReport(processId, request);
                    break;
                default:
                    return CompletableFuture.completedFuture(
                        ResponseEntity.badRequest().body(
                            createErrorResponse("Unsupported report type: " + request.getReportType())
                        )
                    );
            }
            
            return CompletableFuture.completedFuture(ResponseEntity.ok(response));
            
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    createErrorResponse("Failed to generate report: " + e.getMessage())
                )
            );
        }
    }
    
    /**
     * Get existing report
     * GET /api/reports/bpmn/{diagramId}/{reportType}
     */
    @GetMapping("/reports/bpmn/{diagramId}/{reportType}")
    public ResponseEntity<ReportResponse> getReport(
            @PathVariable String diagramId,
            @PathVariable String reportType) {
        
        try {
            // In a real implementation, this would fetch from a database
            // For now, return a mock response
            ReportResponse response = createMockReport(diagramId, reportType);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve report: " + e.getMessage()));
        }
    }
    
    /**
     * Get dashboard visualization data
     * GET /api/visualization/bpmn/{diagramId}/dashboard
     */
    @GetMapping("/visualization/bpmn/{diagramId}/dashboard")
    public ResponseEntity<DashboardMetrics> getDashboardMetrics(@PathVariable String diagramId) {
        try {
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            DashboardMetrics metrics = visualizationService.generateDashboardMetrics(processId);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorMetrics("Failed to generate dashboard metrics: " + e.getMessage()));
        }
    }
    
    /**
     * Generate process flow visualization
     * GET /api/visualization/bpmn/{diagramId}/flow
     */
    @GetMapping("/visualization/bpmn/{diagramId}/flow")
    public ResponseEntity<VisualizationData> getProcessFlowVisualization(@PathVariable String diagramId) {
        try {
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            VisualizationData data = visualizationService.generateProcessFlowVisualization(processId);
            return ResponseEntity.ok(data);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorVisualizationData("Failed to generate flow visualization: " + e.getMessage()));
        }
    }
    
    /**
     * Generate issue heat map
     * POST /api/visualization/bpmn/{diagramId}/heatmap
     */
    @PostMapping("/visualization/bpmn/{diagramId}/heatmap")
    public ResponseEntity<VisualizationData> generateHeatMap(
            @PathVariable String diagramId,
            @RequestBody Map<String, Object> request) {
        try {
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            VisualizationData data = visualizationService.generateIssueHeatMap(processId);
            return ResponseEntity.ok(data);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorVisualizationData("Failed to generate heat map: " + e.getMessage()));
        }
    }
    
    /**
     * Get API process mapping chart
     * GET /api/visualization/bpmn/{diagramId}/api-mapping
     */
    @GetMapping("/visualization/bpmn/{diagramId}/api-mapping")
    public ResponseEntity<VisualizationData> getApiProcessMappingChart(@PathVariable String diagramId) {
        try {
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            VisualizationData data = visualizationService.generateApiProcessMappingChart(processId);
            return ResponseEntity.ok(data);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorVisualizationData("Failed to generate API mapping chart: " + e.getMessage()));
        }
    }
    
    /**
     * Get security risk matrix
     * GET /api/visualization/bpmn/{diagramId}/security-matrix
     */
    @GetMapping("/visualization/bpmn/{diagramId}/security-matrix")
    public ResponseEntity<VisualizationData> getSecurityRiskMatrix(@PathVariable String diagramId) {
        try {
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            VisualizationData data = visualizationService.generateSecurityRiskMatrix(processId);
            return ResponseEntity.ok(data);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorVisualizationData("Failed to generate security risk matrix: " + e.getMessage()));
        }
    }
    
    /**
     * Get performance metrics chart
     * GET /api/visualization/bpmn/{diagramId}/performance
     */
    @GetMapping("/visualization/bpmn/{diagramId}/performance")
    public ResponseEntity<VisualizationData> getPerformanceMetricsChart(@PathVariable String diagramId) {
        try {
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            VisualizationData data = visualizationService.generatePerformanceMetricsChart(processId);
            return ResponseEntity.ok(data);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorVisualizationData("Failed to generate performance chart: " + e.getMessage()));
        }
    }
    
    /**
     * Get executive summary
     * GET /api/reports/bpmn/{diagramId}/summary
     */
    @GetMapping("/reports/bpmn/{diagramId}/summary")
    public ResponseEntity<ExecutiveSummaryDto> getExecutiveSummary(@PathVariable String diagramId) {
        try {
            // Generate a mock executive summary
            ExecutiveSummaryDto summary = createExecutiveSummary(diagramId);
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorSummary("Failed to generate executive summary: " + e.getMessage()));
        }
    }
    
    /**
     * Get all reports for a diagram
     * GET /api/reports/bpmn/{diagramId}/all
     */
    @GetMapping("/reports/bpmn/{diagramId}/all")
    public ResponseEntity<Map<String, Object>> getAllReports(@PathVariable String diagramId) {
        try {
            Map<String, Object> reports = createAllReports(diagramId);
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve all reports: " + e.getMessage()));
        }
    }
    
    // Helper methods
    
    private ReportResponse createErrorResponse(String error) {
        ReportResponse response = new ReportResponse();
        response.setId(UUID.randomUUID());
        response.setReportName("Error Report");
        response.setReportType("ERROR");
        response.setErrors(java.util.Collections.singletonList(error));
        return response;
    }
    
    private ReportResponse createMockReport(String diagramId, String reportType) {
        ReportResponse response = new ReportResponse();
        response.setId(UUID.randomUUID());
        response.setReportName(reportType + " Report");
        response.setReportType(reportType);
        response.setFormat("PDF");
        
        ReportResponse.ReportContent content = new ReportResponse.ReportContent();
        content.setRawData(java.util.Map.of(
            "diagramId", diagramId,
            "reportType", reportType,
            "generatedAt", java.time.LocalDateTime.now().toString(),
            "status", "Mock report for demonstration"
        ));
        response.setContent(content);
        
        return response;
    }
    
    private DashboardMetrics createErrorMetrics(String error) {
        DashboardMetrics metrics = new DashboardMetrics("error");
        metrics.setSummary(java.util.Map.of("error", error));
        return metrics;
    }
    
    private VisualizationData createErrorVisualizationData(String error) {
        VisualizationData data = new VisualizationData("error", "Error");
        data.setData(java.util.Map.of("error", error));
        return data;
    }
    
    private ExecutiveSummaryDto createErrorSummary(String error) {
        ExecutiveSummaryDto summary = new ExecutiveSummaryDto("Error", error);
        return summary;
    }
    
    private ExecutiveSummaryDto createExecutiveSummary(String diagramId) {
        ExecutiveSummaryDto summary = new ExecutiveSummaryDto(
            "BPMN Analysis Executive Summary",
            "Comprehensive analysis of BPMN diagram " + diagramId
        );
        
        summary.setTotalProcesses(1);
        summary.setTotalApiEndpoints(5);
        summary.setTotalIssues(8);
        summary.setCriticalIssues(1);
        summary.setComplianceScore(87);
        summary.setOverallStatus("WARNING");
        
        summary.setKeyFindings(java.util.Arrays.asList(
            "BPMN diagram contains 1 process(es)",
            "Identified 5 API endpoint(s)",
            "Found 8 issue(s) requiring attention",
            "Overall compliance score: 87%"
        ));
        
        summary.setRecommendations(java.util.Arrays.asList(
            "Address critical security issues immediately",
            "Improve compliance with industry standards",
            "Regular monitoring and maintenance recommended"
        ));
        
        return summary;
    }
    
    private Map<String, Object> createAllReports(String diagramId) {
        return java.util.Map.of(
            "diagramId", diagramId,
            "reports", java.util.Map.of(
                "processApiMapping", createMockReport(diagramId, "PROCESS_API_MAPPING"),
                "businessAnalysis", createMockReport(diagramId, "BUSINESS_PROCESS_ANALYSIS"),
                "compliance", createMockReport(diagramId, "API_PROCESS_COMPLIANCE"),
                "security", createMockReport(diagramId, "PROCESS_SECURITY_ASSESSMENT"),
                "summary", createExecutiveSummary(diagramId)
            ),
            "generatedAt", java.time.LocalDateTime.now().toString()
        );
    }
}