package org.example.features.analysis_processes.infrastructure.adapters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.features.analysis_processes.domain.entities.AnalysisSession;
import org.example.features.analysis_processes.domain.repositories.AnalysisSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class FileAnalysisSessionRepository implements AnalysisSessionRepository {

    private final ObjectMapper objectMapper;
    private final Path storageFile;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, AnalysisSession> cache = new LinkedHashMap<>();

    public FileAnalysisSessionRepository(
        ObjectMapper mapper,
        @Value("${analysis.sessions.storage-path:data/analysis_sessions.json}") String storagePath
    ) {
        this.objectMapper = mapper.copy()
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
                persist(new ArrayList<>());
            }
            loadFromDisk();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize analysis session storage", e);
        }
    }

    @Override
    public List<AnalysisSession> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(cache.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<AnalysisSession> findByProcessId(String processId) {
        lock.readLock().lock();
        try {
            return cache.values().stream()
                .filter(session -> processId.equals(session.getProcessId()))
                .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<AnalysisSession> findById(String sessionId) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(cache.get(sessionId));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public AnalysisSession save(AnalysisSession session) {
        lock.writeLock().lock();
        try {
            if (session.getId() == null || session.getId().isBlank()) {
                session.setId(UUID.randomUUID().toString());
            }
            if (session.getCreatedAt() == null) {
                session.setCreatedAt(LocalDateTime.now());
            }
            session.setUpdatedAt(LocalDateTime.now());
            cache.put(session.getId(), session);
            persist(new ArrayList<>(cache.values()));
            return session;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadFromDisk() throws IOException {
        lock.writeLock().lock();
        try {
            byte[] data = Files.readAllBytes(storageFile);
            if (data.length == 0) {
                cache.clear();
                return;
            }
            List<AnalysisSession> sessions = objectMapper.readValue(
                data,
                new TypeReference<List<AnalysisSession>>() {}
            );
            cache.clear();
            sessions.forEach(session -> cache.put(session.getId(), session));
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void persist(List<AnalysisSession> sessions) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile.toFile(), sessions);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist analysis sessions", e);
        }
    }
}
