package org.example.features.analysis_processes.domain.services;

import org.example.features.analysis_processes.domain.entities.AnalysisSession;
import org.example.features.analysis_processes.domain.entities.AnalysisStep;
import org.example.features.analysis_processes.domain.repositories.AnalysisSessionRepository;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisSessionStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnalysisSessionService {

    private final AnalysisSessionRepository repository;

    public AnalysisSessionService(AnalysisSessionRepository repository) {
        this.repository = repository;
    }

    public AnalysisSession startSession(String processId, List<String> requiredInputs) {
        String firstStepId = UUID.randomUUID().toString();
        AnalysisStep collectInputs = AnalysisStep.builder()
            .id(firstStepId)
            .title("Сбор дополнительных данных")
            .description("LLM требует дополнительные данные перед запуском анализа")
            .type(AnalysisStepType.COLLECT_INPUTS)
            .status(AnalysisStepStatus.WAITING)
            .metadata(Map.of("requiredInputs", requiredInputs))
            .build();

        AnalysisStep llmStep = AnalysisStep.builder()
            .id(UUID.randomUUID().toString())
            .title("Запуск LLM анализа")
            .description("После получения параметров запускаем LLM для построения плана тестирования")
            .type(AnalysisStepType.LLM_ANALYSIS)
            .status(AnalysisStepStatus.PENDING)
            .build();

        AnalysisStep testStep = AnalysisStep.builder()
            .id(UUID.randomUUID().toString())
            .title("Выполнение тестового скрипта")
            .description("Запустить предоставленный LLM скрипт и отправить результат")
            .type(AnalysisStepType.TEST_EXECUTION)
            .status(AnalysisStepStatus.PENDING)
            .build();

        AnalysisSession session = AnalysisSession.builder()
            .processId(processId)
            .status(AnalysisSessionStatus.WAITING_FOR_INPUT)
            .currentStepId(firstStepId)
            .steps(new java.util.ArrayList<>(List.of(collectInputs, llmStep, testStep)))
            .build();

        return repository.save(session);
    }

    public Optional<AnalysisSession> getSession(String sessionId) {
        return repository.findById(sessionId);
    }

    public AnalysisSession updateSession(AnalysisSession session) {
        return repository.save(session);
    }

    public Optional<AnalysisSession> getLatestForProcess(String processId) {
        return repository.findByProcessId(processId).stream()
            .max(java.util.Comparator.comparing(AnalysisSession::getUpdatedAt));
    }
}
