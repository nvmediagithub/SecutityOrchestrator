package org.example.application.service.openapi;

import org.example.domain.entities.openapi.EndpointAnalysis;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.OpenApiService;
import org.example.infrastructure.repositories.openapi.EndpointAnalysisRepository;
import org.example.infrastructure.repositories.openapi.OpenApiServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Сервис для анализа эндпоинтов API
 */
@Service
@Transactional
public class EndpointAnalysisService {
    
    private final EndpointAnalysisRepository analysisRepository;
    private final OpenApiServiceRepository serviceRepository;
    
    @Autowired
    public EndpointAnalysisService(EndpointAnalysisRepository analysisRepository,
                                  OpenApiServiceRepository serviceRepository) {
        this.analysisRepository = analysisRepository;
        this.serviceRepository = serviceRepository;
    }
    
    /**
     * Запуск анализа эндпоинтов сервиса
     */
    public CompletableFuture<EndpointAnalysis> startAnalysis(UUID serviceId, EndpointAnalysis.AnalysisType analysisType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Получаем сервис
                OpenApiService service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Сервис не найден: " + serviceId));
                
                // Создаем анализ
                EndpointAnalysis analysis = new EndpointAnalysis(
                    generateAnalysisName(service, analysisType),
                    getAnalysisDescription(analysisType),
                    analysisType
                );
                analysis.setService(service);
                analysis.startAnalysis();
                analysis.setTotalEndpoints(service.getEndpointCount());
                
                // Сохраняем начальное состояние
                return analysisRepository.save(analysis);
                
            } catch (Exception e) {
                throw new RuntimeException("Ошибка запуска анализа: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Выполнение анализа эндпоинтов
     */
    public CompletableFuture<EndpointAnalysis> performAnalysis(UUID serviceId, EndpointAnalysis.AnalysisType analysisType, 
                                                               List<String> analysisOptions) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Запускаем анализ
                EndpointAnalysis analysis = startAnalysis(serviceId, analysisType).join();
                
                // Получаем эндпоинты
                OpenApiService service = analysis.getService();
                List<ApiEndpoint> endpoints = service.getEndpoints();
                
                if (endpoints.isEmpty()) {
                    analysis.setErrorMessage("Нет эндпоинтов для анализа");
                    analysis.failAnalysis("Нет эндпоинтов");
                    return analysisRepository.save(analysis);
                }
                
                // Проводим анализ
                AnalysisResult result = performDetailedAnalysis(endpoints, service, analysisType, analysisOptions);
                
                // Обновляем результаты анализа
                updateAnalysisResults(analysis, result);
                analysis.completeAnalysis();
                
                return analysisRepository.save(analysis);
                
            } catch (Exception e) {
                throw new RuntimeException("Ошибка выполнения анализа: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Получение результатов анализа
     */
    public Optional<EndpointAnalysis> getAnalysis(UUID analysisId) {
        return analysisRepository.findById(analysisId);
    }
    
    /**
     * Получение анализов сервиса
     */
    public List<EndpointAnalysis> getServiceAnalyses(UUID serviceId) {
        return analysisRepository.findByServiceId(serviceId);
    }
    
    /**
     * Получение последнего успешного анализа
     */
    public Optional<EndpointAnalysis> getLatestSuccessfulAnalysis(UUID serviceId) {
        return analysisRepository.findByServiceAndStatus(
            serviceRepository.findById(serviceId).orElseThrow(),
            EndpointAnalysis.AnalysisStatus.COMPLETED
        ).stream()
         .filter(analysis -> analysis.getCompletedAt() != null)
         .max(Comparator.comparing(EndpointAnalysis::getCompletedAt));
    }
    
    /**
     * Получение статистики анализов сервиса
     */
    public EndpointAnalysisRepository.AnalysisStatistics getServiceAnalysisStatistics(UUID serviceId) {
        OpenApiService service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Сервис не найден: " + serviceId));
        return analysisRepository.getStatisticsByService(service);
    }
    
    /**
     * Выполнение детального анализа эндпоинтов
     */
    private AnalysisResult performDetailedAnalysis(List<ApiEndpoint> endpoints, OpenApiService service,
                                                  EndpointAnalysis.AnalysisType analysisType,
                                                  List<String> analysisOptions) {
        AnalysisResult result = new AnalysisResult();
        
        // Проверяем, нужно ли выполнять различные типы анализа
        boolean includeSecurity = analysisType == EndpointAnalysis.AnalysisType.SECURITY_AUDIT ||
                                 (analysisOptions != null && analysisOptions.contains("security"));
        boolean includeValidation = analysisType == EndpointAnalysis.AnalysisType.VALIDATION_CHECK ||
                                   (analysisOptions != null && analysisOptions.contains("validation"));
        boolean includeConsistency = analysisType == EndpointAnalysis.AnalysisType.CONSISTENCY_AUDIT ||
                                    (analysisOptions != null && analysisOptions.contains("consistency"));
        boolean includePerformance = analysisType == EndpointAnalysis.AnalysisType.PERFORMANCE_ANALYSIS ||
                                    (analysisOptions != null && analysisOptions.contains("performance"));
        
        for (ApiEndpoint endpoint : endpoints) {
            try {
                // Базовый анализ эндпоинта
                performBasicEndpointAnalysis(endpoint, result);
                
                // Анализ безопасности
                if (includeSecurity) {
                    performSecurityAnalysis(endpoint, result);
                }
                
                // Анализ валидации
                if (includeValidation) {
                    performValidationAnalysis(endpoint, result);
                }
                
                // Анализ консистентности
                if (includeConsistency) {
                    performConsistencyAnalysis(endpoint, result, endpoints);
                }
                
                // Анализ производительности
                if (includePerformance) {
                    performPerformanceAnalysis(endpoint, result);
                }
                
                result.incrementAnalyzedEndpoints();
                
            } catch (Exception e) {
                result.addIssue("ANALYSIS_ERROR", "Ошибка анализа эндпоинта " + endpoint.getPath() + ": " + e.getMessage());
            }
        }
        
        // Общие проверки
        performGeneralAnalysis(endpoints, service, result);
        
        return result;
    }
    
    /**
     * Базовый анализ эндпоинта
     */
    private void performBasicEndpointAnalysis(ApiEndpoint endpoint, AnalysisResult result) {
        // Проверка на наличие описания
        if (endpoint.getDescription() == null || endpoint.getDescription().trim().isEmpty()) {
            result.addIssue("MISSING_DESCRIPTION", 
                "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + " не имеет описания");
        }
        
        // Проверка на наличие summary
        if (endpoint.getSummary() == null || endpoint.getSummary().trim().isEmpty()) {
            result.addIssue("MISSING_SUMMARY", 
                "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + " не имеет краткого описания");
        }
        
        // Проверка на наличие operationId
        if (endpoint.getOperationId() == null || endpoint.getOperationId().trim().isEmpty()) {
            result.addIssue("MISSING_OPERATION_ID", 
                "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + " не имеет operationId");
        }
    }
    
    /**
     * Анализ безопасности
     */
    private void performSecurityAnalysis(ApiEndpoint endpoint, AnalysisResult result) {
        // Проверка на наличие схем безопасности
        if (endpoint.getSecurity() == null || endpoint.getSecurity().isEmpty()) {
            // Проверяем, требуется ли аутентификация для этого эндпоинта
            if (endpoint.getMethod() == org.example.domain.valueobjects.HttpMethod.POST ||
                endpoint.getMethod() == org.example.domain.valueobjects.HttpMethod.PUT ||
                endpoint.getMethod() == org.example.domain.valueobjects.HttpMethod.DELETE) {
                result.addIssue("SECURITY_NO_AUTH", 
                    "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + 
                    " не имеет схемы безопасности (требуется аутентификация)");
            }
        }
        
        // Проверка на наличие параметров безопасности в URL
        if (endpoint.getPath().contains("{") && endpoint.getPath().contains("}")) {
            // Проверяем, нет ли чувствительных данных в URL
            if (endpoint.getPath().toLowerCase().contains("password") ||
                endpoint.getPath().toLowerCase().contains("token") ||
                endpoint.getPath().toLowerCase().contains("secret")) {
                result.addIssue("SECURITY_SENSITIVE_IN_URL", 
                    "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + 
                    " содержит чувствительные данные в URL");
            }
        }
    }
    
    /**
     * Анализ валидации
     */
    private void performValidationAnalysis(ApiEndpoint endpoint, AnalysisResult result) {
        // Проверка на наличие параметров валидации
        if (endpoint.getParameters() != null && !endpoint.getParameters().isEmpty()) {
            for (var param : endpoint.getParameters()) {
                if (param.isRequired() && (param.getDescription() == null || param.getDescription().trim().isEmpty())) {
                    result.addIssue("VALIDATION_MISSING_PARAM_DESC", 
                        "Параметр " + param.getName() + " эндпоинта " + endpoint.getMethod() + " " + endpoint.getPath() + 
                        " является обязательным, но не имеет описания");
                }
            }
        }
        
        // Проверка на наличие схемы запроса для POST/PUT
        if ((endpoint.getMethod() == org.example.domain.valueobjects.HttpMethod.POST ||
             endpoint.getMethod() == org.example.domain.valueobjects.HttpMethod.PUT) &&
            (endpoint.getRequestBody() == null || endpoint.getRequestBody().isEmpty())) {
            result.addIssue("VALIDATION_MISSING_REQUEST_SCHEMA", 
                "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + 
                " не имеет схемы запроса (требуется для POST/PUT операций)");
        }
    }
    
    /**
     * Анализ консистентности
     */
    private void performConsistencyAnalysis(ApiEndpoint endpoint, AnalysisResult result, List<ApiEndpoint> allEndpoints) {
        // Проверка на дублирующиеся operationId
        String operationId = endpoint.getOperationId();
        if (operationId != null && !operationId.trim().isEmpty()) {
            long count = allEndpoints.stream()
                .filter(e -> operationId.equals(e.getOperationId()))
                .count();
            if (count > 1) {
                result.addIssue("CONSISTENCY_DUPLICATE_OPERATION_ID", 
                    "OperationId '" + operationId + "' используется несколькими эндпоинтами");
            }
        }
        
        // Проверка на консистентность тегов
        if (endpoint.getTags() != null && !endpoint.getTags().isEmpty()) {
            // Проверяем, используются ли теги консистентно
            for (String tag : endpoint.getTags()) {
                long tagCount = allEndpoints.stream()
                    .filter(e -> e.getTags() != null && e.getTags().contains(tag))
                    .count();
                if (tagCount == 1) {
                    result.addIssue("CONSISTENCY_UNUSED_TAG", 
                        "Тег '" + tag + "' используется только один раз");
                }
            }
        }
    }
    
    /**
     * Анализ производительности
     */
    private void performPerformanceAnalysis(ApiEndpoint endpoint, AnalysisResult result) {
        // Проверка на длинные пути
        if (endpoint.getPath() != null && endpoint.getPath().length() > 100) {
            result.addIssue("PERFORMANCE_LONG_PATH", 
                "Путь эндпоинта " + endpoint.getMethod() + " " + endpoint.getPath() + 
                " слишком длинный (больше 100 символов)");
        }
        
        // Проверка на слишком много параметров
        if (endpoint.getParameters() != null && endpoint.getParameters().size() > 10) {
            result.addIssue("PERFORMANCE_TOO_MANY_PARAMS", 
                "Эндпоинт " + endpoint.getMethod() + " " + endpoint.getPath() + 
                " имеет слишком много параметров (" + endpoint.getParameters().size() + ")");
        }
        
        // Проверка на длинные описания (может влиять на размер документации)
        int descriptionLength = (endpoint.getDescription() != null) ? endpoint.getDescription().length() : 0;
        if (descriptionLength > 1000) {
            result.addIssue("PERFORMANCE_LONG_DESCRIPTION", 
                "Описание эндпоинта " + endpoint.getMethod() + " " + endpoint.getPath() + 
                " слишком длинное (" + descriptionLength + " символов)");
        }
    }
    
    /**
     * Общий анализ
     */
    private void performGeneralAnalysis(List<ApiEndpoint> endpoints, OpenApiService service, AnalysisResult result) {
        // Проверка на отсутствие тегов вообще
        long endpointsWithoutTags = endpoints.stream()
            .filter(e -> e.getTags() == null || e.getTags().isEmpty())
            .count();
        if (endpointsWithoutTags > endpoints.size() * 0.5) {
            result.addIssue("GENERAL_NO_TAGS",
                "Более 50% эндпоинтов не имеют тегов");
        }
        
        // Проверка на отсутствие базового URL
        if (service != null && (service.getBaseUrl() == null || service.getBaseUrl().trim().isEmpty())) {
            result.addIssue("GENERAL_NO_BASE_URL",
                "Сервис не имеет базового URL");
        }
    }
    
    /**
     * Обновление результатов анализа
     */
    private void updateAnalysisResults(EndpointAnalysis analysis, AnalysisResult result) {
        analysis.setAnalyzedEndpoints(result.getAnalyzedEndpoints());
        analysis.setIssuesFound(result.getIssuesCount());
        analysis.setSecurityIssues(result.getSecurityIssues());
        analysis.setValidationIssues(result.getValidationIssues());
        analysis.setConsistencyIssues(result.getConsistencyIssues());
        analysis.setPerformanceIssues(result.getPerformanceIssues());
        
        // Расчет качества
        double qualityScore = calculateQualityScore(analysis);
        analysis.setQualityScore(qualityScore);
        
        // Добавление рекомендаций
        result.getRecommendations().forEach(analysis::addRecommendation);
        
        // Формирование отчетов
        analysis.setSummaryReport(generateSummaryReport(result));
        analysis.setDetailedReport(generateDetailedReport(result));
    }
    
    /**
     * Расчет оценки качества
     */
    private double calculateQualityScore(EndpointAnalysis analysis) {
        if (analysis.getTotalEndpoints() == 0) {
            return 0.0;
        }
        
        double baseScore = 100.0;
        
        // Снижаем оценку за проблемы
        baseScore -= analysis.getIssuesFound() * 2.0; // -2 балла за каждую проблему
        baseScore -= analysis.getSecurityIssues() * 5.0; // -5 баллов за проблемы безопасности
        baseScore -= analysis.getValidationIssues() * 3.0; // -3 балла за проблемы валидации
        baseScore -= analysis.getConsistencyIssues() * 1.0; // -1 балл за проблемы консистентности
        baseScore -= analysis.getPerformanceIssues() * 2.0; // -2 балла за проблемы производительности
        
        // Нормализуем оценку
        return Math.max(0.0, Math.min(100.0, baseScore));
    }
    
    /**
     * Генерация имени анализа
     */
    private String generateAnalysisName(OpenApiService service, EndpointAnalysis.AnalysisType analysisType) {
        return service.getServiceTitle() + " - " + getTypeDisplayName(analysisType) + " - " + 
               LocalDateTime.now().toString();
    }
    
    /**
     * Получение описания типа анализа
     */
    private String getAnalysisDescription(EndpointAnalysis.AnalysisType analysisType) {
        return switch (analysisType) {
            case QUICK_SCAN -> "Быстрое сканирование эндпоинтов";
            case FULL_ANALYSIS -> "Полный анализ API";
            case SECURITY_AUDIT -> "Аудит безопасности";
            case VALIDATION_CHECK -> "Проверка валидации";
            case CONSISTENCY_AUDIT -> "Аудит консистентности";
            case PERFORMANCE_ANALYSIS -> "Анализ производительности";
        };
    }
    
    /**
     * Получение отображаемого имени типа анализа
     */
    private String getTypeDisplayName(EndpointAnalysis.AnalysisType analysisType) {
        return switch (analysisType) {
            case QUICK_SCAN -> "Быстрое сканирование";
            case FULL_ANALYSIS -> "Полный анализ";
            case SECURITY_AUDIT -> "Аудит безопасности";
            case VALIDATION_CHECK -> "Проверка валидации";
            case CONSISTENCY_AUDIT -> "Аудит консистентности";
            case PERFORMANCE_ANALYSIS -> "Анализ производительности";
        };
    }
    
    /**
     * Генерация краткого отчета
     */
    private String generateSummaryReport(AnalysisResult result) {
        return String.format("Анализ завершен. Проверено эндпоинтов: %d. Найдено проблем: %d. Оценка качества: %.1f%%", 
            result.getAnalyzedEndpoints(), result.getIssuesCount(), result.getQualityScore());
    }
    
    /**
     * Генерация детального отчета
     */
    private String generateDetailedReport(AnalysisResult result) {
        StringBuilder report = new StringBuilder();
        report.append("=== ДЕТАЛЬНЫЙ ОТЧЕТ ===\n\n");
        
        if (!result.getIssues().isEmpty()) {
            report.append("НАЙДЕННЫЕ ПРОБЛЕМЫ:\n");
            result.getIssues().forEach(issue -> report.append("- ").append(issue).append("\n"));
        } else {
            report.append("Проблем не найдено.\n");
        }
        
        report.append("\n=== РЕКОМЕНДАЦИИ ===\n");
        if (!result.getRecommendations().isEmpty()) {
            result.getRecommendations().forEach(rec -> report.append("- ").append(rec).append("\n"));
        } else {
            report.append("Рекомендации отсутствуют.\n");
        }
        
        return report.toString();
    }
    
    /**
     * Класс для хранения результатов анализа
     */
    public static class AnalysisResult {
        private final List<String> issues = new ArrayList<>();
        private final List<String> recommendations = new ArrayList<>();
        private int analyzedEndpoints = 0;
        private int securityIssues = 0;
        private int validationIssues = 0;
        private int consistencyIssues = 0;
        private int performanceIssues = 0;
        
        public void addIssue(String type, String description) {
            String issue = type + ": " + description;
            issues.add(issue);
            
            switch (type) {
                case "SECURITY_NO_AUTH", "SECURITY_SENSITIVE_IN_URL":
                    securityIssues++;
                    break;
                case "VALIDATION_MISSING_PARAM_DESC", "VALIDATION_MISSING_REQUEST_SCHEMA":
                    validationIssues++;
                    break;
                case "CONSISTENCY_DUPLICATE_OPERATION_ID", "CONSISTENCY_UNUSED_TAG":
                    consistencyIssues++;
                    break;
                case "PERFORMANCE_LONG_PATH", "PERFORMANCE_TOO_MANY_PARAMS", "PERFORMANCE_LONG_DESCRIPTION":
                    performanceIssues++;
                    break;
            }
        }
        
        public void addRecommendation(String recommendation) {
            recommendations.add(recommendation);
        }
        
        public void incrementAnalyzedEndpoints() {
            analyzedEndpoints++;
        }
        
        // Геттеры
        public List<String> getIssues() { return issues; }
        public List<String> getRecommendations() { return recommendations; }
        public int getAnalyzedEndpoints() { return analyzedEndpoints; }
        public int getSecurityIssues() { return securityIssues; }
        public int getValidationIssues() { return validationIssues; }
        public int getConsistencyIssues() { return consistencyIssues; }
        public int getPerformanceIssues() { return performanceIssues; }
        public int getIssuesCount() { return issues.size(); }
        public double getQualityScore() { 
            return 100.0 - (issues.size() * 2.0 + securityIssues * 5.0 + validationIssues * 3.0); 
        }
    }
}