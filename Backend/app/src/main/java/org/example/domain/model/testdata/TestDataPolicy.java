package org.example.domain.model.testdata;

import org.example.domain.model.testdata.enums.PolicyType;
import org.example.domain.model.testdata.valueobjects.DataConstraints;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing policies for test data management
 */
@Entity
@Table(name = "test_data_policies")
public class TestDataPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String policyId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType policyType;
    
    @Column(nullable = false, length = 2000)
    private String rules;
    
    @Embedded
    private DataConstraints dataConstraints;
    
    @Column(length = 1000)
    private String accessLevel; // PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED
    
    @Column(length = 1000)
    private String scope; // GLOBAL, PROJECT, MODULE, TEST_CASE
    
    @ElementCollection
    private List<String> applicableDataTypes = new ArrayList<>();
    
    @ElementCollection
    private List<String> affectedEntities = new ArrayList<>();
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private LocalDateTime lastEvaluated;
    
    private Boolean isActive = true;
    private Boolean isMandatory = false;
    private Boolean isAutoApplied = false;
    
    private Integer priority = 0;
    private Integer evaluationOrder = 0;
    
    @Column(length = 2000)
    private String complianceRequirements;
    
    @Column(length = 2000)
    private String violationActions; // WARN, BLOCK, ENCRYPT, ANONYMIZE
    
    @Column(length = 2000)
    private String enforcementLogic;
    
    @ElementCollection
    private Map<String, Object> metadata = new HashMap<>();
    
    private Integer usageCount = 0;
    private LocalDateTime lastUsed;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String lastModifiedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public TestDataPolicy() {
        this.policyId = "TDP_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.applicableDataTypes = new ArrayList<>();
        this.affectedEntities = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public TestDataPolicy(String name, PolicyType policyType, String rules) {
        this();
        this.name = name;
        this.policyType = policyType;
        this.rules = rules;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public PolicyType getPolicyType() { return policyType; }
    public void setPolicyType(PolicyType policyType) { this.policyType = policyType; }
    
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    
    public DataConstraints getDataConstraints() { return dataConstraints; }
    public void setDataConstraints(DataConstraints dataConstraints) { this.dataConstraints = dataConstraints; }
    
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    
    public List<String> getApplicableDataTypes() { return applicableDataTypes; }
    public void setApplicableDataTypes(List<String> applicableDataTypes) { this.applicableDataTypes = applicableDataTypes; }
    
    public List<String> getAffectedEntities() { return affectedEntities; }
    public void setAffectedEntities(List<String> affectedEntities) { this.affectedEntities = affectedEntities; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    
    public LocalDateTime getLastEvaluated() { return lastEvaluated; }
    public void setLastEvaluated(LocalDateTime lastEvaluated) { this.lastEvaluated = lastEvaluated; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
    
    public Boolean getIsAutoApplied() { return isAutoApplied; }
    public void setIsAutoApplied(Boolean isAutoApplied) { this.isAutoApplied = isAutoApplied; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Integer getEvaluationOrder() { return evaluationOrder; }
    public void setEvaluationOrder(Integer evaluationOrder) { this.evaluationOrder = evaluationOrder; }
    
    public String getComplianceRequirements() { return complianceRequirements; }
    public void setComplianceRequirements(String complianceRequirements) { this.complianceRequirements = complianceRequirements; }
    
    public String getViolationActions() { return violationActions; }
    public void setViolationActions(String violationActions) { this.violationActions = violationActions; }
    
    public String getEnforcementLogic() { return enforcementLogic; }
    public void setEnforcementLogic(String enforcementLogic) { this.enforcementLogic = enforcementLogic; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    
    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
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
    
    public boolean isMandatory() {
        return isMandatory != null && isMandatory;
    }
    
    public boolean isAutoApplicable() {
        return isAutoApplied != null && isAutoApplied;
    }
    
    public boolean isApplicableTo(String dataType) {
        return applicableDataTypes.isEmpty() || 
               applicableDataTypes.contains(dataType) ||
               applicableDataTypes.contains("*") ||
               applicableDataTypes.contains("ALL");
    }
    
    public boolean isCritical() {
        return policyType != null && policyType.isCritical();
    }
    
    public boolean isDataProtection() {
        return policyType != null && policyType.isDataProtection();
    }
    
    public boolean isLifecycleManagement() {
        return policyType != null && policyType.isLifecycleManagement();
    }
    
    public void addApplicableDataType(String dataType) {
        if (dataType != null && !applicableDataTypes.contains(dataType)) {
            applicableDataTypes.add(dataType);
        }
    }
    
    public void removeApplicableDataType(String dataType) {
        applicableDataTypes.remove(dataType);
    }
    
    public void addAffectedEntity(String entityId) {
        if (entityId != null && !affectedEntities.contains(entityId)) {
            affectedEntities.add(entityId);
        }
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public void setValidityPeriod(LocalDateTime from, LocalDateTime to) {
        this.effectiveFrom = from;
        this.effectiveTo = to;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsEvaluated() {
        this.lastEvaluated = LocalDateTime.now();
        incrementUsage();
    }
    
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void expire() {
        this.effectiveTo = LocalDateTime.now();
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getViolationActionsList() {
        if (violationActions == null || violationActions.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(violationActions.split(","));
    }
    
    @Override
    public String toString() {
        return "TestDataPolicy{" +
                "policyId='" + policyId + '\'' +
                ", name='" + name + '\'' +
                ", policyType=" + policyType +
                ", accessLevel='" + accessLevel + '\'' +
                ", scope='" + scope + '\'' +
                ", isActive=" + isActive +
                ", priority=" + priority +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestDataPolicy that = (TestDataPolicy) o;
        return Objects.equals(policyId, that.policyId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(policyId);
    }
}