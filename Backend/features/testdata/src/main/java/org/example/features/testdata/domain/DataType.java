package org.example.features.testdata.domain;

/**
 * Enumeration of supported data types for test data generation
 */
public enum DataType {
    STRING,
    INTEGER,
    LONG,
    DOUBLE,
    DECIMAL,
    BOOLEAN,
    DATE,
    DATETIME,
    UUID,
    EMAIL,
    PHONE,
    URL,
    ADDRESS,
    CUSTOM;

    public static DataType fromString(String value) {
        if (value == null) {
            return STRING;
        }
        try {
            return DataType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STRING; // default fallback
        }
    }
}