package org.example.domain.valueobjects;

/**
 * Enumeration of severity levels for process issues
 */
public enum ProcessIssueSeverity {
    /**
     * Critical issues that must be addressed immediately
     */
    CRITICAL,
    
    /**
     * High priority issues that should be addressed soon
     */
    HIGH,
    
    /**
     * Medium priority issues
     */
    MEDIUM,
    
    /**
     * Low priority issues or minor improvements
     */
    LOW
}