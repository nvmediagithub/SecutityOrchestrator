package org.example.bpmn.domain.valueobjects;

/**
 * Value object representing a unique identifier for a BPMN element
 */
public class BpmnElementId {
    private final String value;

    private BpmnElementId(String value) {
        this.value = value;
    }

    /**
     * Generates a new unique BPMN element ID
     * @return a new BpmnElementId instance
     */
    public static BpmnElementId generate() {
        return new BpmnElementId("element-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
    }

    /**
     * Creates a BpmnElementId from a string value
     * @param value the string representation of the ID
     * @return a new BpmnElementId instance
     */
    public static BpmnElementId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("BPMN element ID cannot be null or empty");
        }
        return new BpmnElementId(value.trim());
    }

    /**
     * Gets the string value of the ID
     * @return the ID value as string
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BpmnElementId that = (BpmnElementId) obj;
        return value.equals(that.value);
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