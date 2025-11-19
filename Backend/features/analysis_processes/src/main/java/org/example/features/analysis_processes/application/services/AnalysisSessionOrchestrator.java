package org.example.features.analysis_processes.application.services;

import org.example.features.analysis_processes.domain.entities.AnalysisSession;
import org.example.features.analysis_processes.domain.entities.AnalysisStep;
import org.example.features.analysis_processes.domain.services.AnalysisSessionService;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisSessionStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AnalysisSessionOrchestrator {

    private final AnalysisSessionService sessionService;

    public AnalysisSessionOrchestrator(AnalysisSessionService sessionService) {
        this.sessionService = sessionService;
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
            session.getContext().put("llmPlan", "LLM план анализа будет сформирован после интеграции с реальным провайдером.");
            return sessionService.updateSession(session);
        });
    }

    public Optional<AnalysisSession> completeLlmStep(String sessionId, String scriptContent) {
        return sessionService.getSession(sessionId).map(session -> {
            AnalysisStep current = getCurrentStep(session);
            if (current.getType() != AnalysisStepType.LLM_ANALYSIS) {
                return session;
            }
            markStepCompleted(current);
            session.getContext().put("testScript", StringUtils.hasText(scriptContent) ? scriptContent :
                """
                // TODO: заменить на код, полученный от LLM
                console.log('Security test stub');
                """);
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
                step.setStatus(type == AnalysisStepType.LLM_ANALYSIS ? AnalysisStepStatus.RUNNING : AnalysisStepStatus.WAITING);
                session.setCurrentStepId(step.getId());
                return;
            }
        }
        session.setCurrentStepId(null);
    }
}
