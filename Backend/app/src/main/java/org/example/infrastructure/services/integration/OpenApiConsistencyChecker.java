package org.example.infrastructure.services.integration;

import org.example.domain.model.consistency.*;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.llm.infrastructure.LocalLLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Специализированный проверяющий консистентности для OpenAPI спецификаций
 * Отвечает за проверки API-BPMN консистентности, выравнивания схем и другие API-связанные проверки
 */
@Component
public class OpenApiConsistencyChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenApiConsistencyChecker.class);
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private Executor dataConsistencyExecutor;
    
    /**
     * Выполняет проверку API консистентности
     */
    @Async
    public CompletableFuture<PartialCheckResult> checkApiConsistency(ConsistencyCheckRequest request, String checkId) {
        logger.info("Starting API consistency check for API: {} and BPMN: {}, checkId: {}", 
            request.getApiSpecId(), request.getBpmnDiagramId(), checkId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PartialCheckResult result = new PartialCheckResult("API_CONSISTENCY");
                result.setStatus("IN_PROGRESS");
                
                // Создаем подзадачи для API проверки
                Map<String, CompletableFuture<PartialCheckResult>> apiCheckTasks = new HashMap<>();
                
                // 1. Проверка соответствия endpoints и tasks
                apiCheckTasks.put("API_BPMN_MAPPING", checkApiBpmnMapping(request, checkId));
                
                // 2. Проверка выравнивания схем
                apiCheckTasks.put("SCHEMA_ALIGNMENT", checkSchemaAlignment(request, checkId));
                
                // 3. Проверка данных потоков
                apiCheckTasks.put("DATA_FLOW_CONSISTENCY", checkDataFlowConsistency(request, checkId));
                
                // 4. Проверка HTTP методов и task типов
                apiCheckTasks.put("METHOD_TASK_CONSISTENCY", checkMethodTaskConsistency(request, checkId));
                
                // Ожидаем все подзадачи
                CompletableFuture<Void> allSubTasks = CompletableFuture.allOf(
                    apiCheckTasks.values().toArray(new CompletableFuture[0])
                );
                
                return allSubTasks.thenApply(v -> {
                    try {
                        Map<String, Object> aggregatedData = new HashMap<>();
                        
                        // Агрегируем результаты подзадач
                        for (Map.Entry<String, CompletableFuture<PartialCheckResult>> entry : apiCheckTasks.entrySet()) {
                            PartialCheckResult subResult = entry.getValue().get();
                            aggregatedData.put(entry.getKey(), subResult.getCheckData());
                        }
                        
                        // Обновляем основной результат
                        result.setCheckData(aggregatedData);
                        result.setProcessingTimeMs(System.currentTimeMillis() - checkId.hashCode());
                        result.setStatus("COMPLETED");
                        result.setSuccess(true);
                        
                        logger.info("API consistency check completed for API: {} and BPMN: {}", 
                            request.getApiSpecId(), request.getBpmnDiagramId());
                        return result;
                        
                    } catch (Exception e) {
                        logger.error("Error in API consistency sub-tasks for API: {} and BPMN: {}", 
                            request.getApiSpecId(), request.getBpmnDiagramId(), e);
                        result.setStatus("FAILED");
                        result.setSuccess(false);
                        result.setErrorMessage(e.getMessage());
                        return result;
                    }
                }).join();
                
            } catch (Exception e) {
                logger.error("API consistency check failed for API: {} and BPMN: {}", 
                    request.getApiSpecId(), request.getBpmnDiagramId(), e);
                PartialCheckResult result = new PartialCheckResult("API_CONSISTENCY");
                result.setStatus("FAILED");
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет соответствие API endpoints и BPMN tasks
     */
    private CompletableFuture<PartialCheckResult> checkApiBpmnMapping(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildApiBpmnMappingPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "api_bpmn_mapping");
                
                PartialCheckResult result = new PartialCheckResult("API_BPMN_MAPPING");
                result.setCheckData(parseLLMApiBpmnMapping(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("API_BPMN_MAPPING");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет выравнивание схем данных
     */
    private CompletableFuture<PartialCheckResult> checkSchemaAlignment(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildSchemaAlignmentPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "schema_alignment");
                
                PartialCheckResult result = new PartialCheckResult("SCHEMA_ALIGNMENT");
                result.setCheckData(parseLLMSchemaAlignment(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("SCHEMA_ALIGNMENT");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет согласованность потоков данных
     */
    private CompletableFuture<PartialCheckResult> checkDataFlowConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildDataFlowConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "data_flow_consistency");
                
                PartialCheckResult result = new PartialCheckResult("DATA_FLOW_CONSISTENCY");
                result.setCheckData(parseLLMDataFlowConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("DATA_FLOW_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет соответствие HTTP методов и типов задач
     */
    private CompletableFuture<PartialCheckResult> checkMethodTaskConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildMethodTaskConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "method_task_consistency");
                
                PartialCheckResult result = new PartialCheckResult("METHOD_TASK_CONSISTENCY");
                result.setCheckData(parseLLMMethodTaskConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("METHOD_TASK_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    // Промпты для LLM анализа
    
    private String buildApiBpmnMappingPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь соответствие API endpoints и BPMN tasks:

            1. API-BPMN Mapping Analysis:
               - Соответствие endpoints процессам
               - Проверка task-endpoint маппинга
               - Валидация API-ориентированного дизайна

            2. Structural Consistency:
               - Соответствие структуры API и BPMN
               - Проверка иерархий и группировок
               - Валидация компонентной архитектуры

            3. Behavioral Alignment:
               - Соответствие поведения API и процессов
               - Проверка последовательностей операций
               - Валидация workflow логики

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "endpointTaskMappings": [...],
              "structuralConsistencies": [...],
              "behavioralAlignments": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    private String buildSchemaAlignmentPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь выравнивание схем данных API и BPMN:

            1. Schema Alignment Analysis:
               - Соответствие типов данных
               - Проверка схем запросов/ответов
               - Валидация структур данных

            2. Data Type Consistency:
               - Сопоставление типов данных
               - Проверка форматов и валидации
               - Валидация required/optional полей

            3. Field Mapping Validation:
               - Корректность маппинга полей
               - Проверка трансформаций данных
               - Валидация соответствия схем

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "schemaAlignments": [...],
              "typeConsistencies": [...],
              "fieldMappings": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    private String buildDataFlowConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь согласованность потоков данных:

            1. Data Flow Analysis:
               - Валидность потоков данных
               - Проверка последовательностей
               - Валидация направлений передачи

            2. End-to-End Data Flow:
               - Полные потоки от API к BPMN
               - Проверка связности процессов
               - Валидация complete workflows

            3. Data Transformation Consistency:
               - Согласованность трансформаций
               - Проверка форматов данных
               - Валидация конвертации типов

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "dataFlows": [...],
              "endToEndValidations": [...],
              "transformationConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    private String buildMethodTaskConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь соответствие HTTP методов и типов BPMN задач:

            1. Method-Task Mapping Analysis:
               - Соответствие HTTP методов task типам
               - Проверка RESTful дизайна
               - Валидация операционной логики

            2. CRUD Operation Alignment:
               - Соответствие CRUD операций
               - Проверка семантики методов
               - Валидация HTTP статусов

            3. Business Logic Consistency:
               - Соответствие бизнес-логики
               - Проверка операционной семантики
               - Валидация процедурных паттернов

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "methodTaskMappings": [...],
              "crudAlignments": [...],
              "businessLogicConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    // Парсинг LLM ответов
    
    private Map<String, Object> parseLLMApiBpmnMapping(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "api_bpmn_mapping");
        return result;
    }
    
    private Map<String, Object> parseLLMSchemaAlignment(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "schema_alignment");
        return result;
    }
    
    private Map<String, Object> parseLLMDataFlowConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "data_flow_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMMethodTaskConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "method_task_consistency");
        return result;
    }
    
    // Выполнение LLM анализа
    
    private String executeLLMAnalysis(String prompt, String checkType) {
        try {
            if (openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} check", checkType);
                return openRouterClient.chatCompletion("anthropic/claude-3-sonnet", prompt, 4000, 0.3)
                    .get(60, java.util.concurrent.TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.warn("OpenRouter failed for {} check, trying local model", checkType, e);
        }
        
        try {
            if (localLLMService.checkOllamaConnection() != null) {
                logger.debug("Using local LLM for {} check", checkType);
                return localLLMService.localChatCompletion("llama3.1:8b", prompt, 4000, 0.3)
                    .get(60, java.util.concurrent.TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.error("All LLM providers failed for {} check", checkType, e);
            throw new RuntimeException("No LLM provider available", e);
        }
        
        throw new RuntimeException("No LLM providers available");
    }
}
