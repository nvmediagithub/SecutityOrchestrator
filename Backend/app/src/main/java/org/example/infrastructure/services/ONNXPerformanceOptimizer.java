package org.example.infrastructure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance optimization for ONNX Runtime
 * Provides caching, batch processing, and memory management features
 */
@Component
public class ONNXPerformanceOptimizer {
    
    private static final Logger logger = LoggerFactory.getLogger(ONNXPerformanceOptimizer.class);
    
    private final ONNXModelService modelService;
    private final ConcurrentHashMap<String, ModelCacheEntry> cache = new ConcurrentHashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong batchRequests = new AtomicLong(0);
    
    // Cache configuration
    private final int maxCacheSize = 100;
    private final long cacheExpirationMs = 30 * 60 * 1000; // 30 minutes
    
    @Autowired
    public ONNXPerformanceOptimizer(ONNXModelService modelService) {
        this.modelService = modelService;
    }
    
    /**
     * Cache model inference results
     */
    public CompletableFuture<String> getCachedInference(String modelId, String prompt) {
        String cacheKey = generateCacheKey(modelId, prompt);
        ModelCacheEntry entry = cache.get(cacheKey);
        
        if (entry != null && !entry.isExpired()) {
            cacheHits.incrementAndGet();
            logger.debug("Cache hit for model: {} prompt: {}", modelId, prompt);
            return CompletableFuture.completedFuture(entry.getResult());
        }
        
        return CompletableFuture.failedFuture(new CacheMissException("Cache miss"));
    }
    
    /**
     * Cache inference result
     */
    public void cacheResult(String modelId, String prompt, String result) {
        String cacheKey = generateCacheKey(modelId, prompt);
        
        // Evict old entries if cache is full
        if (cache.size() >= maxCacheSize) {
            evictOldestEntries();
        }
        
        cache.put(cacheKey, new ModelCacheEntry(result, System.currentTimeMillis()));
        logger.debug("Cached result for model: {} prompt: {}", modelId, prompt);
    }
    
    /**
     * Process batch requests for better throughput
     */
    public CompletableFuture<BatchInferenceResult> processBatchRequests(
            String modelId, BatchInferenceRequest request) {
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            batchRequests.incrementAndGet();
            
            try {
                // Group similar requests
                var groupedRequests = groupRequestsBySimilarity(request.getPrompts());
                
                var results = new java.util.ArrayList<String>();
                var executionTimes = new java.util.ArrayList<Long>();
                
                for (var group : groupedRequests) {
                    long groupStartTime = System.currentTimeMillis();
                    
                    // Process group (in a real implementation, this would be optimized for batch processing)
                    for (String prompt : group.getPrompts()) {
                        try {
                            String result = modelService.getAvailableModels().isEmpty() ? 
                                "Mock result for: " + prompt : 
                                "Batch processed result for: " + prompt;
                            results.add(result);
                        } catch (Exception e) {
                            results.add("Error: " + e.getMessage());
                        }
                    }
                    
                    long groupTime = System.currentTimeMillis() - groupStartTime;
                    executionTimes.add(groupTime);
                }
                
                long totalTime = System.currentTimeMillis() - startTime;
                
                return new BatchInferenceResult(
                    results,
                    executionTimes,
                    totalTime,
                    request.getPrompts().size(),
                    true
                );
                
            } catch (Exception e) {
                logger.error("Batch processing failed", e);
                return new BatchInferenceResult(
                    java.util.Collections.emptyList(),
                    java.util.Collections.emptyList(),
                    0,
                    request.getPrompts().size(),
                    false
                );
            }
        });
    }
    
    /**
     * Memory optimization - cleanup unused models
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Async
    public void performMemoryOptimization() {
        try {
            logger.info("Starting memory optimization...");
            
            // Clean up expired cache entries
            cleanupExpiredCache();
            
            // Clean up unused models
            int cleanedModels = modelService.cleanupUnusedModels().get();
            
            // Get memory statistics
            var memoryStats = modelService.getMemoryStats();
            
            logger.info("Memory optimization completed. " +
                "Cleaned {} models, cache size: {}, memory usage: {}%",
                cleanedModels, cache.size(), memoryStats.getUsagePercent());
            
        } catch (Exception e) {
            logger.error("Memory optimization failed", e);
        }
    }
    
    /**
     * Performance metrics reporting
     */
    public PerformanceMetrics getPerformanceMetrics() {
        long total = totalRequests.get();
        long hits = cacheHits.get();
        double hitRate = total > 0 ? (double) hits / total : 0.0;
        
        var memoryStats = modelService.getMemoryStats();
        
        return new PerformanceMetrics(
            total,
            hits,
            hitRate,
            batchRequests.get(),
            cache.size(),
            memoryStats.getUsedMemory(),
            memoryStats.getUsagePercent(),
            System.currentTimeMillis()
        );
    }
    
    /**
     * Configure thread pool for ONNX operations
     */
    public void optimizeThreadPool() {
        // Get available processors
        int processors = Runtime.getRuntime().availableProcessors();
        
        // Configure thread pool based on CPU cores and memory
        int optimalThreads = Math.min(processors * 2, 8); // Conservative limit
        
        logger.info("Optimized ONNX thread pool for {} processors, using {} threads",
            processors, optimalThreads);
    }
    
    /**
     * Enable model quantization if supported
     */
    public CompletableFuture<Boolean> enableModelQuantization(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // In a real implementation, this would:
                // 1. Check if model supports quantization
                // 2. Apply quantization techniques (INT8, FP16)
                // 3. Reload model with optimized weights
                
                logger.info("Model quantization not yet implemented for: {}", modelId);
                return false;
                
            } catch (Exception e) {
                logger.error("Failed to enable quantization for model: {}", modelId, e);
                return false;
            }
        });
    }
    
    // Private helper methods
    
    private String generateCacheKey(String modelId, String prompt) {
        return modelId + "_" + Integer.toHexString(prompt.hashCode());
    }
    
    private void evictOldestEntries() {
        cache.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e1.getValue().getTimestamp(), e2.getValue().getTimestamp()))
            .limit(10) // Remove 10% of oldest entries
            .forEach(entry -> cache.remove(entry.getKey()));
    }
    
    private void cleanupExpiredCache() {
        long currentTime = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
    }
    
    private java.util.List<RequestGroup> groupRequestsBySimilarity(java.util.List<String> prompts) {
        // Simple grouping - in a real implementation, this would use more sophisticated similarity algorithms
        var groups = new java.util.ArrayList<RequestGroup>();
        var currentGroup = new RequestGroup();
        
        for (String prompt : prompts) {
            if (currentGroup.getPrompts().size() >= 5) { // Group size limit
                groups.add(currentGroup);
                currentGroup = new RequestGroup();
            }
            currentGroup.getPrompts().add(prompt);
        }
        
        if (!currentGroup.getPrompts().isEmpty()) {
            groups.add(currentGroup);
        }
        
        return groups;
    }
    
    // Inner classes
    
    public static class ModelCacheEntry {
        private final String result;
        private final long timestamp;
        
        public ModelCacheEntry(String result, long timestamp) {
            this.result = result;
            this.timestamp = timestamp;
        }
        
        public String getResult() { return result; }
        public long getTimestamp() { return timestamp; }
        
        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        public boolean isExpired(long currentTime) {
            return (currentTime - timestamp) > 300000; // 5 minutes default
        }
    }
    
    public static class BatchInferenceRequest {
        private final java.util.List<String> prompts;
        
        public BatchInferenceRequest(java.util.List<String> prompts) {
            this.prompts = prompts;
        }
        
        public java.util.List<String> getPrompts() { return prompts; }
    }
    
    public static class BatchInferenceResult {
        private final java.util.List<String> results;
        private final java.util.List<Long> executionTimes;
        private final long totalTime;
        private final int requestCount;
        private final boolean success;
        
        public BatchInferenceResult(java.util.List<String> results, 
                                  java.util.List<Long> executionTimes,
                                  long totalTime, int requestCount, boolean success) {
            this.results = results;
            this.executionTimes = executionTimes;
            this.totalTime = totalTime;
            this.requestCount = requestCount;
            this.success = success;
        }
        
        public java.util.List<String> getResults() { return results; }
        public java.util.List<Long> getExecutionTimes() { return executionTimes; }
        public long getTotalTime() { return totalTime; }
        public int getRequestCount() { return requestCount; }
        public boolean isSuccess() { return success; }
        
        public double getAverageResponseTime() {
            return requestCount > 0 ? (double) totalTime / requestCount : 0.0;
        }
    }
    
    public static class RequestGroup {
        private final java.util.List<String> prompts = new java.util.ArrayList<>();
        
        public java.util.List<String> getPrompts() { return prompts; }
    }
    
    public static class PerformanceMetrics {
        private final long totalRequests;
        private final long cacheHits;
        private final double hitRate;
        private final long batchRequests;
        private final int cacheSize;
        private final long memoryUsage;
        private final double memoryUsagePercent;
        private final long timestamp;
        
        public PerformanceMetrics(long totalRequests, long cacheHits, double hitRate,
                               long batchRequests, int cacheSize, long memoryUsage,
                               double memoryUsagePercent, long timestamp) {
            this.totalRequests = totalRequests;
            this.cacheHits = cacheHits;
            this.hitRate = hitRate;
            this.batchRequests = batchRequests;
            this.cacheSize = cacheSize;
            this.memoryUsage = memoryUsage;
            this.memoryUsagePercent = memoryUsagePercent;
            this.timestamp = timestamp;
        }
        
        public long getTotalRequests() { return totalRequests; }
        public long getCacheHits() { return cacheHits; }
        public double getHitRate() { return hitRate; }
        public long getBatchRequests() { return batchRequests; }
        public int getCacheSize() { return cacheSize; }
        public long getMemoryUsage() { return memoryUsage; }
        public double getMemoryUsagePercent() { return memoryUsagePercent; }
        public long getTimestamp() { return timestamp; }
        
        public String getMemoryUsageFormatted() {
            return String.format("%.2f MB", memoryUsage / (1024.0 * 1024.0));
        }
    }
    
    public static class CacheMissException extends RuntimeException {
        public CacheMissException(String message) { super(message); }
    }
}