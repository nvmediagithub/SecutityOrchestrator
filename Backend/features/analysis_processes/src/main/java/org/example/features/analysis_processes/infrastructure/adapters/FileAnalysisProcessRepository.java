package org.example.features.analysis_processes.infrastructure.adapters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.repositories.AnalysisProcessRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * File-based repository that stores analysis processes as JSON.
 * Keeps an in-memory cache synchronized with the on-disk representation.
 */
@Repository
public class FileAnalysisProcessRepository implements AnalysisProcessRepository {

    private final ObjectMapper objectMapper;
    private final Path storageFile;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, AnalysisProcess> cache = new LinkedHashMap<>();

    public FileAnalysisProcessRepository(
        ObjectMapper objectMapper,
        @Value("${analysis.processes.storage-path:data/analysis_processes.json}") String storagePath
    ) {
        this.objectMapper = objectMapper.copy()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.storageFile = Paths.get(storagePath).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() {
        try {
            Path parent = storageFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(storageFile)) {
                persist(defaultSeed());
            }
            loadFromDisk();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize analysis process storage", e);
        }
    }

    @Override
    public List<AnalysisProcess> findAll() {
        lock.readLock().lock();
        try {
            return cache.values().stream()
                .sorted(Comparator.comparing(AnalysisProcess::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toUnmodifiableList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<AnalysisProcess> findById(String id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(cache.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public AnalysisProcess save(AnalysisProcess process) {
        lock.writeLock().lock();
        try {
            if (process.getId() == null || process.getId().isBlank()) {
                process.setId(UUID.randomUUID().toString());
            }
            if (process.getCreatedAt() == null) {
                process.setCreatedAt(LocalDateTime.now());
            }
            cache.put(process.getId(), process);
            persist(new ArrayList<>(cache.values()));
            return process;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteById(String id) {
        lock.writeLock().lock();
        try {
            if (cache.remove(id) != null) {
                persist(new ArrayList<>(cache.values()));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadFromDisk() {
        lock.writeLock().lock();
        try {
            byte[] data = Files.readAllBytes(storageFile);
            if (data.length == 0) {
                cache.clear();
                return;
            }
            List<AnalysisProcess> processes = objectMapper.readValue(
                data,
                new TypeReference<List<AnalysisProcess>>() {}
            );
            cache.clear();
            processes.forEach(process -> cache.put(process.getId(), process));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read analysis processes from storage", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void persist(List<AnalysisProcess> processes) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile.toFile(), processes);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist analysis processes to storage", e);
        }
    }

    private List<AnalysisProcess> defaultSeed() {
        LocalDateTime now = LocalDateTime.now();
        List<AnalysisProcess> seed = new ArrayList<>();
        seed.add(AnalysisProcess.builder()
            .id(UUID.randomUUID().toString())
            .name("Security Baseline Scan")
            .description("Runs OWASP and SAST checks on the latest build")
            .status("running")
            .type("securityAnalysis")
            .createdAt(now.minusMinutes(25))
            .build());
        seed.add(AnalysisProcess.builder()
            .id(UUID.randomUUID().toString())
            .name("Post-release regression")
            .description("Smoke tests across staging clusters")
            .status("completed")
            .type("integrationTest")
            .createdAt(now.minusHours(2))
            .build());
        return seed;
    }
}
