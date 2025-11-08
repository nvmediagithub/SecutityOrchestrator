package org.example.web.controllers;

import org.example.application.service.openapi.OpenApiParsingServiceFixed;
import org.example.application.service.openapi.EndpointAnalysisService;
import org.example.domain.entities.openapi.EndpointAnalysis;
import org.example.domain.entities.openapi.OpenApiService;
import org.example.domain.valueobjects.OpenApiVersion;
import org.example.infrastructure.repositories.openapi.EndpointAnalysisRepository;
import org.example.infrastructure.repositories.openapi.OpenApiServiceRepository;
import org.example.infrastructure.repositories.openapi.ApiSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * REST контроллер для работы с OpenAPI парсингом и анализом
 */
@RestController
@RequestMapping("/api/test/openapi")
public class OpenApiController {
    
    private final OpenApiParsingServiceFixed parsingService;
    private final EndpointAnalysisService analysisService;
    private final OpenApiServiceRepository serviceRepository;
    private final ApiSchemaRepository schemaRepository;
    private final EndpointAnalysisRepository analysisRepository;
    
    @Autowired
    public OpenApiController(OpenApiParsingServiceFixed parsingService,
                           EndpointAnalysisService analysisService,
                           OpenApiServiceRepository serviceRepository,
                           ApiSchemaRepository schemaRepository,
                           EndpointAnalysisRepository analysisRepository) {
        this.parsingService = parsingService;
        this.analysisService = analysisService;
        this.serviceRepository = serviceRepository;
        this.schemaRepository = schemaRepository;
        this.analysisRepository = analysisRepository;
    }
    
    /**
     * Парсинг OpenAPI спецификации по URL
     */
    @PostMapping("/parse")
    public CompletableFuture<ResponseEntity<ParseOpenApiResponse>> parseOpenApi(
            @RequestParam String url) {
        
        return parsingService.parseFromUrl(url)
            .thenApply(result -> {
                if (result.isSuccess()) {
                    ParseOpenApiResponse response = new ParseOpenApiResponse(
                        result.getService().getId(),
                        result.getService().getServiceTitle(),
                        result.getEndpointsCount(),
                        result.getSchemasCount(),
                        "Спецификация успешно распарсена"
                    );
                    return ResponseEntity.ok(response);
                } else {
                    ParseOpenApiResponse response = new ParseOpenApiResponse(
                        null, null, 0, 0, result.getErrorMessage()
                    );
                    return ResponseEntity.badRequest().body(response);
                }
            });
    }
    
    /**
     * Парсинг OpenAPI спецификации из файла
     */
    @PostMapping("/parse/file")
    public CompletableFuture<ResponseEntity<ParseOpenApiResponse>> parseOpenApiFromFile(
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            ParseOpenApiResponse response = new ParseOpenApiResponse(
                null, null, 0, 0, "Файл не выбран"
            );
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] fileContent = file.getBytes();
                var result = parsingService.parseFromFile(fileContent, file.getOriginalFilename());
                
                if (result.isSuccess()) {
                    ParseOpenApiResponse response = new ParseOpenApiResponse(
                        result.getService().getId(),
                        result.getService().getServiceTitle(),
                        result.getEndpointsCount(),
                        result.getSchemasCount(),
                        "Файл успешно распарсен"
                    );
                    return ResponseEntity.ok(response);
                } else {
                    ParseOpenApiResponse response = new ParseOpenApiResponse(
                        null, null, 0, 0, result.getErrorMessage()
                    );
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                ParseOpenApiResponse response = new ParseOpenApiResponse(
                    null, null, 0, 0, "Ошибка обработки файла: " + e.getMessage()
                );
                return ResponseEntity.badRequest().body(response);
            }
        });
    }
    
    /**
     * Получение списка всех сервисов
     */
    @GetMapping("/services")
    public ResponseEntity<Page<ServiceListItem>> getServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OpenApiService> services = serviceRepository.findAll(pageable);
        
        Page<ServiceListItem> response = services.map(service -> new ServiceListItem(
            service.getId(),
            service.getServiceName(),
            service.getServiceTitle(),
            service.getServiceVersion(),
            service.getDescription(),
            service.getBaseUrl(),
            service.getSpecificationUrl(),
            service.getOpenApiVersion().name(),
            service.getIsActive(),
            service.getParseStatus().name(),
            service.getEndpointCount(),
            service.getSchemaCount(),
            service.getCreatedAt(),
            service.getUpdatedAt()
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение сервиса по ID
     */
    @GetMapping("/services/{serviceId}")
    public ResponseEntity<ServiceDetails> getService(@PathVariable UUID serviceId) {
        
        Optional<OpenApiService> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        OpenApiService service = serviceOpt.get();
        
        ServiceDetails response = new ServiceDetails(
            service.getId(),
            service.getServiceName(),
            service.getServiceTitle(),
            service.getServiceVersion(),
            service.getDescription(),
            service.getBaseUrl(),
            service.getSpecificationUrl(),
            service.getOpenApiVersion().name(),
            service.getIsActive(),
            service.getParseStatus().name(),
            service.getLastParsedAt(),
            service.getTags(),
            service.getCreatedAt(),
            service.getUpdatedAt()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение эндпоинтов сервиса
     */
    @GetMapping("/services/{serviceId}/endpoints")
    public ResponseEntity<List<EndpointItem>> getServiceEndpoints(@PathVariable UUID serviceId) {
        
        Optional<OpenApiService> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<EndpointItem> endpoints = serviceOpt.get().getEndpoints().stream()
            .map(endpoint -> new EndpointItem(
                endpoint.getPath(),
                endpoint.getMethod().name(),
                endpoint.getSummary(),
                endpoint.getDescription(),
                endpoint.getOperationId(),
                endpoint.getTags(),
                endpoint.getParameters() != null ? endpoint.getParameters().size() : 0
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(endpoints);
    }
    
    /**
     * Получение схем сервиса
     */
    @GetMapping("/services/{serviceId}/schemas")
    public ResponseEntity<List<SchemaItem>> getServiceSchemas(@PathVariable UUID serviceId) {
        
        Optional<OpenApiService> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<SchemaItem> schemas = serviceOpt.get().getSchemas().stream()
            .map(schema -> new SchemaItem(
                schema.getId(),
                schema.getName(),
                schema.getType(),
                schema.getFormat(),
                schema.getDescription(),
                schema.getTitle(),
                schema.getIsDeprecated(),
                schema.getRequiredFields(),
                new ArrayList<>(schema.getProperties().keySet()),
                schema.getCreatedAt(),
                schema.getUpdatedAt()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(schemas);
    }
    
    /**
     * Запуск анализа эндпоинтов сервиса
     */
    @PostMapping("/services/{serviceId}/analyze")
    public CompletableFuture<ResponseEntity<AnalysisStartResponse>> startAnalysis(
            @PathVariable UUID serviceId,
            @RequestParam EndpointAnalysis.AnalysisType analysisType,
            @RequestParam(required = false) List<String> options) {
        
        return analysisService.startAnalysis(serviceId, analysisType)
            .thenApply(analysis -> {
                AnalysisStartResponse response = new AnalysisStartResponse(
                    analysis.getId(),
                    analysis.getAnalysisName(),
                    analysis.getStatus().name(),
                    analysis.getStartedAt(),
                    analysis.getTotalEndpoints()
                );
                return ResponseEntity.ok(response);
            })
            .exceptionally(ex -> {
                return ResponseEntity.badRequest().body(
                    new AnalysisStartResponse(null, null, "ERROR", null, 0)
                );
            });
    }
    
    /**
     * Получение результатов анализа
     */
    @GetMapping("/analysis/{analysisId}")
    public ResponseEntity<AnalysisResultResponse> getAnalysisResults(@PathVariable UUID analysisId) {
        
        Optional<EndpointAnalysis> analysisOpt = analysisRepository.findById(analysisId);
        if (analysisOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        EndpointAnalysis analysis = analysisOpt.get();
        
        AnalysisResultResponse response = new AnalysisResultResponse(
            analysis.getId(),
            analysis.getAnalysisName(),
            analysis.getAnalysisType().name(),
            analysis.getStatus().name(),
            analysis.getStartedAt(),
            analysis.getCompletedAt(),
            analysis.getErrorMessage(),
            analysis.getTotalEndpoints(),
            analysis.getAnalyzedEndpoints(),
            analysis.getIssuesFound(),
            analysis.getSecurityIssues(),
            analysis.getValidationIssues(),
            analysis.getConsistencyIssues(),
            analysis.getPerformanceIssues(),
            analysis.getQualityScore(),
            analysis.getRecommendations(),
            analysis.getSummaryReport(),
            analysis.getDetailedReport()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение всех анализов сервиса
     */
    @GetMapping("/services/{serviceId}/analysis")
    public ResponseEntity<List<AnalysisSummaryResponse>> getServiceAnalyses(@PathVariable UUID serviceId) {
        
        Optional<OpenApiService> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<AnalysisSummaryResponse> analyses = analysisRepository.findByServiceId(serviceId).stream()
            .map(analysis -> new AnalysisSummaryResponse(
                analysis.getId(),
                analysis.getAnalysisName(),
                analysis.getAnalysisType().name(),
                analysis.getStatus().name(),
                analysis.getStartedAt(),
                analysis.getCompletedAt(),
                analysis.getQualityScore(),
                analysis.getIssuesFound()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(analyses);
    }
    
    // DTO классы для ответов
    public static class ParseOpenApiResponse {
        private UUID serviceId;
        private String serviceTitle;
        private int endpointsCount;
        private int schemasCount;
        private String message;
        
        public ParseOpenApiResponse(UUID serviceId, String serviceTitle, int endpointsCount, int schemasCount, String message) {
            this.serviceId = serviceId;
            this.serviceTitle = serviceTitle;
            this.endpointsCount = endpointsCount;
            this.schemasCount = schemasCount;
            this.message = message;
        }
        
        // Геттеры
        public UUID getServiceId() { return serviceId; }
        public String getServiceTitle() { return serviceTitle; }
        public int getEndpointsCount() { return endpointsCount; }
        public int getSchemasCount() { return schemasCount; }
        public String getMessage() { return message; }
    }
    
    public static class ServiceListItem {
        private UUID id;
        private String serviceName;
        private String serviceTitle;
        private String serviceVersion;
        private String description;
        private String baseUrl;
        private String specificationUrl;
        private String openApiVersion;
        private Boolean isActive;
        private String parseStatus;
        private int endpointCount;
        private int schemaCount;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        
        public ServiceListItem(UUID id, String serviceName, String serviceTitle, String serviceVersion,
                             String description, String baseUrl, String specificationUrl, String openApiVersion,
                             Boolean isActive, String parseStatus, int endpointCount, int schemaCount,
                             java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
            this.id = id;
            this.serviceName = serviceName;
            this.serviceTitle = serviceTitle;
            this.serviceVersion = serviceVersion;
            this.description = description;
            this.baseUrl = baseUrl;
            this.specificationUrl = specificationUrl;
            this.openApiVersion = openApiVersion;
            this.isActive = isActive;
            this.parseStatus = parseStatus;
            this.endpointCount = endpointCount;
            this.schemaCount = schemaCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // Геттеры
        public UUID getId() { return id; }
        public String getServiceName() { return serviceName; }
        public String getServiceTitle() { return serviceTitle; }
        public String getServiceVersion() { return serviceVersion; }
        public String getDescription() { return description; }
        public String getBaseUrl() { return baseUrl; }
        public String getSpecificationUrl() { return specificationUrl; }
        public String getOpenApiVersion() { return openApiVersion; }
        public Boolean getIsActive() { return isActive; }
        public String getParseStatus() { return parseStatus; }
        public int getEndpointCount() { return endpointCount; }
        public int getSchemaCount() { return schemaCount; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    }
    
    public static class ServiceDetails extends ServiceListItem {
        private java.time.LocalDateTime lastParsedAt;
        private String tags;
        
        public ServiceDetails(UUID id, String serviceName, String serviceTitle, String serviceVersion,
                            String description, String baseUrl, String specificationUrl, String openApiVersion,
                            Boolean isActive, String parseStatus, java.time.LocalDateTime lastParsedAt,
                            String tags, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
            super(id, serviceName, serviceTitle, serviceVersion, description, baseUrl, specificationUrl,
                  openApiVersion, isActive, parseStatus, 0, 0, createdAt, updatedAt);
            this.lastParsedAt = lastParsedAt;
            this.tags = tags;
        }
        
        public java.time.LocalDateTime getLastParsedAt() { return lastParsedAt; }
        public String getTags() { return tags; }
    }
    
    public static class EndpointItem {
        private String path;
        private String method;
        private String summary;
        private String description;
        private String operationId;
        private List<String> tags;
        private int parametersCount;
        
        public EndpointItem(String path, String method, String summary, String description,
                          String operationId, List<String> tags, int parametersCount) {
            this.path = path;
            this.method = method;
            this.summary = summary;
            this.description = description;
            this.operationId = operationId;
            this.tags = tags;
            this.parametersCount = parametersCount;
        }
        
        // Геттеры
        public String getPath() { return path; }
        public String getMethod() { return method; }
        public String getSummary() { return summary; }
        public String getDescription() { return description; }
        public String getOperationId() { return operationId; }
        public List<String> getTags() { return tags; }
        public int getParametersCount() { return parametersCount; }
    }
    
    public static class SchemaItem {
        private UUID id;
        private String name;
        private String type;
        private String format;
        private String description;
        private String title;
        private Boolean isDeprecated;
        private List<String> requiredFields;
        private List<String> properties;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        
        public SchemaItem(UUID id, String name, String type, String format, String description,
                        String title, Boolean isDeprecated, List<String> requiredFields,
                        List<String> properties, java.time.LocalDateTime createdAt,
                        java.time.LocalDateTime updatedAt) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.format = format;
            this.description = description;
            this.title = title;
            this.isDeprecated = isDeprecated;
            this.requiredFields = requiredFields;
            this.properties = properties;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // Геттеры
        public UUID getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getFormat() { return format; }
        public String getDescription() { return description; }
        public String getTitle() { return title; }
        public Boolean getIsDeprecated() { return isDeprecated; }
        public List<String> getRequiredFields() { return requiredFields; }
        public List<String> getProperties() { return properties; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    }
    
    public static class AnalysisStartResponse {
        private UUID analysisId;
        private String analysisName;
        private String status;
        private java.time.LocalDateTime startedAt;
        private Integer totalEndpoints;
        
        public AnalysisStartResponse(UUID analysisId, String analysisName, String status,
                                   java.time.LocalDateTime startedAt, Integer totalEndpoints) {
            this.analysisId = analysisId;
            this.analysisName = analysisName;
            this.status = status;
            this.startedAt = startedAt;
            this.totalEndpoints = totalEndpoints;
        }
        
        // Геттеры
        public UUID getAnalysisId() { return analysisId; }
        public String getAnalysisName() { return analysisName; }
        public String getStatus() { return status; }
        public java.time.LocalDateTime getStartedAt() { return startedAt; }
        public Integer getTotalEndpoints() { return totalEndpoints; }
    }
    
    public static class AnalysisSummaryResponse {
        private UUID id;
        private String analysisName;
        private String analysisType;
        private String status;
        private java.time.LocalDateTime startedAt;
        private java.time.LocalDateTime completedAt;
        private Double qualityScore;
        private Integer issuesFound;
        
        public AnalysisSummaryResponse(UUID id, String analysisName, String analysisType, String status,
                                     java.time.LocalDateTime startedAt, java.time.LocalDateTime completedAt,
                                     Double qualityScore, Integer issuesFound) {
            this.id = id;
            this.analysisName = analysisName;
            this.analysisType = analysisType;
            this.status = status;
            this.startedAt = startedAt;
            this.completedAt = completedAt;
            this.qualityScore = qualityScore;
            this.issuesFound = issuesFound;
        }
        
        // Геттеры
        public UUID getId() { return id; }
        public String getAnalysisName() { return analysisName; }
        public String getAnalysisType() { return analysisType; }
        public String getStatus() { return status; }
        public java.time.LocalDateTime getStartedAt() { return startedAt; }
        public java.time.LocalDateTime getCompletedAt() { return completedAt; }
        public Double getQualityScore() { return qualityScore; }
        public Integer getIssuesFound() { return issuesFound; }
    }
    
    public static class AnalysisResultResponse extends AnalysisSummaryResponse {
        private String errorMessage;
        private Integer analyzedEndpoints;
        private Integer securityIssues;
        private Integer validationIssues;
        private Integer consistencyIssues;
        private Integer performanceIssues;
        private List<String> recommendations;
        private String summaryReport;
        private String detailedReport;
        
        public AnalysisResultResponse(UUID id, String analysisName, String analysisType, String status,
                                    java.time.LocalDateTime startedAt, java.time.LocalDateTime completedAt,
                                    String errorMessage, Integer totalEndpoints, Integer analyzedEndpoints,
                                    Integer issuesFound, Integer securityIssues, Integer validationIssues,
                                    Integer consistencyIssues, Integer performanceIssues, Double qualityScore,
                                    List<String> recommendations, String summaryReport, String detailedReport) {
            super(id, analysisName, analysisType, status, startedAt, completedAt, qualityScore, issuesFound);
            this.errorMessage = errorMessage;
            this.analyzedEndpoints = analyzedEndpoints;
            this.securityIssues = securityIssues;
            this.validationIssues = validationIssues;
            this.consistencyIssues = consistencyIssues;
            this.performanceIssues = performanceIssues;
            this.recommendations = recommendations;
            this.summaryReport = summaryReport;
            this.detailedReport = detailedReport;
        }
        
        // Геттеры
        public String getErrorMessage() { return errorMessage; }
        public Integer getAnalyzedEndpoints() { return analyzedEndpoints; }
        public Integer getSecurityIssues() { return securityIssues; }
        public Integer getValidationIssues() { return validationIssues; }
        public Integer getConsistencyIssues() { return consistencyIssues; }
        public Integer getPerformanceIssues() { return performanceIssues; }
        public List<String> getRecommendations() { return recommendations; }
        public String getSummaryReport() { return summaryReport; }
        public String getDetailedReport() { return detailedReport; }
    }
}