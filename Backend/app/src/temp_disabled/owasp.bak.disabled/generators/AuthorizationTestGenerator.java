package org.example.infrastructure.services.owasp.generators;

import org.example.infrastructure.services.owasp.base.OwaspTestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Генератор тестов безопасности для проверки авторизации (OWASP-A02: Broken Access Control)
 * Обеспечивает comprehensive testing authorization mechanisms
 */
public class AuthorizationTestGenerator implements OwaspTestGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationTestGenerator.class);
    private static final Pattern ROLE_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{2,30}$");
    private static final Pattern PERMISSION_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]{2,50}$");
    
    private final Builder builder;
    
    private AuthorizationTestGenerator(Builder builder) {
        this.builder = builder;
    }
    
    /**
     * Builder pattern для создания генератора тестов авторизации
     */
    public static class Builder {
        private boolean enableRoleTesting = true;
        private boolean enablePermissionTesting = true;
        private boolean enableDataLevelTesting = true;
        private boolean enableHorizontalPrivilegeEscalation = true;
        private boolean enableVerticalPrivilegeEscalation = true;
        private List<String> testRoles = Arrays.asList(
            "admin", "user", "guest", "moderator", "analyst"
        );
        private List<String> testPermissions = Arrays.asList(
            "read", "write", "delete", "admin", "execute", "upload"
        );
        private List<String> testDataTypes = Arrays.asList(
            "user_data", "admin_data", "financial_data", "personal_data"
        );
        
        public Builder enableRoleTesting(boolean enable) {
            this.enableRoleTesting = enable;
            return this;
        }
        
        public Builder enablePermissionTesting(boolean enable) {
            this.enablePermissionTesting = enable;
            return this;
        }
        
        public Builder enableDataLevelTesting(boolean enable) {
            this.enableDataLevelTesting = enable;
            return this;
        }
        
        public Builder enableHorizontalPrivilegeEscalation(boolean enable) {
            this.enableHorizontalPrivilegeEscalation = enable;
            return this;
        }
        
        public Builder enableVerticalPrivilegeEscalation(boolean enable) {
            this.enableVerticalPrivilegeEscalation = enable;
            return this;
        }
        
        public Builder setTestRoles(List<String> testRoles) {
            if (testRoles == null || testRoles.isEmpty()) 
                throw new IllegalArgumentException("testRoles cannot be null or empty");
            this.testRoles = new ArrayList<>(testRoles);
            return this;
        }
        
        public Builder setTestPermissions(List<String> testPermissions) {
            if (testPermissions == null || testPermissions.isEmpty()) 
                throw new IllegalArgumentException("testPermissions cannot be null or empty");
            this.testPermissions = new ArrayList<>(testPermissions);
            return this;
        }
        
        public Builder setTestDataTypes(List<String> testDataTypes) {
            if (testDataTypes == null || testDataTypes.isEmpty()) 
                throw new IllegalArgumentException("testDataTypes cannot be null or empty");
            this.testDataTypes = new ArrayList<>(testDataTypes);
            return this;
        }
        
        public AuthorizationTestGenerator build() {
            return new AuthorizationTestGenerator(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public CompletableFuture<GeneratedTestResult> generateTests(String targetEndpoint, Map<String, Object> parameters) {
        logger.info("Generating authorization tests for endpoint: {}", targetEndpoint);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateInputs(targetEndpoint, parameters);
                
                List<String> testCases = new ArrayList<>();
                List<String> dependencies = Arrays.asList("junit-jupiter", "rest-assured", "json-path", "mockito");
                
                if (builder.enableRoleTesting) {
                    testCases.addAll(generateRoleBasedTests());
                }
                
                if (builder.enablePermissionTesting) {
                    testCases.addAll(generatePermissionTests());
                }
                
                if (builder.enableDataLevelTesting) {
                    testCases.addAll(generateDataLevelTests());
                }
                
                if (builder.enableHorizontalPrivilegeEscalation) {
                    testCases.addAll(generateHorizontalPrivilegeTests());
                }
                
                if (builder.enableVerticalPrivilegeEscalation) {
                    testCases.addAll(generateVerticalPrivilegeTests());
                }
                
                String testCode = generateTestCode(targetEndpoint, testCases);
                Map<String, Object> metadata = createMetadata(parameters);
                
                return new GeneratedTestResult(
                    "AUTHZ-" + System.currentTimeMillis(),
                    "Authorization Security Tests",
                    "Comprehensive authorization security testing including role-based access control, permission validation, data-level security, and privilege escalation attacks",
                    testCode,
                    "All authorization mechanisms should enforce proper access controls",
                    "CRITICAL",
                    dependencies,
                    true,
                    metadata
                );
                
            } catch (Exception e) {
                logger.error("Failed to generate authorization tests", e);
                throw new RuntimeException("Authorization test generation failed: " + e.getMessage(), e);
            }
        });
    }
    
    @Override
    public CompletableFuture<SecurityCheckResult> performSecurityCheck(String specification) {
        logger.info("Performing authorization security check for specification: {}", specification);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> vulnerabilities = new ArrayList<>();
                List<String> evidence = new ArrayList<>();
                
                // Проверка на недостаточную авторизацию
                if (!specification.contains("role") && !specification.contains("permission") && !specification.contains("authorize")) {
                    vulnerabilities.add("Insufficient authorization mechanisms");
                    evidence.add("No role-based or permission-based authorization found");
                }
                
                // Проверка на слабую проверку доступа к данным
                if (!specification.contains("user_id") && !specification.contains("data_access")) {
                    vulnerabilities.add("Missing data-level access control");
                    evidence.add("No user-specific data access validation");
                }
                
                // Проверка на отсутствие privilege escalation protection
                if (!specification.contains("escalation") && !specification.contains("privilege")) {
                    vulnerabilities.add("No privilege escalation protection");
                    evidence.add("Missing mechanisms to prevent privilege escalation");
                }
                
                String status = vulnerabilities.isEmpty() ? "SECURE" : "VULNERABLE";
                String remediation = generateRemediation(vulnerabilities);
                Double confidenceScore = calculateConfidenceScore(vulnerabilities.size());
                
                return new SecurityCheckResult(
                    "AUTHZ-SEC-" + System.currentTimeMillis(),
                    "Authorization Security Check",
                    status,
                    "Security assessment of authorization implementation",
                    String.join("; ", vulnerabilities),
                    remediation,
                    confidenceScore,
                    evidence
                );
                
            } catch (Exception e) {
                logger.error("Failed to perform authorization security check", e);
                return new SecurityCheckResult(
                    "AUTHZ-SEC-ERROR",
                    "Authorization Security Check",
                    "ERROR",
                    "Failed to perform security check",
                    "System error: " + e.getMessage(),
                    "Review system configuration and try again",
                    0.0,
                    Arrays.asList("Error: " + e.getMessage())
                );
            }
        });
    }
    
    @Override
    public String getCategoryName() {
        return "Authorization Security Testing";
    }
    
    @Override
    public boolean supportsTestType(String testType) {
        return Arrays.asList(
            "role-based", "permission-based", "data-level", "horizontal-privilege", 
            "vertical-privilege", "id-or", "insecure-direct-object", "bypass"
        ).contains(testType.toLowerCase());
    }
    
    private void validateInputs(String targetEndpoint, Map<String, Object> parameters) {
        if (targetEndpoint == null || targetEndpoint.trim().isEmpty()) {
            throw new IllegalArgumentException("Target endpoint cannot be null or empty");
        }
        
        if (parameters == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
    }
    
    private List<String> generateRoleBasedTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestRoleBasedAccessControl");
        tests.add("TestRoleAssignment");
        tests.add("TestRoleValidation");
        tests.add("TestRoleEscalation");
        
        return tests;
    }
    
    private List<String> generatePermissionTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestPermissionValidation");
        tests.add("TestPermissionBypass");
        tests.add("TestInsufficientPermissions");
        tests.add("TestExcessivePermissions");
        
        return tests;
    }
    
    private List<String> generateDataLevelTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestDataAccessByUserId");
        tests.add("TestDirectObjectReference");
        tests.add("TestDataLeakage");
        tests.add("TestCrossUserDataAccess");
        
        return tests;
    }
    
    private List<String> generateHorizontalPrivilegeTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestHorizontalPrivilegeEscalation");
        tests.add("TestUserIdManipulation");
        tests.add("TestSessionHijacking");
        tests.add("TestAccountTakeover");
        
        return tests;
    }
    
    private List<String> generateVerticalPrivilegeTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestVerticalPrivilegeEscalation");
        tests.add("TestAdminBypass");
        tests.add("TestSuperUserExploitation");
        tests.add("TestUnauthorizedAdminAccess");
        
        return tests;
    }
    
    private String generateTestCode(String targetEndpoint, List<String> testCases) {
        StringBuilder code = new StringBuilder();
        
        code.append("import org.junit.jupiter.api.Test;\n");
        code.append("import org.junit.jupiter.api.BeforeEach;\n");
        code.append("import io.restassured.RestAssured;\n");
        code.append("import io.restassured.response.Response;\n");
        code.append("import org.mockito.Mockito;\n");
        code.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        
        code.append("public class AuthorizationSecurityTest {\n\n");
        code.append("    private static final String BASE_URL = \"").append(targetEndpoint).append("\";\n\n");
        
        for (String testCase : testCases) {
            code.append("    @Test\n");
            code.append("    public void test").append(testCase).append("() {\n");
            code.append("        // Test implementation for ").append(testCase).append("\n");
            code.append("        // TODO: Implement specific authorization test logic\n");
            code.append("    }\n\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    private Map<String, Object> createMetadata(Map<String, Object> parameters) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("generator", "AuthorizationTestGenerator");
        metadata.put("version", "1.0.0");
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("supportedTests", Arrays.asList(
            "role-based", "permission-based", "data-level", "horizontal-privilege", 
            "vertical-privilege", "id-or", "insecure-direct-object", "bypass"
        ));
        metadata.put("parameters", parameters);
        return metadata;
    }
    
    private String generateRemediation(List<String> vulnerabilities) {
        List<String> recommendations = new ArrayList<>();
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("authorization") || v.contains("access"))) {
            recommendations.add("Implement proper authorization checks on every endpoint");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("data") || v.contains("user_id"))) {
            recommendations.add("Add data-level access controls to prevent unauthorized data access");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("privilege") || v.contains("escalation"))) {
            recommendations.add("Implement mechanisms to prevent privilege escalation attacks");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("role") || v.contains("permission"))) {
            recommendations.add("Enforce strict role and permission validation");
        }
        
        return String.join(". ", recommendations) + ".";
    }
    
    private Double calculateConfidenceScore(int vulnerabilityCount) {
        if (vulnerabilityCount == 0) return 1.0;
        if (vulnerabilityCount <= 2) return 0.9;
        if (vulnerabilityCount <= 3) return 0.7;
        return 0.4;
    }
}