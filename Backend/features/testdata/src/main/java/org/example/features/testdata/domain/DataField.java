package org.example.features.testdata.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain entity representing a data field for test data generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataField {
    private Long id;
    private String fieldId;
    private String fieldName;
    private String description;
    private DataType dataType;
    private FieldStatus status;
    private List<FieldConstraint> constraints;
    private String defaultValue;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum FieldStatus {
        ACTIVE,
        INACTIVE,
        DEPRECATED
    }

    public boolean isActive() {
        return status == FieldStatus.ACTIVE;
    }
}