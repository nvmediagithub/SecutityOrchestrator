package org.example.infrastructure.services.monitoring;

/**
 * Alert severity levels for monitoring system
 */
public enum AlertSeverity {
    INFO("info", "Informational message"),
    WARNING("warning", "Warning condition"),
    ERROR("error", "Error condition"),
    CRITICAL("critical", "Critical condition");
    
    private final String key;
    private final String description;
    
    AlertSeverity(String key, String description) {
        this.key = key;
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AlertSeverity fromKey(String key) {
        for (AlertSeverity severity : values()) {
            if (severity.getKey().equals(key)) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Unknown alert severity: " + key);
    }
}