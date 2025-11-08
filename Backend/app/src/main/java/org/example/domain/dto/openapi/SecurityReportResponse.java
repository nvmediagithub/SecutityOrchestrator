package org.example.domain.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для отчета о безопасности OpenAPI спецификации
 */
public class SecurityReportResponse {
    
    private String analysisId;
    private String specificationId;
    private String specificationTitle;
    
    // Общая информация
    @JsonProperty("securityScore")
    private int securityScore;
    
    @JsonProperty("totalChecks")
    private int totalChecks;
    
    @JsonProperty("passedChecks")
    private int passedChecks;
    
    @JsonProperty("failedChecks")
    private int failedChecks;
    
    @JsonProperty("warningChecks")
    private int warningChecks;
    
    @JsonProperty("checks")
    private List<SecurityCheck> checks;
    
    @JsonProperty("securityStandards")
    private List<String> securityStandards;
    
    @JsonProperty("overallSecurityLevel")
    private String overallSecurityLevel;
    
    @JsonProperty("lastSecurityScan")
    private LocalDateTime lastSecurityScan;
    
    // Рекомендации по безопасности
    @JsonProperty("securityRecommendations")
    private List<String> securityRecommendations;
    
    @JsonProperty("criticalVulnerabilities")
    private int criticalVulnerabilities;
    
    @JsonProperty("complianceStatus")
    private String complianceStatus;
    
    // Конструкторы
    public SecurityReportResponse() {}
    
    public SecurityReportResponse(String analysisId, String specificationId, String specificationTitle,
                                List<SecurityCheck> checks) {
        this.analysisId = analysisId;
        this.specificationId = specificationId;
        this.specificationTitle = specificationTitle;
        this.checks = checks;
        this.totalChecks = checks.size();
        this.lastSecurityScan = LocalDateTime.now();
        
        // Подсчитываем результаты проверок
        for (SecurityCheck check : checks) {
            switch (check.getStatus().toUpperCase()) {
                case "PASSED":
                    this.passedChecks++;
                    break;
                case "FAILED":
                    this.failedChecks++;
                    break;
                case "WARNING":
                    this.warningChecks++;
                    break;
            }
        }
        
        // Вычисляем общий счет безопасности
        this.securityScore = calculateSecurityScore();
        this.overallSecurityLevel = determineSecurityLevel();
        this.criticalVulnerabilities = (int) checks.stream()
            .filter(check -> "FAILED".equals(check.getStatus()) && 
                           ("HIGH".equals(check.getSeverity()) || "CRITICAL".equals(check.getSeverity())))
            .count();
        this.complianceStatus = determineComplianceStatus();
    }
    
    // Статические фабричные методы
    public static SecurityReportResponse createMock(String analysisId, String specificationId) {
        List<SecurityCheck> mockChecks = List.of(
            SecurityCheck.createMock("AUTH_001", "Аутентификация", "FAILED", 
                "Требуется реализация JWT аутентификации", "HIGH"),
            SecurityCheck.createMock("HTTPS_001", "HTTPS", "PASSED", 
                "Все эндпоинты используют HTTPS", "LOW"),
            SecurityCheck.createMock("RATE_001", "Rate Limiting", "WARNING", 
                "Rate limiting не описан в спецификации", "MEDIUM"),
            SecurityCheck.createMock("INPUT_001", "Валидация входных данных", "FAILED", 
                "Недостаточная валидация параметров", "HIGH"),
            SecurityCheck.createMock("CORS_001", "CORS настройки", "PASSED", 
                "CORS настроены корректно", "LOW")
        );
        
        SecurityReportResponse response = new SecurityReportResponse(analysisId, specificationId, 
            "Mock API Specification", mockChecks);
        response.securityStandards = List.of("OWASP API Security", "NIST Cybersecurity Framework", "ISO 27001");
        response.securityRecommendations = List.of(
            "Реализовать JWT аутентификацию для всех защищенных эндпоинтов",
            "Добавить rate limiting для предотвращения DDoS атак",
            "Усилить валидацию входных данных",
            "Настроить мониторинг безопасности в реальном времени"
        );
        return response;
    }
    
    public static SecurityReportResponse createEmpty() {
        return new SecurityReportResponse();
    }
    
    // Вложенный класс для проверки безопасности
    public static class SecurityCheck {
        private String id;
        private String name;
        private String status; // PASSED, FAILED, WARNING
        private String description;
        private String recommendation;
        private String severity; // LOW, MEDIUM, HIGH, CRITICAL
        private String category;
        private String technicalDetails;
        private LocalDateTime checkedAt;
        
        public SecurityCheck() {}
        
        public SecurityCheck(String id, String name, String status, String description, String severity) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.description = description;
            this.severity = severity;
            this.category = determineCategory(name);
            this.checkedAt = LocalDateTime.now();
        }
        
        public static SecurityCheck createMock(String id, String name, String status, String description, String severity) {
            SecurityCheck check = new SecurityCheck();
            check.id = id;
            check.name = name;
            check.status = status;
            check.description = description;
            check.severity = severity;
            check.category = determineCategory(name);
            check.recommendation = "Следует исправить данную проблему безопасности";
            check.technicalDetails = "Детальная техническая информация будет добавлена в полной версии";
            check.checkedAt = LocalDateTime.now();
            return check;
        }
        
        private static String determineCategory(String name) {
            String lowerName = name.toLowerCase();
            if (lowerName.contains("auth") || lowerName.contains("аутентификац")) return "authentication";
            if (lowerName.contains("https") || lowerName.contains("ssl") || lowerName.contains("tls")) return "encryption";
            if (lowerName.contains("rate") || lowerName.contains("ddos")) return "protection";
            if (lowerName.contains("input") || lowerName.contains("validation") || lowerName.contains("валидац")) return "validation";
            if (lowerName.contains("cors")) return "access_control";
            return "general";
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getTechnicalDetails() { return technicalDetails; }
        public void setTechnicalDetails(String technicalDetails) { this.technicalDetails = technicalDetails; }
        
        public LocalDateTime getCheckedAt() { return checkedAt; }
        public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
    }
    
    // Вспомогательные методы
    private int calculateSecurityScore() {
        if (totalChecks == 0) return 0;
        return (int) Math.round((double) passedChecks / totalChecks * 100);
    }
    
    private String determineSecurityLevel() {
        int score = calculateSecurityScore();
        if (score >= 90) return "EXCELLENT";
        if (score >= 80) return "GOOD";
        if (score >= 70) return "ACCEPTABLE";
        if (score >= 60) return "NEEDS_IMPROVEMENT";
        return "POOR";
    }
    
    private String determineComplianceStatus() {
        if (criticalVulnerabilities == 0 && securityScore >= 80) {
            return "COMPLIANT";
        } else if (criticalVulnerabilities <= 2 && securityScore >= 60) {
            return "PARTIALLY_COMPLIANT";
        } else {
            return "NON_COMPLIANT";
        }
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
    public int getSecurityScore() { return securityScore; }
    public void setSecurityScore(int securityScore) { this.securityScore = securityScore; }
    
    public int getTotalChecks() { return totalChecks; }
    public void setTotalChecks(int totalChecks) { this.totalChecks = totalChecks; }
    
    public int getPassedChecks() { return passedChecks; }
    public void setPassedChecks(int passedChecks) { this.passedChecks = passedChecks; }
    
    public int getFailedChecks() { return failedChecks; }
    public void setFailedChecks(int failedChecks) { this.failedChecks = failedChecks; }
    
    public int getWarningChecks() { return warningChecks; }
    public void setWarningChecks(int warningChecks) { this.warningChecks = warningChecks; }
    
    public List<SecurityCheck> getChecks() { return checks; }
    public void setChecks(List<SecurityCheck> checks) { 
        this.checks = checks;
        if (checks != null) {
            this.totalChecks = checks.size();
        }
    }
    
    public List<String> getSecurityStandards() { return securityStandards; }
    public void setSecurityStandards(List<String> securityStandards) { this.securityStandards = securityStandards; }
    
    public String getOverallSecurityLevel() { return overallSecurityLevel; }
    public void setOverallSecurityLevel(String overallSecurityLevel) { this.overallSecurityLevel = overallSecurityLevel; }
    
    public LocalDateTime getLastSecurityScan() { return lastSecurityScan; }
    public void setLastSecurityScan(LocalDateTime lastSecurityScan) { this.lastSecurityScan = lastSecurityScan; }
    
    public List<String> getSecurityRecommendations() { return securityRecommendations; }
    public void setSecurityRecommendations(List<String> securityRecommendations) { this.securityRecommendations = securityRecommendations; }
    
    public int getCriticalVulnerabilities() { return criticalVulnerabilities; }
    public void setCriticalVulnerabilities(int criticalVulnerabilities) { this.criticalVulnerabilities = criticalVulnerabilities; }
    
    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
    
    @Override
    public String toString() {
        return "SecurityReportResponse{" +
                "analysisId='" + analysisId + '\'' +
                ", specificationId='" + specificationId + '\'' +
                ", specificationTitle='" + specificationTitle + '\'' +
                ", securityScore=" + securityScore +
                ", totalChecks=" + totalChecks +
                ", passedChecks=" + passedChecks +
                ", failedChecks=" + failedChecks +
                ", warningChecks=" + warningChecks +
                ", overallSecurityLevel='" + overallSecurityLevel + '\'' +
                ", complianceStatus='" + complianceStatus + '\'' +
                '}';
    }
}