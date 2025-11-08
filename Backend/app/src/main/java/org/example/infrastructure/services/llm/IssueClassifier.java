package org.example.infrastructure.services.llm;

import org.example.domain.valueobjects.SeverityLevel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Классификатор проблем OpenAPI спецификаций
 * Автоматически классифицирует выявленные проблемы по типам, приоритетам и доменам
 */
@Service
public class IssueClassifier {
    
    // Паттерны для автоматической классификации проблем
    private static final Pattern SECURITY_KEYWORDS = Pattern.compile(
        "(?i)(auth|authentication|authorization|security|crypto|token|password|credential|sql|xss|injection|vulnerability|cve|cwe|owasp|jwt|bearer|oauth|ssl|tls|encrypt|hash|cipher|cert|certificate|csrftoken|csrf|x-frame-options|content-security-policy)"
    );
    
    private static final Pattern VALIDATION_KEYWORDS = Pattern.compile(
        "(?i)(validat|schema|type|format|pattern|required|constraint|min|max|length|range|enum|integer|number|string|boolean|email|url|date|time|regex|property|field|attribute)"
    );
    
    private static final Pattern CONSISTENCY_KEYWORDS = Pattern.compile(
        "(?i)(consistenc|inconsistenc|contradict|conflict|mismatch|align|same|equal|different|similar|uniform|variat|deviation|deviate)"
    );
    
    private static final Pattern PERFORMANCE_KEYWORDS = Pattern.compile(
        "(?i)(performance|slow|fast|optimi|latency|throughput|timeout|load|resource|memory|cpu|efficien|scalab|timeout|throttl|rate.limit|quota)"
    );
    
    private static final Pattern DOCUMENTATION_KEYWORDS = Pattern.compile(
        "(?i)(doc|comment|description|readme|example|comment|annotat|help|guide|tutorial|manual|specificat|reference)"
    );
    
    private static final Pattern HIGH_SEVERITY_KEYWORDS = Pattern.compile(
        "(?i)(critical|critical|urgent|severe|serious|high.risk|dangerous|catastrophic|emergency|exploit|vulnerability|breach|attack|malicious|weapon|hack|steal|compromise|expose|leak|at.risk)"
    );
    
    private static final Pattern MEDIUM_SEVERITY_KEYWORDS = Pattern.compile(
        "(?i)(medium|moderate|concern|worry|warning|attention|important|significant|notable|substantial|considerable|consider)"
    );
    
    private static final Pattern LOW_SEVERITY_KEYWORDS = Pattern.compile(
        "(?i)(low|minor|trivial|small|little|negligible|insignificant|cosmetic|suggest|recommend|best.practice|prefer|ideal|should|could|might|may"
    );
    
    // Категории проблем по приоритету
    private static final Map<String, Integer> CATEGORY_PRIORITY = Map.of(
        "SECURITY", 1,
        "VALIDATION", 2,
        "CONSISTENCY", 3,
        "PERFORMANCE", 4,
        "DOCUMENTATION", 5,
        "DESIGN", 6,
        "QUALITY", 7
    );
    
    // Специфичные для домена проблемы
    private static final Map<String, Set<String>> DOMAIN_PATTERNS = Map.of(
        "API_DESIGN", Set.of("endpoint", "route", "path", "method", "resource", "uri", "url"),
        "DATA_MODEL", Set.of("schema", "model", "entity", "object", "structure", "field", "property"),
        "AUTHENTICATION", Set.of("login", "signin", "token", "session", "credential", "password", "username"),
        "DATA_PROTECTION", Set.of("pii", "personal", "private", "sensitive", "confidential", "gdpr", "privacy"),
        "API_VERSIONING", Set.of("version", "v1", "v2", "deprecated", "legacy", "compatibility")
    );
    
    /**
     * Классифицирует набор проблем по типам и приоритетам
     */
    public ClassifiedIssues classifyIssues(List<RawIssue> rawIssues) {
        ClassifiedIssues classified = new ClassifiedIssues();
        
        for (RawIssue rawIssue : rawIssues) {
            ClassifiedIssue classifiedIssue = classifySingleIssue(rawIssue);
            classified.addIssue(classifiedIssue);
        }
        
        // Группировка похожих проблем
        classified.groupSimilarIssues();
        
        // Вычисление общих метрик
        classified.calculateMetrics();
        
        return classified;
    }
    
    /**
     * Классифицирует одну проблему
     */
    private ClassifiedIssue classifySingleIssue(RawIssue rawIssue) {
        ClassifiedIssue issue = new ClassifiedIssue();
        issue.setId(rawIssue.getId());
        issue.setTitle(rawIssue.getTitle());
        issue.setDescription(rawIssue.getDescription());
        issue.setOriginalSeverity(rawIssue.getSeverity());
        
        // Классификация по типу
        IssueType type = classifyIssueType(rawIssue);
        issue.setType(type);
        
        // Классификация по домену
        IssueDomain domain = classifyIssueDomain(rawIssue);
        issue.setDomain(domain);
        
        // Определение серьезности
        SeverityLevel severity = determineSeverity(rawIssue, type);
        issue.setSeverity(severity);
        
        // Вычисление приоритета
        int priority = calculatePriority(type, domain, severity);
        issue.setPriority(priority);
        
        // Группировка
        String groupId = generateGroupId(rawIssue, type, domain);
        issue.setGroupId(groupId);
        
        // Рекомендации
        issue.setRecommendations(generateRecommendations(rawIssue, type, domain));
        
        return issue;
    }
    
    /**
     * Классифицирует тип проблемы по ключевым словам
     */
    private IssueType classifyIssueType(RawIssue rawIssue) {
        String text = (rawIssue.getTitle() + " " + rawIssue.getDescription()).toLowerCase();
        
        if (SECURITY_KEYWORDS.matcher(text).find()) {
            return IssueType.SECURITY;
        } else if (VALIDATION_KEYWORDS.matcher(text).find()) {
            return IssueType.VALIDATION;
        } else if (CONSISTENCY_KEYWORDS.matcher(text).find()) {
            return IssueType.CONSISTENCY;
        } else if (PERFORMANCE_KEYWORDS.matcher(text).find()) {
            return IssueType.PERFORMANCE;
        } else if (DOCUMENTATION_KEYWORDS.matcher(text).find()) {
            return IssueType.DOCUMENTATION;
        } else if (text.contains("design") || text.contains("architecture")) {
            return IssueType.DESIGN;
        } else {
            return IssueType.QUALITY;
        }
    }
    
    /**
     * Классифицирует домен проблемы
     */
    private IssueDomain classifyIssueDomain(RawIssue rawIssue) {
        String text = (rawIssue.getTitle() + " " + rawIssue.getDescription() + 
                      (rawIssue.getLocation() != null ? " " + rawIssue.getLocation() : "")).toLowerCase();
        
        for (Map.Entry<String, Set<String>> entry : DOMAIN_PATTERNS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) {
                    return IssueDomain.valueOf(entry.getKey());
                }
            }
        }
        
        return IssueDomain.GENERAL;
    }
    
    /**
     * Определяет серьезность проблемы
     */
    private SeverityLevel determineSeverity(RawIssue rawIssue, IssueType type) {
        String text = (rawIssue.getTitle() + " " + rawIssue.getDescription()).toLowerCase();
        
        // Проверяем на критические проблемы безопасности
        if (type == IssueType.SECURITY &&
            (text.contains("critical") || text.contains("vulnerability") ||
             text.contains("injection") || text.contains("exposure"))) {
            return SeverityLevel.CRITICAL;
        }
        
        // Автоматическая оценка по ключевым словам
        if (HIGH_SEVERITY_KEYWORDS.matcher(text).find()) {
            return SeverityLevel.HIGH;
        } else if (MEDIUM_SEVERITY_KEYWORDS.matcher(text).find()) {
            return SeverityLevel.MEDIUM;
        } else if (LOW_SEVERITY_KEYWORDS.matcher(text).find()) {
            return SeverityLevel.LOW;
        }
        
        // По умолчанию на основе типа
        return switch (type) {
            case SECURITY -> SeverityLevel.CRITICAL;
            case VALIDATION -> SeverityLevel.MEDIUM;
            case CONSISTENCY -> SeverityLevel.MEDIUM;
            case PERFORMANCE -> SeverityLevel.MEDIUM;
            case DOCUMENTATION, QUALITY, DESIGN -> SeverityLevel.LOW;
        };
    }
    
    /**
     * Вычисляет приоритет проблемы
     */
    private int calculatePriority(IssueType type, IssueDomain domain, SeverityLevel severity) {
        int basePriority = CATEGORY_PRIORITY.getOrDefault(type.name(), 8);
        
        // Коррекция на основе серьезности
        int severityMultiplier = switch (severity) {
            case CRITICAL, HIGH -> 0;
            case MEDIUM -> 1;
            case LOW, INFO -> 2;
        };
        
        return basePriority * 10 + severityMultiplier;
    }
    
    /**
     * Генерирует ID группы для похожих проблем
     */
    private String generateGroupId(RawIssue rawIssue, IssueType type, IssueDomain domain) {
        String normalized = (rawIssue.getTitle() + " " + rawIssue.getDescription())
            .toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "")
            .replaceAll("\\s+", " ")
            .trim();
        
        // Берем первые 50 символов
        String shortText = normalized.length() > 50 ? normalized.substring(0, 50) : normalized;
        
        return String.format("%s_%s_%s", type.name(), domain.name(), 
                           shortText.replaceAll("\\s", "_"));
    }
    
    /**
     * Генерирует рекомендации для проблемы
     */
    private List<String> generateRecommendations(RawIssue rawIssue, IssueType type, IssueDomain domain) {
        List<String> recommendations = new ArrayList<>();
        
        switch (type) {
            case SECURITY:
                recommendations.addAll(Arrays.asList(
                    "Использовать HTTPS для всех эндпоинтов",
                    "Применить правильную аутентификацию и авторизацию",
                    "Валидировать все входные данные",
                    "Использовать безопасные алгоритмы шифрования"
                ));
                break;
            case VALIDATION:
                recommendations.addAll(Arrays.asList(
                    "Добавить валидацию схем для всех входных данных",
                    "Определить правильные типы и форматы",
                    "Добавить ограничения на поля (min, max, pattern)",
                    "Использовать enum для ограниченных значений"
                ));
                break;
            case CONSISTENCY:
                recommendations.addAll(Arrays.asList(
                    "Стандартизировать названия и описания",
                    "Обеспечить единообразие типов данных",
                    "Использовать единые соглашения об именовании",
                    "Проверить согласованность между компонентами"
                ));
                break;
            case PERFORMANCE:
                recommendations.addAll(Arrays.asList(
                    "Оптимизировать размер ответа",
                    "Реализовать пагинацию для больших наборов данных",
                    "Добавить кэширование где возможно",
                    "Ограничить количество параллельных запросов"
                ));
                break;
            case DOCUMENTATION:
                recommendations.addAll(Arrays.asList(
                    "Добавить подробные описания для всех эндпоинтов",
                    "Включить примеры запросов и ответов",
                    "Описать все параметры и их значения",
                    "Добавить документацию по аутентификации"
                ));
                break;
            default:
                recommendations.add("Рассмотреть лучшие практики OpenAPI");
        }
        
        return recommendations;
    }
    
    // Классы для представления проблем
    
    public static class RawIssue {
        private String id;
        private String title;
        private String description;
        private String severity;
        private String location;
        private String endpoint;
        private Map<String, Object> metadata;
        
        // Конструкторы, геттеры и сеттеры
        public RawIssue() {}
        
        public RawIssue(String id, String title, String description, String severity) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.severity = severity;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class ClassifiedIssue {
        private String id;
        private String title;
        private String description;
        private String originalSeverity;
        private IssueType type;
        private IssueDomain domain;
        private SeverityLevel severity;
        private int priority;
        private String groupId;
        private List<String> recommendations;
        private List<String> similarIssues;
        
        // Группировка и сортировка
        public int compareTo(ClassifiedIssue other) {
            return Integer.compare(this.priority, other.priority);
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getOriginalSeverity() { return originalSeverity; }
        public void setOriginalSeverity(String originalSeverity) { this.originalSeverity = originalSeverity; }
        
        public IssueType getType() { return type; }
        public void setType(IssueType type) { this.type = type; }
        
        public IssueDomain getDomain() { return domain; }
        public void setDomain(IssueDomain domain) { this.domain = domain; }
        
        public SeverityLevel getSeverity() { return severity; }
        public void setSeverity(SeverityLevel severity) { this.severity = severity; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
        
        public List<String> getSimilarIssues() { return similarIssues; }
        public void setSimilarIssues(List<String> similarIssues) { this.similarIssues = similarIssues; }
    }
    
    public static class ClassifiedIssues {
        private List<ClassifiedIssue> issues;
        private Map<String, List<ClassifiedIssue>> groupedIssues;
        private Map<IssueType, Integer> typeCounts;
        private Map<IssueDomain, Integer> domainCounts;
        private Map<SeverityLevel, Integer> severityCounts;
        private int totalIssues;
        private double averagePriority;
        
        public ClassifiedIssues() {
            this.issues = new ArrayList<>();
            this.groupedIssues = new HashMap<>();
            this.typeCounts = new HashMap<>();
            this.domainCounts = new HashMap<>();
            this.severityCounts = new HashMap<>();
        }
        
        public void addIssue(ClassifiedIssue issue) {
            issues.add(issue);
            
            // Обновляем статистику
            typeCounts.put(issue.getType(), typeCounts.getOrDefault(issue.getType(), 0) + 1);
            domainCounts.put(issue.getDomain(), domainCounts.getOrDefault(issue.getDomain(), 0) + 1);
            severityCounts.put(issue.getSeverity(), severityCounts.getOrDefault(issue.getSeverity(), 0) + 1);
        }
        
        public void groupSimilarIssues() {
            Map<String, List<ClassifiedIssue>> groups = issues.stream()
                .collect(Collectors.groupingBy(ClassifiedIssue::getGroupId));
            
            for (List<ClassifiedIssue> group : groups.values()) {
                if (group.size() > 1) {
                    for (ClassifiedIssue issue : group) {
                        issue.setSimilarIssues(group.stream()
                            .filter(i -> !i.getId().equals(issue.getId()))
                            .map(i -> i.getTitle())
                            .collect(Collectors.toList()));
                    }
                }
            }
            
            this.groupedIssues = groups;
        }
        
        public void calculateMetrics() {
            this.totalIssues = issues.size();
            this.averagePriority = issues.stream()
                .mapToInt(ClassifiedIssue::getPriority)
                .average()
                .orElse(0.0);
            
            // Сортируем проблемы по приоритету
            issues.sort(ClassifiedIssue::compareTo);
        }
        
        // Getters
        public List<ClassifiedIssue> getIssues() { return issues; }
        public Map<String, List<ClassifiedIssue>> getGroupedIssues() { return groupedIssues; }
        public Map<IssueType, Integer> getTypeCounts() { return typeCounts; }
        public Map<IssueDomain, Integer> getDomainCounts() { return domainCounts; }
        public Map<SeverityLevel, Integer> getSeverityCounts() { return severityCounts; }
        public int getTotalIssues() { return totalIssues; }
        public double getAveragePriority() { return averagePriority; }
    }
    
    // Перечисления для классификации
    
    public enum IssueType {
        SECURITY("Безопасность"),
        VALIDATION("Валидация"),
        CONSISTENCY("Согласованность"),
        PERFORMANCE("Производительность"),
        DOCUMENTATION("Документация"),
        DESIGN("Дизайн"),
        QUALITY("Качество");
        
        private final String displayName;
        
        IssueType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum IssueDomain {
        API_DESIGN("Дизайн API"),
        DATA_MODEL("Модель данных"),
        AUTHENTICATION("Аутентификация"),
        DATA_PROTECTION("Защита данных"),
        API_VERSIONING("Версионирование API"),
        GENERAL("Общие");
        
        private final String displayName;
        
        IssueDomain(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}