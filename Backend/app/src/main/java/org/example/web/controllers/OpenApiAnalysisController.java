package org.example.web.controllers;

import org.example.domain.dto.openapi.OpenApiAnalysisRequest;
import org.example.domain.dto.openapi.OpenApiAnalysisResponse;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.infrastructure.services.openapi.OpenApiParserService;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for OpenAPI analysis endpoints
 */
@RestController
@RequestMapping("/api/analysis/openapi")
@CrossOrigin(origins = "*")
public class OpenApiAnalysisController {
    
    @Autowired
    private OpenApiAnalysisService analysisService;
    
    @Autowired
    private OpenApiParserService parserService;
    
    /**
     * POST /api/analysis/openapi/{specId} - Запуск анализа OpenAPI спецификации
     */
    @PostMapping("/{specId}")
    public CompletableFuture<ResponseEntity<OpenApiAnalysisResponse>> startAnalysis(
            @PathVariable String specId,
            @RequestBody OpenApiAnalysisRequest request) {
        
        try {
            // Валидация запроса
            if (request == null || request.getSpecificationId() == null) {
                return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().build()
                );
            }
            
            // Создаем демонстрационную спецификацию для анализа
            OpenApiSpecification spec = createDemoSpecification(specId, request.getSpecificationTitle());
            
            // Запускаем анализ асинхронно
            CompletableFuture<OpenApiAnalysisService.AnalysisResult> analysisFuture = 
                analysisService.analyzeSpecification(specId, spec);
            
            // Возвращаем мгновенный ответ с ID анализа
            OpenApiAnalysisResponse response = OpenApiAnalysisResponse.createMock(
                "analysis_" + System.currentTimeMillis(), 
                request.getSpecificationTitle()
            );
            
            return CompletableFuture.completedFuture(
                ResponseEntity.accepted().body(response)
            );
            
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            );
        }
    }
    
    /**
     * GET /api/analysis/openapi/{specId} - Получение результатов анализа
     */
    @GetMapping("/{specId}")
    public ResponseEntity<OpenApiAnalysisResponse> getAnalysisResults(@PathVariable String specId) {
        try {
            // Возвращаем моковые данные для демонстрации
            OpenApiAnalysisResponse response = OpenApiAnalysisResponse.createMock(
                "analysis_" + specId, 
                "Demo API Specification"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/analysis/openapi/{specId}/issues - Список проблем
     */
    @GetMapping("/{specId}/issues")
    public ResponseEntity<List<Map<String, Object>>> getIssues(@PathVariable String specId) {
        try {
            List<Map<String, Object>> issues = Arrays.asList(
                createIssue("SECURITY_001", "Отсутствует аутентификация", "HIGH", "POST /api/users", 
                    "Эндпоинт создания пользователя не требует аутентификации"),
                createIssue("VALIDATION_001", "Некорректная валидация email", "MEDIUM", "POST /api/users",
                    "Поле email не имеет соответствующей валидации"),
                createIssue("CONSISTENCY_001", "Несоответствие статусов", "LOW", "GET /api/users",
                    "Различные форматы статусов в ответах API")
            );
            
            return ResponseEntity.ok(issues);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/analysis/openapi/{specId}/security - Анализ безопасности
     */
    @GetMapping("/{specId}/security")
    public ResponseEntity<Map<String, Object>> getSecurityAnalysis(@PathVariable String specId) {
        try {
            Map<String, Object> securityAnalysis = new HashMap<>();
            securityAnalysis.put("securityScore", 65);
            securityAnalysis.put("totalChecks", 15);
            securityAnalysis.put("passedChecks", 8);
            securityAnalysis.put("failedChecks", 4);
            securityAnalysis.put("warningChecks", 3);
            
            List<Map<String, Object>> securityChecks = Arrays.asList(
                createSecurityCheck("AUTH_001", "Аутентификация", "FAILED", "Требуется реализация JWT аутентификации"),
                createSecurityCheck("HTTPS_001", "HTTPS", "PASSED", "Все эндпоинты используют HTTPS"),
                createSecurityCheck("RATE_001", "Rate Limiting", "WARNING", "Rate limiting не описан в спецификации")
            );
            securityAnalysis.put("checks", securityChecks);
            
            return ResponseEntity.ok(securityAnalysis);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/analysis/openapi/{specId}/inconsistencies - Несогласованности
     */
    @GetMapping("/{specId}/inconsistencies")
    public ResponseEntity<List<Map<String, Object>>> getInconsistencies(@PathVariable String specId) {
        try {
            List<Map<String, Object>> inconsistencies = Arrays.asList(
                createInconsistency("NAMING_001", "Соглашения об именовании", "MEDIUM", 
                    "Несоответствие стиля именования в параметрах"),
                createInconsistency("VERSION_001", "Версионирование", "LOW", 
                    "Отсутствует версионирование API"),
                createInconsistency("DOC_001", "Документация", "LOW", 
                    "Неполные описания некоторых эндпоинтов")
            );
            
            return ResponseEntity.ok(inconsistencies);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/analysis/openapi/{specId}/regenerate - Перезапуск анализа
     */
    @PostMapping("/{specId}/regenerate")
    public ResponseEntity<OpenApiAnalysisResponse> regenerateAnalysis(@PathVariable String specId) {
        try {
            // Аналогично запуску анализа
            OpenApiAnalysisResponse response = OpenApiAnalysisResponse.createMock(
                "analysis_" + System.currentTimeMillis(), 
                "Regenerated Analysis"
            );
            
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/analysis/openapi/{specId}/summary - Краткая сводка
     */
    @GetMapping("/{specId}/summary")
    public ResponseEntity<Map<String, Object>> getAnalysisSummary(@PathVariable String specId) {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalIssues", 8);
            summary.put("criticalIssues", 1);
            summary.put("highIssues", 3);
            summary.put("mediumIssues", 2);
            summary.put("lowIssues", 2);
            summary.put("overallScore", 7.2);
            summary.put("grade", "B");
            summary.put("analysisTime", "2.3s");
            summary.put("lastAnalyzed", "2024-11-08T11:20:00Z");
            
            List<String> recommendations = Arrays.asList(
                "Добавить JWT аутентификацию для защищенных эндпоинтов",
                "Улучшить валидацию входных данных",
                "Стандартизировать соглашения об именовании",
                "Добавить rate limiting описание"
            );
            summary.put("topRecommendations", recommendations);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Вспомогательные методы
    
    private OpenApiSpecification createDemoSpecification(String specId, String title) {
        OpenApiSpecification spec = new OpenApiSpecification();
        spec.setId(new org.example.domain.valueobjects.SpecificationId(specId));
        spec.setTitle(title != null ? title : "Demo API");
        spec.setDescription("Демонстрационная API спецификация для тестирования");
        spec.setVersion(new org.example.domain.valueobjects.Version(1, 0, 0));
        spec.setOpenApiVersion(org.example.domain.valueobjects.OpenApiVersion.V3_0_1);
        spec.setValid(true);
        
        // Добавляем демонстрационные эндпоинты
        List<org.example.domain.entities.openapi.ApiEndpoint> endpoints = new ArrayList<>();
        
        org.example.domain.entities.openapi.ApiEndpoint getUsersEndpoint = new org.example.domain.entities.openapi.ApiEndpoint();
        getUsersEndpoint.setPath("/api/users");
        getUsersEndpoint.setMethod(org.example.domain.valueobjects.HttpMethod.GET);
        getUsersEndpoint.setSummary("Получить список пользователей");
        endpoints.add(getUsersEndpoint);
        
        org.example.domain.entities.openapi.ApiEndpoint createUserEndpoint = new org.example.domain.entities.openapi.ApiEndpoint();
        createUserEndpoint.setPath("/api/users");
        createUserEndpoint.setMethod(org.example.domain.valueobjects.HttpMethod.POST);
        createUserEndpoint.setSummary("Создать пользователя");
        endpoints.add(createUserEndpoint);
        
        spec.setEndpoints(endpoints);
        return spec;
    }
    
    private Map<String, Object> createIssue(String id, String title, String severity, String location, String description) {
        Map<String, Object> issue = new HashMap<>();
        issue.put("id", id);
        issue.put("title", title);
        issue.put("severity", severity);
        issue.put("location", location);
        issue.put("description", description);
        issue.put("category", "general");
        issue.put("confidence", 0.85);
        return issue;
    }
    
    private Map<String, Object> createSecurityCheck(String id, String name, String status, String description) {
        Map<String, Object> check = new HashMap<>();
        check.put("id", id);
        check.put("name", name);
        check.put("status", status);
        check.put("description", description);
        check.put("recommendation", "Следует исправить данную проблему безопасности");
        return check;
    }
    
    private Map<String, Object> createInconsistency(String id, String type, String severity, String description) {
        Map<String, Object> inconsistency = new HashMap<>();
        inconsistency.put("id", id);
        inconsistency.put("type", type);
        inconsistency.put("severity", severity);
        inconsistency.put("description", description);
        inconsistency.put("affectedElements", Arrays.asList("parameters", "endpoints"));
        return inconsistency;
    }
}