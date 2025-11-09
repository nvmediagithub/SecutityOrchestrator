package org.example.web.controllers;

import org.example.domain.entities.llm.ConsistencyAnalysis;
import org.example.domain.entities.llm.InconsistencyReport;
import org.example.domain.entities.llm.LLMSuggestion;
import org.example.infrastructure.services.llm.LLMConsistencyAnalysisService;
import org.example.infrastructure.services.llm.LLMInconsistencyDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST API controller для LLM анализа согласованности между OpenAPI и BPMN
 * Предоставляет endpoints для семантического анализа, обнаружения несогласованностей и генерации рекомендаций
 */
@RestController
@RequestMapping("/api/test/llm")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LLMConsistencyAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMConsistencyAnalysisController.class);
    
    @Autowired
    private LLMConsistencyAnalysisService consistencyAnalysisService;
    
    @Autowired
    private LLMInconsistencyDetectionService inconsistencyDetectionService;
    
    /**
     * POST /api/test/llm/analyze-consistency
     * Запуск анализа соответствия между OpenAPI и BPMN
     */
    @PostMapping("/analyze-consistency")
    public CompletableFuture<ResponseEntity<ConsistencyAnalysisRequest>> startConsistencyAnalysis(
            @RequestBody ConsistencyAnalysisRequest request) {
        
        logger.info("Starting consistency analysis request: openApiSpecId={}, bpmnProcessId={}", 
                   request.getOpenApiSpecId(), request.getBpmnProcessId());
        
        try {
            // Валидация входных данных
            if (request.getOpenApiSpecId() == null || request.getOpenApiSpecId().trim().isEmpty()) {
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(
                        new ConsistencyAnalysisRequest("OpenAPI Spec ID is required", null, null, null, null)
                    )
                );
            }
            
            if (request.getBpmnProcessId() == null || request.getBpmnProcessId().trim().isEmpty()) {
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(
                        new ConsistencyAnalysisRequest("BPMN Process ID is required", null, null, null, null)
                    )
                );
            }
            
            // Запуск анализа
            ConsistencyAnalysis.AnalysisType analysisType = request.getAnalysisType() != null ? 
                request.getAnalysisType() : ConsistencyAnalysis.AnalysisType.COMPREHENSIVE_ANALYSIS;
            
            ConsistencyAnalysis.AnalysisScope analysisScope = request.getAnalysisScope() != null ? 
                request.getAnalysisScope() : ConsistencyAnalysis.AnalysisScope.FULL;
            
            CompletableFuture<ConsistencyAnalysis> analysisFuture = 
                consistencyAnalysisService.analyzeConsistency(
                    request.getOpenApiSpecId(),
                    request.getBpmnProcessId(),
                    analysisType,
                    analysisScope
                );
            
            // Возвращаем ID анализа для отслеживания прогресса
            String analysisId = "consistency_" + request.getOpenApiSpecId() + "_" + 
                              request.getBpmnProcessId() + "_" + System.currentTimeMillis();
            
            ConsistencyAnalysisRequest response = new ConsistencyAnalysisRequest(
                "Analysis started successfully",
                request.getOpenApiSpecId(),
                request.getBpmnProcessId(),
                analysisType,
                analysisScope
            );
            response.setAnalysisId(analysisId);
            
            return CompletableFuture.completedFuture(ResponseEntity.ok(response));
            
        } catch (Exception e) {
            logger.error("Error starting consistency analysis", e);
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ConsistencyAnalysisRequest("Internal server error: " + e.getMessage(), null, null, null, null))
            );
        }
    }
    
    /**
     * GET /api/test/llm/analysis/{analysisId}
     * Получение результатов анализа
     */
    @GetMapping("/analysis/{analysisId}")
    public ResponseEntity<?> getAnalysisResults(@PathVariable String analysisId) {
        logger.info("Getting analysis results for analysisId={}", analysisId);
        
        try {
            LLMConsistencyAnalysisService.AnalysisStatus status =
                consistencyAnalysisService.getAnalysisStatus(analysisId);
            
            if (status == null) {
                return ResponseEntity.notFound().build();
            }
            
            if ("COMPLETED".equals(status.getStatus())) {
                ConsistencyAnalysis analysis = consistencyAnalysisService.getAnalysisResults(analysisId);
                if (analysis != null) {
                    return ResponseEntity.<Object>ok(new AnalysisResponse(analysis, status));
                }
            }
            
            return ResponseEntity.<Object>ok(new AnalysisStatusResponse(status));
            
        } catch (Exception e) {
            logger.error("Error getting analysis results", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get analysis results: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/test/llm/inconsistencies/{serviceId}
     * Получение списка несогласованностей для сервиса
     */
    @GetMapping("/inconsistencies/{serviceId}")
    public CompletableFuture<ResponseEntity<InconsistenciesResponse>> getInconsistencies(
            @PathVariable String serviceId) {
        
        logger.info("Getting inconsistencies for serviceId={}", serviceId);
        
        try {
            // Парсим serviceId для извлечения openApiSpecId и bpmnProcessId
            String[] parts = serviceId.split("_");
            if (parts.length < 2) {
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(
                        new InconsistenciesResponse("Invalid service ID format", serviceId, List.of(), 0, 0)
                    )
                );
            }
            
            String openApiSpecId = parts[0];
            String bpmnProcessId = parts[1];
            
            CompletableFuture<List<InconsistencyReport>> inconsistenciesFuture = 
                inconsistencyDetectionService.detectInconsistencies(openApiSpecId, bpmnProcessId);
            
            return inconsistenciesFuture.thenApply(inconsistencies -> {
                long criticalCount = inconsistencies.stream()
                    .filter(InconsistencyReport::isCritical)
                    .count();
                
                long highCount = inconsistencies.stream()
                    .filter(InconsistencyReport::isHighPriority)
                    .count();
                
                InconsistenciesResponse response = new InconsistenciesResponse(
                    "Inconsistencies retrieved successfully",
                    serviceId,
                    inconsistencies,
                    criticalCount,
                    highCount
                );
                
                return ResponseEntity.ok(response);
            });
            
        } catch (Exception e) {
            logger.error("Error getting inconsistencies", e);
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InconsistenciesResponse("Failed to get inconsistencies: " + e.getMessage(), 
                                                   serviceId, List.of(), 0, 0))
            );
        }
    }
    
    /**
     * GET /api/test/llm/suggestions/{serviceId}
     * Получение рекомендаций для сервиса
     */
    @GetMapping("/suggestions/{serviceId}")
    public ResponseEntity<SuggestionsResponse> getSuggestions(@PathVariable String serviceId) {
        logger.info("Getting suggestions for serviceId={}", serviceId);
        
        try {
            // Для демонстрации возвращаем базовые рекомендации
            // В реальной реализации нужно интегрировать с сервисом генерации рекомендаций
            List<LLMSuggestion> suggestions = generateBasicSuggestions(serviceId);
            
            SuggestionsResponse response = new SuggestionsResponse(
                "Suggestions retrieved successfully",
                serviceId,
                suggestions
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting suggestions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SuggestionsResponse("Failed to get suggestions: " + e.getMessage(), serviceId, List.of()));
        }
    }
    
    /**
     * POST /api/test/llm/validate-description
     * Валидация описания эндпоинта или BPMN элемента
     */
    @PostMapping("/validate-description")
    public CompletableFuture<ResponseEntity<DescriptionValidationResponse>> validateDescription(
            @RequestBody DescriptionValidationRequest request) {
        
        logger.info("Validating description for type={}, elementId={}", request.getType(), request.getElementId());
        
        try {
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(
                        new DescriptionValidationResponse("Description is required", request.getType(), 
                                                        request.getElementId(), false, List.of())
                    )
                );
            }
            
            // Создаем промпт для валидации описания
            String prompt = buildDescriptionValidationPrompt(request);
            
            // Выполняем валидацию через LLM
            // Для демонстрации возвращаем базовый результат
            List<String> issues = new ArrayList<>();
            boolean isValid = true;
            
            if (request.getDescription().length() < 10) {
                issues.add("Description is too short");
                isValid = false;
            }
            
            if (!request.getDescription().contains(" ") && request.getDescription().length() > 5) {
                issues.add("Description should be more descriptive");
            }
            
            DescriptionValidationResponse response = new DescriptionValidationResponse(
                isValid ? "Description is valid" : "Description has issues",
                request.getType(),
                request.getElementId(),
                isValid,
                issues
            );
            
            return CompletableFuture.completedFuture(ResponseEntity.ok(response));
            
        } catch (Exception e) {
            logger.error("Error validating description", e);
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DescriptionValidationResponse("Failed to validate description: " + e.getMessage(),
                                                        request.getType(), request.getElementId(), false, List.of()))
            );
        }
    }
    
    /**
     * GET /api/test/llm/clear-cache
     * Очистка кэша анализа
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, String>> clearCache() {
        logger.info("Clearing analysis cache");
        
        try {
            consistencyAnalysisService.clearCache();
            inconsistencyDetectionService.clearCache();
            
            return ResponseEntity.ok(Map.of(
                "message", "Cache cleared successfully",
                "timestamp", java.time.Instant.now().toString()
            ));
        } catch (Exception e) {
            logger.error("Error clearing cache", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to clear cache: " + e.getMessage()));
        }
    }
    
    // Вспомогательные методы
    
    private String buildDescriptionValidationPrompt(DescriptionValidationRequest request) {
        return String.format("""
            Валидируй следующее описание:
            
            Тип элемента: %s
            ID элемента: %s
            Описание: %s
            
            Проверь:
            - Понятность и информативность
            - Соответствие стандартам документирования
            - Отсутствие противоречий
            - Грамматическая корректность
            """, request.getType(), request.getElementId(), request.getDescription());
    }
    
    private List<LLMSuggestion> generateBasicSuggestions(String serviceId) {
        List<LLMSuggestion> suggestions = new ArrayList<>();
        
        // Демонстрационные рекомендации
        suggestions.add(new LLMSuggestion.Builder()
            .title("Improve API Documentation")
            .description("Enhance API endpoint descriptions to better align with BPMN process semantics")
            .category(LLMSuggestion.SuggestionCategory.DOCUMENTATION)
            .priority(LLMSuggestion.SuggestionPriority.MEDIUM)
            .stepsToImplement(Arrays.asList(
                "Review current API descriptions",
                "Align terminology with BPMN processes",
                "Add missing parameter descriptions"
            ))
            .benefits(Arrays.asList(
                "Better developer experience",
                "Improved API usability",
                "Reduced integration time"
            ))
            .build());
        
        suggestions.add(new LLMSuggestion.Builder()
            .title("Align Error Handling")
            .description("Synchronize error handling between API responses and BPMN exception flows")
            .category(LLMSuggestion.SuggestionCategory.ERROR_HANDLING)
            .priority(LLMSuggestion.SuggestionPriority.HIGH)
            .stepsToImplement(Arrays.asList(
                "Map BPMN error events to HTTP status codes",
                "Standardize error response format",
                "Document error handling procedures"
            ))
            .benefits(Arrays.asList(
                "Consistent error experience",
                "Better monitoring and debugging",
                "Improved system reliability"
            ))
            .build());
        
        return suggestions;
    }
    
    // DTO классы для запросов и ответов
    
    public static class ConsistencyAnalysisRequest {
        private String message;
        private String openApiSpecId;
        private String bpmnProcessId;
        private ConsistencyAnalysis.AnalysisType analysisType;
        private ConsistencyAnalysis.AnalysisScope analysisScope;
        private String analysisId;
        
        public ConsistencyAnalysisRequest() {}
        
        public ConsistencyAnalysisRequest(String message, String openApiSpecId, String bpmnProcessId,
                                        ConsistencyAnalysis.AnalysisType analysisType,
                                        ConsistencyAnalysis.AnalysisScope analysisScope) {
            this.message = message;
            this.openApiSpecId = openApiSpecId;
            this.bpmnProcessId = bpmnProcessId;
            this.analysisType = analysisType;
            this.analysisScope = analysisScope;
        }
        
        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getOpenApiSpecId() { return openApiSpecId; }
        public void setOpenApiSpecId(String openApiSpecId) { this.openApiSpecId = openApiSpecId; }
        
        public String getBpmnProcessId() { return bpmnProcessId; }
        public void setBpmnProcessId(String bpmnProcessId) { this.bpmnProcessId = bpmnProcessId; }
        
        public ConsistencyAnalysis.AnalysisType getAnalysisType() { return analysisType; }
        public void setAnalysisType(ConsistencyAnalysis.AnalysisType analysisType) { this.analysisType = analysisType; }
        
        public ConsistencyAnalysis.AnalysisScope getAnalysisScope() { return analysisScope; }
        public void setAnalysisScope(ConsistencyAnalysis.AnalysisScope analysisScope) { this.analysisScope = analysisScope; }
        
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    }
    
    public static class AnalysisResponse {
        private ConsistencyAnalysis analysis;
        private LLMConsistencyAnalysisService.AnalysisStatus status;
        
        public AnalysisResponse() {}
        
        public AnalysisResponse(ConsistencyAnalysis analysis, LLMConsistencyAnalysisService.AnalysisStatus status) {
            this.analysis = analysis;
            this.status = status;
        }
        
        // Getters and Setters
        public ConsistencyAnalysis getAnalysis() { return analysis; }
        public void setAnalysis(ConsistencyAnalysis analysis) { this.analysis = analysis; }
        
        public LLMConsistencyAnalysisService.AnalysisStatus getStatus() { return status; }
        public void setStatus(LLMConsistencyAnalysisService.AnalysisStatus status) { this.status = status; }
    }
    
    public static class AnalysisStatusResponse {
        private LLMConsistencyAnalysisService.AnalysisStatus status;
        
        public AnalysisStatusResponse() {}
        
        public AnalysisStatusResponse(LLMConsistencyAnalysisService.AnalysisStatus status) {
            this.status = status;
        }
        
        // Getters and Setters
        public LLMConsistencyAnalysisService.AnalysisStatus getStatus() { return status; }
        public void setStatus(LLMConsistencyAnalysisService.AnalysisStatus status) { this.status = status; }
    }
    
    public static class InconsistenciesResponse {
        private String message;
        private String serviceId;
        private List<InconsistencyReport> inconsistencies;
        private long criticalCount;
        private long highCount;
        
        public InconsistenciesResponse() {}
        
        public InconsistenciesResponse(String message, String serviceId, List<InconsistencyReport> inconsistencies,
                                     long criticalCount, long highCount) {
            this.message = message;
            this.serviceId = serviceId;
            this.inconsistencies = inconsistencies;
            this.criticalCount = criticalCount;
            this.highCount = highCount;
        }
        
        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getServiceId() { return serviceId; }
        public void setServiceId(String serviceId) { this.serviceId = serviceId; }
        
        public List<InconsistencyReport> getInconsistencies() { return inconsistencies; }
        public void setInconsistencies(List<InconsistencyReport> inconsistencies) { this.inconsistencies = inconsistencies; }
        
        public long getCriticalCount() { return criticalCount; }
        public void setCriticalCount(long criticalCount) { this.criticalCount = criticalCount; }
        
        public long getHighCount() { return highCount; }
        public void setHighCount(long highCount) { this.highCount = highCount; }
    }
    
    public static class SuggestionsResponse {
        private String message;
        private String serviceId;
        private List<LLMSuggestion> suggestions;
        
        public SuggestionsResponse() {}
        
        public SuggestionsResponse(String message, String serviceId, List<LLMSuggestion> suggestions) {
            this.message = message;
            this.serviceId = serviceId;
            this.suggestions = suggestions;
        }
        
        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getServiceId() { return serviceId; }
        public void setServiceId(String serviceId) { this.serviceId = serviceId; }
        
        public List<LLMSuggestion> getSuggestions() { return suggestions; }
        public void setSuggestions(List<LLMSuggestion> suggestions) { this.suggestions = suggestions; }
    }
    
    public static class DescriptionValidationRequest {
        private String type; // "endpoint" или "bpmn_element"
        private String elementId;
        private String description;
        
        public DescriptionValidationRequest() {}
        
        public DescriptionValidationRequest(String type, String elementId, String description) {
            this.type = type;
            this.elementId = elementId;
            this.description = description;
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getElementId() { return elementId; }
        public void setElementId(String elementId) { this.elementId = elementId; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class DescriptionValidationResponse {
        private String message;
        private String type;
        private String elementId;
        private boolean isValid;
        private List<String> issues;
        
        public DescriptionValidationResponse() {}
        
        public DescriptionValidationResponse(String message, String type, String elementId, boolean isValid, List<String> issues) {
            this.message = message;
            this.type = type;
            this.elementId = elementId;
            this.isValid = isValid;
            this.issues = issues;
        }
        
        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getElementId() { return elementId; }
        public void setElementId(String elementId) { this.elementId = elementId; }
        
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }
        
        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }
    }
}