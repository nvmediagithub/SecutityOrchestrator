package org.example.infrastructure.services.bpmn;

import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.BpmnAnalysis;
import org.example.domain.entities.ProcessIssue;
import org.example.domain.entities.ProcessSecurityCheck;
import org.example.domain.valueobjects.BpmnProcessId;
import org.example.domain.valueobjects.ProcessStatus;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.infrastructure.services.ArtifactService;
import org.example.domain.valueobjects.SeverityLevel;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Основной сервис анализа BPMN диаграмм с использованием LLM
 * Координирует асинхронный анализ структуры, безопасности, производительности и комплексный анализ
 */
@Service
public class BpmnAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(BpmnAnalysisService.class);
    
    @Autowired
    private BpmnLLMPromptBuilder promptBuilder;
    
    @Autowired
    private BpmnIssueClassifier issueClassifier;
    
    @Autowired
    private BpmnLLMAnalyzer llmAnalyzer;
    
    @Autowired
    private BpmnParser bpmnParser;
    
    @Autowired
    private ProcessFlowAnalyzer flowAnalyzer;
    
    @Autowired
    private StructureAnalyzer structureAnalyzer;
    
    @Autowired
    private SecurityProcessAnalyzer securityAnalyzer;
    
    @Autowired
    private PerformanceProcessAnalyzer performanceAnalyzer;
    
    @Autowired
    private BpmnApiMapper apiMapper;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private ArtifactService artifactService;
    
    @Autowired
    private Executor bpmnAnalysisExecutor;
    
    @Autowired
    private ScheduledExecutorService scheduledExecutor;
    
    // Кэш для результатов анализа
    private final Map<String, BpmnAnalysisResult> resultCache = new ConcurrentHashMap<>();
    private final Map<String, BpmnAnalysisStatus> activeAnalyses = new ConcurrentHashMap<>();
    private final Queue<String> analysisQueue = new LinkedList<>();
    
    // Конфигурация анализа
    private static final int MAX_CONCURRENT_ANALYSES = 3;
    private static final long CACHE_TTL_HOURS = 24;
    private static final long ANALYSIS_TIMEOUT_MINUTES = 45;
    
    /**
     * Выполняет полный анализ BPMN диаграммы
     */
    @Async
    public CompletableFuture<BpmnAnalysisResult> analyzeBpmnDiagram(String diagramId, BpmnDiagram diagram) {
        String analysisId = generateAnalysisId(diagramId);
        logger.info("Starting comprehensive BPMN analysis for diagram: {}, analysisId: {}", diagramId, analysisId);
        
        try {
            activeAnalyses.put(analysisId, new BpmnAnalysisStatus(analysisId, "STARTED", LocalDateTime.now()));
            
            // Проверяем кэш
            String cacheKey = generateCacheKey(diagramId, "comprehensive");
            if (resultCache.containsKey(cacheKey)) {
                BpmnAnalysisResult cached = resultCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached analysis result for diagram: {}", diagramId);
                    return CompletableFuture.completedFuture(cached);
                }
            }
            
            // Подготавливаем задачи анализа
            List<CompletableFuture<PartialBpmnAnalysisResult>> analysisTasks = new ArrayList<>();
            
            // 1. Анализ структуры
            analysisTasks.add(analyzeStructure(diagramId, diagram, analysisId));
            
            // 2. Анализ безопасности
            analysisTasks.add(analyzeSecurity(diagramId, diagram, analysisId));
            
            // 3. Анализ производительности
            analysisTasks.add(analyzePerformance(diagramId, diagram, analysisId));
            
            // 4. Комплексный анализ
            analysisTasks.add(analyzeComprehensive(diagramId, diagram, analysisId));
            
            // Ожидаем завершения всех задач
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                analysisTasks.toArray(new CompletableFuture[0])
            );
            
            return allTasks.thenApply(v -> {
                try {
                    updateAnalysisStatus(analysisId, "AGGREGATING");
                    
                    // Агрегируем результаты
                    PartialBpmnAnalysisResult structureResult = analysisTasks.get(0).get();
                    PartialBpmnAnalysisResult securityResult = analysisTasks.get(1).get();
                    PartialBpmnAnalysisResult performanceResult = analysisTasks.get(2).get();
                    PartialBpmnAnalysisResult comprehensiveResult = analysisTasks.get(3).get();
                    
                    BpmnAnalysisResult finalResult = aggregateResults(
                        diagramId, diagram, analysisId,
                        structureResult, securityResult, 
                        performanceResult, comprehensiveResult
                    );
                    
                    // Кэшируем результат
                    cacheResult(cacheKey, finalResult);
                    
                    // Обновляем статус
                    activeAnalyses.put(analysisId, 
                        new BpmnAnalysisStatus(analysisId, "COMPLETED", LocalDateTime.now()));
                    
                    logger.info("BPMN analysis completed for diagram: {}, analysisId: {}", diagramId, analysisId);
                    return finalResult;
                    
                } catch (Exception e) {
                    logger.error("Error aggregating BPMN analysis results", e);
                    activeAnalyses.put(analysisId, 
                        new BpmnAnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
                    throw new RuntimeException("BPMN analysis aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting BPMN analysis for diagram: {}", diagramId, e);
            activeAnalyses.put(analysisId, 
                new BpmnAnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Анализ структуры BPMN диаграммы
     */
    @Async
    public CompletableFuture<PartialBpmnAnalysisResult> analyzeStructure(String diagramId, BpmnDiagram diagram, String analysisId) {
        logger.info("Starting structure analysis for diagram: {}, analysisId: {}", diagramId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "STRUCTURE_ANALYSIS");
                
                // Парсим BPMN диаграмму
                BpmnParsedData parsedData = bpmnParser.parseBpmnContent(diagram.getBpmnContent());
                
                // Анализируем структуру
                StructureAnalysisResult structureResult = structureAnalyzer.analyzeStructure(parsedData);
                
                // Генерируем промпт
                String prompt = promptBuilder.buildStructureAnalysisPrompt(parsedData, structureResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "structure");
                
                // Парсим результат
                List<BpmnIssueClassifier.RawBpmnIssue> issues = llmAnalyzer.parseStructureAnalysis(llmResponse);
                
                // Классифицируем проблемы
                BpmnIssueClassifier.ClassifiedBpmnIssues classified = issueClassifier.classifyBpmnIssues(issues);
                
                PartialBpmnAnalysisResult result = new PartialBpmnAnalysisResult();
                result.setAnalysisType("STRUCTURE");
                result.setIssues(classified);
                result.setStructureData(structureResult);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Structure analysis completed for diagram: {}, found {} issues", 
                    diagramId, classified.getTotalIssues());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Structure analysis failed for diagram: {}", diagramId, e);
                throw new RuntimeException("Structure analysis failed", e);
            }
        }, bpmnAnalysisExecutor);
    }
    
    /**
     * Анализ безопасности BPMN процесса
     */
    @Async
    public CompletableFuture<PartialBpmnAnalysisResult> analyzeSecurity(String diagramId, BpmnDiagram diagram, String analysisId) {
        logger.info("Starting security analysis for diagram: {}, analysisId: {}", diagramId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "SECURITY_ANALYSIS");
                
                // Парсим BPMN диаграмму
                BpmnParsedData parsedData = bpmnParser.parseBpmnContent(diagram.getBpmnContent());
                
                // Анализируем безопасность процесса
                SecurityAnalysisResult securityResult = securityAnalyzer.analyzeSecurity(parsedData);
                
                // Генерируем промпт
                String prompt = promptBuilder.buildSecurityAnalysisPrompt(parsedData, securityResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "security");
                
                // Парсим результат
                List<BpmnIssueClassifier.RawBpmnIssue> issues = llmAnalyzer.parseSecurityAnalysis(llmResponse);
                
                // Классифицируем проблемы
                BpmnIssueClassifier.ClassifiedBpmnIssues classified = issueClassifier.classifyBpmnIssues(issues);
                
                PartialBpmnAnalysisResult result = new PartialBpmnAnalysisResult();
                result.setAnalysisType("SECURITY");
                result.setIssues(classified);
                result.setSecurityData(securityResult);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Security analysis completed for diagram: {}, found {} issues", 
                    diagramId, classified.getTotalIssues());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Security analysis failed for diagram: {}", diagramId, e);
                throw new RuntimeException("Security analysis failed", e);
            }
        }, bpmnAnalysisExecutor);
    }
    
    /**
     * Анализ производительности BPMN процесса
     */
    @Async
    public CompletableFuture<PartialBpmnAnalysisResult> analyzePerformance(String diagramId, BpmnDiagram diagram, String analysisId) {
        logger.info("Starting performance analysis for diagram: {}, analysisId: {}", diagramId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "PERFORMANCE_ANALYSIS");
                
                // Парсим BPMN диаграмму
                BpmnParsedData parsedData = bpmnParser.parseBpmnContent(diagram.getBpmnContent());
                
                // Анализируем потоки процессов
                FlowAnalysisResult flowResult = flowAnalyzer.analyzeFlows(parsedData);
                
                // Анализируем производительность
                PerformanceAnalysisResult performanceResult = performanceAnalyzer.analyzePerformance(parsedData, flowResult);
                
                // Генерируем промпт
                String prompt = promptBuilder.buildPerformanceAnalysisPrompt(parsedData, performanceResult);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "performance");
                
                // Парсим результат
                List<BpmnIssueClassifier.RawBpmnIssue> issues = llmAnalyzer.parsePerformanceAnalysis(llmResponse);
                
                // Классифицируем проблемы
                BpmnIssueClassifier.ClassifiedBpmnIssues classified = issueClassifier.classifyBpmnIssues(issues);
                
                PartialBpmnAnalysisResult result = new PartialBpmnAnalysisResult();
                result.setAnalysisType("PERFORMANCE");
                result.setIssues(classified);
                result.setPerformanceData(performanceResult);
                result.setFlowData(flowResult);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Performance analysis completed for diagram: {}, found {} issues", 
                    diagramId, classified.getTotalIssues());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Performance analysis failed for diagram: {}", diagramId, e);
                throw new RuntimeException("Performance analysis failed", e);
            }
        }, bpmnAnalysisExecutor);
    }
    
    /**
     * Комплексный анализ BPMN диаграммы
     */
    @Async
    public CompletableFuture<PartialBpmnAnalysisResult> analyzeComprehensive(String diagramId, BpmnDiagram diagram, String analysisId) {
        logger.info("Starting comprehensive analysis for diagram: {}, analysisId: {}", diagramId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "COMPREHENSIVE_ANALYSIS");
                
                // Парсим BPMN диаграмму
                BpmnParsedData parsedData = bpmnParser.parseBpmnContent(diagram.getBpmnContent());
                
                // Генерируем промпт
                String prompt = promptBuilder.buildComprehensiveAnalysisPrompt(parsedData);
                
                // Выполняем LLM анализ
                String llmResponse = executeLLMAnalysis(prompt, "comprehensive");
                
                // Парсим результат
                Map<String, Object> comprehensiveData = llmAnalyzer.parseComprehensiveAnalysis(llmResponse);
                
                PartialBpmnAnalysisResult result = new PartialBpmnAnalysisResult();
                result.setAnalysisType("COMPREHENSIVE");
                result.setComprehensiveData(comprehensiveData);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Comprehensive analysis completed for diagram: {}", diagramId);
                
                return result;
                
            } catch (Exception e) {
                logger.error("Comprehensive analysis failed for diagram: {}", diagramId, e);
                throw new RuntimeException("Comprehensive analysis failed", e);
            }
        }, bpmnAnalysisExecutor);
    }
    
    /**
     * Выполняет LLM анализ с fallback на разные провайдеры
     */
    private String executeLLMAnalysis(String prompt, String analysisType) {
        try {
            // Пробуем сначала OpenRouter (внешние модели)
            if (openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} BPMN analysis", analysisType);
                return openRouterClient.chatCompletion(getPreferredModel(), prompt, 3000, 0.3)
                    .get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.warn("OpenRouter failed for {} BPMN analysis, trying local model", analysisType, e);
        }
        
        try {
            // Fallback на локальную модель
            if (localLLMService.checkOllamaConnection() != null) {
                logger.debug("Using local LLM for {} BPMN analysis", analysisType);
                return localLLMService.localChatCompletion(getPreferredLocalModel(), prompt, 3000, 0.3)
                    .get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.error("All LLM providers failed for {} BPMN analysis", analysisType, e);
            throw new RuntimeException("No LLM provider available for BPMN analysis", e);
        }
        
        throw new RuntimeException("No LLM providers available");
    }
    
    /**
     * Получает статус анализа
     */
    public BpmnAnalysisStatus getAnalysisStatus(String analysisId) {
        return activeAnalyses.get(analysisId);
    }
    
    /**
     * Получает результаты анализа
     */
    public BpmnAnalysisResult getAnalysisResults(String analysisId) {
        return activeAnalyses.values().stream()
            .filter(status -> status.getAnalysisId().equals(analysisId))
            .map(status -> {
                String cacheKey = generateCacheKey(extractDiagramIdFromAnalysisId(analysisId), "comprehensive");
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
        logger.info("BPMN analysis result cache cleared");
    }
    
    /**
     * Получает статистику сервиса
     */
    public BpmnServiceStatistics getServiceStatistics() {
        BpmnServiceStatistics stats = new BpmnServiceStatistics();
        stats.setActiveAnalyses(activeAnalyses.size());
        stats.setCachedResults(resultCache.size());
        stats.setQueueSize(analysisQueue.size());
        return stats;
    }
    
    // Вспомогательные методы
    
    private String generateAnalysisId(String diagramId) {
        return "bpmn_analysis_" + diagramId + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(String diagramId, String analysisType) {
        return "bpmn_" + diagramId + "_" + analysisType;
    }
    
    private String getPreferredModel() {
        return "anthropic/claude-3-sonnet";
    }
    
    private String getPreferredLocalModel() {
        return "llama3.1:8b";
    }
    
    private void updateAnalysisStatus(String analysisId, String status) {
        activeAnalyses.put(analysisId, new BpmnAnalysisStatus(analysisId, status, LocalDateTime.now()));
    }
    
    private String extractDiagramIdFromAnalysisId(String analysisId) {
        String[] parts = analysisId.split("_");
        return parts.length > 2 ? parts[2] : "unknown";
    }
    
    private boolean isCacheValid(BpmnAnalysisResult result) {
        if (result == null || result.getCreatedAt() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(result.getCreatedAt().plusHours(CACHE_TTL_HOURS));
    }
    
    private void cacheResult(String cacheKey, BpmnAnalysisResult result) {
        resultCache.put(cacheKey, result);
        
        // Запускаем таймер очистки кэша
        scheduledExecutor.schedule(() -> {
            resultCache.remove(cacheKey);
        }, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }
    
    private BpmnAnalysisResult aggregateResults(String diagramId, BpmnDiagram diagram, String analysisId,
                                              PartialBpmnAnalysisResult structure, PartialBpmnAnalysisResult security,
                                              PartialBpmnAnalysisResult performance, PartialBpmnAnalysisResult comprehensive) {
        BpmnAnalysisResult result = new BpmnAnalysisResult();
        result.setAnalysisId(analysisId);
        result.setDiagramId(diagramId);
        result.setDiagram(diagram);
        result.setCreatedAt(LocalDateTime.now());
        
        // Агрегируем все проблемы
        List<BpmnIssueClassifier.ClassifiedBpmnIssue> allIssues = new ArrayList<>();
        if (structure.getIssues() != null) {
            allIssues.addAll(structure.getIssues().getIssues());
        }
        if (security.getIssues() != null) {
            allIssues.addAll(security.getIssues().getIssues());
        }
        if (performance.getIssues() != null) {
            allIssues.addAll(performance.getIssues().getIssues());
        }
        
        result.setAllIssues(allIssues);
        result.setStructureAnalysis(structure);
        result.setSecurityAnalysis(security);
        result.setPerformanceAnalysis(performance);
        result.setComprehensiveAnalysis(comprehensive);
        
        // Вычисляем общие метрики
        result.setTotalIssues(allIssues.size());
        result.setCriticalIssues((int) allIssues.stream()
            .filter(issue -> issue.getSeverity() == SeverityLevel.CRITICAL)
            .count());
        result.setHighIssues((int) allIssues.stream()
            .filter(issue -> issue.getSeverity() == SeverityLevel.HIGH)
            .count());
        
        // Создаем связанные сущности
        BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
        List<ProcessIssue> processIssues = convertToProcessIssues(allIssues, processId);
        List<ProcessSecurityCheck> securityChecks = convertToSecurityChecks(security, processId);
        
        result.setProcessIssues(processIssues);
        result.setSecurityChecks(securityChecks);
        
        return result;
    }
    
    private List<ProcessIssue> convertToProcessIssues(List<BpmnIssueClassifier.ClassifiedBpmnIssue> issues, BpmnProcessId processId) {
        return issues.stream()
            .map(issue -> ProcessIssue.createIssue(
                processId,
                convertToProcessIssueType(issue.getType()),
                convertToProcessIssueSeverity(issue.getSeverity()),
                issue.getTitle(),
                issue.getDescription()
            ))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private org.example.domain.valueobjects.ProcessIssueType convertToProcessIssueType(BpmnIssueClassifier.BpmnIssueType type) {
        return switch (type) {
            case STRUCTURE -> org.example.domain.valueobjects.ProcessIssueType.STRUCTURE;
            case SECURITY -> org.example.domain.valueobjects.ProcessIssueType.SECURITY;
            case PERFORMANCE -> org.example.domain.valueobjects.ProcessIssueType.PERFORMANCE;
            case VALIDATION -> org.example.domain.valueobjects.ProcessIssueType.VALIDATION;
            case LOGIC_ERROR -> org.example.domain.valueobjects.ProcessIssueType.LOGIC_ERROR;
            case COMPLIANCE -> org.example.domain.valueobjects.ProcessIssueType.COMPLIANCE;
        };
    }
    
    private org.example.domain.valueobjects.ProcessIssueSeverity convertToProcessIssueSeverity(SeverityLevel severity) {
        return switch (severity) {
            case CRITICAL -> org.example.domain.valueobjects.ProcessIssueSeverity.CRITICAL;
            case HIGH -> org.example.domain.valueobjects.ProcessIssueSeverity.HIGH;
            case MEDIUM -> org.example.domain.valueobjects.ProcessIssueSeverity.MEDIUM;
            case LOW -> org.example.domain.valueobjects.ProcessIssueSeverity.LOW;
            case INFO -> org.example.domain.valueobjects.ProcessIssueSeverity.LOW;
        };
    }
    
    private List<ProcessSecurityCheck> convertToSecurityChecks(PartialBpmnAnalysisResult security, BpmnProcessId processId) {
        if (security.getSecurityData() == null) {
            return new ArrayList<>();
        }
        
        return security.getSecurityData().getSecurityChecks().stream()
            .map(check -> ProcessSecurityCheck.createCheck(
                processId,
                check.getCheckType(),
                check.getCheckName(),
                check.getDescription()
            ))
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Вложенные классы для результатов анализа
    
    public static class BpmnAnalysisResult {
        private String analysisId;
        private String diagramId;
        private BpmnDiagram diagram;
        private LocalDateTime createdAt;
        private List<BpmnIssueClassifier.ClassifiedBpmnIssue> allIssues;
        private PartialBpmnAnalysisResult structureAnalysis;
        private PartialBpmnAnalysisResult securityAnalysis;
        private PartialBpmnAnalysisResult performanceAnalysis;
        private PartialBpmnAnalysisResult comprehensiveAnalysis;
        private List<ProcessIssue> processIssues;
        private List<ProcessSecurityCheck> securityChecks;
        private int totalIssues;
        private int criticalIssues;
        private int highIssues;
        
        // Getters and Setters
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
        
        public String getDiagramId() { return diagramId; }
        public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
        
        public BpmnDiagram getDiagram() { return diagram; }
        public void setDiagram(BpmnDiagram diagram) { this.diagram = diagram; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public List<BpmnIssueClassifier.ClassifiedBpmnIssue> getAllIssues() { return allIssues; }
        public void setAllIssues(List<BpmnIssueClassifier.ClassifiedBpmnIssue> allIssues) { this.allIssues = allIssues; }
        
        public PartialBpmnAnalysisResult getStructureAnalysis() { return structureAnalysis; }
        public void setStructureAnalysis(PartialBpmnAnalysisResult structureAnalysis) { this.structureAnalysis = structureAnalysis; }
        
        public PartialBpmnAnalysisResult getSecurityAnalysis() { return securityAnalysis; }
        public void setSecurityAnalysis(PartialBpmnAnalysisResult securityAnalysis) { this.securityAnalysis = securityAnalysis; }
        
        public PartialBpmnAnalysisResult getPerformanceAnalysis() { return performanceAnalysis; }
        public void setPerformanceAnalysis(PartialBpmnAnalysisResult performanceAnalysis) { this.performanceAnalysis = performanceAnalysis; }
        
        public PartialBpmnAnalysisResult getComprehensiveAnalysis() { return comprehensiveAnalysis; }
        public void setComprehensiveAnalysis(PartialBpmnAnalysisResult comprehensiveAnalysis) { this.comprehensiveAnalysis = comprehensiveAnalysis; }
        
        public List<ProcessIssue> getProcessIssues() { return processIssues; }
        public void setProcessIssues(List<ProcessIssue> processIssues) { this.processIssues = processIssues; }
        
        public List<ProcessSecurityCheck> getSecurityChecks() { return securityChecks; }
        public void setSecurityChecks(List<ProcessSecurityCheck> securityChecks) { this.securityChecks = securityChecks; }
        
        public int getTotalIssues() { return totalIssues; }
        public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
        
        public int getCriticalIssues() { return criticalIssues; }
        public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
        
        public int getHighIssues() { return highIssues; }
        public void setHighIssues(int highIssues) { this.highIssues = highIssues; }
    }
    
    public static class PartialBpmnAnalysisResult {
        private String analysisType;
        private BpmnIssueClassifier.ClassifiedBpmnIssues issues;
        private StructureAnalysisResult structureData;
        private SecurityAnalysisResult securityData;
        private PerformanceAnalysisResult performanceData;
        private FlowAnalysisResult flowData;
        private Map<String, Object> comprehensiveData;
        private long processingTimeMs;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getAnalysisType() { return analysisType; }
        public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
        
        public BpmnIssueClassifier.ClassifiedBpmnIssues getIssues() { return issues; }
        public void setIssues(BpmnIssueClassifier.ClassifiedBpmnIssues issues) { this.issues = issues; }
        
        public StructureAnalysisResult getStructureData() { return structureData; }
        public void setStructureData(StructureAnalysisResult structureData) { this.structureData = structureData; }
        
        public SecurityAnalysisResult getSecurityData() { return securityData; }
        public void setSecurityData(SecurityAnalysisResult securityData) { this.securityData = securityData; }
        
        public PerformanceAnalysisResult getPerformanceData() { return performanceData; }
        public void setPerformanceData(PerformanceAnalysisResult performanceData) { this.performanceData = performanceData; }
        
        public FlowAnalysisResult getFlowData() { return flowData; }
        public void setFlowData(FlowAnalysisResult flowData) { this.flowData = flowData; }
        
        public Map<String, Object> getComprehensiveData() { return comprehensiveData; }
        public void setComprehensiveData(Map<String, Object> comprehensiveData) { this.comprehensiveData = comprehensiveData; }
        
        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    public static class BpmnAnalysisStatus {
        private String analysisId;
        private String status;
        private LocalDateTime timestamp;
        private String errorMessage;
        
        public BpmnAnalysisStatus(String analysisId, String status, LocalDateTime timestamp) {
            this.analysisId = analysisId;
            this.status = status;
            this.timestamp = timestamp;
        }
        
        public BpmnAnalysisStatus(String analysisId, String status, LocalDateTime timestamp, String errorMessage) {
            this(analysisId, status, timestamp);
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public String getAnalysisId() { return analysisId; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class BpmnServiceStatistics {
        private int activeAnalyses;
        private int cachedResults;
        private int queueSize;
        
        // Getters and Setters
        public int getActiveAnalyses() { return activeAnalyses; }
        public void setActiveAnalyses(int activeAnalyses) { this.activeAnalyses = activeAnalyses; }
        
        public int getCachedResults() { return cachedResults; }
        public void setCachedResults(int cachedResults) { this.cachedResults = cachedResults; }
        
        public int getQueueSize() { return queueSize; }
        public void setQueueSize(int queueSize) { this.queueSize = queueSize; }
    }
}