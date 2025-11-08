package org.example.domain.valueobjects;

/**
 * Enumeration of different types of security checks for OpenAPI analysis
 */
public enum SecurityCheckType {
    AUTHENTICATION_REQUIRED("Check if authentication is required"),
    AUTHORIZATION_CHECK("Verify proper authorization configuration"),
    INPUT_VALIDATION("Validate input parameters"),
    OUTPUT_SANITIZATION("Check output data sanitization"),
    RATE_LIMITING("Verify rate limiting implementation"),
    HTTPS_ENFORCEMENT("Check HTTPS enforcement"),
    SECURITY_HEADERS("Verify security headers configuration"),
    CORS_CONFIGURATION("Check CORS settings"),
    JWT_VALIDATION("Validate JWT token configuration"),
    API_KEY_PROTECTION("Check API key protection"),
    SENSITIVE_DATA_EXPOSURE("Check for sensitive data exposure"),
    INJECTION_VULNERABILITIES("Check for injection vulnerabilities"),
    SESSION_MANAGEMENT("Verify session management security"),
    ERROR_INFORMATION_DISCLOSURE("Check error handling for information disclosure");

    private final String description;

    SecurityCheckType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}