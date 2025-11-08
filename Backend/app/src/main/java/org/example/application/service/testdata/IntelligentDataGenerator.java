package org.example.application.service.testdata;

import org.example.domain.model.testdata.*;
import org.example.domain.model.testdata.enums.GenerationScope;
import org.example.infrastructure.llm.testdata.*;
import org.example.infrastructure.llm.testdata.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Intelligent Data Generator - основной сервис для генерации умных тестовых данных
 * Интегрируется с LLMProvider и обеспечивает высококачественную генерацию данных
 */
@Service
public class IntelligentDataGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(IntelligentDataGenerator.class);
    
    @Autowired
    private TestDataLLMService testDataLLMService;
    
    @Autowired
    private TestDataLLMConfig llmConfig;
    
    @Autowired
    private Executor dataGenerationExecutor;
    
    // Cache for profiles
    private final Map<String, DataGeneratorProfile> profileCache = new ConcurrentHashMap<>();
    
    /**
     * Генерирует интеллектуальные тестовые данные
     */
    @Async
    public CompletableFuture<IntelligentTestDataResult> generateIntelligentData(IntelligentDataGenerationRequest request) {
        logger.info("Starting intelligent data generation for request: {}", request.getRequestId());
        
        try {
            // 1. Подготавливаем профиль генерации
            DataGeneratorProfile profile = prepareGenerationProfile(request);
            
            // 2. Создаем LLM запрос
            TestDataGenerationRequest llmRequest = createLLMRequest(request, profile);
            
            // 3. Выполняем генерацию через LLM
            TestDataGenerationResult llmResult = testDataLLMService.generateTestData(llmRequest).join();
            
            // 4. Анализируем качество данных
            TestDataQualityReport qualityReport = testDataLLMService.analyzeDataQuality(llmResult).join();
            
            // 5. Валидируем данные
            TestDataValidationResult validationResult = testDataLLMService.validateTestData(llmResult, request.getValidationRules()).join();
            
            // 6. Создаем финальный результат
            IntelligentTestDataResult result = createIntelligentResult(request, profile, llmResult, qualityReport, validationResult);
            
            logger.info("Intelligent data generation completed for request: {}, quality score: {}", 
                request.getRequestId(), qualityReport.getOverallScore());
            
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            logger.error("Intelligent data generation failed for request: {}", request.getRequestId(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Генерирует данные с анализом качества
     */
    @Async
    public CompletableFuture<IntelligentTestDataResult> generateWithQualityAnalysis(IntelligentDataGenerationRequest request) {
        return generateIntelligentData(request);
    }
    
    /**
     * Генерирует данные с валидацией
     */
    @Async
    public CompletableFuture<IntelligentTestDataResult> generateWithValidation(IntelligentDataGenerationRequest request) {
        return generateIntelligentData(request);
    }
    
    /**
     * Генерирует данные в батчах для больших объемов
     */
    @Async
    public CompletableFuture<IntelligentTestDataResult> generateBatchData(IntelligentDataGenerationRequest request) {
        logger.info("Starting batch data generation for request: {}", request.getRequestId());
        
        try {
            int batchSize = llmConfig.getDefaultBatchSize();
            int totalRecords = request.getRecordCount();
            int numberOfBatches = (int) Math.ceil((double) totalRecords / batchSize);
            
            List<CompletableFuture<IntelligentTestDataResult>> batchFutures = new ArrayList<>();
            
            for (int i = 0; i < numberOfBatches; i++) {
                int batchStart = i * batchSize;
                int batchEnd = Math.min(batchStart + batchSize, totalRecords);
                int currentBatchSize = batchEnd - batchStart;
                
                IntelligentDataGenerationRequest batchRequest = createBatchRequest(request, batchStart, currentBatchSize);
                batchFutures.add(generateIntelligentData(batchRequest));
            }
            
            // Ожидаем завершения всех батчей
            CompletableFuture<Void> allBatches = CompletableFuture.allOf(
                batchFutures.toArray(new CompletableFuture[0])
            );
            
            return allBatches.thenApply(v -> {
                try {
                    List<IntelligentTestDataResult> batchResults = batchFutures.stream()
                        .map(future -> future.join())
                        .collect(Collectors.toList());
                    
                    return aggregateBatchResults(request, batchResults);
                } catch (Exception e) {
                    logger.error("Failed to aggregate batch results for request: {}", request.getRequestId(), e);
                    throw new RuntimeException("Batch aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Batch data generation failed for request: {}", request.getRequestId(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Кэширует результат генерации
     */
    public void cacheGenerationResult(String cacheKey, IntelligentTestDataResult result) {
        // Implementation for caching
        logger.debug("Caching generation result for key: {}", cacheKey);
    }
    
    /**
     * Получает кэшированный результат
     */
    public IntelligentTestDataResult getCachedResult(String cacheKey) {
        // Implementation for retrieving cached results
        logger.debug("Retrieving cached result for key: {}", cacheKey);
        return null; // Placeholder
    }
    
    /**
     * Очищает кэш
     */
    public void clearCache() {
        profileCache.clear();
        testDataLLMService.clearCache();
        logger.info("Intelligent data generator cache cleared");
    }
    
    // Private helper methods
    
    private DataGeneratorProfile prepareGenerationProfile(IntelligentDataGenerationRequest request) {
        String profileId = request.getProfileId();
        
        if (profileId != null && profileCache.containsKey(profileId)) {
            return profileCache.get(profileId);
        }
        
        // Создаем новый профиль на основе запроса
        DataGeneratorProfile profile = new DataGeneratorProfile();
        profile.setName("Generated Profile for " + request.getRequestId());
        profile.setGeneratorType("LLM_INTELLIGENT");
        profile.setGenerationScope(request.getGenerationScope());
        profile.setQualityLevel(request.getQualityLevel());
        profile.setMaxDataRecords(request.getRecordCount());
        profile.setBatchSize(llmConfig.getDefaultBatchSize());
        profile.setIsCachingEnabled(llmConfig.isEnableCaching());
        profile.setIsValidationEnabled(request.isEnableValidation());
        profile.setIsParallelProcessing(true);
        profile.markAsExecuted();
        
        // Кэшируем профиль
        if (profileId != null) {
            profileCache.put(profileId, profile);
        }
        
        return profile;
    }
    
    private TestDataGenerationRequest createLLMRequest(IntelligentDataGenerationRequest request, DataGeneratorProfile profile) {
        TestDataGenerationRequest llmRequest = new TestDataGenerationRequest();
        llmRequest.setRequestId(request.getRequestId());
        llmRequest.setDataType(request.getDataType());
        llmRequest.setGenerationScope(request.getGenerationScope());
        llmRequest.setRecordCount(request.getRecordCount());
        llmRequest.setConstraints(request.getConstraints());
        llmRequest.setRequiredFields(request.getRequiredFields());
        llmRequest.setExcludeFields(request.getExcludeFields());
        llmRequest.setQualityLevel(request.getQualityLevel());
        llmRequest.setContext(request.getContext());
        llmRequest.setTemplateId(request.getTemplateId());
        llmRequest.setEnableValidation(request.isEnableValidation());
        llmRequest.setEnableDependencyAnalysis(request.isEnableDependencyAnalysis());
        llmRequest.setPreserveContext(request.isPreserveContext());
        
        return llmRequest;
    }
    
    private IntelligentTestDataResult createIntelligentResult(IntelligentDataGenerationRequest request,
                                                             DataGeneratorProfile profile,
                                                             TestDataGenerationResult llmResult,
                                                             TestDataQualityReport qualityReport,
                                                             TestDataValidationResult validationResult) {
        IntelligentTestDataResult result = new IntelligentTestDataResult();
        result.setRequestId(request.getRequestId());
        result.setProfileId(profile.getProfileId());
        result.setGeneratedData(llmResult.getGeneratedData());
        result.setDataRecords(llmResult.getDataRecords());
        result.setQualityReport(qualityReport);
        result.setValidationResult(validationResult);
        result.setGenerationMetrics(llmResult.getMetrics());
        result.setMetadata(llmResult.getMetadata());
        result.setGeneratedAt(LocalDateTime.now());
        result.setSuccess(llmResult.isSuccessful());
        
        // Добавляем информацию о профиле
        result.setGeneratorProfile(profile);
        
        return result;
    }
    
    private IntelligentDataGenerationRequest createBatchRequest(IntelligentDataGenerationRequest originalRequest, 
                                                                int startIndex, int batchSize) {
        IntelligentDataGenerationRequest batchRequest = new IntelligentDataGenerationRequest();
        batchRequest.setRequestId(originalRequest.getRequestId() + "_batch_" + startIndex);
        batchRequest.setDataType(originalRequest.getDataType());
        batchRequest.setGenerationScope(originalRequest.getGenerationScope());
        batchRequest.setRecordCount(batchSize);
        batchRequest.setConstraints(originalRequest.getConstraints());
        batchRequest.setRequiredFields(originalRequest.getRequiredFields());
        batchRequest.setExcludeFields(originalRequest.getExcludeFields());
        batchRequest.setQualityLevel(originalRequest.getQualityLevel());
        batchRequest.setContext(originalRequest.getContext());
        batchRequest.setTemplateId(originalRequest.getTemplateId());
        batchRequest.setEnableValidation(originalRequest.isEnableValidation());
        batchRequest.setEnableDependencyAnalysis(originalRequest.isEnableDependencyAnalysis());
        batchRequest.setPreserveContext(originalRequest.isPreserveContext());
        
        return batchRequest;
    }
    
    private IntelligentTestDataResult aggregateBatchResults(IntelligentDataGenerationRequest request,
                                                           List<IntelligentTestDataResult> batchResults) {
        // Агрегируем результаты батчей
        IntelligentTestDataResult aggregatedResult = new IntelligentTestDataResult();
        aggregatedResult.setRequestId(request.getRequestId());
        aggregatedResult.setGeneratedAt(LocalDateTime.now());
        aggregatedResult.setSuccess(true);
        
        // Объединяем данные
        List<Map<String, Object>> allDataRecords = new ArrayList<>();
        List<TestDataQualityReport> qualityReports = new ArrayList<>();
        List<TestDataValidationResult> validationResults = new ArrayList<>();
        
        for (IntelligentTestDataResult batchResult : batchResults) {
            if (batchResult.getDataRecords() != null) {
                allDataRecords.addAll(batchResult.getDataRecords());
            }
            if (batchResult.getQualityReport() != null) {
                qualityReports.add(batchResult.getQualityReport());
            }
            if (batchResult.getValidationResult() != null) {
                validationResults.add(batchResult.getValidationResult());
            }
        }
        
        aggregatedResult.setDataRecords(allDataRecords);
        
        // Вычисляем средние показатели качества
        if (!qualityReports.isEmpty()) {
            int avgQualityScore = (int) qualityReports.stream()
                .mapToInt(TestDataQualityReport::getOverallScore)
                .average()
                .orElse(0.0);
            
            TestDataQualityReport aggregatedQualityReport = new TestDataQualityReport();
            aggregatedQualityReport.setOverallScore(avgQualityScore);
            aggregatedQualityReport.setAnalyzedAt(LocalDateTime.now());
            aggregatedResult.setQualityReport(aggregatedQualityReport);
        }
        
        return aggregatedResult;
    }
}