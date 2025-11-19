package org.example.features.analysis_processes.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InputRequirement {

    @JsonProperty("name")
    private String name;

    @JsonProperty("label")
    private String label;

    @JsonProperty("description")
    private String description;

    @JsonProperty("required")
    private boolean required;

    public InputRequirement() {
        // For Jackson
    }

    public InputRequirement(String name, String label, String description, boolean required) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public InputRequirement withDefaults() {
        return new InputRequirement(
            Objects.requireNonNullElse(name, "input_" + System.nanoTime()),
            label != null ? label : humanize(name),
            description,
            required
        );
    }

    private String humanize(String value) {
        if (value == null || value.isBlank()) {
            return "Input";
        }
        String normalized = value.replace('_', ' ').replace('-', ' ');
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }
}
