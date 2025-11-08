package org.example.domain.model.datamanagement;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing privacy classification for test data
 */
@Entity
@Table(name = "data_privacy_classification")
public class DataPrivacyClassification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 255)
    private String classificationId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrivacyLevel privacyLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataCategory dataCategory;
    
    @Column(length = 1000)
    private String complianceStandard; // GDPR, HIPAA, PCI-DSS, SOX, etc.
    
    @ElementCollection
    private List<String> piiTypes = new ArrayList<>(); // EMAIL, PHONE, SSN, etc.
    
    @ElementCollection
    private List<String> sensitiveFields = new ArrayList<>();
    
    @ElementCollection
    private List<String> requiredActions = new ArrayList<>(); // ENCRYPT, ANONYMIZE, MASK
    
    @Column(length = 2000)
    private String retentionPeriod; // e.g., "7 years", "90 days", "indefinite"
    
    private Boolean requiresConsent = false;
    private Boolean hasRightToErasure = false;
    private Boolean hasDataPortability = false;
    
    @Column(length = 2000)
    private String accessRestrictions;
    
    @Column(length = 2000)
    private String processingRules;
    
    @ElementCollection
    private List<String> authorizedRoles = new ArrayList<>();
    
    @ElementCollection
    private List<String> restrictedCountries = new ArrayList<>();
    
    @ElementCollection
    private Map<String, Object> metadata = new HashMap<>();
    
    private Boolean isActive = true;
    private Integer priority = 0;
    
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String lastModifiedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public DataPrivacyClassification() {
        this.classificationId = "DPC_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.piiTypes = new ArrayList<>();
        this.sensitiveFields = new ArrayList<>();
        this.requiredActions = new ArrayList<>();
        this.authorizedRoles = new ArrayList<>();
        this.restrictedCountries = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public DataPrivacyClassification(String name, PrivacyLevel privacyLevel, DataCategory dataCategory) {
        this();
        this.name = name;
        this.privacyLevel = privacyLevel;
        this.dataCategory = dataCategory;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getClassificationId() { return classificationId; }
    public void setClassificationId(String classificationId) { this.classificationId = classificationId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public PrivacyLevel getPrivacyLevel() { return privacyLevel; }
    public void setPrivacyLevel(PrivacyLevel privacyLevel) { this.privacyLevel = privacyLevel; }
    
    public DataCategory getDataCategory() { return dataCategory; }
    public void setDataCategory(DataCategory dataCategory) { this.dataCategory = dataCategory; }
    
    public String getComplianceStandard() { return complianceStandard; }
    public void setComplianceStandard(String complianceStandard) { this.complianceStandard = complianceStandard; }
    
    public List<String> getPiiTypes() { return piiTypes; }
    public void setPiiTypes(List<String> piiTypes) { this.piiTypes = piiTypes; }
    
    public List<String> getSensitiveFields() { return sensitiveFields; }
    public void setSensitiveFields(List<String> sensitiveFields) { this.sensitiveFields = sensitiveFields; }
    
    public List<String> getRequiredActions() { return requiredActions; }
    public void setRequiredActions(List<String> requiredActions) { this.requiredActions = requiredActions; }
    
    public String getRetentionPeriod() { return retentionPeriod; }
    public void setRetentionPeriod(String retentionPeriod) { this.retentionPeriod = retentionPeriod; }
    
    public Boolean getRequiresConsent() { return requiresConsent; }
    public void setRequiresConsent(Boolean requiresConsent) { this.requiresConsent = requiresConsent; }
    
    public Boolean getHasRightToErasure() { return hasRightToErasure; }
    public void setHasRightToErasure(Boolean hasRightToErasure) { this.hasRightToErasure = hasRightToErasure; }
    
    public Boolean getHasDataPortability() { return hasDataPortability; }
    public void setHasDataPortability(Boolean hasDataPortability) { this.hasDataPortability = hasDataPortability; }
    
    public String getAccessRestrictions() { return accessRestrictions; }
    public void setAccessRestrictions(String accessRestrictions) { this.accessRestrictions = accessRestrictions; }
    
    public String getProcessingRules() { return processingRules; }
    public void setProcessingRules(String processingRules) { this.processingRules = processingRules; }
    
    public List<String> getAuthorizedRoles() { return authorizedRoles; }
    public void setAuthorizedRoles(List<String> authorizedRoles) { this.authorizedRoles = authorizedRoles; }
    
    public List<String> getRestrictedCountries() { return restrictedCountries; }
    public void setRestrictedCountries(List<String> restrictedCountries) { this.restrictedCountries = restrictedCountries; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    
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
    
    public boolean isHighlySensitive() {
        return privacyLevel == PrivacyLevel.RESTRICTED || privacyLevel == PrivacyLevel.CONFIDENTIAL;
    }
    
    public boolean containsPII() {
        return piiTypes != null && !piiTypes.isEmpty();
    }
    
    public boolean requiresEncryption() {
        return requiredActions != null && requiredActions.contains("ENCRYPT");
    }
    
    public boolean requiresAnonymization() {
        return requiredActions != null && requiredActions.contains("ANONYMIZE");
    }
    
    public boolean requiresMasking() {
        return requiredActions != null && requiredActions.contains("MASK");
    }
    
    public boolean isGDPRCompliant() {
        return complianceStandard != null && complianceStandard.toUpperCase().contains("GDPR");
    }
    
    public boolean isHIPAACompliant() {
        return complianceStandard != null && complianceStandard.toUpperCase().contains("HIPAA");
    }
    
    public boolean hasRetentionPolicy() {
        return retentionPeriod != null && !retentionPeriod.trim().isEmpty();
    }
    
    public void addPIIType(String piiType) {
        if (piiType != null && !piiTypes.contains(piiType)) {
            piiTypes.add(piiType);
        }
    }
    
    public void removePIIType(String piiType) {
        piiTypes.remove(piiType);
    }
    
    public void addSensitiveField(String fieldName) {
        if (fieldName != null && !sensitiveFields.contains(fieldName)) {
            sensitiveFields.add(fieldName);
        }
    }
    
    public void removeSensitiveField(String fieldName) {
        sensitiveFields.remove(fieldName);
    }
    
    public void addRequiredAction(String action) {
        if (action != null && !requiredActions.contains(action)) {
            requiredActions.add(action);
        }
    }
    
    public void removeRequiredAction(String action) {
        requiredActions.remove(action);
    }
    
    public void addAuthorizedRole(String role) {
        if (role != null && !authorizedRoles.contains(role)) {
            authorizedRoles.add(role);
        }
    }
    
    public void removeAuthorizedRole(String role) {
        authorizedRoles.remove(role);
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setEffectivePeriod(LocalDateTime from, LocalDateTime to) {
        this.effectiveFrom = from;
        this.effectiveTo = to;
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
    
    // Enums
    public enum PrivacyLevel {
        PUBLIC,
        INTERNAL,
        CONFIDENTIAL,
        RESTRICTED
    }
    
    public enum DataCategory {
        PERSONAL_DATA,
        FINANCIAL_DATA,
        HEALTH_DATA,
        EDUCATIONAL_DATA,
        EMPLOYMENT_DATA,
        LOCATION_DATA,
        BIOMETRIC_DATA,
        GENETIC_DATA,
        BEHAVIORAL_DATA,
        TECHNICAL_DATA,
        OTHER
    }
    
    @Override
    public String toString() {
        return "DataPrivacyClassification{" +
                "classificationId='" + classificationId + '\'' +
                ", name='" + name + '\'' +
                ", privacyLevel=" + privacyLevel +
                ", dataCategory=" + dataCategory +
                ", complianceStandard='" + complianceStandard + '\'' +
                ", piiTypesCount=" + (piiTypes != null ? piiTypes.size() : 0) +
                ", isActive=" + isActive +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPrivacyClassification that = (DataPrivacyClassification) o;
        return Objects.equals(classificationId, that.classificationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(classificationId);
    }
}