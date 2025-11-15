package org.example.infrastructure.services.openapi;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.ApiSchema;
import org.example.domain.entities.openapi.ApiParameter;
import org.example.domain.entities.openapi.ApiSecurityScheme;
import org.example.domain.valueobjects.HttpMethod;
import org.example.domain.valueobjects.SeverityLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Детектор проблем в OpenAPI спецификациях
 */
@Service
public class OpenApiIssueDetector {
    
    public OpenApiIssueReport detectIssues(OpenApiSpecification specification) {
        OpenApiIssueReport report = new OpenApiIssueReport();
        report.setSpecificationId(specification.getId().toString());
        report.setSpecificationTitle(specification.getTitle());
        
        if (specification.getEndpoints() == null) {
            report.addIssue(new OpenApiIssue(
                IssueType.STRUCTURAL,
                SeverityLevel.ERROR,
                "Отсутствуют эндпоинты",
                "Спецификация не содержит эндпоинтов"
            ));
            return report;
        }
        
        detectStructuralIssues(specification, report);
        detectDocumentationInconsistencies(specification, report);
        detectVersionCompatibilityIssues(specification, report);
        detectQualityIssues(specification, report);
        detectSecurityIssues(specification, report);
        detectPerformanceIssues(specification, report);
        detectDesignIssues(specification, report);
        
        return report;
    }
    
    private void detectStructuralIssues(OpenApiSpecification specification, OpenApiIssueReport report) {
        if (specification.getEndpoints().isEmpty()) {
            report.addIssue(new OpenApiIssue(
                IssueType.STRUCTURAL,
                SeverityLevel.ERROR,
                "Отсутствуют эндпоинты",
                "Спецификация не содержит эндпоинтов"
            ));
        }
        
        Set<String> endpointKeys = new HashSet<>();
        for (ApiEndpoint endpoint : specification.getEndpoints()) {
            String key = endpoint.getMethod() + "_" + endpoint.getPath();
            if (endpointKeys.contains(key)) {
                report.addIssue(new OpenApiIssue(
                    IssueType.STRUCTURAL,
                    SeverityLevel.ERROR,
                    "Дублирование эндпоинта",
                    "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + " определен несколько раз"
                ));
            }
            endpointKeys.add(key);
        }
    }
    
    private void detectDocumentationInconsistencies(OpenApiSpecification specification, OpenApiIssueReport report) {
        for (ApiEndpoint endpoint : specification.getEndpoints()) {
            boolean hasSummary = endpoint.getSummary() != null && !endpoint.getSummary().trim().isEmpty();
            boolean hasDescription = endpoint.getDescription() != null && !endpoint.getDescription().trim().isEmpty();
            
            if (!hasSummary && !hasDescription) {
                report.addIssue(new OpenApiIssue(
                    IssueType.DOCUMENTATION,
                    SeverityLevel.WARNING,
                    "Эндпоинт без описания",
                    "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + " не имеет описания"
                ));
            }
        }
    }
    
    private void detectVersionCompatibilityIssues(OpenApiSpecification specification, OpenApiIssueReport report) {
        if (specification.getOpenApiVersion().toString().startsWith("V2_0")) {
            report.addIssue(new OpenApiIssue(
                IssueType.VERSION_COMPATIBILITY,
                SeverityLevel.WARNING,
                "Использование устаревшей версии",
                "OpenAPI 2.0 (Swagger) является устаревшим. Рекомендуется обновиться до OpenAPI 3.0+"
            ));
        }
    }
    
    private void detectQualityIssues(OpenApiSpecification specification, OpenApiIssueReport report) {
        for (ApiEndpoint endpoint : specification.getEndpoints()) {
            if (endpoint.getSummary() != null && endpoint.getSummary().length() < 10) {
                report.addIssue(new OpenApiIssue(
                    IssueType.QUALITY,
                    SeverityLevel.WARNING,
                    "Короткое описание эндпоинта",
                    "Summary для " + endpoint.getMethod() + " " + endpoint.getPath() + " слишком короткий"
                ));
            }
        }
        
        long endpointsWithoutTags = specification.getEndpoints().stream()
            .filter(endpoint -> endpoint.getTags() == null || endpoint.getTags().isEmpty())
            .count();
        
        if (endpointsWithoutTags > 0) {
            report.addIssue(new OpenApiIssue(
                IssueType.QUALITY,
                SeverityLevel.WARNING,
                "Эндпоинты без тегов",
                endpointsWithoutTags + " эндпоинтов не имеют тегов для группировки"
            ));
        }
    }
    
    private void detectSecurityIssues(OpenApiSpecification specification, OpenApiIssueReport report) {
        if (specification.getSecuritySchemes() == null || specification.getSecuritySchemes().isEmpty()) {
            boolean hasProtectedEndpoints = specification.getEndpoints().stream()
                .anyMatch(endpoint -> {
                    String path = endpoint.getPath().toLowerCase();
                    return path.contains("admin") || 
                           path.contains("user") || 
                           path.contains("auth") ||
                           path.contains("profile") ||
                           path.contains("account");
                });
            
            if (hasProtectedEndpoints) {
                report.addIssue(new OpenApiIssue(
                    IssueType.SECURITY,
                    SeverityLevel.ERROR,
                    "Отсутствует аутентификация",
                    "API содержит защищенные эндпоинты, но не определяет схемы безопасности"
                ));
            }
        }
    }
    
    private void detectPerformanceIssues(OpenApiSpecification specification, OpenApiIssueReport report) {
        for (ApiEndpoint endpoint : specification.getEndpoints()) {
            if (endpoint.getParameters() != null && endpoint.getParameters().size() > 10) {
                report.addIssue(new OpenApiIssue(
                    IssueType.PERFORMANCE,
                    SeverityLevel.WARNING,
                    "Много параметров",
                    "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + 
                    " имеет " + endpoint.getParameters().size() + " параметров"
                ));
            }
        }
    }
    
    private void detectDesignIssues(OpenApiSpecification specification, OpenApiIssueReport report) {
        Set<String> pathPrefixes = specification.getEndpoints().stream()
            .map(endpoint -> {
                String path = endpoint.getPath();
                int slashIndex = path.indexOf('/', 1);
                return slashIndex > 0 ? path.substring(0, slashIndex) : path;
            })
            .collect(Collectors.toSet());
        
        if (pathPrefixes.size() > 10) {
            report.addIssue(new OpenApiIssue(
                IssueType.DESIGN,
                SeverityLevel.INFO,
                "Много различных префиксов путей",
                "API использует " + pathPrefixes.size() + " различных префиксов путей"
            ));
        }
    }
    
    public static class OpenApiIssueReport {
        private String specificationId;
        private String specificationTitle;
        private List<OpenApiIssue> issues = new ArrayList<>();
        private LocalDateTime analysisDate = LocalDateTime.now();
        
        public void addIssue(OpenApiIssue issue) {
            issues.add(issue);
        }
        
        public List<OpenApiIssue> getIssuesBySeverity(SeverityLevel severity) {
            return issues.stream()
                .filter(issue -> issue.getSeverity() == severity)
                .collect(Collectors.toList());
        }
        
        public int getTotalIssues() {
            return issues.size();
        }
        
        public int getErrorCount() {
            return getIssuesBySeverity(SeverityLevel.ERROR).size();
        }
        
        public int getWarningCount() {
            return getIssuesBySeverity(SeverityLevel.WARNING).size();
        }
        
        public String getSpecificationId() { return specificationId; }
        public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
        
        public String getSpecificationTitle() { return specificationTitle; }
        public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
        
        public List<OpenApiIssue> getIssues() { return issues; }
        public void setIssues(List<OpenApiIssue> issues) { this.issues = issues; }
    }
    
    public static class OpenApiIssue {
        private IssueType type;
        private SeverityLevel severity;
        private String title;
        private String description;
        private LocalDateTime detectedAt = LocalDateTime.now();
        
        public OpenApiIssue(IssueType type, SeverityLevel severity, String title, String description) {
            this.type = type;
            this.severity = severity;
            this.title = title;
            this.description = description;
        }
        
        public IssueType getType() { return type; }
        public void setType(IssueType type) { this.type = type; }
        
        public SeverityLevel getSeverity() { return severity; }
        public void setSeverity(SeverityLevel severity) { this.severity = severity; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public enum IssueType {
        STRUCTURAL("Структурная"),
        DOCUMENTATION("Документация"),
        VERSION_COMPATIBILITY("Совместимость версий"),
        QUALITY("Качество"),
        SECURITY("Безопасность"),
        PERFORMANCE("Производительность"),
        DESIGN("Дизайн API");
        
        private final String displayName;
        
        IssueType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}