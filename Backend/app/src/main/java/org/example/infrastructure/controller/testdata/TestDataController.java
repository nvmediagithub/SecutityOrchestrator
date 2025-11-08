package org.example.infrastructure.controller.testdata;

import org.example.domain.dto.testdata.GenerationRequest;
import org.example.domain.dto.testdata.GenerationResponse;
import org.example.domain.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

/**
 * REST Controller for test data generation endpoints
 * Handles: POST /api/test-data/generate/{scenarioId}
 */
@RestController
@RequestMapping("/api/test-data")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestDataController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataController.class);
    
    // Cache for tracking active generations
    private final Map<String, GenerationResponse> activeGenerations = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<GenerationResponse>> generationFutures = new ConcurrentHashMap<>();
    
    /**
     * Generate test data for a specific scenario
     * POST /api/test-data/generate/{scenarioId}
     */
    @PostMapping("/generate/{scenarioId}")
    public CompletableFuture<ResponseEntity<ApiResponse<GenerationResponse>>> generateTestData(
            @PathVariable String scenarioId,
            @Valid @RequestBody GenerationRequest request) {
        
        logger.info("Starting test data generation for scenario: {}, request: {}", 
                   scenarioId, request.getRequestId());
        
        try {
            // Validate scenario ID
            if (scenarioId == null || scenarioId.trim().isEmpty()) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "INVALID_SCENARIO_ID", "Scenario ID cannot be empty");
                ApiResponse<GenerationResponse> errorResponse = ApiResponse.error(
                    error, request.getRequestId());
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(errorResponse));
            }
            
            // Create generation response tracking
            GenerationResponse response = new GenerationResponse(request.getRequestId());
            response.setMessage("Test data generation started for scenario: " + scenarioId);
            response.setDataType(request.getDataType());
            response.setGenerationScope(request.getGenerationScope());
            response.setRequestedRecordCount(request.getRecordCount());
            response.setStartedAt(java.time.LocalDateTime.now());
            
            // Store in cache
            activeGenerations.put(request.getRequestId(), response);
            
            // Start generation process in background
            CompletableFuture<GenerationResponse> generationFuture = 
                processGenerationAsync(request, scenarioId);
            
            generationFutures.put(request.getRequestId(), generationFuture);
            
            // Return immediate response with generation ID
            ApiResponse<GenerationResponse> apiResponse = ApiResponse.success(
                response, request.getRequestId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.accepted().body(apiResponse));
                
        } catch (Exception e) {
            logger.error("Error starting test data generation for scenario: {}", scenarioId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "GENERATION_START_FAILED", 
                "Failed to start test data generation: " + e.getMessage());
            ApiResponse<GenerationResponse> errorResponse = ApiResponse.error(
                error, request.getRequestId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
        }
    }
    
    /**
     * Get generation status
     * GET /api/test-data/generate/status/{requestId}
     */
    @GetMapping("/generate/status/{requestId}")
    public ResponseEntity<ApiResponse<GenerationResponse>> getGenerationStatus(
            @PathVariable String requestId) {
        
        logger.debug("Checking generation status for request: {}", requestId);
        
        try {
            // Check if we have a cached response
            GenerationResponse cachedResponse = activeGenerations.get(requestId);
            if (cachedResponse != null) {
                ApiResponse<GenerationResponse> response = ApiResponse.success(
                    cachedResponse, requestId);
                return ResponseEntity.ok(response);
            }
            
            // Check if generation is still running
            CompletableFuture<GenerationResponse> future = generationFutures.get(requestId);
            if (future != null && !future.isDone()) {
                // Generation in progress - return pending response
                GenerationResponse pendingResponse = new GenerationResponse(requestId);
                pendingResponse.setMessage("Generation in progress");
                pendingResponse.setSuccess(false);
                
                ApiResponse<GenerationResponse> response = ApiResponse.success(
                    pendingResponse, requestId);
                return ResponseEntity.ok(response);
            }
            
            // Check if future completed and get result
            if (future != null && future.isDone()) {
                try {
                    GenerationResponse result = future.get(1, TimeUnit.SECONDS);
                    activeGenerations.put(requestId, result); // Cache the result
                    ApiResponse<GenerationResponse> response = ApiResponse.success(
                        result, requestId);
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    logger.error("Error getting generation result for request: {}", requestId, e);
                }
            }
            
            // Not found
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "GENERATION_NOT_FOUND", 
                "No generation found for request ID: " + requestId);
            ApiResponse<GenerationResponse> errorResponse = ApiResponse.error(
                error, requestId);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error checking generation status for request: {}", requestId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "STATUS_CHECK_FAILED", 
                "Failed to check generation status: " + e.getMessage());
            ApiResponse<GenerationResponse> errorResponse = ApiResponse.error(
                error, requestId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Cancel ongoing generation
     * DELETE /api/test-data/generate/{requestId}
     */
    @DeleteMapping("/generate/{requestId}")
    public ResponseEntity<ApiResponse<String>> cancelGeneration(@PathVariable String requestId) {
        
        logger.info("Cancelling generation for request: {}", requestId);
        
        try {
            // Remove from cache and futures
            GenerationResponse removed = activeGenerations.remove(requestId);
            CompletableFuture<GenerationResponse> future = generationFutures.remove(requestId);
            
            if (removed != null) {
                removed.setSuccess(false);
                removed.setMessage("Generation cancelled by user");
                removed.setCompletedAt(java.time.LocalDateTime.now());
            }
            
            if (future != null && !future.isDone()) {
                // In a real implementation, you would cancel the actual generation process
                // future.cancel(true);
            }
            
            ApiResponse<String> response = ApiResponse.success(
                requestId, requestId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cancelling generation for request: {}", requestId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "CANCELLATION_FAILED", 
                "Failed to cancel generation: " + e.getMessage());
            ApiResponse<String> errorResponse = ApiResponse.error(
                error, requestId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get generation history
     * GET /api/test-data/generate/history
     */
    @GetMapping("/generate/history")
    public ResponseEntity<ApiResponse<Map<String, GenerationResponse>>> getGenerationHistory(
            @RequestParam(defaultValue = "50") int limit) {
        
        logger.debug("Getting generation history with limit: {}", limit);
        
        try {
            // Return recent generations from cache
            Map<String, GenerationResponse> recentGenerations = new java.util.HashMap<>();
            
            activeGenerations.entrySet().stream()
                .limit(Math.max(1, Math.min(limit, 100)))
                .forEach(entry -> recentGenerations.put(entry.getKey(), entry.getValue()));
            
            ApiResponse<Map<String, GenerationResponse>> response = ApiResponse.success(
                recentGenerations, "history_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting generation history", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "HISTORY_RETRIEVAL_FAILED", 
                "Failed to retrieve generation history: " + e.getMessage());
            ApiResponse<Map<String, GenerationResponse>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get supported data types
     * GET /api/test-data/generate/supported-types
     */
    @GetMapping("/generate/supported-types")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSupportedDataTypes() {
        
        logger.debug("Getting supported data types");
        
        try {
            Map<String, String> supportedTypes = Map.of(
                "PERSONAL_DATA", "Personal information data (names, emails, addresses)",
                "FINANCIAL_DATA", "Financial information data (accounts, transactions)",
                "SECURITY_DATA", "Security-related data (passwords, tokens, keys)",
                "COMMERCIAL_DATA", "Business and commercial data (products, orders)",
                "SYSTEM_DATA", "System and technical data (logs, metrics, configs)"
            );
            
            ApiResponse<Map<String, String>> response = ApiResponse.success(
                supportedTypes, "types_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting supported data types", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "TYPES_RETRIEVAL_FAILED", 
                "Failed to retrieve supported data types: " + e.getMessage());
            ApiResponse<Map<String, String>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/test-data/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        
        logger.debug("Test data service health check");
        
        try {
            Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "TestDataController",
                "timestamp", java.time.Instant.now().toString(),
                "activeGenerations", activeGenerations.size(),
                "runningFutures", generationFutures.size()
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                health, "health_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error in health check", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "HEALTH_CHECK_FAILED", 
                "Health check failed: " + e.getMessage());
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Private helper methods
    
    private CompletableFuture<GenerationResponse> processGenerationAsync(GenerationRequest request, String scenarioId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GenerationResponse response = activeGenerations.get(request.getRequestId());
                if (response == null) {
                    response = new GenerationResponse(request.getRequestId());
                    activeGenerations.put(request.getRequestId(), response);
                }
                
                // Simulate generation time based on record count
                int delayMs = Math.min(request.getRecordCount() * 50, 10000); // Max 10 seconds
                Thread.sleep(delayMs);
                
                // Simulate successful generation
                response.setSuccess(true);
                response.setMessage("Test data generated successfully for scenario: " + scenarioId);
                response.setActualGeneratedCount(request.getRecordCount());
                response.setQualityScore(85.0);
                response.setValidated(true);
                response.setProvider("Simulated Generator");
                response.setCompletedAt(java.time.LocalDateTime.now());
                
                // Calculate execution time
                if (response.getStartedAt() != null) {
                    long executionTime = java.time.Duration.between(
                        response.getStartedAt(), 
                        response.getCompletedAt()).toMillis();
                    response.setExecutionTimeMs(executionTime);
                }
                
                // Add some sample metadata
                response.addMetadata("generator", "TestDataController");
                response.addMetadata("scenario", scenarioId);
                response.addMetadata("generationType", request.getDataType());
                response.addMetadata("qualityLevel", request.getQualityLevel());
                
                // Simulate data preview
                response.setDataPreview("[\n  {\"id\": 1, \"name\": \"Sample Record 1\", \"type\": \"" + 
                    request.getDataType() + "\"},\n  {\"id\": 2, \"name\": \"Sample Record 2\", \"type\": \"" + 
                    request.getDataType() + "\"}\n]");
                
                // Add some sample quality metrics
                response.addQualityMetric("completeness", 95.0);
                response.addQualityMetric("accuracy", 88.0);
                response.addQualityMetric("consistency", 92.0);
                response.addQualityMetric("uniqueness", 98.0);
                
                // Set memory usage simulation
                response.setMemoryUsedMB(request.getRecordCount() * 2L); // 2MB per 1000 records
                
                // Calculate records per second
                if (response.getExecutionTimeMs() > 0) {
                    response.setRecordsPerSecond((int) (request.getRecordCount() * 1000.0 / response.getExecutionTimeMs()));
                }
                
                // Add some warnings for large datasets
                if (request.getRecordCount() > 1000) {
                    response.addWarning("Large dataset generated - consider using bulk processing for better performance");
                }
                
                if ("BASIC".equalsIgnoreCase(request.getQualityLevel())) {
                    response.addWarning("Basic quality level used - consider STANDARD or higher for production testing");
                }
                
                logger.info("Generation completed for request: {}, records: {}", 
                           request.getRequestId(), request.getRecordCount());
                
                return response;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Generation interrupted for request: {}", request.getRequestId(), e);
                
                GenerationResponse errorResponse = activeGenerations.get(request.getRequestId());
                if (errorResponse == null) {
                    errorResponse = new GenerationResponse(request.getRequestId());
                    activeGenerations.put(request.getRequestId(), errorResponse);
                }
                
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Generation interrupted");
                errorResponse.addError("Generation was interrupted");
                errorResponse.setCompletedAt(java.time.LocalDateTime.now());
                
                return errorResponse;
                
            } catch (Exception e) {
                logger.error("Generation failed for request: {}", request.getRequestId(), e);
                
                GenerationResponse errorResponse = activeGenerations.get(request.getRequestId());
                if (errorResponse == null) {
                    errorResponse = new GenerationResponse(request.getRequestId());
                    activeGenerations.put(request.getRequestId(), errorResponse);
                }
                
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Generation failed: " + e.getMessage());
                errorResponse.addError(e.getMessage());
                errorResponse.setCompletedAt(java.time.LocalDateTime.now());
                
                return errorResponse;
            }
        });
    }
}