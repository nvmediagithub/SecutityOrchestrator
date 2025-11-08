package org.example.infrastructure.services.llm;

import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.llm.ConsistencyAnalysis;
import org.example.domain.entities.llm.InconsistencyReport;
import org.example.domain.entities.llm.LLMSuggestion;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.valueobjects.InconsistencyType;
import org.example.domain.valueobjects.SeverityLevel;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Сервис для обнаружения несогласованностей между OpenAPI и BPMN с использованием LLM
 * Фокусируется на выявлении конкретных проблем и несоответствий
 */
@Service
public class LLMInconsistencyDetectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMInconsistencyDetectionService.class);
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private Executor detectionExecutor;
    
    // Кэш для результатов обнаружения
    private final Map<String, DetectionResult> detectionCache = new ConcurrentHashMap<>();
    private final Map<String, DetectionStatus> activeDetections = new ConcurrentHashMap<>();
    
    // Конфигурация обнаружения
    private static final long DETECTION_TIMEOUT_MINUTES = 20;
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.6;
    private static final int MAX_INCONSISTENCIES_PER_CATEGORY = 50;
    
    /**
     * Обнаруживает несогласованности между OpenAPI и BPMN
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectInconsistencies(String openApiSpecId, String bpmnProcessId) {
        String detectionId = generateDetectionId(openApiSpecId, bpmnProcessId);
        logger.info("Starting inconsistency detection: openApiSpecId={}, bpmnProcessId={}, detectionId={}", 
                   openApiSpecId, bpmnProcessId, detectionId);
        
        try {
            activeDetections.put(detectionId, new DetectionStatus(detectionId, "STARTED", LocalDateTime.now()));
            
            // Проверяем кэш
            String cacheKey = generateCacheKey(openApiSpecId, bpmnProcessId);
            if (detectionCache.containsKey(cacheKey)) {
                DetectionResult cached = detectionCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached detection result for spec: {}, process: {}", openApiSpecId, bpmnProcessId);
                    activeDetections.put(detectionId, new DetectionStatus(detectionId, "COMPLETED", LocalDateTime.now()));
                    return CompletableFuture.completedFuture(cached.getInconsistencies());
                }
            }
            
            // Подготавливаем задачи обнаружения
            List<CompletableFuture<List<InconsistencyReport>>> detectionTasks = new ArrayList<>();
            
            // 1. Логические противоречия
            detectionTasks.add(detectLogicalContradictions(detectionId, openApiSpecId, bpmnProcessId));
            
            // 2. Семантические несоответствия
            detectionTasks.add(detectSemanticMismatches(detectionId, openApiSpecId, bpmnProcessId));
            
            // 3. Структурные различия
            detectionTasks.add(detectStructuralDifferences(detectionId, openApiSpecId, bpmnProcessId));
            
            // 4. Проблемы с типами данных
            detectionTasks.add(detectDataTypeIssues(detectionId, openApiSpecId, bpmnProcessId));
            
            // 5. Несоответствия в безопасности
            detectionTasks.add(detectSecurityInconsistencies(detectionId, openApiSpecId, bpmnProcessId));
            
            // 6. Проблемы с обработкой ошибок
            detectionTasks.add(detectErrorHandlingIssues(detectionId, openApiSpecId, bpmnProcessId));
            
            // 7. Недостающие элементы
            detectionTasks.add(detectMissingElements(detectionId, openApiSpecId, bpmnProcessId));
            
            // 8. Валидационные проблемы
            detectionTasks.add(detectValidationIssues(detectionId, openApiSpecId, bpmnProcessId));
            
            // Ожидаем завершения всех задач
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                detectionTasks.toArray(new CompletableFuture[0])
            );
            
            return allTasks.thenApply(v -> {
                try {
                    updateDetectionStatus(detectionId, "AGGREGATING");
                    
                    // Агрегируем результаты
                    List<InconsistencyReport> allInconsistencies = new ArrayList<>();
                    
                    for (CompletableFuture<List<InconsistencyReport>> task : detectionTasks) {
                        List<InconsistencyReport> inconsistencies = task.get();
                        if (inconsistencies != null) {
                            allInconsistencies.addAll(inconsistencies);
                        }
                    }
                    
                    // Фильтруем и приоритизируем
                    List<InconsistencyReport> filteredInconsistencies = filterAndPrioritizeInconsistencies(allInconsistencies);
                    
                    // Кэшируем результат
                    DetectionResult result = new DetectionResult(detectionId, openApiSpecId, bpmnProcessId, 
                                                               filteredInconsistencies, LocalDateTime.now());
                    cacheResult(cacheKey, result);
                    
                    // Обновляем статус
                    activeDetections.put(detectionId, new DetectionStatus(detectionId, "COMPLETED", LocalDateTime.now()));
                    
                    logger.info("Inconsistency detection completed: found {} issues", filteredInconsistencies.size());
                    return filteredInconsistencies;
                    
                } catch (Exception e) {
                    logger.error("Error aggregating detection results", e);
                    activeDetections.put(detectionId, 
                        new DetectionStatus(detectionId, "FAILED", LocalDateTime.now(), e.getMessage()));
                    throw new RuntimeException("Detection aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting detection for spec: {}, process: {}", openApiSpecId, bpmnProcessId, e);
            activeDetections.put(detectionId, 
                new DetectionStatus(detectionId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Обнаружение логических противоречий
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectLogicalContradictions(String detectionId, 
                                                                                    String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting logical contradictions: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "LOGICAL_CONTRADICTIONS");
                
                String prompt = buildLogicalContradictionsPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "logical_contradictions");
                
                List<InconsistencyReport> inconsistencies = parseLogicalContradictions(llmResponse);
                
                logger.info("Logical contradictions detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Logical contradictions detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Обнаружение семантических несоответствий
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectSemanticMismatches(String detectionId,
                                                                                String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting semantic mismatches: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "SEMANTIC_MISMATCHES");
                
                String prompt = buildSemanticMismatchesPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "semantic_mismatches");
                
                List<InconsistencyReport> inconsistencies = parseSemanticMismatches(llmResponse);
                
                logger.info("Semantic mismatches detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Semantic mismatches detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Обнаружение структурных различий
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectStructuralDifferences(String detectionId,
                                                                                    String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting structural differences: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "STRUCTURAL_DIFFERENCES");
                
                String prompt = buildStructuralDifferencesPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "structural_differences");
                
                List<InconsistencyReport> inconsistencies = parseStructuralDifferences(llmResponse);
                
                logger.info("Structural differences detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Structural differences detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Обнаружение проблем с типами данных
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectDataTypeIssues(String detectionId,
                                                                             String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting data type issues: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "DATA_TYPE_ISSUES");
                
                String prompt = buildDataTypeIssuesPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "data_type_issues");
                
                List<InconsistencyReport> inconsistencies = parseDataTypeIssues(llmResponse);
                
                logger.info("Data type issues detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Data type issues detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Обнаружение несоответствий в безопасности
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectSecurityInconsistencies(String detectionId,
                                                                                      String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting security inconsistencies: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "SECURITY_INCONSISTENCIES");
                
                String prompt = buildSecurityInconsistenciesPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "security_inconsistencies");
                
                List<InconsistencyReport> inconsistencies = parseSecurityInconsistencies(llmResponse);
                
                logger.info("Security inconsistencies detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Security inconsistencies detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Обнаружение проблем с обработкой ошибок
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectErrorHandlingIssues(String detectionId,
                                                                                  String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting error handling issues: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "ERROR_HANDLING_ISSUES");
                
                String prompt = buildErrorHandlingIssuesPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "error_handling_issues");
                
                List<InconsistencyReport> inconsistencies = parseErrorHandlingIssues(llmResponse);
                
                logger.info("Error handling issues detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Error handling issues detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Обнаружение недостающих элементов
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectMissingElements(String detectionId,
                                                                              String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting missing elements: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "MISSING_ELEMENTS");
                
                String prompt = buildMissingElementsPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "missing_elements");
                
                List<InconsistencyReport> inconsistencies = parseMissingElements(llmResponse);
                
                logger.info("Missing elements detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Missing elements detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Обнаружение валидационных проблем
     */
    @Async
    public CompletableFuture<List<InconsistencyReport>> detectValidationIssues(String detectionId,
                                                                               String openApiSpecId, String bpmnProcessId) {
        logger.debug("Detecting validation issues: {}", detectionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateDetectionStatus(detectionId, "VALIDATION_ISSUES");
                
                String prompt = buildValidationIssuesPrompt(openApiSpecId, bpmnProcessId);
                String llmResponse = executeLLMAnalysis(prompt, "validation_issues");
                
                List<InconsistencyReport> inconsistencies = parseValidationIssues(llmResponse);
                
                logger.info("Validation issues detection completed: found {} issues", inconsistencies.size());
                return inconsistencies;
                
            } catch (Exception e) {
                logger.error("Validation issues detection failed: {}", detectionId, e);
                return Collections.emptyList();
            }
        }, detectionExecutor);
    }
    
    /**
     * Выполняет LLM анализ с fallback на разные провайдеры
     */
    private String executeLLMAnalysis(String prompt, String detectionType) {
        try {
            // Пробуем сначала OpenRouter (внешние модели)
            if (openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} detection", detectionType);
                return openRouterClient.chatCompletion(getPreferredModel(), prompt, 1500, 0.2)
                    .get(DETECTION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.warn("OpenRouter failed for {} detection, trying local model", detectionType, e);
        }
        
        try {
            // Fallback на локальную модель
            if (localLLMService.checkOllamaConnection() != null) {
                logger.debug("Using local LLM for {} detection", detectionType);
                return localLLMService.localChatCompletion(getPreferredLocalModel(), prompt, 1500, 0.2)
                    .get(DETECTION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.error("All LLM providers failed for {} detection", detectionType, e);
            throw new RuntimeException("No LLM provider available for detection", e);
        }
        
        throw new RuntimeException("No LLM providers available");
    }
    
    // Вспомогательные методы для генерации промптов
    
    private String buildLogicalContradictionsPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди логические противоречия между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Противоречия в HTTP методах vs бизнес-операциях
            - Несоответствия в статусах ответов vs результатах процессов
            - Логические ошибки в последовательности операций
            - Противоречия в условной логике
            
            Верни список найденных противоречий в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    private String buildSemanticMismatchesPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди семантические несоответствия между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Различия в именах эндпоинтов vs задач
            - Несоответствия в описаниях и документации
            - Семантические различия в параметрах
            - Различия в типах операций
            
            Верни список семантических несоответствий в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    private String buildStructuralDifferencesPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди структурные различия между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Отсутствующие эндпоинты для BPMN задач
            - Неиспользуемые BPMN задачи
            - Структурные несоответствия в потоках
            - Различия в иерархии элементов
            
            Верни список структурных различий в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    private String buildDataTypeIssuesPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди проблемы с типами данных между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Несоответствия типов данных параметров
            - Различия в форматах данных
            - Проблемы с обязательными полями
            - Несоответствия в валидации данных
            
            Верни список проблем с типами данных в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    private String buildSecurityInconsistenciesPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди несоответствия в безопасности между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Различия в требованиях аутентификации
            - Несоответствия в авторизации
            - Различия в обработке конфиденциальных данных
            - Проблемы с безопасностью операций
            
            Верни список проблем безопасности в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    private String buildErrorHandlingIssuesPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди проблемы с обработкой ошибок между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Несоответствия в кодах ошибок HTTP vs BPMN исключениях
            - Различия в сообщениях об ошибках
            - Проблемы в обработке исключений
            - Несоответствия в восстановлении после ошибок
            
            Верни список проблем обработки ошибок в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    private String buildMissingElementsPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди недостающие элементы между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Недостающие API эндпоинты для BPMN задач
            - Неиспользуемые BPMN процессы
            - Отсутствующие обработчики ошибок
            - Недостающие валидации
            
            Верни список недостающих элементов в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    private String buildValidationIssuesPrompt(String openApiSpecId, String bpmnProcessId) {
        return String.format("""
            Найди валидационные проблемы между OpenAPI и BPMN:
            
            OpenAPI Spec ID: %s
            BPMN Process ID: %s
            
            Проверь:
            - Недостающие валидации в API vs BPMN требованиях
            - Несоответствия в бизнес-правилах валидации
            - Проблемы с граничными значениями
            - Несоответствия в форматах данных
            
            Верни список валидационных проблем в JSON формате.
            """, openApiSpecId, bpmnProcessId);
    }
    
    // Вспомогательные методы для парсинга результатов LLM
    
    private List<InconsistencyReport> parseLogicalContradictions(String llmResponse) {
        List<InconsistencyReport> inconsistencies = new ArrayList<>();
        
        if (llmResponse.contains("contradiction") || llmResponse.contains("противоречие")) {
            inconsistencies.add(new InconsistencyReport.Builder()
                .type(InconsistencyType.SCHEMA_INCONSISTENCY)
                .severity(SeverityLevel.HIGH)
                .title("Logical Contradiction Detected")
                .description("Found logical contradiction between API and BPMN process")
                .openApiElement("Multiple endpoints")
                .bpmnElement("Process flow")
                .confidence(InconsistencyReport.ConfidenceLevel.HIGH)
                .impact(InconsistencyReport.ImpactAssessment.HIGH)
                .build());
        }
        
        return inconsistencies;
    }
    
    private List<InconsistencyReport> parseSemanticMismatches(String llmResponse) {
        List<InconsistencyReport> inconsistencies = new ArrayList<>();
        
        if (llmResponse.contains("semantic mismatch") || llmResponse.contains("семантическое несоответствие")) {
            inconsistencies.add(new InconsistencyReport.Builder()
                .type(InconsistencyType.DESCRIPTION_INCONSISTENCY)
                .severity(SeverityLevel.MEDIUM)
                .title("Semantic Mismatch")
                .description("API endpoint naming doesn't match BPMN task semantics")
                .openApiElement("Endpoint naming")
                .bpmnElement("Task naming")
                .confidence(InconsistencyReport.ConfidenceLevel.MEDIUM)
                .impact(InconsistencyReport.ImpactAssessment.MEDIUM)
                .build());
        }
        
        return inconsistencies;
    }
    
    private List<InconsistencyReport> parseStructuralDifferences(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<InconsistencyReport> parseDataTypeIssues(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<InconsistencyReport> parseSecurityInconsistencies(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<InconsistencyReport> parseErrorHandlingIssues(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<InconsistencyReport> parseMissingElements(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    private List<InconsistencyReport> parseValidationIssues(String llmResponse) {
        return List.of(); // Упрощенная реализация
    }
    
    /**
     * Фильтрует и приоритизирует найденные несогласованности
     */
    private List<InconsistencyReport> filterAndPrioritizeInconsistencies(List<InconsistencyReport> allInconsistencies) {
        return allInconsistencies.stream()
            .filter(issue -> issue.getConfidence().getScore() >= MIN_CONFIDENCE_THRESHOLD)
            .filter(issue -> !issue.isResolved())
            .sorted((a, b) -> {
                // Сортируем по важности
                int severityCompare = Integer.compare(
                    a.getSeverity().getPriority(), 
                    b.getSeverity().getPriority()
                );
                if (severityCompare != 0) {
                    return severityCompare;
                }
                
                // Затем по уверенности
                return Double.compare(
                    b.getConfidence().getScore(), 
                    a.getConfidence().getScore()
                );
            })
            .limit(MAX_INCONSISTENCIES_PER_CATEGORY * 8) // Максимум по всем категориям
            .collect(Collectors.toList());
    }
    
    // Вспомогательные методы
    
    private String generateDetectionId(String openApiSpecId, String bpmnProcessId) {
        return "detection_" + openApiSpecId + "_" + bpmnProcessId + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(String openApiSpecId, String bpmnProcessId) {
        return openApiSpecId + "_" + bpmnProcessId;
    }
    
    private String getPreferredModel() {
        return "anthropic/claude-3-sonnet";
    }
    
    private String getPreferredLocalModel() {
        return "llama3.1:8b";
    }
    
    private void updateDetectionStatus(String detectionId, String status) {
        activeDetections.put(detectionId, new DetectionStatus(detectionId, status, LocalDateTime.now()));
    }
    
    private boolean isCacheValid(DetectionResult result) {
        if (result == null || result.getDetectedAt() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(result.getDetectedAt().plusHours(12)); // 12 часов TTL
    }
    
    private void cacheResult(String cacheKey, DetectionResult result) {
        detectionCache.put(cacheKey, result);
    }
    
    // Геттеры для статуса и результатов
    
    public DetectionStatus getDetectionStatus(String detectionId) {
        return activeDetections.get(detectionId);
    }
    
    public DetectionResult getDetectionResults(String detectionId) {
        return activeDetections.values().stream()
            .filter(status -> status.getDetectionId().equals(detectionId))
            .map(status -> {
                // Извлекаем ключ кэша из detectionId
                String[] parts = detectionId.split("_");
                if (parts.length >= 3) {
                    String openApiSpecId = parts[1];
                    String bpmnProcessId = parts[2];
                    String cacheKey = generateCacheKey(openApiSpecId, bpmnProcessId);
                    return detectionCache.get(cacheKey);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
    
    public void clearCache() {
        detectionCache.clear();
        logger.info("Detection result cache cleared");
    }
    
    // Вспомогательные классы
    
    public static class DetectionStatus {
        private final String detectionId;
        private final String status;
        private final LocalDateTime timestamp;
        private final String errorMessage;
        
        public DetectionStatus(String detectionId, String status, LocalDateTime timestamp) {
            this.detectionId = detectionId;
            this.status = status;
            this.timestamp = timestamp;
            this.errorMessage = null;
        }
        
        public DetectionStatus(String detectionId, String status, LocalDateTime timestamp, String errorMessage) {
            this.detectionId = detectionId;
            this.status = status;
            this.timestamp = timestamp;
            this.errorMessage = errorMessage;
        }
        
        // Геттеры
        public String getDetectionId() { return detectionId; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class DetectionResult {
        private final String detectionId;
        private final String openApiSpecId;
        private final String bpmnProcessId;
        private final List<InconsistencyReport> inconsistencies;
        private final LocalDateTime detectedAt;
        
        public DetectionResult(String detectionId, String openApiSpecId, String bpmnProcessId,
                              List<InconsistencyReport> inconsistencies, LocalDateTime detectedAt) {
            this.detectionId = detectionId;
            this.openApiSpecId = openApiSpecId;
            this.bpmnProcessId = bpmnProcessId;
            this.inconsistencies = inconsistencies;
            this.detectedAt = detectedAt;
        }
        
        // Геттеры
        public String getDetectionId() { return detectionId; }
        public String getOpenApiSpecId() { return openApiSpecId; }
        public String getBpmnProcessId() { return bpmnProcessId; }
        public List<InconsistencyReport> getInconsistencies() { return inconsistencies; }
        public LocalDateTime getDetectedAt() { return detectedAt; }
        
        public int getTotalInconsistencies() {
            return inconsistencies != null ? inconsistencies.size() : 0;
        }
        
        public long getCriticalInconsistencies() {
            return inconsistencies != null ? 
                inconsistencies.stream().filter(InconsistencyReport::isCritical).count() : 0;
        }
        
        public long getHighInconsistencies() {
            return inconsistencies != null ? 
                inconsistencies.stream().filter(InconsistencyReport::isHighPriority).count() : 0;
        }
    }
}