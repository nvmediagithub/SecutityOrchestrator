package org.example.domain.valueobjects;

/**
 * Enumeration of different types of issues that can be found in OpenAPI specifications
 */
public enum IssueType {
    SECURITY_VULNERABILITY("Security vulnerability in endpoint configuration"),
    VALIDATION_ERROR("Input/Output validation issues"),
    INCONSISTENCY("Inconsistent data or configuration"),
    DEPRECATION("Deprecated features or endpoints"),
    MISSING_DOCUMENTATION("Missing or incomplete documentation"),
    PERFORMANCE_CONCERN("Potential performance issues"),
    COMPLIANCE_VIOLATION("Non-compliance with standards"),
    AUTHENTICATION_ISSUE("Authentication configuration problems"),
    AUTHORIZATION_ISSUE("Authorization/permission issues"),
    RATE_LIMITING_MISSING("Missing rate limiting configuration"),
    ERROR_HANDLING_INSUFFICIENT("Insufficient error handling"),
    SCHEMA_INCONSISTENCY("Schema validation inconsistencies");

    private final String description;

    IssueType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}