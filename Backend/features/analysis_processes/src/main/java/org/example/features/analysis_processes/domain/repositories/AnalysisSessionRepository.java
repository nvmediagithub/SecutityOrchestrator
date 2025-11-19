package org.example.features.analysis_processes.domain.repositories;

import org.example.features.analysis_processes.domain.entities.AnalysisSession;

import java.util.List;
import java.util.Optional;

public interface AnalysisSessionRepository {

    List<AnalysisSession> findAll();

    List<AnalysisSession> findByProcessId(String processId);

    Optional<AnalysisSession> findById(String sessionId);

    AnalysisSession save(AnalysisSession session);
}
