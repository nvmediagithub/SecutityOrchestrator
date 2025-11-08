package org.example.infrastructure.services.llm;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.infrastructure.services.openapi.OpenApiOrchestrationService;
import org.example.domain.valueobjects.SpecificationId;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Основной сервис анализа OpenAPI спецификаций с использованием LLM
 * Координирует анализ безопасности, валидации и согласованности
 */
@Service
public class OpenApiAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenApiAnalysisService.class);
    
    @Autowired
    private LLMPromptBuilder promptBuilder;
    
    @Autowired
    private IssueClassifier issueClassifier;
    
    @Autowired
    private OpenApiLLMAnalyzer llmAnalyzer;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private OpenApiOrchestrationService orchestrationService;
    
    @Autowired
    private Executor analysisExecutor;
    
    @Autowired
    private ScheduledExecutorService scheduledExecutor;
    
    // Кэш для результатов анализа
    private final Map<String, AnalysisResult> resultCache = new ConcurrentHashMap<>();
    private final Map<String, AnalysisStatus> activeAnalyses = new ConcurrentHashMap<>();
    private final Queue<String> analysisQueue = new LinkedList<>();
    
    // Конфигурация анализа
    private static final int MAX_CONCURRENT_ANALYSES = 3;
    private static final long CACHE_TTL_HOURS = 24;
    private static final long ANALYSIS_TIMEOUT_MINUTES = 30;
    
    /**
     * Выполняет полный анализ OpenAPI спецификации
     */
    @Async
    public CompletableFuture<AnalysisResult> analyzeSpecification(String specId, OpenApiSpecification spec) {
        String analysisId = generateAnalysisId(specId);
        logger.info("Starting comprehensive analysis for spec: {}, analysisId: {}", specId, analysisId);
        
        try {
            activeAnalyses.put(analysisId, new AnalysisStatus(analysisId, "STARTED", LocalDateTime.now()));
            
            // Проверяем кэш
            String cacheKey = generateCacheKey(specId, "comprehensive");
            if (resultCache.containsKey(cacheKey)) {
                AnalysisResult cached = resultCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached analysis result for spec: {}", specId);
                    return CompletableFuture.completedFuture(cached);
                }
            }
            
            // Подготавливаем задачи анализа
            List<CompletableFuture<PartialAnalysisResult>> analysisTasks = new ArrayList<>();
            
            // 1. Анализ безопасности
            analysisTasks.add(analyzeSecurity(specId, spec, analysisId));
            
            // 2. Анализ валидации
            analysisTasks.add(analyzeValidation(specId, spec, analysisId));
            
            // 3. Анализ согласованности
            analysisTasks.add(analyzeConsistency(specId, spec, analysisId));
            
            // 4. Комплексный анализ
            analysisTasks.add(analyzeComprehensive(specId, spec, analysisId));
            
            // Ожидаем завершения всех задач
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                analysisTasks.toArray(new CompletableFuture[0])
            );
            
            return allTasks.thenApply(v -> {
                try {
                    updateAnalysisStatus(analysisId, "AGGREGATING");
                    
                    // Агрегируем результаты
                    PartialAnalysisResult securityResult = analysisTasks.get(0).get();
                    PartialAnalysisResult validationResult = analysisTasks.get(1).get();
                    PartialAnalysisResult consistencyResult = analysisTasks.get(2).get();
                    PartialAnalysisResult comprehensiveResult = analysisTasks.get(3).get();
                    
                    AnalysisResult finalResult = aggregateResults(
                        specId, spec, analysisId,
                        securityResult, validationResult, 
                        consistencyResult, comprehensiveResult
                    );
                    
                    // Кэшируем результат
                    cacheResult(cacheKey, finalResult);
                    
                    // Обновляем статус
                    activeAnalyses.put(analysisId, 
                        new AnalysisStatus(analysisId, "COMPLETED", LocalDateTime.now()));
                    
                    logger.info("Analysis completed for spec: {}, analysisId: {}", specId, analysisId);
                    return finalResult;
                    
                } catch (Exception e) {
                    logger.error("Error aggregating analysis results", e);
                    activeAnalyses.put(analysisId, 
                        new AnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
                    throw new RuntimeException("Analysis aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting analysis for spec: {}", specId, e);
            activeAnalyses.put(analysisId, 
                new AnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Анализ безопасности OpenAPI спецификации
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeSecurity(String specId, OpenApiSpecification spec, String analysisId) {
        logger.info("Starting security analysis for spec: {}, analysisId: {}", specId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "SECURITY_ANALYSIS");
                
                // Генерируем промпт
                String prompt = promptBuilder.buildSecurityAnalysisPrompt(spec);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "security");
                
                // Парсим результат
                List<IssueClassifier.RawIssue> issues = llmAnalyzer.parseSecurityAnalysis(llmResponse);
                
                // Классифицируем проблемы
                IssueClassifier.ClassifiedIssues classified = issueClassifier.classifyIssues(issues);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("SECURITY");
                result.setIssues(classified);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Security analysis completed for spec: {}, found {} issues", 
                    specId, classified.getTotalIssues());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Security analysis failed for spec: {}", specId, e);
                throw new RuntimeException("Security analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Анализ валидации OpenAPI спецификации
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeValidation(String specId, OpenApiSpecification spec, String analysisId) {
        logger.info("Starting validation analysis for spec: {}, analysisId: {}", specId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "VALIDATION_ANALYSIS");
                
                // Генерируем промпт
                String prompt = promptBuilder.buildValidationAnalysisPrompt(spec);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "validation");
                
                // Парсим результат
                List<IssueClassifier.RawIssue> issues = llmAnalyzer.parseValidationAnalysis(llmResponse);
                
                // Классифицируем проблемы
                IssueClassifier.ClassifiedIssues classified = issueClassifier.classifyIssues(issues);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("VALIDATION");
                result.setIssues(classified);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Validation analysis completed for spec: {}, found {} issues", 
                    specId, classified.getTotalIssues());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Validation analysis failed for spec: {}", specId, e);
                throw new RuntimeException("Validation analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Анализ согласованности OpenAPI спецификации
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeConsistency(String specId, OpenApiSpecification spec, String analysisId) {
        logger.info("Starting consistency analysis for spec: {}, analysisId: {}", specId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "CONSISTENCY_ANALYSIS");
                
                // Генерируем промпт
                String prompt = promptBuilder.buildConsistencyAnalysisPrompt(spec);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "consistency");
                
                // Парсим результат
                List<IssueClassifier.RawIssue> issues = llmAnalyzer.parseConsistencyAnalysis(llmResponse);
                
                // Классифицируем проблемы
                IssueClassifier.ClassifiedIssues classified = issueClassifier.classifyIssues(issues);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("CONSISTENCY");
                result.setIssues(classified);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Consistency analysis completed for spec: {}, found {} issues", 
                    specId, classified.getTotalIssues());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Consistency analysis failed for spec: {}", specId, e);
                throw new RuntimeException("Consistency analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Комплексный анализ OpenAPI спецификации
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeComprehensive(String specId, OpenApiSpecification spec, String analysisId) {
        logger.info("Starting comprehensive analysis for spec: {}, analysisId: {}", specId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "COMPREHENSIVE_ANALYSIS");
                
                // Генерируем промпт
                String prompt = promptBuilder.buildComprehensiveAnalysisPrompt(spec);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "comprehensive");
                
                // Парсим результат
                Map<String, Object> comprehensiveData = llmAnalyzer.parseComprehensiveAnalysis(llmResponse);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("COMPREHENSIVE");
                result.setComprehensiveData(comprehensiveData);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Comprehensive analysis completed for spec: {}", specId);
                
                return result;
                
            } catch (Exception e) {
                logger.error("Comprehensive analysis failed for spec: {}", specId, e);
                throw new RuntimeException("Comprehensive analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Выполняет LLM анализ с fallback на разные провайдеры
     */
    private String executeLLMAnalysis(String prompt, String analysisType) {
        try {
            // Пробуем сначала OpenRouter (внешние модели)
            if (openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} analysis", analysisType);
                return openRouterClient.chatCompletion(getPreferredModel(), prompt, 2000, 0.3)
                    .get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.warn("OpenRouter failed for {} analysis, trying local model", analysisType, e);
        }
        
        try {
            // Fallback на локальную модель
            if (localLLMService.checkOllamaConnection() != null) {
                logger.debug("Using local LLM for {} analysis", analysisType);
                return localLLMService.localChatCompletion(getPreferredLocalModel(), prompt, 2000, 0.3)
                    .get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.error("All LLM providers failed for {} analysis", analysisType, e);
            throw new RuntimeException("No LLM provider available for analysis", e);
        }
        
        throw new RuntimeException("No LLM providers available");
    }
    
    /**
     * Получает статус анализа
     */
    public AnalysisStatus getAnalysisStatus(String analysisId) {
        return activeAnalyses.get(analysisId);
    }
    
    /**
     * Получает результаты анализа
     */
    public AnalysisResult getAnalysisResults(String analysisId) {
        return activeAnalyses.values().stream()
            .filter(status -> status.getAnalysisId().equals(analysisId))
            .map(status -> {
                String cacheKey = generateCacheKey(extractSpecIdFromAnalysisId(analysisId), "comprehensive");
                return resultCache.get(cacheKey);
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Очищает кэш результатов
     */
    public void clearCache() {
        resultCache.clear();
        logger.info("Analysis result cache cleared");
    }
    
    /**
     * Получает статистику сервиса
     */
    public ServiceStatistics getServiceStatistics() {
        ServiceStatistics stats = new ServiceStatistics();
        stats.setActiveAnalyses(activeAnalyses.size());
        stats.setCachedResults(resultCache.size());
        stats.setQueueSize(analysisQueue.size());
        return stats;
    }
    
    // Вспомогательные методы
    
    private String generateAnalysisId(String specId) {
        return "analysis_" + specId + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(String specId, String analysisType) {
        return specId + "_" + analysisType;
    }
    
    private String getPreferredModel() {
        // Возвращаем предпочитаемую модель для анализа
        return "anthropic/claude-3-sonnet";
    }
    
    private String getPreferredLocalModel() {
        // Возвращаем предпочитаемую локальную модель
        return "llama3.1:8b";
    }
    
    private void updateAnalysisStatus(String analysisId, String status) {
        activeAnalyses.put(analysisId, new AnalysisStatus(analysisId, status, LocalDateTime.now()));
    }
    
    private String extractSpecIdFromAnalysisId(String analysisId) {
        // Извлекаем specId из analysisId
        String[] parts = analysisId.split("_");
        return parts.length > 1 ? parts[1] : "unknown";
    }
    
    private boolean isCacheValid(AnalysisResult result) {
        if (result == null || result.getCreatedAt() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(result.getCreatedAt().plusHours(CACHE_TTL_HOURS));
    }
    
    private void cacheResult(String cacheKey, AnalysisResult result) {
        resultCache.put(cacheKey, result);
        
        // Запускаем таймер очистки кэша
        scheduledExecutor.schedule(() -> {
            resultCache.remove(cacheKey);
        }, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }
    
    private AnalysisResult aggregateResults(String specId, OpenApiSpecification spec, String analysisId,
                                          PartialAnalysisResult security, PartialAnalysisResult validation,
                                          PartialAnalysisResult consistency, PartialAnalysisResult comprehensive) {
        AnalysisResult result = new AnalysisResult();
        result.setAnalysisId(analysisId);
        result.setSpecId(specId);
        result.setSpecification(spec);
        result.setCreatedAt(LocalDateTime.now());
        
        // Агрегируем все проблемы
        List<IssueClassifier.ClassifiedIssue> allIssues = new ArrayList<>();
        if (security.getIssues() != null) {
            allIssues.addAll(security.getIssues().getIssues());
        }
        if (validation.getIssues() != null) {
            allIssues.addAll(validation.getIssues().getIssues());
        }
        if (consistency.getIssues() != null) {
            allIssues.addAll(consistency.getIssues().getIssues());
        }
        
        result.setAllIssues(allIssues);
        result.setSecurityAnalysis(security);
        result.setValidationAnalysis(validation);
        result.setConsistencyAnalysis(consistency);
        result.setComprehensiveAnalysis(comprehensive);
        
        // Вычисляем общие метрики
        result.setTotalIssues(allIssues.size());
        result.setCriticalIssues((int) allIssues.stream()
            .filter(issue -> issue.getSeverity() == org.example.domain.valueobjects.SeverityLevel.CRITICAL)
            .count());
        result.setHighIssues((int) allIssues.stream()
            .filter(issue -> issue.getSeverity() == org.example.domain.valueobjects.SeverityLevel.HIGH)
            .count());
        
        return result;
    }
    
    // Классы для представления результатов анализа
    
    public static class AnalysisResult {
        private String analysisId;
        private String specId;
        private OpenApiSpecification specification;
        private LocalDateTime createdAt;
        private List<IssueClassifier.ClassifiedIssue> allIssues;
        private PartialAnalysisResult securityAnalysis;
        private PartialAnalysisResult validationAnalysis;
        private PartialAnalysisResult consistencyAnalysis;
        private PartialAnalysisResult comprehensiveAnalysis;
        private int totalIssues;
        private int criticalIssues;
        private int highIssues;
        
        // Getters and Setters
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
        
        public String getSpecId() { return specId; }
        public void setSpecId(String specId) { this.specId = specId; }
        
        public OpenApiSpecification getSpecification() { return specification; }
        public void setSpecification(OpenApiSpecification specification) { this.specification = specification; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public List<IssueClassifier.ClassifiedIssue> getAllIssues() { return allIssues; }
        public void setAllIssues(List<IssueClassifier.ClassifiedIssue> allIssues) { this.allIssues = allIssues; }
        
        public PartialAnalysisResult getSecurityAnalysis() { return securityAnalysis; }
        public void setSecurityAnalysis(PartialAnalysisResult securityAnalysis) { this.securityAnalysis = securityAnalysis; }
        
        public PartialAnalysisResult getValidationAnalysis() { return validationAnalysis; }
        public void setValidationAnalysis(PartialAnalysisResult validationAnalysis) { this.validationAnalysis = validationAnalysis; }
        
        public PartialAnalysisResult getConsistencyAnalysis() { return consistencyAnalysis; }
        public void setConsistencyAnalysis(PartialAnalysisResult consistencyAnalysis) { this.consistencyAnalysis = consistencyAnalysis; }
        
        public PartialAnalysisResult getComprehensiveAnalysis() { return comprehensiveAnalysis; }
        public void setComprehensiveAnalysis(PartialAnalysisResult comprehensiveAnalysis) { this.comprehensiveAnalysis = comprehensiveAnalysis; }
        
        public int getTotalIssues() { return totalIssues; }
        public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
        
        public int getCriticalIssues() { return criticalIssues; }
        public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
        
        public int getHighIssues() { return highIssues; }
        public void setHighIssues(int highIssues) { this.highIssues = highIssues; }
    }
    
    public static class PartialAnalysisResult {
        private String analysisType;
        private IssueClassifier.ClassifiedIssues issues;
        private Map<String, Object> comprehensiveData;
        private long processingTimeMs;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getAnalysisType() { return analysisType; }
        public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
        
        public IssueClassifier.ClassifiedIssues getIssues() { return issues; }
        public void setIssues(IssueClassifier.ClassifiedIssues issues) { this.issues = issues; }
        
        public Map<String, Object> getComprehensiveData() { return comprehensiveData; }
        public void setComprehensiveData(Map<String, Object> comprehensiveData) { this.comprehensiveData = comprehensiveData; }
        
        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    public static class AnalysisStatus {
        private String analysisId;
        private String status;
        private LocalDateTime timestamp;
        private String errorMessage;
        
        public AnalysisStatus(String analysisId, String status, LocalDateTime timestamp) {
            this.analysisId = analysisId;
            this.status = status;
            this.timestamp = timestamp;
        }
        
        public AnalysisStatus(String analysisId, String status, LocalDateTime timestamp, String errorMessage) {
            this(analysisId, status, timestamp);
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public String getAnalysisId() { return analysisId; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class ServiceStatistics {
        private int activeAnalyses;
        private int cachedResults;
        private int queueSize;
        
        // Getters and Setters
        public int getActiveAnalyses() { return activeAnalyses; }
        public void setActiveAnalyses(int activeAnalyses) { this.activeAnalyses = activeAnalyses; }
        
        public int getCachedResults() { return cachedResults; }
        public void setCachedResults(int cachedResults) { this.cachedResults = cachedResults; }
        
        public int getQueueSize() { return queueSize; }
        public void setQueueSize(int queueSize) { this.queueSize = queueSize; }
    }
}