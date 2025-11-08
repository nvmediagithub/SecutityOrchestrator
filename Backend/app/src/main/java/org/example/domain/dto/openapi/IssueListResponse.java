package org.example.domain.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для списка проблем, найденных в OpenAPI спецификации
 */
public class IssueListResponse {
    
    private String analysisId;
    private String specificationId;
    private String specificationTitle;
    
    @JsonProperty("totalIssues")
    private int totalIssues;
    
    @JsonProperty("criticalIssues")
    private int criticalIssues;
    
    @JsonProperty("highIssues")
    private int highIssues;
    
    @JsonProperty("mediumIssues")
    private int mediumIssues;
    
    @JsonProperty("lowIssues")
    private int lowIssues;
    
    @JsonProperty("issues")
    private List<IssueDetail> issues;
    
    @JsonProperty("filterCategories")
    private List<String> filterCategories;
    
    @JsonProperty("lastUpdated")
    private LocalDateTime lastUpdated;
    
    // Конструкторы
    public IssueListResponse() {}
    
    public IssueListResponse(String analysisId, String specificationId, String specificationTitle,
                           List<IssueDetail> issues) {
        this.analysisId = analysisId;
        this.specificationId = specificationId;
        this.specificationTitle = specificationTitle;
        this.issues = issues;
        this.totalIssues = issues.size();
        this.lastUpdated = LocalDateTime.now();
        
        // Подсчитываем проблемы по уровням
        for (IssueDetail issue : issues) {
            switch (issue.getSeverity().toUpperCase()) {
                case "CRITICAL":
                    this.criticalIssues++;
                    break;
                case "HIGH":
                    this.highIssues++;
                    break;
                case "MEDIUM":
                    this.mediumIssues++;
                    break;
                case "LOW":
                    this.lowIssues++;
                    break;
            }
        }
    }
    
    // Статические фабричные методы
    public static IssueListResponse createMock(String analysisId, String specificationId) {
        List<IssueDetail> mockIssues = List.of(
            IssueDetail.createMock("SECURITY_001", "Отсутствует аутентификация", "HIGH", 
                "POST /api/users", "Эндпоинт создания пользователя не требует аутентификации"),
            IssueDetail.createMock("VALIDATION_001", "Некорректная валидация email", "MEDIUM", 
                "POST /api/users", "Поле email не имеет соответствующей валидации"),
            IssueDetail.createMock("CONSISTENCY_001", "Несоответствие статусов", "LOW", 
                "GET /api/users", "Различные форматы статусов в ответах API"),
            IssueDetail.createMock("SECURITY_002", "Отсутствует rate limiting", "HIGH", 
                "POST /api/auth/login", "Эндпоинт входа в систему не защищен от брутфорса")
        );
        
        IssueListResponse response = new IssueListResponse(analysisId, specificationId, "Mock API Specification", mockIssues);
        response.filterCategories = List.of("security", "validation", "consistency", "documentation", "performance");
        return response;
    }
    
    public static IssueListResponse createEmpty() {
        return new IssueListResponse();
    }
    
    // Вложенный класс для детального описания проблемы
    public static class IssueDetail {
        private String id;
        private String title;
        private String severity;
        private String category;
        private String location;
        private String description;
        private String recommendation;
        private double confidence;
        private LocalDateTime detectedAt;
        private String[] affectedElements;
        
        public IssueDetail() {}
        
        public IssueDetail(String id, String title, String severity, String category,
                         String location, String description, String recommendation) {
            this.id = id;
            this.title = title;
            this.severity = severity;
            this.category = category;
            this.location = location;
            this.description = description;
            this.recommendation = recommendation;
            this.confidence = 0.85;
            this.detectedAt = LocalDateTime.now();
        }
        
        public static IssueDetail createMock(String id, String title, String severity, String location, String description) {
            IssueDetail issue = new IssueDetail();
            issue.id = id;
            issue.title = title;
            issue.severity = severity;
            issue.location = location;
            issue.description = description;
            issue.category = "general";
            issue.recommendation = "Следует исправить данную проблему";
            issue.confidence = 0.85;
            issue.detectedAt = LocalDateTime.now();
            issue.affectedElements = new String[]{"endpoints", "parameters"};
            return issue;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public LocalDateTime getDetectedAt() { return detectedAt; }
        public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
        
        public String[] getAffectedElements() { return affectedElements; }
        public void setAffectedElements(String[] affectedElements) { this.affectedElements = affectedElements; }
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
    public int getTotalIssues() { return totalIssues; }
    public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
    
    public int getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public int getHighIssues() { return highIssues; }
    public void setHighIssues(int highIssues) { this.highIssues = highIssues; }
    
    public int getMediumIssues() { return mediumIssues; }
    public void setMediumIssues(int mediumIssues) { this.mediumIssues = mediumIssues; }
    
    public int getLowIssues() { return lowIssues; }
    public void setLowIssues(int lowIssues) { this.lowIssues = lowIssues; }
    
    public List<IssueDetail> getIssues() { return issues; }
    public void setIssues(List<IssueDetail> issues) { 
        this.issues = issues;
        if (issues != null) {
            this.totalIssues = issues.size();
        }
    }
    
    public List<String> getFilterCategories() { return filterCategories; }
    public void setFilterCategories(List<String> filterCategories) { this.filterCategories = filterCategories; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    @Override
    public String toString() {
        return "IssueListResponse{" +
                "analysisId='" + analysisId + '\'' +
                ", specificationId='" + specificationId + '\'' +
                ", specificationTitle='" + specificationTitle + '\'' +
                ", totalIssues=" + totalIssues +
                ", criticalIssues=" + criticalIssues +
                ", highIssues=" + highIssues +
                ", mediumIssues=" + mediumIssues +
                ", lowIssues=" + lowIssues +
                ", issues=" + issues +
                ", filterCategories=" + filterCategories +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}