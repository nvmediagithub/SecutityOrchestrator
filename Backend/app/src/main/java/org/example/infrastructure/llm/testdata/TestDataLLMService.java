package org.example.infrastructure.llm.testdata;

import org.example.domain.entities.LLMProvider;
import org.example.infrastructure.config.LLMConfig;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.infrastructure.llm.testdata.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Main LLM service for intelligent test data generation
 * Integrates with existing LLM providers and provides specialized test data generation capabilities
 */
@Service
public class TestDataLLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataLLMService.class);
    
    @Autowired
    private TestDataLLMConfig config;
    
    @Autowired
    private LLMTestDataPrompts prompts;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private Executor testDataExecutor;
    
    // Cache for generated data
    private final Map<String, CachedTestData> dataCache = new ConcurrentHashMap<>();
    private final Map<String, TestDataGenerationStatus> activeGenerations = new ConcurrentHashMap<>();
    
    // Configuration
    private static final int MAX_RETRIES = 3;
    private static final long CACHE_TTL_MINUTES = 60;
    private static final long GENERATION_TIMEOUT_MINUTES = 15;
    
    /**
     * Generates intelligent test data using LLM
     */
    @Async
    public CompletableFuture<TestDataGenerationResult> generateTestData(TestDataGenerationRequest request) {
        String generationId = generateGenerationId(request);
        logger.info("Starting test data generation for request: {}, generationId: {}", request.getRequestId(), generationId);
        
        try {
            activeGenerations.put(generationId, new TestDataGenerationStatus(generationId, "STARTED", LocalDateTime.now()));
            
            // Check cache first
            String cacheKey = generateCacheKey(request);
            if (config.isEnableCaching() && dataCache.containsKey(cacheKey)) {
                CachedTestData cached = dataCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached test data for request: {}", request.getRequestId());
                    return CompletableFuture.completedFuture(cached.getResult());
                }
            }
            
            // Generate test data
            TestDataGenerationResult result = executeDataGeneration(request, generationId);
            
            // Cache the result
            if (config.isEnableCaching() && result.isSuccessful()) {
                cacheResult(cacheKey, result);
            }
            
            // Update status
            activeGenerations.put(generationId, 
                new TestDataGenerationStatus(generationId, "COMPLETED", LocalDateTime.now()));
            
            logger.info("Test data generation completed for request: {}, generationId: {}", 
                request.getRequestId(), generationId);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            logger.error("Test data generation failed for request: {}", request.getRequestId(), e);
            activeGenerations.put(generationId, 
                new TestDataGenerationStatus(generationId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Analyzes data quality of generated test data
     */
    @Async
    public CompletableFuture<TestDataQualityReport> analyzeDataQuality(TestDataGenerationResult result) {
        logger.info("Analyzing data quality for generation result: {}", result.getGenerationId());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateGenerationStatus(result.getGenerationId(), "QUALITY_ANALYSIS");
                
                String prompt = prompts.buildQualityAnalysisPrompt(result);
                String llmResponse = executeLLMAnalysis(prompt, "quality");
                
                TestDataQualityReport report = parseQualityReport(llmResponse);
                report.setAnalysisId(result.getGenerationId());
                report.setAnalyzedAt(LocalDateTime.now());
                
                logger.info("Data quality analysis completed for result: {}, score: {}", 
                    result.getGenerationId(), report.getOverallScore());
                
                return report;
                
            } catch (Exception e) {
                logger.error("Data quality analysis failed for result: {}", result.getGenerationId(), e);
                throw new RuntimeException("Data quality analysis failed", e);
            }
        }, testDataExecutor);
    }
    
    /**
     * Validates test data against business rules and constraints
     */
    @Async
    public CompletableFuture<TestDataValidationResult> validateTestData(TestDataGenerationResult result, 
                                                                        List<String> validationRules) {
        logger.info("Validating test data against {} rules for result: {}", 
            validationRules.size(), result.getGenerationId());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateGenerationStatus(result.getGenerationId(), "VALIDATION");
                
                String prompt = prompts.buildValidationPrompt(result, validationRules);
                String llmResponse = executeLLMAnalysis(prompt, "validation");
                
                TestDataValidationResult validation = parseValidationResult(llmResponse);
                validation.setValidationId(result.getGenerationId());
                validation.setValidatedAt(LocalDateTime.now());
                
                logger.info("Test data validation completed for result: {}, passed: {}", 
                    result.getGenerationId(), validation.isValid());
                
                return validation;
                
            } catch (Exception e) {
                logger.error("Test data validation failed for result: {}", result.getGenerationId(), e);
                throw new RuntimeException("Test data validation failed", e);
            }
        }, testDataExecutor);
    }
    
    /**
     * Gets generation status
     */
    public TestDataGenerationStatus getGenerationStatus(String generationId) {
        return activeGenerations.get(generationId);
    }
    
    /**
     * Clears the data cache
     */
    public void clearCache() {
        dataCache.clear();
        logger.info("Test data cache cleared");
    }
    
    /**
     * Gets service statistics
     */
    public TestDataServiceStatistics getServiceStatistics() {
        TestDataServiceStatistics stats = new TestDataServiceStatistics();
        stats.setActiveGenerations(activeGenerations.size());
        stats.setCachedResults(dataCache.size());
        return stats;
    }
    
    /**
     * Executes the actual data generation
     */
    private TestDataGenerationResult executeDataGeneration(TestDataGenerationRequest request, String generationId) {
        try {
            updateGenerationStatus(generationId, "GENERATING");
            
            // Build the generation prompt
            String prompt = prompts.buildGenerationPrompt(request);
            
            // Execute LLM analysis
            String llmResponse = executeLLMAnalysis(prompt, "generation");
            
            // Parse the response
            TestDataGenerationResult result = parseGenerationResult(llmResponse, request);
            result.setGenerationId(generationId);
            result.setGeneratedAt(LocalDateTime.now());
            
            // Validate quality if enabled
            if (config.isEnableQualityValidation()) {
                TestDataQualityReport qualityReport = analyzeDataQuality(result).join();
                result.setQualityReport(qualityReport);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Data generation execution failed for request: {}", request.getRequestId(), e);
            
            TestDataGenerationResult errorResult = new TestDataGenerationResult();
            errorResult.setGenerationId(generationId);
            errorResult.setRequestId(request.getRequestId());
            errorResult.setSuccessful(false);
            errorResult.setErrorMessage("Generation failed: " + e.getMessage());
            errorResult.setGeneratedAt(LocalDateTime.now());
            
            return errorResult;
        }
    }
    
    /**
     * Executes LLM analysis with fallback providers
     */
    private String executeLLMAnalysis(String prompt, String analysisType) {
        String provider = config.getDefaultProvider();
        
        // Try primary provider
        try {
            if ("OPENROUTER".equalsIgnoreCase(provider) && openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} test data analysis", analysisType);
                return openRouterClient.chatCompletion(
                    config.getOpenRouterModel(), 
                    prompt, 
                    config.getOpenRouterMaxTokens(), 
                    config.getOpenRouterTemperature()
                ).get(config.getOpenRouterTimeout(), TimeUnit.SECONDS)
                 .getResponse();
            } else if ("LOCAL".equalsIgnoreCase(provider)) {
                logger.debug("Using local LLM for {} test data analysis", analysisType);
                return localLLMService.localChatCompletion(
                    config.getLocalModel(), 
                    prompt, 
                    config.getLocalMaxTokens(), 
                    config.getLocalTemperature()
                ).get(config.getLocalTimeout(), TimeUnit.SECONDS)
                 .getResponse();
            }
        } catch (Exception e) {
            logger.warn("Primary provider failed for {} test data analysis", analysisType, e);
        }
        
        // Try fallback provider
        if (config.isEnableFallback()) {
            try {
                String fallbackProvider = config.getFallbackProvider();
                if ("OPENROUTER".equalsIgnoreCase(fallbackProvider) && openRouterClient.hasApiKey()) {
                    logger.debug("Using OpenRouter fallback for {} test data analysis", analysisType);
                    return openRouterClient.chatCompletion(
                        config.getOpenRouterModel(), 
                        prompt, 
                        config.getOpenRouterMaxTokens(), 
                        config.getOpenRouterTemperature()
                    ).get(config.getFallbackTimeout(), TimeUnit.SECONDS)
                     .getResponse();
                } else if ("LOCAL".equalsIgnoreCase(fallbackProvider)) {
                    logger.debug("Using local LLM fallback for {} test data analysis", analysisType);
                    return localLLMService.localChatCompletion(
                        config.getLocalModel(), 
                        prompt, 
                        config.getLocalMaxTokens(), 
                        config.getLocalTemperature()
                    ).get(config.getFallbackTimeout(), TimeUnit.SECONDS)
                     .getResponse();
                }
            } catch (Exception e) {
                logger.error("Fallback provider failed for {} test data analysis", analysisType, e);
            }
        }
        
        throw new RuntimeException("No LLM providers available for test data analysis");
    }
    
    /**
     * Parses LLM response into TestDataGenerationResult
     */
    private TestDataGenerationResult parseGenerationResult(String llmResponse, TestDataGenerationRequest request) {
        TestDataGenerationResult result = new TestDataGenerationResult();
        result.setRequestId(request.getRequestId());
        result.setSuccessful(true);
        result.setGeneratedData(llmResponse);
        
        // Extract metadata from response
        Map<String, Object> metadata = extractMetadata(llmResponse);
        result.setMetadata(metadata);
        
        // Parse data structure
        List<Map<String, Object>> dataRecords = parseDataRecords(llmResponse);
        result.setDataRecords(dataRecords);
        
        // Extract generation metrics
        GenerationMetrics metrics = extractGenerationMetrics(llmResponse);
        result.setMetrics(metrics);
        
        return result;
    }
    
    /**
     * Parses quality report from LLM response
     */
    private TestDataQualityReport parseQualityReport(String llmResponse) {
        TestDataQualityReport report = new TestDataQualityReport();
        report.setOverallScore(extractQualityScore(llmResponse));
        report.setRecommendations(extractRecommendations(llmResponse));
        report.setIssues(extractQualityIssues(llmResponse));
        return report;
    }
    
    /**
     * Parses validation result from LLM response
     */
    private TestDataValidationResult parseValidationResult(String llmResponse) {
        TestDataValidationResult validation = new TestDataValidationResult();
        validation.setValid(extractValidationStatus(llmResponse));
        validation.setViolations(extractValidationViolations(llmResponse));
        validation.setSuggestions(extractValidationSuggestions(llmResponse));
        return validation;
    }
    
    // Helper methods
    
    private String generateGenerationId(TestDataGenerationRequest request) {
        return "tdg_" + request.getRequestId() + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(TestDataGenerationRequest request) {
        return "tdg_" + request.getRequestId() + "_" + request.getDataType() + "_" + 
               request.getGenerationScope().hashCode();
    }
    
    private void updateGenerationStatus(String generationId, String status) {
        activeGenerations.put(generationId, new TestDataGenerationStatus(generationId, status, LocalDateTime.now()));
    }
    
    private boolean isCacheValid(CachedTestData cached) {
        if (cached == null || cached.getCachedAt() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(cached.getCachedAt().plusMinutes(CACHE_TTL_MINUTES));
    }
    
    private void cacheResult(String cacheKey, TestDataGenerationResult result) {
        dataCache.put(cacheKey, new CachedTestData(result, LocalDateTime.now()));
    }
    
    // Placeholder methods for parsing - would be implemented with proper JSON/markdown parsing
    private Map<String, Object> extractMetadata(String llmResponse) {
        // Implementation would parse metadata from LLM response
        return new HashMap<>();
    }
    
    private List<Map<String, Object>> parseDataRecords(String llmResponse) {
        // Implementation would parse data records from LLM response
        return new ArrayList<>();
    }
    
    private GenerationMetrics extractGenerationMetrics(String llmResponse) {
        // Implementation would extract metrics from LLM response
        return new GenerationMetrics();
    }
    
    private int extractQualityScore(String llmResponse) {
        // Implementation would extract quality score from LLM response
        return 80; // Default score
    }
    
    private List<String> extractRecommendations(String llmResponse) {
        // Implementation would extract recommendations from LLM response
        return new ArrayList<>();
    }
    
    private List<String> extractQualityIssues(String llmResponse) {
        // Implementation would extract quality issues from LLM response
        return new ArrayList<>();
    }
    
    private boolean extractValidationStatus(String llmResponse) {
        // Implementation would extract validation status from LLM response
        return true; // Default to valid
    }
    
    private List<String> extractValidationViolations(String llmResponse) {
        // Implementation would extract validation violations from LLM response
        return new ArrayList<>();
    }
    
    private List<String> extractValidationSuggestions(String llmResponse) {
        // Implementation would extract validation suggestions from LLM response
        return new ArrayList<>();
    }
}