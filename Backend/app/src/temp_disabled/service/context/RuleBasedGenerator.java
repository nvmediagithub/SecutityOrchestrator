package org.example.application.service.context;

import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.application.service.testdata.IntelligentDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * RuleBasedGenerator - генератор на основе бизнес-правил
 * Извлекает и применяет правила из BPMN процессов и OpenAPI спецификаций
 */
@Service
public class RuleBasedGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(RuleBasedGenerator.class);
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private OpenApiAnalysisService openApiAnalysisService;
    
    @Autowired
    private IntelligentDataGenerator intelligentDataGenerator;
    
    @Autowired
    private Executor ruleBasedExecutor;
    
    // Кэш для правил
    private final Map<String, List<Map<String, Object>>> rulesCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> generationCache = new ConcurrentHashMap<>();
    
    // Предопределенные шаблоны валидации
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
    
    /**
     * Генерирует данные на основе извлеченных правил
     */
    public Map<String, Object> generateDataWithRules(String requestId, 
                                                    List<String> diagramIds, 
                                                    List<String> specIds,
                                                    int recordCount) {
        logger.info("Starting rule-based generation for request: {}, records: {}", requestId, recordCount);
        
        try {
            // 1. Извлекаем правила из BPMN процессов
            List<Map<String, Object>> bpmnRules = extractBpmnRules(diagramIds);
            
            // 2. Извлекаем правила из OpenAPI спецификаций
            List<Map<String, Object>> apiRules = extractApiRules(specIds);
            
            // 3. Объединяем все правила
            List<Map<String, Object>> allRules = new ArrayList<>();
            allRules.addAll(bpmnRules);
            allRules.addAll(apiRules);
            
            // 4. Кэшируем правила
            rulesCache.put(requestId, allRules);
            
            // 5. Генерируем данные с применением правил
            List<Map<String, Object>> generatedData = generateDataByRules(allRules, recordCount);
            
            // 6. Создаем результат
            Map<String, Object> result = createGenerationResult(requestId, generatedData, allRules);
            
            // 7. Кэшируем результат
            generationCache.put(requestId, result);
            
            logger.info("Rule-based generation completed for request: {}, generated {} records with {} rules", 
                requestId, generatedData.size(), allRules.size());
            
            return result;
            
        } catch (Exception e) {
            logger.error("Rule-based generation failed for request: {}", requestId, e);
            throw new RuntimeException("Rule-based generation failed", e);
        }
    }
    
    /**
     * Извлекает бизнес-правила из BPMN процессов
     */
    private List<Map<String, Object>> extractBpmnRules(List<String> diagramIds) {
        logger.debug("Extracting BPMN rules from {} diagrams", diagramIds.size());
        
        List<Map<String, Object>> bpmnRules = new ArrayList<>();
        
        for (String diagramId : diagramIds) {
            try {
                // Симулируем извлечение правил из BPMN
                Map<String, Object> processRules = new HashMap<>();
                processRules.put("source", "BPMN");
                processRules.put("diagramId", diagramId);
                processRules.put("ruleType", "Business Logic");
                processRules.put("rules", Arrays.asList(
                    createValidationRule("processId", "UUID", "Process ID must be valid UUID"),
                    createValidationRule("status", "ENUM", "ACTIVE,INACTIVE,PENDING"),
                    createValidationRule("priority", "RANGE", "1-5"),
                    createBusinessRule("taskSequence", "DEPENDENCY", "Tasks must follow defined sequence"),
                    createBusinessRule("approvalWorkflow", "CONDITION", "Manager approval required for high-priority items")
                ));
                
                bpmnRules.add(processRules);
                
            } catch (Exception e) {
                logger.warn("Failed to extract rules from BPMN diagram: {}", diagramId, e);
            }
        }
        
        return bpmnRules;
    }
    
    /**
     * Извлекает правила валидации из OpenAPI спецификаций
     */
    private List<Map<String, Object>> extractApiRules(List<String> specIds) {
        logger.debug("Extracting API rules from {} specifications", specIds.size());
        
        List<Map<String, Object>> apiRules = new ArrayList<>();
        
        for (String specId : specIds) {
            try {
                // Симулируем извлечение правил из OpenAPI
                Map<String, Object> apiValidationRules = new HashMap<>();
                apiValidationRules.put("source", "OpenAPI");
                apiValidationRules.put("specId", specId);
                apiValidationRules.put("ruleType", "Data Validation");
                apiValidationRules.put("rules", Arrays.asList(
                    createValidationRule("email", "EMAIL", "Must be valid email address"),
                    createValidationRule("phone", "PHONE", "Must be valid phone number"),
                    createValidationRule("age", "RANGE", "18-100"),
                    createValidationRule("amount", "DECIMAL", "Must be positive decimal"),
                    createValidationRule("id", "UUID", "Must be valid UUID"),
                    createValidationRule("name", "STRING", "Length 2-50 characters"),
                    createValidationRule("createdAt", "DATETIME", "Must be valid ISO datetime")
                ));
                
                apiRules.add(apiValidationRules);
                
            } catch (Exception e) {
                logger.warn("Failed to extract rules from API spec: {}", specId, e);
            }
        }
        
        return apiRules;
    }
    
    /**
     * Генерирует данные на основе набора правил
     */
    private List<Map<String, Object>> generateDataByRules(List<Map<String, Object>> rules, int recordCount) {
        logger.debug("Generating {} records with {} rule sets", recordCount, rules.size());
        
        List<Map<String, Object>> generatedData = new ArrayList<>();
        
        for (int i = 0; i < recordCount; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", UUID.randomUUID().toString());
            record.put("recordNumber", i + 1);
            record.put("generatedAt", LocalDateTime.now());
            
            // Применяем правила к каждой записи
            for (Map<String, Object> ruleSet : rules) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rulesList = (List<Map<String, Object>>) ruleSet.get("rules");
                
                for (Map<String, Object> rule : rulesList) {
                    String fieldName = (String) rule.get("fieldName");
                    String ruleType = (String) rule.get("ruleType");
                    String ruleValue = (String) rule.get("ruleValue");
                    
                    // Генерируем значение на основе правила
                    Object generatedValue = generateValueByRule(fieldName, ruleType, ruleValue);
                    record.put(fieldName, generatedValue);
                }
            }
            
            generatedData.add(record);
        }
        
        return generatedData;
    }
    
    /**
     * Генерирует значение на основе правила
     */
    private Object generateValueByRule(String fieldName, String ruleType, String ruleValue) {
        switch (ruleType.toUpperCase()) {
            case "EMAIL":
                return fieldName + "." + System.currentTimeMillis() + "@example.com";
                
            case "PHONE":
                return "+1234567890";
                
            case "UUID":
                return UUID.randomUUID().toString();
                
            case "STRING":
                int maxLength = 50;
                if (ruleValue.contains("-")) {
                    String[] parts = ruleValue.split("-");
                    maxLength = Integer.parseInt(parts[1]);
                }
                return generateRandomString(maxLength);
                
            case "INTEGER":
            case "RANGE":
                if (ruleValue.contains("-")) {
                    String[] parts = ruleValue.split("-");
                    int min = Integer.parseInt(parts[0]);
                    int max = Integer.parseInt(parts[1]);
                    return min + (int) (Math.random() * (max - min + 1));
                }
                return (int) (Math.random() * 100);
                
            case "DECIMAL":
                return Math.round(Math.random() * 1000 * 100.0) / 100.0;
                
            case "DATETIME":
                return LocalDateTime.now().toString();
                
            case "ENUM":
                String[] values = ruleValue.split(",");
                return values[(int) (Math.random() * values.length)];
                
            case "BOOLEAN":
                return Math.random() > 0.5;
                
            default:
                return "generated_" + fieldName;
        }
    }
    
    /**
     * Создает правило валидации
     */
    private Map<String, Object> createValidationRule(String fieldName, String ruleType, String description) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("fieldName", fieldName);
        rule.put("ruleType", ruleType);
        rule.put("ruleValue", "");
        rule.put("description", description);
        rule.put("isValidation", true);
        return rule;
    }
    
    /**
     * Создает бизнес-правило
     */
    private Map<String, Object> createBusinessRule(String fieldName, String ruleType, String description) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("fieldName", fieldName);
        rule.put("ruleType", ruleType);
        rule.put("ruleValue", "");
        rule.put("description", description);
        rule.put("isBusiness", true);
        return rule;
    }
    
    /**
     * Валидирует запись на основе правил
     */
    public Map<String, Object> validateRecord(Map<String, Object> record, List<Map<String, Object>> rules) {
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true);
        validationResult.put("validationErrors", new ArrayList<>());
        validationResult.put("validatedAt", LocalDateTime.now());
        
        for (Map<String, Object> ruleSet : rules) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rulesList = (List<Map<String, Object>>) ruleSet.get("rules");
            
            for (Map<String, Object> rule : rulesList) {
                String fieldName = (String) rule.get("fieldName");
                String ruleType = (String) rule.get("ruleType");
                String description = (String) rule.get("description");
                
                Object fieldValue = record.get(fieldName);
                
                if (!isValidByRule(fieldValue, ruleType)) {
                    validationResult.put("isValid", false);
                    ((List<String>) validationResult.get("validationErrors"))
                        .add(fieldName + ": " + description);
                }
            }
        }
        
        return validationResult;
    }
    
    /**
     * Проверяет значение на соответствие правилу
     */
    private boolean isValidByRule(Object value, String ruleType) {
        if (value == null) return false;
        
        switch (ruleType.toUpperCase()) {
            case "EMAIL":
                return value instanceof String && EMAIL_PATTERN.matcher((String) value).matches();
                
            case "PHONE":
                return value instanceof String && PHONE_PATTERN.matcher((String) value).matches();
                
            case "UUID":
                return value instanceof String && UUID_PATTERN.matcher((String) value).matches();
                
            case "STRING":
                return value instanceof String && ((String) value).length() >= 2;
                
            case "INTEGER":
            case "RANGE":
                return value instanceof Integer;
                
            case "DECIMAL":
                return value instanceof Double || value instanceof Float;
                
            case "BOOLEAN":
                return value instanceof Boolean;
                
            default:
                return true;
        }
    }
    
    /**
     * Создает результат генерации
     */
    private Map<String, Object> createGenerationResult(String requestId, 
                                                      List<Map<String, Object>> generatedData, 
                                                      List<Map<String, Object>> rules) {
        Map<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("generatedData", generatedData);
        result.put("totalRecords", generatedData.size());
        result.put("appliedRules", rules);
        result.put("generatedAt", LocalDateTime.now());
        result.put("success", true);
        result.put("ruleCount", rules.size());
        
        return result;
    }
    
    /**
     * Получает кэшированные правила
     */
    public List<Map<String, Object>> getCachedRules(String requestId) {
        return rulesCache.get(requestId);
    }
    
    /**
     * Получает кэшированный результат генерации
     */
    public Map<String, Object> getCachedGeneration(String requestId) {
        return generationCache.get(requestId);
    }
    
    /**
     * Очищает кэш
     */
    public void clearCache() {
        rulesCache.clear();
        generationCache.clear();
        logger.info("Rule-based generator cache cleared");
    }
    
    /**
     * Генерирует случайную строку
     */
    private String generateRandomString(int maxLength) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        int length = Math.min(maxLength, 20);
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        
        return sb.toString();
    }
}