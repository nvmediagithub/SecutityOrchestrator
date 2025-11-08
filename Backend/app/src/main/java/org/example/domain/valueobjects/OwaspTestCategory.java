package org.example.domain.valueobjects;

import java.util.Arrays;
import java.util.List;

/**
 * OWASP API Security Test Categories (OWASP API Security Top 10 2023+)
 * https://owasp.org/www-project-api-security/
 * 
 * Updated to reflect the latest OWASP API Security Top 10 (2023) which includes:
 * - A01:2023 Broken Object Level Authorization
 * - A02:2023 Broken Authentication  
 * - A03:2023 Broken Object Property Level Authorization
 * - A04:2023 Unrestricted Resource Consumption
 * - A05:2023 Broken Function Level Authorization
 * - A06:2023 Unrestricted Access to Sensitive Business Workflows
 * - A07:2023 Server Side Request Forgery
 * - A08:2023 Security Misconfiguration
 * - A09:2023 Improper Inventory Management
 * - A10:2023 Unsafe Consumption of APIs
 */
public enum OwaspTestCategory {
    
    // OWASP API Security Top 10 (2023)
    A01_2023_BROKEN_OBJECT_LEVEL_AUTHORIZATION(
        "A01:2023",
        "Broken Object Level Authorization",
        "Test scenarios for Insecure Direct Object References (IDOR) and broken object level authorization",
        Arrays.asList(
            "Object ID enumeration",
            "Direct object reference testing",
            "IDOR vulnerability testing",
            "Object-level access control bypass",
            "Horizontal privilege escalation through objects",
            "Parameter pollution for object access"
        )
    ),
    
    A02_2023_BROKEN_AUTHENTICATION(
        "A02:2023", 
        "Broken Authentication",
        "Test scenarios for authentication weaknesses, credential stuffing, and session management flaws",
        Arrays.asList(
            "Credential stuffing attacks",
            "Brute force authentication",
            "Session token manipulation",
            "JWT token vulnerabilities",
            "Multi-factor authentication bypass",
            "Password policy weaknesses",
            "Account enumeration",
            "Session fixation attacks"
        )
    ),
    
    A03_2023_BROKEN_OBJECT_PROPERTY_LEVEL_AUTHORIZATION(
        "A03:2023",
        "Broken Object Property Level Authorization", 
        "Test scenarios for excessive data exposure and property-level authorization issues",
        Arrays.asList(
            "Property enumeration attacks",
            "Excessive data exposure",
            "Property-level access control bypass",
            "Mass assignment vulnerabilities",
            "Property injection attacks",
            "Field-level authorization testing"
        )
    ),
    
    A04_2023_UNRESTRICTED_RESOURCE_CONSUMPTION(
        "A04:2023",
        "Unrestricted Resource Consumption",
        "Test scenarios for DoS attacks, resource exhaustion, and rate limiting bypasses",
        Arrays.asList(
            "Large payload attacks",
            "Resource exhaustion through requests",
            "Rate limiting bypass",
            "Memory exhaustion attacks",
            "CPU consumption attacks",
            "Concurrent request flooding",
            "Database query complexity attacks"
        )
    ),
    
    A05_2023_BROKEN_FUNCTION_LEVEL_AUTHORIZATION(
        "A05:2023", 
        "Broken Function Level Authorization",
        "Test scenarios for privilege escalation and function-level access control",
        Arrays.asList(
            "Horizontal privilege escalation",
            "Vertical privilege escalation", 
            "Function-level access control bypass",
            "Method access control testing",
            "Endpoint privilege escalation",
            "Administrative function access"
        )
    ),
    
    A06_2023_UNRESTRICTED_ACCESS_TO_SENSITIVE_BUSINESS_WORKFLOWS(
        "A06:2023",
        "Unrestricted Access to Sensitive Business Workflows",
        "Test scenarios for bypassing business process controls and workflow restrictions",
        Arrays.asList(
            "Business logic bypass",
            "Workflow sequence manipulation",
            "State transition attacks",
            "Business rule circumvention",
            "Multi-step process manipulation",
            "Competitive process exploitation"
        )
    ),
    
    A07_2023_SERVER_SIDE_REQUEST_FORGERY(
        "A07:2023",
        "Server Side Request Forgery",
        "Test scenarios for SSRF vulnerabilities and server-side request manipulation",
        Arrays.asList(
            "SSRF through URL parameters",
            "SSRF through file uploads",
            "SSRF through redirects",
            "Internal network access",
            "Cloud metadata access",
            "Port scanning through SSRF"
        )
    ),
    
    A08_2023_SECURITY_MISCONFIGURATION(
        "A08:2023",
        "Security Misconfiguration", 
        "Test scenarios for security configuration issues, default settings, and insecure defaults",
        Arrays.asList(
            "CORS misconfiguration",
            "Security header validation",
            "Default credential testing",
            "Debug mode exposure",
            "Insecure server configuration",
            "Information disclosure",
            "Verbose error messages",
            "Insecure HTTP methods"
        )
    ),
    
    A09_2023_IMPROPER_INVENTORY_MANAGEMENT(
        "A09:2023",
        "Improper Inventory Management",
        "Test scenarios for API version management, deprecated endpoints, and asset inventory issues",
        Arrays.asList(
            "Version enumeration",
            "Deprecated endpoint testing",
            "Shadow API discovery",
            "API documentation exposure",
            "Beta endpoint testing",
            "Development environment exposure"
        )
    ),
    
    A10_2023_UNSAFE_CONSUMPTION_OF_APIS(
        "A10:2023",
        "Unsafe Consumption of APIs",
        "Test scenarios for insecure API integrations, trust issues, and third-party API vulnerabilities",
        Arrays.asList(
            "Insecure API integrations",
            "Trust boundary violations",
            "Third-party API vulnerabilities",
            "Input validation from external APIs",
            "API chain exploitation",
            "Insufficient logging from external APIs"
        )
    );
    
    private final String categoryCode;
    private final String categoryName;
    private final String description;
    private final List<String> testTypes;
    
    OwaspTestCategory(String categoryCode, String categoryName, String description, List<String> testTypes) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.description = description;
        this.testTypes = testTypes;
    }
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<String> getTestTypes() {
        return testTypes;
    }
    
    public static OwaspTestCategory fromCode(String code) {
        for (OwaspTestCategory category : values()) {
            if (category.getCategoryCode().equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown OWASP test category: " + code);
    }
    
    // Group categories by risk level
    public static List<OwaspTestCategory> getCriticalCategories() {
        return Arrays.asList(
            A01_2023_BROKEN_OBJECT_LEVEL_AUTHORIZATION,
            A02_2023_BROKEN_AUTHENTICATION,
            A05_2023_BROKEN_FUNCTION_LEVEL_AUTHORIZATION
        );
    }
    
    public static List<OwaspTestCategory> getHighRiskCategories() {
        return Arrays.asList(
            A03_2023_BROKEN_OBJECT_PROPERTY_LEVEL_AUTHORIZATION,
            A06_2023_UNRESTRICTED_ACCESS_TO_SENSITIVE_BUSINESS_WORKFLOWS,
            A07_2023_SERVER_SIDE_REQUEST_FORGERY,
            A08_2023_SECURITY_MISCONFIGURATION
        );
    }
    
    public static List<OwaspTestCategory> getResourceCategories() {
        return Arrays.asList(
            A04_2023_UNRESTRICTED_RESOURCE_CONSUMPTION
        );
    }
    
    public static List<OwaspTestCategory> getInventoryCategories() {
        return Arrays.asList(
            A09_2023_IMPROPER_INVENTORY_MANAGEMENT
        );
    }
    
    public static List<OwaspTestCategory> getIntegrationCategories() {
        return Arrays.asList(
            A10_2023_UNSAFE_CONSUMPTION_OF_APIS
        );
    }
    
    // Legacy categories - return empty list since we're using OWASP 2023+
    public static List<OwaspTestCategory> getLegacy2019Categories() {
        return Arrays.asList();
    }
}