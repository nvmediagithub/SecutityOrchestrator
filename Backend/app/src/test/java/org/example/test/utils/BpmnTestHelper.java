package org.example.test.utils;

import java.util.*;

/**
 * Вспомогательные методы для тестирования BPMN компонентов
 */
public class BpmnTestHelper {
    
    // ==========================================
    // Вспомогательные методы для тестирования
    // ==========================================
    
    /**
     * Создает простую BPMN диаграмму в виде XML строки
     */
    public static String createSimpleBpmnXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\">\n" +
               "    <process id=\"TestProcess\">\n" +
               "        <startEvent id=\"start1\"/>\n" +
               "        <task id=\"task1\"/>\n" +
               "        <endEvent id=\"end1\"/>\n" +
               "        <sequenceFlow id=\"flow1\" sourceRef=\"start1\" targetRef=\"task1\"/>\n" +
               "        <sequenceFlow id=\"flow2\" sourceRef=\"task1\" targetRef=\"end1\"/>\n" +
               "    </process>\n" +
               "</definitions>";
    }
    
    /**
     * Создает проблемную BPMN диаграмму
     */
    public static String createProblematicBpmnXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<definitions>\n" +
               "    <process id=\"ProblematicProcess\">\n" +
               "        <startEvent id=\"start1\"/>\n" +
               "        <task id=\"task1\"/>\n" +
               "        <!-- Пропущены потоки и конец -->\n" +
               "    </process>\n" +
               "</definitions>";
    }
    
    /**
     * Создает большую BPMN диаграмму для тестирования производительности
     */
    public static String createLargeBpmnXml(int elementCount) {
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        content.append("<definitions>\n");
        content.append("    <process id=\"LargeProcess\">\n");
        content.append("        <startEvent id=\"start1\"/>\n");
        
        // Создаем элементы
        for (int i = 1; i <= elementCount; i++) {
            content.append("        <task id=\"task").append(i).append("\"/>\n");
        }
        
        content.append("        <endEvent id=\"end1\"/>\n");
        
        // Создаем потоки
        content.append("        <sequenceFlow id=\"flow_start\" sourceRef=\"start1\" targetRef=\"task1\"/>\n");
        for (int i = 1; i < elementCount; i++) {
            content.append("        <sequenceFlow id=\"flow_").append(i).append("\" ")
                  .append("sourceRef=\"task").append(i).append("\" targetRef=\"task")
                  .append(i + 1).append("\"/>\n");
        }
        content.append("        <sequenceFlow id=\"flow_end\" sourceRef=\"task")
               .append(elementCount).append("\" targetRef=\"end1\"/>\n");
        
        content.append("    </process>\n");
        content.append("</definitions>");
        
        return content.toString();
    }
    
    /**
     * Создает мок ответ LLM для структурного анализа
     */
    public static String createMockStructureResponse() {
        return "{\"structureIssues\": [" +
               "{\"type\": \"missingEndEvent\", \"severity\": \"HIGH\", \"element\": \"start1\", " +
               "\"description\": \"Process missing end event\"}]}";
    }
    
    /**
     * Создает мок ответ LLM для анализа безопасности
     */
    public static String createMockSecurityResponse() {
        return "{\"securityIssues\": [" +
               "{\"type\": \"unauthenticatedServiceCall\", \"severity\": \"CRITICAL\", " +
               "\"element\": \"task1\", \"cweId\": \"CWE-306\"}]}";
    }
    
    /**
     * Создает мок ответ LLM для анализа производительности
     */
    public static String createMockPerformanceResponse() {
        return "{\"performanceIssues\": [" +
               "{\"type\": \"complexCondition\", \"severity\": \"MEDIUM\", " +
               "\"element\": \"gateway1\", \"estimatedImpact\": \"medium\"}]}";
    }
    
    /**
     * Создает мок ответ для комплексного анализа
     */
    public static String createMockComprehensiveResponse() {
        return "{\"overallScore\": \"7.5\", \"grade\": \"B\", \"summary\": \"Test analysis\", " +
               "\"recommendations\": [\"Add authentication\"]}";
    }
    
    // ==========================================
    // Утилиты для валидации результатов тестирования
    // ==========================================
    
    /**
     * Проверяет, что результат анализа содержит ожидаемые поля
     */
    public static boolean validateAnalysisResult(Map<String, Object> result) {
        if (result == null) return false;
        
        return result.containsKey("analysisId") && 
               result.containsKey("diagramId") &&
               result.containsKey("createdAt");
    }
    
    /**
     * Проверяет, что результат содержит проблемы
     */
    public static boolean containsIssues(Map<String, Object> result) {
        if (result == null) return false;
        
        Object allIssues = result.get("allIssues");
        return allIssues instanceof List && ((List<?>) allIssues).size() > 0;
    }
    
    /**
     * Проверяет метрики производительности
     */
    public static boolean validatePerformanceMetrics(long processingTime, int maxAllowedTime) {
        return processingTime > 0 && processingTime <= maxAllowedTime;
    }
    
    /**
     * Проверяет корректность структуры BPMN
     */
    public static boolean validateBpmnStructure(String bpmnContent) {
        if (bpmnContent == null || bpmnContent.trim().isEmpty()) {
            return false;
        }
        
        return bpmnContent.contains("<definitions>") && 
               bpmnContent.contains("<process>") &&
               bpmnContent.contains("</definitions>");
    }
    
    // ==========================================
    // Утилиты для генерации тестовых данных
    // ==========================================
    
    /**
     * Генерирует уникальный ID для тестирования
     */
    public static String generateTestId() {
        return "test_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    /**
     * Создает список тестовых диаграмм разных размеров
     */
    public static Map<String, String> createSizeVariants() {
        Map<String, String> variants = new HashMap<>();
        variants.put("small", createSimpleBpmnXml());
        variants.put("medium", createProblematicBpmnXml());
        variants.put("large", createLargeBpmnXml(100));
        return variants;
    }
    
    /**
     * Создает OpenAPI спецификацию для тестирования
     */
    public static String createTestOpenApiSpec() {
        return "openapi: 3.0.0\n" +
               "info:\n" +
               "  title: Test API\n" +
               "  version: 1.0.0\n" +
               "paths:\n" +
               "  /test:\n" +
               "    get:\n" +
               "      responses:\n" +
               "        '200':\n" +
               "          description: Success";
    }
}