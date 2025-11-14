package org.example.shared.core.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique identifier for AI models.
 * This class ensures type safety and immutability for model IDs.
 */
public class ModelId {
    private final UUID value;

    public ModelId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Model ID cannot be null");
        }
        this.value = value;
    }

    public ModelId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Model ID string cannot be null or empty");
        }
        try {
            this.value = UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for Model ID: " + value, e);
        }
    }

    public static ModelId generate() {
        return new ModelId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ModelId modelId = (ModelId) obj;
        return Objects.equals(value, modelId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}