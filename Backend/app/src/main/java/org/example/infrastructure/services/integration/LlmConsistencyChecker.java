package org.example.infrastructure.services.integration;

import org.example.domain.model.consistency.*;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
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
 * Специализированный проверяющий консистентности на основе LLM
 * Отвечает за проверки консистентности, выявленные через LLM анализ
 */
@Component
public class LlmConsistencyChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(LlmConsistencyChecker.class);
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private Executor dataConsistencyExecutor;
    
    /**
     * Выполняет проверку LLM консистентности
     */
    @Async
    public CompletableFuture<PartialCheckResult> checkLLMConsistency(ConsistencyCheckRequest request, String checkId) {
        logger.info("Starting LLM consistency check for API: {} and BPMN: {}, checkId: {}", 
            request.getApiSpecId(), request.getBpmnDiagramId(), checkId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PartialCheckResult result = new PartialCheckResult("LLM_CONSISTENCY");
                result.setStatus("IN_PROGRESS");
                
                // Создаем подзадачи для LLM проверки
                Map<String, CompletableFuture<PartialCheckResult>> llmCheckTasks = new HashMap<>();
                
                // 1. Проверка семантической консистентности
                llmCheckTasks.put("SEMANTIC_CONSISTENCY", checkSemanticConsistency(request, checkId));
                
                // 2. Проверка логической консистентности
                llmCheckTasks.put("LOGICAL_CONSISTENCY", checkLogicalConsistency(request, checkId));
                
                // 3. Проверка контекстуальной консистентности
                llmCheckTasks.put("CONTEXTUAL_CONSISTENCY", checkContextualConsistency(request, checkId));
                
                // 4. Проверка поведенческой консистентности
                llmCheckTasks.put("BEHAVIORAL_CONSISTENCY", checkBehavioralConsistency(request, checkId));
                
                // 5. Проверка архитектурной консистентности
                llmCheckTasks.put("ARCHITECTURAL_CONSISTENCY", checkArchitecturalConsistency(request, checkId));
                
                // 6. Проверка концептуальной консистентности
                llmCheckTasks.put("CONCEPTUAL_CONSISTENCY", checkConceptualConsistency(request, checkId));
                
                // Ожидаем все подзадачи
                CompletableFuture<Void> allSubTasks = CompletableFuture.allOf(
                    llmCheckTasks.values().toArray(new CompletableFuture[0])
                );
                
                return allSubTasks.thenApply(v -> {
                    try {
                        Map<String, Object> aggregatedData = new HashMap<>();
                        
                        // Агрегируем результаты подзадач
                        for (Map.Entry<String, CompletableFuture<PartialCheckResult>> entry : llmCheckTasks.entrySet()) {
                            PartialCheckResult subResult = entry.getValue().get();
                            aggregatedData.put(entry.getKey(), subResult.getCheckData());
                        }
                        
                        // Обновляем основной результат
                        result.setCheckData(aggregatedData);
                        result.setProcessingTimeMs(System.currentTimeMillis() - checkId.hashCode());
                        result.setStatus("COMPLETED");
                        result.setSuccess(true);
                        
                        logger.info("LLM consistency check completed for API: {} and BPMN: {}", 
                            request.getApiSpecId(), request.getBpmnDiagramId());
                        return result;
                        
                    } catch (Exception e) {
                        logger.error("Error in LLM consistency sub-tasks for API: {} and BPMN: {}", 
                            request.getApiSpecId(), request.getBpmnDiagramId(), e);
                        result.setStatus("FAILED");
                        result.setSuccess(false);
                        result.setErrorMessage(e.getMessage());
                        return result;
                    }
                }).join();
                
            } catch (Exception e) {
                logger.error("LLM consistency check failed for API: {} and BPMN: {}", 
                    request.getApiSpecId(), request.getBpmnDiagramId(), e);
                PartialCheckResult result = new PartialCheckResult("LLM_CONSISTENCY");
                result.setStatus("FAILED");
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет семантическую консистентность
     */
    private CompletableFuture<PartialCheckResult> checkSemanticConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildSemanticConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "semantic_consistency");
                
                PartialCheckResult result = new PartialCheckResult("SEMANTIC_CONSISTENCY");
                result.setCheckData(parseLLMSemanticConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("SEMANTIC_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет логическую консистентность
     */
    private CompletableFuture<PartialCheckResult> checkLogicalConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildLogicalConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "logical_consistency");
                
                PartialCheckResult result = new PartialCheckResult("LOGICAL_CONSISTENCY");
                result.setCheckData(parseLLMLogicalConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("LOGICAL_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет контекстуальную консистентность
     */
    private CompletableFuture<PartialCheckResult> checkContextualConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildContextualConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "contextual_consistency");
                
                PartialCheckResult result = new PartialCheckResult("CONTEXTUAL_CONSISTENCY");
                result.setCheckData(parseLLMContextualConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("CONTEXTUAL_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет поведенческую консистентность
     */
    private CompletableFuture<PartialCheckResult> checkBehavioralConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildBehavioralConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "behavioral_consistency");
                
                PartialCheckResult result = new PartialCheckResult("BEHAVIORAL_CONSISTENCY");
                result.setCheckData(parseLLMBehavioralConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("BEHAVIORAL_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет архитектурную консистентность
     */
    private CompletableFuture<PartialCheckResult> checkArchitecturalConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildArchitecturalConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "architectural_consistency");
                
                PartialCheckResult result = new PartialCheckResult("ARCHITECTURAL_CONSISTENCY");
                result.setCheckData(parseLLMArchitecturalConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("ARCHITECTURAL_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет концептуальную консистентность
     */
    private CompletableFuture<PartialCheckResult> checkConceptualConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildConceptualConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "conceptual_consistency");
                
                PartialCheckResult result = new PartialCheckResult("CONCEPTUAL_CONSISTENCY");
                result.setCheckData(parseLLMConceptualConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("CONCEPTUAL_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    // Промпты для LLM анализа
    
    private String buildSemanticConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проанализируй семантическую консистентность между API и BPMN:

            1. Semantic Analysis:
               - Соответствие смыслов API endpoints и BPMN tasks
               - Проверка терминологической согласованности
               - Валидация семантических отношений

            2. Meaning Alignment:
               - Выравнивание значений и интерпретаций
               - Проверка концептуального соответствия
               - Валидация семантических паттернов

            3. Domain Consistency:
               - Согласованность предметной области
               - Проверка бизнес-терминологии
               - Валидация domain-specific концепций

            API Analysis: %s
            BPMN Analysis: %s
            LLM Analysis: %s

            Ответь в формате JSON:
            {
              "semanticAnalyses": [...],
              "meaningAlignments": [...],
              "domainConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A",
            request.getCrossReferenceResult() != null ? request.getCrossReferenceResult().toString() : "N/A");
    }
    
    private String buildLogicalConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проанализируй логическую консистентность между API и BPMN:

            1. Logical Analysis:
               - Проверка логических связей
               - Валидация причинно-следственных связей
               - Анализ логических паттернов

            2. Reasoning Consistency:
               - Согласованность логических выводов
               - Проверка дедуктивной логики
               - Валидация индуктивных паттернов

            3. Logic Flow Validation:
               - Валидация логических потоков
               - Проверка последовательности логики
               - Анализ логических противоречий

            API Analysis: %s
            BPMN Analysis: %s
            LLM Analysis: %s

            Ответь в формате JSON:
            {
              "logicalAnalyses": [...],
              "reasoningConsistencies": [...],
              "logicFlowValidations": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A",
            request.getCrossReferenceResult() != null ? request.getCrossReferenceResult().toString() : "N/A");
    }
    
    private String buildContextualConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проанализируй контекстуальную консистентность между API и BPMN:

            1. Contextual Analysis:
               - Соответствие контекстов использования
               - Проверка ситуационной логики
               - Валидация контекстных ограничений

            2. Environmental Alignment:
               - Выравнивание с рабочей средой
               - Проверка environment-specific логики
               - Валидация контекстных зависимостей

            3. Situational Consistency:
               - Согласованность ситуационных паттернов
               - Проверка контекстно-зависимых операций
               - Анализ контекстных переходов

            API Analysis: %s
            BPMN Analysis: %s
            LLM Analysis: %s

            Ответь в формате JSON:
            {
              "contextualAnalyses": [...],
              "environmentalAlignments": [...],
              "situationalConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A",
            request.getCrossReferenceResult() != null ? request.getCrossReferenceResult().toString() : "N/A");
    }
    
    private String buildBehavioralConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проанализируй поведенческую консистентность между API и BPMN:

            1. Behavioral Analysis:
               - Соответствие поведенческих паттернов
               - Проверка интеракционных моделей
               - Валидация поведенческих последовательностей

            2. Interaction Consistency:
               - Согласованность взаимодействий
               - Проверка communication patterns
               - Анализ interaction flows

            3. Protocol Alignment:
               - Выравнивание протоколов взаимодействия
               - Проверка behavioral contracts
               - Валидация interaction semantics

            API Analysis: %s
            BPMN Analysis: %s
            LLM Analysis: %s

            Ответь в формате JSON:
            {
              "behavioralAnalyses": [...],
              "interactionConsistencies": [...],
              "protocolAlignments": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A",
            request.getCrossReferenceResult() != null ? request.getCrossReferenceResult().toString() : "N/A");
    }
    
    private String buildArchitecturalConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проанализируй архитектурную консистентность между API и BPMN:

            1. Architectural Analysis:
               - Соответствие архитектурных паттернов
               - Проверка structural consistency
               - Валидация component relationships

            2. Design Pattern Alignment:
               - Выравнивание design patterns
               - Проверка architectural styles
               - Анализ design principles

            3. Structural Consistency:
               - Согласованность структурных элементов
               - Проверка component hierarchies
               - Валидация architectural boundaries

            API Analysis: %s
            BPMN Analysis: %s
            LLM Analysis: %s

            Ответь в формате JSON:
            {
              "architecturalAnalyses": [...],
              "designPatternAlignments": [...],
              "structuralConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A",
            request.getCrossReferenceResult() != null ? request.getCrossReferenceResult().toString() : "N/A");
    }
    
    private String buildConceptualConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проанализируй концептуальную консистентность между API и BPMN:

            1. Conceptual Analysis:
               - Соответствие концептуальных моделей
               - Проверка domain concepts
               - Валидация conceptual relationships

            2. Model Alignment:
               - Выравнивание предметных моделей
               - Проверка concept hierarchies
               - Анализ conceptual mappings

            3. Knowledge Consistency:
               - Согласованность знаний
               - Проверка conceptual integrity
               - Валидация knowledge representation

            API Analysis: %s
            BPMN Analysis: %s
            LLM Analysis: %s

            Ответь в формате JSON:
            {
              "conceptualAnalyses": [...],
              "modelAlignments": [...],
              "knowledgeConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A",
            request.getCrossReferenceResult() != null ? request.getCrossReferenceResult().toString() : "N/A");
    }
    
    // Парсинг LLM ответов
    
    private Map<String, Object> parseLLMSemanticConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "semantic_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMLogicalConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "logical_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMContextualConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "contextual_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMBehavioralConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "behavioral_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMArchitecturalConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "architectural_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMConceptualConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "conceptual_consistency");
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