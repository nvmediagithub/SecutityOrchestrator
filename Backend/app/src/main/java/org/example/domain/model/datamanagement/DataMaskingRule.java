package org.example.domain.model.datamanagement;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing data masking rules for test data protection
 */
@Entity(name = "DataMaskingRuleAdvanced")
@Table(name = "data_masking_rules")
public class DataMaskingRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 255)
    private String ruleId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 1000)
    private String fieldName; // Field or pattern to mask
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaskingType maskingType;
    
    @Column(length = 2000)
    private String maskingPattern; // Regex pattern or format
    
    @Column(length = 1000)
    private String replacementValue; // Value to use for replacement
    
    @Column(length = 1000)
    private String maskingAlgorithm; // Hash, Encrypt, Tokenize, etc.
    
    @Column(length = 1000)
    private String format; // Preserve format: EMAIL, PHONE, SSN, etc.
    
    @Column(length = 255)
    private String appliesToClassification; // Privacy classification level
    
    @Column(length = 255)
    private String appliesToDataType; // JSON, XML, CSV, etc.
    
    @Column(length = 1000)
    private String scope; // FIELD, RECORD, DATASET, GLOBAL
    
    @Column(length = 255)
    private String appliesToId; // Dataset ID, Project ID, etc.
    
    @ElementCollection
    private List<String> conditionalFields = new ArrayList<>(); // Fields that must exist for rule to apply
    
    @Column(length = 2000)
    private String conditions; // JSON conditions for rule application
    
    private Boolean isActive = true;
    private Boolean isReversible = false;
    private Boolean preserveFormat = true;
    private Boolean preserveLength = true;
    private Boolean preservePatterns = true;
    
    private Integer priority = 0; // Order of application (lower = first)
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection
    private Map<String, Object> parameters = new HashMap<>();
    
    @ElementCollection
    private Map<String, Object> metadata = new HashMap<>();
    
    @Column(length = 2000)
    private String validationRules; // Rules to validate masked data
    
    @Column(length = 2000)
    private String exceptions; // When not to apply masking
    
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    
    private Integer usageCount = 0;
    private LocalDateTime lastExecuted;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String lastModifiedBy;
    private String lastExecutedBy;
    
    @Column(length = 2000)
    private String performanceNotes;
    
    @Column(length = 2000)
    private String securityNotes;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public DataMaskingRule() {
        this.ruleId = "DMR_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.conditionalFields = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.parameters = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    public DataMaskingRule(String name, String fieldName, MaskingType maskingType) {
        this();
        this.name = name;
        this.fieldName = fieldName;
        this.maskingType = maskingType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    
    public MaskingType getMaskingType() { return maskingType; }
    public void setMaskingType(MaskingType maskingType) { this.maskingType = maskingType; }
    
    public String getMaskingPattern() { return maskingPattern; }
    public void setMaskingPattern(String maskingPattern) { this.maskingPattern = maskingPattern; }
    
    public String getReplacementValue() { return replacementValue; }
    public void setReplacementValue(String replacementValue) { this.replacementValue = replacementValue; }
    
    public String getMaskingAlgorithm() { return maskingAlgorithm; }
    public void setMaskingAlgorithm(String maskingAlgorithm) { this.maskingAlgorithm = maskingAlgorithm; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getAppliesToClassification() { return appliesToClassification; }
    public void setAppliesToClassification(String appliesToClassification) { this.appliesToClassification = appliesToClassification; }
    
    public String getAppliesToDataType() { return appliesToDataType; }
    public void setAppliesToDataType(String appliesToDataType) { this.appliesToDataType = appliesToDataType; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    
    public String getAppliesToId() { return appliesToId; }
    public void setAppliesToId(String appliesToId) { this.appliesToId = appliesToId; }
    
    public List<String> getConditionalFields() { return conditionalFields; }
    public void setConditionalFields(List<String> conditionalFields) { this.conditionalFields = conditionalFields; }
    
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsReversible() { return isReversible; }
    public void setIsReversible(Boolean isReversible) { this.isReversible = isReversible; }
    
    public Boolean getPreserveFormat() { return preserveFormat; }
    public void setPreserveFormat(Boolean preserveFormat) { this.preserveFormat = preserveFormat; }
    
    public Boolean getPreserveLength() { return preserveLength; }
    public void setPreserveLength(Boolean preserveLength) { this.preserveLength = preserveLength; }
    
    public Boolean getPreservePatterns() { return preservePatterns; }
    public void setPreservePatterns(Boolean preservePatterns) { this.preservePatterns = preservePatterns; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }
    
    public String getExceptions() { return exceptions; }
    public void setExceptions(String exceptions) { this.exceptions = exceptions; }
    
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    
    public LocalDateTime getLastExecuted() { return lastExecuted; }
    public void setLastExecuted(LocalDateTime lastExecuted) { this.lastExecuted = lastExecuted; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getLastExecutedBy() { return lastExecutedBy; }
    public void setLastExecutedBy(String lastExecutedBy) { this.lastExecutedBy = lastExecutedBy; }
    
    public String getPerformanceNotes() { return performanceNotes; }
    public void setPerformanceNotes(String performanceNotes) { this.performanceNotes = performanceNotes; }
    
    public String getSecurityNotes() { return securityNotes; }
    public void setSecurityNotes(String securityNotes) { this.securityNotes = securityNotes; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Helper methods
    public boolean isActive() {
        return isActive != null && isActive;
    }
    
    public boolean isInEffect() {
        LocalDateTime now = LocalDateTime.now();
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) {
            return false;
        }
        if (effectiveTo != null && now.isAfter(effectiveTo)) {
            return false;
        }
        return isActive();
    }
    
    public boolean isFieldLevel() {
        return "FIELD".equalsIgnoreCase(scope);
    }
    
    public boolean isRecordLevel() {
        return "RECORD".equalsIgnoreCase(scope);
    }
    
    public boolean isDataSetLevel() {
        return "DATASET".equalsIgnoreCase(scope);
    }
    
    public boolean isGlobal() {
        return "GLOBAL".equalsIgnoreCase(scope);
    }
    
    public boolean isGlobalScope() {
        return scope == null || "GLOBAL".equalsIgnoreCase(scope);
    }
    
    public boolean isDataSetSpecific() {
        return "DATASET".equalsIgnoreCase(scope) && appliesToId != null;
    }
    
    public boolean isProjectSpecific() {
        return "PROJECT".equalsIgnoreCase(scope) && appliesToId != null;
    }
    
    public boolean isReversible() {
        return isReversible != null && isReversible;
    }
    
    public boolean preservesFormat() {
        return preserveFormat != null && preserveFormat;
    }
    
    public boolean preservesLength() {
        return preserveLength != null && preserveLength;
    }
    
    public boolean preservesPatterns() {
        return preservePatterns != null && preservePatterns;
    }
    
    public boolean hasPattern() {
        return maskingPattern != null && !maskingPattern.trim().isEmpty();
    }
    
    public boolean hasReplacement() {
        return replacementValue != null && !replacementValue.trim().isEmpty();
    }
    
    public boolean hasAlgorithm() {
        return maskingAlgorithm != null && !maskingAlgorithm.trim().isEmpty();
    }
    
    public boolean hasConditions() {
        return conditions != null && !conditions.trim().isEmpty();
    }
    
    public boolean hasExceptions() {
        return exceptions != null && !exceptions.trim().isEmpty();
    }
    
    public boolean isHighPriority() {
        return priority != null && priority < 10;
    }
    
    public boolean isLowPriority() {
        return priority != null && priority > 90;
    }
    
    public void addConditionalField(String fieldName) {
        if (fieldName != null && !conditionalFields.contains(fieldName)) {
            conditionalFields.add(fieldName);
        }
    }
    
    public void removeConditionalField(String fieldName) {
        conditionalFields.remove(fieldName);
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public void setEffectivePeriod(LocalDateTime from, LocalDateTime to) {
        this.effectiveFrom = from;
        this.effectiveTo = to;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsExecuted(String executedBy) {
        this.lastExecuted = LocalDateTime.now();
        this.lastExecutedBy = executedBy;
        this.usageCount++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setHighPriority() {
        this.priority = 1;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setLowPriority() {
        this.priority = 100;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addPerformanceNote(String note) {
        this.performanceNotes = (this.performanceNotes != null ? this.performanceNotes + "\n" : "") + note;
    }
    
    public void addSecurityNote(String note) {
        this.securityNotes = (this.securityNotes != null ? this.securityNotes + "\n" : "") + note;
    }
    
    // Enums
    public enum MaskingType {
        STATIC_VALUE,
        RANDOM_VALUE,
        HASH_VALUE,
        ENCRYPT_VALUE,
        TOKENIZE_VALUE,
        PARTIAL_MASK,
        FORMAT_PRESERVING,
        PATTERN_REPLACEMENT,
        CONDITIONAL_MASK,
        CONTEXTUAL_MASK
    }
    
    @Override
    public String toString() {
        return "DataMaskingRule{" +
                "ruleId='" + ruleId + '\'' +
                ", name='" + name + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", maskingType=" + maskingType +
                ", scope='" + scope + '\'' +
                ", priority=" + priority +
                ", isActive=" + isActive +
                ", isReversible=" + isReversible +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataMaskingRule that = (DataMaskingRule) o;
        return Objects.equals(ruleId, that.ruleId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }
}