package org.example.features.testdata.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a constraint on a data field for test data generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldConstraint {
    private String constraintType;
    private String constraintValue;
    private String errorMessage;

    public enum ConstraintType {
        NOT_NULL,
        MIN_LENGTH,
        MAX_LENGTH,
        MIN_VALUE,
        MAX_VALUE,
        PATTERN,
        CUSTOM
    }
}