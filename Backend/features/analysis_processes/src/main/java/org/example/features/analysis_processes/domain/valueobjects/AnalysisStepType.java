package org.example.features.analysis_processes.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;

public enum AnalysisStepType {
    COLLECT_INPUTS,
    LLM_ANALYSIS,
    HTTP_REQUEST,
    TEST_EXECUTION;

    @JsonValue
    public String toJson() {
        return name().toLowerCase(Locale.ROOT);
    }

    @JsonCreator
    public static AnalysisStepType fromJson(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim()
            .replace('-', '_')
            .toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
            .filter(type -> type.name().equals(normalized))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unsupported analysis step type: " + value));
    }
}
