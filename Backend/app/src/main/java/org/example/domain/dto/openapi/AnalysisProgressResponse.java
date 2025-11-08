package org.example.domain.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для отслеживания прогресса анализа OpenAPI
 */
public class AnalysisProgressResponse {
    
    private String analysisId;
    private String specificationId;
    private String specificationTitle;
    
    // Общая информация о прогрессе
    private String status; // STARTED, IN_PROGRESS, AGGREGATING, COMPLETED, FAILED
    private int progressPercentage;
    private String currentPhase;
    
    @JsonProperty("startedAt")
    private LocalDateTime startedAt;
    
    @JsonProperty("estimatedCompletionTime")
    private LocalDateTime estimatedCompletionTime;
    
    @JsonProperty("lastUpdate")
    private LocalDateTime lastUpdate;
    
    // Фазы анализа
    @JsonProperty("analysisPhases")
    private List<AnalysisPhase> analysisPhases;
    
    // Временные метрики
    @JsonProperty("elapsedTimeMinutes")
    private long elapsedTimeMinutes;
    
    @JsonProperty("estimatedTimeRemainingMinutes")
    private long estimatedTimeRemainingMinutes;
    
    @JsonProperty("totalEstimatedTimeMinutes")
    private long totalEstimatedTimeMinutes;
    
    // Статистика результатов (для завершенного анализа)
    @JsonProperty("totalIssues")
    private int totalIssues;
    
    @JsonProperty("completedPhases")
    private int completedPhases;
    
    @JsonProperty("totalPhases")
    private int totalPhases;
    
    private String errorMessage;
    private String[] warnings;
    
    // Конструкторы
    public AnalysisProgressResponse() {}
    
    public AnalysisProgressResponse(String analysisId, String specificationId, String specificationTitle) {
        this.analysisId = analysisId;
        this.specificationId = specificationId;
        this.specificationTitle = specificationTitle;
        this.status = "STARTED";
        this.currentPhase = "Инициализация анализа";
        this.startedAt = LocalDateTime.now();
        this.lastUpdate = LocalDateTime.now();
        this.totalEstimatedTimeMinutes = 5; // Оценка 5 минут
        this.estimatedCompletionTime = startedAt.plusMinutes(this.totalEstimatedTimeMinutes);
        this.totalPhases = 4; // Security, Validation, Consistency, Comprehensive
        
        // Инициализируем фазы анализа
        this.analysisPhases = List.of(
            AnalysisPhase.createPhase("security", "Анализ безопасности", 0),
            AnalysisPhase.createPhase("validation", "Анализ валидации", 0),
            AnalysisPhase.createPhase("consistency", "Анализ согласованности", 0),
            AnalysisPhase.createPhase("comprehensive", "Комплексный анализ", 0)
        );
    }
    
    // Статические фабричные методы
    public static AnalysisProgressResponse createMock(String analysisId, String specificationId) {
        AnalysisProgressResponse response = new AnalysisProgressResponse(analysisId, specificationId, "Mock API Specification");
        
        // Симулируем разные статусы
        response.status = "IN_PROGRESS";
        response.currentPhase = "Анализ безопасности";
        response.progressPercentage = 45;
        
        // Обновляем фазы
        List<AnalysisPhase> phases = response.analysisPhases;
        phases.get(0).setProgress(100); // Security завершен
        phases.get(1).setProgress(60); // Validation в процессе
        phases.get(2).setProgress(0); // Consistency не начат
        phases.get(3).setProgress(0); // Comprehensive не начат
        
        response.completedPhases = 1;
        response.elapsedTimeMinutes = 2;
        response.estimatedTimeRemainingMinutes = 3;
        
        return response;
    }
    
    public static AnalysisProgressResponse createCompleted(String analysisId, String specificationId) {
        AnalysisProgressResponse response = new AnalysisProgressResponse(analysisId, specificationId, "Mock API Specification");
        response.status = "COMPLETED";
        response.currentPhase = "Анализ завершен";
        response.progressPercentage = 100;
        response.completedPhases = 4;
        response.totalIssues = 8;
        response.elapsedTimeMinutes = 4;
        response.estimatedTimeRemainingMinutes = 0;
        
        // Обновляем все фазы как завершенные
        response.analysisPhases.forEach(phase -> phase.setProgress(100));
        
        response.lastUpdate = LocalDateTime.now();
        return response;
    }
    
    public static AnalysisProgressResponse createFailed(String analysisId, String specificationId, String errorMessage) {
        AnalysisProgressResponse response = new AnalysisProgressResponse(analysisId, specificationId, "Mock API Specification");
        response.status = "FAILED";
        response.currentPhase = "Анализ прерван";
        response.errorMessage = errorMessage;
        response.warnings = new String[]{"Не все фазы анализа завершены"};
        response.lastUpdate = LocalDateTime.now();
        return response;
    }
    
    public static AnalysisProgressResponse createEmpty() {
        return new AnalysisProgressResponse();
    }
    
    // Вложенный класс для фазы анализа
    public static class AnalysisPhase {
        private String id;
        private String name;
        private String description;
        private int progress; // 0-100
        private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String result;
        
        public AnalysisPhase() {}
        
        public AnalysisPhase(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.progress = 0;
            this.status = "PENDING";
        }
        
        public static AnalysisPhase createPhase(String id, String name, int progress) {
            AnalysisPhase phase = new AnalysisPhase(id, name, "Детальный анализ " + name.toLowerCase());
            phase.setProgress(progress);
            return phase;
        }
        
        public void start() {
            this.status = "IN_PROGRESS";
            this.startedAt = LocalDateTime.now();
        }
        
        public void complete() {
            this.status = "COMPLETED";
            this.progress = 100;
            this.completedAt = LocalDateTime.now();
        }
        
        public void fail(String error) {
            this.status = "FAILED";
            this.result = "Ошибка: " + error;
            this.completedAt = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
    }
    
    // Вспомогательные методы
    public void updateProgress(int completedPhases, String currentPhaseName) {
        this.completedPhases = completedPhases;
        this.progressPercentage = (int) ((double) completedPhases / totalPhases * 100);
        this.currentPhase = currentPhaseName;
        this.lastUpdate = LocalDateTime.now();
        
        // Обновляем время
        this.elapsedTimeMinutes = java.time.Duration.between(startedAt, LocalDateTime.now()).toMinutes();
        this.estimatedTimeRemainingMinutes = Math.max(0, totalEstimatedTimeMinutes - (int) elapsedTimeMinutes);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status) || "AGGREGATING".equals(status);
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public String getCurrentPhase() { return currentPhase; }
    public void setCurrentPhase(String currentPhase) { this.currentPhase = currentPhase; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getEstimatedCompletionTime() { return estimatedCompletionTime; }
    public void setEstimatedCompletionTime(LocalDateTime estimatedCompletionTime) { this.estimatedCompletionTime = estimatedCompletionTime; }
    
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
    
    public List<AnalysisPhase> getAnalysisPhases() { return analysisPhases; }
    public void setAnalysisPhases(List<AnalysisPhase> analysisPhases) { this.analysisPhases = analysisPhases; }
    
    public long getElapsedTimeMinutes() { return elapsedTimeMinutes; }
    public void setElapsedTimeMinutes(long elapsedTimeMinutes) { this.elapsedTimeMinutes = elapsedTimeMinutes; }
    
    public long getEstimatedTimeRemainingMinutes() { return estimatedTimeRemainingMinutes; }
    public void setEstimatedTimeRemainingMinutes(long estimatedTimeRemainingMinutes) { this.estimatedTimeRemainingMinutes = estimatedTimeRemainingMinutes; }
    
    public long getTotalEstimatedTimeMinutes() { return totalEstimatedTimeMinutes; }
    public void setTotalEstimatedTimeMinutes(long totalEstimatedTimeMinutes) { this.totalEstimatedTimeMinutes = totalEstimatedTimeMinutes; }
    
    public int getTotalIssues() { return totalIssues; }
    public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
    
    public int getCompletedPhases() { return completedPhases; }
    public void setCompletedPhases(int completedPhases) { this.completedPhases = completedPhases; }
    
    public int getTotalPhases() { return totalPhases; }
    public void setTotalPhases(int totalPhases) { this.totalPhases = totalPhases; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String[] getWarnings() { return warnings; }
    public void setWarnings(String[] warnings) { this.warnings = warnings; }
    
    @Override
    public String toString() {
        return "AnalysisProgressResponse{" +
                "analysisId='" + analysisId + '\'' +
                ", specificationId='" + specificationId + '\'' +
                ", specificationTitle='" + specificationTitle + '\'' +
                ", status='" + status + '\'' +
                ", progressPercentage=" + progressPercentage +
                ", currentPhase='" + currentPhase + '\'' +
                ", completedPhases=" + completedPhases +
                ", totalPhases=" + totalPhases +
                ", totalIssues=" + totalIssues +
                ", elapsedTimeMinutes=" + elapsedTimeMinutes +
                '}';
    }
}