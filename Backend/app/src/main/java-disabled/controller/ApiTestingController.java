package org.example.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entities.ApiSpecification;
import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.analysis.AnalysisResult;
import org.example.domain.model.testdata.TestExecutionStep;
import org.example.domain.valueobjects.TestCaseId;
import org.example.domain.valueobjects.WorkflowId;
import org.example.infrastructure.services.api.ApiTestingOrchestrator;
import org.example.infrastructure.services.api.OpenApiAnalysisService;
import org.example.infrastructure.services.api.BpmnTestingIntegrationService;
import org.example.infrastructure.services.llm.LLMInconsistencyDetectionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/testing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "API Testing", description = "Comprehensive API testing with OpenAPI + BPMN + LLM analysis")
public class ApiTestingController {

    private final ApiTestingOrchestrator testingOrchestrator;
    private final OpenApiAnalysisService openApiService;
    private final BpmnTestingIntegrationService bpmnService;
    private final LLMInconsistencyDetectionService llmService;

    @PostMapping(value = "/analyze/openapi", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Analyze OpenAPI specification", 
               description = "Parse and analyze OpenAPI/Swagger specification from URL or file")
    public CompletableFuture<ResponseEntity<AnalysisResult>> analyzeOpenApi(
            @RequestParam("sourceUrl") String sourceUrl,
            @RequestParam(value = "specFile", required = false) MultipartFile specFile,
            @RequestParam(value = "baseUrl", required = false) String baseUrl) {
        
        log.info("Starting OpenAPI analysis for source: {}", sourceUrl);
        return testingOrchestrator.analyzeOpenApiSpec(sourceUrl, specFile, baseUrl)
                .thenApply(result -> ResponseEntity.ok(result));
    }

    @PostMapping(value = "/analyze/bpmn",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Analyze BPMN process", 
               description = "Parse and analyze BPMN diagram for API testing integration")
    public CompletableFuture<ResponseEntity<AnalysisResult>> analyzeBpmn(
            @RequestParam("bpmnFile") MultipartFile bpmnFile) {
        
        log.info("Starting BPMN analysis for file: {}", bpmnFile.getOriginalFilename());
        return testingOrchestrator.analyzeBpmnProcess(bpmnFile)
                .thenApply(result -> ResponseEntity.ok(result));
    }

    @PostMapping("/generate/tests")
    @Operation(summary = "Generate OWASP API security tests",
               description = "Generate comprehensive test cases based on OWASP API Security guidelines")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> generateOwaspTests(
            @RequestBody Map<String, Object> request) {
        
        String openApiId = (String) request.get("openApiId");
        String bpmnId = (String) request.get("bpmnId");
        List<String> owaspCategories = (List<String>) request.get("owaspCategories");
        
        log.info("Generating OWASP tests for OpenAPI: {} and BPMN: {}", openApiId, bpmnId);
        return testingOrchestrator.generateOwaspSecurityTests(openApiId, bpmnId, owaspCategories)
                .thenApply(result -> ResponseEntity.ok(result));
    }

    @PostMapping("/generate/test-data")
    @Operation(summary = "Generate context-aware test data",
               description = "Generate test data considering dependencies between API steps")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> generateTestData(
            @RequestBody Map<String, Object> request) {
        
        String testCaseId = (String) request.get("testCaseId");
        String bpmnId = (String) request.get("bpmnId");
        Map<String, Object> constraints = (Map<String, Object>) request.get("constraints");
        
        log.info("Generating test data for test case: {}", testCaseId);
        return testingOrchestrator.generateContextAwareTestData(testCaseId, bpmnId, constraints)
                .thenApply(result -> ResponseEntity.ok(result));
    }

    @PostMapping("/execute/e2e")
    @Operation(summary = "Execute end-to-end API testing",
               description = "Execute complete API testing workflow from OpenAPI + BPMN")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> executeEndToEnd(
            @RequestBody Map<String, Object> request) {
        
        String openApiId = (String) request.get("openApiId");
        String bpmnId = (String) request.get("bpmnId");
        Map<String, Object> testConfig = (Map<String, Object>) request.get("testConfig");
        
        log.info("Starting end-to-end execution for OpenAPI: {} and BPMN: {}", openApiId, bpmnId);
        return testingOrchestrator.executeEndToEndTesting(openApiId, bpmnId, testConfig)
                .thenApply(result -> ResponseEntity.ok(result));
    }

    @GetMapping("/status/{executionId}")
    @Operation(summary = "Get testing execution status",
               description = "Get real-time status of API testing execution")
    public ResponseEntity<Map<String, Object>> getExecutionStatus(
            @PathVariable String executionId) {
        
        log.info("Getting execution status for: {}", executionId);
        return testingOrchestrator.getExecutionStatus(executionId)
                .map(status -> ResponseEntity.ok(status))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/results/{executionId}")
    @Operation(summary = "Get testing execution results",
               description = "Get complete results of API testing execution")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getExecutionResults(
            @PathVariable String executionId) {
        
        log.info("Getting execution results for: {}", executionId);
        return testingOrchestrator.getExecutionResults(executionId)
                .thenApply(result -> ResponseEntity.ok(result));
    }

    @GetMapping("/llm/analysis/{openApiId}")
    @Operation(summary = "Get LLM inconsistency analysis",
               description = "Get LLM-powered analysis of OpenAPI specifications for inconsistencies")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getLlmAnalysis(
            @PathVariable String openApiId) {
        
        log.info("Getting LLM analysis for OpenAPI: {}", openApiId);
        return llmService.analyzeInconsistencies(openApiId)
                .thenApply(result -> ResponseEntity.ok(result));
    }

    @PostMapping("/visualization/real-time")
    @Operation(summary = "Start real-time visualization",
               description = "Start WebSocket connection for real-time testing visualization")
    public ResponseEntity<Map<String, String>> startRealTimeVisualization(
            @RequestBody Map<String, Object> request) {
        
        String executionId = (String) request.get("executionId");
        log.info("Starting real-time visualization for execution: {}", executionId);
        
        Map<String, String> response = Map.of(
            "status", "connected",
            "webSocketUrl", "/ws/testing/" + executionId,
            "message", "Real-time visualization started"
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bpmn/processes")
    @Operation(summary = "List available BPMN processes",
               description = "Get list of all available BPMN processes from guide/bpmn directory")
    public ResponseEntity<List<BpmnDiagram>> getBpmnProcesses() {
        log.info("Getting list of available BPMN processes");
        return ResponseEntity.ok(bpmnService.getAvailableBpmnProcesses());
    }

    @GetMapping("/owasp/categories")
    @Operation(summary = "Get OWASP API Security categories",
               description = "Get list of OWASP API Security testing categories")
    public ResponseEntity<Map<String, List<String>>> getOwaspCategories() {
        log.info("Getting OWASP API Security categories");
        return ResponseEntity.ok(testingOrchestrator.getOwaspCategories());
    }
}