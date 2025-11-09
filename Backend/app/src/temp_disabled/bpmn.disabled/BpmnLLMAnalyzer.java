package org.example.infrastructure.services.bpmn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Анализатор LLM ответов для BPMN анализа
 * Обрабатывает JSON ответы от LLM и преобразует их в доменные объекты
 */
@Service
public class BpmnLLMAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(BpmnLLMAnalyzer.class);
    
    private final ObjectMapper objectMapper;
    
    // Паттерны для извлечения JSON из текста
    private static final Pattern JSON_PATTERN = Pattern.compile(
        "\\{[\\s\\S]*\\}", Pattern.MULTILINE
    );
    
    // Паттерны для извлечения массивов
    private static final Pattern ARRAY_PATTERN = Pattern.compile(
        "\\[[\\s\\S]*\\]", Pattern.MULTILINE
    );
    
    public BpmnLLMAnalyzer() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Парсит результат анализа структуры из LLM ответа
     */
    public List<BpmnIssueClassifier.RawBpmnIssue> parseStructureAnalysis(String llmResponse) {
        try {
            JsonNode jsonResponse = extractJsonFromResponse(llmResponse);
            if (jsonResponse == null || !jsonResponse.has("structureIssues")) {
                logger.warn("No structureIssues found in LLM response");
                return new ArrayList<>();
            }
            
            List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
            JsonNode structureIssues = jsonResponse.get("structureIssues");
            
            if (structureIssues.isArray()) {
                for (JsonNode issueNode : structureIssues) {
                    BpmnIssueClassifier.RawBpmnIssue issue = parseStructureIssue(issueNode);
                    if (issue != null) {
                        issues.add(issue);
                    }
                }
            }
            
            return issues;
            
        } catch (Exception e) {
            logger.error("Error parsing structure analysis response", e);
            return createFallbackStructureIssues(llmResponse);
        }
    }
    
    /**
     * Парсит результат анализа безопасности из LLM ответа
     */
    public List<BpmnIssueClassifier.RawBpmnIssue> parseSecurityAnalysis(String llmResponse) {
        try {
            JsonNode jsonResponse = extractJsonFromResponse(llmResponse);
            if (jsonResponse == null || !jsonResponse.has("securityIssues")) {
                logger.warn("No securityIssues found in LLM response");
                return new ArrayList<>();
            }
            
            List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
            JsonNode securityIssues = jsonResponse.get("securityIssues");
            
            if (securityIssues.isArray()) {
                for (JsonNode issueNode : securityIssues) {
                    BpmnIssueClassifier.RawBpmnIssue issue = parseSecurityIssue(issueNode);
                    if (issue != null) {
                        issues.add(issue);
                    }
                }
            }
            
            return issues;
            
        } catch (Exception e) {
            logger.error("Error parsing security analysis response", e);
            return createFallbackSecurityIssues(llmResponse);
        }
    }
    
    /**
     * Парсит результат анализа производительности из LLM ответа
     */
    public List<BpmnIssueClassifier.RawBpmnIssue> parsePerformanceAnalysis(String llmResponse) {
        try {
            JsonNode jsonResponse = extractJsonFromResponse(llmResponse);
            if (jsonResponse == null || !jsonResponse.has("performanceIssues")) {
                logger.warn("No performanceIssues found in LLM response");
                return new ArrayList<>();
            }
            
            List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
            JsonNode performanceIssues = jsonResponse.get("performanceIssues");
            
            if (performanceIssues.isArray()) {
                for (JsonNode issueNode : performanceIssues) {
                    BpmnIssueClassifier.RawBpmnIssue issue = parsePerformanceIssue(issueNode);
                    if (issue != null) {
                        issues.add(issue);
                    }
                }
            }
            
            return issues;
            
        } catch (Exception e) {
            logger.error("Error parsing performance analysis response", e);
            return createFallbackPerformanceIssues(llmResponse);
        }
    }
    
    /**
     * Парсит результат комплексного анализа
     */
    public Map<String, Object> parseComprehensiveAnalysis(String llmResponse) {
        try {
            JsonNode jsonResponse = extractJsonFromResponse(llmResponse);
            if (jsonResponse == null) {
                return createFallbackComprehensiveData(llmResponse);
            }
            
            Map<String, Object> result = new HashMap<>();
            
            // Извлекаем общие метрики
            if (jsonResponse.has("overallScore")) {
                result.put("overallScore", jsonResponse.get("overallScore").asText());
            }
            if (jsonResponse.has("grade")) {
                result.put("grade", jsonResponse.get("grade").asText());
            }
            if (jsonResponse.has("summary")) {
                result.put("summary", jsonResponse.get("summary").asText());
            }
            
            // Извлекаем детальный анализ
            if (jsonResponse.has("analysis")) {
                JsonNode analysis = jsonResponse.get("analysis");
                Map<String, Object> analysisMap = new HashMap<>();
                
                if (analysis.has("structure")) {
                    analysisMap.put("structure", extractAnalysisSection(analysis.get("structure")));
                }
                if (analysis.has("security")) {
                    analysisMap.put("security", extractAnalysisSection(analysis.get("security")));
                }
                if (analysis.has("performance")) {
                    analysisMap.put("performance", extractAnalysisSection(analysis.get("performance")));
                }
                if (analysis.has("logic")) {
                    analysisMap.put("logic", extractAnalysisSection(analysis.get("logic")));
                }
                
                result.put("analysis", analysisMap);
            }
            
            // Извлекаем рекомендации
            if (jsonResponse.has("recommendations")) {
                List<String> recommendations = extractStringArray(jsonResponse.get("recommendations"));
                result.put("recommendations", recommendations);
            }
            
            // Извлекаем статус соответствия
            if (jsonResponse.has("complianceStatus")) {
                result.put("complianceStatus", jsonResponse.get("complianceStatus").asText());
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error parsing comprehensive analysis response", e);
            return createFallbackComprehensiveData(llmResponse);
        }
    }
    
    /**
     * Парсит результат анализа логики из LLM ответа
     */
    public List<BpmnIssueClassifier.RawBpmnIssue> parseLogicAnalysis(String llmResponse) {
        try {
            JsonNode jsonResponse = extractJsonFromResponse(llmResponse);
            if (jsonResponse == null || !jsonResponse.has("logicIssues")) {
                logger.warn("No logicIssues found in LLM response");
                return new ArrayList<>();
            }
            
            List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
            JsonNode logicIssues = jsonResponse.get("logicIssues");
            
            if (logicIssues.isArray()) {
                for (JsonNode issueNode : logicIssues) {
                    BpmnIssueClassifier.RawBpmnIssue issue = parseLogicIssue(issueNode);
                    if (issue != null) {
                        issues.add(issue);
                    }
                }
            }
            
            return issues;
            
        } catch (Exception e) {
            logger.error("Error parsing logic analysis response", e);
            return createFallbackLogicIssues(llmResponse);
        }
    }
    
    /**
     * Парсит результат валидации BPMN
     */
    public List<BpmnIssueClassifier.RawBpmnIssue> parseValidationAnalysis(String llmResponse) {
        try {
            JsonNode jsonResponse = extractJsonFromResponse(llmResponse);
            if (jsonResponse == null || !jsonResponse.has("validationIssues")) {
                logger.warn("No validationIssues found in LLM response");
                return new ArrayList<>();
            }
            
            List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
            JsonNode validationIssues = jsonResponse.get("validationIssues");
            
            if (validationIssues.isArray()) {
                for (JsonNode issueNode : validationIssues) {
                    BpmnIssueClassifier.RawBpmnIssue issue = parseValidationIssue(issueNode);
                    if (issue != null) {
                        issues.add(issue);
                    }
                }
            }
            
            return issues;
            
        } catch (Exception e) {
            logger.error("Error parsing validation analysis response", e);
            return createFallbackValidationIssues(llmResponse);
        }
    }
    
    // Вспомогательные методы парсинга
    
    private BpmnIssueClassifier.RawBpmnIssue parseStructureIssue(JsonNode issueNode) {
        try {
            BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
            issue.setId(generateIssueId());
            issue.setType("STRUCTURE");
            
            if (issueNode.has("type")) {
                issue.setTitle(issueNode.get("type").asText());
            }
            if (issueNode.has("severity")) {
                issue.setSeverity(issueNode.get("severity").asText());
            }
            if (issueNode.has("element")) {
                issue.setElementId(issueNode.get("element").asText());
            }
            if (issueNode.has("description")) {
                issue.setDescription(issueNode.get("description").asText());
            }
            if (issueNode.has("recommendation")) {
                issue.setRecommendation(issueNode.get("recommendation").asText());
            }
            
            return issue;
            
        } catch (Exception e) {
            logger.error("Error parsing structure issue", e);
            return null;
        }
    }
    
    private BpmnIssueClassifier.RawBpmnIssue parseSecurityIssue(JsonNode issueNode) {
        try {
            BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
            issue.setId(generateIssueId());
            issue.setType("SECURITY");
            
            if (issueNode.has("type")) {
                issue.setTitle(issueNode.get("type").asText());
            }
            if (issueNode.has("severity")) {
                issue.setSeverity(issueNode.get("severity").asText());
            }
            if (issueNode.has("element")) {
                issue.setElementId(issueNode.get("element").asText());
            }
            if (issueNode.has("description")) {
                issue.setDescription(issueNode.get("description").asText());
            }
            if (issueNode.has("recommendation")) {
                issue.setRecommendation(issueNode.get("recommendation").asText());
            }
            if (issueNode.has("cweId")) {
                issue.setMetadata("cweId", issueNode.get("cweId").asText());
            }
            
            return issue;
            
        } catch (Exception e) {
            logger.error("Error parsing security issue", e);
            return null;
        }
    }
    
    private BpmnIssueClassifier.RawBpmnIssue parsePerformanceIssue(JsonNode issueNode) {
        try {
            BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
            issue.setId(generateIssueId());
            issue.setType("PERFORMANCE");
            
            if (issueNode.has("type")) {
                issue.setTitle(issueNode.get("type").asText());
            }
            if (issueNode.has("severity")) {
                issue.setSeverity(issueNode.get("severity").asText());
            }
            if (issueNode.has("element")) {
                issue.setElementId(issueNode.get("element").asText());
            }
            if (issueNode.has("description")) {
                issue.setDescription(issueNode.get("description").asText());
            }
            if (issueNode.has("recommendation")) {
                issue.setRecommendation(issueNode.get("recommendation").asText());
            }
            if (issueNode.has("estimatedImpact")) {
                issue.setMetadata("impact", issueNode.get("estimatedImpact").asText());
            }
            
            return issue;
            
        } catch (Exception e) {
            logger.error("Error parsing performance issue", e);
            return null;
        }
    }
    
    private BpmnIssueClassifier.RawBpmnIssue parseLogicIssue(JsonNode issueNode) {
        try {
            BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
            issue.setId(generateIssueId());
            issue.setType("LOGIC_ERROR");
            
            if (issueNode.has("type")) {
                issue.setTitle(issueNode.get("type").asText());
            }
            if (issueNode.has("severity")) {
                issue.setSeverity(issueNode.get("severity").asText());
            }
            if (issueNode.has("element")) {
                issue.setElementId(issueNode.get("element").asText());
            }
            if (issueNode.has("description")) {
                issue.setDescription(issueNode.get("description").asText());
            }
            if (issueNode.has("recommendation")) {
                issue.setRecommendation(issueNode.get("recommendation").asText());
            }
            
            return issue;
            
        } catch (Exception e) {
            logger.error("Error parsing logic issue", e);
            return null;
        }
    }
    
    private BpmnIssueClassifier.RawBpmnIssue parseValidationIssue(JsonNode issueNode) {
        try {
            BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
            issue.setId(generateIssueId());
            issue.setType("VALIDATION");
            
            if (issueNode.has("type")) {
                issue.setTitle(issueNode.get("type").asText());
            }
            if (issueNode.has("severity")) {
                issue.setSeverity(issueNode.get("severity").asText());
            }
            if (issueNode.has("element")) {
                issue.setElementId(issueNode.get("element").asText());
            }
            if (issueNode.has("description")) {
                issue.setDescription(issueNode.get("description").asText());
            }
            if (issueNode.has("fix")) {
                issue.setRecommendation(issueNode.get("fix").asText());
            }
            
            return issue;
            
        } catch (Exception e) {
            logger.error("Error parsing validation issue", e);
            return null;
        }
    }
    
    private JsonNode extractJsonFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return null;
        }
        
        // Пытаемся найти JSON в ответе
        Matcher jsonMatcher = JSON_PATTERN.matcher(response);
        if (jsonMatcher.find()) {
            String jsonString = jsonMatcher.group();
            try {
                return objectMapper.readTree(jsonString);
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse JSON from response: {}", jsonString);
            }
        }
        
        // Если не удалось найти полный JSON, попробуем найти массив
        Matcher arrayMatcher = ARRAY_PATTERN.matcher(response);
        if (arrayMatcher.find()) {
            String arrayString = arrayMatcher.group();
            try {
                return objectMapper.readTree("{\"items\": " + arrayString + "}");
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse array from response: {}", arrayString);
            }
        }
        
        logger.warn("No JSON found in LLM response: {}", response.substring(0, Math.min(200, response.length())));
        return null;
    }
    
    private Map<String, Object> extractAnalysisSection(JsonNode section) {
        Map<String, Object> result = new HashMap<>();
        
        if (section.has("score")) {
            result.put("score", section.get("score").asText());
        }
        if (section.has("issues")) {
            result.put("issues", extractStringArray(section.get("issues")));
        }
        
        return result;
    }
    
    private List<String> extractStringArray(JsonNode node) {
        List<String> result = new ArrayList<>();
        
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.isTextual()) {
                    result.add(item.asText());
                }
            }
        }
        
        return result;
    }
    
    private String generateIssueId() {
        return "bpmn_issue_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Fallback методы для обработки некорректных ответов
    
    private List<BpmnIssueClassifier.RawBpmnIssue> createFallbackStructureIssues(String response) {
        List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
        
        BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
        issue.setId(generateIssueId());
        issue.setType("STRUCTURE");
        issue.setTitle("Не удалось проанализировать структуру");
        issue.setSeverity("MEDIUM");
        issue.setDescription("LLM вернул некорректный формат ответа для структурного анализа");
        issue.setRecommendation("Проверить корректность BPMN диаграммы и повторить анализ");
        
        issues.add(issue);
        return issues;
    }
    
    private List<BpmnIssueClassifier.RawBpmnIssue> createFallbackSecurityIssues(String response) {
        List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
        
        BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
        issue.setId(generateIssueId());
        issue.setType("SECURITY");
        issue.setTitle("Не удалось проанализировать безопасность");
        issue.setSeverity("HIGH");
        issue.setDescription("LLM вернул некорректный формат ответа для анализа безопасности");
        issue.setRecommendation("Проверить LLM модель и повторить анализ безопасности");
        
        issues.add(issue);
        return issues;
    }
    
    private List<BpmnIssueClassifier.RawBpmnIssue> createFallbackPerformanceIssues(String response) {
        List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
        
        BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
        issue.setId(generateIssueId());
        issue.setType("PERFORMANCE");
        issue.setTitle("Не удалось проанализировать производительность");
        issue.setSeverity("MEDIUM");
        issue.setDescription("LLM вернул некорректный формат ответа для анализа производительности");
        issue.setRecommendation("Проверить сложность процесса и упростить структуру");
        
        issues.add(issue);
        return issues;
    }
    
    private List<BpmnIssueClassifier.RawBpmnIssue> createFallbackLogicIssues(String response) {
        List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
        
        BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
        issue.setId(generateIssueId());
        issue.setType("LOGIC_ERROR");
        issue.setTitle("Не удалось проанализировать логику");
        issue.setSeverity("HIGH");
        issue.setDescription("LLM вернул некорректный формат ответа для логического анализа");
        issue.setRecommendation("Проверить логику процесса и бизнес-правила");
        
        issues.add(issue);
        return issues;
    }
    
    private List<BpmnIssueClassifier.RawBpmnIssue> createFallbackValidationIssues(String response) {
        List<BpmnIssueClassifier.RawBpmnIssue> issues = new ArrayList<>();
        
        BpmnIssueClassifier.RawBpmnIssue issue = new BpmnIssueClassifier.RawBpmnIssue();
        issue.setId(generateIssueId());
        issue.setType("VALIDATION");
        issue.setTitle("Не удалось выполнить валидацию");
        issue.setSeverity("MEDIUM");
        issue.setDescription("LLM вернул некорректный формат ответа для валидации");
        issue.setRecommendation("Проверить соответствие BPMN 2.0 стандарту");
        
        issues.add(issue);
        return issues;
    }
    
    private Map<String, Object> createFallbackComprehensiveData(String response) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("overallScore", "5");
        result.put("grade", "C");
        result.put("summary", "Анализ не удался - LLM вернул некорректный формат ответа");
        
        Map<String, Object> analysis = new HashMap<>();
        Map<String, Object> structure = new HashMap<>();
        structure.put("score", "5");
        structure.put("issues", List.of("Не удалось проанализировать структуру"));
        analysis.put("structure", structure);
        
        Map<String, Object> security = new HashMap<>();
        security.put("score", "5");
        security.put("issues", List.of("Не удалось проанализировать безопасность"));
        analysis.put("security", security);
        
        Map<String, Object> performance = new HashMap<>();
        performance.put("score", "5");
        performance.put("issues", List.of("Не удалось проанализировать производительность"));
        analysis.put("performance", performance);
        
        result.put("analysis", analysis);
        result.put("recommendations", List.of("Проверить корректность BPMN диаграммы", "Повторить анализ"));
        
        return result;
    }
}