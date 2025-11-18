package org.example.shared.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity for OpenAPI Validation Result
 * Represents the result of validating an OpenAPI specification
 */
@Entity
@Table(name = "validation_results")
public class ValidationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specification_id", nullable = false)
    private OpenAPISpecification specification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationStatus status;

    @Column(nullable = false)
    private LocalDateTime validatedAt;

    @Column(nullable = false)
    private Integer totalErrors = 0;

    @Column(nullable = false)
    private Integer totalWarnings = 0;

    @OneToMany(mappedBy = "validationResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ValidationError> errors = new ArrayList<>();

    // Constructors
    public ValidationResult() {
        this.validatedAt = LocalDateTime.now();
        this.status = ValidationStatus.PENDING;
    }

    public ValidationResult(OpenAPISpecification specification) {
        this();
        this.specification = specification;
    }

    // Business methods
    public void markAsValid() {
        this.status = ValidationStatus.VALID;
        this.validatedAt = LocalDateTime.now();
    }

    public void markAsInvalid() {
        this.status = ValidationStatus.INVALID;
        this.validatedAt = LocalDateTime.now();
    }

    public void addError(ValidationError error) {
        errors.add(error);
        error.setValidationResult(this);
        if (error.getSeverity() == ValidationError.Severity.ERROR) {
            totalErrors++;
        } else if (error.getSeverity() == ValidationError.Severity.WARNING) {
            totalWarnings++;
        }
        this.validatedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return status == ValidationStatus.VALID;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OpenAPISpecification getSpecification() {
        return specification;
    }

    public void setSpecification(OpenAPISpecification specification) {
        this.specification = specification;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public void setStatus(ValidationStatus status) {
        this.status = status;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public Integer getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(Integer totalErrors) {
        this.totalErrors = totalErrors;
    }

    public Integer getTotalWarnings() {
        return totalWarnings;
    }

    public void setTotalWarnings(Integer totalWarnings) {
        this.totalWarnings = totalWarnings;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "id=" + id +
                ", status=" + status +
                ", validatedAt=" + validatedAt +
                ", totalErrors=" + totalErrors +
                ", totalWarnings=" + totalWarnings +
                '}';
    }

    // Enum for validation status
    public enum ValidationStatus {
        PENDING,
        VALID,
        INVALID,
        FAILED
    }
}