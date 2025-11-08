package org.example.domain.dto.bpmn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for BPMN performance analysis reports
 * Used for GET /api/analysis/bpmn/{diagramId}/performance endpoint
 */
public class BpmnPerformanceReportResponse {
    
    private String diagramId;
    private String analysisId;
    private PerformanceSummary summary;
    private List<PerformanceBottleneck> bottlenecks;
    private List<ProcessMetric> processMetrics;
    private List<OptimizationRecommendation> recommendations;
    private PerformanceScore performanceScore;
    private Map<String, Object> analysisMetadata;
    private LocalDateTime analyzedAt;
    private String analysisMethod;
    private int totalElements;
    private int analyzedElements;
    private int bottlenecksFound;
    private List<String> criticalPaths;
    private List<String> performanceWarnings;
    private Map<String, String> slaTargets;
    private List<String> bestPractices;
    
    public BpmnPerformanceReportResponse() {}
    
    public BpmnPerformanceReportResponse(String diagramId, String analysisId, PerformanceSummary summary,
                                        List<PerformanceBottleneck> bottlenecks, List<ProcessMetric> processMetrics,
                                        List<OptimizationRecommendation> recommendations, PerformanceScore performanceScore,
                                        Map<String, Object> analysisMetadata, LocalDateTime analyzedAt,
                                        String analysisMethod, int totalElements, int analyzedElements, int bottlenecksFound,
                                        List<String> criticalPaths, List<String> performanceWarnings, 
                                        Map<String, String> slaTargets, List<String> bestPractices) {
        this.diagramId = diagramId;
        this.analysisId = analysisId;
        this.summary = summary;
        this.bottlenecks = bottlenecks;
        this.processMetrics = processMetrics;
        this.recommendations = recommendations;
        this.performanceScore = performanceScore;
        this.analysisMetadata = analysisMetadata;
        this.analyzedAt = analyzedAt;
        this.analysisMethod = analysisMethod;
        this.totalElements = totalElements;
        this.analyzedElements = analyzedElements;
        this.bottlenecksFound = bottlenecksFound;
        this.criticalPaths = criticalPaths;
        this.performanceWarnings = performanceWarnings;
        this.slaTargets = slaTargets;
        this.bestPractices = bestPractices;
    }
    
    /**
     * Creates a mock response for demonstration
     */
    public static BpmnPerformanceReportResponse createMock(String diagramId) {
        BpmnPerformanceReportResponse response = new BpmnPerformanceReportResponse();
        response.setDiagramId(diagramId);
        response.setAnalysisId("performance_analysis_" + System.currentTimeMillis());
        response.setSummary(createMockSummary());
        response.setBottlenecks(createMockBottlenecks());
        response.setProcessMetrics(createMockProcessMetrics());
        response.setRecommendations(createMockRecommendations());
        response.setPerformanceScore(createMockPerformanceScore());
        response.setAnalysisMetadata(Map.of(
            "analysisEngine", "LLM-Powered Performance Analyzer",
            "processComplexity", "Medium",
            "totalFlows", 8,
            "parallelExecutions", 2,
            "confidenceLevel", 0.91
        ));
        response.setAnalyzedAt(LocalDateTime.now().minusMinutes(20));
        response.setAnalysisMethod("comprehensive");
        response.setTotalElements(25);
        response.setAnalyzedElements(23);
        response.setBottlenecksFound(4);
        response.setCriticalPaths(List.of("user_registration_flow", "payment_processing_flow", "order_fulfillment_flow"));
        response.setPerformanceWarnings(List.of(
            "High latency detected in database operations",
            "Potential memory leaks in long-running processes"
        ));
        response.setSlaTargets(Map.of(
            "responseTime", "< 2000ms",
            "throughput", "> 100 req/sec",
            "availability", "99.9%",
            "errorRate", "< 0.1%"
        ));
        response.setBestPractices(List.of(
            "Implement connection pooling for database operations",
            "Use asynchronous processing for I/O-bound tasks",
            "Add caching layer for frequently accessed data",
            "Implement circuit breaker pattern for external services"
        ));
        return response;
    }
    
    public static class PerformanceSummary {
        private double overallScore;
        private String grade; // "A", "B", "C", "D", "F"
        private int totalBottlenecks;
        private int criticalBottlenecks;
        private int averageExecutionTime;
        private int peakExecutionTime;
        private String performanceLevel; // "EXCELLENT", "GOOD", "FAIR", "POOR", "CRITICAL"
        private Map<String, Integer> bottleneckBreakdown;
        private Map<String, Double> keyMetrics;
        
        public PerformanceSummary() {}
        
        public PerformanceSummary(double overallScore, String grade, int totalBottlenecks,
                                 int criticalBottlenecks, int averageExecutionTime, int peakExecutionTime,
                                 String performanceLevel, Map<String, Integer> bottleneckBreakdown,
                                 Map<String, Double> keyMetrics) {
            this.overallScore = overallScore;
            this.grade = grade;
            this.totalBottlenecks = totalBottlenecks;
            this.criticalBottlenecks = criticalBottlenecks;
            this.averageExecutionTime = averageExecutionTime;
            this.peakExecutionTime = peakExecutionTime;
            this.performanceLevel = performanceLevel;
            this.bottleneckBreakdown = bottleneckBreakdown;
            this.keyMetrics = keyMetrics;
        }
        
        // Getters and setters
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        
        public int getTotalBottlenecks() { return totalBottlenecks; }
        public void setTotalBottlenecks(int totalBottlenecks) { this.totalBottlenecks = totalBottlenecks; }
        
        public int getCriticalBottlenecks() { return criticalBottlenecks; }
        public void setCriticalBottlenecks(int criticalBottlenecks) { this.criticalBottlenecks = criticalBottlenecks; }
        
        public int getAverageExecutionTime() { return averageExecutionTime; }
        public void setAverageExecutionTime(int averageExecutionTime) { this.averageExecutionTime = averageExecutionTime; }
        
        public int getPeakExecutionTime() { return peakExecutionTime; }
        public void setPeakExecutionTime(int peakExecutionTime) { this.peakExecutionTime = peakExecutionTime; }
        
        public String getPerformanceLevel() { return performanceLevel; }
        public void setPerformanceLevel(String performanceLevel) { this.performanceLevel = performanceLevel; }
        
        public Map<String, Integer> getBottleneckBreakdown() { return bottleneckBreakdown; }
        public void setBottleneckBreakdown(Map<String, Integer> bottleneckBreakdown) { this.bottleneckBreakdown = bottleneckBreakdown; }
        
        public Map<String, Double> getKeyMetrics() { return keyMetrics; }
        public void setKeyMetrics(Map<String, Double> keyMetrics) { this.keyMetrics = keyMetrics; }
    }
    
    public static class PerformanceBottleneck {
        private String bottleneckId;
        private String elementId;
        private String elementName;
        private String elementType; // "UserTask", "ServiceTask", "Gateway", "Event"
        private String bottleneckType; // "database", "api_call", "io_operation", "computation", "resource_contention"
        private String severity; // "CRITICAL", "HIGH", "MEDIUM", "LOW"
        private int estimatedDelay; // in milliseconds
        private String impact; // "response_time", "throughput", "resource_usage", "user_experience"
        private String description;
        private String rootCause;
        private String recommendedSolution;
        private LocalDateTime detectedAt;
        private Map<String, Object> bottleneckData;
        private List<String> affectedFlows;
        
        public PerformanceBottleneck() {}
        
        public PerformanceBottleneck(String bottleneckId, String elementId, String elementName, String elementType,
                                   String bottleneckType, String severity, int estimatedDelay, String impact,
                                   String description, String rootCause, String recommendedSolution,
                                   LocalDateTime detectedAt, Map<String, Object> bottleneckData, List<String> affectedFlows) {
            this.bottleneckId = bottleneckId;
            this.elementId = elementId;
            this.elementName = elementName;
            this.elementType = elementType;
            this.bottleneckType = bottleneckType;
            this.severity = severity;
            this.estimatedDelay = estimatedDelay;
            this.impact = impact;
            this.description = description;
            this.rootCause = rootCause;
            this.recommendedSolution = recommendedSolution;
            this.detectedAt = detectedAt;
            this.bottleneckData = bottleneckData;
            this.affectedFlows = affectedFlows;
        }
        
        // Getters and setters
        public String getBottleneckId() { return bottleneckId; }
        public void setBottleneckId(String bottleneckId) { this.bottleneckId = bottleneckId; }
        
        public String getElementId() { return elementId; }
        public void setElementId(String elementId) { this.elementId = elementId; }
        
        public String getElementName() { return elementName; }
        public void setElementName(String elementName) { this.elementName = elementName; }
        
        public String getElementType() { return elementType; }
        public void setElementType(String elementType) { this.elementType = elementType; }
        
        public String getBottleneckType() { return bottleneckType; }
        public void setBottleneckType(String bottleneckType) { this.bottleneckType = bottleneckType; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public int getEstimatedDelay() { return estimatedDelay; }
        public void setEstimatedDelay(int estimatedDelay) { this.estimatedDelay = estimatedDelay; }
        
        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRootCause() { return rootCause; }
        public void setRootCause(String rootCause) { this.rootCause = rootCause; }
        
        public String getRecommendedSolution() { return recommendedSolution; }
        public void setRecommendedSolution(String recommendedSolution) { this.recommendedSolution = recommendedSolution; }
        
        public LocalDateTime getDetectedAt() { return detectedAt; }
        public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
        
        public Map<String, Object> getBottleneckData() { return bottleneckData; }
        public void setBottleneckData(Map<String, Object> bottleneckData) { this.bottleneckData = bottleneckData; }
        
        public List<String> getAffectedFlows() { return affectedFlows; }
        public void setAffectedFlows(List<String> affectedFlows) { this.affectedFlows = affectedFlows; }
        
        // Utility methods
        public boolean isCritical() {
            return "CRITICAL".equals(severity);
        }
        
        public boolean isHighSeverity() {
            return "HIGH".equals(severity);
        }
        
        public boolean isDatabaseBottleneck() {
            return "database".equalsIgnoreCase(bottleneckType);
        }
        
        public boolean isApiBottleneck() {
            return "api_call".equalsIgnoreCase(bottleneckType);
        }
    }
    
    public static class ProcessMetric {
        private String metricId;
        private String metricName;
        private String metricType; // "execution_time", "throughput", "resource_usage", "error_rate"
        private double value;
        private String unit; // "ms", "req/sec", "%", "MB"
        private String status; // "GOOD", "WARNING", "CRITICAL"
        private String threshold; // e.g., "< 1000ms", "> 50 req/sec"
        private String description;
        private LocalDateTime measuredAt;
        private Map<String, Object> metricData;
        
        public ProcessMetric() {}
        
        public ProcessMetric(String metricId, String metricName, String metricType, double value,
                           String unit, String status, String threshold, String description,
                           LocalDateTime measuredAt, Map<String, Object> metricData) {
            this.metricId = metricId;
            this.metricName = metricName;
            this.metricType = metricType;
            this.value = value;
            this.unit = unit;
            this.status = status;
            this.threshold = threshold;
            this.description = description;
            this.measuredAt = measuredAt;
            this.metricData = metricData;
        }
        
        // Getters and setters
        public String getMetricId() { return metricId; }
        public void setMetricId(String metricId) { this.metricId = metricId; }
        
        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }
        
        public String getMetricType() { return metricType; }
        public void setMetricType(String metricType) { this.metricType = metricType; }
        
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getThreshold() { return threshold; }
        public void setThreshold(String threshold) { this.threshold = threshold; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public LocalDateTime getMeasuredAt() { return measuredAt; }
        public void setMeasuredAt(LocalDateTime measuredAt) { this.measuredAt = measuredAt; }
        
        public Map<String, Object> getMetricData() { return metricData; }
        public void setMetricData(Map<String, Object> metricData) { this.metricData = metricData; }
    }
    
    public static class OptimizationRecommendation {
        private String recommendationId;
        private String title;
        private String category; // "database", "caching", "async_processing", "resource_optimization"
        private String priority; // "HIGH", "MEDIUM", "LOW"
        private String description;
        private String implementation;
        private String expectedImprovement;
        private String estimatedEffort; // "LOW", "MEDIUM", "HIGH"
        private String riskLevel; // "LOW", "MEDIUM", "HIGH"
        private List<String> affectedElements;
        private LocalDateTime createdAt;
        
        public OptimizationRecommendation() {}
        
        public OptimizationRecommendation(String recommendationId, String title, String category,
                                        String priority, String description, String implementation,
                                        String expectedImprovement, String estimatedEffort, String riskLevel,
                                        List<String> affectedElements, LocalDateTime createdAt) {
            this.recommendationId = recommendationId;
            this.title = title;
            this.category = category;
            this.priority = priority;
            this.description = description;
            this.implementation = implementation;
            this.expectedImprovement = expectedImprovement;
            this.estimatedEffort = estimatedEffort;
            this.riskLevel = riskLevel;
            this.affectedElements = affectedElements;
            this.createdAt = createdAt;
        }
        
        // Getters and setters
        public String getRecommendationId() { return recommendationId; }
        public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getImplementation() { return implementation; }
        public void setImplementation(String implementation) { this.implementation = implementation; }
        
        public String getExpectedImprovement() { return expectedImprovement; }
        public void setExpectedImprovement(String expectedImprovement) { this.expectedImprovement = expectedImprovement; }
        
        public String getEstimatedEffort() { return estimatedEffort; }
        public void setEstimatedEffort(String estimatedEffort) { this.estimatedEffort = estimatedEffort; }
        
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        
        public List<String> getAffectedElements() { return affectedElements; }
        public void setAffectedElements(List<String> affectedElements) { this.affectedElements = affectedElements; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        // Utility methods
        public boolean isHighPriority() {
            return "HIGH".equals(priority);
        }
        
        public boolean isLowRisk() {
            return "LOW".equals(riskLevel);
        }
    }
    
    public static class PerformanceScore {
        private double overallScore;
        private double executionTimeScore;
        private double throughputScore;
        private double resourceUtilizationScore;
        private double scalabilityScore;
        private double reliabilityScore;
        private String grade;
        private List<String> strengths;
        private List<String> weaknesses;
        private List<String> keyMetrics;
        
        public PerformanceScore() {}
        
        public PerformanceScore(double overallScore, double executionTimeScore, double throughputScore,
                              double resourceUtilizationScore, double scalabilityScore, double reliabilityScore,
                              String grade, List<String> strengths, List<String> weaknesses, List<String> keyMetrics) {
            this.overallScore = overallScore;
            this.executionTimeScore = executionTimeScore;
            this.throughputScore = throughputScore;
            this.resourceUtilizationScore = resourceUtilizationScore;
            this.scalabilityScore = scalabilityScore;
            this.reliabilityScore = reliabilityScore;
            this.grade = grade;
            this.strengths = strengths;
            this.weaknesses = weaknesses;
            this.keyMetrics = keyMetrics;
        }
        
        // Getters and setters
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        
        public double getExecutionTimeScore() { return executionTimeScore; }
        public void setExecutionTimeScore(double executionTimeScore) { this.executionTimeScore = executionTimeScore; }
        
        public double getThroughputScore() { return throughputScore; }
        public void setThroughputScore(double throughputScore) { this.throughputScore = throughputScore; }
        
        public double getResourceUtilizationScore() { return resourceUtilizationScore; }
        public void setResourceUtilizationScore(double resourceUtilizationScore) { this.resourceUtilizationScore = resourceUtilizationScore; }
        
        public double getScalabilityScore() { return scalabilityScore; }
        public void setScalabilityScore(double scalabilityScore) { this.scalabilityScore = scalabilityScore; }
        
        public double getReliabilityScore() { return reliabilityScore; }
        public void setReliabilityScore(double reliabilityScore) { this.reliabilityScore = reliabilityScore; }
        
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        
        public List<String> getStrengths() { return strengths; }
        public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        
        public List<String> getWeaknesses() { return weaknesses; }
        public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
        
        public List<String> getKeyMetrics() { return keyMetrics; }
        public void setKeyMetrics(List<String> keyMetrics) { this.keyMetrics = keyMetrics; }
    }
    
    // Utility methods
    public boolean hasCriticalBottlenecks() {
        return summary != null && summary.getCriticalBottlenecks() > 0;
    }
    
    public boolean isHighPerformance() {
        return summary != null && summary.getOverallScore() >= 8.0;
    }
    
    public boolean hasPerformanceIssues() {
        return bottlenecks != null && !bottlenecks.isEmpty();
    }
    
    public boolean needsOptimization() {
        return summary != null && (summary.getOverallScore() < 7.0 || hasCriticalBottlenecks());
    }
    
    private static PerformanceSummary createMockSummary() {
        return new PerformanceSummary(7.3, "B", 4, 1, 2500, 8500, "FAIR",
                                     Map.of("database", 2, "api_call", 1, "io_operation", 1),
                                     Map.of("avg_response_time", 2500.0, "peak_throughput", 85.0, "error_rate", 0.05));
    }
    
    private static List<PerformanceBottleneck> createMockBottlenecks() {
        return List.of(
            new PerformanceBottleneck(
                "bneck_001", "task_003", "Database Query Task", "ServiceTask",
                "database", "CRITICAL", 3200, "response_time",
                "Complex JOIN operation causing high latency",
                "Missing database indexes and inefficient query structure",
                "Add proper indexes, optimize query structure, consider query caching",
                LocalDateTime.now().minusHours(1), Map.of("queryType", "SELECT JOIN", "rowsAffected", 50000),
                List.of("user_registration_flow", "data_processing_flow")
            ),
            new PerformanceBottleneck(
                "bneck_002", "task_007", "External API Call", "ServiceTask",
                "api_call", "HIGH", 1800, "response_time",
                "Third-party API response time exceeds acceptable limits",
                "API rate limiting and network latency",
                "Implement request caching, add retry mechanism with exponential backoff",
                LocalDateTime.now().minusMinutes(45), Map.of("apiEndpoint", "/external/payment", "timeout", 5000),
                List.of("payment_processing_flow", "order_fulfillment_flow")
            )
        );
    }
    
    private static List<ProcessMetric> createMockProcessMetrics() {
        return List.of(
            new ProcessMetric("metric_001", "Average Response Time", "execution_time", 2500.0,
                            "ms", "WARNING", "< 2000ms", "Average time to complete process execution",
                            LocalDateTime.now().minusMinutes(30), Map.of("min", 1200, "max", 8500, "median", 2100)),
            new ProcessMetric("metric_002", "Process Throughput", "throughput", 65.0,
                            "req/sec", "GOOD", "> 50 req/sec", "Number of processes completed per second",
                            LocalDateTime.now().minusMinutes(30), Map.of("peak", 85, "off_peak", 35))
        );
    }
    
    private static List<OptimizationRecommendation> createMockRecommendations() {
        return List.of(
            new OptimizationRecommendation(
                "opt_001", "Implement Database Connection Pooling", "database", "HIGH",
                "Add connection pooling to reduce database connection overhead",
                "Configure HikariCP or similar connection pool with optimal settings",
                "30-50% reduction in database operation time", "MEDIUM", "LOW",
                List.of("Database Query Task", "Data Processing Task"), LocalDateTime.now().minusMinutes(30)
            ),
            new OptimizationRecommendation(
                "opt_002", "Add Request Caching Layer", "caching", "MEDIUM",
                "Implement Redis or similar caching for frequently accessed data",
                "Set up caching middleware with appropriate TTL and cache invalidation",
                "60-80% reduction in API response time for cached requests", "MEDIUM", "LOW",
                List.of("External API Call", "Data Retrieval Task"), LocalDateTime.now().minusMinutes(30)
            )
        );
    }
    
    private static PerformanceScore createMockPerformanceScore() {
        return new PerformanceScore(7.3, 6.8, 7.5, 7.0, 8.0, 7.5, "B",
                                  List.of("Good scalability", "Reliable error handling", "Efficient resource usage"),
                                  List.of("Slow database queries", "External API latency", "Missing caching layer"),
                                  List.of("Response Time", "Throughput", "Resource Utilization", "Error Rate"));
    }
    
    // Getters and setters
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public PerformanceSummary getSummary() { return summary; }
    public void setSummary(PerformanceSummary summary) { this.summary = summary; }
    
    public List<PerformanceBottleneck> getBottlenecks() { return bottlenecks; }
    public void setBottlenecks(List<PerformanceBottleneck> bottlenecks) { this.bottlenecks = bottlenecks; }
    
    public List<ProcessMetric> getProcessMetrics() { return processMetrics; }
    public void setProcessMetrics(List<ProcessMetric> processMetrics) { this.processMetrics = processMetrics; }
    
    public List<OptimizationRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<OptimizationRecommendation> recommendations) { this.recommendations = recommendations; }
    
    public PerformanceScore getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(PerformanceScore performanceScore) { this.performanceScore = performanceScore; }
    
    public Map<String, Object> getAnalysisMetadata() { return analysisMetadata; }
    public void setAnalysisMetadata(Map<String, Object> analysisMetadata) { this.analysisMetadata = analysisMetadata; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public String getAnalysisMethod() { return analysisMethod; }
    public void setAnalysisMethod(String analysisMethod) { this.analysisMethod = analysisMethod; }
    
    public int getTotalElements() { return totalElements; }
    public void setTotalElements(int totalElements) { this.totalElements = totalElements; }
    
    public int getAnalyzedElements() { return analyzedElements; }
    public void setAnalyzedElements(int analyzedElements) { this.analyzedElements = analyzedElements; }
    
    public int getBottlenecksFound() { return bottlenecksFound; }
    public void setBottlenecksFound(int bottlenecksFound) { this.bottlenecksFound = bottlenecksFound; }
    
    public List<String> getCriticalPaths() { return criticalPaths; }
    public void setCriticalPaths(List<String> criticalPaths) { this.criticalPaths = criticalPaths; }
    
    public List<String> getPerformanceWarnings() { return performanceWarnings; }
    public void setPerformanceWarnings(List<String> performanceWarnings) { this.performanceWarnings = performanceWarnings; }
    
    public Map<String, String> getSlaTargets() { return slaTargets; }
    public void setSlaTargets(Map<String, String> slaTargets) { this.slaTargets = slaTargets; }
    
    public List<String> getBestPractices() { return bestPractices; }
    public void setBestPractices(List<String> bestPractices) { this.bestPractices = bestPractices; }
}