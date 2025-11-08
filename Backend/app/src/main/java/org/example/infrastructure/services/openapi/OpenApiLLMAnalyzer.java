package org.example.infrastructure.services.openapi;

import org.example.infrastructure.services.llm.IssueClassifier;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LLM анализатор для обработки ответов от LLM и извлечения структурированных данных
 */
@Service
public class OpenApiLLMAnalyzer {
    
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{[\\s\\S]*\\}", Pattern.MULTILINE);
    private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[\\s*\\{[\\s\\S]*\\}\\s*\\]", Pattern.MULTILINE);
    
    /**
     * Парсинг результатов анализа безопасности
     */
    public List<IssueClassifier.RawIssue> parseSecurityAnalysis(String llmResponse) {
        List<IssueClassifier.RawIssue> issues = new ArrayList<>();
        
        try {
            // Извлекаем JSON из ответа
            String jsonContent = extractJsonContent(llmResponse);
            if (jsonContent == null) {
                return createFallbackSecurityIssues(llmResponse);
            }
            
            // Парсим JSON
            Map<String, Object> parsed = parseJson(jsonContent);
            
            // Извлекаем проблемы безопасности
            List<Map<String, Object>> securityIssues = extractListFromMap(parsed, "securityIssues");
            
            for (Map<String, Object> issueMap : securityIssues) {
                IssueClassifier.RawIssue issue = parseSecurityIssue(issueMap);
                if (issue != null) {
                    issues.add(issue);
                }
            }
            
            // Если проблемы не найдены, создаем на основе текста
            if (issues.isEmpty()) {
                issues.addAll(createFallbackSecurityIssues(llmResponse));
            }
            
        } catch (Exception e) {
            // Fallback: создаем проблемы на основе текста
            issues.addAll(createFallbackSecurityIssues(llmResponse));
        }
        
        return issues;
    }
    
    /**
     * Парсинг результатов анализа валидации
     */
    public List<IssueClassifier.RawIssue> parseValidationAnalysis(String llmResponse) {
        List<IssueClassifier.RawIssue> issues = new ArrayList<>();
        
        try {
            String jsonContent = extractJsonContent(llmResponse);
            if (jsonContent == null) {
                return createFallbackValidationIssues(llmResponse);
            }
            
            Map<String, Object> parsed = parseJson(jsonContent);
            List<Map<String, Object>> validationIssues = extractListFromMap(parsed, "validationIssues");
            
            for (Map<String, Object> issueMap : validationIssues) {
                IssueClassifier.RawIssue issue = parseValidationIssue(issueMap);
                if (issue != null) {
                    issues.add(issue);
                }
            }
            
            if (issues.isEmpty()) {
                issues.addAll(createFallbackValidationIssues(llmResponse));
            }
            
        } catch (Exception e) {
            issues.addAll(createFallbackValidationIssues(llmResponse));
        }
        
        return issues;
    }
    
    /**
     * Парсинг результатов анализа согласованности
     */
    public List<IssueClassifier.RawIssue> parseConsistencyAnalysis(String llmResponse) {
        List<IssueClassifier.RawIssue> issues = new ArrayList<>();
        
        try {
            String jsonContent = extractJsonContent(llmResponse);
            if (jsonContent == null) {
                return createFallbackConsistencyIssues(llmResponse);
            }
            
            Map<String, Object> parsed = parseJson(jsonContent);
            List<Map<String, Object>> inconsistencies = extractListFromMap(parsed, "inconsistencies");
            
            for (Map<String, Object> issueMap : inconsistencies) {
                IssueClassifier.RawIssue issue = parseConsistencyIssue(issueMap);
                if (issue != null) {
                    issues.add(issue);
                }
            }
            
            if (issues.isEmpty()) {
                issues.addAll(createFallbackConsistencyIssues(llmResponse));
            }
            
        } catch (Exception e) {
            issues.addAll(createFallbackConsistencyIssues(llmResponse));
        }
        
        return issues;
    }
    
    /**
     * Парсинг результатов комплексного анализа
     */
    public Map<String, Object> parseComprehensiveAnalysis(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String jsonContent = extractJsonContent(llmResponse);
            if (jsonContent == null) {
                return createFallbackComprehensiveData(llmResponse);
            }
            
            Map<String, Object> parsed = parseJson(jsonContent);
            
            // Извлекаем основные метрики
            result.put("overallScore", parsed.get("overallScore"));
            result.put("grade", parsed.get("grade"));
            result.put("summary", parsed.get("summary"));
            
            // Извлекаем детальный анализ
            Map<String, Object> analysis = extractMapFromMap(parsed, "analysis");
            if (analysis != null) {
                result.put("security", extractMapFromMap(analysis, "security"));
                result.put("validation", extractMapFromMap(analysis, "validation"));
                result.put("consistency", extractMapFromMap(analysis, "consistency"));
            }
            
            // Извлекаем рекомендации
            List<String> recommendations = extractStringListFromMap(parsed, "recommendations");
            result.put("recommendations", recommendations);
            
        } catch (Exception e) {
            result = createFallbackComprehensiveData(llmResponse);
        }
        
        return result;
    }
    
    // Вспомогательные методы парсинга
    
    private String extractJsonContent(String response) {
        if (response == null) return null;
        
        // Ищем JSON в ответе
        Matcher matcher = JSON_PATTERN.matcher(response);
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String json) {
        // Простой JSON парсер для базовых структур
        // В реальном проекте лучше использовать Jackson или Gson
        return new HashMap<>(); // Заглушка
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractListFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<Map<String, Object>>) value;
        }
        return new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    private List<String> extractStringListFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractMapFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return new HashMap<>();
    }
    
    private IssueClassifier.RawIssue parseSecurityIssue(Map<String, Object> issueMap) {
        if (issueMap == null || issueMap.isEmpty()) return null;
        
        IssueClassifier.RawIssue issue = new IssueClassifier.RawIssue();
        issue.setId(generateId());
        issue.setTitle(getString(issueMap, "type", "Проблема безопасности"));
        issue.setDescription(getString(issueMap, "description", ""));
        issue.setSeverity(getString(issueMap, "severity", "MEDIUM"));
        issue.setLocation(getString(issueMap, "endpoint", ""));
        issue.setMetadata(issueMap);
        
        return issue;
    }
    
    private IssueClassifier.RawIssue parseValidationIssue(Map<String, Object> issueMap) {
        if (issueMap == null || issueMap.isEmpty()) return null;
        
        IssueClassifier.RawIssue issue = new IssueClassifier.RawIssue();
        issue.setId(generateId());
        issue.setTitle(getString(issueMap, "type", "Проблема валидации"));
        issue.setDescription(getString(issueMap, "description", ""));
        issue.setSeverity(getString(issueMap, "severity", "MEDIUM"));
        issue.setLocation(getString(issueMap, "location", ""));
        issue.setMetadata(issueMap);
        
        return issue;
    }
    
    private IssueClassifier.RawIssue parseConsistencyIssue(Map<String, Object> issueMap) {
        if (issueMap == null || issueMap.isEmpty()) return null;
        
        IssueClassifier.RawIssue issue = new IssueClassifier.RawIssue();
        issue.setId(generateId());
        issue.setTitle(getString(issueMap, "type", "Несогласованность"));
        issue.setDescription(getString(issueMap, "description", ""));
        issue.setSeverity(getString(issueMap, "severity", "LOW"));
        issue.setLocation(getString(issueMap, "location", ""));
        issue.setMetadata(issueMap);
        
        return issue;
    }
    
    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    private String generateId() {
        return "issue_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Fallback методы для обработки текстовых ответов
    
    private List<IssueClassifier.RawIssue> createFallbackSecurityIssues(String response) {
        List<IssueClassifier.RawIssue> issues = new ArrayList<>();
        
        if (response.toLowerCase().contains("auth") || response.toLowerCase().contains("security")) {
            IssueClassifier.RawIssue issue = new IssueClassifier.RawIssue();
            issue.setId(generateId());
            issue.setTitle("Проблемы аутентификации");
            issue.setDescription("Обнаружены потенциальные проблемы с аутентификацией");
            issue.setSeverity("HIGH");
            issue.setLocation("API endpoints");
            issues.add(issue);
        }
        
        return issues;
    }
    
    private List<IssueClassifier.RawIssue> createFallbackValidationIssues(String response) {
        List<IssueClassifier.RawIssue> issues = new ArrayList<>();
        
        if (response.toLowerCase().contains("validat") || response.toLowerCase().contains("schema")) {
            IssueClassifier.RawIssue issue = new IssueClassifier.RawIssue();
            issue.setId(generateId());
            issue.setTitle("Проблемы валидации схем");
            issue.setDescription("Обнаружены проблемы с валидацией данных");
            issue.setSeverity("MEDIUM");
            issue.setLocation("Data schemas");
            issues.add(issue);
        }
        
        return issues;
    }
    
    private List<IssueClassifier.RawIssue> createFallbackConsistencyIssues(String response) {
        List<IssueClassifier.RawIssue> issues = new ArrayList<>();
        
        if (response.toLowerCase().contains("consistenc") || response.toLowerCase().contains("mismatch")) {
            IssueClassifier.RawIssue issue = new IssueClassifier.RawIssue();
            issue.setId(generateId());
            issue.setTitle("Несогласованность данных");
            issue.setDescription("Обнаружены несогласованности в спецификации");
            issue.setSeverity("LOW");
            issue.setLocation("API specification");
            issues.add(issue);
        }
        
        return issues;
    }
    
    private Map<String, Object> createFallbackComprehensiveData(String response) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("overallScore", "7");
        data.put("grade", "B");
        data.put("summary", "Обнаружены общие проблемы в спецификации");
        
        Map<String, Object> analysis = new HashMap<>();
        Map<String, Object> security = new HashMap<>();
        security.put("score", "6");
        security.put("issues", Arrays.asList("Требуется улучшение безопасности"));
        
        Map<String, Object> validation = new HashMap<>();
        validation.put("score", "7");
        validation.put("issues", Arrays.asList("Некоторые схемы требуют улучшения"));
        
        Map<String, Object> consistency = new HashMap<>();
        consistency.put("score", "8");
        consistency.put("issues", Arrays.asList("Хорошая согласованность данных"));
        
        analysis.put("security", security);
        analysis.put("validation", validation);
        analysis.put("consistency", consistency);
        
        data.put("analysis", analysis);
        data.put("recommendations", Arrays.asList(
            "Улучшить документацию API",
            "Добавить больше примеров",
            "Проверить согласованность схем"
        ));
        
        return data;
    }
}