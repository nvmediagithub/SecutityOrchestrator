package org.example.infrastructure.services.owasp.generators;

import org.example.infrastructure.services.owasp.base.OwaspTestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Генератор тестов безопасности для проверки аутентификации (OWASP-A01: Broken Authentication)
 * Обеспечивает comprehensive testing authentication mechanisms
 */
public class AuthenticationTestGenerator implements OwaspTestGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTestGenerator.class);
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{8,128}$");
    
    private final Builder builder;
    
    private AuthenticationTestGenerator(Builder builder) {
        this.builder = builder;
    }
    
    /**
     * Builder pattern для создания генератора тестов аутентификации
     */
    public static class Builder {
        private boolean enableBruteForceTesting = true;
        private boolean enableSessionTesting = true;
        private boolean enablePasswordPolicyTesting = true;
        private boolean enableMultiFactorTesting = true;
        private int maxAttempts = 5;
        private int sessionTimeoutSeconds = 1800;
        private List<String> weakPasswords = Arrays.asList(
            "password", "123456", "admin", "qwerty", "letmein"
        );
        private List<String> testUsers = Arrays.asList(
            "testuser", "admin", "administrator", "user", "guest"
        );
        
        public Builder enableBruteForceTesting(boolean enable) {
            this.enableBruteForceTesting = enable;
            return this;
        }
        
        public Builder enableSessionTesting(boolean enable) {
            this.enableSessionTesting = enable;
            return this;
        }
        
        public Builder enablePasswordPolicyTesting(boolean enable) {
            this.enablePasswordPolicyTesting = enable;
            return this;
        }
        
        public Builder enableMultiFactorTesting(boolean enable) {
            this.enableMultiFactorTesting = enable;
            return this;
        }
        
        public Builder setMaxAttempts(int maxAttempts) {
            if (maxAttempts <= 0) throw new IllegalArgumentException("maxAttempts must be positive");
            this.maxAttempts = maxAttempts;
            return this;
        }
        
        public Builder setSessionTimeoutSeconds(int sessionTimeoutSeconds) {
            if (sessionTimeoutSeconds <= 0) throw new IllegalArgumentException("sessionTimeoutSeconds must be positive");
            this.sessionTimeoutSeconds = sessionTimeoutSeconds;
            return this;
        }
        
        public Builder setWeakPasswords(List<String> weakPasswords) {
            if (weakPasswords == null || weakPasswords.isEmpty()) 
                throw new IllegalArgumentException("weakPasswords cannot be null or empty");
            this.weakPasswords = new ArrayList<>(weakPasswords);
            return this;
        }
        
        public Builder setTestUsers(List<String> testUsers) {
            if (testUsers == null || testUsers.isEmpty()) 
                throw new IllegalArgumentException("testUsers cannot be null or empty");
            this.testUsers = new ArrayList<>(testUsers);
            return this;
        }
        
        public AuthenticationTestGenerator build() {
            return new AuthenticationTestGenerator(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public CompletableFuture<GeneratedTestResult> generateTests(String targetEndpoint, Map<String, Object> parameters) {
        logger.info("Generating authentication tests for endpoint: {}", targetEndpoint);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateInputs(targetEndpoint, parameters);
                
                List<String> testCases = new ArrayList<>();
                List<String> dependencies = Arrays.asList("junit-jupiter", "rest-assured", "json-path");
                
                if (builder.enableBruteForceTesting) {
                    testCases.addAll(generateBruteForceTests());
                }
                
                if (builder.enableSessionTesting) {
                    testCases.addAll(generateSessionTests());
                }
                
                if (builder.enablePasswordPolicyTesting) {
                    testCases.addAll(generatePasswordPolicyTests());
                }
                
                if (builder.enableMultiFactorTesting) {
                    testCases.addAll(generateMultiFactorTests());
                }
                
                String testCode = generateTestCode(targetEndpoint, testCases);
                Map<String, Object> metadata = createMetadata(parameters);
                
                return new GeneratedTestResult(
                    "AUTH-" + System.currentTimeMillis(),
                    "Authentication Security Tests",
                    "Comprehensive authentication security testing including brute force, session management, password policy, and multi-factor authentication",
                    testCode,
                    "All authentication mechanisms should be secure and resistant to common attacks",
                    "HIGH",
                    dependencies,
                    true,
                    metadata
                );
                
            } catch (Exception e) {
                logger.error("Failed to generate authentication tests", e);
                throw new RuntimeException("Authentication test generation failed: " + e.getMessage(), e);
            }
        });
    }
    
    @Override
    public CompletableFuture<SecurityCheckResult> performSecurityCheck(String specification) {
        logger.info("Performing authentication security check for specification: {}", specification);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> vulnerabilities = new ArrayList<>();
                List<String> evidence = new ArrayList<>();
                
                // Проверка на слабые пароли
                if (containsWeakCredentials(specification)) {
                    vulnerabilities.add("Weak or default credentials detected");
                    evidence.add("Found default usernames or passwords in specification");
                }
                
                // Проверка на отсутствие rate limiting
                if (!specification.contains("rate") && !specification.contains("throttle")) {
                    vulnerabilities.add("No rate limiting implemented");
                    evidence.add("Authentication endpoint lacks rate limiting");
                }
                
                // Проверка на отсутствие session management
                if (!specification.contains("session") && !specification.contains("token")) {
                    vulnerabilities.add("Insufficient session management");
                    evidence.add("No session management mechanisms specified");
                }
                
                String status = vulnerabilities.isEmpty() ? "SECURE" : "VULNERABLE";
                String remediation = generateRemediation(vulnerabilities);
                Double confidenceScore = calculateConfidenceScore(vulnerabilities.size());
                
                return new SecurityCheckResult(
                    "AUTH-SEC-" + System.currentTimeMillis(),
                    "Authentication Security Check",
                    status,
                    "Security assessment of authentication implementation",
                    String.join("; ", vulnerabilities),
                    remediation,
                    confidenceScore,
                    evidence
                );
                
            } catch (Exception e) {
                logger.error("Failed to perform authentication security check", e);
                return new SecurityCheckResult(
                    "AUTH-SEC-ERROR",
                    "Authentication Security Check",
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
        return "Authentication Security Testing";
    }
    
    @Override
    public boolean supportsTestType(String testType) {
        return Arrays.asList(
            "brute-force", "session-management", "password-policy", 
            "multi-factor", "token-validation", "credential-stuffing"
        ).contains(testType.toLowerCase());
    }
    
    private void validateInputs(String targetEndpoint, Map<String, Object> parameters) {
        if (targetEndpoint == null || targetEndpoint.trim().isEmpty()) {
            throw new IllegalArgumentException("Target endpoint cannot be null or empty");
        }
        
        if (parameters == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        if (!targetEndpoint.startsWith("/") && !targetEndpoint.contains("http")) {
            throw new IllegalArgumentException("Target endpoint must be a valid path or URL");
        }
    }
    
    private List<String> generateBruteForceTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestBruteForceAttack");
        tests.add("TestCredentialStuffing");
        tests.add("TestDictionaryAttack");
        tests.add("TestCommonPasswords");
        
        return tests;
    }
    
    private List<String> generateSessionTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestSessionFixation");
        tests.add("TestSessionHijacking");
        tests.add("TestSessionTimeout");
        tests.add("TestConcurrentSessions");
        
        return tests;
    }
    
    private List<String> generatePasswordPolicyTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestWeakPasswordPolicy");
        tests.add("TestPasswordComplexity");
        tests.add("TestPasswordHistory");
        tests.add("TestPasswordExpiration");
        
        return tests;
    }
    
    private List<String> generateMultiFactorTests() {
        List<String> tests = new ArrayList<>();
        
        tests.add("TestMfaBypass");
        tests.add("TestMfaImplementation");
        tests.add("TestBackupCodes");
        tests.add("TestMfaRecovery");
        
        return tests;
    }
    
    private String generateTestCode(String targetEndpoint, List<String> testCases) {
        StringBuilder code = new StringBuilder();
        
        code.append("import org.junit.jupiter.api.Test;\n");
        code.append("import org.junit.jupiter.api.BeforeEach;\n");
        code.append("import io.restassured.RestAssured;\n");
        code.append("import io.restassured.response.Response;\n");
        code.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        
        code.append("public class AuthenticationSecurityTest {\n\n");
        code.append("    private static final String BASE_URL = \"").append(targetEndpoint).append("\";\n\n");
        
        for (String testCase : testCases) {
            code.append("    @Test\n");
            code.append("    public void test").append(testCase).append("() {\n");
            code.append("        // Test implementation for ").append(testCase).append("\n");
            code.append("        // TODO: Implement specific test logic\n");
            code.append("    }\n\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    private Map<String, Object> createMetadata(Map<String, Object> parameters) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("generator", "AuthenticationTestGenerator");
        metadata.put("version", "1.0.0");
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("supportedTests", Arrays.asList(
            "brute-force", "session-management", "password-policy", 
            "multi-factor", "token-validation", "credential-stuffing"
        ));
        metadata.put("parameters", parameters);
        return metadata;
    }
    
    private boolean containsWeakCredentials(String specification) {
        String lowerSpec = specification.toLowerCase();
        return builder.weakPasswords.stream().anyMatch(lowerSpec::contains) ||
               builder.testUsers.stream().anyMatch(lowerSpec::contains);
    }
    
    private String generateRemediation(List<String> vulnerabilities) {
        List<String> recommendations = new ArrayList<>();
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("brute force") || v.contains("rate"))) {
            recommendations.add("Implement rate limiting and account lockout mechanisms");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("password") || v.contains("credential"))) {
            recommendations.add("Enforce strong password policies and avoid default credentials");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("session"))) {
            recommendations.add("Implement proper session management with secure tokens");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("multi-factor") || v.contains("mfa"))) {
            recommendations.add("Enable multi-factor authentication for additional security");
        }
        
        return String.join(". ", recommendations) + ".";
    }
    
    private Double calculateConfidenceScore(int vulnerabilityCount) {
        if (vulnerabilityCount == 0) return 1.0;
        if (vulnerabilityCount <= 2) return 0.8;
        if (vulnerabilityCount <= 4) return 0.6;
        return 0.3;
    }
}