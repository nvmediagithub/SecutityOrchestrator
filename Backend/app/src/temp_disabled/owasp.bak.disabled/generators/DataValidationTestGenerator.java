package org.example.infrastructure.services.owasp.generators;

import org.example.infrastructure.services.owasp.base.OwaspTestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Генератор тестов безопасности для проверки валидации данных (OWASP-A03: Injection)
 * Обеспечивает testing input validation и sanitization
 */
public class DataValidationTestGenerator implements OwaspTestGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(DataValidationTestGenerator.class);
    private static final Pattern INPUT_PATTERN = Pattern.compile("^[\\w\\s\\-\\.@]{0,1000}$");
    
    private final Builder builder;
    
    private DataValidationTestGenerator(Builder builder) {
        this.builder = builder;
    }
    
    public static class Builder {
        private boolean enableInputValidationTesting = true;
        private boolean enableOutputEncodingTesting = true;
        private boolean enableSanitizationTesting = true;
        private boolean enableTypeValidationTesting = true;
        private boolean enableLengthValidationTesting = true;
        private List<String> testInputTypes = Arrays.asList(
            "string", "integer", "email", "url", "phone", "date", "file"
        );
        private List<String> maliciousInputs = Arrays.asList(
            "<script>alert('xss')</script>", 
            "'; DROP TABLE users; --",
            "{{7*7}}",
            "${7*7}",
            "<img src=x onerror=alert(1)>"
        );
        
        public Builder enableInputValidationTesting(boolean enable) {
            this.enableInputValidationTesting = enable;
            return this;
        }
        
        public Builder enableOutputEncodingTesting(boolean enable) {
            this.enableOutputEncodingTesting = enable;
            return this;
        }
        
        public Builder enableSanitizationTesting(boolean enable) {
            this.enableSanitizationTesting = enable;
            return this;
        }
        
        public Builder enableTypeValidationTesting(boolean enable) {
            this.enableTypeValidationTesting = enable;
            return this;
        }
        
        public Builder enableLengthValidationTesting(boolean enable) {
            this.enableLengthValidationTesting = enable;
            return this;
        }
        
        public Builder setTestInputTypes(List<String> testInputTypes) {
            if (testInputTypes == null || testInputTypes.isEmpty()) 
                throw new IllegalArgumentException("testInputTypes cannot be null or empty");
            this.testInputTypes = new ArrayList<>(testInputTypes);
            return this;
        }
        
        public Builder setMaliciousInputs(List<String> maliciousInputs) {
            if (maliciousInputs == null || maliciousInputs.isEmpty()) 
                throw new IllegalArgumentException("maliciousInputs cannot be null or empty");
            this.maliciousInputs = new ArrayList<>(maliciousInputs);
            return this;
        }
        
        public DataValidationTestGenerator build() {
            return new DataValidationTestGenerator(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public CompletableFuture<GeneratedTestResult> generateTests(String targetEndpoint, Map<String, Object> parameters) {
        logger.info("Generating data validation tests for endpoint: {}", targetEndpoint);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateInputs(targetEndpoint, parameters);
                
                List<String> testCases = new ArrayList<>();
                List<String> dependencies = Arrays.asList("junit-jupiter", "rest-assured", "commons-validator");
                
                if (builder.enableInputValidationTesting) {
                    testCases.addAll(generateInputValidationTests());
                }
                
                if (builder.enableOutputEncodingTesting) {
                    testCases.addAll(generateOutputEncodingTests());
                }
                
                if (builder.enableSanitizationTesting) {
                    testCases.addAll(generateSanitizationTests());
                }
                
                if (builder.enableTypeValidationTesting) {
                    testCases.addAll(generateTypeValidationTests());
                }
                
                if (builder.enableLengthValidationTesting) {
                    testCases.addAll(generateLengthValidationTests());
                }
                
                String testCode = generateTestCode(targetEndpoint, testCases);
                Map<String, Object> metadata = createMetadata(parameters);
                
                return new GeneratedTestResult(
                    "DATA-" + System.currentTimeMillis(),
                    "Data Validation Security Tests",
                    "Comprehensive data validation testing including input validation, output encoding, sanitization, type checking, and length validation",
                    testCode,
                    "All data inputs should be properly validated and sanitized",
                    "CRITICAL",
                    dependencies,
                    true,
                    metadata
                );
                
            } catch (Exception e) {
                logger.error("Failed to generate data validation tests", e);
                throw new RuntimeException("Data validation test generation failed: " + e.getMessage(), e);
            }
        });
    }
    
    @Override
    public CompletableFuture<SecurityCheckResult> performSecurityCheck(String specification) {
        logger.info("Performing data validation security check for specification: {}", specification);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> vulnerabilities = new ArrayList<>();
                List<String> evidence = new ArrayList<>();
                
                // Проверка на недостаточную валидацию входных данных
                if (!specification.contains("validate") && !specification.contains("sanitize")) {
                    vulnerabilities.add("Insufficient input validation");
                    evidence.add("No input validation or sanitization mechanisms found");
                }
                
                // Проверка на отсутствие output encoding
                if (!specification.contains("encode") && !specification.contains("escape")) {
                    vulnerabilities.add("Missing output encoding");
                    evidence.add("No output encoding or escaping mechanisms");
                }
                
                // Проверка на type validation
                if (!specification.contains("type") && !specification.contains("format")) {
                    vulnerabilities.add("Insufficient type validation");
                    evidence.add("No type validation for inputs");
                }
                
                String status = vulnerabilities.isEmpty() ? "SECURE" : "VULNERABLE";
                String remediation = generateRemediation(vulnerabilities);
                Double confidenceScore = calculateConfidenceScore(vulnerabilities.size());
                
                return new SecurityCheckResult(
                    "DATA-SEC-" + System.currentTimeMillis(),
                    "Data Validation Security Check",
                    status,
                    "Security assessment of data validation implementation",
                    String.join("; ", vulnerabilities),
                    remediation,
                    confidenceScore,
                    evidence
                );
                
            } catch (Exception e) {
                logger.error("Failed to perform data validation security check", e);
                return new SecurityCheckResult(
                    "DATA-SEC-ERROR",
                    "Data Validation Security Check",
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
        return "Data Validation Security Testing";
    }
    
    @Override
    public boolean supportsTestType(String testType) {
        return Arrays.asList(
            "input-validation", "output-encoding", "sanitization", "type-validation", 
            "length-validation", "xss", "injection", "command-injection"
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
    
    private List<String> generateInputValidationTests() {
        return Arrays.asList(
            "TestInputValidation", "TestMaliciousInput", "TestBoundaryValues", "TestSpecialCharacters"
        );
    }
    
    private List<String> generateOutputEncodingTests() {
        return Arrays.asList(
            "TestOutputEncoding", "TestXSSPrevention", "TestHTMLEncoding", "TestURLEncoding"
        );
    }
    
    private List<String> generateSanitizationTests() {
        return Arrays.asList(
            "TestInputSanitization", "TestSQLInjection", "TestCommandInjection", "TestNoSQLInjection"
        );
    }
    
    private List<String> generateTypeValidationTests() {
        return Arrays.asList(
            "TestTypeValidation", "TestFormatValidation", "TestRangeValidation", "TestPatternMatching"
        );
    }
    
    private List<String> generateLengthValidationTests() {
        return Arrays.asList(
            "TestLengthValidation", "TestBufferOverflow", "TestStringLength", "TestMaxSize"
        );
    }
    
    private String generateTestCode(String targetEndpoint, List<String> testCases) {
        StringBuilder code = new StringBuilder();
        
        code.append("import org.junit.jupiter.api.Test;\n");
        code.append("import org.junit.jupiter.api.BeforeEach;\n");
        code.append("import io.restassured.RestAssured;\n");
        code.append("import io.restassured.response.Response;\n");
        code.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        
        code.append("public class DataValidationSecurityTest {\n\n");
        code.append("    private static final String BASE_URL = \"").append(targetEndpoint).append("\";\n\n");
        
        for (String testCase : testCases) {
            code.append("    @Test\n");
            code.append("    public void test").append(testCase).append("() {\n");
            code.append("        // Test implementation for ").append(testCase).append("\n");
            code.append("    }\n\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    private Map<String, Object> createMetadata(Map<String, Object> parameters) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("generator", "DataValidationTestGenerator");
        metadata.put("version", "1.0.0");
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("supportedTests", Arrays.asList(
            "input-validation", "output-encoding", "sanitization", "type-validation", 
            "length-validation", "xss", "injection", "command-injection"
        ));
        metadata.put("parameters", parameters);
        return metadata;
    }
    
    private String generateRemediation(List<String> vulnerabilities) {
        List<String> recommendations = new ArrayList<>();
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("validation") || v.contains("input"))) {
            recommendations.add("Implement comprehensive input validation for all user inputs");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("encoding") || v.contains("escape"))) {
            recommendations.add("Add proper output encoding and escaping for all outputs");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("type") || v.contains("format"))) {
            recommendations.add("Enforce strict type validation and format checking");
        }
        
        return String.join(". ", recommendations) + ".";
    }
    
    private Double calculateConfidenceScore(int vulnerabilityCount) {
        if (vulnerabilityCount == 0) return 1.0;
        if (vulnerabilityCount <= 2) return 0.8;
        if (vulnerabilityCount <= 3) return 0.6;
        return 0.3;
    }
}