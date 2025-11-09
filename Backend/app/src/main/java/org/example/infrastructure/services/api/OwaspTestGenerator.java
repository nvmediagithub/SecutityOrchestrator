package org.example.infrastructure.services.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.valueobjects.OwaspTestCategory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for generating OWASP API Security test cases
 * Implements comprehensive security testing based on OWASP API Security Top 10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OwaspTestGenerator {

    /**
     * Generate security test cases based on OWASP API Security Top 10
     */
    public Map<String, Object> generateSecurityTests(String openApiId, List<String> owaspCategories) {
        log.info("Generating OWASP security tests for OpenAPI: {} with categories: {}", openApiId, owaspCategories);
        
        Map<String, Object> testCases = new HashMap<>();
        
        for (String category : owaspCategories) {
            List<Map<String, Object>> categoryTests = generateTestsForCategory(category);
            testCases.put(category, categoryTests);
        }
        
        return testCases;
    }

    /**
     * Generate test scenarios combining OpenAPI endpoints with BPMN processes
     */
    public Map<String, Object> generateTestScenarios(String openApiId, String bpmnId) {
        log.info("Generating test scenarios for OpenAPI: {} and BPMN: {}", openApiId, bpmnId);
        
        Map<String, Object> scenarios = new HashMap<>();
        
        // Generate end-to-end test scenarios
        scenarios.put("endToEndScenarios", generateEndToEndScenarios());
        scenarios.put("securityScenarios", generateSecurityScenarios());
        scenarios.put("negativeScenarios", generateNegativeScenarios());
        scenarios.put("performanceScenarios", generatePerformanceScenarios());
        
        return scenarios;
    }

    /**
     * Generate tests for specific OWASP category
     */
    private List<Map<String, Object>> generateTestsForCategory(String category) {
        List<Map<String, Object>> tests = new ArrayList<>();
        
        switch (category) {
            case "API1":
                tests.addAll(generateBolaTests()); // Broken Object Level Authorization
                break;
            case "API2":
                tests.addAll(generateBuaTests()); // Broken User Authentication
                break;
            case "API3":
                tests.addAll(generateBoplaTests()); // Broken Object Property Level Authorization
                break;
            case "API4":
                tests.addAll(generateUrcTests()); // Unrestricted Resource Consumption
                break;
            case "API5":
                tests.addAll(generateBflaTests()); // Broken Function Level Authorization
                break;
            case "API6":
                tests.addAll(generateUasbfTests()); // Unrestricted Access to Sensitive Business Flows
                break;
            case "API7":
                tests.addAll(generateSsrfTests()); // Server Side Request Forgery
                break;
            case "API8":
                tests.addAll(generateSmTests()); // Security Misconfiguration
                break;
            case "API9":
                tests.addAll(generateIimTests()); // Improper Inventory Management
                break;
            case "API10":
                tests.addAll(generateUcaTests()); // Unsafe Consumption of APIs
                break;
            default:
                tests.addAll(generateGenericTests());
        }
        
        return tests;
    }

    /**
     * API1: Broken Object Level Authorization (BOLA) tests
     */
    private List<Map<String, Object>> generateBolaTests() {
        return Arrays.asList(
            createTestCase("BOLA-001", "Test IDOR vulnerability", "Test if user can access other users' resources by changing ID",
                Map.of("type", "security", "severity", "high", "owaspCategory", "API1")),
            createTestCase("BOLA-002", "Test authorization bypass", "Test if user can bypass authorization checks",
                Map.of("type", "security", "severity", "critical", "owaspCategory", "API1")),
            createTestCase("BOLA-003", "Test object property access", "Test if user can access restricted object properties",
                Map.of("type", "security", "severity", "high", "owaspCategory", "API1"))
        );
    }

    /**
     * API2: Broken User Authentication (BUA) tests
     */
    private List<Map<String, Object>> generateBuaTests() {
        return Arrays.asList(
            createTestCase("BUA-001", "Test weak password policy", "Test if weak passwords are accepted",
                Map.of("type", "security", "severity", "medium", "owaspCategory", "API2")),
            createTestCase("BUA-002", "Test authentication bypass", "Test if authentication can be bypassed",
                Map.of("type", "security", "severity", "critical", "owaspCategory", "API2")),
            createTestCase("BUA-003", "Test session management", "Test session management vulnerabilities",
                Map.of("type", "security", "severity", "high", "owaspCategory", "API2"))
        );
    }

    /**
     * API3: Broken Object Property Level Authorization (BOPLA) tests
     */
    private List<Map<String, Object>> generateBoplaTests() {
        return Arrays.asList(
            createTestCase("BOPLA-001", "Test property level access", "Test if user can access restricted object properties",
                Map.of("type", "security", "severity", "high", "owaspCategory", "API3")),
            createTestCase("BOPLA-002", "Test mass assignment", "Test mass assignment vulnerabilities",
                Map.of("type", "security", "severity", "medium", "owaspCategory", "API3"))
        );
    }

    /**
     * API4: Unrestricted Resource Consumption (URC) tests
     */
    private List<Map<String, Object>> generateUrcTests() {
        return Arrays.asList(
            createTestCase("URC-001", "Test DoS via large payload", "Test if system can handle large payloads",
                Map.of("type", "performance", "severity", "high", "owaspCategory", "API4")),
            createTestCase("URC-002", "Test resource exhaustion", "Test if system has resource limits",
                Map.of("type", "performance", "severity", "medium", "owaspCategory", "API4"))
        );
    }

    /**
     * API5: Broken Function Level Authorization (BFLA) tests
     */
    private List<Map<String, Object>> generateBflaTests() {
        return Arrays.asList(
            createTestCase("BFLA-001", "Test admin function access", "Test if regular users can access admin functions",
                Map.of("type", "security", "severity", "critical", "owaspCategory", "API5")),
            createTestCase("BFLA-002", "Test privilege escalation", "Test if users can escalate privileges",
                Map.of("type", "security", "severity", "high", "owaspCategory", "API5"))
        );
    }

    /**
     * API6: Unrestricted Access to Sensitive Business Flows (UASBF) tests
     */
    private List<Map<String, Object>> generateUasbfTests() {
        return Arrays.asList(
            createTestCase("UASBF-001", "Test business flow manipulation", "Test if business flows can be manipulated",
                Map.of("type", "business", "severity", "high", "owaspCategory", "API6")),
            createTestCase("UASBF-002", "Test unauthorized business operations", "Test unauthorized business operations",
                Map.of("type", "business", "severity", "critical", "owaspCategory", "API6"))
        );
    }

    /**
     * API7: Server Side Request Forgery (SSRF) tests
     */
    private List<Map<String, Object>> generateSsrfTests() {
        return Arrays.asList(
            createTestCase("SSRF-001", "Test internal network access", "Test if system can make requests to internal network",
                Map.of("type", "security", "severity", "high", "owaspCategory", "API7")),
            createTestCase("SSRF-002", "Test URL validation bypass", "Test URL validation bypass",
                Map.of("type", "security", "severity", "medium", "owaspCategory", "API7"))
        );
    }

    /**
     * API8: Security Misconfiguration (SM) tests
     */
    private List<Map<String, Object>> generateSmTests() {
        return Arrays.asList(
            createTestCase("SM-001", "Test default configurations", "Test for default/weak configurations",
                Map.of("type", "security", "severity", "medium", "owaspCategory", "API8")),
            createTestCase("SM-002", "Test information disclosure", "Test for information disclosure",
                Map.of("type", "security", "severity", "low", "owaspCategory", "API8"))
        );
    }

    /**
     * API9: Improper Inventory Management (IIM) tests
     */
    private List<Map<String, Object>> generateIimTests() {
        return Arrays.asList(
            createTestCase("IIM-001", "Test undocumented endpoints", "Test for undocumented/hidden endpoints",
                Map.of("type", "security", "severity", "medium", "owaspCategory", "API9")),
            createTestCase("IIM-002", "Test API versioning", "Test for version-related vulnerabilities",
                Map.of("type", "security", "severity", "low", "owaspCategory", "API9"))
        );
    }

    /**
     * API10: Unsafe Consumption of APIs (UCA) tests
     */
    private List<Map<String, Object>> generateUcaTests() {
        return Arrays.asList(
            createTestCase("UCA-001", "Test third-party API security", "Test security of consumed third-party APIs",
                Map.of("type", "security", "severity", "high", "owaspCategory", "API10")),
            createTestCase("UCA-002", "Test API trust boundaries", "Test trust boundaries with third-party APIs",
                Map.of("type", "security", "severity", "medium", "owaspCategory", "API10"))
        );
    }

    /**
     * Generate generic tests for unknown categories
     */
    private List<Map<String, Object>> generateGenericTests() {
        return Arrays.asList(
            createTestCase("GEN-001", "Test basic security", "Test basic security measures",
                Map.of("type", "security", "severity", "medium", "category", "generic"))
        );
    }

    /**
     * Generate end-to-end test scenarios
     */
    private List<Map<String, Object>> generateEndToEndScenarios() {
        return Arrays.asList(
            Map.of(
                "scenarioId", "E2E-001",
                "name", "Complete User Registration and Authentication Flow",
                "description", "Test complete user journey from registration to authenticated access",
                "steps", Arrays.asList(
                    "POST /api/register - Register new user",
                    "POST /api/login - Authenticate user",
                    "GET /api/user/profile - Access user profile",
                    "PUT /api/user/profile - Update user profile",
                    "DELETE /api/user/account - Delete account"
                ),
                "expectedResults", "Successful end-to-end flow with proper authorization at each step"
            ),
            Map.of(
                "scenarioId", "E2E-002",
                "name", "Payment Processing Workflow",
                "description", "Test complete payment processing workflow",
                "steps", Arrays.asList(
                    "GET /api/products - List available products",
                    "POST /api/cart - Add product to cart",
                    "POST /api/payment/initiate - Initiate payment",
                    "POST /api/payment/confirm - Confirm payment",
                    "GET /api/orders - Verify order creation"
                ),
                "expectedResults", "Successful payment flow with proper validation and security"
            )
        );
    }

    /**
     * Generate security-focused test scenarios
     */
    private List<Map<String, Object>> generateSecurityScenarios() {
        return Arrays.asList(
            Map.of(
                "scenarioId", "SEC-001",
                "name", "Authentication Security Test",
                "description", "Test various authentication security scenarios",
                "testCases", Arrays.asList(
                    "Test with invalid credentials",
                    "Test with expired tokens",
                    "Test with manipulated tokens",
                    "Test concurrent logins from different devices"
                )
            ),
            Map.of(
                "scenarioId", "SEC-002",
                "name", "Authorization Security Test",
                "description", "Test authorization edge cases",
                "testCases", Arrays.asList(
                    "Test access to other users' resources",
                    "Test privilege escalation attempts",
                    "Test admin function access without proper role"
                )
            )
        );
    }

    /**
     * Generate negative test scenarios
     */
    private List<Map<String, Object>> generateNegativeScenarios() {
        return Arrays.asList(
            Map.of(
                "scenarioId", "NEG-001",
                "name", "Input Validation Tests",
                "description", "Test input validation and error handling",
                "testCases", Arrays.asList(
                    "Test with SQL injection payloads",
                    "Test with XSS payloads",
                    "Test with malformed JSON",
                    "Test with missing required fields",
                    "Test with invalid data types"
                )
            ),
            Map.of(
                "scenarioId", "NEG-002",
                "name", "Error Handling Tests",
                "description", "Test error handling and information disclosure",
                "testCases", Arrays.asList(
                    "Test error message information disclosure",
                    "Test stack trace exposure",
                    "Test system error handling"
                )
            )
        );
    }

    /**
     * Generate performance test scenarios
     */
    private List<Map<String, Object>> generatePerformanceScenarios() {
        return Arrays.asList(
            Map.of(
                "scenarioId", "PERF-001",
                "name", "Load Testing",
                "description", "Test system under various load conditions",
                "testCases", Arrays.asList(
                    "Test with 100 concurrent users",
                    "Test with 1000 concurrent users",
                    "Test with 10000 concurrent users"
                )
            ),
            Map.of(
                "scenarioId", "PERF-002",
                "name", "Stress Testing",
                "description", "Test system behavior under stress",
                "testCases", Arrays.asList(
                    "Test with maximum payload sizes",
                    "Test with long-running requests",
                    "Test memory usage patterns"
                )
            )
        );
    }

    /**
     * Create a standardized test case
     */
    private Map<String, Object> createTestCase(String testId, String name, String description, Map<String, Object> metadata) {
        Map<String, Object> testCase = new HashMap<>();
        testCase.put("testId", testId);
        testCase.put("name", name);
        testCase.put("description", description);
        testCase.put("metadata", metadata);
        
        // Add test steps (placeholder)
        testCase.put("testSteps", generateTestSteps(name));
        
        return testCase;
    }

    /**
     * Generate test steps based on test name
     */
    private List<String> generateTestSteps(String testName) {
        if (testName.toLowerCase().contains("idor")) {
            return Arrays.asList(
                "Identify object IDs in requests",
                "Modify object IDs to access other users' resources",
                "Verify unauthorized access attempts"
            );
        } else if (testName.toLowerCase().contains("auth")) {
            return Arrays.asList(
                "Attempt authentication with invalid credentials",
                "Test session management",
                "Verify proper error messages"
            );
        } else {
            return Arrays.asList(
                "Prepare test data",
                "Execute security test",
                "Verify results",
                "Clean up test data"
            );
        }
    }
}