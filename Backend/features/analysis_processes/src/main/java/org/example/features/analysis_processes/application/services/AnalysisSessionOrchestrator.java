package org.example.features.analysis_processes.application.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.entities.AnalysisSession;
import org.example.features.analysis_processes.domain.entities.AnalysisStep;
import org.example.features.analysis_processes.domain.services.AnalysisProcessService;
import org.example.features.analysis_processes.domain.services.AnalysisSessionService;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisSessionStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepType;
import org.example.features.analysis_processes.domain.valueobjects.HttpRequestStep;
import org.example.features.analysis_processes.domain.valueobjects.InputRequirement;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
            session.getContext().put("llmPrompt", planResult.prompt());
            session.getContext().put("llmRawResponse", planResult.rawResponse());
            session.getContext().put("requiresAdditionalInput", planResult.requiresAdditionalInput());
            session.getContext().put("requiredInputFields", planResult.additionalInputs());
            String baseUrl = valueOrDefault(extractUserInputs(session.getContext()).get("baseUrl"), "http://localhost:8080");
            session.getContext().put("baseUrl", baseUrl);

            markStepCompleted(current);
            insertHttpSteps(session, planResult);
            session.setStatus(AnalysisSessionStatus.RUNNING);
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
            AnalysisStep current = session.getCurrentStepId() == null
                ? null
                : findStepById(session, session.getCurrentStepId());
            if (current == null || current.getType() != AnalysisStepType.HTTP_REQUEST) {
                return session;
            }
            return executeHttpStepInternal(session, current, Collections.emptyMap());
        });
    }

    public Optional<AnalysisSession> executeHttpStep(
        String sessionId,
        String stepId,
        Map<String, Object> additionalInputs
    ) {
        return sessionService.getSession(sessionId).map(session -> {
            AnalysisStep step = findStepById(session, stepId);
            if (step == null || step.getType() != AnalysisStepType.HTTP_REQUEST) {
                return session;
            }
            return executeHttpStepInternal(session, step, additionalInputs == null ? Collections.emptyMap() : additionalInputs);
        });
    }

    private AnalysisSession executeHttpStepInternal(
        AnalysisSession session,
        AnalysisStep step,
        Map<String, Object> additionalInputs
    ) {
        if (step.getStatus() == AnalysisStepStatus.COMPLETED) {
            return session;
        }
        step.setStatus(AnalysisStepStatus.RUNNING);
        session.setStatus(AnalysisSessionStatus.RUNNING);
        if (additionalInputs != null && !additionalInputs.isEmpty()) {
            session.getContext().put("httpStepInputs:" + step.getId(), additionalInputs);
        }
        HttpRequestStep request = extractHttpRequest(step);
        if (request == null) {
            step.setStatus(AnalysisStepStatus.FAILED);
            return sessionService.updateSession(session);
        }
        request.setStepId(step.getId());
        if (hasResultForStep(session, step.getId())) {
            step.setStatus(AnalysisStepStatus.COMPLETED);
            advanceAfterHttpStep(session, step);
            return sessionService.updateSession(session);
        }
        String baseUrl = valueOrDefault(extractUserInputs(session.getContext()).get("baseUrl"), "http://localhost:8080");
        List<Map<String, Object>> results = requestExecutor.execute(List.of(request), baseUrl);
        if (results.isEmpty()) {
            step.setStatus(AnalysisStepStatus.FAILED);
            return sessionService.updateSession(session);
        }
        Map<String, Object> result = new HashMap<>(results.get(0));
        result.put("stepId", step.getId());
        persistHttpResult(session, result);
        step.setStatus(AnalysisStepStatus.COMPLETED);
        advanceAfterHttpStep(session, step);
        return sessionService.updateSession(session);
    }

    private void advanceAfterHttpStep(AnalysisSession session, AnalysisStep completed) {
        AnalysisStep next = findNextHttpStep(session, completed);
        if (next != null) {
            next.setStatus(AnalysisStepStatus.WAITING);
            session.setCurrentStepId(next.getId());
            session.setStatus(AnalysisSessionStatus.RUNNING);
            return;
        }
        activateNextStep(session, AnalysisStepType.TEST_EXECUTION);
        session.setStatus(AnalysisSessionStatus.WAITING_FOR_TEST);
    }

    private boolean hasResultForStep(AnalysisSession session, String stepId) {
        return loadHttpResults(session).stream()
            .anyMatch(result -> stepId.equals(result.get("stepId")));
    }

    private void persistHttpResult(AnalysisSession session, Map<String, Object> result) {
        List<Map<String, Object>> results = new ArrayList<>(loadHttpResults(session));
        String stepId = Optional.ofNullable(result.get("stepId")).map(Object::toString).orElse(null);
        if (stepId != null && results.stream().anyMatch(entry -> stepId.equals(entry.get("stepId")))) {
            session.getContext().put("lastHttpResult", result);
            return;
        }
        results.add(result);
        session.getContext().put("httpResults", results);
        session.getContext().put("lastHttpResult", result);
    }

    private void insertHttpSteps(AnalysisSession session, ProcessAnalysisPlanner.PlanResult planResult) {
        List<HttpRequestStep> httpRequests = planResult.httpRequests();
        List<List<InputRequirement>> httpInputs = planResult.httpAdditionalInputs();
        session.getSteps().removeIf(step -> step.getType() == AnalysisStepType.HTTP_REQUEST);
        AnalysisStep testStep = session.getSteps().stream()
            .filter(step -> step.getType() == AnalysisStepType.TEST_EXECUTION)
            .findFirst()
            .orElse(null);
        int insertIndex = testStep == null ? session.getSteps().size() : session.getSteps().indexOf(testStep);
        if (httpRequests.isEmpty()) {
            if (testStep != null) {
                testStep.setStatus(AnalysisStepStatus.WAITING);
                session.setCurrentStepId(testStep.getId());
            }
            return;
        }
        List<AnalysisStep> httpSteps = new ArrayList<>();
        for (int index = 0; index < httpRequests.size(); index++) {
            HttpRequestStep request = httpRequests.get(index);
            List<InputRequirement> inputs =
                httpInputs.size() > index ? httpInputs.get(index) : List.of();
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("httpRequest", request);
            metadata.put("requiresAdditionalInput", !inputs.isEmpty());
            if (!inputs.isEmpty()) {
                metadata.put("additionalInputs", inputs);
            }
            AnalysisStep step = AnalysisStep.builder()
                .id(UUID.randomUUID().toString())
                .title("Execute HTTP request: " + request.getName())
                .description(StringUtils.hasText(request.getDescription())
                    ? request.getDescription()
                    : request.getMethod() + " " + request.getUrl())
                .type(AnalysisStepType.HTTP_REQUEST)
                .status(index == 0 ? AnalysisStepStatus.WAITING : AnalysisStepStatus.PENDING)
                .metadata(metadata)
                .build();
            httpSteps.add(step);
        }
        session.getSteps().addAll(insertIndex, httpSteps);
        session.setCurrentStepId(httpSteps.get(0).getId());
        if (testStep != null) {
            testStep.setStatus(AnalysisStepStatus.PENDING);
        }
    }

    private AnalysisStep getCurrentStep(AnalysisSession session) {
        return session.getSteps().stream()
            .filter(step -> step.getId().equals(session.getCurrentStepId()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Current step not found"));
    }

    private AnalysisStep findStepById(AnalysisSession session, String stepId) {
        if (stepId == null) {
            return null;
        }
        return session.getSteps().stream()
            .filter(step -> step.getId().equals(stepId))
            .findFirst()
            .orElse(null);
    }

    private AnalysisStep findNextHttpStep(AnalysisSession session, AnalysisStep completed) {
        List<AnalysisStep> httpSteps = session.getSteps().stream()
            .filter(step -> step.getType() == AnalysisStepType.HTTP_REQUEST)
            .collect(Collectors.toList());
        for (int index = 0; index < httpSteps.size(); index++) {
            if (httpSteps.get(index).getId().equals(completed.getId()) && index + 1 < httpSteps.size()) {
                return httpSteps.get(index + 1);
            }
        }
        return null;
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

    private List<Map<String, Object>> loadHttpResults(AnalysisSession session) {
        Object raw = session.getContext().get("httpResults");
        if (raw instanceof List<?> list) {
            List<Map<String, Object>> results = new ArrayList<>();
            for (Object entry : list) {
                if (entry instanceof Map<?, ?> mapEntry) {
                    Map<String, Object> normalized = new HashMap<>();
                    mapEntry.forEach((key, value) -> {
                        if (key != null) {
                            normalized.put(key.toString(), value);
                        }
                    });
                    results.add(normalized);
                }
            }
            return results;
        }
        return new ArrayList<>();
    }

    private HttpRequestStep extractHttpRequest(AnalysisStep step) {
        Object raw = step.getMetadata().get("httpRequest");
        if (raw instanceof HttpRequestStep request) {
            return request;
        }
        if (raw instanceof Map<?, ?> map) {
            return objectMapper.convertValue(
                map,
                new TypeReference<HttpRequestStep>() {}
            );
        }
        return null;
    }

    private Map<String, Object> extractUserInputs(Map<String, Object> context) {
        if (context == null) {
            return Collections.emptyMap();
        }
        Object raw = context.get("userInputs");
        if (raw instanceof Map<?, ?> rawMap) {
            Map<String, Object> normalized = new HashMap<>();
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
