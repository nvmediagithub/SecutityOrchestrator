package org.example.shared.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Domain entity for Validation Summary
 * Represents a summary of validation results for reporting purposes
 */
@Entity
@Table(name = "validation_summaries")
public class ValidationSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specificationTitle;

    @Column(nullable = false)
    private String specificationVersion;

    @Column(nullable = false)
    private Integer totalValidations = 0;

    @Column(nullable = false)
    private Integer totalErrors = 0;

    @Column(nullable = false)
    private Integer totalWarnings = 0;

    @Column(nullable = false)
    private Integer totalValidSpecs = 0;

    @Column(nullable = false)
    private Integer totalInvalidSpecs = 0;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column(columnDefinition = "TEXT")
    private String summaryReport;

    // Constructors
    public ValidationSummary() {
        this.generatedAt = LocalDateTime.now();
    }

    public ValidationSummary(String specificationTitle, String specificationVersion) {
        this();
        this.specificationTitle = specificationTitle;
        this.specificationVersion = specificationVersion;
    }

    // Business methods
    public void incrementValidationCount() {
        this.totalValidations++;
    }

    public void addValidationResult(ValidationResult result) {
        incrementValidationCount();
        if (result.isValid()) {
            totalValidSpecs++;
        } else {
            totalInvalidSpecs++;
            totalErrors += result.getTotalErrors();
            totalWarnings += result.getTotalWarnings();
        }
        this.generatedAt = LocalDateTime.now();
    }

    public double getErrorRate() {
        return totalValidations > 0 ? (double) totalInvalidSpecs / totalValidations * 100 : 0.0;
    }

    public double getSuccessRate() {
        return totalValidations > 0 ? (double) totalValidSpecs / totalValidations * 100 : 0.0;
    }

    public void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("OpenAPI Validation Summary Report\n");
        report.append("================================\n\n");
        report.append("Specification: ").append(specificationTitle).append(" v").append(specificationVersion).append("\n");
        report.append("Generated: ").append(generatedAt).append("\n\n");
        report.append("Statistics:\n");
        report.append("- Total validations: ").append(totalValidations).append("\n");
        report.append("- Valid specifications: ").append(totalValidSpecs).append("\n");
        report.append("- Invalid specifications: ").append(totalInvalidSpecs).append("\n");
        report.append("- Total errors: ").append(totalErrors).append("\n");
        report.append("- Total warnings: ").append(totalWarnings).append("\n");
        report.append("- Success rate: ").append(String.format("%.2f", getSuccessRate())).append("%\n");
        report.append("- Error rate: ").append(String.format("%.2f", getErrorRate())).append("%\n");

        this.summaryReport = report.toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecificationTitle() {
        return specificationTitle;
    }

    public void setSpecificationTitle(String specificationTitle) {
        this.specificationTitle = specificationTitle;
    }

    public String getSpecificationVersion() {
        return specificationVersion;
    }

    public void setSpecificationVersion(String specificationVersion) {
        this.specificationVersion = specificationVersion;
    }

    public Integer getTotalValidations() {
        return totalValidations;
    }

    public void setTotalValidations(Integer totalValidations) {
        this.totalValidations = totalValidations;
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

    public Integer getTotalValidSpecs() {
        return totalValidSpecs;
    }

    public void setTotalValidSpecs(Integer totalValidSpecs) {
        this.totalValidSpecs = totalValidSpecs;
    }

    public Integer getTotalInvalidSpecs() {
        return totalInvalidSpecs;
    }

    public void setTotalInvalidSpecs(Integer totalInvalidSpecs) {
        this.totalInvalidSpecs = totalInvalidSpecs;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getSummaryReport() {
        return summaryReport;
    }

    public void setSummaryReport(String summaryReport) {
        this.summaryReport = summaryReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationSummary that = (ValidationSummary) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ValidationSummary{" +
                "id=" + id +
                ", specificationTitle='" + specificationTitle + '\'' +
                ", totalValidations=" + totalValidations +
                ", totalErrors=" + totalErrors +
                ", totalValidSpecs=" + totalValidSpecs +
                ", generatedAt=" + generatedAt +
                '}';
    }
}