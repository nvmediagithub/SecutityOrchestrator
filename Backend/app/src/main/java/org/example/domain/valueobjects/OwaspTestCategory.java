package org.example.domain.valueobjects;

import java.util.Arrays;
import java.util.List;

/**
 * OWASP API Security Test Categories (OWASP API Security Top 10 2019)
 * https://owasp.org/www-project-api-security/
 */
public enum OwaspTestCategory {
    
    API1_2019_BROKEN_OBJECT_LEVEL_AUTHORIZATION(
        "API1:2019",
        "Broken Object Level Authorization (BOLA)",
        "Test scenarios for Insecure Direct Object References (IDOR) vulnerabilities",
        Arrays.asList(
            "Access other users' objects",
            "ID manipulation testing", 
            "Object-level access control testing"
        )
    ),
    
    API2_2019_BROKEN_USER_AUTHENTICATION(
        "API2:2019", 
        "Broken User Authentication",
        "Test scenarios for authentication weaknesses and bypasses",
        Arrays.asList(
            "Weak password policies",
            "Authentication token manipulation",
            "Session management testing"
        )
    ),
    
    API3_2019_EXCESSIVE_DATA_EXPOSURE(
        "API3:2019",
        "Excessive Data Exposure", 
        "Test scenarios for data leakage and information disclosure",
        Arrays.asList(
            "Metadata exposure testing",
            "Verbose error messages",
            "Sensitive data in responses"
        )
    ),
    
    API4_2019_LACK_OF_RESOURCES_RATE_LIMITING(
        "API4:2019",
        "Lack of Resources & Rate Limiting",
        "Test scenarios for DoS attacks and resource exhaustion",
        Arrays.asList(
            "Large request payload testing",
            "Rate limiting bypass",
            "Resource exhaustion attacks"
        )
    ),
    
    API5_2019_BROKEN_FUNCTION_LEVEL_AUTHORIZATION(
        "API5:2019", 
        "Broken Function Level Authorization",
        "Test scenarios for privilege escalation and function access control",
        Arrays.asList(
            "Horizontal privilege escalation",
            "Vertical privilege escalation", 
            "Function-level access control testing"
        )
    ),
    
    API6_2019_MASS_ASSIGNMENT(
        "API6:2019",
        "Mass Assignment",
        "Test scenarios for binding vulnerabilities and mass assignment attacks",
        Arrays.asList(
            "Unexpected field binding",
            "Privilege escalation through binding",
            "Property binding manipulation"
        )
    ),
    
    API7_2019_SECURITY_MISCONFIGURATION(
        "API7:2019",
        "Security Misconfiguration", 
        "Test scenarios for security configuration issues",
        Arrays.asList(
            "CORS policy testing",
            "Security header validation",
            "Default configuration testing"
        )
    ),
    
    API8_2019_INJECTION(
        "API8:2019",
        "Injection",
        "Test scenarios for various injection vulnerabilities",
        Arrays.asList(
            "SQL injection",
            "NoSQL injection", 
            "Command injection",
            "Template injection"
        )
    ),
    
    API9_2019_IMPROPER_ASSETS_MANAGEMENT(
        "API9:2019",
        "Improper Assets Management",
        "Test scenarios for API versioning and asset management issues",
        Arrays.asList(
            "Deprecated endpoint testing",
            "Version enumeration",
            "Test environment exposure"
        )
    ),
    
    API10_2019_INSUFFICIENT_LOGGING_MONITORING(
        "API10:2019",
        "Insufficient Logging & Monitoring",
        "Test scenarios for logging and monitoring gaps",
        Arrays.asList(
            "Security event logging",
            "Alerting mechanism testing", 
            "Forensic capability testing"
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
    
    public static List<OwaspTestCategory> getSecurityCategories() {
        return Arrays.asList(
            API1_2019_BROKEN_OBJECT_LEVEL_AUTHORIZATION,
            API2_2019_BROKEN_USER_AUTHENTICATION,
            API5_2019_BROKEN_FUNCTION_LEVEL_AUTHORIZATION,
            API6_2019_MASS_ASSIGNMENT,
            API7_2019_SECURITY_MISCONFIGURATION,
            API8_2019_INJECTION
        );
    }
    
    public static List<OwaspTestCategory> getDataExposureCategories() {
        return Arrays.asList(
            API3_2019_EXCESSIVE_DATA_EXPOSURE,
            API9_2019_IMPROPER_ASSETS_MANAGEMENT
        );
    }
    
    public static List<OwaspTestCategory> getPerformanceCategories() {
        return Arrays.asList(
            API4_2019_LACK_OF_RESOURCES_RATE_LIMITING
        );
    }
    
    public static List<OwaspTestCategory> getMonitoringCategories() {
        return Arrays.asList(
            API10_2019_INSUFFICIENT_LOGGING_MONITORING
        );
    }
}