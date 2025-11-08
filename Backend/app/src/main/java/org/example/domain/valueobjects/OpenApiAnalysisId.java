package org.example.domain.valueobjects;

/**
 * Value object representing a unique identifier for OpenAPI analysis
 */
public class OpenApiAnalysisId {
    private final String value;

    private OpenApiAnalysisId(String value) {
        this.value = value;
    }

    public static OpenApiAnalysisId generate() {
        return new OpenApiAnalysisId("analysis-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000));
    }

    public static OpenApiAnalysisId fromString(String value) {
        return new OpenApiAnalysisId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OpenApiAnalysisId that = (OpenApiAnalysisId) obj;
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