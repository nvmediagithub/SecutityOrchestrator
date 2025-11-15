package org.example.features.testdata.domain;

/**
 * Value object representing different data types for test data generation
 */
public enum DataType {
    STRING("String"),
    INTEGER("Integer"),
    DECIMAL("Decimal"),
    BOOLEAN("Boolean"),
    DATE("Date"),
    DATETIME("DateTime"),
    EMAIL("Email"),
    PHONE("Phone"),
    URL("URL"),
    UUID("UUID"),
    JSON("JSON"),
    XML("XML"),
    CSV("CSV");

    private final String displayName;

    DataType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isNumeric() {
        return this == INTEGER || this == DECIMAL;
    }

    public boolean isText() {
        return this == STRING || this == EMAIL || this == PHONE || this == URL || this == UUID;
    }

    public boolean isTemporal() {
        return this == DATE || this == DATETIME;
    }

    public boolean isStructured() {
        return this == JSON || this == XML || this == CSV;
    }

    public static DataType fromString(String value) {
        if (value == null) return null;
        for (DataType type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return STRING; // Default fallback
    }
}