package org.example.bpmn.domain.valueobjects;

/**
 * Value object representing a unique identifier for a BPMN process
 */
public class BpmnProcessId {
    private final String value;

    private BpmnProcessId(String value) {
        this.value = value;
    }

    /**
     * Generates a new unique BPMN process ID
     * @return a new BpmnProcessId instance
     */
    public static BpmnProcessId generate() {
        return new BpmnProcessId("bpmn-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
    }

    /**
     * Creates a BpmnProcessId from a string value
     * @param value the string representation of the ID
     * @return a new BpmnProcessId instance
     */
    public static BpmnProcessId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("BPMN process ID cannot be null or empty");
        }
        return new BpmnProcessId(value.trim());
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
        BpmnProcessId that = (BpmnProcessId) obj;
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