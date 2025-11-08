package org.example.domain.model.testdata;

import org.example.domain.model.testdata.enums.GenerationScope;
import org.example.domain.model.testdata.valueobjects.DataConstraints;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing profiles for test data generators
 */
@Entity
@Table(name = "data_generator_profiles")
public class DataGeneratorProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String profileId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false, length = 1000)
    private String generatorType; // MOCKITO, FAKER, JPAUDIT, CUSTOM, TEMPLATE
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationScope generationScope;
    
    @Column(length = 2000)
    private String configuration;
    
    @ElementCollection
    private List<String> supportedDataTypes = new ArrayList<>();
    
    @ElementCollection
    private List<String> dataTypePatterns = new ArrayList<>();
    
    @Embedded
    private DataConstraints dataConstraints;
    
    @Column(length = 1000)
    private String qualityLevel; // BASIC, STANDARD, PREMIUM, ENTERPRISE
    
    private Integer maxDataRecords = 1000;
    private Integer batchSize = 100;
    private Integer timeoutSeconds = 300;
    
    private Boolean isParallelProcessing = false;
    private Boolean isCachingEnabled = true;
    private Boolean isValidationEnabled = true;
    private Boolean isRealTime = false;
    
    @ElementCollection
    private List<String> dependencies = new ArrayList<>();
    
    @ElementCollection
    private Map<String, Object> parameters = new HashMap<>();
    
    @ElementCollection
    private Map<String, Object> performanceSettings = new HashMap<>();
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    @Column(length = 2000)
    private String templatePath;
    
    @Column(length = 2000)
    private String scriptPath;
    
    @Column(length = 2000)
    private String customLogic;
    
    private Integer usageCount = 0;
    private LocalDateTime lastUsed;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastExecuted;
    
    private String createdBy;
    private String lastModifiedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public DataGeneratorProfile() {
        this.profileId = "DGP_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.supportedDataTypes = new ArrayList<>();
        this.dataTypePatterns = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.parameters = new HashMap<>();
        this.performanceSettings = new HashMap<>();
        this.tags = new ArrayList<>();
    }
    
    public DataGeneratorProfile(String name, String generatorType, GenerationScope generationScope) {
        this();
        this.name = name;
        this.generatorType = generatorType;
        this.generationScope = generationScope;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getGeneratorType() { return generatorType; }
    public void setGeneratorType(String generatorType) { this.generatorType = generatorType; }
    
    public GenerationScope getGenerationScope() { return generationScope; }
    public void setGenerationScope(GenerationScope generationScope) { this.generationScope = generationScope; }
    
    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }
    
    public List<String> getSupportedDataTypes() { return supportedDataTypes; }
    public void setSupportedDataTypes(List<String> supportedDataTypes) { this.supportedDataTypes = supportedDataTypes; }
    
    public List<String> getDataTypePatterns() { return dataTypePatterns; }
    public void setDataTypePatterns(List<String> dataTypePatterns) { this.dataTypePatterns = dataTypePatterns; }
    
    public DataConstraints getDataConstraints() { return dataConstraints; }
    public void setDataConstraints(DataConstraints dataConstraints) { this.dataConstraints = dataConstraints; }
    
    public String getQualityLevel() { return qualityLevel; }
    public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }
    
    public Integer getMaxDataRecords() { return maxDataRecords; }
    public void setMaxDataRecords(Integer maxDataRecords) { this.maxDataRecords = maxDataRecords; }
    
    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
    
    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public Boolean getIsParallelProcessing() { return isParallelProcessing; }
    public void setIsParallelProcessing(Boolean isParallelProcessing) { this.isParallelProcessing = isParallelProcessing; }
    
    public Boolean getIsCachingEnabled() { return isCachingEnabled; }
    public void setIsCachingEnabled(Boolean isCachingEnabled) { this.isCachingEnabled = isCachingEnabled; }
    
    public Boolean getIsValidationEnabled() { return isValidationEnabled; }
    public void setIsValidationEnabled(Boolean isValidationEnabled) { this.isValidationEnabled = isValidationEnabled; }
    
    public Boolean getIsRealTime() { return isRealTime; }
    public void setIsRealTime(Boolean isRealTime) { this.isRealTime = isRealTime; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public Map<String, Object> getPerformanceSettings() { return performanceSettings; }
    public void setPerformanceSettings(Map<String, Object> performanceSettings) { this.performanceSettings = performanceSettings; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getTemplatePath() { return templatePath; }
    public void setTemplatePath(String templatePath) { this.templatePath = templatePath; }
    
    public String getScriptPath() { return scriptPath; }
    public void setScriptPath(String scriptPath) { this.scriptPath = scriptPath; }
    
    public String getCustomLogic() { return customLogic; }
    public void setCustomLogic(String customLogic) { this.customLogic = customLogic; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    
    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastExecuted() { return lastExecuted; }
    public void setLastExecuted(LocalDateTime lastExecuted) { this.lastExecuted = lastExecuted; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Helper methods
    public boolean supportsDataType(String dataType) {
        return supportedDataTypes.isEmpty() || 
               supportedDataTypes.contains(dataType) ||
               supportedDataTypes.contains("*") ||
               supportedDataTypes.contains("ALL");
    }
    
    public boolean matchesPattern(String dataType) {
        return dataTypePatterns.isEmpty() || 
               dataTypePatterns.stream().anyMatch(pattern -> 
                   dataType != null && matchesPattern(dataType, pattern));
    }
    
    private boolean matchesPattern(String dataType, String pattern) {
        if (pattern == null) return false;
        if (pattern.equals("*") || pattern.equals("ALL")) return true;
        if (pattern.startsWith("*") && pattern.endsWith("*")) {
            return dataType != null && dataType.toLowerCase().contains(pattern.substring(1, pattern.length() - 1).toLowerCase());
        }
        if (pattern.startsWith("*")) {
            return dataType != null && dataType.toLowerCase().endsWith(pattern.substring(1).toLowerCase());
        }
        if (pattern.endsWith("*")) {
            return dataType != null && dataType.toLowerCase().startsWith(pattern.substring(0, pattern.length() - 1).toLowerCase());
        }
        return dataType != null && dataType.equalsIgnoreCase(pattern);
    }
    
    public boolean isHighPerformance() {
        return isParallelProcessing != null && isParallelProcessing;
    }
    
    public boolean hasCache() {
        return isCachingEnabled != null && isCachingEnabled;
    }
    
    public boolean requiresValidation() {
        return isValidationEnabled != null && isValidationEnabled;
    }
    
    public boolean isRealTime() {
        return isRealTime != null && isRealTime;
    }
    
    public boolean isEnterprise() {
        return "ENTERPRISE".equalsIgnoreCase(qualityLevel);
    }
    
    public boolean isPremium() {
        return "PREMIUM".equalsIgnoreCase(qualityLevel) || isEnterprise();
    }
    
    public boolean hasCustomLogic() {
        return customLogic != null && !customLogic.trim().isEmpty();
    }
    
    public boolean hasTemplate() {
        return templatePath != null && !templatePath.trim().isEmpty();
    }
    
    public boolean hasScript() {
        return scriptPath != null && !scriptPath.trim().isEmpty();
    }
    
    public boolean hasDependencies() {
        return dependencies != null && !dependencies.isEmpty();
    }
    
    public void addSupportedDataType(String dataType) {
        if (dataType != null && !supportedDataTypes.contains(dataType)) {
            supportedDataTypes.add(dataType);
        }
    }
    
    public void addDataTypePattern(String pattern) {
        if (pattern != null && !dataTypePatterns.contains(pattern)) {
            dataTypePatterns.add(pattern);
        }
    }
    
    public void addDependency(String dependencyId) {
        if (dependencyId != null && !dependencies.contains(dependencyId)) {
            dependencies.add(dependencyId);
        }
    }
    
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addPerformanceSetting(String key, Object value) {
        this.performanceSettings.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsExecuted() {
        this.lastExecuted = LocalDateTime.now();
        incrementUsage();
    }
    
    public void updateQualityLevel(String level) {
        this.qualityLevel = level;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setMaxRecords(int maxRecords) {
        this.maxDataRecords = maxRecords;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setTimeout(int seconds) {
        this.timeoutSeconds = seconds;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "DataGeneratorProfile{" +
                "profileId='" + profileId + '\'' +
                ", name='" + name + '\'' +
                ", generatorType='" + generatorType + '\'' +
                ", generationScope=" + generationScope +
                ", qualityLevel='" + qualityLevel + '\'' +
                ", supportedDataTypesCount=" + (supportedDataTypes != null ? supportedDataTypes.size() : 0) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataGeneratorProfile that = (DataGeneratorProfile) o;
        return Objects.equals(profileId, that.profileId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(profileId);
    }
}