package org.example.features.analysis-processes.domain.repositories;

import org.example.features.analysis-processes.domain.entities.AnalysisProcess;
import java.util.List;
import java.util.Optional;

public interface AnalysisProcessRepository {
    List<AnalysisProcess> findAll();
    Optional<AnalysisProcess> findById(String id);
    AnalysisProcess save(AnalysisProcess process);
    void deleteById(String id);
}