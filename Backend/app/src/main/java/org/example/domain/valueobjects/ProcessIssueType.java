package org.example.domain.valueobjects;

/**
 * Enumeration of different types of issues that can be found in BPMN processes
 */
public enum ProcessIssueType {
    /**
     * Logic errors in the process flow
     */
    LOGIC_ERROR,
    
    /**
     * Performance-related issues
     */
    PERFORMANCE,
    
    /**
     * Compliance violations
     */
    COMPLIANCE,
    
    /**
     * Security vulnerabilities
     */
    SECURITY,
    
    /**
     * Validation errors
     */
    VALIDATION
}