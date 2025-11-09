package org.example.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "data_privacy_classifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPrivacyClassification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "data_content", columnDefinition = "TEXT", nullable = false)
    private String dataContent;
    
    @Column(name = "data_type", nullable = false)
    private String dataType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level", nullable = false)
    private PrivacyLevel privacyLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "data_category", nullable = false)
    private DataCategory dataCategory;
    
    @ElementCollection
    @CollectionTable(name = "pii_types", joinColumns = @JoinColumn(name = "classification_id"))
    @Column(name = "pii_type")
    private List<String> piiTypes;
    
    @Column(name = "compliance_standards")
    private String complianceStandards;
    
    @ElementCollection
    @CollectionTable(name = "required_actions", joinColumns = @JoinColumn(name = "classification_id"))
    @Column(name = "action")
    private List<String> requiredActions;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PrivacyLevel {
        PUBLIC,
        INTERNAL,
        CONFIDENTIAL,
        RESTRICTED,
        HIGHLY_CONFIDENTIAL
    }
    
    public enum DataCategory {
        PERSONAL_DATA,
        FINANCIAL_DATA,
        HEALTH_DATA,
        BUSINESS_DATA,
        TECHNICAL_DATA,
        BEHAVIORAL_DATA,
        CONTENT_DATA
    }
}