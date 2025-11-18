package org.example.features.analysis-processes.domain.services;

import org.example.features.analysis-processes.domain.entities.AnalysisProcess;
import org.example.features.analysis-processes.domain.repositories.AnalysisProcessRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnalysisProcessService {

    private final AnalysisProcessRepository repository;

    public AnalysisProcessService(AnalysisProcessRepository repository) {
        this.repository = repository;
    }

    public List<AnalysisProcess> getAllProcesses() {
        return repository.findAll();
    }

    public Optional<AnalysisProcess> getProcessById(String id) {
        return repository.findById(id);
    }

    public AnalysisProcess createProcess(AnalysisProcess process) {
        process.setId(UUID.randomUUID().toString());
        ensureDefaults(process);
        return repository.save(process);
    }

    public Optional<AnalysisProcess> updateProcess(String id, AnalysisProcess updatedProcess) {
        Optional<AnalysisProcess> existingProcess = repository.findById(id);
        if (existingProcess.isPresent()) {
            updatedProcess.setId(id);
            if (updatedProcess.getCreatedAt() == null) {
                updatedProcess.setCreatedAt(existingProcess.get().getCreatedAt());
            }
            ensureDefaults(updatedProcess);
            return Optional.of(repository.save(updatedProcess));
        }
        return Optional.empty();
    }

    public boolean deleteProcess(String id) {
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private void ensureDefaults(AnalysisProcess process) {
        if (process.getCreatedAt() == null) {
            process.setCreatedAt(LocalDateTime.now());
        }
        if (process.getStatus() == null || process.getStatus().isBlank()) {
            process.setStatus("pending");
        }
        if (process.getType() == null || process.getType().isBlank()) {
            process.setType("custom");
        }
    }
}
