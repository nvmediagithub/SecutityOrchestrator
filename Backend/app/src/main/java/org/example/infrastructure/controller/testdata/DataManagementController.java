package org.example.infrastructure.controller.testdata;

import org.example.domain.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

/**
 * REST Controller for data management endpoints
 * Handles: POST /api/data/sync
 *          POST /api/data/cleanup
 *          GET /api/data/profiles
 *          GET /api/data/templates
 */
@RestController
@RequestMapping("/api/data")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataManagementController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataManagementController.class);
    
    // Cache for data management operations
    private final Map<String, DataManagementResult> operationCache = new ConcurrentHashMap<>();
    
    // Mock data stores
    private final List<DataGeneratorProfile> profiles = new ArrayList<>();
    private final List<DataTemplate> templates = new ArrayList<>();
    private final Random random = new Random();
    
    public DataManagementController() {
        // Initialize with sample profiles and templates
        initializeSampleData();
    }
    
    /**
     * Synchronize test data
     * POST /api/data/sync
     */
    @PostMapping("/sync")
    public CompletableFuture<ResponseEntity<ApiResponse<DataManagementResult>>> syncData(@RequestBody SyncRequest request) {
        
        logger.info("Starting data synchronization, operation: {}", request.getOperationId());
        
        try {
            // Validate request
            if (request.getOperationId() == null || request.getOperationId().trim().isEmpty()) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "INVALID_OPERATION_ID", "Operation ID cannot be empty");
                ApiResponse<DataManagementResult> errorResponse = ApiResponse.error(
                    error, "sync_" + UUID.randomUUID().toString());
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(errorResponse));
            }
            
            // Start sync process
            CompletableFuture<DataManagementResult> syncFuture = processDataSync(request);
            
            // Return immediate response
            DataManagementResult immediateResult = new DataManagementResult();
            immediateResult.setOperationId(request.getOperationId());
            immediateResult.setOperationType("SYNC");
            immediateResult.setStatus("STARTED");
            immediateResult.setStartedAt(LocalDateTime.now());
            immediateResult.setMessage("Data synchronization started");
            
            ApiResponse<DataManagementResult> apiResponse = ApiResponse.success(
                immediateResult, request.getOperationId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.accepted().body(apiResponse));
                
        } catch (Exception e) {
            logger.error("Error starting data synchronization", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "SYNC_START_FAILED", 
                "Failed to start synchronization: " + e.getMessage());
            ApiResponse<DataManagementResult> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
        }
    }
    
    /**
     * Clean up test data
     * POST /api/data/cleanup
     */
    @PostMapping("/cleanup")
    public CompletableFuture<ResponseEntity<ApiResponse<DataManagementResult>>> cleanupData(@RequestBody CleanupRequest request) {
        
        logger.info("Starting data cleanup, operation: {}", request.getOperationId());
        
        try {
            // Validate request
            if (request.getOperationId() == null || request.getOperationId().trim().isEmpty()) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "INVALID_OPERATION_ID", "Operation ID cannot be empty");
                ApiResponse<DataManagementResult> errorResponse = ApiResponse.error(
                    error, "cleanup_" + UUID.randomUUID().toString());
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(errorResponse));
            }
            
            // Start cleanup process
            CompletableFuture<DataManagementResult> cleanupFuture = processDataCleanup(request);
            
            // Return immediate response
            DataManagementResult immediateResult = new DataManagementResult();
            immediateResult.setOperationId(request.getOperationId());
            immediateResult.setOperationType("CLEANUP");
            immediateResult.setStatus("STARTED");
            immediateResult.setStartedAt(LocalDateTime.now());
            immediateResult.setMessage("Data cleanup started");
            
            ApiResponse<DataManagementResult> apiResponse = ApiResponse.success(
                immediateResult, request.getOperationId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.accepted().body(apiResponse));
                
        } catch (Exception e) {
            logger.error("Error starting data cleanup", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "CLEANUP_START_FAILED", 
                "Failed to start cleanup: " + e.getMessage());
            ApiResponse<DataManagementResult> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
        }
    }
    
    /**
     * Get data generator profiles
     * GET /api/data/profiles
     */
    @GetMapping("/profiles")
    public ResponseEntity<ApiResponse<List<DataGeneratorProfile>>> getProfiles(
            @RequestParam(defaultValue = "all") String generatorType,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        
        logger.debug("Getting profiles for type: {}, activeOnly: {}", generatorType, activeOnly);
        
        try {
            // Filter profiles
            List<DataGeneratorProfile> filteredProfiles = profiles.stream()
                .filter(p -> "all".equalsIgnoreCase(generatorType) || 
                           generatorType.equalsIgnoreCase(p.getGeneratorType()))
                .filter(p -> !activeOnly || p.getIsActive())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            ApiResponse<List<DataGeneratorProfile>> response = ApiResponse.success(
                filteredProfiles, "profiles_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting profiles", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "PROFILES_RETRIEVAL_FAILED", 
                "Failed to retrieve profiles: " + e.getMessage());
            ApiResponse<List<DataGeneratorProfile>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get data templates
     * GET /api/data/templates
     */
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<DataTemplate>>> getTemplates(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        
        logger.debug("Getting templates for category: {}, activeOnly: {}", category, activeOnly);
        
        try {
            // Filter templates
            List<DataTemplate> filteredTemplates = templates.stream()
                .filter(t -> "all".equalsIgnoreCase(category) || 
                           category.equalsIgnoreCase(t.getCategory()))
                .filter(t -> !activeOnly || t.getIsActive())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            ApiResponse<List<DataTemplate>> response = ApiResponse.success(
                filteredTemplates, "templates_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting templates", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "TEMPLATES_RETRIEVAL_FAILED", 
                "Failed to retrieve templates: " + e.getMessage());
            ApiResponse<List<DataTemplate>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get specific profile by ID
     * GET /api/data/profiles/{profileId}
     */
    @GetMapping("/profiles/{profileId}")
    public ResponseEntity<ApiResponse<DataGeneratorProfile>> getProfileById(@PathVariable String profileId) {
        
        logger.debug("Getting profile by ID: {}", profileId);
        
        try {
            DataGeneratorProfile profile = profiles.stream()
                .filter(p -> profileId.equals(p.getProfileId()))
                .findFirst()
                .orElse(null);
            
            if (profile == null) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "PROFILE_NOT_FOUND", "Profile not found: " + profileId);
                ApiResponse<DataGeneratorProfile> errorResponse = ApiResponse.error(
                    error, profileId);
                return ResponseEntity.notFound().build();
            }
            
            ApiResponse<DataGeneratorProfile> response = ApiResponse.success(
                profile, profileId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting profile by ID: {}", profileId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "PROFILE_RETRIEVAL_FAILED", 
                "Failed to retrieve profile: " + e.getMessage());
            ApiResponse<DataGeneratorProfile> errorResponse = ApiResponse.error(
                error, profileId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get specific template by ID
     * GET /api/data/templates/{templateId}
     */
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<DataTemplate>> getTemplateById(@PathVariable String templateId) {
        
        logger.debug("Getting template by ID: {}", templateId);
        
        try {
            DataTemplate template = templates.stream()
                .filter(t -> templateId.equals(t.getTemplateId()))
                .findFirst()
                .orElse(null);
            
            if (template == null) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "TEMPLATE_NOT_FOUND", "Template not found: " + templateId);
                ApiResponse<DataTemplate> errorResponse = ApiResponse.error(
                    error, templateId);
                return ResponseEntity.notFound().build();
            }
            
            ApiResponse<DataTemplate> response = ApiResponse.success(
                template, templateId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting template by ID: {}", templateId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "TEMPLATE_RETRIEVAL_FAILED", 
                "Failed to retrieve template: " + e.getMessage());
            ApiResponse<DataTemplate> errorResponse = ApiResponse.error(
                error, templateId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get operation status
     * GET /api/data/operations/{operationId}
     */
    @GetMapping("/operations/{operationId}")
    public ResponseEntity<ApiResponse<DataManagementResult>> getOperationStatus(@PathVariable String operationId) {
        
        logger.debug("Getting operation status for: {}", operationId);
        
        try {
            DataManagementResult result = operationCache.get(operationId);
            
            if (result == null) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "OPERATION_NOT_FOUND", "Operation not found: " + operationId);
                ApiResponse<DataManagementResult> errorResponse = ApiResponse.error(
                    error, operationId);
                return ResponseEntity.notFound().build();
            }
            
            ApiResponse<DataManagementResult> response = ApiResponse.success(
                result, operationId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting operation status: {}", operationId, e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "STATUS_CHECK_FAILED", 
                "Failed to get operation status: " + e.getMessage());
            ApiResponse<DataManagementResult> errorResponse = ApiResponse.error(
                error, operationId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get data management capabilities
     * GET /api/data/management/capabilities
     */
    @GetMapping("/management/capabilities")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getManagementCapabilities() {
        
        logger.debug("Getting data management capabilities");
        
        try {
            Map<String, Object> capabilities = Map.of(
                "syncOperations", Arrays.asList("FULL_SYNC", "INCREMENTAL_SYNC", "DEPENDENCY_SYNC"),
                "cleanupPolicies", Arrays.asList("RETENTION_BASED", "SIZE_BASED", "TIME_BASED", "MANUAL"),
                "supportedFormats", Arrays.asList("JSON", "XML", "CSV", "YAML", "SQL"),
                "storageBackends", Arrays.asList("FILE_SYSTEM", "DATABASE", "CLOUD_STORAGE"),
                "maxRetentionDays", 365,
                "compressionSupported", true,
                "encryptionSupported", true,
                "backupSupported", true,
                "archiveSupported", true
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                capabilities, "capabilities_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting management capabilities", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "CAPABILITIES_RETRIEVAL_FAILED", 
                "Failed to retrieve capabilities: " + e.getMessage());
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Private helper methods
    
    private void initializeSampleData() {
        // Sample profiles
        DataGeneratorProfile basicProfile = new DataGeneratorProfile();
        basicProfile.setProfileId("profile_basic");
        basicProfile.setName("Basic Data Generator");
        basicProfile.setDescription("Standard profile for basic test data generation");
        basicProfile.setGeneratorType("BASIC");
        basicProfile.setMaxDataRecords(1000);
        basicProfile.setQualityLevel("STANDARD");
        basicProfile.setIsActive(true);
        basicProfile.setSupportedDataTypes(Arrays.asList("PERSONAL_DATA", "SYSTEM_DATA"));
        basicProfile.setCreatedAt(LocalDateTime.now().minusDays(30));
        profiles.add(basicProfile);
        
        DataGeneratorProfile advancedProfile = new DataGeneratorProfile();
        advancedProfile.setProfileId("profile_advanced");
        advancedProfile.setName("Advanced Data Generator");
        advancedProfile.setDescription("Advanced profile with LLM integration");
        advancedProfile.setGeneratorType("LLM");
        advancedProfile.setMaxDataRecords(10000);
        advancedProfile.setQualityLevel("PREMIUM");
        advancedProfile.setIsActive(true);
        advancedProfile.setSupportedDataTypes(Arrays.asList("PERSONAL_DATA", "FINANCIAL_DATA", "SECURITY_DATA"));
        advancedProfile.setCreatedAt(LocalDateTime.now().minusDays(15));
        profiles.add(advancedProfile);
        
        // Sample templates
        DataTemplate userTemplate = new DataTemplate();
        userTemplate.setTemplateId("template_user");
        userTemplate.setName("User Data Template");
        userTemplate.setDescription("Template for generating user-related test data");
        userTemplate.setCategory("USER_DATA");
        userTemplate.setDataType("PERSONAL_DATA");
        userTemplate.setIsActive(true);
        userTemplate.setTemplateContent("{\\\"users\\\": [{\\\"id\\\": 1, \\\"name\\\": \\\"{{name}}\\\", \\\"email\\\": \\\"{{email}}\\\"}]}");
        userTemplate.setVariables(Arrays.asList("name", "email", "phone", "address"));
        templates.add(userTemplate);
        
        DataTemplate transactionTemplate = new DataTemplate();
        transactionTemplate.setTemplateId("template_transaction");
        transactionTemplate.setName("Transaction Data Template");
        transactionTemplate.setDescription("Template for financial transaction data");
        transactionTemplate.setCategory("FINANCIAL_DATA");
        transactionTemplate.setDataType("FINANCIAL_DATA");
        transactionTemplate.setIsActive(true);
        transactionTemplate.setTemplateContent("{\\\"transactions\\\": [{\\\"id\\\": 1, \\\"amount\\\": \\\"{{amount}}\\\", \\\"currency\\\": \\\"{{currency}}\\\"}]}");
        transactionTemplate.setVariables(Arrays.asList("amount", "currency", "date", "account"));
        templates.add(transactionTemplate);
    }
    
    private CompletableFuture<DataManagementResult> processDataSync(SyncRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DataManagementResult result = new DataManagementResult();
                result.setOperationId(request.getOperationId());
                result.setOperationType("SYNC");
                result.setStatus("PROCESSING");
                result.setStartedAt(LocalDateTime.now());
                result.setMessage("Processing data synchronization");
                
                // Simulate sync process
                int totalRecords = 1000 + random.nextInt(9000); // 1000-10000 records
                int syncedRecords = 0;
                int failedRecords = 0;
                
                // Simulate progress
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(200); // Simulate processing time
                        int progress = (i + 1) * 10;
                        result.setProgress(progress);
                        result.setMessage("Synchronizing data... " + progress + "%");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // Final results
                syncedRecords = (int) (totalRecords * 0.95); // 95% success rate
                failedRecords = totalRecords - syncedRecords;
                
                result.setStatus("COMPLETED");
                result.setProgress(100);
                result.setCompletedAt(LocalDateTime.now());
                result.setMessage("Data synchronization completed");
                result.setTotalRecords(totalRecords);
                result.setProcessedRecords(syncedRecords);
                result.setFailedRecords(failedRecords);
                result.setExecutionTimeMs(2000); // 2 seconds
                
                // Add results
                Map<String, Object> syncResults = new HashMap<>();
                syncResults.put("syncedRecords", syncedRecords);
                syncResults.put("failedRecords", failedRecords);
                syncResults.put("successRate", String.format("%.1f%%", (syncedRecords * 100.0 / totalRecords)));
                result.setResults(syncResults);
                
                // Cache the result
                operationCache.put(request.getOperationId(), result);
                
                logger.info("Data sync completed for operation: {}, synced: {}/{}", 
                           request.getOperationId(), syncedRecords, totalRecords);
                
                return result;
                
            } catch (Exception e) {
                logger.error("Data sync failed for operation: {}", request.getOperationId(), e);
                
                DataManagementResult errorResult = new DataManagementResult();
                errorResult.setOperationId(request.getOperationId());
                errorResult.setOperationType("SYNC");
                errorResult.setStatus("FAILED");
                errorResult.setStartedAt(LocalDateTime.now());
                errorResult.setCompletedAt(LocalDateTime.now());
                errorResult.setMessage("Data synchronization failed: " + e.getMessage());
                errorResult.addError(e.getMessage());
                
                operationCache.put(request.getOperationId(), errorResult);
                return errorResult;
            }
        });
    }
    
    private CompletableFuture<DataManagementResult> processDataCleanup(CleanupRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DataManagementResult result = new DataManagementResult();
                result.setOperationId(request.getOperationId());
                result.setOperationType("CLEANUP");
                result.setStatus("PROCESSING");
                result.setStartedAt(LocalDateTime.now());
                result.setMessage("Processing data cleanup");
                
                // Simulate cleanup process
                int totalRecords = 500 + random.nextInt(4500); // 500-5000 records
                int cleanedRecords = 0;
                int archivedRecords = 0;
                
                // Simulate progress
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(150); // Simulate processing time
                        int progress = (i + 1) * 10;
                        result.setProgress(progress);
                        result.setMessage("Cleaning up data... " + progress + "%");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // Final results
                cleanedRecords = (int) (totalRecords * 0.7); // 70% cleaned
                archivedRecords = (int) (totalRecords * 0.2); // 20% archived
                int deletedRecords = totalRecords - cleanedRecords - archivedRecords;
                
                result.setStatus("COMPLETED");
                result.setProgress(100);
                result.setCompletedAt(LocalDateTime.now());
                result.setMessage("Data cleanup completed");
                result.setTotalRecords(totalRecords);
                result.setProcessedRecords(cleanedRecords + archivedRecords + deletedRecords);
                result.setExecutionTimeMs(1500); // 1.5 seconds
                
                // Add results
                Map<String, Object> cleanupResults = new HashMap<>();
                cleanupResults.put("cleanedRecords", cleanedRecords);
                cleanupResults.put("archivedRecords", archivedRecords);
                cleanupResults.put("deletedRecords", deletedRecords);
                cleanupResults.put("freedSpaceMB", totalRecords * 2); // 2MB per record
                result.setResults(cleanupResults);
                
                // Cache the result
                operationCache.put(request.getOperationId(), result);
                
                logger.info("Data cleanup completed for operation: {}, cleaned: {}, archived: {}, deleted: {}", 
                           request.getOperationId(), cleanedRecords, archivedRecords, deletedRecords);
                
                return result;
                
            } catch (Exception e) {
                logger.error("Data cleanup failed for operation: {}", request.getOperationId(), e);
                
                DataManagementResult errorResult = new DataManagementResult();
                errorResult.setOperationId(request.getOperationId());
                errorResult.setOperationType("CLEANUP");
                errorResult.setStatus("FAILED");
                errorResult.setStartedAt(LocalDateTime.now());
                errorResult.setCompletedAt(LocalDateTime.now());
                errorResult.setMessage("Data cleanup failed: " + e.getMessage());
                errorResult.addError(e.getMessage());
                
                operationCache.put(request.getOperationId(), errorResult);
                return errorResult;
            }
        });
    }
    
    // Request and Result classes
    
    public static class SyncRequest {
        private String operationId;
        private String syncType; // FULL_SYNC, INCREMENTAL_SYNC, DEPENDENCY_SYNC
        private String scope; // GLOBAL, PROJECT, MODULE
        private List<String> dataTypes;
        private boolean includeDependencies;
        private boolean updateLinks;
        private String backupLocation;
        
        public SyncRequest() {}
        
        // Getters and Setters
        public String getOperationId() { return operationId; }
        public void setOperationId(String operationId) { this.operationId = operationId; }
        
        public String getSyncType() { return syncType; }
        public void setSyncType(String syncType) { this.syncType = syncType; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public List<String> getDataTypes() { return dataTypes; }
        public void setDataTypes(List<String> dataTypes) { this.dataTypes = dataTypes; }
        
        public boolean isIncludeDependencies() { return includeDependencies; }
        public void setIncludeDependencies(boolean includeDependencies) { this.includeDependencies = includeDependencies; }
        
        public boolean isUpdateLinks() { return updateLinks; }
        public void setUpdateLinks(boolean updateLinks) { this.updateLinks = updateLinks; }
        
        public String getBackupLocation() { return backupLocation; }
        public void setBackupLocation(String backupLocation) { this.backupLocation = backupLocation; }
    }
    
    public static class CleanupRequest {
        private String operationId;
        private String policyType; // RETENTION_BASED, SIZE_BASED, TIME_BASED, MANUAL
        private String scope; // GLOBAL, PROJECT, MODULE
        private int retentionDays;
        private long maxSizeMB;
        private boolean archiveInsteadOfDelete;
        private String archiveLocation;
        private List<String> dataTypes;
        
        public CleanupRequest() {}
        
        // Getters and Setters
        public String getOperationId() { return operationId; }
        public void setOperationId(String operationId) { this.operationId = operationId; }
        
        public String getPolicyType() { return policyType; }
        public void setPolicyType(String policyType) { this.policyType = policyType; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public int getRetentionDays() { return retentionDays; }
        public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }
        
        public long getMaxSizeMB() { return maxSizeMB; }
        public void setMaxSizeMB(long maxSizeMB) { this.maxSizeMB = maxSizeMB; }
        
        public boolean isArchiveInsteadOfDelete() { return archiveInsteadOfDelete; }
        public void setArchiveInsteadOfDelete(boolean archiveInsteadOfDelete) { this.archiveInsteadOfDelete = archiveInsteadOfDelete; }
        
        public String getArchiveLocation() { return archiveLocation; }
        public void setArchiveLocation(String archiveLocation) { this.archiveLocation = archiveLocation; }
        
        public List<String> getDataTypes() { return dataTypes; }
        public void setDataTypes(List<String> dataTypes) { this.dataTypes = dataTypes; }
    }
    
    public static class DataManagementResult {
        private String operationId;
        private String operationType;
        private String status;
        private String message;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private int progress; // 0-100
        private long executionTimeMs;
        private int totalRecords;
        private int processedRecords;
        private int failedRecords;
        private Map<String, Object> results;
        private List<String> warnings;
        private List<String> errors;
        
        public DataManagementResult() {
            this.results = new HashMap<>();
            this.warnings = new ArrayList<>();
            this.errors = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getOperationId() { return operationId; }
        public void setOperationId(String operationId) { this.operationId = operationId; }
        
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        
        public long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        
        public int getProcessedRecords() { return processedRecords; }
        public void setProcessedRecords(int processedRecords) { this.processedRecords = processedRecords; }
        
        public int getFailedRecords() { return failedRecords; }
        public void setFailedRecords(int failedRecords) { this.failedRecords = failedRecords; }
        
        public Map<String, Object> getResults() { return results; }
        public void setResults(Map<String, Object> results) { this.results = results; }
        
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
        
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        
        // Helper methods
        public void addWarning(String warning) {
            if (warning != null) {
                warnings.add(warning);
            }
        }
        
        public void addError(String error) {
            if (error != null) {
                errors.add(error);
            }
        }
        
        public void addResult(String key, Object value) {
            if (key != null) {
                results.put(key, value);
            }
        }
        
        public boolean isCompleted() {
            return "COMPLETED".equals(status);
        }
        
        public boolean isFailed() {
            return "FAILED".equals(status);
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
    
    // Data classes (simplified versions of domain entities)
    
    public static class DataGeneratorProfile {
        private String profileId;
        private String name;
        private String description;
        private String generatorType;
        private String qualityLevel;
        private int maxDataRecords;
        private boolean isActive;
        private List<String> supportedDataTypes;
        private LocalDateTime createdAt;
        
        public DataGeneratorProfile() {
            this.supportedDataTypes = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getProfileId() { return profileId; }
        public void setProfileId(String profileId) { this.profileId = profileId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getGeneratorType() { return generatorType; }
        public void setGeneratorType(String generatorType) { this.generatorType = generatorType; }
        
        public String getQualityLevel() { return qualityLevel; }
        public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }
        
        public int getMaxDataRecords() { return maxDataRecords; }
        public void setMaxDataRecords(int maxDataRecords) { this.maxDataRecords = maxDataRecords; }
        
        public boolean getIsActive() { return isActive; }
        public void setIsActive(boolean isActive) { this.isActive = isActive; }
        
        public List<String> getSupportedDataTypes() { return supportedDataTypes; }
        public void setSupportedDataTypes(List<String> supportedDataTypes) { this.supportedDataTypes = supportedDataTypes; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
    
    public static class DataTemplate {
        private String templateId;
        private String name;
        private String description;
        private String category;
        private String dataType;
        private String templateContent;
        private List<String> variables;
        private boolean isActive;
        private LocalDateTime createdAt;
        
        public DataTemplate() {
            this.variables = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        
        public String getTemplateContent() { return templateContent; }
        public void setTemplateContent(String templateContent) { this.templateContent = templateContent; }
        
        public List<String> getVariables() { return variables; }
        public void setVariables(List<String> variables) { this.variables = variables; }
        
        public boolean getIsActive() { return isActive; }
        public void setIsActive(boolean isActive) { this.isActive = isActive; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}