package org.example.application.service.context;

import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.application.service.testdata.IntelligentDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ContextPreservingGenerator - генератор с сохранением контекста
 * Сохраняет контекст BPMN процессов и API при генерации данных
 */
@Service
public class ContextPreservingGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(ContextPreservingGenerator.class);
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private OpenApiAnalysisService openApiAnalysisService;
    
    @Autowired
    private IntelligentDataGenerator intelligentDataGenerator;
    
    @Autowired
    private Executor contextPreservationExecutor;
    
    // Кэш для контекстов и сгенерированных данных
    private final Map<String, Map<String, Object>> bpmnContextCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> apiContextCache = new ConcurrentHashMap<>();
    private final Map<String, List<Map<String, Object>>> preservedDataCache = new ConcurrentHashMap<>();
    
    /**
     * Генерирует данные с сохранением BPMN контекста
     */
    public Map<String, Object> generateDataWithBpmnContext(String requestId, 
                                                          String diagramId, 
                                                          List<Map<String, Object>> processVariables,
                                                          int recordCount) {
        logger.info("Generating data with BPMN context for request: {}, diagram: {}", requestId, diagramId);
        
        try {
            // 1. Извлекаем BPMN контекст
            Map<String, Object> bpmnContext = extractBpmnContext(diagramId, processVariables);
            
            // 2. Сохраняем контекст в кэше
            bpmnContextCache.put(requestId, bpmnContext);
            
            // 3. Генерируем данные с учетом контекста
            List<Map<String, Object>> contextAwareData = generateContextAwareData(bpmnContext, recordCount);
            
            // 4. Добавляем traceable контекст к данным
            List<Map<String, Object>> traceableData = addContextTraceability(contextAwareData, bpmnContext);
            
            // 5. Создаем результат
            Map<String, Object> result = createBpmnPreservationResult(requestId, traceableData, bpmnContext);
            
            // 6. Кэшируем результат
            preservedDataCache.put(requestId, traceableData);
            
            logger.info("BPMN context preservation completed for request: {}, generated {} records", 
                requestId, traceableData.size());
            
            return result;
            
        } catch (Exception e) {
            logger.error("BPMN context preservation failed for request: {}", requestId, e);
            throw new RuntimeException("BPMN context preservation failed", e);
        }
    }
    
    /**
     * Генерирует данные с сохранением API контекста
     */
    public Map<String, Object> generateDataWithApiContext(String requestId, 
                                                        String specId, 
                                                        List<Map<String, Object>> apiEndpoints,
                                                        int recordCount) {
        logger.info("Generating data with API context for request: {}, spec: {}", requestId, specId);
        
        try {
            // 1. Извлекаем API контекст
            Map<String, Object> apiContext = extractApiContext(specId, apiEndpoints);
            
            // 2. Сохраняем контекст в кэше
            apiContextCache.put(requestId, apiContext);
            
            // 3. Генерируем данные с учетом API контекста
            List<Map<String, Object>> contextAwareData = generateApiContextAwareData(apiContext, recordCount);
            
            // 4. Добавляем request-response пары
            List<Map<String, Object>> traceableData = addApiContextTraceability(contextAwareData, apiContext);
            
            // 5. Создаем результат
            Map<String, Object> result = createApiPreservationResult(requestId, traceableData, apiContext);
            
            // 6. Кэшируем результат
            preservedDataCache.put(requestId, traceableData);
            
            logger.info("API context preservation completed for request: {}, generated {} records", 
                requestId, traceableData.size());
            
            return result;
            
        } catch (Exception e) {
            logger.error("API context preservation failed for request: {}", requestId, e);
            throw new RuntimeException("API context preservation failed", e);
        }
    }
    
    /**
     * Генерирует данные с комплексным контекстом (BPMN + API)
     */
    public Map<String, Object> generateDataWithComplexContext(String requestId,
                                                             String diagramId,
                                                             String specId,
                                                             List<Map<String, Object>> bpmnVariables,
                                                             List<Map<String, Object>> apiEndpoints,
                                                             int recordCount) {
        logger.info("Generating data with complex context for request: {}", requestId);
        
        try {
            // 1. Извлекаем оба контекста
            Map<String, Object> bpmnContext = extractBpmnContext(diagramId, bpmnVariables);
            Map<String, Object> apiContext = extractApiContext(specId, apiEndpoints);
            
            // 2. Создаем интегрированный контекст
            Map<String, Object> complexContext = integrateContexts(bpmnContext, apiContext);
            
            // 3. Генерируем данные с учетом интегрированного контекста
            List<Map<String, Object>> contextAwareData = generateComplexContextData(complexContext, recordCount);
            
            // 4. Добавляем полную traceable информацию
            List<Map<String, Object>> traceableData = addFullContextTraceability(contextAwareData, complexContext);
            
            // 5. Создаем результат
            Map<String, Object> result = createComplexPreservationResult(requestId, traceableData, complexContext);
            
            // 6. Кэшируем результат
            preservedDataCache.put(requestId, traceableData);
            
            logger.info("Complex context preservation completed for request: {}, generated {} records", 
                requestId, traceableData.size());
            
            return result;
            
        } catch (Exception e) {
            logger.error("Complex context preservation failed for request: {}", requestId, e);
            throw new RuntimeException("Complex context preservation failed", e);
        }
    }
    
    /**
     * Восстанавливает контекст из traceable данных
     */
    public Map<String, Object> restoreContextFromData(List<Map<String, Object>> traceableData) {
        logger.debug("Restoring context from {} traceable records", traceableData.size());
        
        try {
            Map<String, Object> restoredContext = new HashMap<>();
            restoredContext.put("restorationId", "restore_" + System.currentTimeMillis());
            restoredContext.put("restoredAt", LocalDateTime.now());
            restoredContext.put("bpmnContext", new HashMap<>());
            restoredContext.put("apiContext", new HashMap<>());
            restoredContext.put("traceabilityInfo", new ArrayList<>());
            
            // Восстанавливаем контекст из traceability информации
            for (Map<String, Object> record : traceableData) {
                @SuppressWarnings("unchecked")
                Map<String, Object> traceabilityInfo = (Map<String, Object>) record.get("traceabilityInfo");
                if (traceabilityInfo != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> recordBpmnContext = (Map<String, Object>) traceabilityInfo.get("bpmnContext");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> recordApiContext = (Map<String, Object>) traceabilityInfo.get("apiContext");
                    
                    if (recordBpmnContext != null) {
                        restoredContext.put("bpmnContext", mergeContexts((Map<String, Object>) restoredContext.get("bpmnContext"), recordBpmnContext));
                    }
                    
                    if (recordApiContext != null) {
                        restoredContext.put("apiContext", mergeContexts((Map<String, Object>) restoredContext.get("apiContext"), recordApiContext));
                    }
                }
            }
            
            return restoredContext;
            
        } catch (Exception e) {
            logger.error("Context restoration failed", e);
            throw new RuntimeException("Context restoration failed", e);
        }
    }
    
    // Private helper methods
    
    private Map<String, Object> extractBpmnContext(String diagramId, List<Map<String, Object>> processVariables) {
        Map<String, Object> bpmnContext = new HashMap<>();
        bpmnContext.put("contextId", "bpmn_ctx_" + System.currentTimeMillis());
        bpmnContext.put("diagramId", diagramId);
        bpmnContext.put("extractedAt", LocalDateTime.now());
        bpmnContext.put("processVariables", processVariables);
        bpmnContext.put("taskOutputs", extractTaskOutputs(processVariables));
        bpmnContext.put("gatewayConditions", extractGatewayConditions(processVariables));
        bpmnContext.put("eventContexts", extractEventContexts(processVariables));
        bpmnContext.put("dataFlows", extractBpmnDataFlows(processVariables));
        
        return bpmnContext;
    }
    
    private Map<String, Object> extractApiContext(String specId, List<Map<String, Object>> apiEndpoints) {
        Map<String, Object> apiContext = new HashMap<>();
        apiContext.put("contextId", "api_ctx_" + System.currentTimeMillis());
        apiContext.put("specId", specId);
        apiContext.put("extractedAt", LocalDateTime.now());
        apiContext.put("endpoints", apiEndpoints);
        apiContext.put("requestResponsePairs", extractRequestResponsePairs(apiEndpoints));
        apiContext.put("parameterRelationships", extractParameterRelationships(apiEndpoints));
        apiContext.put("authenticationContexts", extractAuthenticationContexts(apiEndpoints));
        apiContext.put("businessScenarios", extractBusinessScenarios(apiEndpoints));
        
        return apiContext;
    }
    
    private List<Map<String, Object>> generateContextAwareData(Map<String, Object> bpmnContext, int recordCount) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> processVariables = (List<Map<String, Object>>) bpmnContext.get("processVariables");
        
        for (int i = 0; i < recordCount; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", "ctx_data_" + System.currentTimeMillis() + "_" + i);
            record.put("recordNumber", i + 1);
            record.put("bpmnContextAware", true);
            
            // Добавляем process variables к записи
            if (processVariables != null && !processVariables.isEmpty()) {
                Map<String, Object> variable = processVariables.get(i % processVariables.size());
                record.putAll(variable);
            }
            
            record.put("generatedAt", LocalDateTime.now());
            data.add(record);
        }
        
        return data;
    }
    
    private List<Map<String, Object>> generateApiContextAwareData(Map<String, Object> apiContext, int recordCount) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> endpoints = (List<Map<String, Object>>) apiContext.get("endpoints");
        
        for (int i = 0; i < recordCount; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", "api_ctx_data_" + System.currentTimeMillis() + "_" + i);
            record.put("recordNumber", i + 1);
            record.put("apiContextAware", true);
            
            // Добавляем endpoint информацию к записи
            if (endpoints != null && !endpoints.isEmpty()) {
                Map<String, Object> endpoint = endpoints.get(i % endpoints.size());
                record.put("endpointPath", endpoint.get("path"));
                record.put("endpointMethod", endpoint.get("method"));
                record.put("requestSchema", endpoint.get("requestSchema"));
                record.put("responseSchema", endpoint.get("responseSchema"));
            }
            
            record.put("generatedAt", LocalDateTime.now());
            data.add(record);
        }
        
        return data;
    }
    
    private List<Map<String, Object>> generateComplexContextData(Map<String, Object> complexContext, int recordCount) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        for (int i = 0; i < recordCount; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", "complex_ctx_data_" + System.currentTimeMillis() + "_" + i);
            record.put("recordNumber", i + 1);
            record.put("complexContextAware", true);
            record.put("generatedAt", LocalDateTime.now());
            
            data.add(record);
        }
        
        return data;
    }
    
    private List<Map<String, Object>> addContextTraceability(List<Map<String, Object>> data, Map<String, Object> bpmnContext) {
        return data.stream()
            .map(record -> {
                Map<String, Object> traceableRecord = new HashMap<>(record);
                
                // Добавляем traceable информацию
                Map<String, Object> traceabilityInfo = new HashMap<>();
                traceabilityInfo.put("bpmnContext", bpmnContext);
                traceabilityInfo.put("contextType", "BPMN");
                traceabilityInfo.put("preservationTimestamp", LocalDateTime.now());
                traceabilityInfo.put("sourceDiagram", bpmnContext.get("diagramId"));
                
                traceableRecord.put("traceabilityInfo", traceabilityInfo);
                traceableRecord.put("isTraceable", true);
                
                return traceableRecord;
            })
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> addApiContextTraceability(List<Map<String, Object>> data, Map<String, Object> apiContext) {
        return data.stream()
            .map(record -> {
                Map<String, Object> traceableRecord = new HashMap<>(record);
                
                // Добавляем traceable информацию
                Map<String, Object> traceabilityInfo = new HashMap<>();
                traceabilityInfo.put("apiContext", apiContext);
                traceabilityInfo.put("contextType", "API");
                traceabilityInfo.put("preservationTimestamp", LocalDateTime.now());
                traceabilityInfo.put("sourceSpec", apiContext.get("specId"));
                
                traceableRecord.put("traceabilityInfo", traceabilityInfo);
                traceableRecord.put("isTraceable", true);
                
                return traceableRecord;
            })
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> addFullContextTraceability(List<Map<String, Object>> data, Map<String, Object> complexContext) {
        return data.stream()
            .map(record -> {
                Map<String, Object> traceableRecord = new HashMap<>(record);
                
                // Добавляем полную traceable информацию
                Map<String, Object> traceabilityInfo = new HashMap<>();
                traceabilityInfo.put("complexContext", complexContext);
                traceabilityInfo.put("contextType", "COMPLEX");
                traceabilityInfo.put("preservationTimestamp", LocalDateTime.now());
                traceabilityInfo.put("sourceDiagram", complexContext.get("diagramId"));
                traceabilityInfo.put("sourceSpec", complexContext.get("specId"));
                
                traceableRecord.put("traceabilityInfo", traceabilityInfo);
                traceableRecord.put("isTraceable", true);
                traceableRecord.put("hasFullContext", true);
                
                return traceableRecord;
            })
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> integrateContexts(Map<String, Object> bpmnContext, Map<String, Object> apiContext) {
        Map<String, Object> complexContext = new HashMap<>();
        complexContext.put("contextId", "complex_" + System.currentTimeMillis());
        complexContext.put("integrationType", "BPMN_API");
        complexContext.put("integratedAt", LocalDateTime.now());
        complexContext.put("diagramId", bpmnContext.get("diagramId"));
        complexContext.put("specId", apiContext.get("specId"));
        complexContext.put("bpmnContext", bpmnContext);
        complexContext.put("apiContext", apiContext);
        complexContext.put("contextCorrelations", calculateContextCorrelations(bpmnContext, apiContext));
        
        return complexContext;
    }
    
    // Вспомогательные методы для извлечения контекстной информации
    
    private List<Map<String, Object>> extractTaskOutputs(List<Map<String, Object>> processVariables) {
        return processVariables.stream()
            .filter(variable -> "output".equals(variable.get("type")))
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> extractGatewayConditions(List<Map<String, Object>> processVariables) {
        return processVariables.stream()
            .filter(variable -> "gateway".equals(variable.get("type")))
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> extractEventContexts(List<Map<String, Object>> processVariables) {
        return processVariables.stream()
            .filter(variable -> "event".equals(variable.get("type")))
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> extractBpmnDataFlows(List<Map<String, Object>> processVariables) {
        return processVariables.stream()
            .filter(variable -> "dataflow".equals(variable.get("type")))
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> extractRequestResponsePairs(List<Map<String, Object>> apiEndpoints) {
        return apiEndpoints.stream()
            .map(endpoint -> {
                Map<String, Object> pair = new HashMap<>();
                pair.put("endpoint", endpoint.get("path"));
                pair.put("method", endpoint.get("method"));
                pair.put("requestSchema", endpoint.get("requestSchema"));
                pair.put("responseSchema", endpoint.get("responseSchema"));
                return pair;
            })
            .collect(Collectors.toList());
    }
    
    private Map<String, List<String>> extractParameterRelationships(List<Map<String, Object>> apiEndpoints) {
        Map<String, List<String>> relationships = new HashMap<>();
        
        for (Map<String, Object> endpoint : apiEndpoints) {
            @SuppressWarnings("unchecked")
            List<String> parameters = (List<String>) endpoint.get("parameters");
            if (parameters != null) {
                relationships.put((String) endpoint.get("path"), parameters);
            }
        }
        
        return relationships;
    }
    
    private List<String> extractAuthenticationContexts(List<Map<String, Object>> apiEndpoints) {
        return apiEndpoints.stream()
            .filter(endpoint -> endpoint.containsKey("authRequired") && (Boolean) endpoint.get("authRequired"))
            .map(endpoint -> (String) endpoint.get("path"))
            .collect(Collectors.toList());
    }
    
    private List<String> extractBusinessScenarios(List<Map<String, Object>> apiEndpoints) {
        return apiEndpoints.stream()
            .filter(endpoint -> endpoint.containsKey("businessScenario"))
            .map(endpoint -> (String) endpoint.get("businessScenario"))
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> calculateContextCorrelations(Map<String, Object> bpmnContext, Map<String, Object> apiContext) {
        List<Map<String, Object>> correlations = new ArrayList<>();
        
        // Упрощенное вычисление корреляций
        for (int i = 0; i < 3; i++) {
            Map<String, Object> correlation = new HashMap<>();
            correlation.put("correlationId", "corr_" + System.currentTimeMillis() + "_" + i);
            correlation.put("bpmnElement", "task_" + i);
            correlation.put("apiEndpoint", "/api/endpoint" + i);
            correlation.put("correlationStrength", 0.8 + Math.random() * 0.2);
            correlation.put("dataFlow", "bidirectional");
            
            correlations.add(correlation);
        }
        
        return correlations;
    }
    
    private Map<String, Object> mergeContexts(Map<String, Object> context1, Map<String, Object> context2) {
        Map<String, Object> merged = new HashMap<>(context1);
        merged.putAll(context2);
        return merged;
    }
    
    // Методы создания результатов
    
    private Map<String, Object> createBpmnPreservationResult(String requestId, 
                                                           List<Map<String, Object>> data, 
                                                           Map<String, Object> bpmnContext) {
        Map<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("preservationType", "BPMN_CONTEXT");
        result.put("generatedData", data);
        result.put("preservedContext", bpmnContext);
        result.put("totalRecords", data.size());
        result.put("preservationTimestamp", LocalDateTime.now());
        result.put("success", true);
        result.put("traceabilityEnabled", true);
        
        return result;
    }
    
    private Map<String, Object> createApiPreservationResult(String requestId, 
                                                          List<Map<String, Object>> data, 
                                                          Map<String, Object> apiContext) {
        Map<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("preservationType", "API_CONTEXT");
        result.put("generatedData", data);
        result.put("preservedContext", apiContext);
        result.put("totalRecords", data.size());
        result.put("preservationTimestamp", LocalDateTime.now());
        result.put("success", true);
        result.put("traceabilityEnabled", true);
        
        return result;
    }
    
    private Map<String, Object> createComplexPreservationResult(String requestId, 
                                                              List<Map<String, Object>> data, 
                                                              Map<String, Object> complexContext) {
        Map<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("preservationType", "COMPLEX_CONTEXT");
        result.put("generatedData", data);
        result.put("preservedContext", complexContext);
        result.put("totalRecords", data.size());
        result.put("preservationTimestamp", LocalDateTime.now());
        result.put("success", true);
        result.put("traceabilityEnabled", true);
        result.put("integrationType", complexContext.get("integrationType"));
        
        return result;
    }
    
    // Getters для кэшированных результатов
    public Map<String, Object> getCachedBpmnContext(String requestId) {
        return bpmnContextCache.get(requestId);
    }
    
    public Map<String, Object> getCachedApiContext(String requestId) {
        return apiContextCache.get(requestId);
    }
    
    public List<Map<String, Object>> getCachedPreservedData(String requestId) {
        return preservedDataCache.get(requestId);
    }
    
    public void clearCache() {
        bpmnContextCache.clear();
        apiContextCache.clear();
        preservedDataCache.clear();
        logger.info("Context preserving generator cache cleared");
    }
}