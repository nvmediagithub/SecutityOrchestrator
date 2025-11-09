package org.example.application.service.context;

import org.example.domain.dto.context.*;
import org.example.domain.entities.ContextMetrics;
import org.example.domain.model.testdata.ContextAwareData;
import org.example.domain.model.testdata.DataFlowChain;
import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.application.service.testdata.IntelligentDataGenerator;
import org.example.application.service.testdata.DependencyAwareGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ContextAnalyzer - основной анализатор контекста для генерации тестовых данных
 * Интегрируется с BpmnAnalysisService, OpenApiAnalysisService, IntelligentDataGenerator
 */
@Service
public class ContextAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(ContextAnalyzer.class);
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private OpenApiAnalysisService openApiAnalysisService;
    
    @Autowired
    private IntelligentDataGenerator intelligentDataGenerator;
    
    @Autowired
    private DependencyAwareGenerator dependencyAwareGenerator;
    
    @Autowired
    private Executor contextAnalysisExecutor;
    
    // Кэш для контекстной информации
    private final Map<String, ContextAwareData> contextCache = new ConcurrentHashMap<>();
    private final Map<String, UnifiedContext> unifiedContextCache = new ConcurrentHashMap<>();
    
    /**
     * Выполняет комплексный анализ контекста
     */
    @Async
    public CompletableFuture<ContextAnalysisResult> analyzeContext(ContextAnalysisRequest request) {
        logger.info("Starting context analysis for request: {}", request.getRequestId());
        
        try {
            // 1. Анализ BPMN процессов
            CompletableFuture<BpmnContext> bpmnContextFuture = analyzeBpmnContext(request);
            
            // 2. Анализ OpenAPI спецификаций
            CompletableFuture<ApiContext> apiContextFuture = analyzeApiContext(request);
            
            // 3. Кросс-системный анализ
            CompletableFuture<CrossSystemContext> crossSystemContextFuture = 
                analyzeCrossSystemContext(request, bpmnContextFuture, apiContextFuture);
            
            // 4. Объединение контекстов
            CompletableFuture<UnifiedContext> unifiedContextFuture = 
                createUnifiedContext(bpmnContextFuture, apiContextFuture, crossSystemContextFuture);
            
            return unifiedContextFuture.thenApply(unifiedContext -> {
                try {
                    // 5. Создание контекстно-осведомленных данных
                    List<ContextAwareData> contextAwareData = 
                        createContextAwareData(unifiedContext, request);
                    
                    // 6. Создание финального результата
                    ContextAnalysisResult result = createContextAnalysisResult(
                        request, unifiedContext, contextAwareData
                    );
                    
                    // 7. Кэширование результатов
                    cacheAnalysisResults(request.getRequestId(), unifiedContext, contextAwareData);
                    
                    logger.info("Context analysis completed for request: {}, unified contexts: {}", 
                        request.getRequestId(), unifiedContext.getContextCount());
                    
                    return result;
                    
                } catch (Exception e) {
                    logger.error("Error completing context analysis for request: {}", request.getRequestId(), e);
                    throw new RuntimeException("Context analysis completion failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Context analysis failed for request: {}", request.getRequestId(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Анализ контекста из BPMN процессов
     */
    @Async
    public CompletableFuture<BpmnContext> analyzeBpmnContext(ContextAnalysisRequest request) {
        logger.info("Analyzing BPMN context for request: {}", request.getRequestId());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                BpmnContext bpmnContext = new BpmnContext();
                bpmnContext.setAnalysisId("bpmn_context_" + System.currentTimeMillis());
                bpmnContext.setAnalyzedAt(LocalDateTime.now());
                
                // Обрабатываем BPMN диаграммы
                for (String diagramId : request.getBpmnDiagramIds()) {
                    BpmnProcessContext processContext = analyzeSingleBpmnProcess(diagramId, request);
                    bpmnContext.getProcessContexts().add(processContext);
                }
                
                // Извлекаем контекстные зависимости
                bpmnContext.setContextualDependencies(extractBpmnContextualDependencies(bpmnContext.getProcessContexts()));
                
                // Анализируем data flow
                bpmnContext.setDataFlowMapping(analyzeBpmnDataFlow(bpmnContext.getProcessContexts()));
                
                logger.info("BPMN context analysis completed: {} processes, {} dependencies", 
                    bpmnContext.getProcessContexts().size(), 
                    bpmnContext.getContextualDependencies().size());
                
                return bpmnContext;
                
            } catch (Exception e) {
                logger.error("BPMN context analysis failed for request: {}", request.getRequestId(), e);
                throw new RuntimeException("BPMN context analysis failed", e);
            }
        }, contextAnalysisExecutor);
    }
    
    /**
     * Анализ контекста из OpenAPI спецификаций
     */
    @Async
    public CompletableFuture<ApiContext> analyzeApiContext(ContextAnalysisRequest request) {
        logger.info("Analyzing API context for request: {}", request.getRequestId());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                ApiContext apiContext = new ApiContext();
                apiContext.setAnalysisId("api_context_" + System.currentTimeMillis());
                apiContext.setAnalyzedAt(LocalDateTime.now());
                
                // Обрабатываем API спецификации
                for (String specId : request.getApiSpecIds()) {
                    ApiEndpointContext endpointContext = analyzeSingleApiSpec(specId, request);
                    apiContext.getEndpointContexts().add(endpointContext);
                }
                
                // Извлекаем схемы данных
                apiContext.setDataSchemas(extractApiDataSchemas(apiContext.getEndpointContexts()));
                
                // Анализируем параметры
                apiContext.setParameterAnalysis(analyzeApiParameters(apiContext.getEndpointContexts()));
                
                // Извлекаем business context
                apiContext.setBusinessContext(extractApiBusinessContext(apiContext.getEndpointContexts()));
                
                logger.info("API context analysis completed: {} endpoints, {} schemas", 
                    apiContext.getEndpointContexts().size(), 
                    apiContext.getDataSchemas().size());
                
                return apiContext;
                
            } catch (Exception e) {
                logger.error("API context analysis failed for request: {}", request.getRequestId(), e);
                throw new RuntimeException("API context analysis failed", e);
            }
        }, contextAnalysisExecutor);
    }
    
    /**
     * Кросс-системный анализ контекста
     */
    @Async
    public CompletableFuture<CrossSystemContext> analyzeCrossSystemContext(ContextAnalysisRequest request,
                                                                          CompletableFuture<BpmnContext> bpmnContextFuture,
                                                                          CompletableFuture<ApiContext> apiContextFuture) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BpmnContext bpmnContext = bpmnContextFuture.join();
                ApiContext apiContext = apiContextFuture.join();
                
                CrossSystemContext crossSystemContext = new CrossSystemContext();
                crossSystemContext.setAnalysisId("cross_system_" + System.currentTimeMillis());
                crossSystemContext.setAnalyzedAt(LocalDateTime.now());
                
                // Анализ связей между BPMN и API
                crossSystemContext.setBpmnApiConnections(analyzeBpmnApiConnections(bpmnContext, apiContext));
                
                // End-to-end сценарии
                crossSystemContext.setEndToEndScenarios(analyzeEndToEndScenarios(bpmnContext, apiContext));
                
                // Пользовательские journey
                crossSystemContext.setUserJourneys(analyzeUserJourneys(bpmnContext, apiContext));
                
                // Data lineage mapping
                crossSystemContext.setDataLineage(analyzeDataLineage(bpmnContext, apiContext));
                
                logger.info("Cross-system context analysis completed: {} connections, {} journeys", 
                    crossSystemContext.getBpmnApiConnections().size(), 
                    crossSystemContext.getUserJourneys().size());
                
                return crossSystemContext;
                
            } catch (Exception e) {
                logger.error("Cross-system context analysis failed for request: {}", request.getRequestId(), e);
                throw new RuntimeException("Cross-system context analysis failed", e);
            }
        }, contextAnalysisExecutor);
    }
    
    /**
     * Создает объединенный контекст
     */
    private CompletableFuture<UnifiedContext> createUnifiedContext(CompletableFuture<BpmnContext> bpmnContextFuture,
                                                                  CompletableFuture<ApiContext> apiContextFuture,
                                                                  CompletableFuture<CrossSystemContext> crossSystemContextFuture) {
        return CompletableFuture.allOf(bpmnContextFuture, apiContextFuture, crossSystemContextFuture)
            .thenApply(v -> {
                try {
                    BpmnContext bpmnContext = bpmnContextFuture.join();
                    ApiContext apiContext = apiContextFuture.join();
                    CrossSystemContext crossSystemContext = crossSystemContextFuture.join();
                    
                    UnifiedContext unifiedContext = new UnifiedContext();
                    unifiedContext.setContextId("unified_" + System.currentTimeMillis());
                    unifiedContext.setCreatedAt(LocalDateTime.now());
                    unifiedContext.setBpmnContext(bpmnContext);
                    unifiedContext.setApiContext(apiContext);
                    unifiedContext.setCrossSystemContext(crossSystemContext);
                    
                    // Объединяем все элементы контекста
                    unifiedContext.setContextElements(mergeContextElements(bpmnContext, apiContext, crossSystemContext));
                    
                    // Вычисляем метрики контекста
                    unifiedContext.setContextMetrics(calculateContextMetrics(unifiedContext));
                    
                    return unifiedContext;
                    
                } catch (Exception e) {
                    logger.error("Failed to create unified context", e);
                    throw new RuntimeException("Unified context creation failed", e);
                }
            });
    }
    
    // Private helper methods
    
    private BpmnProcessContext analyzeSingleBpmnProcess(String diagramId, ContextAnalysisRequest request) {
        // Placeholder for single BPMN process analysis
        BpmnProcessContext context = new BpmnProcessContext();
        context.setDiagramId(diagramId);
        context.setProcessVariables(new ArrayList<>());
        context.setTaskDependencies(new ArrayList<>());
        context.setWorkflowLogic(new HashMap<>());
        return context;
    }
    
    private List<ContextDependency> extractBpmnContextualDependencies(List<BpmnProcessContext> processContexts) {
        // Extract contextual dependencies from BPMN processes
        return new ArrayList<>();
    }
    
    private Map<String, DataFlowMapping> analyzeBpmnDataFlow(List<BpmnProcessContext> processContexts) {
        // Analyze data flow in BPMN processes
        return new HashMap<>();
    }
    
    private ApiEndpointContext analyzeSingleApiSpec(String specId, ContextAnalysisRequest request) {
        // Placeholder for single API spec analysis
        ApiEndpointContext context = new ApiEndpointContext();
        context.setSpecId(specId);
        context.setEndpoints(new ArrayList<>());
        context.setRequestResponsePairs(new ArrayList<>());
        return context;
    }
    
    private List<ApiDataSchema> extractApiDataSchemas(List<ApiEndpointContext> endpointContexts) {
        // Extract data schemas from API endpoints
        return new ArrayList<>();
    }
    
    private ParameterAnalysis analyzeApiParameters(List<ApiEndpointContext> endpointContexts) {
        // Analyze API parameters
        return new ParameterAnalysis();
    }
    
    private BusinessContext extractApiBusinessContext(List<ApiEndpointContext> endpointContexts) {
        // Extract business context from API documentation
        return new BusinessContext();
    }
    
    private List<BpmnApiConnection> analyzeBpmnApiConnections(BpmnContext bpmnContext, ApiContext apiContext) {
        // Analyze connections between BPMN processes and API endpoints
        return new ArrayList<>();
    }
    
    private List<EndToEndScenario> analyzeEndToEndScenarios(BpmnContext bpmnContext, ApiContext apiContext) {
        // Analyze end-to-end scenarios
        return new ArrayList<>();
    }
    
    private List<UserJourney> analyzeUserJourneys(BpmnContext bpmnContext, ApiContext apiContext) {
        // Analyze user journeys
        return new ArrayList<>();
    }
    
    private List<DataLineageMapping> analyzeDataLineage(BpmnContext bpmnContext, ApiContext apiContext) {
        // Analyze data lineage mapping
        return new ArrayList<>();
    }
    
    private List<ContextElement> mergeContextElements(BpmnContext bpmnContext, ApiContext apiContext, CrossSystemContext crossSystemContext) {
        // Merge all context elements
        return new ArrayList<>();
    }
    
    private ContextMetrics calculateContextMetrics(UnifiedContext unifiedContext) {
        // Calculate context metrics
        return new ContextMetrics();
    }
    
    private List<ContextAwareData> createContextAwareData(UnifiedContext unifiedContext, ContextAnalysisRequest request) {
        // Create context-aware data based on unified context
        return new ArrayList<>();
    }
    
    private ContextAnalysisResult createContextAnalysisResult(ContextAnalysisRequest request, 
                                                            UnifiedContext unifiedContext,
                                                            List<ContextAwareData> contextAwareData) {
        ContextAnalysisResult result = new ContextAnalysisResult();
        result.setRequestId(request.getRequestId());
        result.setUnifiedContext(unifiedContext);
        result.setContextAwareData(contextAwareData);
        result.setAnalysisCompletedAt(LocalDateTime.now());
        result.setSuccess(true);
        return result;
    }
    
    private void cacheAnalysisResults(String requestId, UnifiedContext unifiedContext, List<ContextAwareData> contextAwareData) {
        unifiedContextCache.put(requestId, unifiedContext);
        for (ContextAwareData data : contextAwareData) {
            contextCache.put(data.getDataId(), data);
        }
    }
    
    // Getters for cached results
    public UnifiedContext getCachedUnifiedContext(String requestId) {
        return unifiedContextCache.get(requestId);
    }
    
    public List<ContextAwareData> getCachedContextAwareData(String requestId) {
        return contextCache.values().stream()
            .filter(data -> data.getSourceDataId().equals(requestId))
            .collect(Collectors.toList());
    }
    
    public void clearCache() {
        contextCache.clear();
        unifiedContextCache.clear();
        logger.info("Context analyzer cache cleared");
    }
}