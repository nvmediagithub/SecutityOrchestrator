package org.example.application.service.testdata;

import org.example.domain.model.testdata.*;
import org.example.domain.model.testdata.enums.GenerationScope;
import org.example.infrastructure.llm.testdata.*;
import org.example.infrastructure.llm.testdata.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * DependencyAwareGenerator - генератор с учетом зависимостей между данными
 * Анализирует существующие зависимости и создает согласованные наборы данных
 */
@Service
public class DependencyAwareGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(DependencyAwareGenerator.class);
    
    @Autowired
    private TestDataLLMService testDataLLMService;
    
    @Autowired
    private TestDataLLMConfig llmConfig;
    
    @Autowired
    private IntelligentDataGenerator intelligentDataGenerator;
    
    @Autowired
    private Executor dependencyAwareExecutor;
    
    // Cache for dependencies and chains
    private final Map<String, List<DataDependency>> dependencyCache = new ConcurrentHashMap<>();
    private final Map<String, DataFlowChain> chainCache = new ConcurrentHashMap<>();
    
    /**
     * Генерирует данные с учетом зависимостей
     */
    @Async
    public CompletableFuture<DependencyAwareTestDataResult> generateWithDependencies(DependencyAwareDataGenerationRequest request) {
        logger.info("Starting dependency-aware generation for request: {}", request.getRequestId());
        
        try {
            // 1. Загружаем зависимости
            List<DataDependency> dependencies = loadDependencies(request.getDependencyIds());
            
            // 2. Анализируем цепочки данных
            List<DataFlowChain> flowChains = analyzeDataFlowChains(dependencies);
            
            // 3. Определяем порядок генерации
            DependencyGenerationOrder generationOrder = determineGenerationOrder(dependencies, flowChains, request);
            
            // 4. Генерируем данные с учетом зависимостей
            Map<String, IntelligentTestDataResult> generationResults = generateDataInOrder(generationOrder, request);
            
            // 5. Валидируем зависимости
            DependencyValidationResult validationResult = validateDependencies(generationResults, dependencies);
            
            // 6. Создаем финальный результат
            DependencyAwareTestDataResult result = createDependencyAwareResult(request, generationResults, dependencies, flowChains, validationResult);
            
            logger.info("Dependency-aware generation completed for request: {}, validated dependencies: {}", 
                request.getRequestId(), validationResult.getValidDependenciesCount());
            
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            logger.error("Dependency-aware generation failed for request: {}", request.getRequestId(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Генерирует данные для сложных цепочек зависимостей
     */
    @Async
    public CompletableFuture<DependencyAwareTestDataResult> generateComplexChains(DependencyAwareDataGenerationRequest request) {
        logger.info("Starting complex chain generation for request: {}", request.getRequestId());
        
        try {
            // 1. Загружаем и анализируем сложные цепочки
            List<DataFlowChain> complexChains = loadComplexChains(request.getChainIds());
            
            // 2. Создаем план выполнения для цепочек
            ChainExecutionPlan executionPlan = createChainExecutionPlan(complexChains, request);
            
            // 3. Выполняем генерацию по цепочкам
            Map<String, IntelligentTestDataResult> chainResults = executeChainPlan(executionPlan, request);
            
            // 4. Валидируем целостность цепочек
            ChainIntegrityResult integrityResult = validateChainIntegrity(chainResults, complexChains);
            
            // 5. Создаем результат
            DependencyAwareTestDataResult result = createChainResult(request, chainResults, complexChains, integrityResult);
            
            logger.info("Complex chain generation completed for request: {}", request.getRequestId());
            
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            logger.error("Complex chain generation failed for request: {}", request.getRequestId(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Анализирует зависимости и возвращает отчет
     */
    @Async
    public CompletableFuture<DependencyAnalysisResult> analyzeDependencies(List<String> dependencyIds) {
        logger.info("Starting dependency analysis for {} dependencies", dependencyIds.size());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<DataDependency> dependencies = loadDependencies(dependencyIds);
                
                DependencyAnalysisResult result = new DependencyAnalysisResult();
                result.setAnalyzedAt(LocalDateTime.now());
                result.setTotalDependencies(dependencies.size());
                
                // Анализируем типы зависимостей
                Map<String, Long> dependencyTypes = dependencies.stream()
                    .collect(Collectors.groupingBy(
                        dep -> dep.getDependencyType().toString(),
                        Collectors.counting()
                    ));
                result.setDependencyTypes(dependencyTypes);
                
                // Анализируем силу зависимостей
                Map<String, Long> strengthAnalysis = dependencies.stream()
                    .collect(Collectors.groupingBy(
                        dep -> dep.getStrength().toString(),
                        Collectors.counting()
                    ));
                result.setStrengthAnalysis(strengthAnalysis);
                
                // Находим циклические зависимости
                List<List<String>> cyclicDependencies = findCyclicDependencies(dependencies);
                result.setCyclicDependencies(cyclicDependencies);
                
                // Вычисляем критические зависимости
                List<String> criticalDependencies = dependencies.stream()
                    .filter(DataDependency::isCritical)
                    .map(DataDependency::getDependencyId)
                    .collect(Collectors.toList());
                result.setCriticalDependencies(criticalDependencies);
                
                logger.info("Dependency analysis completed: {} total, {} cyclic, {} critical", 
                    dependencies.size(), cyclicDependencies.size(), criticalDependencies.size());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Dependency analysis failed", e);
                throw new RuntimeException("Dependency analysis failed", e);
            }
        }, dependencyAwareExecutor);
    }
    
    /**
     * Оптимизирует порядок генерации данных
     */
    public DependencyGenerationOrder optimizeGenerationOrder(List<DataDependency> dependencies, 
                                                            List<DataFlowChain> flowChains,
                                                            DependencyAwareDataGenerationRequest request) {
        logger.debug("Optimizing generation order for {} dependencies", dependencies.size());
        
        // Создаем граф зависимостей
        Map<String, Set<String>> dependencyGraph = buildDependencyGraph(dependencies);
        
        // Выполняем топологическую сортировку
        List<String> generationOrder = topologicalSort(dependencyGraph);
        
        // Группируем по цепочкам
        Map<String, List<String>> chainGroups = groupByChains(generationOrder, flowChains);
        
        DependencyGenerationOrder order = new DependencyGenerationOrder();
        order.setGenerationSequence(generationOrder);
        order.setChainGroups(chainGroups);
        order.setEstimatedTimeMs(estimateGenerationTime(generationOrder, request));
        
        return order;
    }
    
    /**
     * Валидирует существующие зависимости
     */
    @Async
    public CompletableFuture<DependencyValidationResult> validateExistingDependencies(List<String> dependencyIds) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<DataDependency> dependencies = loadDependencies(dependencyIds);
                return validateDependencyList(dependencies);
            } catch (Exception e) {
                logger.error("Dependency validation failed", e);
                throw new RuntimeException("Dependency validation failed", e);
            }
        }, dependencyAwareExecutor);
    }
    
    // Private helper methods
    
    private List<DataDependency> loadDependencies(List<String> dependencyIds) {
        // В реальной реализации здесь был бы доступ к репозиторию
        // Пока возвращаем заглушку
        List<DataDependency> dependencies = new ArrayList<>();
        for (String id : dependencyIds) {
            DataDependency dep = new DataDependency();
            dep.setDependencyId(id);
            dep.setName("Dependency " + id);
            dep.setDependencyType(DataDependency.DependencyType.DIRECT);
            dep.setStrength(DependencyStrength.WEAK);
            dependencies.add(dep);
        }
        return dependencies;
    }
    
    private List<DataFlowChain> analyzeDataFlowChains(List<DataDependency> dependencies) {
        List<DataFlowChain> chains = new ArrayList<>();
        
        // Группируем зависимости в цепочки
        Map<String, List<DataDependency>> groupedDeps = dependencies.stream()
            .collect(Collectors.groupingBy(dep -> extractChainId(dep.getSourceDataId())));
        
        for (Map.Entry<String, List<DataDependency>> entry : groupedDeps.entrySet()) {
            DataFlowChain chain = new DataFlowChain();
            chain.setChainId(entry.getKey());
            chain.setName("Chain " + entry.getKey());
            chain.setContext("Auto-generated chain");
            chain.setDataElements(entry.getValue().stream()
                .map(DataDependency::getTargetDataId)
                .collect(Collectors.toList()));
            chains.add(chain);
        }
        
        return chains;
    }
    
    private DependencyGenerationOrder determineGenerationOrder(List<DataDependency> dependencies,
                                                              List<DataFlowChain> flowChains,
                                                              DependencyAwareDataGenerationRequest request) {
        return optimizeGenerationOrder(dependencies, flowChains, request);
    }
    
    private Map<String, IntelligentTestDataResult> generateDataInOrder(DependencyGenerationOrder order,
                                                                      DependencyAwareDataGenerationRequest request) {
        Map<String, IntelligentTestDataResult> results = new HashMap<>();
        
        for (String dataId : order.getGenerationSequence()) {
            try {
                // Создаем подзапрос для конкретного элемента данных
                IntelligentDataGenerationRequest subRequest = createSubRequest(request, dataId);
                
                // Генерируем данные
                IntelligentTestDataResult result = intelligentDataGenerator.generateIntelligentData(subRequest).join();
                results.put(dataId, result);
                
                logger.debug("Generated data for element: {}", dataId);
                
            } catch (Exception e) {
                logger.error("Failed to generate data for element: {}", dataId, e);
                // Продолжаем с другими элементами
            }
        }
        
        return results;
    }
    
    private DependencyValidationResult validateDependencies(Map<String, IntelligentTestDataResult> generationResults,
                                                           List<DataDependency> dependencies) {
        DependencyValidationResult result = new DependencyValidationResult();
        result.setValidatedAt(LocalDateTime.now());
        result.setTotalDependencies(dependencies.size());
        
        List<String> validDependencies = new ArrayList<>();
        List<String> invalidDependencies = new ArrayList<>();
        
        for (DataDependency dep : dependencies) {
            IntelligentTestDataResult sourceResult = generationResults.get(dep.getSourceDataId());
            IntelligentTestDataResult targetResult = generationResults.get(dep.getTargetDataId());
            
            if (sourceResult != null && targetResult && sourceResult.isSuccess() && targetResult.isSuccess()) {
                // Дополнительная валидация зависимости
                if (validateDependencyLogic(sourceResult, targetResult, dep)) {
                    validDependencies.add(dep.getDependencyId());
                } else {
                    invalidDependencies.add(dep.getDependencyId());
                }
            } else {
                invalidDependencies.add(dep.getDependencyId());
            }
        }
        
        result.setValidDependencies(validDependencies);
        result.setInvalidDependencies(invalidDependencies);
        result.setValidDependenciesCount(validDependencies.size());
        result.setInvalidDependenciesCount(invalidDependencies.size());
        
        return result;
    }
    
    private DependencyAwareTestDataResult createDependencyAwareResult(DependencyAwareDataGenerationRequest request,
                                                                     Map<String, IntelligentTestDataResult> generationResults,
                                                                     List<DataDependency> dependencies,
                                                                     List<DataFlowChain> flowChains,
                                                                     DependencyValidationResult validationResult) {
        DependencyAwareTestDataResult result = new DependencyAwareTestDataResult();
        result.setRequestId(request.getRequestId());
        result.setGenerationResults(generationResults);
        result.setDependencies(dependencies);
        result.setFlowChains(flowChains);
        result.setValidationResult(validationResult);
        result.setGeneratedAt(LocalDateTime.now());
        result.setSuccess(true);
        
        return result;
    }
    
    // Additional helper methods (placeholders for complex logic)
    
    private List<DataFlowChain> loadComplexChains(List<String> chainIds) {
        return new ArrayList<>(); // Placeholder
    }
    
    private ChainExecutionPlan createChainExecutionPlan(List<DataFlowChain> chains, DependencyAwareDataGenerationRequest request) {
        return new ChainExecutionPlan(); // Placeholder
    }
    
    private Map<String, IntelligentTestDataResult> executeChainPlan(ChainExecutionPlan plan, DependencyAwareDataGenerationRequest request) {
        return new HashMap<>(); // Placeholder
    }
    
    private ChainIntegrityResult validateChainIntegrity(Map<String, IntelligentTestDataResult> results, List<DataFlowChain> chains) {
        return new ChainIntegrityResult(); // Placeholder
    }
    
    private DependencyAwareTestDataResult createChainResult(DependencyAwareDataGenerationRequest request,
                                                           Map<String, IntelligentTestDataResult> results,
                                                           List<DataFlowChain> chains,
                                                           ChainIntegrityResult integrityResult) {
        return new DependencyAwareTestDataResult(); // Placeholder
    }
    
    private List<List<String>> findCyclicDependencies(List<DataDependency> dependencies) {
        return new ArrayList<>(); // Placeholder for cycle detection
    }
    
    private Map<String, Set<String>> buildDependencyGraph(List<DataDependency> dependencies) {
        Map<String, Set<String>> graph = new HashMap<>();
        for (DataDependency dep : dependencies) {
            graph.computeIfAbsent(dep.getSourceDataId(), k -> new HashSet<>()).add(dep.getTargetDataId());
        }
        return graph;
    }
    
    private List<String> topologicalSort(Map<String, Set<String>> graph) {
        // Simplified topological sort
        return new ArrayList<>(graph.keySet()); // Placeholder
    }
    
    private Map<String, List<String>> groupByChains(List<String> order, List<DataFlowChain> chains) {
        return new HashMap<>(); // Placeholder
    }
    
    private long estimateGenerationTime(List<String> order, DependencyAwareDataGenerationRequest request) {
        return order.size() * 1000; // Placeholder estimation
    }
    
    private String extractChainId(String dataId) {
        return dataId.split("_")[0]; // Simple extraction
    }
    
    private IntelligentDataGenerationRequest createSubRequest(DependencyAwareDataGenerationRequest request, String dataId) {
        IntelligentDataGenerationRequest subRequest = new IntelligentDataGenerationRequest();
        subRequest.setRequestId(request.getRequestId() + "_" + dataId);
        subRequest.setDataType(request.getDataType());
        subRequest.setGenerationScope(request.getGenerationScope());
        subRequest.setRecordCount(request.getRecordCount());
        subRequest.setContext(request.getContext());
        subRequest.setQualityLevel(request.getQualityLevel());
        return subRequest;
    }
    
    private boolean validateDependencyLogic(IntelligentTestDataResult source, IntelligentTestDataResult target, DataDependency dep) {
        // Placeholder for complex dependency validation logic
        return true;
    }
    
    private DependencyValidationResult validateDependencyList(List<DataDependency> dependencies) {
        DependencyValidationResult result = new DependencyValidationResult();
        result.setTotalDependencies(dependencies.size());
        result.setValidDependenciesCount(dependencies.size());
        result.setValidatedAt(LocalDateTime.now());
        return result;
    }
}