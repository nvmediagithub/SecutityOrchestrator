package org.example.web.controllers;

import org.example.domain.entities.SecurityTest;
import org.example.domain.entities.SecurityFinding;
import org.example.domain.valueobjects.OwaspTestCategory;
import org.example.infrastructure.services.owasp.OwaspTestGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * REST API Controller for managing OWASP API Security tests
 * Provides endpoints for:
 * - Generating security tests from OpenAPI, BPMN, and LLM analysis
 * - Managing test categories and templates
 * - Executing tests and retrieving results
 * - Managing test findings and vulnerabilities
 */
@RestController
@RequestMapping("/api/test/security")
@CrossOrigin(origins = "*")
public class OwaspSecurityTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(OwaspSecurityTestController.class);
    
    private final OwaspTestGenerationService testGenerationService;
    
    @Autowired
    public OwaspSecurityTestController(OwaspTestGenerationService testGenerationService) {
        this.testGenerationService = testGenerationService;
    }
    
    /**
     * Generate security tests for OpenAPI specification
     */
    @PostMapping("/generate/openapi")
    public CompletableFuture<ResponseEntity<SecurityTestGenerationResponse>> generateOpenApiTests(
            @RequestBody SecurityTestGenerationRequest request) {
        
        logger.info("Received request to generate OpenAPI security tests: {}", request);
        
        return testGenerationService.generateOpenApiTests(request.getTargetId(), request.getUserId())
                .thenApply(tests -> {
                    SecurityTestGenerationResponse response = new SecurityTestGenerationResponse();
                    response.setMessage("Security tests generated successfully");
                    response.setTargetId(request.getTargetId());
                    response.setTestType("OpenAPI");
                    response.setGeneratedTests(tests.size());
                    response.setTests(tests);
                    response.setTimestamp(new Date());
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Error generating OpenAPI security tests", throwable);
                    SecurityTestGenerationResponse errorResponse = new SecurityTestGenerationResponse();
                    errorResponse.setMessage("Error generating security tests: " + throwable.getMessage());
                    errorResponse.setTimestamp(new Date());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
    
    /**
     * Generate security tests for BPMN process
     */
    @PostMapping("/generate/bpmn")
    public CompletableFuture<ResponseEntity<SecurityTestGenerationResponse>> generateBpmnTests(
            @RequestBody SecurityTestGenerationRequest request) {
        
        logger.info("Received request to generate BPMN security tests: {}", request);
        
        return testGenerationService.generateBpmnTests(request.getTargetId(), request.getUserId())
                .thenApply(tests -> {
                    SecurityTestGenerationResponse response = new SecurityTestGenerationResponse();
                    response.setMessage("BPMN security tests generated successfully");
                    response.setTargetId(request.getTargetId());
                    response.setTestType("BPMN");
                    response.setGeneratedTests(tests.size());
                    response.setTests(tests);
                    response.setTimestamp(new Date());
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Error generating BPMN security tests", throwable);
                    SecurityTestGenerationResponse errorResponse = new SecurityTestGenerationResponse();
                    errorResponse.setMessage("Error generating BPMN security tests: " + throwable.getMessage());
                    errorResponse.setTimestamp(new Date());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
    
    /**
     * Generate security tests based on LLM analysis
     */
    @PostMapping("/generate/llm")
    public CompletableFuture<ResponseEntity<SecurityTestGenerationResponse>> generateLLMTests(
            @RequestBody LLMTestGenerationRequest request) {
        
        logger.info("Received request to generate LLM-based security tests: {}", request);
        
        return testGenerationService.generateLLMBasedTests(request.getAnalysisId(), request.getCategories(), request.getUserId())
                .thenApply(tests -> {
                    SecurityTestGenerationResponse response = new SecurityTestGenerationResponse();
                    response.setMessage("LLM-based security tests generated successfully");
                    response.setTargetId(request.getAnalysisId());
                    response.setTestType("LLM");
                    response.setGeneratedTests(tests.size());
                    response.setTests(tests);
                    response.setTimestamp(new Date());
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Error generating LLM security tests", throwable);
                    SecurityTestGenerationResponse errorResponse = new SecurityTestGenerationResponse();
                    errorResponse.setMessage("Error generating LLM security tests: " + throwable.getMessage());
                    errorResponse.setTimestamp(new Date());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
    
    /**
     * Generate comprehensive test suite combining all sources
     */
    @PostMapping("/generate/comprehensive")
    public CompletableFuture<ResponseEntity<SecurityTestGenerationResponse>> generateComprehensiveTests(
            @RequestBody ComprehensiveTestGenerationRequest request) {
        
        logger.info("Received request to generate comprehensive test suite: {}", request);
        
        return testGenerationService.generateComprehensiveTestSuite(
                request.getOpenApiSpecId(), 
                request.getBpmnProcessId(), 
                request.getLlmAnalysisId(), 
                request.getUserId())
                .thenApply(tests -> {
                    SecurityTestGenerationResponse response = new SecurityTestGenerationResponse();
                    response.setMessage("Comprehensive test suite generated successfully");
                    response.setTargetId("comprehensive");
                    response.setTestType("Comprehensive");
                    response.setGeneratedTests(tests.size());
                    response.setTests(tests);
                    response.setTimestamp(new Date());
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Error generating comprehensive test suite", throwable);
                    SecurityTestGenerationResponse errorResponse = new SecurityTestGenerationResponse();
                    errorResponse.setMessage("Error generating comprehensive test suite: " + throwable.getMessage());
                    errorResponse.setTimestamp(new Date());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
    
    /**
     * Generate targeted tests for specific OWASP category
     */
    @PostMapping("/generate/category")
    public CompletableFuture<ResponseEntity<SecurityTestGenerationResponse>> generateCategoryTests(
            @RequestBody CategoryTestGenerationRequest request) {
        
        logger.info("Received request to generate category-specific tests: {}", request);
        
        return testGenerationService.generateCategoryTests(request.getOpenApiSpecId(), request.getCategory(), request.getUserId())
                .thenApply(tests -> {
                    SecurityTestGenerationResponse response = new SecurityTestGenerationResponse();
                    response.setMessage("Category-specific tests generated successfully");
                    response.setTargetId(request.getOpenApiSpecId());
                    response.setTestType("Category: " + request.getCategory().getCategoryCode());
                    response.setGeneratedTests(tests.size());
                    response.setTests(tests);
                    response.setTimestamp(new Date());
                    
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    logger.error("Error generating category tests", throwable);
                    SecurityTestGenerationResponse errorResponse = new SecurityTestGenerationResponse();
                    errorResponse.setMessage("Error generating category tests: " + throwable.getMessage());
                    errorResponse.setTimestamp(new Date());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
    
    /**
     * Get all OWASP API Security categories
     */
    @GetMapping("/categories")
    public ResponseEntity<OwaspCategoriesResponse> getOwaspCategories() {
        logger.info("Retrieving OWASP API Security categories");
        
        try {
            List<OwaspCategoryInfo> categories = Arrays.stream(OwaspTestCategory.values())
                    .map(category -> {
                        OwaspCategoryInfo info = new OwaspCategoryInfo();
                        info.setCategoryCode(category.getCategoryCode());
                        info.setCategoryName(category.getCategoryName());
                        info.setDescription(category.getDescription());
                        info.setTestTypes(category.getTestTypes());
                        return info;
                    })
                    .collect(Collectors.toList());
            
            OwaspCategoriesResponse response = new OwaspCategoriesResponse();
            response.setMessage("OWASP categories retrieved successfully");
            response.setCategories(categories);
            response.setTimestamp(new Date());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving OWASP categories", e);
            OwaspCategoriesResponse errorResponse = new OwaspCategoriesResponse();
            errorResponse.setMessage("Error retrieving categories: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get all security tests (placeholder - will be implemented with repository)
     */
    @GetMapping("/tests")
    public ResponseEntity<SecurityTestsListResponse> getAllTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        logger.info("Retrieving security tests - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        
        try {
            // Placeholder implementation
            SecurityTestsListResponse response = new SecurityTestsListResponse();
            response.setMessage("Security tests retrieved successfully");
            response.setTests(new ArrayList<>());
            response.setTotalCount(0);
            response.setPage(page);
            response.setSize(size);
            response.setSortBy(sortBy);
            response.setTimestamp(new Date());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving security tests", e);
            SecurityTestsListResponse errorResponse = new SecurityTestsListResponse();
            errorResponse.setMessage("Error retrieving security tests: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get test generation statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<TestStatsResponse> getTestStats(@RequestParam String userId) {
        logger.info("Retrieving test generation statistics for user: {}", userId);
        
        try {
            Map<String, Object> stats = testGenerationService.getTestGenerationStats(userId);
            
            TestStatsResponse response = new TestStatsResponse();
            response.setMessage("Test statistics retrieved successfully");
            response.setStats(stats);
            response.setTimestamp(new Date());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving test statistics", e);
            TestStatsResponse errorResponse = new TestStatsResponse();
            errorResponse.setMessage("Error retrieving test statistics: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Execute a specific security test
     */
    @PostMapping("/{testId}/execute")
    public CompletableFuture<ResponseEntity<TestExecutionResponse>> executeTest(@PathVariable Long testId) {
        logger.info("Executing security test with ID: {}", testId);
        
        // Placeholder implementation
        CompletableFuture<TestExecutionResponse> future = CompletableFuture.supplyAsync(() -> {
            TestExecutionResponse response = new TestExecutionResponse();
            response.setTestId(testId);
            response.setStatus("QUEUED");
            response.setMessage("Test execution started");
            response.setTimestamp(new Date());
            return response;
        });
        
        return future.thenApply(response -> ResponseEntity.ok(response))
                .exceptionally(throwable -> {
                    logger.error("Error executing test", throwable);
                    TestExecutionResponse errorResponse = new TestExecutionResponse();
                    errorResponse.setTestId(testId);
                    errorResponse.setStatus("ERROR");
                    errorResponse.setMessage("Error executing test: " + throwable.getMessage());
                    errorResponse.setTimestamp(new Date());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
    
    /**
     * Get test results
     */
    @GetMapping("/{testId}/results")
    public ResponseEntity<TestResultsResponse> getTestResults(@PathVariable Long testId) {
        logger.info("Retrieving results for test ID: {}", testId);
        
        try {
            // Placeholder implementation
            TestResultsResponse response = new TestResultsResponse();
            response.setTestId(testId);
            response.setResults(new ArrayList<>());
            response.setStatus("PENDING");
            response.setMessage("Test results retrieved successfully");
            response.setTimestamp(new Date());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving test results", e);
            TestResultsResponse errorResponse = new TestResultsResponse();
            errorResponse.setTestId(testId);
            errorResponse.setStatus("ERROR");
            errorResponse.setMessage("Error retrieving test results: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "OWASP Security Test Service");
        health.put("timestamp", new Date());
        return ResponseEntity.ok(health);
    }
    
    // Request and Response DTO classes (to be defined in separate files)
    
    public static class SecurityTestGenerationRequest {
        private String targetId;
        private String userId;
        
        public String getTargetId() { return targetId; }
        public void setTargetId(String targetId) { this.targetId = targetId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public static class LLMTestGenerationRequest {
        private String analysisId;
        private List<OwaspTestCategory> categories;
        private String userId;
        
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
        public List<OwaspTestCategory> getCategories() { return categories; }
        public void setCategories(List<OwaspTestCategory> categories) { this.categories = categories; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public static class ComprehensiveTestGenerationRequest {
        private String openApiSpecId;
        private String bpmnProcessId;
        private String llmAnalysisId;
        private String userId;
        
        public String getOpenApiSpecId() { return openApiSpecId; }
        public void setOpenApiSpecId(String openApiSpecId) { this.openApiSpecId = openApiSpecId; }
        public String getBpmnProcessId() { return bpmnProcessId; }
        public void setBpmnProcessId(String bpmnProcessId) { this.bpmnProcessId = bpmnProcessId; }
        public String getLlmAnalysisId() { return llmAnalysisId; }
        public void setLlmAnalysisId(String llmAnalysisId) { this.llmAnalysisId = llmAnalysisId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public static class CategoryTestGenerationRequest {
        private String openApiSpecId;
        private OwaspTestCategory category;
        private String userId;
        
        public String getOpenApiSpecId() { return openApiSpecId; }
        public void setOpenApiSpecId(String openApiSpecId) { this.openApiSpecId = openApiSpecId; }
        public OwaspTestCategory getCategory() { return category; }
        public void setCategory(OwaspTestCategory category) { this.category = category; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public static class SecurityTestGenerationResponse {
        private String message;
        private String targetId;
        private String testType;
        private int generatedTests;
        private List<SecurityTest> tests;
        private Date timestamp;
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTargetId() { return targetId; }
        public void setTargetId(String targetId) { this.targetId = targetId; }
        public String getTestType() { return testType; }
        public void setTestType(String testType) { this.testType = testType; }
        public int getGeneratedTests() { return generatedTests; }
        public void setGeneratedTests(int generatedTests) { this.generatedTests = generatedTests; }
        public List<SecurityTest> getTests() { return tests; }
        public void setTests(List<SecurityTest> tests) { this.tests = tests; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }
    
    public static class OwaspCategoriesResponse {
        private String message;
        private List<OwaspCategoryInfo> categories;
        private Date timestamp;
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<OwaspCategoryInfo> getCategories() { return categories; }
        public void setCategories(List<OwaspCategoryInfo> categories) { this.categories = categories; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }
    
    public static class OwaspCategoryInfo {
        private String categoryCode;
        private String categoryName;
        private String description;
        private List<String> testTypes;
        
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getTestTypes() { return testTypes; }
        public void setTestTypes(List<String> testTypes) { this.testTypes = testTypes; }
    }
    
    public static class SecurityTestsListResponse {
        private String message;
        private List<SecurityTest> tests;
        private int totalCount;
        private int page;
        private int size;
        private String sortBy;
        private Date timestamp;
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<SecurityTest> getTests() { return tests; }
        public void setTests(List<SecurityTest> tests) { this.tests = tests; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }
    
    public static class TestStatsResponse {
        private String message;
        private Map<String, Object> stats;
        private Date timestamp;
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getStats() { return stats; }
        public void setStats(Map<String, Object> stats) { this.stats = stats; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }
    
    public static class TestExecutionResponse {
        private Long testId;
        private String status;
        private String message;
        private Date timestamp;
        
        public Long getTestId() { return testId; }
        public void setTestId(Long testId) { this.testId = testId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }
    
    public static class TestResultsResponse {
        private Long testId;
        private String status;
        private List<SecurityFinding> results;
        private String message;
        private Date timestamp;
        
        public Long getTestId() { return testId; }
        public void setTestId(Long testId) { this.testId = testId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<SecurityFinding> getResults() { return results; }
        public void setResults(List<SecurityFinding> results) { this.results = results; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }
}
