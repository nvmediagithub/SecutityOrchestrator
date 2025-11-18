package org.example.features.analysis-processes.infrastructure.adapters;

import org.example.features.analysis-processes.domain.entities.AnalysisProcess;
import org.example.features.analysis-processes.domain.repositories.AnalysisProcessRepository;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryAnalysisProcessRepository implements AnalysisProcessRepository {

    private final Map<String, AnalysisProcess> storage = new ConcurrentHashMap<>();

    @PostConstruct
    void seedData() {
        storage.clear();
        save(AnalysisProcess.builder()
            .id(UUID.randomUUID().toString())
            .name("Security Baseline Scan")
            .description("Runs OWASP checks on the latest build")
            .status("running")
            .type("securityAnalysis")
            .createdAt(LocalDateTime.now().minusMinutes(20))
            .build());

        save(AnalysisProcess.builder()
            .id(UUID.randomUUID().toString())
            .name("Post-release regression")
            .description("Runs smoke tests against staging")
            .status("completed")
            .type("integrationTest")
            .createdAt(LocalDateTime.now().minusHours(2))
            .build());
    }

    @Override
    public List<AnalysisProcess> findAll() {
        return storage.values().stream()
            .sorted(Comparator.comparing(AnalysisProcess::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public Optional<AnalysisProcess> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public AnalysisProcess save(AnalysisProcess process) {
        if (process.getId() == null || process.getId().isBlank()) {
            process.setId(UUID.randomUUID().toString());
        }
        storage.put(process.getId(), process);
        return process;
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }
}
