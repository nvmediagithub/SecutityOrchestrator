package org.example.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_masking_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataMaskingRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "field_name", nullable = false)
    private String fieldName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "masking_type", nullable = false)
    private MaskingType maskingType;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    @Column(name = "created_by")
    private String createdBy;
    
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
    
    public enum MaskingType {
        FULL_MASK,
        PARTIAL_MASK,
        TOKENIZE,
        HASH,
        ENCRYPT,
        RANDOMIZE,
        REDACT
    }
}