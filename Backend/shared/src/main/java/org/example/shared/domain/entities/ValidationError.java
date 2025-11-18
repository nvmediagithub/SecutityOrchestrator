package org.example.shared.domain.entities;

import jakarta.persistence.*;

/**
 * Domain entity for Validation Error
 * Represents an individual validation error or warning in an OpenAPI specification
 */
@Entity
@Table(name = "validation_errors")
public class ValidationError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validation_result_id", nullable = false)
    private ValidationResult validationResult;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String path;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

    // Constructors
    public ValidationError() {}

    public ValidationError(Severity severity, String code, String message) {
        this.severity = severity;
        this.code = code;
        this.message = message;
    }

    public ValidationError(Severity severity, String code, String message, String path) {
        this(severity, code, message);
        this.path = path;
    }

    public ValidationError(Severity severity, String code, String message, String path, String suggestion) {
        this(severity, code, message, path);
        this.suggestion = suggestion;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationError that = (ValidationError) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "id=" + id +
                ", severity=" + severity +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    // Enum for error severity
    public enum Severity {
        ERROR,
        WARNING,
        INFO
    }
}