package org.example.web.controllers;

import org.example.domain.dto.test.*;
import org.example.domain.entities.TestScenario;
import org.example.domain.entities.TestExecution;
import org.example.domain.entities.TestDataSet;
import org.example.infrastructure.repositories.TestScenarioRepository;
import org.example.infrastructure.repositories.TestExecutionRepository;
import org.example.infrastructure.repositories.TestDataSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * REST контроллер для управления тестовыми сценариями OWASP
 */
@RestController
@RequestMapping("/api/scenarios")
@CrossOrigin(origins = "*")
public class TestScenarioController {
    
    @Autowired
    private TestScenarioRepository scenarioRepository;
    
    @Autowired
    private TestExecutionRepository executionRepository;
    
    @Autowired
    private TestDataSetRepository dataSetRepository;
    
    // POST /api/scenarios/generate/{analysisId} - генерация сценариев
    @PostMapping("/generate/{analysisId}")
    public ResponseEntity<List<TestScenarioResponse>> generateScenarios(
            @PathVariable String analysisId,
            @RequestBody(required = false) TestScenarioRequest request) {
        try {
            // TODO: Интеграция с LLM сервисом для генерации сценариев
            // Здесь будет вызов TestScenarioGenerator.generateFromAnalysis()
            
            List<TestScenario> scenarios = List.of(
                createMockScenario(analysisId, request)
            );
            
            List<TestScenarioResponse> responses = scenarios.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET /api/scenarios - список сценариев
    @GetMapping
    public ResponseEntity<List<TestScenarioResponse>> getAllScenarios(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        
        List<TestScenario> scenarios;
        
        if (category != null) {
            scenarios = scenarioRepository.findByOwaspCategory(category);
        } else if (type != null) {
            scenarios = scenarioRepository.findByScenarioType(type);
        } else if (status != null) {
            scenarios = scenarioRepository.findByStatus(status);
        } else {
            scenarios = scenarioRepository.findAll();
        }
        
        List<TestScenarioResponse> responses = scenarios.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    // GET /api/scenarios/{id} - детали сценария
    @GetMapping("/{id}")
    public ResponseEntity<TestScenarioResponse> getScenarioById(@PathVariable Long id) {
        Optional<TestScenario> scenario = scenarioRepository.findById(id);
        
        if (scenario.isPresent()) {
            return ResponseEntity.ok(convertToResponse(scenario.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /api/scenarios/{id}/execute - выполнение
    @PostMapping("/{id}/execute")
    public ResponseEntity<TestExecutionResponse> executeScenario(
            @PathVariable Long id,
            @RequestBody TestExecutionRequest request) {
        
        Optional<TestScenario> scenario = scenarioRepository.findById(id);
        if (!scenario.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            // TODO: Интеграция с TestExecutor
            TestExecution execution = new TestExecution(scenario.get(), request.getInitiatedBy());
            execution.setEnvironment(request.getEnvironment());
            execution.setExecutionType(request.getExecutionType());
            
            execution = executionRepository.save(execution);
            
            return ResponseEntity.ok(convertToExecutionResponse(execution));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET /api/scenarios/{id}/results - результаты
    @GetMapping("/{id}/results")
    public ResponseEntity<List<TestExecutionResponse>> getScenarioResults(@PathVariable Long id) {
        List<TestExecution> executions = executionRepository.findByTestScenario_ScenarioId(
            scenarioRepository.findById(id).map(TestScenario::getScenarioId).orElse(""));
        
        List<TestExecutionResponse> responses = executions.stream()
            .map(this::convertToExecutionResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    // GET /api/scenarios/owasp/{category} - сценарии по OWASP категории
    @GetMapping("/owasp/{category}")
    public ResponseEntity<List<TestScenarioResponse>> getScenariosByOwaspCategory(@PathVariable String category) {
        List<TestScenario> scenarios = scenarioRepository.findByOwaspCategory(category);
        
        List<TestScenarioResponse> responses = scenarios.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    // POST /api/scenarios/{id}/regenerate - перегенерация
    @PostMapping("/{id}/regenerate")
    public ResponseEntity<TestScenarioResponse> regenerateScenario(@PathVariable Long id) {
        Optional<TestScenario> scenario = scenarioRepository.findById(id);
        if (!scenario.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            // TODO: Интеграция с TestScenarioGenerator для перегенерации
            TestScenario updatedScenario = scenario.get();
            updatedScenario.setStatus("DRAFT");
            updatedScenario = scenarioRepository.save(updatedScenario);
            
            return ResponseEntity.ok(convertToResponse(updatedScenario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET /api/scenarios/{id}/execution/{execId}/logs - логи выполнения
    @GetMapping("/{id}/execution/{execId}/logs")
    public ResponseEntity<String> getExecutionLogs(@PathVariable Long id, @PathVariable String execId) {
        Optional<TestExecution> execution = executionRepository.findByExecutionId(execId);
        
        if (execution.isPresent()) {
            String logs = String.join("\n", execution.get().getCapturedLogs());
            return ResponseEntity.ok(logs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Helper methods
    private TestScenarioResponse convertToResponse(TestScenario scenario) {
        TestScenarioResponse response = new TestScenarioResponse();
        response.setId(scenario.getId());
        response.setScenarioId(scenario.getScenarioId());
        response.setName(scenario.getName());
        response.setDescription(scenario.getDescription());
        response.setScenarioType(scenario.getScenarioType());
        response.setOwaspCategory(scenario.getOwaspCategory());
        response.setAnalysisId(scenario.getAnalysisId());
        response.setStatus(scenario.getStatus());
        response.setPriority(scenario.getPriority());
        response.setEnvironment(scenario.getEnvironment());
        response.setIsSecurityTest(scenario.getIsSecurityTest());
        response.setIsFunctionalTest(scenario.getIsFunctionalTest());
        response.setIsPerformanceTest(scenario.getIsPerformanceTest());
        response.setCreatedAt(scenario.getCreatedAt());
        response.setUpdatedAt(scenario.getUpdatedAt());
        response.setTotalExecutions(scenario.getTotalExecutions());
        response.setSuccessfulExecutions(scenario.getSuccessfulExecutions());
        response.setFailedExecutions(scenario.getFailedExecutions());
        response.setSuccessRate(scenario.getSuccessRate());
        return response;
    }
    
    private TestExecutionResponse convertToExecutionResponse(TestExecution execution) {
        TestExecutionResponse response = new TestExecutionResponse();
        response.setId(execution.getId());
        response.setExecutionId(execution.getExecutionId());
        response.setTestScenarioId(execution.getTestScenario() != null ? 
            execution.getTestScenario().getScenarioId() : null);
        response.setEnvironment(execution.getEnvironment());
        response.setStatus(execution.getStatus().toString());
        response.setExecutionType(execution.getExecutionType().toString());
        response.setInitiatedBy(execution.getInitiatedBy());
        response.setExecutedBy(execution.getExecutedBy());
        response.setStartedAt(execution.getStartedAt());
        response.setCompletedAt(execution.getCompletedAt());
        response.setTotalDurationMs(execution.getTotalDurationMs());
        response.setTotalSteps(execution.getTotalSteps());
        response.setPassedSteps(execution.getPassedSteps());
        response.setFailedSteps(execution.getFailedSteps());
        response.setTotalAssertions(execution.getTotalAsserted());
        response.setPassedAssertions(execution.getPassedAssertions());
        response.setFailedAssertions(execution.getFailedAssertions());
        response.setExecutionSummary(execution.getExecutionSummary());
        response.setDetailedResults(execution.getDetailedResults());
        response.setErrorDetails(execution.getErrorDetails());
        response.setCapturedLogs(execution.getCapturedLogs());
        response.setIsParallelExecution(execution.getIsParallelExecution());
        response.setIsAutomatedExecution(execution.getIsAutomatedExecution());
        response.calculateMetrics();
        return response;
    }
    
    private TestScenario createMockScenario(String analysisId, TestScenarioRequest request) {
        TestScenario scenario = new TestScenario(
            request != null ? request.getName() : "Generated Security Test",
            request != null ? request.getScenarioType() : "SECURITY",
            request != null ? request.getOwaspCategory() : "A03"
        );
        scenario.setAnalysisId(analysisId);
        scenario.setDescription(request != null ? request.getDescription() : "Auto-generated test scenario");
        scenario.setStatus("ACTIVE");
        scenario.setEnvironment(request != null ? request.getEnvironment() : "TEST");
        return scenario;
    }
}