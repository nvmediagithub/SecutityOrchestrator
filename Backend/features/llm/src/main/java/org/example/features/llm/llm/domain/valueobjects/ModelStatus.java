package org.example.features.llm.domain.valueobjects;

/**
 * Value object representing the status of an AI model.
 * This enum defines the possible states a model can be in.
 */
public enum ModelStatus {
    AVAILABLE("Available"),
    LOADING("Loading"),
    LOADED("Loaded"),
    UNLOADING("Unloading"),
    UNLOADED("Unloaded"),
    ERROR("Error"),
    DISABLED("Disabled");

    private final String displayName;

    ModelStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == LOADED || this == AVAILABLE;
    }

    public boolean isInactive() {
        return this == UNLOADED || this == DISABLED;
    }

    public boolean isTransitioning() {
        return this == LOADING || this == UNLOADING;
    }

    public boolean hasError() {
        return this == ERROR;
    }
}