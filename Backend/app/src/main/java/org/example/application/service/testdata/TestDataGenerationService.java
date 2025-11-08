package org.example.application.service.testdata;

import org.example.domain.entities.TestDataSet;
import org.example.domain.model.testdata.enums.GenerationScope;
import org.example.domain.model.testdata.enums.DataType;
import org.example.infrastructure.llm.testdata.TestDataLLMService;
import org.example.infrastructure.llm.testdata.dto.*;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Main service for AI-powered test data generation
 * Orchestrates LLM-based data generation with dependency management and validation
 */
@Service
public class TestDataGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataGenerationService.class);
    
    @Autowired
    private TestDataLLMService llmService;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    // Cache for generated test data
    private final Map<String, CachedTestData> dataCache = new ConcurrentHashMap<>();
    private final Map<String, GenerationStatus> activeGenerations = new ConcurrentHashMap<>();
    
    // Configuration
    private static final int MAX_CACHE_SIZE = 1000;
    private static final long CACHE_TTL_MINUTES = 60;
    private static final int MAX_RETRIES = 3;
    
    /**
     * Main method to generate test data using LLM
     */
    public CompletableFuture<TestDataGenerationResult> generateTestData(
            TestDataGenerationRequest request) {
        
        String generationId = generateGenerationId(request);
        logger.info("Starting AI-powered test data generation: requestId={}, generationId={}, dataType={}", 
                   request.getRequestId(), generationId, request.getDataType());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Update generation status
                updateGenerationStatus(generationId, "STARTED", request);
                
                // Check cache first
                String cacheKey = generateCacheKey(request);
                if (isCacheEnabled() && dataCache.containsKey(cacheKey)) {
                    CachedTestData cached = dataCache.get(cacheKey);
                    if (isCacheValid(cached)) {
                        logger.info("Returning cached test data for request: {}", request.getRequestId());
                        TestDataGenerationResult cachedResult = cached.getResult();
                        cachedResult.setGenerationId(generationId);
                        updateGenerationStatus(generationId, "COMPLETED", request);
                        return cachedResult;
                    }
                }
                
                // Generate data using LLM
                TestDataGenerationResult result = executeLLMGeneration(request, generationId);
                
                // Validate quality if requested
                if (request.isEnableValidation() && result.isSuccessful()) {
                    result = enhanceWithValidation(result, request);
                }
                
                // Cache successful results
                if (result.isSuccessful() && isCacheEnabled()) {
                    cacheResult(cacheKey, result);
                }
                
                // Update final status
                updateGenerationStatus(generationId, "COMPLETED", request);
                
                logger.info("AI test data generation completed: requestId={}, recordsGenerated={}, qualityScore={}", 
                           request.getRequestId(), 
                           result.getDataRecords() != null ? result.getDataRecords().size() : 0,
                           result.getQualityReport() != null ? 
                               String.format("%.1f", result.getQualityReport().getOverallScore()) : "N/A");
                
                return result;
                
            } catch (Exception e) {
                logger.error("AI test data generation failed: requestId={}", request.getRequestId(), e);
                
                TestDataGenerationResult errorResult = new TestDataGenerationResult();
                errorResult.setGenerationId(generationId);
                errorResult.setRequestId(request.getRequestId());
                errorResult.setSuccessful(false);
                errorResult.setErrorMessage("Generation failed: " + e.getMessage());
                errorResult.setGeneratedAt(LocalDateTime.now());
                
                updateGenerationStatus(generationId, "FAILED", request, e.getMessage());
                return errorResult;
            }
        });
    }
    
    /**
     * Generate test data with context awareness (OpenAPI, BPMN, etc.)
     */
    public CompletableFuture<TestDataGenerationResult> generateContextualTestData(
            TestDataGenerationRequest request, String integrationType, Object contextData) {
        
        logger.info("Generating contextual test data: requestId={}, integrationType={}, dataType={}", 
                   request.getRequestId(), integrationType, request.getDataType());
        
        // Enhanced request with context
        TestDataGenerationRequest enhancedRequest = enhanceRequestWithContext(request, integrationType, contextData);
        
        return generateTestData(enhancedRequest);
    }
    
    /**
     * Generate test data for specific test scenario
     */
    public CompletableFuture<TestDataGenerationResult> generateForTest(
            String testId, TestDataGenerationRequest request) {
        
        logger.info("Generating test data for specific test: testId={}, requestId={}", testId, request.getRequestId());
        
        // Add test context to request
        request.getContext().put("testId", testId);
        request.getContext().put("scenario", "test_specific");
        
        return generateTestData(request);
    }
    
    /**
     * Bulk generation of test data
     */
    public CompletableFuture<List<TestDataGenerationResult>> generateBulk(
            List<TestDataGenerationRequest> requests) {
        
        logger.info("Starting bulk test data generation: {} requests", requests.size());
        
        return CompletableFuture.supplyAsync(() -> {
            return requests.parallelStream()
                .map(request -> generateTestData(request).join())
                .collect(Collectors.toList());
        });
    }
    
    /**
     * Validate generated test data quality
     */
    public CompletableFuture<TestDataValidationResult> validateGeneratedData(
            TestDataGenerationResult result, List<String> validationRules) {
        
        logger.info("Validating generated test data: resultId={}, rulesCount={}", 
                   result.getGenerationId(), validationRules.size());
        
        return llmService.validateTestData(result, validationRules);
    }
    
    /**
     * Get generation status
     */
    public GenerationStatus getGenerationStatus(String generationId) {
        return activeGenerations.get(generationId);
    }
    
    /**
     * Clear generation cache
     */
    public void clearCache() {
        dataCache.clear();
        logger.info("Test data generation cache cleared");
    }
    
    /**
     * Get service statistics
     */
    public Map<String, Object> getServiceStatistics() {
        TestDataServiceStatistics stats = llmService.getServiceStatistics();
        
        Map<String, Object> serviceStats = new HashMap<>();
        serviceStats.put("activeGenerations", activeGenerations.size());
        serviceStats.put("cachedResults", dataCache.size());
        serviceStats.put("cacheHitRate", calculateCacheHitRate());
        serviceStats.put("averageQualityScore", stats.getAverageQualityScore());
        serviceStats.put("isHealthy", stats.isHealthy());
        serviceStats.put("lastUpdated", LocalDateTime.now());
        
        return serviceStats;
    }
    
    // Private helper methods
    
    private TestDataGenerationResult executeLLMGeneration(
            TestDataGenerationRequest request, String generationId) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Use the LLM service for generation
            TestDataGenerationResult result = llmService.generateTestData(request).join();
            
            // Add performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            result.setGenerationTimeMs(executionTime);
            
            // Set provider info
            result.setProvider(getLLMProvider());
            result.setModel(getLLMModel());
            
            return result;
            
        } catch (Exception e) {
            logger.error("LLM generation failed for request: {}", request.getRequestId(), e);
            throw new RuntimeException("LLM generation failed", e);
        }
    }
    
    private TestDataGenerationResult enhanceWithValidation(
            TestDataGenerationResult result, TestDataGenerationRequest request) {
        
        try {
            // Perform quality analysis
            if (result.getQualityReport() == null) {
                TestDataQualityReport qualityReport = llmService.analyzeDataQuality(result).join();
                result.setQualityReport(qualityReport);
            }
            
            // Perform validation if rules are provided
            if (request.getValidationRules() != null && !request.getValidationRules().isEmpty()) {
                TestDataValidationResult validation = llmService.validateTestData(
                    result, request.getValidationRules()).join();
                result.setValidationResult(validation);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.warn("Quality analysis failed for result: {}", result.getGenerationId(), e);
            result.addWarning("Quality analysis failed: " + e.getMessage());
            return result;
        }
    }
    
    private TestDataGenerationRequest enhanceRequestWithContext(
            TestDataGenerationRequest request, String integrationType, Object contextData) {
        
        // Create enhanced request copy
        TestDataGenerationRequest enhanced = new TestDataGenerationRequest();
        enhanced.setRequestId(request.getRequestId());
        enhanced.setDataType(request.getDataType());
        enhanced.setGenerationScope(request.getGenerationScope());
        enhanced.setRecordCount(request.getRecordCount());
        enhanced.setQualityLevel(request.getQualityLevel());
        enhanced.setEnableLLM(request.isEnableLLM());
        enhanced.setTimeoutMinutes(request.getTimeoutMinutes());
        
        // Add integration context
        enhanced.getContext().put("integrationType", integrationType);
        enhanced.getContext().put("contextData", contextData);
        enhanced.getContext().put("enhanced", true);
        
        return enhanced;
    }
    
    private boolean isCacheEnabled() {
        return true; // Could be made configurable
    }
    
    private boolean isCacheValid(CachedTestData cached) {
        if (cached == null || cached.getCachedAt() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(cached.getCachedAt().plusMinutes(CACHE_TTL_MINUTES));
    }
    
    private void cacheResult(String cacheKey, TestDataGenerationResult result) {
        // Implement LRU cache eviction if needed
        if (dataCache.size() >= MAX_CACHE_SIZE) {
            String oldestKey = dataCache.keySet().iterator().next();
            dataCache.remove(oldestKey);
        }
        
        dataCache.put(cacheKey, new CachedTestData(result, LocalDateTime.now()));
    }
    
    private double calculateCacheHitRate() {
        int total = dataCache.size();
        if (total == 0) return 0.0;
        
        // Simple hit rate calculation
        return Math.min(100.0, (double) total / MAX_CACHE_SIZE * 100);
    }
    
    private String generateGenerationId(TestDataGenerationRequest request) {
        return "gen_" + request.getRequestId() + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(TestDataGenerationRequest request) {
        return "cache_" + request.getDataType() + "_" + 
               request.getGenerationScope().hashCode() + "_" +
               request.getQualityLevel().hashCode();
    }
    
    private void updateGenerationStatus(String generationId, String status, 
                                      TestDataGenerationRequest request) {
        updateGenerationStatus(generationId, status, request, null);
    }
    
    private void updateGenerationStatus(String generationId, String status, 
                                      TestDataGenerationRequest request, String message) {
        GenerationStatus genStatus = new GenerationStatus();
        genStatus.setGenerationId(generationId);
        genStatus.setStatus(status);
        genStatus.setRequestId(request.getRequestId());
        genStatus.setDataType(request.getDataType());
        genStatus.setRecordCount(request.getRecordCount());
        genStatus.setStartedAt(LocalDateTime.now());
        if (message != null) {
            genStatus.setMessage(message);
        }
        
        activeGenerations.put(generationId, genStatus);
    }
    
    private String getLLMProvider() {
        return openRouterClient.hasApiKey() ? "OPENROUTER" : "LOCAL";
    }
    
    private String getLLMModel() {
        return openRouterClient.hasApiKey() ? "gpt-4" : "local-model";
    }
    
    // Inner classes for status tracking
    public static class GenerationStatus {
        private String generationId;
        private String status;
        private String requestId;
        private String dataType;
        private int recordCount;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String message;
        
        // Getters and Setters
        public String getGenerationId() { return generationId; }
        public void setGenerationId(String generationId) { this.generationId = generationId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        
        public int getRecordCount() { return recordCount; }
        public void setRecordCount(int recordCount) { this.recordCount = recordCount; }
        
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public boolean isCompleted() {
            return "COMPLETED".equals(status) || "FAILED".equals(status);
        }
    }
}