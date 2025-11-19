package org.example.features.analysis_processes.application.dto;

import org.example.features.analysis_processes.domain.entities.AnalysisSession;
import org.example.features.analysis_processes.domain.entities.AnalysisStep;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisSessionStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepStatus;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisStepType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record AnalysisSessionResponse(
    String id,
    String processId,
    AnalysisSessionStatus status,
    String currentStepId,
    List<StepResponse> steps,
    Map<String, Object> context,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static AnalysisSessionResponse from(AnalysisSession session) {
        return new AnalysisSessionResponse(
            session.getId(),
            session.getProcessId(),
            session.getStatus(),
            session.getCurrentStepId(),
            session.getSteps().stream().map(StepResponse::from).toList(),
            session.getContext(),
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }

    public record StepResponse(
        String id,
        String title,
        String description,
        AnalysisStepType type,
        AnalysisStepStatus status,
        Map<String, Object> metadata
    ) {
        static StepResponse from(AnalysisStep step) {
            return new StepResponse(
                step.getId(),
                step.getTitle(),
                step.getDescription(),
                step.getType(),
                step.getStatus(),
                step.getMetadata()
            );
        }
    }
}
