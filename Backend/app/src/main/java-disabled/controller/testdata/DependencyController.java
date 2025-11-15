package org.example.infrastructure.controller.testdata;

import org.example.domain.dto.testdata.GenerationRequest;
import org.example.domain.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Random;
import java.time.LocalDateTime;

/**
 * REST Controller for data dependency analysis endpoints
 * Handles: GET /api/data/dependencies/{resourceId}
 *          GET /api/data/chains/{processId}
 */
@RestController
@RequestMapping("/api/data")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class DependencyController {
    
    private static final Logger logger = LoggerFactory.getLogger(DependencyController.class);
    private final Random random = new Random();
    
    // Cache for dependency analysis results
    private final Map<String, DependencyAnalysisResult> dependencyCache = new HashMap<>();
    private final Map<String, ChainAnalysisResult> chainCache = new HashMap<>();
    
    /**
     * Get dependencies for a specific resource
     * GET /api/data/dependencies/{resourceId}
     */
    @GetMapping("/dependencies/{resourceId}")
    public ResponseEntity<ApiResponse<DependencyAnalysisResult>> getResourceDependencies(
            @PathVariable String resourceId,
            @RequestParam(defaultValue = "false") boolean includeTransitive,
            @RequestParam(defaultValue = "5") int maxDepth) {
        
        logger.info("Getting dependencies for resource: {}, includeTransitive: {}, maxDepth: {}", 
                   resourceId, includeTransitive, maxDepth);
        
        try {
            // Validate resource ID
            if (resourceId == null || resourceId.trim().isEmpty()) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "INVALID_RESOURCE_ID", "Resource ID cannot be empty");
                ApiResponse<DependencyAnalysisResult> errorResponse = ApiResponse.error(
                    error, resourceId);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Check cache first
            String cacheKey = resourceId + "_" + includeTransitive + "_" + maxDepth;
            DependencyAnalysisResult cached = dependencyCache.get(cacheKey);
            if (cached != null) {
                ApiResponse<DependencyAnalysisResult> response = ApiResponse.success(
                    cached, resourceId);
                return ResponseEntity.ok(response);
            }
            
            // Perform dependency analysis
            DependencyAnalysisResult result = analyzeResourceDependencies(resourceId, includeTransitive, maxDepth);
            
            // Cache the result
            dependencyCache.put(cacheKey, result);
            
            ApiResponse<DependencyAnalysisResult> response = ApiResponse.success(
                result, resourceId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error analyzing dependencies for resource: {}", resourceId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "DEPENDENCY_ANALYSIS_FAILED", 
                "Failed to analyze dependencies: " + e.getMessage());
            ApiResponse<DependencyAnalysisResult> errorResponse = ApiResponse.error(
                error, resourceId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get data flow chains for a process
     * GET /api/data/chains/{processId}
     */
    @GetMapping("/chains/{processId}")
    public ResponseEntity<ApiResponse<ChainAnalysisResult>> getDataFlowChains(
            @PathVariable String processId,
            @RequestParam(defaultValue = "true") boolean includeValidation,
            @RequestParam(defaultValue = "true") boolean includeOptimization) {
        
        logger.info("Getting data flow chains for process: {}, includeValidation: {}, includeOptimization: {}", 
                   processId, includeValidation, includeOptimization);
        
        try {
            // Validate process ID
            if (processId == null || processId.trim().isEmpty()) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "INVALID_PROCESS_ID", "Process ID cannot be empty");
                ApiResponse<ChainAnalysisResult> errorResponse = ApiResponse.error(
                    error, processId);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Check cache first
            String cacheKey = processId + "_" + includeValidation + "_" + includeOptimization;
            ChainAnalysisResult cached = chainCache.get(cacheKey);
            if (cached != null) {
                ApiResponse<ChainAnalysisResult> response = ApiResponse.success(
                    cached, processId);
                return ResponseEntity.ok(response);
            }
            
            // Perform chain analysis
            ChainAnalysisResult result = analyzeDataFlowChains(processId, includeValidation, includeOptimization);
            
            // Cache the result
            chainCache.put(cacheKey, result);
            
            ApiResponse<ChainAnalysisResult> response = ApiResponse.success(
                result, processId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error analyzing data flow chains for process: {}", processId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "CHAIN_ANALYSIS_FAILED", 
                "Failed to analyze data flow chains: " + e.getMessage());
            ApiResponse<ChainAnalysisResult> errorResponse = ApiResponse.error(
                error, processId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get all dependencies for a resource with visualization data
     * GET /api/data/dependencies/{resourceId}/graph
     */
    @GetMapping("/dependencies/{resourceId}/graph")
    public ResponseEntity<ApiResponse<DependencyGraphResult>> getDependencyGraph(
            @PathVariable String resourceId,
            @RequestParam(defaultValue = "json") String format) {
        
        logger.info("Getting dependency graph for resource: {}, format: {}", resourceId, format);
        
        try {
            // Get base dependency analysis
            DependencyAnalysisResult baseResult = analyzeResourceDependencies(resourceId, true, 5);
            
            // Create graph visualization data
            DependencyGraphResult graphResult = createDependencyGraph(baseResult, format);
            
            ApiResponse<DependencyGraphResult> response = ApiResponse.success(
                graphResult, resourceId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating dependency graph for resource: {}", resourceId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "GRAPH_GENERATION_FAILED", 
                "Failed to generate dependency graph: " + e.getMessage());
            ApiResponse<DependencyGraphResult> errorResponse = ApiResponse.error(
                error, resourceId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Validate dependency integrity
     * POST /api/data/dependencies/validate
     */
    @PostMapping("/dependencies/validate")
    public ResponseEntity<ApiResponse<DependencyValidationResult>> validateDependencies(
            @RequestBody DependencyValidationRequest request) {
        
        logger.info("Validating dependencies for {} resources", 
                   request.getResourceIds().size());
        
        try {
            // Perform validation
            DependencyValidationResult result = validateDependencyIntegrity(request);
            
            ApiResponse<DependencyValidationResult> response = ApiResponse.success(
                result, "validation_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating dependencies", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "DEPENDENCY_VALIDATION_FAILED", 
                "Failed to validate dependencies: " + e.getMessage());
            ApiResponse<DependencyValidationResult> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get dependency analysis history
     * GET /api/data/dependencies/history
     */
    @GetMapping("/dependencies/history")
    public ResponseEntity<ApiResponse<Map<String, DependencyAnalysisResult>>> getDependencyHistory(
            @RequestParam(defaultValue = "20") int limit) {
        
        logger.debug("Getting dependency analysis history with limit: {}", limit);
        
        try {
            Map<String, DependencyAnalysisResult> recentAnalyses = new HashMap<>();
            
            dependencyCache.entrySet().stream()
                .limit(Math.max(1, Math.min(limit, 50)))
                .forEach(entry -> recentAnalyses.put(entry.getKey(), entry.getValue()));
            
            ApiResponse<Map<String, DependencyAnalysisResult>> response = ApiResponse.success(
                recentAnalyses, "history_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting dependency history", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "HISTORY_RETRIEVAL_FAILED", 
                "Failed to retrieve dependency history: " + e.getMessage());
            ApiResponse<Map<String, DependencyAnalysisResult>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get dependency analysis capabilities
     * GET /api/data/dependencies/capabilities
     */
    @GetMapping("/dependencies/capabilities")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDependencyCapabilities() {
        
        logger.debug("Getting dependency analysis capabilities");
        
        try {
            Map<String, Object> capabilities = Map.of(
                "maxDepth", 10,
                "supportsTransitive", true,
                "supportedFormats", List.of("json", "xml", "graphviz", "mermaid"),
                "maxResources", 1000,
                "analysisTypes", List.of("DIRECT", "TRANSITIVE", "CYCLIC", "CRITICAL"),
                "visualizationTypes", List.of("hierarchy", "force-directed", "tree", "circular"),
                "validationChecks", List.of("integrity", "consistency", "completeness", "performance")
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                capabilities, "capabilities_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting dependency capabilities", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "CAPABILITIES_RETRIEVAL_FAILED", 
                "Failed to retrieve capabilities: " + e.getMessage());
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Private helper methods
    
    private DependencyAnalysisResult analyzeResourceDependencies(String resourceId, boolean includeTransitive, int maxDepth) {
        DependencyAnalysisResult result = new DependencyAnalysisResult();
        result.setResourceId(resourceId);
        result.setAnalysisId("dep_" + UUID.randomUUID().toString());
        result.setAnalyzedAt(LocalDateTime.now());
        result.setIncludeTransitive(includeTransitive);
        result.setMaxDepth(maxDepth);
        
        // Simulate dependency analysis
        List<DependencyInfo> directDependencies = generateDirectDependencies(resourceId);
        result.setDirectDependencies(directDependencies);
        
        if (includeTransitive && maxDepth > 1) {
            List<DependencyInfo> transitiveDependencies = generateTransitiveDependencies(resourceId, maxDepth);
            result.setTransitiveDependencies(transitiveDependencies);
        }
        
        // Analyze dependency metrics
        result.setTotalDependencies(directDependencies.size() + 
            (result.getTransitiveDependencies() != null ? result.getTransitiveDependencies().size() : 0));
        
        // Find cyclic dependencies
        List<String> cyclicDependencies = findCyclicDependencies(resourceId, directDependencies);
        result.setCyclicDependencies(cyclicDependencies);
        
        // Identify critical dependencies
        List<String> criticalDependencies = identifyCriticalDependencies(directDependencies);
        result.setCriticalDependencies(criticalDependencies);
        
        // Calculate dependency metrics
        result.setMaxDepth(calculateMaxDepth(directDependencies));
        result.setAvgDepth(calculateAvgDepth(directDependencies));
        result.setComplexityScore(calculateComplexityScore(directDependencies));
        
        // Add recommendations
        List<String> recommendations = generateDependencyRecommendations(result);
        result.setRecommendations(recommendations);
        
        return result;
    }
    
    private ChainAnalysisResult analyzeDataFlowChains(String processId, boolean includeValidation, boolean includeOptimization) {
        ChainAnalysisResult result = new ChainAnalysisResult();
        result.setProcessId(processId);
        result.setAnalysisId("chain_" + UUID.randomUUID().toString());
        result.setAnalyzedAt(LocalDateTime.now());
        result.setIncludeValidation(includeValidation);
        result.setIncludeOptimization(includeOptimization);
        
        // Simulate chain analysis
        List<DataFlowChain> chains = generateDataFlowChains(processId);
        result.setChains(chains);
        
        // Analyze chain metrics
        result.setTotalChains(chains.size());
        result.setTotalElements(chains.stream().mapToInt(chain -> chain.getElements().size()).sum());
        
        // Find chain dependencies
        List<ChainDependency> chainDependencies = findChainDependencies(chains);
        result.setChainDependencies(chainDependencies);
        
        // Validate chains if requested
        if (includeValidation) {
            List<ChainValidationResult> validationResults = validateChains(chains);
            result.setValidationResults(validationResults);
        }
        
        // Optimize chains if requested
        if (includeOptimization) {
            List<ChainOptimization> optimizations = optimizeChains(chains);
            result.setOptimizations(optimizations);
        }
        
        // Calculate performance metrics
        result.setAvgChainLength(chains.stream().mapToInt(chain -> chain.getElements().size()).average().orElse(0.0));
        result.setMaxParallelism(calculateMaxParallelism(chains));
        result.setBottleneckScore(calculateBottleneckScore(chains));
        
        // Add recommendations
        List<String> recommendations = generateChainRecommendations(result);
        result.setRecommendations(recommendations);
        
        return result;
    }
    
    private List<DependencyInfo> generateDirectDependencies(String resourceId) {
        List<DependencyInfo> dependencies = new ArrayList<>();
        
        // Generate random number of dependencies (1-5)
        int count = 1 + random.nextInt(5);
        
        for (int i = 0; i < count; i++) {
            DependencyInfo dep = new DependencyInfo();
            dep.setDependencyId("dep_" + UUID.randomUUID().toString());
            dep.setSourceResourceId(resourceId);
            dep.setTargetResourceId("resource_" + UUID.randomUUID().toString());
            dep.setDependencyType(DependencyType.values()[random.nextInt(DependencyType.values().length)]);
            dep.setStrength(DependencyStrength.values()[random.nextInt(DependencyStrength.values().length)]);
            dep.setIsActive(random.nextBoolean());
            dep.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            // Add properties
            dep.setProperties(Map.of(
                "type", dep.getDependencyType().name(),
                "strength", dep.getStrength().name(),
                "isActive", dep.getIsActive()
            ));
            
            dependencies.add(dep);
        }
        
        return dependencies;
    }
    
    private List<DependencyInfo> generateTransitiveDependencies(String resourceId, int maxDepth) {
        List<DependencyInfo> dependencies = new ArrayList<>();
        
        // Generate fewer transitive dependencies
        int count = random.nextInt(3);
        
        for (int i = 0; i < count; i++) {
            DependencyInfo dep = new DependencyInfo();
            dep.setDependencyId("trans_" + UUID.randomUUID().toString());
            dep.setSourceResourceId(resourceId);
            dep.setTargetResourceId("trans_resource_" + UUID.randomUUID().toString());
            dep.setDependencyType(DependencyType.INDIRECT);
            dep.setStrength(DependencyStrength.WEAK);
            dep.setIsActive(random.nextBoolean());
            dep.setDepth(2 + random.nextInt(maxDepth - 1));
            
            dependencies.add(dep);
        }
        
        return dependencies;
    }
    
    private List<String> findCyclicDependencies(String resourceId, List<DependencyInfo> dependencies) {
        // Simulate finding cyclic dependencies
        if (random.nextDouble() < 0.1) { // 10% chance of cycles
            return List.of("resource_cycle_1", "resource_cycle_2");
        }
        return new ArrayList<>();
    }
    
    private List<String> identifyCriticalDependencies(List<DependencyInfo> dependencies) {
        return dependencies.stream()
            .filter(dep -> dep.getStrength() == DependencyStrength.CRITICAL)
            .map(DependencyInfo::getDependencyId)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    private int calculateMaxDepth(List<DependencyInfo> dependencies) {
        return dependencies.stream()
            .mapToInt(dep -> dep.getDepth() != null ? dep.getDepth() : 1)
            .max()
            .orElse(1);
    }
    
    private double calculateAvgDepth(List<DependencyInfo> dependencies) {
        return dependencies.stream()
            .mapToInt(dep -> dep.getDepth() != null ? dep.getDepth() : 1)
            .average()
            .orElse(0.0);
    }
    
    private double calculateComplexityScore(List<DependencyInfo> dependencies) {
        // Simple complexity calculation based on number of dependencies and their strengths
        double baseScore = dependencies.size() * 10;
        double strengthBonus = dependencies.stream()
            .mapToInt(dep -> dep.getStrength().ordinal() + 1)
            .sum() * 5;
        
        return Math.min(100.0, baseScore + strengthBonus);
    }
    
    private List<String> generateDependencyRecommendations(DependencyAnalysisResult result) {
        List<String> recommendations = new ArrayList<>();
        
        if (result.getTotalDependencies() > 5) {
            recommendations.add("Consider refactoring to reduce direct dependencies");
        }
        
        if (!result.getCyclicDependencies().isEmpty()) {
            recommendations.add("Resolve cyclic dependencies to improve maintainability");
        }
        
        if (result.getComplexityScore() > 70) {
            recommendations.add("High complexity detected - consider dependency optimization");
        }
        
        return recommendations;
    }
    
    private List<DataFlowChain> generateDataFlowChains(String processId) {
        List<DataFlowChain> chains = new ArrayList<>();
        
        // Generate 1-3 chains
        int count = 1 + random.nextInt(3);
        
        for (int i = 0; i < count; i++) {
            DataFlowChain chain = new DataFlowChain();
            chain.setChainId("chain_" + UUID.randomUUID().toString());
            chain.setName("Chain " + (i + 1) + " for " + processId);
            chain.setProcessId(processId);
            chain.setState(ChainState.ACTIVE);
            
            // Generate elements for the chain
            List<String> elements = new ArrayList<>();
            int elementCount = 2 + random.nextInt(5);
            for (int j = 0; j < elementCount; j++) {
                elements.add("element_" + UUID.randomUUID().toString());
            }
            chain.setElements(elements);
            
            chains.add(chain);
        }
        
        return chains;
    }
    
    private List<ChainDependency> findChainDependencies(List<DataFlowChain> chains) {
        List<ChainDependency> dependencies = new ArrayList<>();
        
        // Simulate chain dependencies
        for (int i = 0; i < chains.size() - 1; i++) {
            ChainDependency dep = new ChainDependency();
            dep.setSourceChainId(chains.get(i).getChainId());
            dep.setTargetChainId(chains.get(i + 1).getChainId());
            dep.setDependencyType(ChainDependencyType.DATA_FLOW);
            dependencies.add(dep);
        }
        
        return dependencies;
    }
    
    private List<ChainValidationResult> validateChains(List<DataFlowChain> chains) {
        List<ChainValidationResult> results = new ArrayList<>();
        
        for (DataFlowChain chain : chains) {
            ChainValidationResult result = new ChainValidationResult();
            result.setChainId(chain.getChainId());
            result.setValid(random.nextBoolean());
            result.setValidationScore(70.0 + random.nextDouble() * 30.0);
            result.setIssues(random.nextBoolean() ? List.of("Minor performance issue detected") : new ArrayList<>());
            results.add(result);
        }
        
        return results;
    }
    
    private List<ChainOptimization> optimizeChains(List<DataFlowChain> chains) {
        List<ChainOptimization> optimizations = new ArrayList<>();
        
        for (DataFlowChain chain : chains) {
            if (random.nextBoolean()) { // 50% chance of optimization
                ChainOptimization opt = new ChainOptimization();
                opt.setChainId(chain.getChainId());
                opt.setOptimizationType("Performance");
                opt.setImprovementPercentage(5.0 + random.nextDouble() * 20.0);
                opt.setDescription("Optimized data flow processing");
                optimizations.add(opt);
            }
        }
        
        return optimizations;
    }
    
    private double calculateMaxParallelism(List<DataFlowChain> chains) {
        return chains.stream()
            .mapToInt(chain -> chain.getElements().size())
            .max()
            .orElse(1) * 0.7; // Assume 70% parallelizable
    }
    
    private double calculateBottleneckScore(List<DataFlowChain> chains) {
        return random.nextDouble() * 50.0; // 0-50% bottleneck score
    }
    
    private List<String> generateChainRecommendations(ChainAnalysisResult result) {
        List<String> recommendations = new ArrayList<>();
        
        if (result.getAvgChainLength() > 5) {
            recommendations.add("Consider breaking long chains into smaller segments");
        }
        
        if (result.getBottleneckScore() > 30) {
            recommendations.add("High bottleneck detected - optimize critical path");
        }
        
        if (result.getTotalChains() > 3) {
            recommendations.add("Multiple chains detected - consider parallel execution");
        }
        
        return recommendations;
    }
    
    private DependencyGraphResult createDependencyGraph(DependencyAnalysisResult baseResult, String format) {
        DependencyGraphResult graphResult = new DependencyGraphResult();
        graphResult.setResourceId(baseResult.getResourceId());
        graphResult.setFormat(format);
        graphResult.setGeneratedAt(LocalDateTime.now());
        
        // Create nodes
        List<GraphNode> nodes = new ArrayList<>();
        nodes.add(new GraphNode(baseResult.getResourceId(), "resource", "Resource"));
        
        for (DependencyInfo dep : baseResult.getDirectDependencies()) {
            nodes.add(new GraphNode(dep.getTargetResourceId(), "dependency", "Dependency"));
        }
        
        // Create edges
        List<GraphEdge> edges = new ArrayList<>();
        for (DependencyInfo dep : baseResult.getDirectDependencies()) {
            edges.add(new GraphEdge(
                baseResult.getResourceId(), 
                dep.getTargetResourceId(), 
                dep.getDependencyType().name()
            ));
        }
        
        graphResult.setNodes(nodes);
        graphResult.setEdges(edges);
        graphResult.setLayout("hierarchical");
        
        return graphResult;
    }
    
    private DependencyValidationResult validateDependencyIntegrity(DependencyValidationRequest request) {
        DependencyValidationResult result = new DependencyValidationResult();
        result.setValidationId("val_" + UUID.randomUUID().toString());
        result.setValidatedAt(LocalDateTime.now());
        result.setResourceCount(request.getResourceIds().size());
        
        // Simulate validation
        int totalChecks = request.getResourceIds().size() * 3; // 3 checks per resource
        int passedChecks = (int) (totalChecks * 0.85); // 85% pass rate
        int failedChecks = totalChecks - passedChecks;
        
        result.setTotalChecks(totalChecks);
        result.setPassedChecks(passedChecks);
        result.setFailedChecks(failedChecks);
        result.setValidationScore((passedChecks * 100.0) / totalChecks);
        
        if (failedChecks > 0) {
            result.setIssues(List.of(
                "Some dependencies have integrity issues",
                "Missing transitive dependencies detected"
            ));
        }
        
        return result;
    }
    
    // Inner classes for result objects
    
    public static class DependencyAnalysisResult {
        private String resourceId;
        private String analysisId;
        private LocalDateTime analyzedAt;
        private boolean includeTransitive;
        private int maxDepth;
        private List<DependencyInfo> directDependencies;
        private List<DependencyInfo> transitiveDependencies;
        private List<String> cyclicDependencies;
        private List<String> criticalDependencies;
        private int totalDependencies;
        private int calculatedMaxDepth;
        private double avgDepth;
        private double complexityScore;
        private List<String> recommendations;
        
        // Getters and setters
        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }
        
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
        
        public LocalDateTime getAnalyzedAt() { return analyzedAt; }
        public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
        
        public boolean isIncludeTransitive() { return includeTransitive; }
        public void setIncludeTransitive(boolean includeTransitive) { this.includeTransitive = includeTransitive; }
        
        public int getMaxDepth() { return maxDepth; }
        public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
        
        public List<DependencyInfo> getDirectDependencies() { return directDependencies; }
        public void setDirectDependencies(List<DependencyInfo> directDependencies) { this.directDependencies = directDependencies; }
        
        public List<DependencyInfo> getTransitiveDependencies() { return transitiveDependencies; }
        public void setTransitiveDependencies(List<DependencyInfo> transitiveDependencies) { this.transitiveDependencies = transitiveDependencies; }
        
        public List<String> getCyclicDependencies() { return cyclicDependencies; }
        public void setCyclicDependencies(List<String> cyclicDependencies) { this.cyclicDependencies = cyclicDependencies; }
        
        public List<String> getCriticalDependencies() { return criticalDependencies; }
        public void setCriticalDependencies(List<String> criticalDependencies) { this.criticalDependencies = criticalDependencies; }
        
        public int getTotalDependencies() { return totalDependencies; }
        public void setTotalDependencies(int totalDependencies) { this.totalDependencies = totalDependencies; }
        
        public int getCalculatedMaxDepth() { return calculatedMaxDepth; }
        public void setCalculatedMaxDepth(int calculatedMaxDepth) { this.calculatedMaxDepth = calculatedMaxDepth; }
        
        public double getAvgDepth() { return avgDepth; }
        public void setAvgDepth(double avgDepth) { this.avgDepth = avgDepth; }
        
        public double getComplexityScore() { return complexityScore; }
        public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
    
    public static class ChainAnalysisResult {
        private String processId;
        private String analysisId;
        private LocalDateTime analyzedAt;
        private boolean includeValidation;
        private boolean includeOptimization;
        private List<DataFlowChain> chains;
        private List<ChainDependency> chainDependencies;
        private List<ChainValidationResult> validationResults;
        private List<ChainOptimization> optimizations;
        private int totalChains;
        private int totalElements;
        private double avgChainLength;
        private double maxParallelism;
        private double bottleneckScore;
        private List<String> recommendations;
        
        // Getters and setters
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
        
        public LocalDateTime getAnalyzedAt() { return analyzedAt; }
        public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
        
        public boolean isIncludeValidation() { return includeValidation; }
        public void setIncludeValidation(boolean includeValidation) { this.includeValidation = includeValidation; }
        
        public boolean isIncludeOptimization() { return includeOptimization; }
        public void setIncludeOptimization(boolean includeOptimization) { this.includeOptimization = includeOptimization; }
        
        public List<DataFlowChain> getChains() { return chains; }
        public void setChains(List<DataFlowChain> chains) { this.chains = chains; }
        
        public List<ChainDependency> getChainDependencies() { return chainDependencies; }
        public void setChainDependencies(List<ChainDependency> chainDependencies) { this.chainDependencies = chainDependencies; }
        
        public List<ChainValidationResult> getValidationResults() { return validationResults; }
        public void setValidationResults(List<ChainValidationResult> validationResults) { this.validationResults = validationResults; }
        
        public List<ChainOptimization> getOptimizations() { return optimizations; }
        public void setOptimizations(List<ChainOptimization> optimizations) { this.optimizations = optimizations; }
        
        public int getTotalChains() { return totalChains; }
        public void setTotalChains(int totalChains) { this.totalChains = totalChains; }
        
        public int getTotalElements() { return totalElements; }
        public void setTotalElements(int totalElements) { this.totalElements = totalElements; }
        
        public double getAvgChainLength() { return avgChainLength; }
        public void setAvgChainLength(double avgChainLength) { this.avgChainLength = avgChainLength; }
        
        public double getMaxParallelism() { return maxParallelism; }
        public void setMaxParallelism(double maxParallelism) { this.maxParallelism = maxParallelism; }
        
        public double getBottleneckScore() { return bottleneckScore; }
        public void setBottleneckScore(double bottleneckScore) { this.bottleneckScore = bottleneckScore; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
    
    // Additional helper classes
    public static class DependencyInfo {
        private String dependencyId;
        private String sourceResourceId;
        private String targetResourceId;
        private DependencyType dependencyType;
        private DependencyStrength strength;
        private boolean isActive;
        private LocalDateTime createdAt;
        private Integer depth;
        private Map<String, Object> properties;
        
        // Getters and setters
        public String getDependencyId() { return dependencyId; }
        public void setDependencyId(String dependencyId) { this.dependencyId = dependencyId; }
        
        public String getSourceResourceId() { return sourceResourceId; }
        public void setSourceResourceId(String sourceResourceId) { this.sourceResourceId = sourceResourceId; }
        
        public String getTargetResourceId() { return targetResourceId; }
        public void setTargetResourceId(String targetResourceId) { this.targetResourceId = targetResourceId; }
        
        public DependencyType getDependencyType() { return dependencyType; }
        public void setDependencyType(DependencyType dependencyType) { this.dependencyType = dependencyType; }
        
        public DependencyStrength getStrength() { return strength; }
        public void setStrength(DependencyStrength strength) { this.strength = strength; }
        
        public boolean getIsActive() { return isActive; }
        public void setIsActive(boolean isActive) { this.isActive = isActive; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public Integer getDepth() { return depth; }
        public void setDepth(Integer depth) { this.depth = depth; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
    
    public static class DataFlowChain {
        private String chainId;
        private String name;
        private String processId;
        private ChainState state;
        private List<String> elements;
        
        // Getters and setters
        public String getChainId() { return chainId; }
        public void setChainId(String chainId) { this.chainId = chainId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        
        public ChainState getState() { return state; }
        public void setState(ChainState state) { this.state = state; }
        
        public List<String> getElements() { return elements; }
        public void setElements(List<String> elements) { this.elements = elements; }
    }
    
    // Enums
    public enum DependencyType {
        DIRECT, INDIRECT, CONDITIONAL, BIDIRECTIONAL
    }
    
    public enum DependencyStrength {
        WEAK, MEDIUM, STRONG, CRITICAL
    }
    
    public enum ChainState {
        ACTIVE, INACTIVE, DEPRECATED, ERROR
    }
    
    // Additional result classes (shortened for brevity)
    public static class ChainDependency { private String sourceChainId; private String targetChainId; private ChainDependencyType dependencyType; public String getSourceChainId() { return sourceChainId; } public void setSourceChainId(String sourceChainId) { this.sourceChainId = sourceChainId; } public String getTargetChainId() { return targetChainId; } public void setTargetChainId(String targetChainId) { this.targetChainId = targetChainId; } public ChainDependencyType getDependencyType() { return dependencyType; } public void setDependencyType(ChainDependencyType dependencyType) { this.dependencyType = dependencyType; } }
    public enum ChainDependencyType { DATA_FLOW, CONTROL_FLOW, RESOURCE_SHARING }
    public static class ChainValidationResult { private String chainId; private boolean isValid; private double validationScore; private List<String> issues; public String getChainId() { return chainId; } public void setChainId(String chainId) { this.chainId = chainId; } public boolean isValid() { return isValid; } public void setValid(boolean isValid) { this.isValid = isValid; } public double getValidationScore() { return validationScore; } public void setValidationScore(double validationScore) { this.validationScore = validationScore; } public List<String> getIssues() { return issues; } public void setIssues(List<String> issues) { this.issues = issues; } }
    public static class ChainOptimization { private String chainId; private String optimizationType; private double improvementPercentage; private String description; public String getChainId() { return chainId; } public void setChainId(String chainId) { this.chainId = chainId; } public String getOptimizationType() { return optimizationType; } public void setOptimizationType(String optimizationType) { this.optimizationType = optimizationType; } public double getImprovementPercentage() { return improvementPercentage; } public void setImprovementPercentage(double improvementPercentage) { this.improvementPercentage = improvementPercentage; } public String getDescription() { return description; } public void setDescription(String description) { this.description = description; } }
    public static class DependencyGraphResult { private String resourceId; private String format; private LocalDateTime generatedAt; private List<GraphNode> nodes; private List<GraphEdge> edges; private String layout; public String getResourceId() { return resourceId; } public void setResourceId(String resourceId) { this.resourceId = resourceId; } public String getFormat() { return format; } public void setFormat(String format) { this.format = format; } public LocalDateTime getGeneratedAt() { return generatedAt; } public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; } public List<GraphNode> getNodes() { return nodes; } public void setNodes(List<GraphNode> nodes) { this.nodes = nodes; } public List<GraphEdge> getEdges() { return edges; } public void setEdges(List<GraphEdge> edges) { this.edges = edges; } public String getLayout() { return layout; } public void setLayout(String layout) { this.layout = layout; } }
    public static class GraphNode { private String id; private String type; private String label; public GraphNode(String id, String type, String label) { this.id = id; this.type = type; this.label = label; } public String getId() { return id; } public void setId(String id) { this.id = id; } public String getType() { return type; } public void setType(String type) { this.type = type; } public String getLabel() { return label; } public void setLabel(String label) { this.label = label; } }
    public static class GraphEdge { private String source; private String target; private String label; public GraphEdge(String source, String target, String label) { this.source = source; this.target = target; this.label = label; } public String getSource() { return source; } public void setSource(String source) { this.source = source; } public String getTarget() { return target; } public void setTarget(String target) { this.target = target; } public String getLabel() { return label; } public void setLabel(String label) { this.label = label; } }
    public static class DependencyValidationRequest { private List<String> resourceIds; public List<String> getResourceIds() { return resourceIds; } public void setResourceIds(List<String> resourceIds) { this.resourceIds = resourceIds; } }
    public static class DependencyValidationResult { private String validationId; private LocalDateTime validatedAt; private int resourceCount; private int totalChecks; private int passedChecks; private int failedChecks; private double validationScore; private List<String> issues; public String getValidationId() { return validationId; } public void setValidationId(String validationId) { this.validationId = validationId; } public LocalDateTime getValidatedAt() { return validatedAt; } public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; } public int getResourceCount() { return resourceCount; } public void setResourceCount(int resourceCount) { this.resourceCount = resourceCount; } public int getTotalChecks() { return totalChecks; } public void setTotalChecks(int totalChecks) { this.totalChecks = totalChecks; } public int getPassedChecks() { return passedChecks; } public void setPassedChecks(int passedChecks) { this.passedChecks = passedChecks; } public int getFailedChecks() { return failedChecks; } public void setFailedChecks(int failedChecks) { this.failedChecks = failedChecks; } public double getValidationScore() { return validationScore; } public void setValidationScore(double validationScore) { this.validationScore = validationScore; } public List<String> getIssues() { return issues; } public void setIssues(List<String> issues) { this.issues = issues; } }
}