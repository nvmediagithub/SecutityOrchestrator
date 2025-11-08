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
 * Специализированный проверяющий консистентности для BPMN процессов
 * Отвечает за проверки согласованности бизнес-правил, потоков процессов и других BPMN-связанных аспектов
 */
@Component
public class BpmnConsistencyChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(BpmnConsistencyChecker.class);
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private Executor dataConsistencyExecutor;
    
    /**
     * Выполняет проверку BPMN консистентности
     */
    @Async
    public CompletableFuture<PartialCheckResult> checkBpmnConsistency(ConsistencyCheckRequest request, String checkId) {
        logger.info("Starting BPMN consistency check for API: {} and BPMN: {}, checkId: {}", 
            request.getApiSpecId(), request.getBpmnDiagramId(), checkId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PartialCheckResult result = new PartialCheckResult("BPMN_CONSISTENCY");
                result.setStatus("IN_PROGRESS");
                
                // Создаем подзадачи для BPMN проверки
                Map<String, CompletableFuture<PartialCheckResult>> bpmnCheckTasks = new HashMap<>();
                
                // 1. Проверка согласованности бизнес-правил
                bpmnCheckTasks.put("BUSINESS_RULE_CONSISTENCY", checkBusinessRuleConsistency(request, checkId));
                
                // 2. Проверка потоков процессов
                bpmnCheckTasks.put("PROCESS_FLOW_CONSISTENCY", checkProcessFlowConsistency(request, checkId));
                
                // 3. Проверка структурной целостности
                bpmnCheckTasks.put("STRUCTURAL_INTEGRITY", checkStructuralIntegrity(request, checkId));
                
                // 4. Проверка событий и таймеров
                bpmnCheckTasks.put("EVENT_TIMER_CONSISTENCY", checkEventTimerConsistency(request, checkId));
                
                // 5. Проверка шлюзов и условий
                bpmnCheckTasks.put("GATEWAY_CONDITION_CONSISTENCY", checkGatewayConditionConsistency(request, checkId));
                
                // Ожидаем все подзадачи
                CompletableFuture<Void> allSubTasks = CompletableFuture.allOf(
                    bpmnCheckTasks.values().toArray(new CompletableFuture[0])
                );
                
                return allSubTasks.thenApply(v -> {
                    try {
                        Map<String, Object> aggregatedData = new HashMap<>();
                        
                        // Агрегируем результаты подзадач
                        for (Map.Entry<String, CompletableFuture<PartialCheckResult>> entry : bpmnCheckTasks.entrySet()) {
                            PartialCheckResult subResult = entry.getValue().get();
                            aggregatedData.put(entry.getKey(), subResult.getCheckData());
                        }
                        
                        // Обновляем основной результат
                        result.setCheckData(aggregatedData);
                        result.setProcessingTimeMs(System.currentTimeMillis() - checkId.hashCode());
                        result.setStatus("COMPLETED");
                        result.setSuccess(true);
                        
                        logger.info("BPMN consistency check completed for API: {} and BPMN: {}", 
                            request.getApiSpecId(), request.getBpmnDiagramId());
                        return result;
                        
                    } catch (Exception e) {
                        logger.error("Error in BPMN consistency sub-tasks for API: {} and BPMN: {}", 
                            request.getApiSpecId(), request.getBpmnDiagramId(), e);
                        result.setStatus("FAILED");
                        result.setSuccess(false);
                        result.setErrorMessage(e.getMessage());
                        return result;
                    }
                }).join();
                
            } catch (Exception e) {
                logger.error("BPMN consistency check failed for API: {} and BPMN: {}", 
                    request.getApiSpecId(), request.getBpmnDiagramId(), e);
                PartialCheckResult result = new PartialCheckResult("BPMN_CONSISTENCY");
                result.setStatus("FAILED");
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет согласованность бизнес-правил
     */
    private CompletableFuture<PartialCheckResult> checkBusinessRuleConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildBusinessRuleConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "business_rule_consistency");
                
                PartialCheckResult result = new PartialCheckResult("BUSINESS_RULE_CONSISTENCY");
                result.setCheckData(parseLLMBusinessRuleConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("BUSINESS_RULE_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет согласованность потоков процессов
     */
    private CompletableFuture<PartialCheckResult> checkProcessFlowConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildProcessFlowConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "process_flow_consistency");
                
                PartialCheckResult result = new PartialCheckResult("PROCESS_FLOW_CONSISTENCY");
                result.setCheckData(parseLLMProcessFlowConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("PROCESS_FLOW_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет структурную целостность
     */
    private CompletableFuture<PartialCheckResult> checkStructuralIntegrity(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildStructuralIntegrityPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "structural_integrity");
                
                PartialCheckResult result = new PartialCheckResult("STRUCTURAL_INTEGRITY");
                result.setCheckData(parseLLMStructuralIntegrity(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("STRUCTURAL_INTEGRITY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет согласованность событий и таймеров
     */
    private CompletableFuture<PartialCheckResult> checkEventTimerConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildEventTimerConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "event_timer_consistency");
                
                PartialCheckResult result = new PartialCheckResult("EVENT_TIMER_CONSISTENCY");
                result.setCheckData(parseLLMEventTimerConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("EVENT_TIMER_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    /**
     * Проверяет согласованность шлюзов и условий
     */
    private CompletableFuture<PartialCheckResult> checkGatewayConditionConsistency(ConsistencyCheckRequest request, String checkId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = buildGatewayConditionConsistencyPrompt(request);
                String llmResponse = executeLLMAnalysis(prompt, "gateway_condition_consistency");
                
                PartialCheckResult result = new PartialCheckResult("GATEWAY_CONDITION_CONSISTENCY");
                result.setCheckData(parseLLMGatewayConditionConsistency(llmResponse));
                result.setStatus("COMPLETED");
                return result;
                
            } catch (Exception e) {
                PartialCheckResult result = new PartialCheckResult("GATEWAY_CONDITION_CONSISTENCY");
                result.setStatus("FAILED");
                result.setErrorMessage(e.getMessage());
                return result;
            }
        }, dataConsistencyExecutor);
    }
    
    // Промпты для LLM анализа
    
    private String buildBusinessRuleConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь согласованность бизнес-правил в BPMN процессе:

            1. Business Rule Consistency:
               - Соответствие бизнес-логики API и BPMN
               - Проверка правил валидации данных
               - Валидация бизнес-ограничений

            2. Logic Validation:
               - Валидация принятия решений
               - Проверка условной логики
               - Валидация workflow правил

            3. Rule Implementation Check:
               - Соответствие реализации правил
               - Проверка условий и ограничений
               - Валидация процедурных правил

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "businessRuleConsistencies": [...],
              "logicValidations": [...],
              "ruleImplementationChecks": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    private String buildProcessFlowConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь согласованность потоков процессов BPMN:

            1. Process Flow Analysis:
               - Валидность потоков процессов
               - Проверка последовательностей операций
               - Валидация направлений выполнения

            2. End-to-End Workflow:
               - Полные workflow сценарии
               - Проверка связности процессов
               - Валидация complete бизнес-процессов

            3. Flow Control Consistency:
               - Согласованность управления потоками
               - Проверка переходов между задачами
               - Валидация условий выполнения

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "processFlows": [...],
              "endToEndWorkflows": [...],
              "flowControlConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    private String buildStructuralIntegrityPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь структурную целостность BPMN процесса:

            1. Structural Integrity Analysis:
               - Целостность структуры процесса
               - Проверка компонентной архитектуры
               - Валидация иерархий и группировок

            2. Element Relationship Validation:
               - Валидация связей между элементами
               - Проверка корректности соединений
               - Валидация референсов и ссылок

            3. Process Architecture Consistency:
               - Соответствие архитектурных паттернов
               - Проверка модульности процессов
               - Валидация компонентного дизайна

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "structuralIntegrities": [...],
              "elementRelationships": [...],
              "architectureConsistencies": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    private String buildEventTimerConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь согласованность событий и таймеров в BPMN:

            1. Event-Timer Consistency:
               - Соответствие событий и таймеров
               - Проверка временных ограничений
               - Валидация триггеров и условий

            2. Temporal Logic Validation:
               - Валидация временной логики
               - Проверка последовательностей по времени
               - Валидация timeout и deadline логики

            3. Event-Driven Process Alignment:
               - Соответствие event-driven паттернов
               - Проверка реактивного поведения
               - Валидация асинхронных операций

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "eventTimerConsistencies": [...],
              "temporalLogics": [...],
              "eventDrivenAlignments": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    private String buildGatewayConditionConsistencyPrompt(ConsistencyCheckRequest request) {
        return String.format("""
            Проверь согласованность шлюзов и условий в BPMN:

            1. Gateway-Condition Alignment:
               - Соответствие шлюзов и условий
               - Проверка логики ветвления
               - Валидация условий принятия решений

            2. Decision Logic Consistency:
               - Согласованность логики решений
               - Проверка mutually exclusive условий
               - Валидация completeness условий

            3. Flow Routing Validation:
               - Валидация маршрутизации потоков
               - Проверка converge/divergent логики
               - Валидация merge и split паттернов

            API Analysis: %s
            BPMN Analysis: %s

            Ответь в формате JSON:
            {
              "gatewayConditionAlignments": [...],
              "decisionLogics": [...],
              "flowRoutings": [...]
            }
            """, 
            request.getApiAnalysisResult() != null ? request.getApiAnalysisResult().toString() : "N/A",
            request.getBpmnAnalysisResult() != null ? request.getBpmnAnalysisResult().toString() : "N/A");
    }
    
    // Парсинг LLM ответов
    
    private Map<String, Object> parseLLMBusinessRuleConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "business_rule_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMProcessFlowConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "process_flow_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMStructuralIntegrity(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "structural_integrity");
        return result;
    }
    
    private Map<String, Object> parseLLMEventTimerConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "event_timer_consistency");
        return result;
    }
    
    private Map<String, Object> parseLLMGatewayConditionConsistency(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("type", "gateway_condition_consistency");
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