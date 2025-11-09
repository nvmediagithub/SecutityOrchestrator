package org.example.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "EndpointAnalysis")
@Table(name = "endpoint_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "endpoint_id", nullable = false)
    private String endpointId;
    
    @Column(name = "endpoint_path", nullable = false)
    private String endpointPath;
    
    @Column(name = "http_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "security_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private SecurityLevel securityLevel = SecurityLevel.UNKNOWN;
    
    @ElementCollection
    @CollectionTable(name = "endpoint_owasp_categories", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "category")
    private List<String> owaspCategories;
    
    @ElementCollection
    @CollectionTable(name = "endpoint_vulnerabilities", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "vulnerability")
    private List<String> vulnerabilities;
    
    @Column(name = "risk_score")
    private Double riskScore;
    
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
    
    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
    }
    
    public enum SecurityLevel {
        HIGH,
        MEDIUM,
        LOW,
        CRITICAL,
        UNKNOWN
    }
}