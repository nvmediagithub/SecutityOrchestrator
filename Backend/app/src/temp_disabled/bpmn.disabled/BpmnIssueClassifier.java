package org.example.infrastructure.services.bpmn;

import org.example.domain.valueobjects.SeverityLevel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Классификатор проблем BPMN диаграмм
 * Классифицирует и группирует проблемы по типам и уровням критичности
 */
@Service
public class BpmnIssueClassifier {

    /**
     * Классифицированные проблемы BPMN
     */
    public static class ClassifiedBpmnIssues {
        private List<ClassifiedBpmnIssue> issues;
        private int totalIssues;
        private int criticalIssues;
        private int highIssues;
        private int mediumIssues;
        private int lowIssues;
        
        // Getters and Setters
        public List<ClassifiedBpmnIssue> getIssues() { return issues; }
        public void setIssues(List<ClassifiedBpmnIssue> issues) { 
            this.issues = issues;
            this.totalIssues = issues.size();
            this.criticalIssues = (int) issues.stream()
                .filter(issue -> issue.getSeverity() == SeverityLevel.CRITICAL)
                .count();
            this.highIssues = (int) issues.stream()
                .filter(issue -> issue.getSeverity() == SeverityLevel.HIGH)
                .count();
            this.mediumIssues = (int) issues.stream()
                .filter(issue -> issue.getSeverity() == SeverityLevel.MEDIUM)
                .count();
            this.lowIssues = (int) issues.stream()
                .filter(issue -> issue.getSeverity() == SeverityLevel.LOW)
                .count();
        }
        
        public int getTotalIssues() { return totalIssues; }
        public int getCriticalIssues() { return criticalIssues; }
        public int getHighIssues() { return highIssues; }
        public int getMediumIssues() { return mediumIssues; }
        public int getLowIssues() { return lowIssues; }
    }

    /**
     * Классифицированная проблема BPMN
     */
    public static class ClassifiedBpmnIssue {
        private String id;
        private String title;
        private String description;
        private String recommendation;
        private BpmnIssueType type;
        private SeverityLevel severity;
        private String elementId;
        private String elementName;
        private Map<String, Object> metadata;
        
        public ClassifiedBpmnIssue() {
            this.metadata = new HashMap<>();
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public BpmnIssueType getType() { return type; }
        public void setType(BpmnIssueType type) { this.type = type; }
        
        public SeverityLevel getSeverity() { return severity; }
        public void setSeverity(SeverityLevel severity) { this.severity = severity; }
        
        public String getElementId() { return elementId; }
        public void setElementId(String elementId) { this.elementId = elementId; }
        
        public String getElementName() { return elementName; }
        public void setElementName(String elementName) { this.elementName = elementName; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public Object getMetadata(String key) {
            return metadata.get(key);
        }
        
        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }
    }

    /**
     * Сырые проблемы BPMN (до классификации)
     */
    public static class RawBpmnIssue {
        private String id;
        private String type;
        private String title;
        private String description;
        private String recommendation;
        private String severity;
        private String elementId;
        private String elementName;
        private Map<String, Object> metadata;
        
        public RawBpmnIssue() {
            this.metadata = new HashMap<>();
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getElementId() { return elementId; }
        public void setElementId(String elementId) { this.elementId = elementId; }
        
        public String getElementName() { return elementName; }
        public void setElementName(String elementName) { this.elementName = elementName; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public Object getMetadata(String key) {
            return metadata.get(key);
        }
        
        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }
    }

    /**
     * Типы проблем BPMN
     */
    public enum BpmnIssueType {
        STRUCTURE,       // Структурные проблемы
        SECURITY,        // Проблемы безопасности
        PERFORMANCE,     // Проблемы производительности
        LOGIC_ERROR,     // Логические ошибки
        VALIDATION,      // Валидация BPMN
        COMPLIANCE       // Соответствие стандартам
    }

    /**
     * Классифицирует список сырых проблем
     */
    public ClassifiedBpmnIssues classifyBpmnIssues(List<RawBpmnIssue> rawIssues) {
        ClassifiedBpmnIssues classified = new ClassifiedBpmnIssues();
        
        if (rawIssues == null || rawIssues.isEmpty()) {
            classified.setIssues(new ArrayList<>());
            return classified;
        }
        
        List<ClassifiedBpmnIssue> classifiedIssues = rawIssues.stream()
            .map(this::classifyIssue)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        classified.setIssues(classifiedIssues);
        return classified;
    }

    /**
     * Классифицирует отдельную проблему
     */
    private ClassifiedBpmnIssue classifyIssue(RawBpmnIssue rawIssue) {
        if (rawIssue == null) {
            return null;
        }
        
        ClassifiedBpmnIssue classified = new ClassifiedBpmnIssue();
        
        // Копируем базовые поля
        classified.setId(rawIssue.getId());
        classified.setTitle(rawIssue.getTitle());
        classified.setDescription(rawIssue.getDescription());
        classified.setRecommendation(rawIssue.getRecommendation());
        classified.setElementId(rawIssue.getElementId());
        classified.setElementName(rawIssue.getElementName());
        classified.setMetadata(rawIssue.getMetadata());
        
        // Классифицируем тип
        classified.setType(classifyIssueType(rawIssue.getType()));
        
        // Классифицируем критичность
        classified.setSeverity(classifySeverity(rawIssue.getSeverity()));
        
        return classified;
    }

    /**
     * Классифицирует тип проблемы
     */
    private BpmnIssueType classifyIssueType(String rawType) {
        if (rawType == null) {
            return BpmnIssueType.STRUCTURE;
        }
        
        String normalizedType = rawType.toUpperCase().replaceAll("\\s+", "_");
        
        return switch (normalizedType) {
            case "STRUCTURE", "STRUCTURAL" -> BpmnIssueType.STRUCTURE;
            case "SECURITY" -> BpmnIssueType.SECURITY;
            case "PERFORMANCE" -> BpmnIssueType.PERFORMANCE;
            case "LOGIC", "LOGIC_ERROR", "LOGICAL" -> BpmnIssueType.LOGIC_ERROR;
            case "VALIDATION", "VALIDATION_ERROR" -> BpmnIssueType.VALIDATION;
            case "COMPLIANCE", "COMPLIANCE_ERROR" -> BpmnIssueType.COMPLIANCE;
            default -> BpmnIssueType.STRUCTURE;
        };
    }

    /**
     * Классифицирует уровень критичности
     */
    private SeverityLevel classifySeverity(String rawSeverity) {
        if (rawSeverity == null) {
            return SeverityLevel.MEDIUM;
        }
        
        String normalizedSeverity = rawSeverity.toUpperCase();
        
        return switch (normalizedSeverity) {
            case "CRITICAL", "VERY_HIGH" -> SeverityLevel.CRITICAL;
            case "HIGH" -> SeverityLevel.HIGH;
            case "MEDIUM", "MODERATE" -> SeverityLevel.MEDIUM;
            case "LOW", "MINOR" -> SeverityLevel.LOW;
            case "INFO", "INFORMATION" -> SeverityLevel.LOW;
            default -> SeverityLevel.MEDIUM;
        };
    }

    /**
     * Группирует проблемы по типу
     */
    public Map<BpmnIssueType, List<ClassifiedBpmnIssue>> groupIssuesByType(List<ClassifiedBpmnIssue> issues) {
        return issues.stream()
            .collect(Collectors.groupingBy(ClassifiedBpmnIssue::getType));
    }

    /**
     * Сортирует проблемы по критичности
     */
    public List<ClassifiedBpmnIssue> sortIssuesBySeverity(List<ClassifiedBpmnIssue> issues) {
        return issues.stream()
            .sorted((issue1, issue2) -> {
                // Сортируем в порядке убывания критичности
                int severity1 = getSeverityRank(issue1.getSeverity());
                int severity2 = getSeverityRank(issue2.getSeverity());
                return Integer.compare(severity2, severity1);
            })
            .collect(Collectors.toList());
    }

    private int getSeverityRank(SeverityLevel severity) {
        return switch (severity) {
            case CRITICAL -> 5;
            case HIGH -> 4;
            case MEDIUM -> 3;
            case LOW -> 2;
            case INFO -> 1;
        };
    }
}