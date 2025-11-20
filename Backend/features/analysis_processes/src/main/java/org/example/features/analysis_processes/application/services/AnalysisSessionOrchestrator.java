package org.example.features.analysis_processes.application.services;

import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.entities.AnalysisSession;
import org.example.features.analysis_processes.domain.entities.AnalysisStep;
import org.example.features.analysis_processes.domain.services.AnalysisProcessService;
import org.example.features.analysis_processes.domain.services.AnalysisSessionService;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisSessionStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepType;
import org.example.features.analysis_processes.domain.valueobjects.HttpRequestStep;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AnalysisSessionOrchestrator {

    private final AnalysisSessionService sessionService;
    private final AnalysisProcessService processService;
    private final ProcessAnalysisPlanner planner;
    private final HttpRequestExecutor requestExecutor;
    private final ObjectMapper objectMapper;

    public AnalysisSessionOrchestrator(
        AnalysisSessionService sessionService,
        AnalysisProcessService processService,
        ProcessAnalysisPlanner planner,
        HttpRequestExecutor requestExecutor
    ) {
        this.sessionService = sessionService;
        this.processService = processService;
        this.planner = planner;
        this.requestExecutor = requestExecutor;
        this.objectMapper = new ObjectMapper();
    }

    public Optional<AnalysisSession> provideInputs(String sessionId, Map<String, Object> inputs) {
        return sessionService.getSession(sessionId).map(session -> {
            AnalysisStep current = getCurrentStep(session);
            if (current.getType() != AnalysisStepType.COLLECT_INPUTS) {
                return session;
            }
            session.getContext().put("userInputs", inputs);
            markStepCompleted(current);
            activateNextStep(session, AnalysisStepType.LLM_ANALYSIS);
            session.setStatus(AnalysisSessionStatus.RUNNING);
            return sessionService.updateSession(session);
        });
    }

    public Optional<AnalysisSession> generatePlan(String sessionId) {
        return sessionService.getSession(sessionId).map(session -> {
            AnalysisStep current = getCurrentStep(session);
            if (current.getType() != AnalysisStepType.LLM_ANALYSIS) {
                return session;
            }

            AnalysisProcess process = processService.getProcessById(session.getProcessId())
                .orElseThrow(() -> new IllegalStateException("Process not found for session"));

            current.setStatus(AnalysisStepStatus.RUNNING);
            ProcessAnalysisPlanner.PlanResult planResult = planner.generatePlan(process, session.getContext());
            session.getContext().put("llmPlan", planResult.plan());
            session.getContext().put("llmSummary", planResult.summary());
            session.getContext().put("llmPlanActions", planResult.actions());
            session.getContext().put("llmTestAssertions", planResult.assertions());
            session.getContext().put("httpRequests", planResult.httpRequests());
            String baseUrl = valueOrDefault(extractUserInputs(session.getContext()).get("baseUrl"), "http://localhost:8080");
            session.getContext().put("baseUrl", baseUrl);

            markStepCompleted(current);
            activateNextStep(session, AnalysisStepType.TEST_EXECUTION);
            session.setStatus(AnalysisSessionStatus.WAITING_FOR_TEST);
            return sessionService.updateSession(session);
        });
    }

    public Optional<AnalysisSession> completeTestStep(String sessionId, Map<String, Object> testResult) {
        return sessionService.getSession(sessionId).map(session -> {
            AnalysisStep current = getCurrentStep(session);
            if (current.getType() != AnalysisStepType.TEST_EXECUTION) {
                return session;
            }
            markStepCompleted(current);
            session.getContext().put("testResult", testResult);
            session.setStatus(AnalysisSessionStatus.COMPLETED);
            session.setCurrentStepId(null);
            return sessionService.updateSession(session);
        });
    }

    public Optional<AnalysisSession> executeHttpRequests(String sessionId) {
        return sessionService.getSession(sessionId).map(session -> {
            List<HttpRequestStep> steps = loadHttpRequests(session);
            if (steps.isEmpty()) {
                return session;
            }
            String baseUrl = valueOrDefault(extractUserInputs(session.getContext()).get("baseUrl"), "http://localhost:8080");
            List<Map<String, Object>> results = requestExecutor.execute(steps, baseUrl);
            session.getContext().put("httpResults", results);
            return sessionService.updateSession(session);
        });
    }

    private AnalysisStep getCurrentStep(AnalysisSession session) {
        return session.getSteps().stream()
            .filter(step -> step.getId().equals(session.getCurrentStepId()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Current step not found"));
    }

    private void markStepCompleted(AnalysisStep step) {
        step.setStatus(AnalysisStepStatus.COMPLETED);
    }

    private void activateNextStep(AnalysisSession session, AnalysisStepType type) {
        List<AnalysisStep> steps = session.getSteps();
        for (AnalysisStep step : steps) {
            if (step.getType() == type) {
                step.setStatus(AnalysisStepStatus.WAITING);
                session.setCurrentStepId(step.getId());
                return;
            }
        }
        session.setCurrentStepId(null);
    }

    private List<HttpRequestStep> loadHttpRequests(AnalysisSession session) {
        Object raw = session.getContext().get("httpRequests");
        if (raw == null) {
            return List.of();
        }
        return objectMapper.convertValue(
            raw,
            new TypeReference<List<HttpRequestStep>>() {}
        );
    }

    private Map<String, Object> extractUserInputs(Map<String, Object> context) {
        if (context == null) {
            return Collections.emptyMap();
        }
        Object raw = context.get("userInputs");
        if (raw instanceof Map<?, ?> rawMap) {
            Map<String, Object> normalized = new java.util.HashMap<>();
            rawMap.forEach((key, value) -> {
                if (key != null) {
                    normalized.put(key.toString(), value);
                }
            });
            return normalized;
        }
        return Collections.emptyMap();
    }

    private String valueOrDefault(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String text = value.toString();
        return text.isBlank() ? fallback : text;
    }
}
