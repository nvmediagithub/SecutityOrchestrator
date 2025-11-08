package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Лог ошибок - централизованное хранение всех ошибок, предупреждений и событий
 * в системе сквозного тестирования для анализа и мониторинга
 */
@Entity
@Table(name = "error_logs")
public class ErrorLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String logId;
    
    @Column(nullable = false)
    private String sessionId;
    
    @Column(nullable = false)
    private String stepId;
    
    @Column(length = 1000, nullable = false)
    private String logName;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel logLevel = LogLevel.INFO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType logType;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity = SeverityLevel.INFO;
    
    @Column(length = 2000)
    private String source; // Источник лога: EXECUTION_ENGINE, OWASP_SERVICE, API_CLIENT, BPMN_ENGINE
    
    @Column(length = 2000)
    private String category; // EXECUTION, VALIDATION, TIMEOUT, SECURITY, INTEGRATION, SYSTEM
    
    private LocalDateTime timestamp;
    private LocalDateTime createdAt;
    
    @Column(length = 4000)
    private String message;
    
    @Column(length = 4000)
    private String detailedMessage;
    
    @Column(length = 4000)
    private String stackTrace;
    
    @Column(length = 2000)
    private String errorCode;
    
    @Column(length = 2000)
    private String errorType; // ASSERTION_ERROR, TIMEOUT_ERROR, NETWORK_ERROR, PARSING_ERROR, LLM_ERROR
    
    @Column(length = 2000)
    private String httpStatus; // Для HTTP ошибок
    
    @Column(length = 2000)
    private String httpMethod; // Для API вызовов
    
    @Column(length = 2000)
    private String endpoint; // Для API endpoints
    
    @Column(length = 2000)
    private String component; // Компонент где произошла ошибка
    
    @Column(length = 2000)
    private String operation; // Операция которая вызвала ошибку
    
    @Column(length = 4000)
    private String context; // Контекст ошибки (JSON строка)
    
    @Column(length = 4000)
    private String requestData; // Данные запроса
    
    @Column(length = 4000)
    private String responseData; // Данные ответа
    
    @Column(length = 4000)
    private String validationDetails; // Детали валидации
    
    @Column(length = 4000)
    private String contractViolation; // Нарушение контракта
    
    @Column(length = 4000)
    private String securityViolation; // Нарушение безопасности
    
    @ElementCollection
    private List<String> relatedLogs = new ArrayList<>();
    
    @ElementCollection
    private List<String> relatedErrors = new ArrayList<>();
    
    @ElementCollection
    private List<String> affectedComponents = new ArrayList<>();
    
    @ElementCollection
    private Map<String, String> properties = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> metrics = new HashMap<>();
    
    private Long durationMs; // Время выполнения операции
    
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    
    private Boolean isRecoverable = false;
    private Boolean isAutoRecoverable = false;
    private Boolean requiresManualIntervention = false;
    
    @Column(length = 2000)
    private String recoveryAction; // Рекомендуемое действие по восстановлению
    
    @Column(length = 4000)
    private String resolution; // Как была решена проблема
    
    @Column(length = 2000)
    private String resolvedBy; // Кто решил проблему
    
    private LocalDateTime resolvedAt;
    
    @Column(length = 2000)
    private String llmAnalysis; // Анализ ошибки от LLM
    
    @Column(length = 2000)
    private String llmRecommendation; // Рекомендации от LLM
    
    private LocalDateTime llmAnalysisTimestamp;
    
    @Column(length = 2000)
    private String userId; // Пользователь связанный с логом
    
    @Column(length = 2000)
    private String environment; // Среда выполнения
    
    @Column(length = 2000)
    private String host; // Хост где произошла ошибка
    
    @Column(length = 2000)
    private String threadName; // Имя потока
    
    @Column(length = 2000)
    private String className; // Класс где произошла ошибка
    
    @Column(length = 2000)
    private String methodName; // Метод где произошла ошибка
    
    private Integer lineNumber = 0;
    
    @Column(length = 4000)
    private String systemInfo; // Информация о системе
    
    @Column(length = 2000)
    private String correlationId; // ID корреляции для трассировки
    
    @Column(length = 2000)
    private String traceId; // ID трассировки
    
    @Column(length = 4000)
    private String additionalContext; // Дополнительный контекст
    
    @Column(length = 2000)
    private String tags; // Теги для фильтрации
    
    @Column(length = 4000)
    private String metadata; // Метаданные
    
    @Column(length = 4000)
    private String notes; // Заметки
    
    private Boolean isAlertTriggered = false;
    private Boolean isNotificationSent = false;
    
    @Column(length = 2000)
    private String relatedIssueId; // ID связанной задачи/issue
    
    // Конструкторы
    public ErrorLog() {
        this.logId = "ERR_LOG_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.timestamp = LocalDateTime.now();
        this.relatedLogs = new ArrayList<>();
        this.relatedErrors = new ArrayList<>();
        this.affectedComponents = new ArrayList<>();
        this.properties = new HashMap<>();
        this.metrics = new HashMap<>();
    }
    
    public ErrorLog(String sessionId, String stepId, String message, LogLevel logLevel, LogType logType) {
        this();
        this.sessionId = sessionId;
        this.stepId = stepId;
        this.message = message;
        this.logLevel = logLevel;
        this.logType = logType;
        this.severity = mapLogLevelToSeverity(logLevel);
    }
    
    // Вспомогательные методы
    private SeverityLevel mapLogLevelToSeverity(LogLevel logLevel) {
        return switch (logLevel) {
            case TRACE, DEBUG -> SeverityLevel.INFO;
            case INFO -> SeverityLevel.INFO;
            case WARN -> SeverityLevel.LOW;
            case ERROR -> SeverityLevel.MEDIUM;
            case FATAL -> SeverityLevel.HIGH;
            case CRITICAL -> SeverityLevel.CRITICAL;
        };
    }
    
    public boolean isError() {
        return logLevel == LogLevel.ERROR || logLevel == LogLevel.FATAL || logLevel == LogLevel.CRITICAL;
    }
    
    public boolean isWarning() {
        return logLevel == LogLevel.WARN;
    }
    
    public boolean isRecoverable() {
        return isRecoverable || isAutoRecoverable;
    }
    
    public boolean needsAttention() {
        return severity == SeverityLevel.HIGH || severity == SeverityLevel.CRITICAL;
    }
    
    public boolean hasLLMAnalysis() {
        return llmAnalysis != null && !llmAnalysis.trim().isEmpty();
    }
    
    public void addRelatedLog(String relatedLogId) {
        if (relatedLogId != null && !relatedLogs.contains(relatedLogId)) {
            relatedLogs.add(relatedLogId);
        }
    }
    
    public void addRelatedError(String errorId) {
        if (errorId != null && !relatedErrors.contains(errorId)) {
            relatedErrors.add(errorId);
        }
    }
    
    public void addAffectedComponent(String component) {
        if (component != null && !affectedComponents.contains(component)) {
            affectedComponents.add(component);
        }
    }
    
    public void addProperty(String key, String value) {
        if (key != null && value != null) {
            properties.put(key, value);
        }
    }
    
    public void addMetric(String key, String value) {
        if (key != null && value != null) {
            metrics.put(key, value);
        }
    }
    
    public void markAsRecovered(String resolvedBy, String resolution) {
        this.resolvedBy = resolvedBy;
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
        this.logLevel = LogLevel.INFO;
        this.severity = SeverityLevel.INFO;
        addLog("Error recovered: " + resolution);
    }
    
    public void requestManualIntervention(String reason) {
        this.requiresManualIntervention = true;
        this.recoveryAction = "Manual intervention required: " + reason;
        this.isAlertTriggered = true;
        addLog("Manual intervention requested: " + reason);
    }
    
    public void setLLMAnalysis(String analysis, String recommendation) {
        this.llmAnalysis = analysis;
        this.llmRecommendation = recommendation;
        this.llmAnalysisTimestamp = LocalDateTime.now();
    }
    
    public void addLog(String additionalMessage) {
        if (additionalMessage != null) {
            String timestamp = LocalDateTime.now().toString();
            this.detailedMessage = (this.detailedMessage != null ? this.detailedMessage + "\n" : "") +
                                  "[" + timestamp + "] " + additionalMessage;
        }
    }
    
    public void triggerAlert(String alertMessage) {
        this.isAlertTriggered = true;
        this.isNotificationSent = true;
        addLog("Alert triggered: " + alertMessage);
    }
    
    // Enums
    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, FATAL, CRITICAL
    }
    
    public enum LogType {
        EXECUTION_LOG,
        ERROR_LOG,
        SECURITY_LOG,
        PERFORMANCE_LOG,
        VALIDATION_LOG,
        INTEGRATION_LOG,
        SYSTEM_LOG,
        DEBUG_LOG,
        AUDIT_LOG,
        BUSINESS_LOGIC_LOG
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    
    public String getLogName() { return logName; }
    public void setLogName(String logName) { this.logName = logName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LogLevel getLogLevel() { return logLevel; }
    public void setLogLevel(LogLevel logLevel) { this.logLevel = logLevel; }
    
    public LogType getLogType() { return logType; }
    public void setLogType(LogType logType) { this.logType = logType; }
    
    public SeverityLevel getSeverity() { return severity; }
    public void setSeverity(SeverityLevel severity) { this.severity = severity; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getDetailedMessage() { return detailedMessage; }
    public void setDetailedMessage(String detailedMessage) { this.detailedMessage = detailedMessage; }
    
    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }
    
    public String getHttpStatus() { return httpStatus; }
    public void setHttpStatus(String httpStatus) { this.httpStatus = httpStatus; }
    
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public String getRequestData() { return requestData; }
    public void setRequestData(String requestData) { this.requestData = requestData; }
    
    public String getResponseData() { return responseData; }
    public void setResponseData(String responseData) { this.responseData = responseData; }
    
    public String getValidationDetails() { return validationDetails; }
    public void setValidationDetails(String validationDetails) { this.validationDetails = validationDetails; }
    
    public String getContractViolation() { return contractViolation; }
    public void setContractViolation(String contractViolation) { this.contractViolation = contractViolation; }
    
    public String getSecurityViolation() { return securityViolation; }
    public void setSecurityViolation(String securityViolation) { this.securityViolation = securityViolation; }
    
    public List<String> getRelatedLogs() { return relatedLogs; }
    public void setRelatedLogs(List<String> relatedLogs) { this.relatedLogs = relatedLogs; }
    
    public List<String> getRelatedErrors() { return relatedErrors; }
    public void setRelatedErrors(List<String> relatedErrors) { this.relatedErrors = relatedErrors; }
    
    public List<String> getAffectedComponents() { return affectedComponents; }
    public void setAffectedComponents(List<String> affectedComponents) { this.affectedComponents = affectedComponents; }
    
    public Map<String, String> getProperties() { return properties; }
    public void setProperties(Map<String, String> properties) { this.properties = properties; }
    
    public Map<String, String> getMetrics() { return metrics; }
    public void setMetrics(Map<String, String> metrics) { this.metrics = metrics; }
    
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public Boolean getIsRecoverable() { return isRecoverable; }
    public void setIsRecoverable(Boolean isRecoverable) { this.isRecoverable = isRecoverable; }
    
    public Boolean getIsAutoRecoverable() { return isAutoRecoverable; }
    public void setIsAutoRecoverable(Boolean isAutoRecoverable) { this.isAutoRecoverable = isAutoRecoverable; }
    
    public Boolean getRequiresManualIntervention() { return requiresManualIntervention; }
    public void setRequiresManualIntervention(Boolean requiresManualIntervention) { this.requiresManualIntervention = requiresManualIntervention; }
    
    public String getRecoveryAction() { return recoveryAction; }
    public void setRecoveryAction(String recoveryAction) { this.recoveryAction = recoveryAction; }
    
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    
    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public String getLlmAnalysis() { return llmAnalysis; }
    public void setLlmAnalysis(String llmAnalysis) { this.llmAnalysis = llmAnalysis; }
    
    public String getLlmRecommendation() { return llmRecommendation; }
    public void setLlmRecommendation(String llmRecommendation) { this.llmRecommendation = llmRecommendation; }
    
    public LocalDateTime getLlmAnalysisTimestamp() { return llmAnalysisTimestamp; }
    public void setLlmAnalysisTimestamp(LocalDateTime llmAnalysisTimestamp) { this.llmAnalysisTimestamp = llmAnalysisTimestamp; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public String getThreadName() { return threadName; }
    public void setThreadName(String threadName) { this.threadName = threadName; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    
    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    
    public String getSystemInfo() { return systemInfo; }
    public void setSystemInfo(String systemInfo) { this.systemInfo = systemInfo; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public String getAdditionalContext() { return additionalContext; }
    public void setAdditionalContext(String additionalContext) { this.additionalContext = additionalContext; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Boolean getIsAlertTriggered() { return isAlertTriggered; }
    public void setIsAlertTriggered(Boolean isAlertTriggered) { this.isAlertTriggered = isAlertTriggered; }
    
    public Boolean getIsNotificationSent() { return isNotificationSent; }
    public void setIsNotificationSent(Boolean isNotificationSent) { this.isNotificationSent = isNotificationSent; }
    
    public String getRelatedIssueId() { return relatedIssueId; }
    public void setRelatedIssueId(String relatedIssueId) { this.relatedIssueId = relatedIssueId; }
    
    @Override
    public String toString() {
        return "ErrorLog{" +
                "logId='" + logId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", logLevel=" + logLevel +
                ", logType=" + logType +
                ", severity=" + severity +
                ", message='" + message + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorLog errorLog = (ErrorLog) o;
        return Objects.equals(logId, errorLog.logId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
}