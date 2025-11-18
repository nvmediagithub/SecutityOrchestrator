package org.example.features.llm.domain.valueobjects;

/**
 * Value object representing model status
 */
public enum ModelStatus {

    ACTIVE("active"),
    INACTIVE("inactive"),
    DEPRECATED("deprecated");

    private final String value;

    ModelStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Create ModelStatus from string value
     * @param value The string value
     * @return The corresponding ModelStatus
     * @throws IllegalArgumentException if value is invalid
     */
    public static ModelStatus fromString(String value) {
        for (ModelStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ModelStatus value: " + value);
    }
}