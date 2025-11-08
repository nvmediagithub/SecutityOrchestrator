package org.example.infrastructure.controller.testdata;

import org.example.domain.dto.testdata.BulkGenerationRequest;
import org.example.domain.dto.testdata.BulkGenerationResponse;
import org.example.domain.dto.testdata.GenerationRequest;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * REST Controller for bulk test data generation endpoints
 * Handles: POST /api/test-data/generate/bulk
 *          GET /api/test-data/generate/status/{batchId}
 */
@RestController
@RequestMapping("/api/test-data/generate")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataGenerationController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataGenerationController.class);
    
    // Cache for tracking active bulk generations
    private final Map<String, BulkGenerationResponse> activeBatches = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<BulkGenerationResponse>> batchFutures = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    /**
     * Start bulk test data generation
     * POST /api/test-data/generate/bulk
     */
    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<ApiResponse<BulkGenerationResponse>>> startBulkGeneration(
            @Valid @RequestBody BulkGenerationRequest request) {
        
        logger.info("Starting bulk generation for batch: {}, requests: {}", 
                   request.getBatchId(), request.getGenerationRequests().size());
        
        try {
            // Validate request
            if (request.getGenerationRequests() == null || request.getGenerationRequests().isEmpty()) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "EMPTY_REQUEST_LIST", "Generation requests list cannot be empty");
                ApiResponse<BulkGenerationResponse> errorResponse = ApiResponse.error(
                    error, request.getBatchId());
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(errorResponse));
            }
            
            // Validate total record count
            int totalRecords = calculateTotalRecords(request);
            if (totalRecords > 100000) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "TOO_MANY_RECORDS", "Total record count cannot exceed 100,000");
                ApiResponse<BulkGenerationResponse> errorResponse = ApiResponse.error(
                    error, request.getBatchId());
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(errorResponse));
            }
            
            // Create bulk response
            BulkGenerationResponse response = BulkGenerationResponse.accepted(request.getBatchId(), request);
            response.setTotalRequests(request.getGenerationRequests().size());
            response.setStartedAt(java.time.LocalDateTime.now());
            response.setEstimatedCompletionAt(calculateEstimatedCompletion(request));
            
            // Store in cache
            activeBatches.put(request.getBatchId(), response);
            
            // Start bulk processing
            CompletableFuture<BulkGenerationResponse> bulkFuture = 
                processBulkGenerationAsync(request);
            
            batchFutures.put(request.getBatchId(), bulkFuture);
            
            // Return immediate response
            ApiResponse<BulkGenerationResponse> apiResponse = ApiResponse.success(
                response, request.getBatchId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.accepted().body(apiResponse));
                
        } catch (Exception e) {
            logger.error("Error starting bulk generation for batch: {}", request.getBatchId(), e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "BULK_START_FAILED", 
                "Failed to start bulk generation: " + e.getMessage());
            ApiResponse<BulkGenerationResponse> errorResponse = ApiResponse.error(
                error, request.getBatchId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
        }
    }
    
    /**
     * Get bulk generation status
     * GET /api/test-data/generate/status/{batchId}
     */
    @GetMapping("/status/{batchId}")
    public ResponseEntity<ApiResponse<BulkGenerationResponse>> getBulkStatus(@PathVariable String batchId) {
        
        logger.debug("Checking bulk status for batch: {}", batchId);
        
        try {
            // Check if we have a cached response
            BulkGenerationResponse cachedResponse = activeBatches.get(batchId);
            if (cachedResponse != null) {
                // Update progress if still processing
                if (cachedResponse.isProcessing()) {
                    cachedResponse.updateProgress();
                }
                
                ApiResponse<BulkGenerationResponse> response = ApiResponse.success(
                    cachedResponse, batchId);
                return ResponseEntity.ok(response);
            }
            
            // Check if bulk processing is still running
            CompletableFuture<BulkGenerationResponse> future = batchFutures.get(batchId);
            if (future != null && !future.isDone()) {
                // Bulk processing in progress
                BulkGenerationResponse pendingResponse = new BulkGenerationResponse(batchId, "Pending Batch");
                pendingResponse.setMessage("Bulk processing in progress");
                pendingResponse.setStatus(BulkGenerationResponse.BatchStatus.PROCESSING);
                
                ApiResponse<BulkGenerationResponse> response = ApiResponse.success(
                    pendingResponse, batchId);
                return ResponseEntity.ok(response);
            }
            
            // Check if future completed and get result
            if (future != null && future.isDone()) {
                try {
                    BulkGenerationResponse result = future.get(1, TimeUnit.SECONDS);
                    activeBatches.put(batchId, result); // Cache the result
                    ApiResponse<BulkGenerationResponse> response = ApiResponse.success(
                        result, batchId);
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    logger.error("Error getting bulk result for batch: {}", batchId, e);
                }
            }
            
            // Not found
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "BATCH_NOT_FOUND", 
                "No bulk generation found for batch ID: " + batchId);
            ApiResponse<BulkGenerationResponse> errorResponse = ApiResponse.error(
                error, batchId);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error checking bulk status for batch: {}", batchId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "BULK_STATUS_CHECK_FAILED", 
                "Failed to check bulk status: " + e.getMessage());
            ApiResponse<BulkGenerationResponse> errorResponse = ApiResponse.error(
                error, batchId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Cancel bulk generation
     * DELETE /api/test-data/generate/bulk/{batchId}
     */
    @DeleteMapping("/bulk/{batchId}")
    public ResponseEntity<ApiResponse<String>> cancelBulkGeneration(@PathVariable String batchId) {
        
        logger.info("Cancelling bulk generation for batch: {}", batchId);
        
        try {
            // Remove from cache and futures
            BulkGenerationResponse removed = activeBatches.remove(batchId);
            CompletableFuture<BulkGenerationResponse> future = batchFutures.remove(batchId);
            
            if (removed != null) {
                removed.setStatus(BulkGenerationResponse.BatchStatus.CANCELLED);
                removed.setCompletedAt(java.time.LocalDateTime.now());
                removed.setMessage("Bulk generation cancelled by user");
            }
            
            if (future != null && !future.isDone()) {
                // Cancel the actual bulk processing
                // future.cancel(true);
            }
            
            ApiResponse<String> response = ApiResponse.success(
                batchId, batchId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cancelling bulk generation for batch: {}", batchId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "BULK_CANCELLATION_FAILED", 
                "Failed to cancel bulk generation: " + e.getMessage());
            ApiResponse<String> errorResponse = ApiResponse.error(
                error, batchId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get bulk generation history
     * GET /api/test-data/generate/bulk/history
     */
    @GetMapping("/bulk/history")
    public ResponseEntity<ApiResponse<Map<String, BulkGenerationResponse>>> getBulkHistory(
            @RequestParam(defaultValue = "20") int limit) {
        
        logger.debug("Getting bulk history with limit: {}", limit);
        
        try {
            // Return recent bulk generations from cache
            Map<String, BulkGenerationResponse> recentBatches = new java.util.HashMap<>();
            
            activeBatches.entrySet().stream()
                .limit(Math.max(1, Math.min(limit, 50)))
                .forEach(entry -> recentBatches.put(entry.getKey(), entry.getValue()));
            
            ApiResponse<Map<String, BulkGenerationResponse>> response = ApiResponse.success(
                recentBatches, "bulk_history_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting bulk history", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "BULK_HISTORY_RETRIEVAL_FAILED", 
                "Failed to retrieve bulk history: " + e.getMessage());
            ApiResponse<Map<String, BulkGenerationResponse>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get bulk processing capabilities
     * GET /api/test-data/generate/bulk/capabilities
     */
    @GetMapping("/bulk/capabilities")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBulkCapabilities() {
        
        logger.debug("Getting bulk processing capabilities");
        
        try {
            Map<String, Object> capabilities = Map.of(
                "maxBatchSize", 1000,
                "maxTotalRecords", 100000,
                "supportedProcessingModes", List.of("SEQUENTIAL", "PARALLEL", "HYBRID"),
                "maxParallelThreads", 20,
                "supportedPriorities", List.of("LOW", "NORMAL", "HIGH", "URGENT"),
                "maxTimeoutMinutes", 120,
                "supportsWebhooks", true,
                "supportsScheduling", true,
                "supportsRetry", true,
                "maxRetries", 5
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                capabilities, "capabilities_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting bulk capabilities", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "CAPABILITIES_RETRIEVAL_FAILED", 
                "Failed to retrieve capabilities: " + e.getMessage());
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Private helper methods
    
    private int calculateTotalRecords(BulkGenerationRequest request) {
        if (request.getGenerationRequests() == null) return 0;
        return request.getGenerationRequests().stream()
            .mapToInt(GenerationRequest::getRecordCount)
            .sum();
    }
    
    private java.time.LocalDateTime calculateEstimatedCompletion(BulkGenerationRequest request) {
        int totalRecords = calculateTotalRecords(request);
        // Estimate 100ms per record + overhead
        long estimatedMs = totalRecords * 100L + 5000L; // 5 second overhead
        return java.time.LocalDateTime.now().plus(java.time.Duration.ofMillis(estimatedMs));
    }
    
    private CompletableFuture<BulkGenerationResponse> processBulkGenerationAsync(BulkGenerationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BulkGenerationResponse response = activeBatches.get(request.getBatchId());
                if (response == null) {
                    response = BulkGenerationResponse.accepted(request.getBatchId(), request);
                    activeBatches.put(request.getBatchId(), response);
                }
                
                // Mark as started
                response.markStarted();
                response.updateProgress();
                
                List<GenerationRequest> requests = request.getGenerationRequests();
                int totalRequests = requests.size();
                int completedRequests = 0;
                int failedRequests = 0;
                int totalRecordsGenerated = 0;
                double totalQualityScore = 0.0;
                
                // Process each request
                for (int i = 0; i < requests.size(); i++) {
                    GenerationRequest individualRequest = requests.get(i);
                    
                    // Simulate processing time based on record count
                    int delayMs = Math.min(individualRequest.getRecordCount() * 20, 3000); // Max 3 seconds per request
                    Thread.sleep(delayMs);
                    
                    // Create item status
                    BulkGenerationResponse.BatchItemStatus itemStatus = 
                        new BulkGenerationResponse.BatchItemStatus(individualRequest.getRequestId());
                    itemStatus.setStartedAt(java.time.LocalDateTime.now());
                    itemStatus.setStatus(BulkGenerationResponse.BatchItemStatus.ItemStatus.PROCESSING);
                    
                    // Simulate success/failure
                    boolean success = random.nextDouble() > 0.1; // 90% success rate
                    
                    if (success) {
                        // Successful generation
                        itemStatus.setStatus(BulkGenerationResponse.BatchItemStatus.ItemStatus.COMPLETED);
                        itemStatus.setRecordsGenerated(individualRequest.getRecordCount());
                        itemStatus.setDataType(individualRequest.getDataType());
                        itemStatus.setQualityScore(75.0 + random.nextDouble() * 20.0); // 75-95
                        completedRequests++;
                        totalRecordsGenerated += individualRequest.getRecordCount();
                        totalQualityScore += itemStatus.getQualityScore();
                        
                        // Update summaries
                        response.addDataTypeSummary(individualRequest.getDataType(), individualRequest.getRecordCount());
                        response.addScopeSummary(individualRequest.getGenerationScope().toString(), individualRequest.getRecordCount());
                        
                    } else {
                        // Failed generation
                        itemStatus.setStatus(BulkGenerationResponse.BatchItemStatus.ItemStatus.FAILED);
                        itemStatus.setErrorMessage("Simulated generation failure");
                        failedRequests++;
                    }
                    
                    itemStatus.setCompletedAt(java.time.LocalDateTime.now());
                    response.addItemStatus(itemStatus);
                    
                    // Update progress
                    response.updateProgress();
                    
                    // Check if cancelled
                    if (response.getStatus() == BulkGenerationResponse.BatchStatus.CANCELLED) {
                        logger.info("Bulk generation cancelled for batch: {}", request.getBatchId());
                        break;
                    }
                }
                
                // Calculate final metrics
                response.setCompletedRequests(completedRequests);
                response.setFailedRequests(failedRequests);
                response.setTotalRecordsGenerated(totalRecordsGenerated);
                
                if (completedRequests > 0) {
                    response.setAverageQualityScore(totalQualityScore / completedRequests);
                }
                
                // Calculate performance metrics
                if (response.getCompletedAt() != null) {
                    long executionTime = java.time.Duration.between(
                        response.getStartedAt(), 
                        response.getCompletedAt()).toMillis();
                    response.setTotalExecutionTimeMs(executionTime);
                    
                    if (executionTime > 0) {
                        response.setRecordsPerSecond((int) (totalRecordsGenerated * 1000.0 / executionTime));
                    }
                }
                
                // Set resource usage
                response.setPeakParallelThreads(request.getParallelThreads());
                response.setMemoryUsedMB(totalRecordsGenerated * 3L); // 3MB per 1000 records
                
                // Set allocation info
                response.setCpuCores(request.getParallelThreads());
                response.setMemoryGB((long) Math.ceil(totalRecordsGenerated * 0.001)); // 1GB per 1000 records
                response.setExecutionEnvironment("bulk-processor-" + request.getProcessingMode().name().toLowerCase());
                
                // Generate sample file IDs
                for (int i = 0; i < Math.min(5, completedRequests); i++) {
                    response.addGeneratedFileId("file_" + UUID.randomUUID().toString() + ".json");
                }
                
                // Set URLs
                response.setDownloadUrl("/api/test-data/download/" + request.getBatchId());
                response.setReportUrl("/api/test-data/report/" + request.getBatchId());
                
                // Add some warnings
                if (failedRequests > 0) {
                    response.addWarning(failedRequests + " out of " + totalRequests + " requests failed");
                }
                
                if (totalRecordsGenerated > 10000) {
                    response.addWarning("Large dataset generated - consider using data streaming for better performance");
                }
                
                // Mark as completed
                response.markCompleted();
                
                logger.info("Bulk generation completed for batch: {}, records: {}, success rate: {}", 
                           request.getBatchId(), totalRecordsGenerated, 
                           String.format("%.1f%%", (completedRequests * 100.0 / totalRequests)));
                
                return response;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Bulk generation interrupted for batch: {}", request.getBatchId(), e);
                
                BulkGenerationResponse errorResponse = activeBatches.get(request.getBatchId());
                if (errorResponse == null) {
                    errorResponse = BulkGenerationResponse.accepted(request.getBatchId(), request);
                    activeBatches.put(request.getBatchId(), errorResponse);
                }
                
                errorResponse.setStatus(BulkGenerationResponse.BatchStatus.CANCELLED);
                errorResponse.setMessage("Bulk generation was interrupted");
                errorResponse.setCompletedAt(java.time.LocalDateTime.now());
                
                return errorResponse;
                
            } catch (Exception e) {
                logger.error("Bulk generation failed for batch: {}", request.getBatchId(), e);
                
                BulkGenerationResponse errorResponse = activeBatches.get(request.getBatchId());
                if (errorResponse == null) {
                    errorResponse = BulkGenerationResponse.accepted(request.getBatchId(), request);
                    activeBatches.put(request.getBatchId(), errorResponse);
                }
                
                errorResponse.setStatus(BulkGenerationResponse.BatchStatus.FAILED);
                errorResponse.setMessage("Bulk generation failed: " + e.getMessage());
                errorResponse.addError(e.getMessage());
                errorResponse.setCompletedAt(java.time.LocalDateTime.now());
                
                return errorResponse;
            }
        });
    }
}