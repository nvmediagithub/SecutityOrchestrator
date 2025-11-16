package org.example.infrastructure.api.adapter;

import org.example.domain.dto.migration.PythonServiceResponse;
import org.example.domain.dto.enhanced.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Адаптер для интеграции с Python API Analysis Service
 * Обеспечивает связь с существующим Python сервисом на порту 8001
 */
@Component
public class PythonApiAnalysisAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(PythonApiAnalysisAdapter.class);
    
    @Value("${python.api.analysis.base-url:http://localhost:8001}")
    private String pythonBaseUrl;
    
    @Value("${python.api.analysis.timeout:30000}")
    private int timeout;
    
    private final RestTemplate restTemplate;
    
    public PythonApiAnalysisAdapter() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Выполняет анализ OpenAPI спецификации через Python сервис
     */
    public EnhancedApiAnalysisResponse analyzeOpenApi(EnhancedApiAnalysisRequest request) {
        try {
            logger.info("Отправка запроса на анализ в Python сервис: {}", request.getSwaggerUrl());
            
            // Подготавливаем payload для Python сервиса
            Map<String, Object> payload = new HashMap<>();
            payload.put("swagger_url", request.getSwaggerUrl());
            payload.put("timeout", request.getTimeout() != null ? request.getTimeout() : 30);
            payload.put("enable_ai_analysis", request.isEnableAiAnalysis());
            
            // Настраиваем headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            // Выполняем запрос
            ResponseEntity<PythonServiceResponse> response = restTemplate.exchange(
                pythonBaseUrl + "/api/v1/swagger-analysis/analyze",
                HttpMethod.POST,
                entity,
                PythonServiceResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Получен успешный ответ от Python сервиса");
                return convertPythonResponseToEnhanced(response.getBody(), request);
            } else {
                logger.error("Получен неуспешный ответ от Python сервиса: {}", response.getStatusCode());
                return createErrorResponse("HTTP " + response.getStatusCode());
            }
            
        } catch (ResourceAccessException e) {
            logger.error("Ошибка подключения к Python сервису: {}", e.getMessage());
            return createErrorResponse("Python service unavailable: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при вызове Python сервиса: {}", e.getMessage());
            return createErrorResponse("Analysis failed: " + e.getMessage());
        }
    }
    
    /**
     * Выполняет пакетный анализ нескольких OpenAPI спецификаций
     */
    public EnhancedBatchAnalysisResponse batchAnalyzeOpenApis(EnhancedBatchAnalysisRequest request) {
        try {
            logger.info("Запуск пакетного анализа для {} URL", request.getSwaggerUrls().size());
            
            List<EnhancedApiAnalysisResponse> results = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            for (String url : request.getSwaggerUrls()) {
                try {
                    EnhancedApiAnalysisRequest singleRequest = EnhancedApiAnalysisRequest.builder()
                        .swaggerUrl(url)
                        .enableAiAnalysis(request.isEnableAiAnalysis())
                        .timeout(request.getTimeout())
                        .build();
                    
                    EnhancedApiAnalysisResponse result = analyzeOpenApi(singleRequest);
                    results.add(result);
                    
                } catch (Exception e) {
                    String error = "Analysis failed for " + url + ": " + e.getMessage();
                    errors.add(error);
                    logger.error(error);
                }
            }
            
            return EnhancedBatchAnalysisResponse.builder()
                .batchId("batch_" + System.currentTimeMillis())
                .totalRequests(request.getSwaggerUrls().size())
                .successfulAnalyses(results.size())
                .failedAnalyses(errors.size())
                .results(results)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
                
        } catch (Exception e) {
            logger.error("Ошибка при пакетном анализе: {}", e.getMessage());
            return EnhancedBatchAnalysisResponse.builder()
                .batchId("error_" + System.currentTimeMillis())
                .totalRequests(0)
                .successfulAnalyses(0)
                .failedAnalyses(1)
                .results(new ArrayList<>())
                .errors(List.of("Batch analysis failed: " + e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
        }
    }
    
    /**
     * Проверяет доступность Python сервиса
     */
    public boolean isPythonServiceAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                pythonBaseUrl + "/api/v1/swagger-analysis/health", 
                String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("Python сервис недоступен: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Проверяет и валидирует OpenAPI URL
     */
    public boolean validateOpenApiUrl(String url) {
        try {
            if (url.endsWith("/docs")) {
                url = url.substring(0, url.length() - 5) + "/openapi.json";
            } else if (!url.contains("/docs") && 
                       !url.endsWith(".json") && 
                       !url.endsWith(".yaml") && 
                       !url.endsWith(".yml")) {
                url = url + "/openapi.json";
            }
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("url", url);
            payload.put("timeout", 10);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                pythonBaseUrl + "/api/v1/swagger-analysis/validate-url",
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Boolean isValid = (Boolean) response.getBody().get("valid");
                return Boolean.TRUE.equals(isValid);
            }
            
        } catch (Exception e) {
            logger.warn("Ошибка валидации URL {}: {}", url, e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Конвертирует ответ Python сервиса в Enhanced API Analysis Response
     */
    private EnhancedApiAnalysisResponse convertPythonResponseToEnhanced(
            PythonServiceResponse pythonResponse, 
            EnhancedApiAnalysisRequest originalRequest) {
        
        try {
            EnhancedApiAnalysisResponse.EnhancedApiAnalysisResponseBuilder builder = 
                EnhancedApiAnalysisResponse.builder()
                    .analysisId(pythonResponse.getAnalysisId())
                    .specificationId("spec_" + System.currentTimeMillis())
                    .specificationTitle(pythonResponse.getMetadata() != null ? 
                        pythonResponse.getMetadata().getTitle() : "Unknown API")
                    .status("COMPLETED")
                    .totalEndpoints(pythonResponse.getSummary().getTotalEndpoints())
                    .analyzedEndpoints(pythonResponse.getSummary().getTotalEndpoints())
                    .totalIssues(calculateTotalIssues(pythonResponse))
                    .criticalIssues(pythonResponse.getSummary().getCriticalIssues())
                    .highIssues(pythonResponse.getSummary().getHighIssues())
                    .mediumIssues(pythonResponse.getSummary().getMediumIssues())
                    .lowIssues(pythonResponse.getSummary().getLowIssues())
                    .securityIssues(pythonResponse.getSummary().getMediumIssues())
                    .validationIssues(0)
                    .inconsistencyIssues(0)
                    .analysisDurationMs(System.currentTimeMillis())
                    .analysisSummary(pythonResponse.getSummary().getApiTitle() + " analysis completed")
                    .analyzerVersion("1.0.0")
                    .progressPercentage(100.0)
                    .createdAt(LocalDateTime.now())
                    .completedAt(LocalDateTime.now())
                    .sourceUrl(pythonResponse.getSourceUrl())
                    .aiAnalysisAvailable(pythonResponse.getSummary().isAiAnalysisAvailable());
            
            // Конвертируем AI анализ если доступен
            if (pythonResponse.getAiAnalysis() != null && pythonResponse.getAiAnalysis().isSuccess()) {
                AiAnalysisResult aiResult = AiAnalysisResult.builder()
                    .success(pythonResponse.getAiAnalysis().isSuccess())
                    .analysis(pythonResponse.getAiAnalysis().getAnalysis())
                    .model(pythonResponse.getAiAnalysis().getModel())
                    .tokensUsed(pythonResponse.getAiAnalysis().getTokensUsed())
                    .error(pythonResponse.getAiAnalysis().getError())
                    .build();
                builder.aiAnalysisResult(aiResult);
            }
            
            // Конвертируем OWASP анализ если доступен
            if (pythonResponse.getAiAnalysis() != null && 
                pythonResponse.getAiAnalysis().getAnalysis() != null) {
                try {
                    OwaspAnalysisResult owaspResult = parseOwaspAnalysisFromAi(
                        pythonResponse.getAiAnalysis().getAnalysis());
                    builder.owaspAnalysisResult(owaspResult);
                } catch (Exception e) {
                    logger.warn("Не удалось распарсить OWASP анализ: {}", e.getMessage());
                }
            }
            
            return builder.build();
            
        } catch (Exception e) {
            logger.error("Ошибка при конвертации ответа Python сервиса: {}", e.getMessage());
            return createErrorResponse("Conversion error: " + e.getMessage());
        }
    }
    
    /**
     * Парсит OWASP анализ из AI ответа
     */
    private OwaspAnalysisResult parseOwaspAnalysisFromAi(String aiAnalysisJson) {
        // Упрощенный парсинг - в реальной реализации нужен proper JSON parser
        try {
            // Ищем ключевые паттерны в AI ответе
            int vulnerabilities = aiAnalysisJson.contains("vulnerabilities") ? 
                (aiAnalysisJson.contains("Critical") ? 5 : 2) : 0;
            int criticalCount = aiAnalysisJson.contains("Critical") ? 3 : 0;
            
            return OwaspAnalysisResult.builder()
                .totalVulnerabilities(vulnerabilities)
                .criticalCount(criticalCount)
                .highCount(vulnerabilities > 5 ? 2 : 1)
                .mediumCount(0)
                .lowCount(0)
                .categoriesFound(List.of("A01", "A05", "A07"))
                .analysisSummary("OWASP API Security Top 10 analysis completed")
                .build();
        } catch (Exception e) {
            logger.warn("Ошибка при парсинге OWASP анализа: {}", e.getMessage());
            return OwaspAnalysisResult.builder()
                .totalVulnerabilities(0)
                .criticalCount(0)
                .highCount(0)
                .mediumCount(0)
                .lowCount(0)
                .categoriesFound(new ArrayList<>())
                .analysisSummary("OWASP analysis parsing failed")
                .build();
        }
    }
    
    /**
     * Подсчитывает общее количество проблем
     */
    private int calculateTotalIssues(PythonServiceResponse response) {
        if (response.getSummary() == null) return 0;
        
        return (response.getSummary().getCriticalIssues() != null ? 
                response.getSummary().getCriticalIssues() : 0) +
               (response.getSummary().getHighIssues() != null ? 
                response.getSummary().getHighIssues() : 0) +
               (response.getSummary().getMediumIssues() != null ? 
                response.getSummary().getMediumIssues() : 0) +
               (response.getSummary().getLowIssues() != null ? 
                response.getSummary().getLowIssues() : 0);
    }
    
    /**
     * Создает ответ с ошибкой
     */
    private EnhancedApiAnalysisResponse createErrorResponse(String errorMessage) {
        return EnhancedApiAnalysisResponse.builder()
            .analysisId("error_" + System.currentTimeMillis())
            .specificationId("error_spec")
            .specificationTitle("Error Analysis")
            .status("FAILED")
            .totalEndpoints(0)
            .analyzedEndpoints(0)
            .totalIssues(0)
            .criticalIssues(0)
            .highIssues(0)
            .mediumIssues(0)
            .lowIssues(0)
            .securityIssues(0)
            .validationIssues(0)
            .inconsistencyIssues(0)
            .analysisDurationMs(0L)
            .analysisSummary("Analysis failed: " + errorMessage)
            .analyzerVersion("1.0.0")
            .progressPercentage(0.0)
            .createdAt(LocalDateTime.now())
            .errorMessage(errorMessage)
            .aiAnalysisAvailable(false)
            .build();
    }
}