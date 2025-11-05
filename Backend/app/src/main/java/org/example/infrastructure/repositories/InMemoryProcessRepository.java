package org.example.infrastructure.repositories;

import org.example.domain.entities.Process;
import org.example.domain.valueobjects.ProcessId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryProcessRepository {

    private final Map<String, Process> processes = new HashMap<>();

    public InMemoryProcessRepository() {
        // Initialize with mock data
        Process mockProcess = Process.createMockProcess();
        processes.put(mockProcess.id.getValue(), mockProcess);
    }

    public Optional<Process> findById(org.example.domain.valueobjects.ProcessId id) {
        return Optional.ofNullable(processes.get(id.getValue()));
    }

    public List<Process> findAll() {
        return processes.values().stream().toList();
    }

    public Process save(Process process) {
        processes.put(process.id.getValue(), process);
        return process;
    }

    public void delete(org.example.domain.valueobjects.ProcessId id) {
        processes.remove(id.getValue());
    }
}