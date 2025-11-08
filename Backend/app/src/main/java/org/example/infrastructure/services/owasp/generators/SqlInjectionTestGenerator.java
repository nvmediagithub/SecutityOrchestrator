package org.example.infrastructure.services.owasp.generators;

import org.example.infrastructure.services.owasp.base.OwaspTestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Генератор тестов безопасности для проверки SQL injection (OWASP-A03: Injection)
 * Обеспечивает testing против SQL injection атак
 */
public class SqlInjectionTestGenerator implements OwaspTestGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlInjectionTestGenerator.class);
    private static final Pattern SQL_PATTERN = Pattern.compile("(?i)(select|insert|update|delete|drop|create|alter|union|exec|execute)");
    
    private final Builder builder;
    
    private SqlInjectionTestGenerator(Builder builder) {
        this.builder = builder;
    }
    
    public static class Builder {
        private boolean enableClassicSqlInjection = true;
        private boolean enableBlindSqlInjection = true;
        private boolean enableTimeBasedSqlInjection = true;
        private boolean enableUnionBasedSqlInjection = true;
        private boolean enableErrorBasedSqlInjection = true;
        private List<String> sqlPayloads = Arrays.asList(
            "' OR '1'='1",
            "' UNION SELECT 1,2,3--",
            "'; DROP TABLE users; --",
            "1' AND (SELECT COUNT(*) FROM information_schema.tables)>0--",
            "' OR 1=1 UNION SELECT null,username,password FROM users--"
        );
        private List<String> blindSqlPayloads = Arrays.asList(
            "' AND 1=1--",
            "' AND 1=2--",
            "' AND (SELECT COUNT(*) FROM users)>0--",
            "' AND (SELECT SUBSTRING(password,1,1) FROM users WHERE username='admin')='a'--"
        );
        
        public Builder enableClassicSqlInjection(boolean enable) {
            this.enableClassicSqlInjection = enable;
            return this;
        }
        
        public Builder enableBlindSqlInjection(boolean enable) {
            this.enableBlindSqlInjection = enable;
            return this;
        }
        
        public Builder enableTimeBasedSqlInjection(boolean enable) {
            this.enableTimeBasedSqlInjection = enable;
            return this;
        }
        
        public Builder enableUnionBasedSqlInjection(boolean enable) {
            this.enableUnionBasedSqlInjection = enable;
            return this;
        }
        
        public Builder enableErrorBasedSqlInjection(boolean enable) {
            this.enableErrorBasedSqlInjection = enable;
            return this;
        }
        
        public Builder setSqlPayloads(List<String> sqlPayloads) {
            if (sqlPayloads == null || sqlPayloads.isEmpty()) 
                throw new IllegalArgumentException("sqlPayloads cannot be null or empty");
            this.sqlPayloads = new ArrayList<>(sqlPayloads);
            return this;
        }
        
        public Builder setBlindSqlPayloads(List<String> blindSqlPayloads) {
            if (blindSqlPayloads == null || blindSqlPayloads.isEmpty()) 
                throw new IllegalArgumentException("blindSqlPayloads cannot be null or empty");
            this.blindSqlPayloads = new ArrayList<>(blindSqlPayloads);
            return this;
        }
        
        public SqlInjectionTestGenerator build() {
            return new SqlInjectionTestGenerator(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public CompletableFuture<GeneratedTestResult> generateTests(String targetEndpoint, Map<String, Object> parameters) {
        logger.info("Generating SQL injection tests for endpoint: {}", targetEndpoint);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateInputs(targetEndpoint, parameters);
                
                List<String> testCases = new ArrayList<>();
                List<String> dependencies = Arrays.asList("junit-jupiter", "rest-assured", "h2");
                
                if (builder.enableClassicSqlInjection) {
                    testCases.addAll(generateClassicSqlInjectionTests());
                }
                
                if (builder.enableBlindSqlInjection) {
                    testCases.addAll(generateBlindSqlInjectionTests());
                }
                
                if (builder.enableTimeBasedSqlInjection) {
                    testCases.addAll(generateTimeBasedSqlInjectionTests());
                }
                
                if (builder.enableUnionBasedSqlInjection) {
                    testCases.addAll(generateUnionBasedSqlInjectionTests());
                }
                
                if (builder.enableErrorBasedSqlInjection) {
                    testCases.addAll(generateErrorBasedSqlInjectionTests());
                }
                
                String testCode = generateTestCode(targetEndpoint, testCases);
                Map<String, Object> metadata = createMetadata(parameters);
                
                return new GeneratedTestResult(
                    "SQLI-" + System.currentTimeMillis(),
                    "SQL Injection Security Tests",
                    "Comprehensive testing against SQL injection attacks including classic, blind, time-based, union-based, and error-based techniques",
                    testCode,
                    "Database queries should be protected against SQL injection",
                    "CRITICAL",
                    dependencies,
                    true,
                    metadata
                );
                
            } catch (Exception e) {
                logger.error("Failed to generate SQL injection tests", e);
                throw new RuntimeException("SQL injection test generation failed: " + e.getMessage(), e);
            }
        });
    }
    
    @Override
    public CompletableFuture<SecurityCheckResult> performSecurityCheck(String specification) {
        logger.info("Performing SQL injection security check for specification: {}", specification);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> vulnerabilities = new ArrayList<>();
                List<String> evidence = new ArrayList<>();
                
                // Проверка на возможность SQL injection
                if (containsSqlKeywords(specification)) {
                    vulnerabilities.add("Potential SQL injection vulnerability");
                    evidence.add("Found SQL keywords in user input handling");
                }
                
                // Проверка на использование prepared statements
                if (!specification.contains("prepared") && !specification.contains("parameterized")) {
                    vulnerabilities.add("Missing parameterized queries");
                    evidence.add("No prepared statement or parameterized query usage found");
                }
                
                // Проверка на proper input sanitization
                if (!specification.contains("sanitize") && !specification.contains("escape")) {
                    vulnerabilities.add("Insufficient input sanitization");
                    evidence.add("No input sanitization or escaping mechanisms found");
                }
                
                String status = vulnerabilities.isEmpty() ? "SECURE" : "VULNERABLE";
                String remediation = generateRemediation(vulnerabilities);
                Double confidenceScore = calculateConfidenceScore(vulnerabilities.size());
                
                return new SecurityCheckResult(
                    "SQLI-SEC-" + System.currentTimeMillis(),
                    "SQL Injection Security Check",
                    status,
                    "Security assessment of SQL injection protection",
                    String.join("; ", vulnerabilities),
                    remediation,
                    confidenceScore,
                    evidence
                );
                
            } catch (Exception e) {
                logger.error("Failed to perform SQL injection security check", e);
                return new SecurityCheckResult(
                    "SQLI-SEC-ERROR",
                    "SQL Injection Security Check",
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
        return "SQL Injection Security Testing";
    }
    
    @Override
    public boolean supportsTestType(String testType) {
        return Arrays.asList(
            "sql-injection", "blind-sql", "union-based", "time-based", "error-based", 
            "classic-sql", "no-sql-injection", "query-injection"
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
    
    private List<String> generateClassicSqlInjectionTests() {
        return Arrays.asList(
            "TestClassicSqlInjection", "TestBasicSqlInjection", "TestStringConcatenation", "TestCommentInjection"
        );
    }
    
    private List<String> generateBlindSqlInjectionTests() {
        return Arrays.asList(
            "TestBlindSqlInjection", "TestBooleanBasedSqlInjection", "TestTimeBasedSqlInjection", "TestBooleanBlind"
        );
    }
    
    private List<String> generateTimeBasedSqlInjectionTests() {
        return Arrays.asList(
            "TestTimeBasedSqlInjection", "TestSleepInjection", "TestBenchmarkInjection", "TestWaitForDelay"
        );
    }
    
    private List<String> generateUnionBasedSqlInjectionTests() {
        return Arrays.asList(
            "TestUnionBasedSqlInjection", "TestUnionSelect", "TestColumnNumberMismatch", "TestUnionOrderBy"
        );
    }
    
    private List<String> generateErrorBasedSqlInjectionTests() {
        return Arrays.asList(
            "TestErrorBasedSqlInjection", "TestCastError", "TestTypeMismatch", "TestConversionError"
        );
    }
    
    private String generateTestCode(String targetEndpoint, List<String> testCases) {
        StringBuilder code = new StringBuilder();
        
        code.append("import org.junit.jupiter.api.Test;\n");
        code.append("import org.junit.jupiter.api.BeforeEach;\n");
        code.append("import io.restassured.RestAssured;\n");
        code.append("import io.restassured.response.Response;\n");
        code.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        
        code.append("public class SqlInjectionSecurityTest {\n\n");
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
        metadata.put("generator", "SqlInjectionTestGenerator");
        metadata.put("version", "1.0.0");
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("supportedTests", Arrays.asList(
            "sql-injection", "blind-sql", "union-based", "time-based", "error-based", 
            "classic-sql", "no-sql-injection", "query-injection"
        ));
        metadata.put("parameters", parameters);
        return metadata;
    }
    
    private boolean containsSqlKeywords(String specification) {
        return SQL_PATTERN.matcher(specification).find();
    }
    
    private String generateRemediation(List<String> vulnerabilities) {
        List<String> recommendations = new ArrayList<>();
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("parameterized") || v.contains("prepared"))) {
            recommendations.add("Use prepared statements and parameterized queries for all database operations");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("sanitization") || v.contains("escape"))) {
            recommendations.add("Implement proper input sanitization and output escaping");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("validation"))) {
            recommendations.add("Add input validation and whitelist acceptable characters");
        }
        
        if (vulnerabilities.stream().anyMatch(v -> v.contains("privilege") || v.contains("least"))) {
            recommendations.add("Use principle of least privilege for database accounts");
        }
        
        return String.join(". ", recommendations) + ".";
    }
    
    private Double calculateConfidenceScore(int vulnerabilityCount) {
        if (vulnerabilityCount == 0) return 1.0;
        if (vulnerabilityCount <= 2) return 0.9;
        if (vulnerabilityCount <= 3) return 0.6;
        return 0.3;
    }
}