package org.example.application.service.testdata;

import org.example.domain.entities.TestDataSet;
import org.example.domain.model.testdata.DataDependency;
import org.example.domain.model.testdata.enums.DependencyType;
import org.example.domain.model.testdata.enums.DataType;
import org.example.domain.model.testdata.valueobjects.DependencyStrength;
import org.example.infrastructure.llm.testdata.dto.TestDataGenerationRequest;
import org.example.infrastructure.llm.testdata.dto.TestDataGenerationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing test data contexts and dependencies
 * Handles the complex relationships between test data elements in end-to-end testing scenarios
 */
@Service
public class TestDataContextService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataContextService.class);
    
    // In-memory storage for contexts (in production, this would be a database)
    private final Map<String, TestDataContext> activeContexts = new ConcurrentHashMap<>();
    private final Map<String, DataDependency> dependencies = new ConcurrentHashMap<>();
    private final Map<String, TestDataGenerationResult> generatedData = new ConcurrentHashMap<>();
    
    /**
     * Create a new test data context
     */
    public TestDataContext createContext(String contextName, Map<String, Object> requiredData) {
        String contextId = generateContextId();
        
        TestDataContext context = new TestDataContext();
        context.setContextId(contextId);
        context.setName(contextName);
        context.setCreatedAt(LocalDateTime.now());
        context.setStatus(TestDataContext.ContextStatus.ACTIVE);
        context.setRequiredData(requiredData != null ? new HashMap<>(requiredData) : new HashMap<>());
        context.setGeneratedData(new HashMap<>());
        context.setDependencies(new ArrayList<>());
        context.setMetadata(new HashMap<>());
        
        activeContexts.put(contextId, context);
        
        logger.info("Created test data context: contextId={}, name={}, requiredFields={}", 
                   contextId, contextName, requiredData != null ? requiredData.size() : 0);
        
        return context;
    }
    
    /**
     * Add generated data to context
     */
    public void addDataToContext(String contextId, String fieldName, TestDataGenerationResult data) {
        TestDataContext context = getContext(contextId);
        if (context == null) {
            throw new IllegalArgumentException("Context not found: " + contextId);
        }
        
        // Store the generated data
        generatedData.put(fieldName, data);
        
        // Add to context
        context.getGeneratedData().put(fieldName, data);
        context.setLastUpdated(LocalDateTime.now());
        
        logger.debug("Added data to context: contextId={}, field={}, generationId={}", 
                    contextId, fieldName, data.getGenerationId());
    }
    
    /**
     * Add dependency between data elements
     */
    public void addDependency(String contextId, String sourceField, String targetField, 
                            DependencyType dependencyType, DependencyStrength strength) {
        
        TestDataContext context = getContext(contextId);
        if (context == null) {
            throw new IllegalArgumentException("Context not found: " + contextId);
        }
        
        String dependencyId = generateDependencyId(sourceField, targetField);
        
        DataDependency dependency = new DataDependency();
        dependency.setDependencyId(dependencyId);
        dependency.setName(sourceField + " -> " + targetField);
        dependency.setSourceDataId(sourceField);
        dependency.setTargetDataId(targetField);
        dependency.setDependencyType(dependencyType);
        dependency.setStrength(strength);
        dependency.setCreatedAt(LocalDateTime.now());
        dependency.setStatus(DataDependency.DependencyStatus.ACTIVE);
        
        dependencies.put(dependencyId, dependency);
        context.getDependencies().add(dependency);
        
        logger.info("Added dependency: contextId={}, source={}, target={}, type={}", 
                   contextId, sourceField, targetField, dependencyType);
    }
    
    /**
     * Generate data with dependency awareness
     */
    public TestDataContext generateDependentData(String contextId, String fieldName, 
                                               TestDataGenerationRequest request) {
        
        TestDataContext context = getContext(contextId);
        if (context == null) {
            throw new IllegalArgumentException("Context not found: " + contextId);
        }
        
        // Build dependency context
        Map<String, Object> dependencyContext = buildDependencyContext(context, fieldName);
        
        // Enhance request with dependency data
        TestDataGenerationRequest enhancedRequest = enhanceRequestWithDependencies(request, context, fieldName);
        
        // Generate the data
        // This would typically call the TestDataGenerationService
        // For now, we'll simulate the generation process
        
        // Simulate generated data (in real implementation, this would call the LLM service)
        TestDataGenerationResult generatedResult = simulateDataGeneration(enhancedRequest, dependencyContext);
        
        // Add to context
        addDataToContext(contextId, fieldName, generatedResult);
        
        // Update context metadata
        context.getMetadata().put("lastGeneration", fieldName);
        context.getMetadata().put("generationTime", LocalDateTime.now());
        context.setLastUpdated(LocalDateTime.now());
        
        logger.info("Generated dependent data: contextId={}, field={}, records={}", 
                   contextId, fieldName, 
                   generatedResult.getDataRecords() != null ? generatedResult.getDataRecords().size() : 0);
        
        return context;
    }
    
    /**
     * Resolve all dependencies for a context
     */
    public TestDataContext resolveDependencies(String contextId) {
        TestDataContext context = getContext(contextId);
        if (context == null) {
            throw new IllegalArgumentException("Context not found: " + contextId);
        }
        
        logger.info("Resolving dependencies for context: {}", contextId);
        
        // Sort dependencies by strength and type for proper resolution
        List<DataDependency> sortedDependencies = context.getDependencies().stream()
            .sorted((d1, d2) -> {
                // Critical dependencies first
                if (d1.getStrength().isCritical() && !d2.getStrength().isCritical()) return -1;
                if (!d1.getStrength().isCritical() && d2.getStrength().isCritical()) return 1;
                
                // Then by dependency type
                return d1.getDependencyType().name().compareTo(d2.getDependencyType().name());
            })
            .collect(Collectors.toList());
        
        int resolvedCount = 0;
        for (DataDependency dependency : sortedDependencies) {
            if (resolveDependency(context, dependency)) {
                resolvedCount++;
            } else {
                logger.warn("Failed to resolve dependency: {} -> {}", 
                           dependency.getSourceDataId(), dependency.getTargetDataId());
            }
        }
        
        context.getMetadata().put("resolvedDependencies", resolvedCount);
        context.getMetadata().put("totalDependencies", context.getDependencies().size());
        context.setLastUpdated(LocalDateTime.now());
        
        logger.info("Resolved {}/{} dependencies for context: {}", 
                   resolvedCount, context.getDependencies().size(), contextId);
        
        return context;
    }
    
    /**
     * Validate context integrity
     */
    public ContextValidationResult validateContext(String contextId) {
        TestDataContext context = getContext(contextId);
        if (context == null) {
            throw new IllegalArgumentException("Context not found: " + contextId);
        }
        
        ContextValidationResult result = new ContextValidationResult();
        result.setContextId(contextId);
        result.setValidatedAt(LocalDateTime.now());
        
        // Check required data
        List<String> missingRequired = context.getRequiredData().entrySet().stream()
            .filter(entry -> !context.getGeneratedData().containsKey(entry.getKey()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        result.setMissingRequiredData(missingRequired);
        
        // Check dependencies
        List<String> brokenDependencies = context.getDependencies().stream()
            .filter(dep -> !isDependencySatisfied(context, dep))
            .map(dep -> dep.getSourceDataId() + " -> " + dep.getTargetDataId())
            .collect(Collectors.toList());
        result.setBrokenDependencies(brokenDependencies);
        
        // Set overall status
        result.setValid(missingRequired.isEmpty() && brokenDependencies.isEmpty());
        result.setValidationScore(calculateValidationScore(context, result));
        
        logger.info("Context validation completed: contextId={}, valid={}, score={}", 
                   contextId, result.isValid(), String.format("%.1f", result.getValidationScore()));
        
        return result;
    }
    
    /**
     * Get context by ID
     */
    public TestDataContext getContext(String contextId) {
        return activeContexts.get(contextId);
    }
    
    /**
     * Get all active contexts
     */
    public List<TestDataContext> getAllContexts() {
        return new ArrayList<>(activeContexts.values());
    }
    
    /**
     * Clean up context
     */
    public void deleteContext(String contextId) {
        TestDataContext context = activeContexts.remove(contextId);
        if (context != null) {
            // Clean up associated data
            context.getGeneratedData().values().forEach(result -> 
                generatedData.remove(result.getGenerationId()));
            
            // Clean up dependencies
            context.getDependencies().forEach(dep -> 
                dependencies.remove(dep.getDependencyId()));
            
            logger.info("Deleted test data context: {}", contextId);
        }
    }
    
    /**
     * Export context data for external use
     */
    public Map<String, Object> exportContextData(String contextId) {
        TestDataContext context = getContext(contextId);
        if (context == null) {
            throw new IllegalArgumentException("Context not found: " + contextId);
        }
        
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("contextId", contextId);
        exportData.put("name", context.getName());
        exportData.put("createdAt", context.getCreatedAt());
        exportData.put("generatedData", context.getGeneratedData());
        exportData.put("dependencies", context.getDependencies());
        exportData.put("metadata", context.getMetadata());
        
        return exportData;
    }
    
    // Private helper methods
    
    private String generateContextId() {
        return "ctx_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    private String generateDependencyId(String source, String target) {
        return "dep_" + source + "_" + target + "_" + System.currentTimeMillis();
    }
    
    private Map<String, Object> buildDependencyContext(TestDataContext context, String targetField) {
        Map<String, Object> dependencyContext = new HashMap<>();
        
        // Add existing generated data
        context.getGeneratedData().forEach((field, result) -> {
            if (result != null && result.getDataRecords() != null) {
                dependencyContext.put(field, result.getDataRecords());
            }
        });
        
        // Add required data
        dependencyContext.putAll(context.getRequiredData());
        
        // Add target field info
        dependencyContext.put("targetField", targetField);
        dependencyContext.put("targetContext", context.getName());
        
        return dependencyContext;
    }
    
    private TestDataGenerationRequest enhanceRequestWithDependencies(
            TestDataGenerationRequest request, TestDataContext context, String fieldName) {
        
        // Create enhanced request
        TestDataGenerationRequest enhanced = new TestDataGenerationRequest();
        enhanced.setRequestId(request.getRequestId());
        enhanced.setDataType(request.getDataType());
        enhanced.setGenerationScope(request.getGenerationScope());
        enhanced.setRecordCount(request.getRecordCount());
        enhanced.setQualityLevel(request.getQualityLevel());
        enhanced.setEnableDependencyAnalysis(true);
        enhanced.setPreserveContext(true);
        
        // Add dependency context
        Map<String, Object> dependencyContext = buildDependencyContext(context, fieldName);
        enhanced.setContext(dependencyContext);
        
        // Add dependency info
        enhanced.getContext().put("sourceFields", getSourceFields(context, fieldName));
        enhanced.getContext().put("dependencyTypes", getDependencyTypes(context, fieldName));
        
        return enhanced;
    }
    
    private List<String> getSourceFields(TestDataContext context, String targetField) {
        return context.getDependencies().stream()
            .filter(dep -> dep.getTargetDataId().equals(targetField))
            .map(DataDependency::getSourceDataId)
            .collect(Collectors.toList());
    }
    
    private List<String> getDependencyTypes(TestDataContext context, String targetField) {
        return context.getDependencies().stream()
            .filter(dep -> dep.getTargetDataId().equals(targetField))
            .map(dep -> dep.getDependencyType().name())
            .collect(Collectors.toList());
    }
    
    private TestDataGenerationResult simulateDataGeneration(TestDataGenerationRequest request, 
                                                          Map<String, Object> context) {
        // Simulate LLM generation (in real implementation, this would call the actual service)
        TestDataGenerationResult result = new TestDataGenerationResult();
        result.setRequestId(request.getRequestId());
        result.setSuccessful(true);
        result.setGeneratedAt(LocalDateTime.now());
        result.setContextId(context.get("targetContext").toString());
        
        // Generate sample data based on type
        List<Map<String, Object>> dataRecords = generateSampleData(request.getDataType(),
                                                                 request.getRecordCount(), context);
        result.setDataRecords(dataRecords);
        
        // Add metadata using the correct method
        Map<String, Object> metadata = result.getMetadata();
        metadata.put("simulated", true);
        metadata.put("contextProvided", !context.isEmpty());
        
        return result;
    }
    
    private List<Map<String, Object>> generateSampleData(String dataType, int count, 
                                                       Map<String, Object> context) {
        List<Map<String, Object>> records = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", dataType + "_" + (i + 1));
            record.put("generatedAt", LocalDateTime.now().toString());
            record.put("index", i);
            
            // Add context-specific data
            if (context.containsKey("userId")) {
                record.put("userId", context.get("userId"));
            }
            if (context.containsKey("orderId")) {
                record.put("orderId", context.get("orderId"));
            }
            
            records.add(record);
        }
        
        return records;
    }
    
    private boolean resolveDependency(TestDataContext context, DataDependency dependency) {
        String sourceField = dependency.getSourceDataId();
        String targetField = dependency.getTargetDataId();
        
        // Check if source data exists
        if (!context.getGeneratedData().containsKey(sourceField)) {
            return false;
        }
        
        // Check if target data exists
        if (!context.getGeneratedData().containsKey(targetField)) {
            return false;
        }
        
        // Additional validation based on dependency type
        switch (dependency.getDependencyType()) {
            case REFERENCE:
                return validateReferenceDependency(context, dependency);
            case PREREQUISITE:
                return validatePrerequisiteDependency(context, dependency);
            case VALIDATION:
                return validateValidationDependency(context, dependency);
            case TRANSFORMATION:
                return validateTransformationDependency(context, dependency);
            default:
                return true;
        }
    }
    
    private boolean validateReferenceDependency(TestDataContext context, DataDependency dependency) {
        // For reference dependencies, ensure target references source
        TestDataGenerationResult targetData = context.getGeneratedData().get(dependency.getTargetDataId());
        // Implement reference validation logic
        return true; // Simplified for example
    }
    
    private boolean validateSequentialDependency(TestDataContext context, DataDependency dependency) {
        // For sequential dependencies, ensure proper ordering
        // Implement sequential validation logic
        return true; // Simplified for example
    }
    
    private boolean validatePrerequisiteDependency(TestDataContext context, DataDependency dependency) {
        // For prerequisite dependencies, ensure source is created before target
        // Implement prerequisite validation logic
        return true; // Simplified for example
    }
    
    private boolean validateValidationDependency(TestDataContext context, DataDependency dependency) {
        // For validation dependencies, validate target against source
        // Implement validation dependency logic
        return true; // Simplified for example
    }
    
    private boolean validateTransformationDependency(TestDataContext context, DataDependency dependency) {
        // For transformation dependencies, validate transformation rules
        // Implement transformation validation logic
        return true; // Simplified for example
    }
    
    private boolean validateConstraintDependency(TestDataContext context, DataDependency dependency) {
        // For constraint dependencies, validate business rules
        // Implement constraint validation logic
        return true; // Simplified for example
    }
    
    private boolean isDependencySatisfied(TestDataContext context, DataDependency dependency) {
        return resolveDependency(context, dependency);
    }
    
    private double calculateValidationScore(TestDataContext context, ContextValidationResult result) {
        int totalFields = context.getRequiredData().size() + context.getDependencies().size();
        int validFields = totalFields - result.getMissingRequiredData().size() -
                         result.getBrokenDependencies().size();
        
        return totalFields > 0 ? (double) validFields / totalFields * 100 : 100;
    }
    
    // Inner classes
    
    public static class TestDataContext {
        private String contextId;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime lastUpdated;
        private ContextStatus status;
        private Map<String, Object> requiredData;
        private Map<String, TestDataGenerationResult> generatedData;
        private List<DataDependency> dependencies;
        private Map<String, Object> metadata;
        
        public enum ContextStatus {
            ACTIVE, 
            COMPLETED, 
            FAILED, 
            ARCHIVED
        }
        
        // Getters and Setters
        public String getContextId() { return contextId; }
        public void setContextId(String contextId) { this.contextId = contextId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
        
        public ContextStatus getStatus() { return status; }
        public void setStatus(ContextStatus status) { this.status = status; }
        
        public Map<String, Object> getRequiredData() { return requiredData; }
        public void setRequiredData(Map<String, Object> requiredData) { this.requiredData = requiredData; }
        
        public Map<String, TestDataGenerationResult> getGeneratedData() { return generatedData; }
        public void setGeneratedData(Map<String, TestDataGenerationResult> generatedData) { this.generatedData = generatedData; }
        
        public List<DataDependency> getDependencies() { return dependencies; }
        public void setDependencies(List<DataDependency> dependencies) { this.dependencies = dependencies; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class ContextValidationResult {
        private String contextId;
        private LocalDateTime validatedAt;
        private boolean valid;
        private double validationScore;
        private List<String> missingRequiredData;
        private List<String> brokenDependencies;
        private List<String> warnings;
        
        // Getters and Setters
        public String getContextId() { return contextId; }
        public void setContextId(String contextId) { this.contextId = contextId; }
        
        public LocalDateTime getValidatedAt() { return validatedAt; }
        public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public double getValidationScore() { return validationScore; }
        public void setValidationScore(double validationScore) { this.validationScore = validationScore; }
        
        public List<String> getMissingRequiredData() { return missingRequiredData; }
        public void setMissingRequiredData(List<String> missingRequiredData) { this.missingRequiredData = missingRequiredData; }
        
        public List<String> getBrokenDependencies() { return brokenDependencies; }
        public void setBrokenDependencies(List<String> brokenDependencies) { this.brokenDependencies = brokenDependencies; }
        
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }
}