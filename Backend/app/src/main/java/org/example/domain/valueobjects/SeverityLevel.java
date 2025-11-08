package org.example.domain.valueobjects;

public enum SeverityLevel {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    INFO,
    ERROR,
    WARNING;
    
    public static SeverityLevel fromString(String level) {
        if (level == null) return MEDIUM;
        
        String upperLevel = level.toUpperCase();
        try {
            return SeverityLevel.valueOf(upperLevel);
        } catch (IllegalArgumentException e) {
            // Fallback mappings
            switch (upperLevel) {
                case "FATAL":
                case "SEVERE":
                    return CRITICAL;
                case "MAJOR":
                case "IMPORTANT":
                    return HIGH;
                case "MINOR":
                case "SUGGESTION":
                    return LOW;
                case "DEBUG":
                case "TRACE":
                    return INFO;
                default:
                    return MEDIUM;
            }
        }
    }
    
    public int getPriority() {
        switch (this) {
            case CRITICAL: return 1;
            case HIGH: return 2;
            case MEDIUM: return 3;
            case LOW: return 4;
            case INFO: return 5;
            case ERROR: return 1;
            case WARNING: return 3;
            default: return 3;
        }
    }
    
    public boolean isHigherThan(SeverityLevel other) {
        return this.getPriority() < other.getPriority();
    }
}