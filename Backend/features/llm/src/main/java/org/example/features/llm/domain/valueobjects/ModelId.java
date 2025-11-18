package org.example.features.llm.domain.valueobjects;

import java.util.UUID;

/**
 * Value object representing a model identifier
 */
public class ModelId {

    private final String value;

    // Private constructor to enforce factory method usage
    private ModelId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Model ID cannot be null or empty");
        }
        this.value = value;
    }

    // Factory method for creating new ModelId
    public static ModelId generate() {
        return new ModelId(UUID.randomUUID().toString());
    }

    // Factory method for creating ModelId from existing string
    public static ModelId fromString(String value) {
        return new ModelId(value);
    }

    // Get the string value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ModelId modelId = (ModelId) obj;
        return value.equals(modelId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}