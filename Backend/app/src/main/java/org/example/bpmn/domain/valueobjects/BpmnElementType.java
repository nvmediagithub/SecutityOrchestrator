package org.example.bpmn.domain.valueobjects;

/**
 * Value object representing BPMN element types
 */
public enum BpmnElementType {
    TASK,
    EVENT,
    GATEWAY,
    SEQUENCE_FLOW,
    SUB_PROCESS,
    CALL_ACTIVITY,
    DATA_OBJECT,
    DATA_STORE,
    MESSAGE,
    SIGNAL,
    TIMER,
    ERROR,
    ESCALATION,
    COMPENSATION,
    CONDITIONAL,
    MULTIPLE,
    PARALLEL_MULTIPLE,
    NON_INTERRUPTING_MULTIPLE,
    CANCEL,
    TERMINATE;

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", "-");
    }

    /**
     * Creates a BpmnElementType from string value
     * @param value string representation
     * @return corresponding enum value
     */
    public static BpmnElementType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Element type cannot be null or empty");
        }
        String normalized = value.toUpperCase().replace("-", "_");
        return BpmnElementType.valueOf(normalized);
    }
}