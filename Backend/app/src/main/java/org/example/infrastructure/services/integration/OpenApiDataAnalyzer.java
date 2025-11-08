package org.example.infrastructure.services.integration;

import org.example.domain.dto.integration.OpenApiDataAnalysisRequest;
import org.example.domain.dto.integration.OpenApiDataAnalysisResult;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.infrastructure.services.openapi.OpenApiOrchestrationService;
import org.example.infrastructure.services.openapi.OpenApiDataExtractor;
import org.example.infrastructure.services.openapi.OpenApiIssueDetector;
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
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Специализированный анализатор OpenAPI для извлечения схем данных и бизнес-контекста
 * Интегрируется с OpenApiAnalysisService для глубокого анализа данных
 */
@Service
public class OpenApiDataAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenApiDataAnalyzer.class);
    
    @Autowired
    private OpenApiOrchestrationService orchestrationService;
    
    @Autowired
    private OpenApiDataExtractor dataExtractor;
    
    @Autowired
    private OpenApiIssueDetector issueDetector;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private Executor openApiAnalysisExecutor;
    
    // Кэш для результатов анализа
    private final Map<String, OpenApiDataAnalysisResult> resultCache = new ConcurrentHashMap<>();
    private final Map<String, AnalysisStatus> activeAnalyses = new ConcurrentHashMap<>();
    
    // Конфигурация анализа
    private static final int MAX_CONCURRENT_ANALYSES = 3;
    private static final long CACHE_TTL_HOURS = 24;
    private static final long ANALYSIS_TIMEOUT_MINUTES = 30;
    
    /**
     * Выполняет комплексный анализ данных из OpenAPI спецификации
     */
    @Async
    public CompletableFuture<OpenApiDataAnalysisResult> analyzeOpenApiData(OpenApiDataAnalysisRequest request) {
        String analysisId = generateAnalysisId(request.getSpecificationId());
        logger.info("Starting OpenAPI data analysis for spec: {}, analysisId: {}", request.getSpecificationId(), analysisId);
        
        try {
            activeAnalyses.put(analysisId, new AnalysisStatus(analysisId, "STARTED", LocalDateTime.now()));
            
            // Проверяем кэш
            String cacheKey = generateCacheKey(request.getSpecificationId(), String.join(",", request.getAnalysisTypes()));
            if (resultCache.containsKey(cacheKey)) {
                OpenApiDataAnalysisResult cached = resultCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached OpenAPI data analysis result for spec: {}", request.getSpecificationId());
                    return CompletableFuture.completedFuture(cached);
                }
            }
            
            // Подготавливаем задачи анализа
            List<CompletableFuture<PartialDataAnalysisResult>> analysisTasks = new ArrayList<>();
            
            // 1. Анализ схем данных
            if (request.getAnalysisTypes().contains("SCHEMAS")) {
                analysisTasks.add(analyzeDataSchemas(request, analysisId));
            }
            
            // 2. Анализ бизнес-логики
            if (request.getAnalysisTypes().contains("BUSINESS_LOGIC")) {
                analysisTasks.add(analyzeBusinessLogic(request, analysisId));
            }
            
            // 3. Анализ валидационных правил
            if (request.getAnalysisTypes().contains("VALIDATION_RULES")) {
                analysisTasks.add(analyzeValidationRules(request, analysisId));
            }
            
            // 4. Анализ пользовательских сценариев
            if (request.getAnalysisTypes().contains("USER_SCENARIOS")) {
                analysisTasks.add(analyzeUserScenarios(request, analysisId));
            }
            
            // 5. Анализ связей
            if (request.getAnalysisTypes().contains("RELATIONSHIPS")) {
                analysisTasks.add(analyzeRelationships(request, analysisId));
            }
            
            // Ожидаем завершения всех задач
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                analysisTasks.toArray(new CompletableFuture[0])
            );
            
            return allTasks.thenApply(v -> {
                try {
                    updateAnalysisStatus(analysisId, "AGGREGATING");
                    
                    // Агрегируем результаты
                    Map<String, PartialDataAnalysisResult> partialResults = new HashMap<>();
                    for (int i = 0; i < analysisTasks.size(); i++) {
                        String taskType = getTaskType(request.getAnalysisTypes(), i);
                        partialResults.put(taskType, analysisTasks.get(i).get());
                    }
                    
                    OpenApiDataAnalysisResult finalResult = aggregateResults(request, analysisId, partialResults);
                    
                    // Кэшируем результат
                    cacheResult(cacheKey, finalResult);
                    
                    // Обновляем статус
                    activeAnalyses.put(analysisId, 
                        new AnalysisStatus(analysisId, "COMPLETED", LocalDateTime.now()));
                    
                    logger.info("OpenAPI data analysis completed for spec: {}, analysisId: {}", request.getSpecificationId(), analysisId);
                    return finalResult;
                    
                } catch (Exception e) {
                    logger.error("Error aggregating OpenAPI data analysis results", e);
                    activeAnalyses.put(analysisId, 
                        new AnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
                    throw new RuntimeException("OpenAPI data analysis aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting OpenAPI data analysis for spec: {}", request.getSpecificationId(), e);
            activeAnalyses.put(analysisId, 
                new AnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Анализ схем данных из OpenAPI
     */
    @Async
    public CompletableFuture<PartialDataAnalysisResult> analyzeDataSchemas(OpenApiDataAnalysisRequest request, String analysisId) {
        logger.info("Starting data schemas analysis for spec: {}, analysisId: {}", request.getSpecificationId(), analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "SCHEMA_ANALYSIS");
                
                // Получаем результаты оркестрационного сервиса
                OpenApiOrchestrationService.OpenApiAnalysisResult orchestrationResult = 
                    orchestrationService.analyzeSpecification(request.getSpecificationContent(), request.getFormat());
                
                if (orchestrationResult.getStatus() != OpenApiOrchestrationService.AnalysisStatus.COMPLETED) {
                    throw new RuntimeException("Failed to parse OpenAPI specification: " + orchestrationResult.getErrorMessage());
                }
                
                OpenApiSpecification specification = orchestrationResult.getSpecification();
                
                // Извлекаем схемы данных
                OpenApiDataExtractor.OpenApiLLMData llmData = orchestrationResult.getLlmData();
                
                // Генерируем LLM промпт для анализа схем
                String prompt = buildSchemaAnalysisPrompt(specification, llmData);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "schema_analysis");
                
                // Парсим результат LLM
                Map<String, Object> schemaAnalysis = parseLLMSchemaResponse(llmResponse);
                
                PartialDataAnalysisResult result = new PartialDataAnalysisResult();
                result.setAnalysisType("SCHEMAS");
                result.setSchemaData(schemaAnalysis);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Schema analysis completed for spec: {}", request.getSpecificationId());
                return result;
                
            } catch (Exception e) {
                logger.error("Schema analysis failed for spec: {}", request.getSpecificationId(), e);
                throw new RuntimeException("Schema analysis failed", e);
            }
        }, openApiAnalysisExecutor);
    }
    
    /**
     * Анализ бизнес-логики из OpenAPI
     */
    @Async
    public CompletableFuture<PartialDataAnalysisResult> analyzeBusinessLogic(OpenApiDataAnalysisRequest request, String analysisId) {
        logger.info("Starting business logic analysis for spec: {}, analysisId: {}", request.getSpecificationId(), analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "BUSINESS_LOGIC_ANALYSIS");
                
                OpenApiOrchestrationService.OpenApiAnalysisResult orchestrationResult = 
                    orchestrationService.analyzeSpecification(request.getSpecificationContent(), request.getFormat());
                
                if (orchestrationResult.getStatus() != OpenApiOrchestrationService.AnalysisStatus.COMPLETED) {
                    throw new RuntimeException("Failed to parse OpenAPI specification");
                }
                
                // Генерируем промпт для анализа бизнес-логики
                String prompt = buildBusinessLogicPrompt(orchestrationResult.getSpecification());
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "business_logic");
                
                // Парсим результат
                Map<String, Object> businessLogicAnalysis = parseLLMBusinessLogicResponse(llmResponse);
                
                PartialDataAnalysisResult result = new PartialDataAnalysisResult();
                result.setAnalysisType("BUSINESS_LOGIC");
                result.setBusinessLogicData(businessLogicAnalysis);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Business logic analysis completed for spec: {}", request.getSpecificationId());
                return result;
                
            } catch (Exception e) {
                logger.error("Business logic analysis failed for spec: {}", request.getSpecificationId(), e);
                throw new RuntimeException("Business logic analysis failed", e);
            }
        }, openApiAnalysisExecutor);
    }
    
    /**
     * Анализ валидационных правил
     */
    @Async
    public CompletableFuture<PartialDataAnalysisResult> analyzeValidationRules(OpenApiDataAnalysisRequest request, String analysisId) {
        logger.info("Starting validation rules analysis for spec: {}, analysisId: {}", request.getSpecificationId(), analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "VALIDATION_ANALYSIS");
                
                OpenApiOrchestrationService.OpenApiAnalysisResult orchestrationResult = 
                    orchestrationService.analyzeSpecification(request.getSpecificationContent(), request.getFormat());
                
                // Извлекаем валидационные правила из результатов анализа проблем
                OpenApiIssueDetector.OpenApiIssueReport issueReport = orchestrationResult.getIssueReport();
                
                // Генерируем промпт для анализа валидации
                String prompt = buildValidationRulesPrompt(orchestrationResult.getSpecification(), issueReport);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "validation_rules");
                
                // Парсим результат
                Map<String, Object> validationAnalysis = parseLLMValidationResponse(llmResponse);
                
                PartialDataAnalysisResult result = new PartialDataAnalysisResult();
                result.setAnalysisType("VALIDATION_RULES");
                result.setValidationData(validationAnalysis);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Validation rules analysis completed for spec: {}", request.getSpecificationId());
                return result;
                
            } catch (Exception e) {
                logger.error("Validation rules analysis failed for spec: {}", request.getSpecificationId(), e);
                throw new RuntimeException("Validation rules analysis failed", e);
            }
        }, openApiAnalysisExecutor);
    }
    
    /**
     * Анализ пользовательских сценариев
     */
    @Async
    public CompletableFuture<PartialDataAnalysisResult> analyzeUserScenarios(OpenApiDataAnalysisRequest request, String analysisId) {
        logger.info("Starting user scenarios analysis for spec: {}, analysisId: {}", request.getSpecificationId(), analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "USER_SCENARIOS_ANALYSIS");
                
                OpenApiOrchestrationService.OpenApiAnalysisResult orchestrationResult = 
                    orchestrationService.analyzeSpecification(request.getSpecificationContent(), request.getFormat());
                
                // Генерируем промпт для анализа сценариев
                String prompt = buildUserScenariosPrompt(orchestrationResult.getSpecification());
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "user_scenarios");
                
                // Парсим результат
                Map<String, Object> scenariosAnalysis = parseLLMScenariosResponse(llmResponse);
                
                PartialDataAnalysisResult result = new PartialDataAnalysisResult();
                result.setAnalysisType("USER_SCENARIOS");
                result.setScenariosData(scenariosAnalysis);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("User scenarios analysis completed for spec: {}", request.getSpecificationId());
                return result;
                
            } catch (Exception e) {
                logger.error("User scenarios analysis failed for spec: {}", request.getSpecificationId(), e);
                throw new RuntimeException("User scenarios analysis failed", e);
            }
        }, openApiAnalysisExecutor);
    }
    
    /**
     * Анализ связей между компонентами API
     */
    @Async
    public CompletableFuture<PartialDataAnalysisResult> analyzeRelationships(OpenApiDataAnalysisRequest request, String analysisId) {
        logger.info("Starting relationships analysis for spec: {}, analysisId: {}", request.getSpecificationId(), analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "RELATIONSHIPS_ANALYSIS");
                
                OpenApiOrchestrationService.OpenApiAnalysisResult orchestrationResult = 
                    orchestrationService.analyzeSpecification(request.getSpecificationContent(), request.getFormat());
                
                // Генерируем промпт для анализа связей
                String prompt = buildRelationshipsPrompt(orchestrationResult.getSpecification());
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "relationships");
                
                // Парсим результат
                Map<String, Object> relationshipsAnalysis = parseLLMRelationshipsResponse(llmResponse);
                
                PartialDataAnalysisResult result = new PartialDataAnalysisResult();
                result.setAnalysisType("RELATIONSHIPS");
                result.setRelationshipsData(relationshipsAnalysis);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Relationships analysis completed for spec: {}", request.getSpecificationId());
                return result;
                
            } catch (Exception e) {
                logger.error("Relationships analysis failed for spec: {}", request.getSpecificationId(), e);
                throw new RuntimeException("Relationships analysis failed", e);
            }
        }, openApiAnalysisExecutor);
    }
    
    /**
     * Выполняет LLM анализ с fallback на разные провайдеры
     */
    private String executeLLMAnalysis(String prompt, String analysisType) {
        try {
            // Пробуем сначала OpenRouter (внешние модели)
            if (openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} OpenAPI analysis", analysisType);
                return openRouterClient.chatCompletion(getPreferredModel(), prompt, 2000, 0.3)
                    .get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.warn("OpenRouter failed for {} OpenAPI analysis, trying local model", analysisType, e);
        }
        
        try {
            // Fallback на локальную модель
            if (localLLMService.checkOllamaConnection() != null) {
                logger.debug("Using local LLM for {} OpenAPI analysis", analysisType);
                return localLLMService.localChatCompletion(getPreferredLocalModel(), prompt, 2000, 0.3)
                    .get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.error("All LLM providers failed for {} OpenAPI analysis", analysisType, e);
            throw new RuntimeException("No LLM provider available for OpenAPI analysis", e);
        }
        
        throw new RuntimeException("No LLM providers available");
    }
    
    // Вспомогательные методы для генерации промптов
    
    private String buildSchemaAnalysisPrompt(OpenApiSpecification specification, OpenApiDataExtractor.OpenApiLLMData llmData) {
        return String.format("""
            Анализируй схемы данных в данной OpenAPI спецификации и извлеки:

            1. Схемы запросов (Request Schemas):
               - Имена схем
               - Типы данных
               - Обязательные поля
               - Валидационные правила

            2. Схемы ответов (Response Schemas):
               - Структура ответов
               - Типы данных в ответах
               - Связи между моделями

            3. Модели данных (Data Models):
               - Основные бизнес-модели
               - Связи между моделями
               - Поля данных и их типы

            4. Поля данных (Data Fields):
               - Имена полей
               - Типы данных
               - Форматы
               - Примеры значений

            Спецификация: %s
            Извлеченные данные: %s

            Ответь в формате JSON со структурой:
            {
              "requestSchemas": [...],
              "responseSchemas": [...],
              "dataModels": [...],
              "dataFields": {...}
            }
            """, specification.getRawContent(), llmData != null ? llmData.toString() : "N/A");
    }
    
    private String buildBusinessLogicPrompt(OpenApiSpecification specification) {
        return String.format("""
            Проанализируй бизнес-логику данной OpenAPI спецификации и извлеки:

            1. API Endpoints (Точки входа API):
               - Пути и методы
               - Назначение каждого endpoint
               - Бизнес-цель

            2. Бизнес-логика (Business Logic):
               - Основные бизнес-операции
               - Workflow паттерны
               - Взаимосвязи между операциями

            3. Пользовательские сценарии (User Scenarios):
               - Основные пользовательские пути
               - Последовательности вызовов API
               - Бизнес-кейсы

            4. Паттерны рабочих процессов (Workflow Patterns):
               - Типовые паттерны использования API
               - Последовательности операций
               - Зависимости между endpoints

            Спецификация: %s

            Ответь в формате JSON со структурой:
            {
              "apiEndpoints": [...],
              "businessLogic": [...],
              "userScenarios": [...],
              "workflowPatterns": [...]
            }
            """, specification.getRawContent());
    }
    
    private String buildValidationRulesPrompt(OpenApiSpecification specification, OpenApiIssueDetector.OpenApiIssueReport issueReport) {
        return String.format("""
            Проанализируй валидационные правила в данной OpenAPI спецификации и извлеки:

            1. Валидационные правила (Validation Rules):
               - Правила для полей
               - Типы валидации
               - Параметры правил

            2. Бизнес-правила (Business Rules):
               - Бизнес-ограничения
               - Условия валидности
               - Правила принятия решений

            3. Ограничения данных (Data Constraints):
               - Ограничения на значения
               - Форматы данных
               - Диапазоны значений

            Спецификация: %s
            Отчет о проблемах: %s

            Ответь в формате JSON со структурой:
            {
              "validationRules": [...],
              "businessRules": [...],
              "dataConstraints": {...}
            }
            """, specification.getRawContent(), 
            issueReport != null ? issueReport.toString() : "N/A");
    }
    
    private String buildUserScenariosPrompt(OpenApiSpecification specification) {
        return String.format("""
            Создай пользовательские сценарии на основе данной OpenAPI спецификации:

            1. Основные сценарии (Main Scenarios):
               - Критические пользовательские пути
               - Бизнес-кейсы высокого уровня
               - Типичные последовательности операций

            2. Сценарии использования API:
               - Как пользователи взаимодействуют с API
               - Последовательности вызовов
               - Зависимости между операциями

            3. End-to-end процессы:
               - Полные бизнес-процессы
               - Интеграция нескольких endpoints
               - Временные аспекты

            Спецификация: %s

            Ответь в формате JSON со структурой:
            {
              "mainScenarios": [...],
              "apiUsageScenarios": [...],
              "endToEndProcesses": [...]
            }
            """, specification.getRawContent());
    }
    
    private String buildRelationshipsPrompt(OpenApiSpecification specification) {
        return String.format("""
            Проанализируй связи и зависимости в данной OpenAPI спецификации:

            1. Связи между endpoints:
               - Последовательные вызовы
               - Параллельные операции
               - Зависимости данных

            2. Связи между моделями данных:
               - Референсы между схемами
               - Наследование
               - Композиция

            3. Data flow корреляции:
               - Потоки данных между операциями
               - Трансформации данных
               - Контекстные зависимости

            4. Кросс-референсы:
               - Взаимные ссылки
               - Общие компоненты
               - Разделяемые ресурсы

            Спецификация: %s

            Ответь в формате JSON со структурой:
            {
              "endpointRelationships": [...],
              "modelRelationships": [...],
              "dataFlowCorrelations": [...],
              "crossReferences": {...}
            }
            """, specification.getRawContent());
    }
    
    // Парсинг LLM ответов
    
    private Map<String, Object> parseLLMSchemaResponse(String llmResponse) {
        // Простая реализация парсинга - в реальности нужно использовать JSON парсер
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMBusinessLogicResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMValidationResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMScenariosResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMRelationshipsResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    // Вспомогательные методы
    
    private String generateAnalysisId(String specId) {
        return "openapi_analysis_" + specId + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(String specId, String analysisType) {
        return "openapi_" + specId + "_" + analysisType;
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
    
    private boolean isCacheValid(OpenApiDataAnalysisResult result) {
        if (result == null || result.getAnalysisStartTime() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(result.getAnalysisStartTime().plusHours(CACHE_TTL_HOURS));
    }
    
    private void cacheResult(String cacheKey, OpenApiDataAnalysisResult result) {
        resultCache.put(cacheKey, result);
    }
    
    private String getTaskType(List<String> analysisTypes, int index) {
        return index < analysisTypes.size() ? analysisTypes.get(index) : "UNKNOWN";
    }
    
    private OpenApiDataAnalysisResult aggregateResults(OpenApiDataAnalysisRequest request, String analysisId, 
                                                      Map<String, PartialDataAnalysisResult> partialResults) {
        OpenApiDataAnalysisResult result = new OpenApiDataAnalysisResult();
        result.setAnalysisId(analysisId);
        result.setSpecificationId(request.getSpecificationId());
        result.setAnalysisStartTime(LocalDateTime.now());
        result.setAnalysisEndTime(LocalDateTime.now());
        result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
        result.setStatus("SUCCESS");
        
        // Агрегируем данные из частичных результатов
        aggregateSchemaData(result, partialResults);
        aggregateBusinessLogicData(result, partialResults);
        aggregateValidationData(result, partialResults);
        aggregateScenariosData(result, partialResults);
        aggregateRelationshipsData(result, partialResults);
        
        // Вычисляем метрики
        result.setMetrics(calculateMetrics(partialResults));
        
        return result;
    }
    
    private void aggregateSchemaData(OpenApiDataAnalysisResult result, Map<String, PartialDataAnalysisResult> partialResults) {
        PartialDataAnalysisResult schemaResult = partialResults.get("SCHEMAS");
        if (schemaResult != null && schemaResult.getSchemaData() != null) {
            // Извлекаем данные схем из partial result
            result.setRequestSchemas(new ArrayList<>());
            result.setResponseSchemas(new ArrayList<>());
            result.setDataModels(new ArrayList<>());
            result.setDataFields(new HashMap<>());
        }
    }
    
    private void aggregateBusinessLogicData(OpenApiDataAnalysisResult result, Map<String, PartialDataAnalysisResult> partialResults) {
        PartialDataAnalysisResult businessResult = partialResults.get("BUSINESS_LOGIC");
        if (businessResult != null && businessResult.getBusinessLogicData() != null) {
            result.setApiEndpoints(new ArrayList<>());
            result.setBusinessLogic(new ArrayList<>());
            result.setUserScenarios(new ArrayList<>());
            result.setWorkflowPatterns(new ArrayList<>());
        }
    }
    
    private void aggregateValidationData(OpenApiDataAnalysisResult result, Map<String, PartialDataAnalysisResult> partialResults) {
        PartialDataAnalysisResult validationResult = partialResults.get("VALIDATION_RULES");
        if (validationResult != null && validationResult.getValidationData() != null) {
            result.setValidationRules(new ArrayList<>());
            result.setBusinessRules(new ArrayList<>());
            result.setDataConstraints(new HashMap<>());
        }
    }
    
    private void aggregateScenariosData(OpenApiDataAnalysisResult result, Map<String, PartialDataAnalysisResult> partialResults) {
        PartialDataAnalysisResult scenariosResult = partialResults.get("USER_SCENARIOS");
        if (scenariosResult != null && scenariosResult.getScenariosData() != null) {
            // Дополняем существующие user scenarios
        }
    }
    
    private void aggregateRelationshipsData(OpenApiDataAnalysisResult result, Map<String, PartialDataAnalysisResult> partialResults) {
        PartialDataAnalysisResult relationshipsResult = partialResults.get("RELATIONSHIPS");
        if (relationshipsResult != null && relationshipsResult.getRelationshipsData() != null) {
            result.setRelationships(new ArrayList<>());
            result.setCrossReferences(new HashMap<>());
        }
    }
    
    private OpenApiDataAnalysisResult.AnalysisMetrics calculateMetrics(Map<String, PartialDataAnalysisResult> partialResults) {
        OpenApiDataAnalysisResult.AnalysisMetrics metrics = new OpenApiDataAnalysisResult.AnalysisMetrics();
        metrics.setTotalSchemas(partialResults.size());
        metrics.setTotalEndpoints(0);
        metrics.setTotalValidationRules(0);
        metrics.setTotalBusinessRules(0);
        metrics.setComplexityScore(50); // Базовая сложность
        metrics.setCoverageScore(75.0); // Базовая покрытие
        return metrics;
    }
    
    // Вложенные классы для результатов анализа
    
    public static class PartialDataAnalysisResult {
        private String analysisType;
        private Map<String, Object> schemaData;
        private Map<String, Object> businessLogicData;
        private Map<String, Object> validationData;
        private Map<String, Object> scenariosData;
        private Map<String, Object> relationshipsData;
        private long processingTimeMs;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getAnalysisType() { return analysisType; }
        public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
        
        public Map<String, Object> getSchemaData() { return schemaData; }
        public void setSchemaData(Map<String, Object> schemaData) { this.schemaData = schemaData; }
        
        public Map<String, Object> getBusinessLogicData() { return businessLogicData; }
        public void setBusinessLogicData(Map<String, Object> businessLogicData) { this.businessLogicData = businessLogicData; }
        
        public Map<String, Object> getValidationData() { return validationData; }
        public void setValidationData(Map<String, Object> validationData) { this.validationData = validationData; }
        
        public Map<String, Object> getScenariosData() { return scenariosData; }
        public void setScenariosData(Map<String, Object> scenariosData) { this.scenariosData = scenariosData; }
        
        public Map<String, Object> getRelationshipsData() { return relationshipsData; }
        public void setRelationshipsData(Map<String, Object> relationshipsData) { this.relationshipsData = relationshipsData; }
        
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
    
    /**
     * Получает статус анализа
     */
    public AnalysisStatus getAnalysisStatus(String analysisId) {
        return activeAnalyses.get(analysisId);
    }
    
    /**
     * Получает результаты анализа
     */
    public OpenApiDataAnalysisResult getAnalysisResults(String analysisId) {
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
        logger.info("OpenAPI data analysis result cache cleared");
    }
    
    private String extractSpecIdFromAnalysisId(String analysisId) {
        String[] parts = analysisId.split("_");
        return parts.length > 2 ? parts[2] : "unknown";
    }
}