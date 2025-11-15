package org.example.infrastructure.services;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock service for quick start - Replace with real implementations later
 */
@Service
public class MockService {
    
    private final Map<String, String> mockData = new ConcurrentHashMap<>();
    
    public MockService() {
        mockData.put("status", "running");
        mockData.put("version", "1.0.0");
    }
    
    public String getStatus() {
        return mockData.get("status");
    }
    
    public CompletableFuture<String> processRequest(String request) {
        return CompletableFuture.supplyAsync(() -> {
            // Mock processing
            return "Processed: " + request;
        });
    }
}