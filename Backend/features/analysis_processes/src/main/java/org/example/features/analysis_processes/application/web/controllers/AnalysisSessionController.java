package org.example.features.analysis_processes.application.web.controllers;

import org.example.features.analysis_processes.application.dto.AnalysisSessionResponse;
import org.example.features.analysis_processes.application.services.AnalysisInputAdvisor;
import org.example.features.analysis_processes.application.services.AnalysisSessionOrchestrator;
import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.entities.AnalysisSession;
import org.example.features.analysis_processes.domain.services.AnalysisProcessService;
import org.example.features.analysis_processes.domain.services.AnalysisSessionService;
import org.example.features.analysis_processes.domain.valueobjects.InputRequirement;
import org.example.shared.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalysisSessionController {

    private final AnalysisProcessService processService;
    private final AnalysisSessionService sessionService;
    private final AnalysisSessionOrchestrator orchestrator;
    private final AnalysisInputAdvisor inputAdvisor;

    public AnalysisSessionController(
        AnalysisProcessService processService,
        AnalysisSessionService sessionService,
        AnalysisSessionOrchestrator orchestrator,
        AnalysisInputAdvisor inputAdvisor
    ) {
        this.processService = processService;
        this.sessionService = sessionService;
        this.orchestrator = orchestrator;
        this.inputAdvisor = inputAdvisor;
    }

    @PostMapping("/analysis-processes/{processId}/analysis-sessions")
    public ResponseEntity<ApiResponse<AnalysisSessionResponse>> startSession(
        @PathVariable("processId") String processId
    ) {
        AnalysisProcess process = processService.getProcessById(processId)
            .orElseThrow(() -> new IllegalArgumentException("Process not found"));
        if (process.getBpmnDiagramPath() == null || process.getOpenapiSpecPath() == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Для запуска анализа необходимо загрузить BPMN и OpenAPI артефакты"));
        }

        List<InputRequirement> requirements = inputAdvisor.determineInputs(process);
        AnalysisSession session = sessionService.startSession(processId, requirements);
        return ResponseEntity.ok(ApiResponse.success(AnalysisSessionResponse.from(session)));
    }

    @PostMapping("/analysis-sessions/{sessionId}/inputs")
    public ResponseEntity<ApiResponse<AnalysisSessionResponse>> provideInputs(
        @PathVariable("sessionId") String sessionId,
        @RequestBody Map<String, Object> payload
    ) {
        return orchestrator.provideInputs(sessionId, payload)
            .map(session -> ResponseEntity.ok(ApiResponse.success(AnalysisSessionResponse.from(session))))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/analysis-sessions/{sessionId}/llm")
    public ResponseEntity<ApiResponse<AnalysisSessionResponse>> generatePlan(
        @PathVariable("sessionId") String sessionId
    ) {
        try {
            return orchestrator.generatePlan(sessionId)
                .map(session -> ResponseEntity.ok(ApiResponse.success(AnalysisSessionResponse.from(session))))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/analysis-sessions/{sessionId}/tests")
    public ResponseEntity<ApiResponse<AnalysisSessionResponse>> submitTestResult(
        @PathVariable("sessionId") String sessionId,
        @RequestBody Map<String, Object> payload
    ) {
        return orchestrator.completeTestStep(sessionId, payload)
            .map(session -> ResponseEntity.ok(ApiResponse.success(AnalysisSessionResponse.from(session))))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/analysis-sessions/{sessionId}")
    public ResponseEntity<ApiResponse<AnalysisSessionResponse>> getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId)
            .map(session -> ResponseEntity.ok(ApiResponse.success(AnalysisSessionResponse.from(session))))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/analysis-processes/{processId}/analysis-sessions/latest")
    public ResponseEntity<ApiResponse<AnalysisSessionResponse>> getLatestSessionForProcess(
        @PathVariable("processId") String processId
    ) {
        return sessionService.getLatestForProcess(processId)
            .map(session -> ResponseEntity.ok(ApiResponse.success(AnalysisSessionResponse.from(session))))
            .orElseGet(() -> ResponseEntity.ok(ApiResponse.success(null)));
    }
}
