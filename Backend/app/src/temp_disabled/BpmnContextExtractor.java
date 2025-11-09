package org.example.infrastructure.services.integration;

import org.example.domain.dto.integration.BpmnContextExtractionRequest;
import org.example.domain.dto.integration.BpmnContextExtractionResult;
import org.example.domain.entities.BpmnDiagram;
import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
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
 * Извлечение контекста из BPMN процессов
 * Интегрируется с BpmnAnalysisService для глубокого анализа процессов
 */
@Service
public class BpmnContextExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(BpmnContextExtractor.class);
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private Executor bpmnExtractionExecutor;
    
    // Кэш для результатов извлечения
    private final Map<String, BpmnContextExtractionResult> resultCache = new ConcurrentHashMap<>();
    private final Map<String, ExtractionStatus> activeExtractions = new ConcurrentHashMap<>();
    
    // Конфигурация извлечения
    private static final int MAX_CONCURRENT_EXTRACTIONS = 3;
    private static final long CACHE_TTL_HOURS = 24;
    private static final long EXTRACTION_TIMEOUT_MINUTES = 30;
    
    /**
     * Выполняет комплексное извлечение контекста из BPMN диаграммы
     */
    @Async
    public CompletableFuture<BpmnContextExtractionResult> extractBpmnContext(BpmnContextExtractionRequest request) {
        String extractionId = generateExtractionId(request.getDiagramId());
        logger.info("Starting BPMN context extraction for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
        
        try {
            activeExtractions.put(extractionId, new ExtractionStatus(extractionId, "STARTED", LocalDateTime.now()));
            
            // Проверяем кэш
            String cacheKey = generateCacheKey(request.getDiagramId(), String.join(",", request.getExtractionTypes()));
            if (resultCache.containsKey(cacheKey)) {
                BpmnContextExtractionResult cached = resultCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached BPMN context extraction result for diagram: {}", request.getDiagramId());
                    return CompletableFuture.completedFuture(cached);
                }
            }
            
            // Подготавливаем задачи извлечения
            List<CompletableFuture<PartialExtractionResult>> extractionTasks = new ArrayList<>();
            
            // 1. Извлечение переменных процесса
            if (request.getExtractionTypes().contains("PROCESS_VARIABLES")) {
                extractionTasks.add(extractProcessVariables(request, extractionId));
            }
            
            // 2. Извлечение данных задач
            if (request.getExtractionTypes().contains("TASK_DATA")) {
                extractionTasks.add(extractTaskData(request, extractionId));
            }
            
            // 3. Извлечение условий шлюзов
            if (request.getExtractionTypes().contains("GATEWAY_CONDITIONS")) {
                extractionTasks.add(extractGatewayConditions(request, extractionId));
            }
            
            // 4. Извлечение данных событий
            if (request.getExtractionTypes().contains("EVENT_DATA")) {
                extractionTasks.add(extractEventData(request, extractionId));
            }
            
            // 5. Извлечение бизнес-правил
            if (request.getExtractionTypes().contains("BUSINESS_RULES")) {
                extractionTasks.add(extractBusinessRules(request, extractionId));
            }
            
            // 6. Анализ потоков данных
            if (request.getExtractionTypes().contains("DATA_FLOWS")) {
                extractionTasks.add(analyzeDataFlows(request, extractionId));
            }
            
            // Ожидаем завершения всех задач
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                extractionTasks.toArray(new CompletableFuture[0])
            );
            
            return allTasks.thenApply(v -> {
                try {
                    updateExtractionStatus(extractionId, "AGGREGATING");
                    
                    // Агрегируем результаты
                    Map<String, PartialExtractionResult> partialResults = new HashMap<>();
                    for (int i = 0; i < extractionTasks.size(); i++) {
                        String taskType = getTaskType(request.getExtractionTypes(), i);
                        partialResults.put(taskType, extractionTasks.get(i).get());
                    }
                    
                    BpmnContextExtractionResult finalResult = aggregateResults(request, extractionId, partialResults);
                    
                    // Кэшируем результат
                    cacheResult(cacheKey, finalResult);
                    
                    // Обновляем статус
                    activeExtractions.put(extractionId, 
                        new ExtractionStatus(extractionId, "COMPLETED", LocalDateTime.now()));
                    
                    logger.info("BPMN context extraction completed for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
                    return finalResult;
                    
                } catch (Exception e) {
                    logger.error("Error aggregating BPMN context extraction results", e);
                    activeExtractions.put(extractionId, 
                        new ExtractionStatus(extractionId, "FAILED", LocalDateTime.now(), e.getMessage()));
                    throw new RuntimeException("BPMN context extraction aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting BPMN context extraction for diagram: {}", request.getDiagramId(), e);
            activeExtractions.put(extractionId, 
                new ExtractionStatus(extractionId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Извлечение переменных процесса
     */
    @Async
    public CompletableFuture<PartialExtractionResult> extractProcessVariables(BpmnContextExtractionRequest request, String extractionId) {
        logger.info("Starting process variables extraction for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateExtractionStatus(extractionId, "PROCESS_VARIABLES_EXTRACTION");
                
                // Создаем BPMN диаграмму для анализа
                BpmnDiagram diagram = createBpmnDiagram(request);
                
                // Выполняем анализ через BpmnAnalysisService
                CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture = 
                    bpmnAnalysisService.analyzeBpmnDiagram(request.getDiagramId(), diagram);
                
                BpmnAnalysisService.BpmnAnalysisResult analysisResult = analysisFuture.get();
                
                // Генерируем промпт для извлечения переменных
                String prompt = buildProcessVariablesPrompt(diagram, analysisResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "process_variables");
                
                // Парсим результат
                Map<String, Object> variablesData = parseLLMVariablesResponse(llmResponse);
                
                PartialExtractionResult result = new PartialExtractionResult();
                result.setExtractionType("PROCESS_VARIABLES");
                result.setVariablesData(variablesData);
                result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Process variables extraction completed for diagram: {}", request.getDiagramId());
                return result;
                
            } catch (Exception e) {
                logger.error("Process variables extraction failed for diagram: {}", request.getDiagramId(), e);
                throw new RuntimeException("Process variables extraction failed", e);
            }
        }, bpmnExtractionExecutor);
    }
    
    /**
     * Извлечение данных задач
     */
    @Async
    public CompletableFuture<PartialExtractionResult> extractTaskData(BpmnContextExtractionRequest request, String extractionId) {
        logger.info("Starting task data extraction for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateExtractionStatus(extractionId, "TASK_DATA_EXTRACTION");
                
                BpmnDiagram diagram = createBpmnDiagram(request);
                
                CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture = 
                    bpmnAnalysisService.analyzeBpmnDiagram(request.getDiagramId(), diagram);
                
                BpmnAnalysisService.BpmnAnalysisResult analysisResult = analysisFuture.get();
                
                // Генерируем промпт для извлечения данных задач
                String prompt = buildTaskDataPrompt(diagram, analysisResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "task_data");
                
                // Парсим результат
                Map<String, Object> taskData = parseLLMTaskDataResponse(llmResponse);
                
                PartialExtractionResult result = new PartialExtractionResult();
                result.setExtractionType("TASK_DATA");
                result.setTaskData(taskData);
                result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Task data extraction completed for diagram: {}", request.getDiagramId());
                return result;
                
            } catch (Exception e) {
                logger.error("Task data extraction failed for diagram: {}", request.getDiagramId(), e);
                throw new RuntimeException("Task data extraction failed", e);
            }
        }, bpmnExtractionExecutor);
    }
    
    /**
     * Извлечение условий шлюзов
     */
    @Async
    public CompletableFuture<PartialExtractionResult> extractGatewayConditions(BpmnContextExtractionRequest request, String extractionId) {
        logger.info("Starting gateway conditions extraction for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateExtractionStatus(extractionId, "GATEWAY_CONDITIONS_EXTRACTION");
                
                BpmnDiagram diagram = createBpmnDiagram(request);
                
                CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture = 
                    bpmnAnalysisService.analyzeBpmnDiagram(request.getDiagramId(), diagram);
                
                BpmnAnalysisService.BpmnAnalysisResult analysisResult = analysisFuture.get();
                
                // Генерируем промпт для извлечения условий шлюзов
                String prompt = buildGatewayConditionsPrompt(diagram, analysisResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "gateway_conditions");
                
                // Парсим результат
                Map<String, Object> gatewayData = parseLLMGatewayConditionsResponse(llmResponse);
                
                PartialExtractionResult result = new PartialExtractionResult();
                result.setExtractionType("GATEWAY_CONDITIONS");
                result.setGatewayData(gatewayData);
                result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Gateway conditions extraction completed for diagram: {}", request.getDiagramId());
                return result;
                
            } catch (Exception e) {
                logger.error("Gateway conditions extraction failed for diagram: {}", request.getDiagramId(), e);
                throw new RuntimeException("Gateway conditions extraction failed", e);
            }
        }, bpmnExtractionExecutor);
    }
    
    /**
     * Извлечение данных событий
     */
    @Async
    public CompletableFuture<PartialExtractionResult> extractEventData(BpmnContextExtractionRequest request, String extractionId) {
        logger.info("Starting event data extraction for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateExtractionStatus(extractionId, "EVENT_DATA_EXTRACTION");
                
                BpmnDiagram diagram = createBpmnDiagram(request);
                
                CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture = 
                    bpmnAnalysisService.analyzeBpmnDiagram(request.getDiagramId(), diagram);
                
                BpmnAnalysisService.BpmnAnalysisResult analysisResult = analysisFuture.get();
                
                // Генерируем промпт для извлечения данных событий
                String prompt = buildEventDataPrompt(diagram, analysisResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "event_data");
                
                // Парсим результат
                Map<String, Object> eventData = parseLLMEventDataResponse(llmResponse);
                
                PartialExtractionResult result = new PartialExtractionResult();
                result.setExtractionType("EVENT_DATA");
                result.setEventData(eventData);
                result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Event data extraction completed for diagram: {}", request.getDiagramId());
                return result;
                
            } catch (Exception e) {
                logger.error("Event data extraction failed for diagram: {}", request.getDiagramId(), e);
                throw new RuntimeException("Event data extraction failed", e);
            }
        }, bpmnExtractionExecutor);
    }
    
    /**
     * Извлечение бизнес-правил
     */
    @Async
    public CompletableFuture<PartialExtractionResult> extractBusinessRules(BpmnContextExtractionRequest request, String extractionId) {
        logger.info("Starting business rules extraction for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateExtractionStatus(extractionId, "BUSINESS_RULES_EXTRACTION");
                
                BpmnDiagram diagram = createBpmnDiagram(request);
                
                CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture = 
                    bpmnAnalysisService.analyzeBpmnDiagram(request.getDiagramId(), diagram);
                
                BpmnAnalysisService.BpmnAnalysisResult analysisResult = analysisFuture.get();
                
                // Генерируем промпт для извлечения бизнес-правил
                String prompt = buildBusinessRulesPrompt(diagram, analysisResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "business_rules");
                
                // Парсим результат
                Map<String, Object> businessRulesData = parseLLMBusinessRulesResponse(llmResponse);
                
                PartialExtractionResult result = new PartialExtractionResult();
                result.setExtractionType("BUSINESS_RULES");
                result.setBusinessRulesData(businessRulesData);
                result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Business rules extraction completed for diagram: {}", request.getDiagramId());
                return result;
                
            } catch (Exception e) {
                logger.error("Business rules extraction failed for diagram: {}", request.getDiagramId(), e);
                throw new RuntimeException("Business rules extraction failed", e);
            }
        }, bpmnExtractionExecutor);
    }
    
    /**
     * Анализ потоков данных
     */
    @Async
    public CompletableFuture<PartialExtractionResult> analyzeDataFlows(BpmnContextExtractionRequest request, String extractionId) {
        logger.info("Starting data flows analysis for diagram: {}, extractionId: {}", request.getDiagramId(), extractionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateExtractionStatus(extractionId, "DATA_FLOWS_ANALYSIS");
                
                BpmnDiagram diagram = createBpmnDiagram(request);
                
                CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture = 
                    bpmnAnalysisService.analyzeBpmnDiagram(request.getDiagramId(), diagram);
                
                BpmnAnalysisService.BpmnAnalysisResult analysisResult = analysisFuture.get();
                
                // Генерируем промпт для анализа потоков данных
                String prompt = buildDataFlowsPrompt(diagram, analysisResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "data_flows");
                
                // Парсим результат
                Map<String, Object> dataFlowsData = parseLLMDataFlowsResponse(llmResponse);
                
                PartialExtractionResult result = new PartialExtractionResult();
                result.setExtractionType("DATA_FLOWS");
                result.setDataFlowsData(dataFlowsData);
                result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Data flows analysis completed for diagram: {}", request.getDiagramId());
                return result;
                
            } catch (Exception e) {
                logger.error("Data flows analysis failed for diagram: {}", request.getDiagramId(), e);
                throw new RuntimeException("Data flows analysis failed", e);
            }
        }, bpmnExtractionExecutor);
    }
    
    /**
     * Выполняет LLM анализ с fallback на разные провайдеры
     */
    private String executeLLMAnalysis(String prompt, String extractionType) {
        try {
            // Пробуем сначала OpenRouter (внешние модели)
            if (openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} BPMN extraction", extractionType);
                return openRouterClient.chatCompletion(getPreferredModel(), prompt, 2000, 0.3)
                    .get(EXTRACTION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.warn("OpenRouter failed for {} BPMN extraction, trying local model", extractionType, e);
        }
        
        try {
            // Fallback на локальную модель
            if (localLLMService.checkOllamaConnection() != null) {
                logger.debug("Using local LLM for {} BPMN extraction", extractionType);
                return localLLMService.localChatCompletion(getPreferredLocalModel(), prompt, 2000, 0.3)
                    .get(EXTRACTION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.error("All LLM providers failed for {} BPMN extraction", extractionType, e);
            throw new RuntimeException("No LLM provider available for BPMN extraction", e);
        }
        
        throw new RuntimeException("No LLM providers available");
    }
    
    // Вспомогательные методы для генерации промптов
    
    private String buildProcessVariablesPrompt(BpmnDiagram diagram, BpmnAnalysisService.BpmnAnalysisResult analysisResult) {
        return String.format("""
            Извлеки переменные процесса из данной BPMN диаграммы:

            1. Переменные процесса (Process Variables):
               - Имена переменных
               - Типы данных
               - Область видимости (PROCESS, TASK, GLOBAL)
               - Обязательность

            2. Источники данных (Data Sources):
               - Пользовательский ввод
               - Системные данные
               - Внешние системы
               - Вычисляемые значения

            3. Валидационные правила (Validation Rules):
               - Правила для переменных
               - Ограничения значений
               - Форматы данных

            BPMN Диаграмма: %s
            Результаты анализа: %s

            Ответь в формате JSON со структурой:
            {
              "processVariables": [...],
              "dataSources": [...],
              "validationRules": [...]
            }
            """, diagram.getBpmnContent(), 
            analysisResult != null ? analysisResult.toString() : "N/A");
    }
    
    private String buildTaskDataPrompt(BpmnDiagram diagram, BpmnAnalysisService.BpmnAnalysisResult analysisResult) {
        return String.format("""
            Извлеки данные задач из данной BPMN диаграммы:

            1. Входные данные задач (Task Inputs):
               - Идентификатор задачи
               - Имя поля
               - Тип данных
               - Источник данных
               - Валидационные правила

            2. Выходные данные задач (Task Outputs):
               - Идентификатор задачи
               - Имя поля
               - Тип данных
               - Назначение
               - Формат

            3. Зависимости данных (Data Dependencies):
               - Связи между задачами
               - Потоки данных
               - Трансформации

            BPMN Диаграмма: %s
            Результаты анализа: %s

            Ответь в формате JSON со структурой:
            {
              "taskInputs": [...],
              "taskOutputs": [...],
              "dataDependencies": [...]
            }
            """, diagram.getBpmnContent(), 
            analysisResult != null ? analysisResult.toString() : "N/A");
    }
    
    private String buildGatewayConditionsPrompt(BpmnDiagram diagram, BpmnAnalysisService.BpmnAnalysisResult analysisResult) {
        return String.format("""
            Извлеки условия шлюзов из данной BPMN диаграммы:

            1. Условия шлюзов (Gateway Conditions):
               - Идентификатор шлюза
               - Название
               - Тип шлюза (EXCLUSIVE, PARALLEL, INCLUSIVE)
               - Условие принятия решения
               - Исходящие потоки

            2. Логика принятия решений (Decision Logic):
               - Критерии выбора
               - Приоритеты правил
               - Последовательность проверки

            BPMN Диаграмма: %s
            Результаты анализа: %s

            Ответь в формате JSON со структурой:
            {
              "gatewayConditions": [...],
              "decisionLogic": [...]
            }
            """, diagram.getBpmnContent(), 
            analysisResult != null ? analysisResult.toString() : "N/A");
    }
    
    private String buildEventDataPrompt(BpmnDiagram diagram, BpmnAnalysisService.BpmnAnalysisResult analysisResult) {
        return String.format("""
            Извлеки данные событий из данной BPMN диаграммы:

            1. События (Events):
               - Идентификатор события
               - Название
               - Тип события (START, END, INTERMEDIATE)
               - Тип триггера (MESSAGE, TIMER, SIGNAL)
               - Связанные данные

            2. Потоки событий (Event Flows):
               - Последовательность событий
               - Условия срабатывания
               - Передаваемые данные

            BPMN Диаграмма: %s
            Результаты анализа: %s

            Ответь в формате JSON со структурой:
            {
              "events": [...],
              "eventFlows": [...]
            }
            """, diagram.getBpmnContent(), 
            analysisResult != null ? analysisResult.toString() : "N/A");
    }
    
    private String buildBusinessRulesPrompt(BpmnDiagram diagram, BpmnAnalysisService.BpmnAnalysisResult analysisResult) {
        return String.format("""
            Извлеки бизнес-правила из данной BPMN диаграммы:

            1. Бизнес-правила (Business Rules):
               - Название правила
               - Тип правила
               - Описание
               - Условия применения
               - Действия

            2. Точки принятия решений (Decision Points):
               - Идентификатор
               - Название
               - Тип решения
               - Варианты выбора
               - Критерии

            3. Паттерны взаимодействия с пользователем (User Interaction Patterns):
               - Тип взаимодействия
               - Действия пользователя
               - Параметры

            BPMN Диаграмма: %s
            Результаты анализа: %s

            Ответь в формате JSON со структурой:
            {
              "businessRules": [...],
              "decisionPoints": [...],
              "userInteractionPatterns": [...]
            }
            """, diagram.getBpmnContent(), 
            analysisResult != null ? analysisResult.toString() : "N/A");
    }
    
    private String buildDataFlowsPrompt(BpmnDiagram diagram, BpmnAnalysisService.BpmnAnalysisResult analysisResult) {
        return String.format("""
            Проанализируй потоки данных в данной BPMN диаграмме:

            1. Потоки данных (Data Flows):
               - Источник данных
               - Назначение
               - Тип потока
               - Условия передачи

            2. Интеграционные точки (Integration Points):
               - Тип интеграции
               - Направление (вход/выход/двунаправленный)
               - Конфигурация

            3. Трансформации данных (Data Transformations):
               - Преобразования
               - Правила трансформации
               - Валидация

            BPMN Диаграмма: %s
            Результаты анализа: %s

            Ответь в формате JSON со структурой:
            {
              "dataFlows": [...],
              "integrationPoints": [...],
              "dataTransformations": [...]
            }
            """, diagram.getBpmnContent(), 
            analysisResult != null ? analysisResult.toString() : "N/A");
    }
    
    // Парсинг LLM ответов
    
    private Map<String, Object> parseLLMVariablesResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMTaskDataResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMGatewayConditionsResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMEventDataResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMBusinessRulesResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    private Map<String, Object> parseLLMDataFlowsResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        return result;
    }
    
    // Вспомогательные методы
    
    private BpmnDiagram createBpmnDiagram(BpmnContextExtractionRequest request) {
        BpmnDiagram diagram = new BpmnDiagram();
        diagram.setDiagramId(request.getDiagramId());
        diagram.setBpmnContent(request.getBpmnContent());
        diagram.setCreatedAt(LocalDateTime.now());
        return diagram;
    }
    
    private String generateExtractionId(String diagramId) {
        return "bpmn_extraction_" + diagramId + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(String diagramId, String extractionType) {
        return "bpmn_" + diagramId + "_" + extractionType;
    }
    
    private String getPreferredModel() {
        return "anthropic/claude-3-sonnet";
    }
    
    private String getPreferredLocalModel() {
        return "llama3.1:8b";
    }
    
    private void updateExtractionStatus(String extractionId, String status) {
        activeExtractions.put(extractionId, new ExtractionStatus(extractionId, status, LocalDateTime.now()));
    }
    
    private boolean isCacheValid(BpmnContextExtractionResult result) {
        if (result == null || result.getExtractionStartTime() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(result.getExtractionStartTime().plusHours(CACHE_TTL_HOURS));
    }
    
    private void cacheResult(String cacheKey, BpmnContextExtractionResult result) {
        resultCache.put(cacheKey, result);
    }
    
    private String getTaskType(List<String> extractionTypes, int index) {
        return index < extractionTypes.size() ? extractionTypes.get(index) : "UNKNOWN";
    }
    
    private BpmnContextExtractionResult aggregateResults(BpmnContextExtractionRequest request, String extractionId, 
                                                        Map<String, PartialExtractionResult> partialResults) {
        BpmnContextExtractionResult result = new BpmnContextExtractionResult();
        result.setExtractionId(extractionId);
        result.setDiagramId(request.getDiagramId());
        result.setExtractionStartTime(LocalDateTime.now());
        result.setExtractionEndTime(LocalDateTime.now());
        result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
        result.setStatus("SUCCESS");
        
        // Агрегируем данные из частичных результатов
        aggregateVariablesData(result, partialResults);
        aggregateTaskData(result, partialResults);
        aggregateGatewayData(result, partialResults);
        aggregateEventData(result, partialResults);
        aggregateBusinessRulesData(result, partialResults);
        aggregateDataFlowsData(result, partialResults);
        
        // Вычисляем метрики
        result.setMetrics(calculateMetrics(partialResults));
        
        return result;
    }
    
    private void aggregateVariablesData(BpmnContextExtractionResult result, Map<String, PartialExtractionResult> partialResults) {
        PartialExtractionResult variablesResult = partialResults.get("PROCESS_VARIABLES");
        if (variablesResult != null && variablesResult.getVariablesData() != null) {
            result.setProcessVariables(new ArrayList<>());
        }
    }
    
    private void aggregateTaskData(BpmnContextExtractionResult result, Map<String, PartialExtractionResult> partialResults) {
        PartialExtractionResult taskDataResult = partialResults.get("TASK_DATA");
        if (taskDataResult != null && taskDataResult.getTaskData() != null) {
            result.setTaskInputs(new ArrayList<>());
            result.setTaskOutputs(new ArrayList<>());
        }
    }
    
    private void aggregateGatewayData(BpmnContextExtractionResult result, Map<String, PartialExtractionResult> partialResults) {
        PartialExtractionResult gatewayResult = partialResults.get("GATEWAY_CONDITIONS");
        if (gatewayResult != null && gatewayResult.getGatewayData() != null) {
            result.setGatewayConditions(new ArrayList<>());
        }
    }
    
    private void aggregateEventData(BpmnContextExtractionResult result, Map<String, PartialExtractionResult> partialResults) {
        PartialExtractionResult eventResult = partialResults.get("EVENT_DATA");
        if (eventResult != null && eventResult.getEventData() != null) {
            result.setEventDataFlows(new ArrayList<>());
        }
    }
    
    private void aggregateBusinessRulesData(BpmnContextExtractionResult result, Map<String, PartialExtractionResult> partialResults) {
        PartialExtractionResult businessRulesResult = partialResults.get("BUSINESS_RULES");
        if (businessRulesResult != null && businessRulesResult.getBusinessRulesData() != null) {
            result.setBusinessRules(new ArrayList<>());
            result.setDecisionPoints(new ArrayList<>());
            result.setUserInteractionPatterns(new ArrayList<>());
            result.setSystemIntegrationPoints(new ArrayList<>());
        }
    }
    
    private void aggregateDataFlowsData(BpmnContextExtractionResult result, Map<String, PartialExtractionResult> partialResults) {
        PartialExtractionResult dataFlowsResult = partialResults.get("DATA_FLOWS");
        if (dataFlowsResult != null && dataFlowsResult.getDataFlowsData() != null) {
            result.setDataTemplates(new ArrayList<>());
            result.setGenerationContexts(new ArrayList<>());
            result.setProcessContexts(new HashMap<>());
        }
    }
    
    private BpmnContextExtractionResult.ExtractionMetrics calculateMetrics(Map<String, PartialExtractionResult> partialResults) {
        BpmnContextExtractionResult.ExtractionMetrics metrics = new BpmnContextExtractionResult.ExtractionMetrics();
        metrics.setTotalProcesses(partialResults.size());
        metrics.setTotalTasks(0);
        metrics.setTotalGateways(0);
        metrics.setTotalEvents(0);
        metrics.setTotalVariables(0);
        metrics.setTotalBusinessRules(0);
        metrics.setComplexityScore(50); // Базовая сложность
        return metrics;
    }
    
    // Вложенные классы для результатов извлечения
    
    public static class PartialExtractionResult {
        private String extractionType;
        private Map<String, Object> variablesData;
        private Map<String, Object> taskData;
        private Map<String, Object> gatewayData;
        private Map<String, Object> eventData;
        private Map<String, Object> businessRulesData;
        private Map<String, Object> dataFlowsData;
        private long processingTimeMs;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getExtractionType() { return extractionType; }
        public void setExtractionType(String extractionType) { this.extractionType = extractionType; }
        
        public Map<String, Object> getVariablesData() { return variablesData; }
        public void setVariablesData(Map<String, Object> variablesData) { this.variablesData = variablesData; }
        
        public Map<String, Object> getTaskData() { return taskData; }
        public void setTaskData(Map<String, Object> taskData) { this.taskData = taskData; }
        
        public Map<String, Object> getGatewayData() { return gatewayData; }
        public void setGatewayData(Map<String, Object> gatewayData) { this.gatewayData = gatewayData; }
        
        public Map<String, Object> getEventData() { return eventData; }
        public void setEventData(Map<String, Object> eventData) { this.eventData = eventData; }
        
        public Map<String, Object> getBusinessRulesData() { return businessRulesData; }
        public void setBusinessRulesData(Map<String, Object> businessRulesData) { this.businessRulesData = businessRulesData; }
        
        public Map<String, Object> getDataFlowsData() { return dataFlowsData; }
        public void setDataFlowsData(Map<String, Object> dataFlowsData) { this.dataFlowsData = dataFlowsData; }
        
        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    public static class ExtractionStatus {
        private String extractionId;
        private String status;
        private LocalDateTime timestamp;
        private String errorMessage;
        
        public ExtractionStatus(String extractionId, String status, LocalDateTime timestamp) {
            this.extractionId = extractionId;
            this.status = status;
            this.timestamp = timestamp;
        }
        
        public ExtractionStatus(String extractionId, String status, LocalDateTime timestamp, String errorMessage) {
            this(extractionId, status, timestamp);
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public String getExtractionId() { return extractionId; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    /**
     * Получает статус извлечения
     */
    public ExtractionStatus getExtractionStatus(String extractionId) {
        return activeExtractions.get(extractionId);
    }
    
    /**
     * Получает результаты извлечения
     */
    public BpmnContextExtractionResult getExtractionResults(String extractionId) {
        return activeExtractions.values().stream()
            .filter(status -> status.getExtractionId().equals(extractionId))
            .map(status -> {
                String cacheKey = generateCacheKey(extractDiagramIdFromExtractionId(extractionId), "comprehensive");
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
        logger.info("BPMN context extraction result cache cleared");
    }
    
    private String extractDiagramIdFromExtractionId(String extractionId) {
        String[] parts = extractionId.split("_");
        return parts.length > 2 ? parts[2] : "unknown";
    }
}