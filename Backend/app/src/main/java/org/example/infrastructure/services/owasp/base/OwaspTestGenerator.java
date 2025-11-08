package org.example.infrastructure.services.owasp.base;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Базовый интерфейс для генераторов OWASP тестов безопасности
 */
public interface OwaspTestGenerator {
    
    /**
     * Генерирует тесты безопасности для указанной категории
     * @param targetEndpoint целевой endpoint
     * @param parameters параметры тестирования
     * @return результат генерации тестов
     */
    CompletableFuture<GeneratedTestResult> generateTests(String targetEndpoint, Map<String, Object> parameters);
    
    /**
     * Выполняет специфичные для категории проверки безопасности
     * @param specification спецификация для проверки
     * @return результат проверки
     */
    CompletableFuture<SecurityCheckResult> performSecurityCheck(String specification);
    
    /**
     * Получает название категории тестирования
     * @return название категории
     */
    String getCategoryName();
    
    /**
     * Проверяет, поддерживает ли генератор указанный тип тестирования
     * @param testType тип тестирования
     * @return true если поддерживается
     */
    boolean supportsTestType(String testType);
    
    /**
     * Результат генерации теста
     */
    class GeneratedTestResult {
        private final String testId;
        private final String testName;
        private final String testDescription;
        private final String testCode;
        private final String expectedResult;
        private final String severity;
        private final List<String> dependencies;
        private final boolean isAutomated;
        private final Map<String, Object> metadata;
        
        public GeneratedTestResult(String testId, String testName, String testDescription, 
                                 String testCode, String expectedResult, String severity,
                                 List<String> dependencies, boolean isAutomated, Map<String, Object> metadata) {
            this.testId = testId;
            this.testName = testName;
            this.testDescription = testDescription;
            this.testCode = testCode;
            this.expectedResult = expectedResult;
            this.severity = severity;
            this.dependencies = dependencies;
            this.isAutomated = isAutomated;
            this.metadata = metadata;
        }
        
        // Getters
        public String getTestId() { return testId; }
        public String getTestName() { return testName; }
        public String getTestDescription() { return testDescription; }
        public String getTestCode() { return testCode; }
        public String getExpectedResult() { return expectedResult; }
        public String getSeverity() { return severity; }
        public List<String> getDependencies() { return dependencies; }
        public boolean isAutomated() { return isAutomated; }
        public Map<String, Object> getMetadata() { return metadata; }
    }
    
    /**
     * Результат проверки безопасности
     */
    class SecurityCheckResult {
        private final String checkId;
        private final String checkName;
        private final String status;
        private final String description;
        private final String vulnerabilityDetails;
        private final String remediation;
        private final Double confidenceScore;
        private final List<String> evidence;
        
        public SecurityCheckResult(String checkId, String checkName, String status, 
                                 String description, String vulnerabilityDetails, String remediation,
                                 Double confidenceScore, List<String> evidence) {
            this.checkId = checkId;
            this.checkName = checkName;
            this.status = status;
            this.description = description;
            this.vulnerabilityDetails = vulnerabilityDetails;
            this.remediation = remediation;
            this.confidenceScore = confidenceScore;
            this.evidence = evidence;
        }
        
        // Getters
        public String getCheckId() { return checkId; }
        public String getCheckName() { return checkName; }
        public String getStatus() { return status; }
        public String getDescription() { return description; }
        public String getVulnerabilityDetails() { return vulnerabilityDetails; }
        public String getRemediation() { return remediation; }
        public Double getConfidenceScore() { return confidenceScore; }
        public List<String> getEvidence() { return evidence; }
        
        public boolean isVulnerable() {
            return "VULNERABLE".equals(status);
        }
        
        public boolean isSecure() {
            return "SECURE".equals(status);
        }
    }
}