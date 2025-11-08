package org.example.infrastructure.services.integrated;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.BpmnDiagram;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.example.infrastructure.services.integrated.analysis.ApiBpmnContradictionAnalyzer;
import org.example.infrastructure.services.integrated.prioritization.BusinessImpactPrioritizer;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.valueobjects.BpmnProcessId;
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
 * Основной сервис комплексного анализа, объединяющий OpenAPI и BPMN анализ
 * Координирует анализ API и бизнес-процессов, выявляет противоречия и приоритизирует проблемы
 */
@Service
public class ComprehensiveAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveAnalysisService.class);
    
    @Autowired
    private OpenApiAnalysisService openApiAnalysisService;
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private ApiBpmnContradictionAnalyzer contradictionAnalyzer;
    
    @Autowired
    private BusinessImpactPrioritizer impactPrioritizer;
    
    @Autowired
    private Executor comprehensiveAnalysisExecutor;
    
    @Autowired
    private ScheduledExecutorService scheduledExecutor;
    
    // Кэш для результатов комплексного анализа
    private final Map<String, ComprehensiveAnalysisResult> resultCache = new ConcurrentHashMap<>();
    private final Map<String, ComprehensiveAnalysisStatus> activeAnalyses = new ConcurrentHashMap<>();
    private final Queue<String> analysisQueue = new LinkedList<>();
    
    // Конфигурация анализа
    private static final int MAX_CONCURRENT_ANALYSES = 2;
    private static final long CACHE_TTL_HOURS = 12;
    private static final long ANALYSIS_TIMEOUT_MINUTES = 60;
    private static final double BUSINESS_IMPACT_THRESHOLD = 0.7;
    
    /**
     * Выполняет комплексный анализ проекта с OpenAPI и BPMN
     */
    @Async
    public CompletableFuture<ComprehensiveAnalysisResult> analyzeProject(
            String projectId, 
            ComprehensiveAnalysisRequest request) {
        
        String analysisId = generateAnalysisId(projectId);
        logger.info("Starting comprehensive analysis for project: {}, analysisId: {}", projectId, analysisId);
        
        try {
            // Валидация входных данных
            if (!validateRequest(request)) {
                return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Invalid request parameters")
                );
            }
            
            activeAnalyses.put(analysisId, 
                new ComprehensiveAnalysisStatus(analysisId, "STARTED", LocalDateTime.now()));
            
            // Проверяем кэш
            String cacheKey = generateCacheKey(projectId, request.getAnalysisType());
            if (resultCache.containsKey(cacheKey)) {
                ComprehensiveAnalysisResult cached = resultCache.get(cacheKey);
                if (isCacheValid(cached)) {
                    logger.info("Returning cached comprehensive analysis result for project: {}", projectId);
                    return CompletableFuture.completedFuture(cached);
                }
            }
            
            // Подготавливаем задачи для параллельного выполнения
            List<CompletableFuture<PartialAnalysisResult>> analysisTasks = new ArrayList<>();
            
            // 1. Анализ OpenAPI (если есть спецификация)
            if (request.hasOpenApiSpec()) {
                analysisTasks.add(analyzeOpenApi(projectId, request, analysisId));
            }
            
            // 2. Анализ BPMN (если есть диаграммы)
            if (request.hasBpmnDiagrams()) {
                analysisTasks.add(analyzeBpmn(projectId, request, analysisId));
            }
            
            // 3. Комплексный сравнительный анализ
            analysisTasks.add(analyzeIntegrated(projectId, request, analysisId));
            
            // Ожидаем завершения всех задач
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                analysisTasks.toArray(new CompletableFuture[0])
            );
            
            return allTasks.thenApply(v -> {
                try {
                    updateAnalysisStatus(analysisId, "AGGREGATING");
                    
                    // Собираем результаты
                    PartialAnalysisResult openApiResult = null;
                    PartialAnalysisResult bpmnResult = null;
                    PartialAnalysisResult integratedResult = null;
                    
                    for (int i = 0; i < analysisTasks.size(); i++) {
                        PartialAnalysisResult result = analysisTasks.get(i).get();
                        switch (result.getAnalysisType()) {
                            case "OPENAPI":
                                openApiResult = result;
                                break;
                            case "BPMN":
                                bpmnResult = result;
                                break;
                            case "INTEGRATED":
                                integratedResult = result;
                                break;
                        }
                    }
                    
                    // Агрегируем результаты
                    ComprehensiveAnalysisResult finalResult = aggregateResults(
                        projectId, request, analysisId,
                        openApiResult, bpmnResult, integratedResult
                    );
                    
                    // Кэшируем результат
                    cacheResult(cacheKey, finalResult);
                    
                    // Обновляем статус
                    activeAnalyses.put(analysisId, 
                        new ComprehensiveAnalysisStatus(analysisId, "COMPLETED", LocalDateTime.now()));
                    
                    logger.info("Comprehensive analysis completed for project: {}", projectId);
                    return finalResult;
                    
                } catch (Exception e) {
                    logger.error("Error aggregating comprehensive analysis results", e);
                    activeAnalyses.put(analysisId, 
                        new ComprehensiveAnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
                    throw new RuntimeException("Comprehensive analysis aggregation failed", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting comprehensive analysis for project: {}", projectId, e);
            activeAnalyses.put(analysisId, 
                new ComprehensiveAnalysisStatus(analysisId, "FAILED", LocalDateTime.now(), e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * Анализ OpenAPI спецификации в контексте проекта
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeOpenApi(
            String projectId, 
            ComprehensiveAnalysisRequest request, 
            String analysisId) {
        
        logger.info("Starting OpenAPI analysis for project: {}, analysisId: {}", projectId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "OPENAPI_ANALYSIS");
                
                // Получаем OpenAPI спецификацию
                OpenApiSpecification spec = createOpenApiSpecification(request);
                SpecificationId specId = SpecificationId.fromString(request.getOpenApiSpecId());
                
                // Запускаем анализ OpenAPI
                CompletableFuture<OpenApiAnalysisService.AnalysisResult> apiAnalysisFuture =
                    openApiAnalysisService.analyzeSpecification(specId.getValue(), spec);
                
                OpenApiAnalysisService.AnalysisResult apiResult = apiAnalysisFuture.get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES);
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("OPENAPI");
                result.setOpenApiResult(apiResult);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("OpenAPI analysis completed for project: {}", projectId);
                return result;
                
            } catch (Exception e) {
                logger.error("OpenAPI analysis failed for project: {}", projectId, e);
                throw new RuntimeException("OpenAPI analysis failed", e);
            }
        }, comprehensiveAnalysisExecutor);
    }
    
    /**
     * Анализ BPMN диаграмм в контексте проекта
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeBpmn(
            String projectId, 
            ComprehensiveAnalysisRequest request, 
            String analysisId) {
        
        logger.info("Starting BPMN analysis for project: {}, analysisId: {}", projectId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "BPMN_ANALYSIS");
                
                // Собираем все BPMN диаграммы
                List<BpmnAnalysisService.BpmnAnalysisResult> bpmnResults = new ArrayList<>();
                
                for (BpmnDiagram diagram : request.getBpmnDiagrams()) {
                    BpmnProcessId processId = BpmnProcessId.fromString(diagram.getDiagramId());
                    CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> bpmnAnalysisFuture =
                        bpmnAnalysisService.analyzeBpmnDiagram(processId.getValue(), diagram);
                    
                    BpmnAnalysisService.BpmnAnalysisResult bpmnResult = 
                        bpmnAnalysisFuture.get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES);
                    bpmnResults.add(bpmnResult);
                }
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("BPMN");
                result.setBpmnResults(bpmnResults);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("BPMN analysis completed for project: {}, found {} diagrams", 
                    projectId, bpmnResults.size());
                return result;
                
            } catch (Exception e) {
                logger.error("BPMN analysis failed for project: {}", projectId, e);
                throw new RuntimeException("BPMN analysis failed", e);
            }
        }, comprehensiveAnalysisExecutor);
    }
    
    /**
     * Интегрированный анализ - выявление противоречий и связей
     */
    @Async
    public CompletableFuture<PartialAnalysisResult> analyzeIntegrated(
            String projectId, 
            ComprehensiveAnalysisRequest request, 
            String analysisId) {
        
        logger.info("Starting integrated analysis for project: {}, analysisId: {}", projectId, analysisId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateAnalysisStatus(analysisId, "INTEGRATED_ANALYSIS");
                
                // Анализируем противоречия между API и BPMN
                List<ApiBpmnContradiction> contradictions = contradictionAnalyzer.analyzeContradictions(
                    request.getOpenApiSpec(), request.getBpmnDiagrams()
                );
                
                // Приоритизируем проблемы по бизнес-импакту
                List<PrioritizedIssue> prioritizedIssues = impactPrioritizer.prioritizeIssues(
                    request.getOpenApiSpec(), request.getBpmnDiagrams()
                );
                
                // Генерируем рекомендации
                List<IntegratedRecommendation> recommendations = generateRecommendations(
                    contradictions, prioritizedIssues
                );
                
                PartialAnalysisResult result = new PartialAnalysisResult();
                result.setAnalysisType("INTEGRATED");
                result.setContradictions(contradictions);
                result.setPrioritizedIssues(prioritizedIssues);
                result.setRecommendations(recommendations);
                result.setProcessingTimeMs(System.currentTimeMillis() - analysisId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Integrated analysis completed for project: {}, found {} contradictions", 
                    projectId, contradictions.size());
                return result;
                
            } catch (Exception e) {
                logger.error("Integrated analysis failed for project: {}", projectId, e);
                throw new RuntimeException("Integrated analysis failed", e);
            }
        }, comprehensiveAnalysisExecutor);
    }
    
    /**
     * Генерирует рекомендации на основе найденных противоречий и проблем
     */
    private List<IntegratedRecommendation> generateRecommendations(
            List<ApiBpmnContradiction> contradictions,
            List<PrioritizedIssue> prioritizedIssues) {
        
        List<IntegratedRecommendation> recommendations = new ArrayList<>();
        
        // Рекомендации по устранению противоречий
        for (ApiBpmnContradiction contradiction : contradictions) {
            IntegratedRecommendation recommendation = new IntegratedRecommendation();
            recommendation.setId("rec_" + UUID.randomUUID().toString());
            recommendation.setType("CONTRADICTION_RESOLUTION");
            recommendation.setTitle("Resolve API-BPMN Contradiction");
            recommendation.setDescription(generateContradictionResolution(contradiction));
            recommendation.setPriority(contradiction.getSeverity().getLevel() >= 3 ? "HIGH" : "MEDIUM");
            recommendation.setEffortEstimate(estimateResolutionEffort(contradiction));
            recommendation.setBusinessImpact(contradiction.getBusinessImpact());
            recommendation.setRelatedIssueId(contradiction.getId());
            recommendation.setCreatedAt(LocalDateTime.now());
            
            recommendations.add(recommendation);
        }
        
        // Рекомендации по улучшению процессов
        for (PrioritizedIssue issue : prioritizedIssues) {
            if (issue.getBusinessImpactScore() >= BUSINESS_IMPACT_THRESHOLD) {
                IntegratedRecommendation recommendation = new IntegratedRecommendation();
                recommendation.setId("rec_" + UUID.randomUUID().toString());
                recommendation.setType("PROCESS_IMPROVEMENT");
                recommendation.setTitle("Improve " + issue.getCategory() + " Process");
                recommendation.setDescription(generateProcessImprovement(issue));
                recommendation.setPriority(issue.getPriority().name());
                recommendation.setEffortEstimate(estimateProcessEffort(issue));
                recommendation.setBusinessImpact(issue.getBusinessImpactScore());
                recommendation.setRelatedIssueId(issue.getId());
                recommendation.setCreatedAt(LocalDateTime.now());
                
                recommendations.add(recommendation);
            }
        }
        
        return recommendations;
    }
    
    /**
     * Создает детальный отчет для дашборда
     */
    public ComprehensiveDashboardData createDashboardData(String projectId, ComprehensiveAnalysisResult result) {
        ComprehensiveDashboardData dashboardData = new ComprehensiveDashboardData();
        dashboardData.setProjectId(projectId);
        dashboardData.setGeneratedAt(LocalDateTime.now());
        
        // Основные метрики
        if (result.getOpenApiAnalysis() != null) {
            OpenApiAnalysisService.AnalysisResult apiResult = result.getOpenApiAnalysis().getOpenApiResult();
            if (apiResult != null) {
                dashboardData.setApiTotalIssues(apiResult.getTotalIssues());
                dashboardData.setApiCriticalIssues(apiResult.getCriticalIssues());
                dashboardData.setApiHighIssues(apiResult.getHighIssues());
                dashboardData.setApiSecurityScore(calculateSecurityScore(apiResult));
            }
        }
        
        // Метрики BPMN
        if (result.getBpmnAnalysis() != null) {
            int totalBpmnIssues = 0;
            int criticalBpmnIssues = 0;
            int highBpmnIssues = 0;
            
            for (BpmnAnalysisService.BpmnAnalysisResult bpmnResult : result.getBpmnAnalysis().getBpmnResults()) {
                totalBpmnIssues += bpmnResult.getTotalIssues();
                criticalBpmnIssues += bpmnResult.getCriticalIssues();
                highBpmnIssues += bpmnResult.getHighIssues();
            }
            
            dashboardData.setBpmnTotalIssues(totalBpmnIssues);
            dashboardData.setBpmnCriticalIssues(criticalBpmnIssues);
            dashboardData.setBpmnHighIssues(highBpmnIssues);
            dashboardData.setBpmnProcessScore(calculateProcessScore(result.getBpmnAnalysis().getBpmnResults()));
        }
        
        // Интегрированные метрики
        if (result.getIntegratedAnalysis() != null) {
            dashboardData.setContradictionsCount(result.getIntegratedAnalysis().getContradictions().size());
            dashboardData.setHighImpactIssues((int) result.getIntegratedAnalysis().getPrioritizedIssues().stream()
                .filter(issue -> issue.getBusinessImpactScore() >= BUSINESS_IMPACT_THRESHOLD)
                .count());
            dashboardData.setRecommendationsCount(result.getIntegratedAnalysis().getRecommendations().size());
            dashboardData.setOverallScore(calculateOverallScore(result));
        }
        
        return dashboardData;
    }
    
    // Вспомогательные методы
    
    private boolean validateRequest(ComprehensiveAnalysisRequest request) {
        return request != null && 
               (request.hasOpenApiSpec() || request.hasBpmnDiagrams()) &&
               request.getProjectId() != null;
    }
    
    private OpenApiSpecification createOpenApiSpecification(ComprehensiveAnalysisRequest request) {
        OpenApiSpecification spec = new OpenApiSpecification();
        spec.setSpecificationId(SpecificationId.fromString(request.getOpenApiSpecId()));
        spec.setTitle(request.getOpenApiTitle());
        spec.setVersion(request.getOpenApiVersion());
        spec.setContent(request.getOpenApiSpec());
        spec.setCreatedAt(LocalDateTime.now());
        spec.setLastModified(LocalDateTime.now());
        return spec;
    }
    
    private double calculateSecurityScore(OpenApiAnalysisService.AnalysisResult result) {
        if (result == null || result.getTotalIssues() == 0) return 10.0;
        
        double deduction = (result.getCriticalIssues() * 3.0 + result.getHighIssues() * 1.5) / result.getTotalIssues();
        return Math.max(0, 10.0 - deduction * 10);
    }
    
    private double calculateProcessScore(List<BpmnAnalysisService.BpmnAnalysisResult> bpmnResults) {
        if (bpmnResults.isEmpty()) return 10.0;
        
        double totalScore = 0;
        for (BpmnAnalysisService.BpmnAnalysisResult result : bpmnResults) {
            if (result.getTotalIssues() == 0) {
                totalScore += 10;
            } else {
                double deduction = (result.getCriticalIssues() * 3.0 + result.getHighIssues() * 1.5) / result.getTotalIssues();
                totalScore += Math.max(0, 10.0 - deduction * 10);
            }
        }
        
        return totalScore / bpmnResults.size();
    }
    
    private double calculateOverallScore(ComprehensiveAnalysisResult result) {
        double apiScore = calculateSecurityScore(result.getOpenApiAnalysis().getOpenApiResult());
        double processScore = calculateProcessScore(result.getBpmnAnalysis().getBpmnResults());
        double contradictionPenalty = result.getIntegratedAnalysis().getContradictions().size() * 0.5;
        
        return Math.max(0, (apiScore + processScore) / 2 - contradictionPenalty);
    }
    
    private String generateContradictionResolution(ApiBpmnContradiction contradiction) {
        return String.format("Resolve contradiction: %s. Recommended action: %s", 
            contradiction.getDescription(), 
            contradiction.getRecommendedAction());
    }
    
    private String estimateResolutionEffort(ApiBpmnContradiction contradiction) {
        return switch (contradiction.getSeverity()) {
            case CRITICAL -> "2-3 weeks";
            case HIGH -> "1-2 weeks";
            case MEDIUM -> "3-5 days";
            case LOW -> "1-2 days";
            default -> "1 week";
        };
    }
    
    private String generateProcessImprovement(PrioritizedIssue issue) {
        return String.format("Improve %s process by addressing: %s. Expected business benefit: %s",
            issue.getCategory(), issue.getTitle(), issue.getBusinessImpact());
    }
    
    private String estimateProcessEffort(PrioritizedIssue issue) {
        return switch (issue.getPriority()) {
            case CRITICAL -> "3-4 weeks";
            case HIGH -> "2-3 weeks";
            case MEDIUM -> "1-2 weeks";
            case LOW -> "3-5 days";
            default -> "1 week";
        };
    }
    
    private ComprehensiveAnalysisResult aggregateResults(
            String projectId, 
            ComprehensiveAnalysisRequest request, 
            String analysisId,
            PartialAnalysisResult openApiResult, 
            PartialAnalysisResult bpmnResult, 
            PartialAnalysisResult integratedResult) {
        
        ComprehensiveAnalysisResult result = new ComprehensiveAnalysisResult();
        result.setAnalysisId(analysisId);
        result.setProjectId(projectId);
        result.setRequest(request);
        result.setCreatedAt(LocalDateTime.now());
        
        result.setOpenApiAnalysis(openApiResult);
        result.setBpmnAnalysis(bpmnResult);
        result.setIntegratedAnalysis(integratedResult);
        
        return result;
    }
    
    private String generateAnalysisId(String projectId) {
        return "comprehensive_" + projectId + "_" + System.currentTimeMillis();
    }
    
    private String generateCacheKey(String projectId, String analysisType) {
        return "comprehensive_" + projectId + "_" + analysisType;
    }
    
    private void updateAnalysisStatus(String analysisId, String status) {
        activeAnalyses.put(analysisId, new ComprehensiveAnalysisStatus(analysisId, status, LocalDateTime.now()));
    }
    
    private boolean isCacheValid(ComprehensiveAnalysisResult result) {
        if (result == null || result.getCreatedAt() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(result.getCreatedAt().plusHours(CACHE_TTL_HOURS));
    }
    
    private void cacheResult(String cacheKey, ComprehensiveAnalysisResult result) {
        resultCache.put(cacheKey, result);
        
        scheduledExecutor.schedule(() -> {
            resultCache.remove(cacheKey);
        }, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }
    
    /**
     * Получает статус анализа
     */
    public ComprehensiveAnalysisStatus getAnalysisStatus(String analysisId) {
        return activeAnalyses.get(analysisId);
    }
    
    /**
     * Получает результаты анализа
     */
    public ComprehensiveAnalysisResult getAnalysisResults(String projectId) {
        return resultCache.values().stream()
            .filter(result -> result.getProjectId().equals(projectId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Очищает кэш результатов
     */
    public void clearCache() {
        resultCache.clear();
        logger.info("Comprehensive analysis result cache cleared");
    }
}