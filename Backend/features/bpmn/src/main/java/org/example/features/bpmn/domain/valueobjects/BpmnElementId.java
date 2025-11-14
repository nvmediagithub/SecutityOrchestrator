package org.example.features.bpmn.domain.valueobjects;

import java.util.Objects;

/**
 * Value object representing a BPMN element identifier
 */
public class BpmnElementId {
    private final String value;

    public BpmnElementId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("BPMN Element ID cannot be null or empty");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BpmnElementId that = (BpmnElementId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public static BpmnElementId fromString(String value) {
        return new BpmnElementId(value);
    }
}