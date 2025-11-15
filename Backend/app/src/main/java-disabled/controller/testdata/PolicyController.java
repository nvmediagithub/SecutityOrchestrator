package org.example.infrastructure.controller.testdata;

import org.example.domain.dto.testdata.ValidationRequest;
import org.example.domain.dto.testdata.ValidationResponse;
import org.example.domain.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

/**
 * REST Controller for data validation and policy management endpoints
 * Handles: POST /api/data/validate
 *          GET /api/data/policies
 */
@RestController
@RequestMapping("/api/data")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class PolicyController {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);
    
    // Cache for validation results
    private final Map<String, ValidationResponse> validationCache = new ConcurrentHashMap<>();
    
    // Mock policy storage
    private final List<DataPolicy> policies = new ArrayList<>();
    private final Random random = new Random();
    
    public PolicyController() {
        // Initialize with some sample policies
        initializeSamplePolicies();
    }
    
    /**
     * Validate test data against rules and policies
     * POST /api/data/validate
     */
    @PostMapping("/validate")
    public CompletableFuture<ResponseEntity<ApiResponse<ValidationResponse>>> validateTestData(
            @Valid @RequestBody ValidationRequest request) {
        
        logger.info("Starting data validation for request: {}, records: {}", 
                   request.getRequestId(), request.getRecordCount());
        
        try {
            // Validate request
            if (request.getDataRecords() == null || request.getDataRecords().isEmpty()) {
                ApiResponse.ApiError error = new ApiResponse.ApiError(
                    "EMPTY_DATA", "Data records cannot be empty");
                ApiResponse<ValidationResponse> errorResponse = ApiResponse.error(
                    error, request.getRequestId());
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(errorResponse));
            }
            
            // Start validation process
            CompletableFuture<ValidationResponse> validationFuture = 
                processValidationAsync(request);
            
            // Return immediate response
            ValidationResponse immediateResponse = new ValidationResponse(request.getRequestId());
            immediateResponse.setMessage("Validation started");
            immediateResponse.setValid(false);
            immediateResponse.setTotalRecords(request.getRecordCount());
            
            ApiResponse<ValidationResponse> apiResponse = ApiResponse.success(
                immediateResponse, request.getRequestId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.accepted().body(apiResponse));
                
        } catch (Exception e) {
            logger.error("Error starting data validation for request: {}", request.getRequestId(), e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "VALIDATION_START_FAILED", 
                "Failed to start validation: " + e.getMessage());
            ApiResponse<ValidationResponse> errorResponse = ApiResponse.error(
                error, request.getRequestId());
            
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
        }
    }
    
    /**
     * Get data policies
     * GET /api/data/policies
     */
    @GetMapping("/policies")
    public ResponseEntity<ApiResponse<List<DataPolicy>>> getDataPolicies(
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        
        logger.debug("Getting data policies for scope: {}, activeOnly: {}", scope, activeOnly);
        
        try {
            // Filter policies based on criteria
            List<DataPolicy> filteredPolicies = filterPolicies(scope, activeOnly);
            
            ApiResponse<List<DataPolicy>> response = ApiResponse.success(
                filteredPolicies, "policies_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting data policies", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "POLICIES_RETRIEVAL_FAILED", 
                "Failed to retrieve policies: " + e.getMessage());
            ApiResponse<List<DataPolicy>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get validation capabilities
     * GET /api/data/validate/capabilities
     */
    @GetMapping("/validate/capabilities")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getValidationCapabilities() {
        
        logger.debug("Getting validation capabilities");
        
        try {
            Map<String, Object> capabilities = Map.of(
                "supportedRules", Arrays.asList(
                    "REQUIRED_FIELD", "DATA_TYPE", "RANGE_CHECK", "PATTERN_MATCH", 
                    "LENGTH_CHECK", "UNIQUE_CHECK", "CUSTOM_VALIDATION", "BUSINESS_RULE"
                ),
                "supportedStandards", Arrays.asList("GDPR", "HIPAA", "SOX", "PCI_DSS", "ISO27001"),
                "maxRecords", 10000,
                "supportedFormats", Arrays.asList("JSON", "XML", "CSV", "YAML"),
                "validationTypes", Arrays.asList("SYNTACTIC", "SEMANTIC", "BUSINESS_RULE", "COMPLIANCE"),
                "customValidationSupported", true,
                "crossFieldValidation", true,
                "deepValidation", true,
                "realTimeValidation", true
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                capabilities, "capabilities_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting validation capabilities", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "CAPABILITIES_RETRIEVAL_FAILED", 
                "Failed to retrieve capabilities: " + e.getMessage());
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get policy types
     * GET /api/data/policies/types
     */
    @GetMapping("/policies/types")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPolicyTypes() {
        
        logger.debug("Getting policy types");
        
        try {
            Map<String, String> policyTypes = Map.of(
                "DATA_RETENTION", "Policies for data lifecycle and retention",
                "DATA_PRIVACY", "Policies for personal data handling",
                "DATA_ACCESS", "Policies for data access control",
                "DATA_QUALITY", "Policies for data quality requirements",
                "DATA_COMPLIANCE", "Policies for regulatory compliance",
                "DATA_SECURITY", "Policies for data security measures",
                "DATA_CLASSIFICATION", "Policies for data classification",
                "DATA_LINEAGE", "Policies for data lineage tracking"
            );
            
            ApiResponse<Map<String, String>> response = ApiResponse.success(
                policyTypes, "types_" + UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting policy types", e);
            
            ApiResponse.ApiError error = new ApiResponse.ApiError(
                "TYPES_RETRIEVAL_FAILED", 
                "Failed to retrieve policy types: " + e.getMessage());
            ApiResponse<Map<String, String>> errorResponse = ApiResponse.error(
                error, "error_" + UUID.randomUUID().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Private helper methods
    
    private void initializeSamplePolicies() {
        // Sample data retention policy
        DataPolicy retentionPolicy = new DataPolicy();
        retentionPolicy.setName("Test Data Retention Policy");
        retentionPolicy.setDescription("Default retention policy for test data");
        retentionPolicy.setPolicyType("DATA_RETENTION");
        retentionPolicy.setRules("Test data should be retained for maximum 30 days");
        retentionPolicy.setAccessLevel("INTERNAL");
        retentionPolicy.setScope("GLOBAL");
        retentionPolicy.setIsActive(true);
        retentionPolicy.setPriority(1);
        retentionPolicy.setApplicableDataTypes(Arrays.asList("PERSONAL_DATA", "FINANCIAL_DATA"));
        retentionPolicy.setTags(Arrays.asList("retention", "test-data", "global"));
        retentionPolicy.setCreatedBy("system");
        retentionPolicy.setCreatedAt(LocalDateTime.now().minusDays(30));
        retentionPolicy.setUpdatedAt(LocalDateTime.now().minusDays(30));
        
        policies.add(retentionPolicy);
    }
    
    private List<DataPolicy> filterPolicies(String scope, boolean activeOnly) {
        return policies.stream()
            .filter(p -> "all".equalsIgnoreCase(scope) || scope.equalsIgnoreCase(p.getScope()))
            .filter(p -> !activeOnly || p.getIsActive())
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    private CompletableFuture<ValidationResponse> processValidationAsync(ValidationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            ValidationResponse response = new ValidationResponse(request.getRequestId());
            response.setDataType(request.getDataType());
            response.setScope(request.getScope());
            response.setTotalRecords(request.getRecordCount());
            
            // Simulate validation process
            int validRecords = (int) (request.getRecordCount() * 0.85); // 85% valid
            int invalidRecords = request.getRecordCount() - validRecords;
            
            response.setValidRecords(validRecords);
            response.setInvalidRecords(invalidRecords);
            response.calculateValidityPercentage();
            
            // Set quality report
            ValidationResponse.DataQualityReport qualityReport = new ValidationResponse.DataQualityReport();
            qualityReport.setCompleteness(85.0 + random.nextDouble() * 15.0);
            qualityReport.setAccuracy(80.0 + random.nextDouble() * 20.0);
            qualityReport.setConsistency(90.0 + random.nextDouble() * 10.0);
            qualityReport.setUniqueness(95.0 + random.nextDouble() * 5.0);
            qualityReport.setValidity(response.getValidityPercentage());
            qualityReport.setOverallQuality(response.getValidityPercentage());
            response.setQualityReport(qualityReport);
            response.setOverallScore(response.getValidityPercentage());
            
            // Add compliance check if requested
            if (request.isComplianceRequired()) {
                ValidationResponse.ComplianceResult complianceResult = 
                    new ValidationResponse.ComplianceResult(request.getComplianceStandard());
                complianceResult.setCompliant(random.nextDouble() > 0.2); // 80% compliance rate
                complianceResult.setComplianceScore(75.0 + random.nextDouble() * 25.0);
                complianceResult.getRequirements().addAll(Arrays.asList(
                    "Data Minimization", "Purpose Limitation", "Data Accuracy", 
                    "Storage Limitation", "Integrity and Confidentiality"
                ));
                response.setComplianceResult(complianceResult);
            }
            
            // Add statistics
            ValidationResponse.ValidationStatistics stats = new ValidationResponse.ValidationStatistics();
            stats.setTotalChecks(request.getValidationRules().size() * request.getRecordCount());
            stats.setPassedChecks(validRecords * request.getValidationRules().size());
            stats.setFailedChecks(invalidRecords * request.getValidationRules().size());
            stats.setWarningChecks((int) (stats.getTotalChecks() * 0.1)); // 10% warnings
            stats.setPassRate(response.getValidityPercentage());
            response.setStatistics(stats);
            
            // Add recommendations
            if (response.getValidityPercentage() < 90.0) {
                response.addRecommendation("Consider improving data quality by addressing invalid records");
            }
            
            // Mark as completed
            response.markCompleted(1000); // Simulate 1 second processing
            
            // Cache the result
            validationCache.put(response.getValidationId(), response);
            
            logger.info("Validation completed for request: {}, valid: {}/{}, score: {}", 
                       request.getRequestId(), validRecords, request.getRecordCount(), 
                       String.format("%.1f", response.getOverallScore()));
            
            return response;
        });
    }
    
    // DataPolicy class for policy management
    public static class DataPolicy {
        private String policyId;
        private String name;
        private String description;
        private String policyType;
        private String rules;
        private String accessLevel;
        private String scope;
        private List<String> applicableDataTypes;
        private List<String> tags;
        private Boolean isActive;
        private Integer priority;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        
        public DataPolicy() {
            this.applicableDataTypes = new ArrayList<>();
            this.tags = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getPolicyId() { return policyId; }
        public void setPolicyId(String policyId) { this.policyId = policyId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPolicyType() { return policyType; }
        public void setPolicyType(String policyType) { this.policyType = policyType; }
        
        public String getRules() { return rules; }
        public void setRules(String rules) { this.rules = rules; }
        
        public String getAccessLevel() { return accessLevel; }
        public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public List<String> getApplicableDataTypes() { return applicableDataTypes; }
        public void setApplicableDataTypes(List<String> applicableDataTypes) { this.applicableDataTypes = applicableDataTypes; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        
        // Helper methods
        public boolean isActive() {
            return isActive != null && isActive;
        }
        
        public boolean isApplicableTo(String dataType) {
            return applicableDataTypes.isEmpty() || 
                   applicableDataTypes.contains(dataType) ||
                   applicableDataTypes.contains("*") ||
                   applicableDataTypes.contains("ALL");
        }
        
        public void addApplicableDataType(String dataType) {
            if (dataType != null && !applicableDataTypes.contains(dataType)) {
                applicableDataTypes.add(dataType);
            }
        }
        
        public void addTag(String tag) {
            if (tag != null && !tags.contains(tag)) {
                tags.add(tag);
            }
        }
        
        @Override
        public String toString() {
            return "DataPolicy{" +
                    "policyId='" + policyId + '\'' +
                    ", name='" + name + '\'' +
                    ", policyType='" + policyType + '\'' +
                    ", scope='" + scope + '\'' +
                    ", isActive=" + isActive +
                    ", priority=" + priority +
                    '}';
        }
    }
}