package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Результат выполнения - детальные результаты каждого шага тестирования
 * Содержит метрики, логи, артефакты и анализ для LLM обработки
 */
@Entity
@Table(name = "execution_results")
public class ExecutionResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String resultId;
    
    @Column(nullable = false)
    private String sessionId;
    
    @Column(nullable = false)
    private String stepId;
    
    @Column(length = 1000, nullable = false)
    private String resultName;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status = ResultStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity = SeverityLevel.INFO;
    
    @Column(length = 2000)
    private String resultType; // TEST_OUTPUT, METRICS, LOGS, ARTIFACTS, ANALYSIS
    
    private LocalDateTime generatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Column(length = 4000)
    private String rawOutput;
    
    @Column(length = 4000)
    private String processedOutput;
    
    @Column(length = 4000)
    private String expectedOutput;
    
    @Column(length = 4000)
    private String validationResult;
    
    @Column(length = 2000)
    private String outputFormat; // JSON, XML, TEXT, HTML, PDF
    
    @Column(length = 2000)
    private String contentType;
    
    @Column(length = 4000)
    private String errorMessage;
    
    @Column(length = 2000)
    private String errorCode;
    
    @Column(length = 2000)
    private String errorCategory; // VALIDATION, EXECUTION, TIMEOUT, SYSTEM
    
    @ElementCollection
    private List<String> validationChecks = new ArrayList<>();
    
    @ElementCollection
    private List<String> failedValidations = new ArrayList<>();
    
    @ElementCollection
    private Map<String, String> metrics = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> performanceMetrics = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> securityMetrics = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> qualityMetrics = new HashMap<>();
    
    @ElementCollection
    private List<String> generatedFiles = new ArrayList<>();
    
    @ElementCollection
    private List<String> capturedScreenshots = new ArrayList<>();
    
    @ElementCollection
    private List<String> logEntries = new ArrayList<>();
    
    @ElementCollection
    private List<String> networkTraces = new ArrayList<>();
    
    @ElementCollection
    private List<String> databaseQueries = new ArrayList<>();
    
    @ElementCollection
    private List<String> apiCalls = new ArrayList<>();
    
    @ElementCollection
    private List<String> securityFindings = new ArrayList<>();
    
    @ElementCollection
    private List<String> complianceChecks = new ArrayList<>();
    
    @ElementCollection
    private List<String> vulnerabilities = new ArrayList<>();
    
    private Integer totalTests = 0;
    private Integer passedTests = 0;
    private Integer failedTests = 0;
    private Integer skippedTests = 0;
    
    private Double successRate = 0.0;
    private Double coveragePercentage = 0.0;
    private Double qualityScore = 0.0;
    private Double securityScore = 0.0;
    private Double performanceScore = 0.0;
    
    @Column(length = 4000)
    private String llmAnalysis;
    
    @Column(length = 4000)
    private String llmInsights;
    
    @Column(length = 4000)
    private String llmRecommendations;
    
    @Column(length = 2000)
    private String llmModelUsed;
    
    private LocalDateTime llmAnalysisTimestamp;
    
    @Column(length = 4000)
    private String contractValidationResult;
    
    @Column(length = 4000)
    private String dataConsistencyResult;
    
    @Column(length = 4000)
    private String integrationTestResult;
    
    @Column(length = 4000)
    private String performanceTestResult;
    
    @Column(length = 4000)
    private String securityTestResult;
    
    @Column(length = 2000)
    private String testFramework; // JUNIT, TESTNG, CUCUMBER, CUSTOM
    
    @Column(length = 2000)
    private String executionEnvironment;
    
    @Column(length = 2000)
    private String platform;
    
    @Column(length = 2000)
    private String browser; // Для веб-тестов
    
    @Column(length = 2000)
    private String deviceInfo; // Для мобильных тестов
    
    @Column(length = 4000)
    private String systemConfiguration;
    
    @Column(length = 2000)
    private String testDataSource;
    
    @Column(length = 4000)
    private String dependencies;
    
    private Boolean isAutomated = false;
    private Boolean isManualReviewRequired = false;
    private Boolean isIntegrationTest = false;
    private Boolean isRegressionTest = false;
    private Boolean isSmokeTest = false;
    
    @Column(length = 2000)
    private String tags;
    
    @Column(length = 4000)
    private String metadata;
    
    @Column(length = 2000)
    private String version;
    
    @Column(length = 2000)
    private String buildNumber;
    
    @Column(length = 2000)
    private String environmentType; // DEV, TEST, STAGING, PROD
    
    @Column(length = 4000)
    private String notes;
    
    @Column(length = 2000)
    private String relatedResultId;
    
    private String parentResultId;
    
    // Конструкторы
    public ExecutionResult() {
        this.resultId = "EXEC_RES_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.generatedAt = LocalDateTime.now();
        this.validationChecks = new ArrayList<>();
        this.failedValidations = new ArrayList<>();
        this.metrics = new HashMap<>();
        this.performanceMetrics = new HashMap<>();
        this.securityMetrics = new HashMap<>();
        this.qualityMetrics = new HashMap<>();
        this.generatedFiles = new ArrayList<>();
        this.capturedScreenshots = new ArrayList<>();
        this.logEntries = new ArrayList<>();
        this.networkTraces = new ArrayList<>();
        this.databaseQueries = new ArrayList<>();
        this.apiCalls = new ArrayList<>();
        this.securityFindings = new ArrayList<>();
        this.complianceChecks = new ArrayList<>();
        this.vulnerabilities = new ArrayList<>();
    }
    
    public ExecutionResult(String sessionId, String stepId, String resultName) {
        this();
        this.sessionId = sessionId;
        this.stepId = stepId;
        this.resultName = resultName;
    }
    
    // Вспомогательные методы
    public boolean isSuccess() {
        return status == ResultStatus.PASSED;
    }
    
    public boolean isFailure() {
        return status == ResultStatus.FAILED || status == ResultStatus.ERROR;
    }
    
    public boolean hasMetrics() {
        return !metrics.isEmpty();
    }
    
    public boolean hasSecurityFindings() {
        return !securityFindings.isEmpty() || !vulnerabilities.isEmpty();
    }
    
    public boolean hasLLMAnalysis() {
        return llmAnalysis != null && !llmAnalysis.trim().isEmpty();
    }
    
    public double getOverallScore() {
        double totalScore = 0.0;
        int scoreCount = 0;
        
        if (successRate > 0) {
            totalScore += successRate;
            scoreCount++;
        }
        if (qualityScore > 0) {
            totalScore += qualityScore;
            scoreCount++;
        }
        if (securityScore > 0) {
            totalScore += securityScore;
            scoreCount++;
        }
        if (performanceScore > 0) {
            totalScore += performanceScore;
            scoreCount++;
        }
        
        return scoreCount > 0 ? totalScore / scoreCount : 0.0;
    }
    
    public void addMetric(String key, String value) {
        if (key != null && value != null) {
            metrics.put(key, value);
            updateScores();
        }
    }
    
    public void addPerformanceMetric(String key, String value) {
        if (key != null && value != null) {
            performanceMetrics.put(key, value);
            calculatePerformanceScore();
        }
    }
    
    public void addSecurityMetric(String key, String value) {
        if (key != null && value != null) {
            securityMetrics.put(key, value);
            calculateSecurityScore();
        }
    }
    
    public void addQualityMetric(String key, String value) {
        if (key != null && value != null) {
            qualityMetrics.put(key, value);
            calculateQualityScore();
        }
    }
    
    public void addValidationCheck(String check) {
        if (check != null && !validationChecks.contains(check)) {
            validationChecks.add(check);
        }
    }
    
    public void addFailedValidation(String validation) {
        if (validation != null && !failedValidations.contains(validation)) {
            failedValidations.add(validation);
            this.status = ResultStatus.FAILED;
            this.severity = SeverityLevel.HIGH;
        }
    }
    
    public void addSecurityFinding(String finding) {
        if (finding != null && !securityFindings.contains(finding)) {
            securityFindings.add(finding);
            this.severity = SeverityLevel.HIGH;
        }
    }
    
    public void addVulnerability(String vulnerability) {
        if (vulnerability != null && !vulnerabilities.contains(vulnerability)) {
            vulnerabilities.add(vulnerability);
            this.severity = SeverityLevel.CRITICAL;
        }
    }
    
    public void addLogEntry(String logEntry) {
        if (logEntry != null) {
            String timestamp = LocalDateTime.now().toString();
            logEntries.add("[" + timestamp + "] " + logEntry);
        }
    }
    
    public void addGeneratedFile(String filePath) {
        if (filePath != null && !generatedFiles.contains(filePath)) {
            generatedFiles.add(filePath);
        }
    }
    
    public void setLLMAnalysis(String analysis, String modelUsed) {
        this.llmAnalysis = analysis;
        this.llmModelUsed = modelUsed;
        this.llmAnalysisTimestamp = LocalDateTime.now();
    }
    
    
    public void calculateSuccessRate() {
        if (totalTests > 0) {
            this.successRate = (double) passedTests / totalTests * 100.0;
        }
    }
    
    public void updateScores() {
        calculateSuccessRate();
        calculatePerformanceScore();
        calculateSecurityScore();
        calculateQualityScore();
    }
    
    private void calculatePerformanceScore() {
        // Базовая логика расчета балла производительности
        if (!performanceMetrics.isEmpty()) {
            double totalScore = 0.0;
            int metricsCount = 0;
            
            for (String value : performanceMetrics.values()) {
                try {
                    double metricValue = Double.parseDouble(value);
                    // Нормализуем метрику к диапазону 0-100
                    double normalizedScore = Math.min(100.0, Math.max(0.0, metricValue));
                    totalScore += normalizedScore;
                    metricsCount++;
                } catch (NumberFormatException e) {
                    // Игнорируем некорректные значения
                }
            }
            
            this.performanceScore = metricsCount > 0 ? totalScore / metricsCount : 0.0;
        }
    }
    
    private void calculateSecurityScore() {
        // Базовый расчет балла безопасности
        int totalSecurityChecks = securityMetrics.size() + securityFindings.size();
        int passedChecks = totalSecurityChecks - vulnerabilities.size();
        
        if (totalSecurityChecks > 0) {
            this.securityScore = (double) passedChecks / totalSecurityChecks * 100.0;
        }
    }
    
    private void calculateQualityScore() {
        // Базовый расчет балла качества
        if (totalTests > 0) {
            double testScore = (double) passedTests / totalTests * 100.0;
            double validationScore = validationChecks.size() > 0 ? 
                (double) (validationChecks.size() - failedValidations.size()) / validationChecks.size() * 100.0 : 100.0;
            
            this.qualityScore = (testScore + validationScore) / 2.0;
        }
    }
    
    public void complete(boolean success) {
        this.updatedAt = LocalDateTime.now();
        if (success) {
            this.status = ResultStatus.PASSED;
            this.severity = SeverityLevel.INFO;
        } else {
            this.status = ResultStatus.FAILED;
            this.severity = SeverityLevel.HIGH;
        }
        updateScores();
    }
    
    public void recordError(String errorMessage, String errorCategory) {
        this.status = ResultStatus.ERROR;
        this.errorMessage = errorMessage;
        this.errorCategory = errorCategory;
        this.severity = SeverityLevel.CRITICAL;
        this.updatedAt = LocalDateTime.now();
        addLogEntry("Error recorded: " + errorMessage);
    }
    
    // Enums
    public enum ResultStatus {
        PENDING,
        IN_PROGRESS,
        PASSED,
        FAILED,
        ERROR,
        SKIPPED,
        CANCELLED,
        TIMEOUT
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    
    public String getResultName() { return resultName; }
    public void setResultName(String resultName) { this.resultName = resultName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ResultStatus getStatus() { return status; }
    public void setStatus(ResultStatus status) { this.status = status; }
    
    public SeverityLevel getSeverity() { return severity; }
    public void setSeverity(SeverityLevel severity) { this.severity = severity; }
    
    public String getResultType() { return resultType; }
    public void setResultType(String resultType) { this.resultType = resultType; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getRawOutput() { return rawOutput; }
    public void setRawOutput(String rawOutput) { this.rawOutput = rawOutput; }
    
    public String getProcessedOutput() { return processedOutput; }
    public void setProcessedOutput(String processedOutput) { this.processedOutput = processedOutput; }
    
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    
    public String getValidationResult() { return validationResult; }
    public void setValidationResult(String validationResult) { this.validationResult = validationResult; }
    
    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorCategory() { return errorCategory; }
    public void setErrorCategory(String errorCategory) { this.errorCategory = errorCategory; }
    
    public List<String> getValidationChecks() { return validationChecks; }
    public void setValidationChecks(List<String> validationChecks) { this.validationChecks = validationChecks; }
    
    public List<String> getFailedValidations() { return failedValidations; }
    public void setFailedValidations(List<String> failedValidations) { this.failedValidations = failedValidations; }
    
    public Map<String, String> getMetrics() { return metrics; }
    public void setMetrics(Map<String, String> metrics) { this.metrics = metrics; }
    
    public Map<String, String> getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(Map<String, String> performanceMetrics) { this.performanceMetrics = performanceMetrics; }
    
    public Map<String, String> getSecurityMetrics() { return securityMetrics; }
    public void setSecurityMetrics(Map<String, String> securityMetrics) { this.securityMetrics = securityMetrics; }
    
    public Map<String, String> getQualityMetrics() { return qualityMetrics; }
    public void setQualityMetrics(Map<String, String> qualityMetrics) { this.qualityMetrics = qualityMetrics; }
    
    public List<String> getGeneratedFiles() { return generatedFiles; }
    public void setGeneratedFiles(List<String> generatedFiles) { this.generatedFiles = generatedFiles; }
    
    public List<String> getCapturedScreenshots() { return capturedScreenshots; }
    public void setCapturedScreenshots(List<String> capturedScreenshots) { this.capturedScreenshots = capturedScreenshots; }
    
    public List<String> getLogEntries() { return logEntries; }
    public void setLogEntries(List<String> logEntries) { this.logEntries = logEntries; }
    
    public List<String> getNetworkTraces() { return networkTraces; }
    public void setNetworkTraces(List<String> networkTraces) { this.networkTraces = networkTraces; }
    
    public List<String> getDatabaseQueries() { return databaseQueries; }
    public void setDatabaseQueries(List<String> databaseQueries) { this.databaseQueries = databaseQueries; }
    
    public List<String> getApiCalls() { return apiCalls; }
    public void setApiCalls(List<String> apiCalls) { this.apiCalls = apiCalls; }
    
    public List<String> getSecurityFindings() { return securityFindings; }
    public void setSecurityFindings(List<String> securityFindings) { this.securityFindings = securityFindings; }
    
    public List<String> getComplianceChecks() { return complianceChecks; }
    public void setComplianceChecks(List<String> complianceChecks) { this.complianceChecks = complianceChecks; }
    
    public List<String> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<String> vulnerabilities) { this.vulnerabilities = vulnerabilities; }
    
    public Integer getTotalTests() { return totalTests; }
    public void setTotalTests(Integer totalTests) { this.totalTests = totalTests; }
    
    public Integer getPassedTests() { return passedTests; }
    public void setPassedTests(Integer passedTests) { this.passedTests = passedTests; }
    
    public Integer getFailedTests() { return failedTests; }
    public void setFailedTests(Integer failedTests) { this.failedTests = failedTests; }
    
    public Integer getSkippedTests() { return skippedTests; }
    public void setSkippedTests(Integer skippedTests) { this.skippedTests = skippedTests; }
    
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    
    public Double getCoveragePercentage() { return coveragePercentage; }
    public void setCoveragePercentage(Double coveragePercentage) { this.coveragePercentage = coveragePercentage; }
    
    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }
    
    public Double getSecurityScore() { return securityScore; }
    public void setSecurityScore(Double securityScore) { this.securityScore = securityScore; }
    
    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }
    
    public String getLlmAnalysis() { return llmAnalysis; }
    public void setLlmAnalysis(String llmAnalysis) { this.llmAnalysis = llmAnalysis; }
    
    public String getLlmInsights() { return llmInsights; }
    public void setLlmInsights(String llmInsights) { this.llmInsights = llmInsights; }
    
    public String getLlmRecommendations() { return llmRecommendations; }
    public void setLlmRecommendations(String llmRecommendations) { this.llmRecommendations = llmRecommendations; }
    
    public String getLlmModelUsed() { return llmModelUsed; }
    public void setLlmModelUsed(String llmModelUsed) { this.llmModelUsed = llmModelUsed; }
    
    public LocalDateTime getLlmAnalysisTimestamp() { return llmAnalysisTimestamp; }
    public void setLlmAnalysisTimestamp(LocalDateTime llmAnalysisTimestamp) { this.llmAnalysisTimestamp = llmAnalysisTimestamp; }
    
    public String getContractValidationResult() { return contractValidationResult; }
    public void setContractValidationResult(String contractValidationResult) { this.contractValidationResult = contractValidationResult; }
    
    public String getDataConsistencyResult() { return dataConsistencyResult; }
    public void setDataConsistencyResult(String dataConsistencyResult) { this.dataConsistencyResult = dataConsistencyResult; }
    
    public String getIntegrationTestResult() { return integrationTestResult; }
    public void setIntegrationTestResult(String integrationTestResult) { this.integrationTestResult = integrationTestResult; }
    
    public String getPerformanceTestResult() { return performanceTestResult; }
    public void setPerformanceTestResult(String performanceTestResult) { this.performanceTestResult = performanceTestResult; }
    
    public String getSecurityTestResult() { return securityTestResult; }
    public void setSecurityTestResult(String securityTestResult) { this.securityTestResult = securityTestResult; }
    
    public String getTestFramework() { return testFramework; }
    public void setTestFramework(String testFramework) { this.testFramework = testFramework; }
    
    public String getExecutionEnvironment() { return executionEnvironment; }
    public void setExecutionEnvironment(String executionEnvironment) { this.executionEnvironment = executionEnvironment; }
    
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
    
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public String getSystemConfiguration() { return systemConfiguration; }
    public void setSystemConfiguration(String systemConfiguration) { this.systemConfiguration = systemConfiguration; }
    
    public String getTestDataSource() { return testDataSource; }
    public void setTestDataSource(String testDataSource) { this.testDataSource = testDataSource; }
    
    public String getDependencies() { return dependencies; }
    public void setDependencies(String dependencies) { this.dependencies = dependencies; }
    
    public Boolean getIsAutomated() { return isAutomated; }
    public void setIsAutomated(Boolean isAutomated) { this.isAutomated = isAutomated; }
    
    public Boolean getIsManualReviewRequired() { return isManualReviewRequired; }
    public void setIsManualReviewRequired(Boolean isManualReviewRequired) { this.isManualReviewRequired = isManualReviewRequired; }
    
    public Boolean getIsIntegrationTest() { return isIntegrationTest; }
    public void setIsIntegrationTest(Boolean isIntegrationTest) { this.isIntegrationTest = isIntegrationTest; }
    
    public Boolean getIsRegressionTest() { return isRegressionTest; }
    public void setIsRegressionTest(Boolean isRegressionTest) { this.isRegressionTest = isRegressionTest; }
    
    public Boolean getIsSmokeTest() { return isSmokeTest; }
    public void setIsSmokeTest(Boolean isSmokeTest) { this.isSmokeTest = isSmokeTest; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getBuildNumber() { return buildNumber; }
    public void setBuildNumber(String buildNumber) { this.buildNumber = buildNumber; }
    
    public String getEnvironmentType() { return environmentType; }
    public void setEnvironmentType(String environmentType) { this.environmentType = environmentType; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getRelatedResultId() { return relatedResultId; }
    public void setRelatedResultId(String relatedResultId) { this.relatedResultId = relatedResultId; }
    
    public String getParentResultId() { return parentResultId; }
    public void setParentResultId(String parentResultId) { this.parentResultId = parentResultId; }
    
    @Override
    public String toString() {
        return "ExecutionResult{" +
                "resultId='" + resultId + '\'' +
                ", resultName='" + resultName + '\'' +
                ", status=" + status +
                ", severity=" + severity +
                ", successRate=" + successRate +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionResult that = (ExecutionResult) o;
        return Objects.equals(resultId, that.resultId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(resultId);
    }
}