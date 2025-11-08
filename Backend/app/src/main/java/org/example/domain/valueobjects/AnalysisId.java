package org.example.domain.valueobjects;

/**
 * Value object representing a unique identifier for analysis
 */
public class AnalysisId {
    private final String value;

    private AnalysisId(String value) {
        this.value = value;
    }

    /**
     * Generates a new unique analysis ID
     * @return a new AnalysisId instance
     */
    public static AnalysisId generate() {
        return new AnalysisId("analysis-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000));
    }

    /**
     * Creates an AnalysisId from a string value
     * @param value the string representation of the ID
     * @return a new AnalysisId instance
     */
    public static AnalysisId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Analysis ID cannot be null or empty");
        }
        return new AnalysisId(value.trim());
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
        AnalysisId analysisId = (AnalysisId) obj;
        return value.equals(analysisId.value);
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