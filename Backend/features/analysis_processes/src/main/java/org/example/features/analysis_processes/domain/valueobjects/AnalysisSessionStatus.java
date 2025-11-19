package org.example.features.analysis_processes.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;

public enum AnalysisSessionStatus {
    WAITING_FOR_INPUT,
    RUNNING,
    WAITING_FOR_TEST,
    COMPLETED,
    FAILED;

    @JsonValue
    public String toJson() {
        return name().toLowerCase(Locale.ROOT);
    }

    @JsonCreator
    public static AnalysisSessionStatus fromJson(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim()
            .replace('-', '_')
            .toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
            .filter(status -> status.name().equals(normalized))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unsupported analysis session status: " + value));
    }
}
