package org.example.features.analysis-processes.domain.services;

import org.example.features.analysis-processes.domain.entities.AnalysisProcess;
import org.example.features.analysis-processes.domain.repositories.AnalysisProcessRepository;
import org.springframework.stereotype.Service;

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
        return repository.save(process);
    }

    public Optional<AnalysisProcess> updateProcess(String id, AnalysisProcess updatedProcess) {
        Optional<AnalysisProcess> existingProcess = repository.findById(id);
        if (existingProcess.isPresent()) {
            updatedProcess.setId(id);
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
}