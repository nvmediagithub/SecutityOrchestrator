package org.example.infrastructure.services.llm;

import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.llm.ConsistencyAnalysis;
import org.example.domain.entities.llm.InconsistencyReport;
import org.example.domain.entities.llm.LLMSuggestion;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.valueobjects.AnalysisId;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.infrastructure.services.openapi.OpenApiParserService;
import org.example.infrastructure.services.bpmn.BpmnParsingService;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Основной сервис для анализа согласованности между OpenAPI и BPMN с использованием LLM
 * Обеспечивает семантический анализ, выявление несогласованностей и генерацию рекомендаций
 */
@Service
public class LLMConsistencyAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMConsistencyAnalysisService.class);
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private OpenApiParserService openApiParserService;
    
    @Autowired
    private BpmnParsingService bpmnParsingService;
    
    @Autowired
    private LLMPromptBuilder promptBuilder;
    
    @Autowired
    private Executor analysisExecutor;
    
    // Кэш для результатов анализа
    private final Map<String, ConsistencyAnalysis> analysisCache = new ConcurrentHashMap<>();
    private final Map<String, AnalysisStatus> activeAnalyses = new ConcurrentHashMap<>();
    private final Queue<String> analysisQueue = new LinkedList<>();
    
    // Конфигурация анализа
    private static final int MAX_CONCURRENT_ANALYSES = 3;
    private static final long CACHE_TTL_HOURS = 24;
    private static final long ANALYSIS_TIMEOUT_MINUTES = 30;
    private static final double MIN_CONSISTENCY_THRESHOLD = 0.7;
    
    /**
     * Выполняет полный анализ согласованности между OpenAPI и BPMN
     */
    @Async
    public CompletableFuture<ConsistencyAnalysis> analyzeConsistency(String openApiSpecId, String bpmnProcessId,
                                                                     ConsistencyAnalysis.AnalysisType analysisType,
                                                                     ConsistencyAnalysis.AnalysisScope scope) {
        String analysisId = generateAnalysisId(openApiSpecId, bpmnProcessId);
        logger.info("Starting consistency analysis: openApiSpecId={}, bpmnProcessId={}, analysisId={}", 
                   openApiSpecId, bpmnProcessId, analysisId);
        
        try {
            activeAnalyses.put(analysisId, new AnalysisStatus(analysisId, "STARTED", LocalDateTime.now()));
            
            // Проверяем кэш
            String cacheKey = generateCacheKey(openApiSpecId, bpmnProcessId, analysisType, scope);
            if (analysisCache.containsKey(cacheKey)) {
                ConsistencyAnalysis cached = analysisCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached analysis result for spec: {}, process: {}", openApiSpecId, bpmnProcessId);
                    activeAnalyses.put(analysisId, new AnalysisStatus(analysisId, "COMPLETED", LocalDateTime.now()));
                    return CompletableFuture.completedFuture(cached);
                }
            }
            
            // Подготавливаем задачи анализа
            List<CompletableFuture<PartialAnalysisResult>> analysisTasks = new ArrayList<>();
            
            // 1. Семантический анализ соответствия
            analysisTasks.add(performSemanticAnalysis(analysisId, openApiSpecId, bpmnProcessId, scope));
            
            // 2. Анализ бизнес-логики
            analysisTasks.add(analyzeBusinessLogic(analysisId, openApiSpecId, bpmnProcessId, scope));
            
            // 3. Анализ параметров и типов данных
            analysisTasks.add(analyzeParameters(analysisId, openApiSpecId, bpmnProcessId, scope));
            
            // 4. Анализ безопасности и доступа
            analysisTasks.add(analyzeSecurityConsistency(analysisId, openApiSpecId, bpmnProcessId, scope));
            
            // 5. Анализ обработки ошибок
            analysisTasks.add(analyzeErrorHandling(analysisId, openApiSpecId, bpmnProcessId, scope));
            
            // Ожидаем завершения всех задач
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                analysisTasks.toArray(new CompletableFuture[0])
            );
            
            return allTasks.thenApply(v -> {
                try {
                    updateAnalysisStatus(analysisId, "AGGREGATING");
                    
                    // Агрегируем результаты
                    PartialAnalysisResult semanticResult = analysisTasks.get(0).get();
                    PartialAnalysisResult businessLogicResult = analysisTasks.get(1).get();
                    PartialAnalysisResult parametersResult = analysisTasks.get(2).get();
                    PartialAnalysisResult securityResult = analysisTasks.get(3).get();
                    PartialAnalysisResult errorHandlingResult = analysisTasks.get(4).get();
                    
                    ConsistencyAnalysis finalResult = aggregateResults(
                        analysisId, openApiSpecId, bpmnProcessId, analysisType, scope,
                        semanticResult, businessLogicResult, parametersResult, 
                        securityResult, errorHandlingResult
                    );
                    
                    // Кэшируем результат
                    cacheResult(cacheKey, finalResult);
                    
                    // Обновляем статус
                    activeAnalyses.put(analysisId, 
                        new AnalysisStatus(analysisId, "COMPLETED", LocalDateTime.now()));
                    
                    logger.info("Consistency analysis completed: openApiSpecId={}, bpmnProcessId={}, score={}", 
                               openApiSpecId, bpmnProcessId, finalResult.getConsistencyScore());
                    return finalResult;
                    
                } catch (Exception e) {
                    logger.error("Error aggregating analysis results", e);
                    activeAnalyses.put(analysisId, 
                        new AnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
                    throw new RuntimeException("Analysis aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting analysis for spec: {}, process: {}", openApiSpecId, bpmnProcessId, e);
            activeAnalyses.put(analysisId, 
                new AnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Семантический анализ соответствия OpenAPI эндпоинтов и BPMN задач
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> performSemanticAnalysis(String analysisId, String openApiSpecId,
                                                                           String bpmnProcessId, 
                                                                           ConsistencyAnalysis.AnalysisScope scope) {
        logger.debug("Starting semantic analysis: {}", analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "SEMANTIC_ANALYSIS");
                
                // Генерируем промпт для семантического анализа
                String prompt = buildSemanticAnalysisPrompt(openApiSpecId, bpmnProcessId, scope);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "semantic");
                
                // Парсим результат
                List<ConsistencyAnalysis.SemanticMatch> matches = parseSemanticMatches(llmResponse);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("SEMANTIC");
                result.setSemanticMatches(matches);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Semantic analysis completed: found {} matches", matches.size());
                return result;
                
            } catch (Exception e) {
                logger.error("Semantic analysis failed: {}", analysisId, e);
                throw new RuntimeException("Semantic analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Анализ соответствия бизнес-логики
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeBusinessLogic(String analysisId, String openApiSpecId,
                                                                        String bpmnProcessId, 
                                                                        ConsistencyAnalysis.AnalysisScope scope) {
        logger.debug("Starting business logic analysis: {}", analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "BUSINESS_LOGIC_ANALYSIS");
                
                String prompt = buildBusinessLogicAnalysisPrompt(openApiSpecId, bpmnProcessId, scope);
                String llmResponse = executeLLMAnalysis(prompt, "business_logic");
                
                List<InconsistencyReport> inconsistencies = parseBusinessLogicInconsistencies(llmResponse);
                List<LLMSuggestion> suggestions = parseBusinessLogicSuggestions(llmResponse);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("BUSINESS_LOGIC");
                result.setInconsistencies(inconsistencies);
                result.setSuggestions(suggestions);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Business logic analysis completed: found {} inconsistencies, {} suggestions", 
                           inconsistencies.size(), suggestions.size());
                return result;
                
            } catch (Exception e) {
                logger.error("Business logic analysis failed: {}", analysisId, e);
                throw new RuntimeException("Business logic analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Анализ соответствия параметров и типов данных
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeParameters(String analysisId, String openApiSpecId,
                                                                     String bpmnProcessId, 
                                                                     ConsistencyAnalysis.AnalysisScope scope) {
        logger.debug("Starting parameters analysis: {}", analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "PARAMETERS_ANALYSIS");
                
                String prompt = buildParametersAnalysisPrompt(openApiSpecId, bpmnProcessId, scope);
                String llmResponse = executeLLMAnalysis(prompt, "parameters");
                
                List<InconsistencyReport> inconsistencies = parseParameterInconsistencies(llmResponse);
                List<LLMSuggestion> suggestions = parseParameterSuggestions(llmResponse);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("PARAMETERS");
                result.setInconsistencies(inconsistencies);
                result.setSuggestions(suggestions);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Parameters analysis completed: found {} inconsistencies, {} suggestions", 
                           inconsistencies.size(), suggestions.size());
                return result;
                
            } catch (Exception e) {
                logger.error("Parameters analysis failed: {}", analysisId, e);
                throw new RuntimeException("Parameters analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Анализ согласованности безопасности
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeSecurityConsistency(String analysisId, String openApiSpecId,
                                                                              String bpmnProcessId, 
                                                                              ConsistencyAnalysis.AnalysisScope scope) {
        logger.debug("Starting security analysis: {}", analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "SECURITY_ANALYSIS");
                
                String prompt = buildSecurityAnalysisPrompt(openApiSpecId, bpmnProcessId, scope);
                String llmResponse = executeLLMAnalysis(prompt, "security");
                
                List<InconsistencyReport> inconsistencies = parseSecurityInconsistencies(llmResponse);
                List<LLMSuggestion> suggestions = parseSecuritySuggestions(llmResponse);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("SECURITY");
                result.setInconsistencies(inconsistencies);
                result.setSuggestions(suggestions);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Security analysis completed: found {} inconsistencies, {} suggestions", 
                           inconsistencies.size(), suggestions.size());
                return result;
                
            } catch (Exception e) {
                logger.error("Security analysis failed: {}", analysisId, e);
                throw new RuntimeException("Security analysis failed", e);
            }
        }, analysisExecutor);
    }
    
    /**
     * Анализ обработки ошибок
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeErrorHandling(String analysisId, String openApiSpecId,
                                                                         String bpmnProcessId, 
                                                                         ConsistencyAnalysis.AnalysisScope scope) {
        logger.debug("Starting error handling analysis: {}", analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "ERROR_HANDLING_ANALYSIS");
                
                String prompt = buildErrorHandlingAnalysisPrompt(openApiSpecId, bpmnProcessId, scope);
                String llmResponse = executeLLMAnalysis(prompt, "error_handling");
                
                List<InconsistencyReport> inconsistencies = parseErrorHandlingInconsistencies(llmResponse);
                List<LLMSuggestion> suggestions = parseErrorHandlingSuggestions(llmResponse);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("ERROR_HANDLING");
                result.setInconsistencies(inconsistencies);
                result.setSuggestions(suggestions);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Error handling analysis completed: found {} inconsistencies, {} suggestions", 
                           inconsistencies.size(), suggestions.size());
                return result;
                
            } catch (Exception e) {
                logger.error("Error handling analysis failed: {}", analysisId, e);
                throw new RuntimeException("Error handling analysis failed", e);
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
    
    // Вспомогательные методы для парсинга результатов LLM
    
    private List<ConsistencyAnalysis.SemanticMatch> parseSemanticMatches(String llmResponse) {
        // Упрощенная реализация - в реальном проекте нужно использовать более сложный парсинг
        List<ConsistencyAnalysis.SemanticMatch> matches = new ArrayList<>();
        
        // Пример парсинга (в реальности нужно парсить JSON или структурированный ответ)
        if (llmResponse.contains("strong match")) {
            matches.add(new ConsistencyAnalysis.SemanticMatch.Builder()
                .openApiElement("example-endpoint")
                .bpmnElement("example-task")
                .similarityScore(0.85)
                .matchType(ConsistencyAnalysis.SemanticMatch.MatchType.SEMANTIC)
                .description("High semantic similarity detected")
                .build());
        }
        
        return matches;
    }
    
    private List<InconsistencyReport> parseBusinessLogicInconsistencies(String llmResponse) {
        // Упрощенная реализация
        List<InconsistencyReport> inconsistencies = new ArrayList<>();
        
        if (llmResponse.contains("business logic mismatch")) {
            inconsistencies.add(new InconsistencyReport.Builder()
                .type(org.example.domain.valueobjects.InconsistencyType.SCHEMA_INCONSISTENCY)
                .severity(org.example.domain.valueobjects.SeverityLevel.MEDIUM)
                .title("Business Logic Inconsistency")
                .description("API operation does not match BPMN process flow")
                .openApiElement("POST /api/orders")
                .bpmnElement("Order Processing Task")
                .build());
        }
        
        return inconsistencies;
    }
    
    private List<LLMSuggestion> parseBusinessLogicSuggestions(String llmResponse) {
        List<LLMSuggestion> suggestions = new ArrayList<>();
        
        if (llmResponse.contains("suggestion")) {
            suggestions.add(new LLMSuggestion.Builder()
                .title("Align API with BPMN Process")
                .description("Ensure API endpoints match BPMN process flow")
                .category(LLMSuggestion.SuggestionCategory.CONSISTENCY_IMPROVEMENT)
                .priority(LLMSuggestion.SuggestionPriority.HIGH)
                .stepsToImplement(Arrays.asList(
                    "Review BPMN process definitions",
                    "Update API endpoint definitions",
                    "Align HTTP methods with process operations"
                ))
                .build());
        }
        
        return suggestions;
    }
    
    private List<InconsistencyReport> parseParameterInconsistencies(String llmResponse) {
        // Упрощенная реализация
        List<InconsistencyReport> inconsistencies = new ArrayList<>();
        
        if (llmResponse.contains("parameter mismatch")) {
            inconsistencies.add(new InconsistencyReport.Builder()
                .type(org.example.domain.valueobjects.InconsistencyType.PARAMETER_INCONSISTENCY)
                .severity(org.example.domain.valueobjects.SeverityLevel.HIGH)
                .title("Parameter Mismatch")
                .description("API parameters don't match BPMN data requirements")
                .openApiElement("orderId parameter")
                .bpmnElement("Order Data Object")
                .build());
        }
        
        return inconsistencies;
    }
    
    private List<LLMSuggestion> parseParameterSuggestions(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<InconsistencyReport> parseSecurityInconsistencies(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<LLMSuggestion> parseSecuritySuggestions(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<InconsistencyReport> parseErrorHandlingInconsistencies(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<LLMSuggestion> parseErrorHandlingSuggestions(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    /**
     * Агрегирует результаты анализа в финальный объект ConsistencyAnalysis
     */
    private ConsistencyAnalysis aggregateResults(String analysisId, String openApiSpecId, String bpmnProcessId,
                                                ConsistencyAnalysis.AnalysisType analysisType, 
                                                ConsistencyAnalysis.AnalysisScope scope,
                                                PartialAnalysisResult... results) {
        
        // Собираем все несогласованности и предложения
        List<InconsistencyReport> allInconsistencies = new ArrayList<>();
        List<LLMSuggestion> allSuggestions = new ArrayList<>();
        List<ConsistencyAnalysis.SemanticMatch> allMatches = new ArrayList<>();
        
        for (PartialAnalysisResult result : results) {
            if (result.getInconsistencies() != null) {
                allInconsistencies.addAll(result.getInconsistencies());
            }
            if (result.getSuggestions() != null) {
                allSuggestions.addAll(result.getSuggestions());
            }
            if (result.getSemanticMatches() != null) {
                allMatches.addAll(result.getSemanticMatches());
            }
        }
        
        // Вычисляем общий балл согласованности
        double consistencyScore = calculateConsistencyScore(allInconsistencies, allMatches);
        
        // Создаем финальный результат
        return new ConsistencyAnalysis.Builder()
            .analysisId(AnalysisId.fromString(analysisId))
            .openApiSpecId(openApiSpecId)
            .bpmnProcessId(bpmnProcessId)
            .analysisType(analysisType)
            .scope(scope)
            .status(ConsistencyAnalysis.ConsistencyStatus.COMPLETED)
            .completedAt(LocalDateTime.now())
            .semanticMatches(allMatches)
            .inconsistencies(allInconsistencies)
            .suggestions(allSuggestions)
            .consistencyScore(consistencyScore)
            .metadata(Map.of(
                "totalMatches", allMatches.size(),
                "totalInconsistencies", allInconsistencies.size(),
                "totalSuggestions", allSuggestions.size(),
                "processingTime", Arrays.stream(results)
                    .mapToLong(PartialAnalysisResult::getProcessingTimeMs)
                    .sum()
            ))
            .build();
    }
    
    /**
     * Вычисляет общий балл согласованности
     */
    private double calculateConsistencyScore(List<InconsistencyReport> inconsistencies, 
                                            List<ConsistencyAnalysis.SemanticMatch> matches) {
        if (matches.isEmpty()) return 0.0;
        
        // База - количество успешных соответствий
        long strongMatches = matches.stream()
            .filter(ConsistencyAnalysis.SemanticMatch::isStrongMatch)
            .count();
        
        // Штрафы за несогласованности
        double penalty = inconsistencies.stream()
            .mapToDouble(issue -> {
                switch (issue.getSeverity()) {
                    case CRITICAL: return 0.2;
                    case HIGH: return 0.15;
                    case MEDIUM: return 0.1;
                    case LOW: return 0.05;
                    default: return 0.1;
                }
            })
            .sum();
        
        double score = (double) strongMatches / matches.size() - penalty;
        return Math.max(0.0, Math.min(1.0, score));
    }
    
    // Вспомогательные методы
    
    private String generateAnalysisId(String openApiSpecId, String bpmnProcessId) {
        return "consistency_" + openApiSpecId + "_" + bpmnProcessId + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(String openApiSpecId, String bpmnProcessId, 
                                   ConsistencyAnalysis.AnalysisType type, ConsistencyAnalysis.AnalysisScope scope) {
        return openApiSpecId + "_" + bpmnProcessId + "_" + type.name() + "_" + scope.name();
    }
    
    private String getPreferredModel() {
        return "anthropic/claude-3-sonnet";
    }
    
    private String getPreferredLocalModel() {
        return "llama3.1:8b";
    }
    
    private void updateAnalysisStatus(String analysisId, String status) {
        activeAnalyses.put(analysisId, new AnalysisStatus(analysisId, status, LocalDateTime.now()));
    }
    
    private boolean isCacheValid(ConsistencyAnalysis result) {
        if (result == null || result.getCompletedAt() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(result.getCompletedAt().plusHours(CACHE_TTL_HOURS));
    }
    
    private void cacheResult(String cacheKey, ConsistencyAnalysis result) {
        analysisCache.put(cacheKey, result);
    }
    
    // Геттеры для статуса и результатов
    
    public AnalysisStatus getAnalysisStatus(String analysisId) {
        return activeAnalyses.get(analysisId);
    }
    
    public ConsistencyAnalysis getAnalysisResults(String analysisId) {
        return activeAnalyses.values().stream()
            .filter(status -> status.getAnalysisId().equals(analysisId))
            .map(status -> {
                // Извлекаем ключ кэша из analysisId
                String[] parts = analysisId.split("_");
                if (parts.length >= 3) {
                    String openApiSpecId = parts[1];
                    String bpmnProcessId = parts[2];
                    String cacheKey = generateCacheKey(openApiSpecId, bpmnProcessId, 
                                                     ConsistencyAnalysis.AnalysisType.COMPREHENSIVE_ANALYSIS,
                                                     ConsistencyAnalysis.AnalysisScope.FULL);
                    return analysisCache.get(cacheKey);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
    
    public void clearCache() {
        analysisCache.clear();
        logger.info("Analysis result cache cleared");
    }
    
    // Вспомогательные классы
    
    public static class AnalysisStatus {
        private final String analysisId;
        private final String status;
        private final LocalDateTime timestamp;
        private final String errorMessage;
        
        public AnalysisStatus(String analysisId, String status, LocalDateTime timestamp) {
            this.analysisId = analysisId;
            this.status = status;
            this.timestamp = timestamp;
            this.errorMessage = null;
        }
        
        public AnalysisStatus(String analysisId, String status, LocalDateTime timestamp, String errorMessage) {
            this.analysisId = analysisId;
            this.status = status;
            this.timestamp = timestamp;
            this.errorMessage = errorMessage;
        }
        
        // Геттеры
        public String getAnalysisId() { return analysisId; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class PartialAnalysisResult {
        private String analysisType;
        private List<ConsistencyAnalysis.SemanticMatch> semanticMatches;
        private List<InconsistencyReport> inconsistencies;
        private List<LLMSuggestion> suggestions;
        private long processingTimeMs;
        private LocalDateTime timestamp;
        
        // Геттеры и сеттеры
        public String getAnalysisType() { return analysisType; }
        public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
        
        public List<ConsistencyAnalysis.SemanticMatch> getSemanticMatches() { return semanticMatches; }
        public void setSemanticMatches(List<ConsistencyAnalysis.SemanticMatch> semanticMatches) { this.semanticMatches = semanticMatches; }
        
        public List<InconsistencyReport> getInconsistencies() { return inconsistencies; }
        public void setInconsistencies(List<InconsistencyReport> inconsistencies) { this.inconsistencies = inconsistencies; }
        
        public List<LLMSuggestion> getSuggestions() { return suggestions; }
        public void setSuggestions(List<LLMSuggestion> suggestions) { this.suggestions = suggestions; }
        
        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    // Вспомогательные методы для генерации промптов
    
    private String buildSemanticAnalysisPrompt(String openApiSpecId, String bpmnProcessId, 
                                             ConsistencyAnalysis.AnalysisScope scope) {
        return String.format("""
            Выполни семантический анализ соответствия между OpenAPI спецификацией и BPMN процессом:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            Scope: %s
            
            Найди семантические соответствия между API эндпоинтами и задачами BPMN.
            Оцени силу соответствия по шкале от 0 до 1.
            Верни результат в JSON формате.
            """, openApiSpecId, bpmnProcessId, scope.name());
    }
    
    private String buildBusinessLogicAnalysisPrompt(String openApiSpecId, String bpmnProcessId,
                                                   ConsistencyAnalysis.AnalysisScope scope) {
        return String.format("""
            Проанализируй соответствие бизнес-логики между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            Scope: %s
            
            Найди противоречия в бизнес-логике, несоответствия в операциях.
            Укажи проблемные места с обоснованием.
            """, openApiSpecId, bpmnProcessId, scope.name());
    }
    
    private String buildParametersAnalysisPrompt(String openApiSpecId, String bpmnProcessId,
                                               ConsistencyAnalysis.AnalysisScope scope) {
        return String.format("""
            Проанализируй соответствие параметров и типов данных:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            Scope: %s
            
            Найди несоответствия в параметрах API и данных в BPMN.
            Проверь типы данных, обязательные поля, валидацию.
            """, openApiSpecId, bpmnProcessId, scope.name());
    }
    
    private String buildSecurityAnalysisPrompt(String openApiSpecId, String bpmnProcessId,
                                             ConsistencyAnalysis.AnalysisScope scope) {
        return String.format("""
            Проанализируй соответствие мер безопасности:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            Scope: %s
            
            Найди несоответствия в аутентификации, авторизации, 
            обработке конфиденциальных данных между API и BPMN.
            """, openApiSpecId, bpmnProcessId, scope.name());
    }
    
    private String buildErrorHandlingAnalysisPrompt(String openApiSpecId, String bpmnProcessId,
                                                  ConsistencyAnalysis.AnalysisScope scope) {
        return String.format("""
            Проанализируй соответствие обработки ошибок:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            Scope: %s
            
            Найди несоответствия в обработке ошибок, статусах HTTP,
            сообщениях об ошибках между API и BPMN процессами.
            """, openApiSpecId, bpmnProcessId, scope.name());
    }
}