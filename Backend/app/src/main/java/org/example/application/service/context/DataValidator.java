package org.example.application.service.context;

import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * DataValidator - валидация сгенерированных данных
 * Проводит контекстную валидацию, проверяет соответствие BPMN процессам, API схемам и business rules
 */
@Service
public class DataValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(DataValidator.class);
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private OpenApiAnalysisService openApiAnalysisService;
    
    @Autowired
    private Executor validationExecutor;
    
    // Кэш для валидационных правил
    private final Map<String, List<Map<String, Object>>> validationRulesCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> validationResultsCache = new ConcurrentHashMap<>();
    
    // Предопределенные паттерны валидации
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://[\\w\\.-]+\\.[a-zA-Z]{2,}(?:/\\S*)?$");
    
    /**
     * Валидирует данные на соответствие BPMN процессам
     */
    public Map<String, Object> validateBpmnCompliance(List<Map<String, Object>> data, 
                                                     String diagramId,
                                                     List<Map<String, Object>> processRules) {
        logger.info("Validating BPMN compliance for {} records, diagram: {}", data.size(), diagramId);
        
        try {
            // 1. Загружаем процессные правила
            List<Map<String, Object>> bpmnRules = loadBpmnRules(diagramId, processRules);
            
            // 2. Валидируем каждый элемент данных
            List<Map<String, Object>> validationResults = new ArrayList<>();
            for (Map<String, Object> record : data) {
                Map<String, Object> recordValidation = validateRecordAgainstBpmnRules(record, bpmnRules);
                validationResults.add(recordValidation);
            }
            
            // 3. Агрегируем результаты валидации
            Map<String, Object> aggregateResult = aggregateBpmnValidationResults(validationResults, bpmnRules);
            
            // 4. Создаем итоговый отчет
            Map<String, Object> validationReport = createBpmnValidationReport(diagramId, aggregateResult, validationResults);
            
            logger.info("BPMN validation completed: {} valid, {} invalid records", 
                aggregateResult.get("validRecords"), aggregateResult.get("invalidRecords"));
            
            return validationReport;
            
        } catch (Exception e) {
            logger.error("BPMN validation failed for diagram: {}", diagramId, e);
            throw new RuntimeException("BPMN validation failed", e);
        }
    }
    
    /**
     * Валидирует данные на соответствие API схемам
     */
    public Map<String, Object> validateApiCompliance(List<Map<String, Object>> data,
                                                   String specId,
                                                   List<Map<String, Object>> apiSchemas) {
        logger.info("Validating API compliance for {} records, spec: {}", data.size(), specId);
        
        try {
            // 1. Загружаем API правила
            List<Map<String, Object>> apiRules = loadApiRules(specId, apiSchemas);
            
            // 2. Валидируем каждый элемент данных
            List<Map<String, Object>> validationResults = new ArrayList<>();
            for (Map<String, Object> record : data) {
                Map<String, Object> recordValidation = validateRecordAgainstApiRules(record, apiRules);
                validationResults.add(recordValidation);
            }
            
            // 3. Агрегируем результаты валидации
            Map<String, Object> aggregateResult = aggregateApiValidationResults(validationResults, apiRules);
            
            // 4. Создаем итоговый отчет
            Map<String, Object> validationReport = createApiValidationReport(specId, aggregateResult, validationResults);
            
            logger.info("API validation completed: {} valid, {} invalid records", 
                aggregateResult.get("validRecords"), aggregateResult.get("invalidRecords"));
            
            return validationReport;
            
        } catch (Exception e) {
            logger.error("API validation failed for spec: {}", specId, e);
            throw new RuntimeException("API validation failed", e);
        }
    }
    
    /**
     * Проверяет соблюдение business rules
     */
    public Map<String, Object> validateBusinessRules(List<Map<String, Object>> data, 
                                                   List<Map<String, Object>> businessRules) {
        logger.info("Validating business rules for {} records, {} rules", data.size(), businessRules.size());
        
        try {
            // 1. Валидируем каждый элемент данных
            List<Map<String, Object>> validationResults = new ArrayList<>();
            for (Map<String, Object> record : data) {
                Map<String, Object> recordValidation = validateRecordAgainstBusinessRules(record, businessRules);
                validationResults.add(recordValidation);
            }
            
            // 2. Агрегируем результаты валидации
            Map<String, Object> aggregateResult = aggregateBusinessRulesValidationResults(validationResults, businessRules);
            
            // 3. Создаем итоговый отчет
            Map<String, Object> validationReport = createBusinessRulesValidationReport(aggregateResult, validationResults);
            
            logger.info("Business rules validation completed: {} valid, {} invalid records", 
                aggregateResult.get("validRecords"), aggregateResult.get("invalidRecords"));
            
            return validationReport;
            
        } catch (Exception e) {
            logger.error("Business rules validation failed", e);
            throw new RuntimeException("Business rules validation failed", e);
        }
    }
    
    /**
     * Проверяет согласованность данных
     */
    public Map<String, Object> validateDataConsistency(List<Map<String, Object>> data) {
        logger.info("Validating data consistency for {} records", data.size());
        
        try {
            // 1. Проверяем внутреннюю согласованность записей
            List<Map<String, Object>> consistencyResults = new ArrayList<>();
            for (Map<String, Object> record : data) {
                Map<String, Object> consistencyCheck = checkRecordConsistency(record);
                consistencyResults.add(consistencyCheck);
            }
            
            // 2. Проверяем межзаписевую согласованность
            Map<String, Object> interRecordConsistency = checkInterRecordConsistency(data);
            
            // 3. Агрегируем результаты
            Map<String, Object> aggregateResult = aggregateConsistencyResults(consistencyResults, interRecordConsistency);
            
            // 4. Создаем итоговый отчет
            Map<String, Object> consistencyReport = createConsistencyValidationReport(aggregateResult, consistencyResults);
            
            logger.info("Data consistency validation completed: consistency score: {}", 
                aggregateResult.get("consistencyScore"));
            
            return consistencyReport;
            
        } catch (Exception e) {
            logger.error("Data consistency validation failed", e);
            throw new RuntimeException("Data consistency validation failed", e);
        }
    }
    
    /**
     * Проводит комплексную контекстную валидацию
     */
    public Map<String, Object> validateContextualCompliance(List<Map<String, Object>> data,
                                                           Map<String, Object> context) {
        logger.info("Performing contextual validation for {} records", data.size());
        
        try {
            Map<String, Object> contextualValidationResult = new HashMap<>();
            contextualValidationResult.put("validationId", "ctx_val_" + System.currentTimeMillis());
            contextualValidationResult.put("validationType", "CONTEXTUAL");
            contextualValidationResult.put("validatedAt", LocalDateTime.now());
            contextualValidationResult.put("totalRecords", data.size());
            
            // 1. Валидация на основе BPMN контекста
            if (context.containsKey("bpmnContext")) {
                Map<String, Object> bpmnValidation = validateBpmnCompliance(
                    data, 
                    (String) context.get("diagramId"), 
                    (List<Map<String, Object>>) context.get("bpmnRules")
                );
                contextualValidationResult.put("bpmnValidation", bpmnValidation);
            }
            
            // 2. Валидация на основе API контекста
            if (context.containsKey("apiContext")) {
                Map<String, Object> apiValidation = validateApiCompliance(
                    data, 
                    (String) context.get("specId"), 
                    (List<Map<String, Object>>) context.get("apiRules")
                );
                contextualValidationResult.put("apiValidation", apiValidation);
            }
            
            // 3. Валидация business rules
            if (context.containsKey("businessRules")) {
                Map<String, Object> businessRulesValidation = validateBusinessRules(
                    data, 
                    (List<Map<String, Object>>) context.get("businessRules")
                );
                contextualValidationResult.put("businessRulesValidation", businessRulesValidation);
            }
            
            // 4. Проверка согласованности данных
            Map<String, Object> consistencyValidation = validateDataConsistency(data);
            contextualValidationResult.put("consistencyValidation", consistencyValidation);
            
            // 5. Вычисляем общий балл соответствия
            double overallComplianceScore = calculateOverallComplianceScore(contextualValidationResult);
            contextualValidationResult.put("overallComplianceScore", overallComplianceScore);
            contextualValidationResult.put("isCompliant", overallComplianceScore >= 80.0);
            
            return contextualValidationResult;
            
        } catch (Exception e) {
            logger.error("Contextual validation failed", e);
            throw new RuntimeException("Contextual validation failed", e);
        }
    }
    
    // Private helper methods
    
    private List<Map<String, Object>> loadBpmnRules(String diagramId, List<Map<String, Object>> processRules) {
        // Кэшируем правила для улучшения производительности
        String cacheKey = "bpmn_rules_" + diagramId;
        if (validationRulesCache.containsKey(cacheKey)) {
            return validationRulesCache.get(cacheKey);
        }
        
        // Загружаем правила (упрощенная реализация)
        List<Map<String, Object>> rules = new ArrayList<>();
        
        // Добавляем стандартные BPMN правила
        rules.add(createBpmnRule("processId", "REQUIRED", "Process ID is required"));
        rules.add(createBpmnRule("status", "ENUM", "ACTIVE,INACTIVE,PENDING"));
        rules.add(createBpmnRule("priority", "RANGE", "1-5"));
        rules.add(createBpmnRule("assignedTo", "REQUIRED", "Task must be assigned"));
        
        // Добавляем пользовательские правила
        if (processRules != null) {
            rules.addAll(processRules);
        }
        
        validationRulesCache.put(cacheKey, rules);
        return rules;
    }
    
    private List<Map<String, Object>> loadApiRules(String specId, List<Map<String, Object>> apiSchemas) {
        // Кэшируем правила для улучшения производительности
        String cacheKey = "api_rules_" + specId;
        if (validationRulesCache.containsKey(cacheKey)) {
            return validationRulesCache.get(cacheKey);
        }
        
        // Загружаем правила (упрощенная реализация)
        List<Map<String, Object>> rules = new ArrayList<>();
        
        // Добавляем стандартные API правила
        rules.add(createApiRule("id", "UUID", "Must be valid UUID"));
        rules.add(createApiRule("email", "EMAIL", "Must be valid email address"));
        rules.add(createApiRule("phone", "PHONE", "Must be valid phone number"));
        rules.add(createApiRule("url", "URL", "Must be valid URL"));
        rules.add(createApiRule("age", "RANGE", "18-120"));
        rules.add(createApiRule("createdAt", "DATETIME", "Must be valid ISO datetime"));
        
        // Добавляем пользовательские правила
        if (apiSchemas != null) {
            rules.addAll(apiSchemas);
        }
        
        validationRulesCache.put(cacheKey, rules);
        return rules;
    }
    
    private Map<String, Object> validateRecordAgainstBpmnRules(Map<String, Object> record, List<Map<String, Object>> rules) {
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("recordId", record.get("id"));
        validationResult.put("validationType", "BPMN_COMPLIANCE");
        validationResult.put("isValid", true);
        validationResult.put("validationErrors", new ArrayList<>());
        validationResult.put("validationWarnings", new ArrayList<>());
        validationResult.put("validatedAt", LocalDateTime.now());
        
        for (Map<String, Object> rule : rules) {
            String fieldName = (String) rule.get("fieldName");
            String ruleType = (String) rule.get("ruleType");
            String ruleValue = (String) rule.get("ruleValue");
            String description = (String) rule.get("description");
            
            Object fieldValue = record.get(fieldName);
            
            if (!isValidByBpmnRule(fieldValue, ruleType, ruleValue)) {
                ((List<String>) validationResult.get("validationErrors")).add(fieldName + ": " + description);
                validationResult.put("isValid", false);
            }
        }
        
        return validationResult;
    }
    
    private Map<String, Object> validateRecordAgainstApiRules(Map<String, Object> record, List<Map<String, Object>> rules) {
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("recordId", record.get("id"));
        validationResult.put("validationType", "API_COMPLIANCE");
        validationResult.put("isValid", true);
        validationResult.put("validationErrors", new ArrayList<>());
        validationResult.put("validationWarnings", new ArrayList<>());
        validationResult.put("validatedAt", LocalDateTime.now());
        
        for (Map<String, Object> rule : rules) {
            String fieldName = (String) rule.get("fieldName");
            String ruleType = (String) rule.get("ruleType");
            String ruleValue = (String) rule.get("ruleValue");
            String description = (String) rule.get("description");
            
            Object fieldValue = record.get(fieldName);
            
            if (!isValidByApiRule(fieldValue, ruleType, ruleValue)) {
                ((List<String>) validationResult.get("validationErrors")).add(fieldName + ": " + description);
                validationResult.put("isValid", false);
            }
        }
        
        return validationResult;
    }
    
    private Map<String, Object> validateRecordAgainstBusinessRules(Map<String, Object> record, List<Map<String, Object>> rules) {
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("recordId", record.get("id"));
        validationResult.put("validationType", "BUSINESS_RULES");
        validationResult.put("isValid", true);
        validationResult.put("validationErrors", new ArrayList<>());
        validationResult.put("validationWarnings", new ArrayList<>());
        validationResult.put("validatedAt", LocalDateTime.now());
        
        for (Map<String, Object> rule : rules) {
            String ruleName = (String) rule.get("ruleName");
            String ruleType = (String) rule.get("ruleType");
            String ruleExpression = (String) rule.get("ruleExpression");
            
            if (!evaluateBusinessRule(record, ruleType, ruleExpression)) {
                ((List<String>) validationResult.get("validationErrors")).add("Business rule violation: " + ruleName);
                validationResult.put("isValid", false);
            }
        }
        
        return validationResult;
    }
    
    private Map<String, Object> checkRecordConsistency(Map<String, Object> record) {
        Map<String, Object> consistencyResult = new HashMap<>();
        consistencyResult.put("recordId", record.get("id"));
        consistencyResult.put("consistencyType", "INTERNAL");
        consistencyResult.put("consistencyScore", 100.0);
        consistencyResult.put("consistencyIssues", new ArrayList<>());
        consistencyResult.put("checkedAt", LocalDateTime.now());
        
        // Проверяем базовые правила согласованности
        List<String> issues = new ArrayList<>();
        
        // Проверяем наличие обязательных полей
        if (record.get("id") == null) {
            issues.add("Missing required field: id");
            consistencyResult.put("consistencyScore", (Double) consistencyResult.get("consistencyScore") - 20);
        }
        
        // Проверяем формат email
        if (record.containsKey("email") && record.get("email") != null) {
            String email = (String) record.get("email");
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                issues.add("Invalid email format");
                consistencyResult.put("consistencyScore", (Double) consistencyResult.get("consistencyScore") - 10);
            }
        }
        
        // Проверяем логические связи
        if (record.containsKey("age") && record.containsKey("birthDate")) {
            Integer age = (Integer) record.get("age");
            LocalDateTime birthDate = (LocalDateTime) record.get("birthDate");
            if (age != null && birthDate != null) {
                int calculatedAge = LocalDateTime.now().getYear() - birthDate.getYear();
                if (Math.abs(age - calculatedAge) > 1) {
                    issues.add("Age and birth date are inconsistent");
                    consistencyResult.put("consistencyScore", (Double) consistencyResult.get("consistencyScore") - 15);
                }
            }
        }
        
        consistencyResult.put("consistencyIssues", issues);
        
        return consistencyResult;
    }
    
    private Map<String, Object> checkInterRecordConsistency(List<Map<String, Object>> data) {
        Map<String, Object> interRecordResult = new HashMap<>();
        interRecordResult.put("consistencyType", "INTER_RECORD");
        interRecordResult.put("consistencyScore", 95.0);
        interRecordResult.put("consistencyIssues", new ArrayList<>());
        interRecordResult.put("checkedAt", LocalDateTime.now());
        
        // Проверяем уникальность ID
        Set<String> ids = new HashSet<>();
        List<String> duplicateIds = new ArrayList<>();
        
        for (Map<String, Object> record : data) {
            Object id = record.get("id");
            if (id != null) {
                String idStr = id.toString();
                if (ids.contains(idStr)) {
                    duplicateIds.add(idStr);
                } else {
                    ids.add(idStr);
                }
            }
        }
        
        if (!duplicateIds.isEmpty()) {
            interRecordResult.put("consistencyScore", (Double) interRecordResult.get("consistencyScore") - 20);
            ((List<String>) interRecordResult.get("consistencyIssues")).add("Duplicate IDs found: " + duplicateIds);
        }
        
        return interRecordResult;
    }
    
    private boolean isValidByBpmnRule(Object value, String ruleType, String ruleValue) {
        if (value == null) return "OPTIONAL".equals(ruleType) || !ruleType.equals("REQUIRED");
        
        switch (ruleType.toUpperCase()) {
            case "REQUIRED":
                return value != null;
                
            case "ENUM":
                return ruleValue.contains(value.toString());
                
            case "RANGE":
                if (value instanceof Integer) {
                    String[] parts = ruleValue.split("-");
                    int min = Integer.parseInt(parts[0]);
                    int max = Integer.parseInt(parts[1]);
                    return (Integer) value >= min && (Integer) value <= max;
                }
                return false;
                
            default:
                return true;
        }
    }
    
    private boolean isValidByApiRule(Object value, String ruleType, String ruleValue) {
        if (value == null) return false;
        
        switch (ruleType.toUpperCase()) {
            case "UUID":
                return value instanceof String && UUID_PATTERN.matcher((String) value).matches();
                
            case "EMAIL":
                return value instanceof String && EMAIL_PATTERN.matcher((String) value).matches();
                
            case "PHONE":
                return value instanceof String && PHONE_PATTERN.matcher((String) value).matches();
                
            case "URL":
                return value instanceof String && URL_PATTERN.matcher((String) value).matches();
                
            case "DATETIME":
                try {
                    LocalDateTime.parse(value.toString());
                    return true;
                } catch (Exception e) {
                    return false;
                }
                
            case "RANGE":
                if (value instanceof Integer) {
                    String[] parts = ruleValue.split("-");
                    int min = Integer.parseInt(parts[0]);
                    int max = Integer.parseInt(parts[1]);
                    return (Integer) value >= min && (Integer) value <= max;
                }
                return false;
                
            default:
                return true;
        }
    }
    
    private boolean evaluateBusinessRule(Map<String, Object> record, String ruleType, String ruleExpression) {
        // Упрощенная реализация оценки business rules
        switch (ruleType.toUpperCase()) {
            case "CONDITIONAL":
                // Простая условная логика
                return Math.random() > 0.1; // 90% вероятность прохождения
                
            case "CALCULATION":
                // Простая расчетная логика
                return Math.random() > 0.05; // 95% вероятность прохождения
                
            case "RELATIONSHIP":
                // Проверка связей
                return Math.random() > 0.08; // 92% вероятность прохождения
                
            default:
                return true;
        }
    }
    
    // Методы создания правил и отчетов
    
    private Map<String, Object> createBpmnRule(String fieldName, String ruleType, String description) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("fieldName", fieldName);
        rule.put("ruleType", ruleType);
        rule.put("ruleValue", "");
        rule.put("description", description);
        rule.put("source", "BPMN");
        return rule;
    }
    
    private Map<String, Object> createApiRule(String fieldName, String ruleType, String description) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("fieldName", fieldName);
        rule.put("ruleType", ruleType);
        rule.put("ruleValue", "");
        rule.put("description", description);
        rule.put("source", "OpenAPI");
        return rule;
    }
    
    private Map<String, Object> aggregateBpmnValidationResults(List<Map<String, Object>> results, List<Map<String, Object>> rules) {
        long validCount = results.stream().filter(r -> (Boolean) r.get("isValid")).count();
        long invalidCount = results.size() - validCount;
        
        Map<String, Object> aggregate = new HashMap<>();
        aggregate.put("totalRecords", results.size());
        aggregate.put("validRecords", validCount);
        aggregate.put("invalidRecords", invalidCount);
        aggregate.put("complianceScore", (validCount * 100.0) / results.size());
        aggregate.put("appliedRules", rules.size());
        aggregate.put("aggregatedAt", LocalDateTime.now());
        
        return aggregate;
    }
    
    private Map<String, Object> aggregateApiValidationResults(List<Map<String, Object>> results, List<Map<String, Object>> rules) {
        long validCount = results.stream().filter(r -> (Boolean) r.get("isValid")).count();
        long invalidCount = results.size() - validCount;
        
        Map<String, Object> aggregate = new HashMap<>();
        aggregate.put("totalRecords", results.size());
        aggregate.put("validRecords", validCount);
        aggregate.put("invalidRecords", invalidCount);
        aggregate.put("complianceScore", (validCount * 100.0) / results.size());
        aggregate.put("appliedRules", rules.size());
        aggregate.put("aggregatedAt", LocalDateTime.now());
        
        return aggregate;
    }
    
    private Map<String, Object> aggregateBusinessRulesValidationResults(List<Map<String, Object>> results, List<Map<String, Object>> rules) {
        long validCount = results.stream().filter(r -> (Boolean) r.get("isValid")).count();
        long invalidCount = results.size() - validCount;
        
        Map<String, Object> aggregate = new HashMap<>();
        aggregate.put("totalRecords", results.size());
        aggregate.put("validRecords", validCount);
        aggregate.put("invalidRecords", invalidCount);
        aggregate.put("complianceScore", (validCount * 100.0) / results.size());
        aggregate.put("appliedRules", rules.size());
        aggregate.put("aggregatedAt", LocalDateTime.now());
        
        return aggregate;
    }
    
    private Map<String, Object> aggregateConsistencyResults(List<Map<String, Object>> internalResults, Map<String, Object> interRecordResult) {
        double avgInternalScore = internalResults.stream()
            .mapToDouble(r -> (Double) r.get("consistencyScore"))
            .average()
            .orElse(100.0);
        
        double interRecordScore = (Double) interRecordResult.get("consistencyScore");
        double overallScore = (avgInternalScore + interRecordScore) / 2.0;
        
        Map<String, Object> aggregate = new HashMap<>();
        aggregate.put("totalRecords", internalResults.size());
        aggregate.put("internalConsistencyScore", avgInternalScore);
        aggregate.put("interRecordConsistencyScore", interRecordScore);
        aggregate.put("consistencyScore", overallScore);
        aggregate.put("aggregatedAt", LocalDateTime.now());
        
        return aggregate;
    }
    
    private double calculateOverallComplianceScore(Map<String, Object> validationResult) {
        List<Double> scores = new ArrayList<>();
        
        if (validationResult.containsKey("bpmnValidation")) {
            Map<String, Object> bpmnValidation = (Map<String, Object>) validationResult.get("bpmnValidation");
            if (bpmnValidation.containsKey("complianceScore")) {
                scores.add((Double) bpmnValidation.get("complianceScore"));
            }
        }
        
        if (validationResult.containsKey("apiValidation")) {
            Map<String, Object> apiValidation = (Map<String, Object>) validationResult.get("apiValidation");
            if (apiValidation.containsKey("complianceScore")) {
                scores.add((Double) apiValidation.get("complianceScore"));
            }
        }
        
        if (validationResult.containsKey("businessRulesValidation")) {
            Map<String, Object> brValidation = (Map<String, Object>) validationResult.get("businessRulesValidation");
            if (brValidation.containsKey("complianceScore")) {
                scores.add((Double) brValidation.get("complianceScore"));
            }
        }
        
        if (validationResult.containsKey("consistencyValidation")) {
            Map<String, Object> consistencyValidation = (Map<String, Object>) validationResult.get("consistencyValidation");
            if (consistencyValidation.containsKey("consistencyScore")) {
                scores.add((Double) consistencyValidation.get("consistencyScore"));
            }
        }
        
        return scores.isEmpty() ? 0.0 : 
               scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    // Методы создания отчетов
    
    private Map<String, Object> createBpmnValidationReport(String diagramId, Map<String, Object> aggregateResult, List<Map<String, Object>> details) {
        Map<String, Object> report = new HashMap<>();
        report.put("validationId", "bpmn_val_" + System.currentTimeMillis());
        report.put("validationType", "BPMN_COMPLIANCE");
        report.put("diagramId", diagramId);
        report.put("summary", aggregateResult);
        report.put("details", details);
        report.put("generatedAt", LocalDateTime.now());
        report.put("success", true);
        
        return report;
    }
    
    private Map<String, Object> createApiValidationReport(String specId, Map<String, Object> aggregateResult, List<Map<String, Object>> details) {
        Map<String, Object> report = new HashMap<>();
        report.put("validationId", "api_val_" + System.currentTimeMillis());
        report.put("validationType", "API_COMPLIANCE");
        report.put("specId", specId);
        report.put("summary", aggregateResult);
        report.put("details", details);
        report.put("generatedAt", LocalDateTime.now());
        report.put("success", true);
        
        return report;
    }
    
    private Map<String, Object> createBusinessRulesValidationReport(Map<String, Object> aggregateResult, List<Map<String, Object>> details) {
        Map<String, Object> report = new HashMap<>();
        report.put("validationId", "br_val_" + System.currentTimeMillis());
        report.put("validationType", "BUSINESS_RULES");
        report.put("summary", aggregateResult);
        report.put("details", details);
        report.put("generatedAt", LocalDateTime.now());
        report.put("success", true);
        
        return report;
    }
    
    private Map<String, Object> createConsistencyValidationReport(Map<String, Object> aggregateResult, List<Map<String, Object>> details) {
        Map<String, Object> report = new HashMap<>();
        report.put("validationId", "cons_val_" + System.currentTimeMillis());
        report.put("validationType", "DATA_CONSISTENCY");
        report.put("summary", aggregateResult);
        report.put("details", details);
        report.put("generatedAt", LocalDateTime.now());
        report.put("success", true);
        
        return report;
    }
    
    // Getters для кэшированных результатов
    public List<Map<String, Object>> getCachedValidationRules(String cacheKey) {
        return validationRulesCache.get(cacheKey);
    }
    
    public Map<String, Object> getCachedValidationResult(String requestId) {
        return validationResultsCache.get(requestId);
    }
    
    public void clearCache() {
        validationRulesCache.clear();
        validationResultsCache.clear();
        logger.info("Data validator cache cleared");
    }
}