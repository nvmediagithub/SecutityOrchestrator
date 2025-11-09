package org.example.application.service.dependency;

import org.example.domain.dto.dependency.ApiDependency;
import org.example.domain.dto.dependency.DependencyAnalysisResult;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.ApiParameter;
import org.example.domain.entities.openapi.ApiSchema;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.infrastructure.services.openapi.OpenApiParserService;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.valueobjects.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * Анализатор зависимостей API с интеграцией OpenApiAnalysisService
 * Анализирует связи между API эндпоинтами, потоки данных, последовательности вызовов
 */
@Service
public class ApiDependencyAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiDependencyAnalyzer.class);
    
    @Autowired
    private OpenApiAnalysisService openApiAnalysisService;
    
    @Autowired
    private OpenApiParserService openApiParserService;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    // Константы для анализа
    private static final long ANALYSIS_TIMEOUT_MINUTES = 15;
    private static final String API_DEPENDENCY_PROMPT = "api_dependency_analysis";
    private static final Pattern HTTP_METHOD_PATTERN = Pattern.compile("(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)");
    private static final Pattern ID_FIELD_PATTERN = Pattern.compile(".*[Ii]d$|.*[Ii]d_");
    private static final Pattern FOREIGN_KEY_PATTERN = Pattern.compile(".*[Ff]k_.*|.*_[Ff]k$|.*[Ff]oreign[Kk]ey.*");
    
    /**
     * Анализирует зависимости API для OpenApi спецификации
     */
    public CompletableFuture<List<ApiDependency>> analyzeApiDependencies(String specId, OpenApiSpecification spec) {
        logger.info("Starting API dependency analysis for spec: {}", specId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ApiDependency> dependencies = new ArrayList<>();
                
                // 1. Анализ прямых связей между эндпоинтами
                List<ApiDependency> directDependencies = analyzeDirectEndpointDependencies(spec);
                dependencies.addAll(directDependencies);
                
                // 2. Анализ полей и их связей
                List<ApiDependency> fieldDependencies = analyzeFieldDependencies(spec);
                dependencies.addAll(fieldDependencies);
                
                // 3. Анализ последовательностей API вызовов
                List<ApiDependency> sequenceDependencies = analyzeApiSequences(spec);
                dependencies.addAll(sequenceDependencies);
                
                // 4. Анализ справочных данных
                List<ApiDependency> referenceDependencies = analyzeReferenceDataDependencies(spec);
                dependencies.addAll(referenceDependencies);
                
                // 5. Дополнительный анализ с помощью OpenApiAnalysisService
                List<ApiDependency> openApiDependencies = analyzeWithOpenApiService(specId, spec);
                dependencies.addAll(openApiDependencies);
                
                // Удаляем дубликаты
                List<ApiDependency> uniqueDependencies = dependencies.stream()
                    .distinct()
                    .collect(Collectors.toList());
                
                logger.info("API dependency analysis completed for spec: {}, found {} dependencies", 
                    specId, uniqueDependencies.size());
                
                return uniqueDependencies;
                
            } catch (Exception e) {
                logger.error("API dependency analysis failed for spec: {}", specId, e);
                throw new RuntimeException("API dependency analysis failed", e);
            }
        });
    }
    
    /**
     * Анализирует прямые связи между эндпоинтами на основе HTTP методов и схем
     */
    private List<ApiDependency> analyzeDirectEndpointDependencies(OpenApiSpecification spec) {
        List<ApiDependency> dependencies = new ArrayList<>();
        
        try {
            // Анализируем эндпоинты из OpenAPI спецификации
            if (spec.getEndpoints() == null || spec.getEndpoints().isEmpty()) return dependencies;
            
            for (ApiEndpoint endpoint : spec.getEndpoints()) {
                // Ищем зависимые эндпоинты
                for (ApiEndpoint otherEndpoint : spec.getEndpoints()) {
                    // Пропускаем текущий эндпоинт
                    if (endpoint.equals(otherEndpoint)) continue;
                    
                    // Анализируем зависимость
                    ApiDependency.DependencyType dependencyType = determineDirectDependencyType(
                        endpoint.getMethod().toString(), otherEndpoint.getMethod().toString());
                    if (dependencyType != null) {
                        ApiDependency dependency = new ApiDependency(
                            endpoint.getPath(), otherEndpoint.getPath(), 
                            endpoint.getMethod().toString(), otherEndpoint.getMethod().toString(), dependencyType
                        );
                        
                        // Добавляем информацию о полях
                        Set<String> responseFields = extractResponseFields(endpoint);
                        Set<String> requestFields = extractRequestFields(otherEndpoint);
                        
                        dependency.setSharedFields(findSharedFields(responseFields, requestFields));
                        dependencies.add(dependency);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing direct endpoint dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует зависимости на основе полей данных
     */
    private List<ApiDependency> analyzeFieldDependencies(OpenApiSpecification spec) {
        List<ApiDependency> dependencies = new ArrayList<>();
        
        try {
            if (spec.getEndpoints() == null || spec.getSchemas() == null) {
                return dependencies;
            }
            
            Map<String, Set<String>> pathFields = new HashMap<>();
            
            // Собираем поля для каждого эндпоинта
            for (ApiEndpoint endpoint : spec.getEndpoints()) {
                Set<String> fields = new HashSet<>();
                
                // Извлекаем поля из параметров
                if (endpoint.getParameters() != null) {
                    for (ApiParameter param : endpoint.getParameters()) {
                        fields.add(param.getName());
                    }
                }
                
                // Извлекаем поля из схем в request body
                if (endpoint.getRequestBody() != null) {
                    Object content = endpoint.getRequestBody().get("content");
                    if (content instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> contentMap = (Map<String, Object>) content;
                        for (Object mediaTypeObj : contentMap.values()) {
                            if (mediaTypeObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> mediaType = (Map<String, Object>) mediaTypeObj;
                                Object schema = mediaType.get("schema");
                                if (schema instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> schemaMap = (Map<String, Object>) schema;
                                    fields.addAll(extractSchemaFieldsFromMap(schemaMap));
                                }
                            }
                        }
                    }
                }
                
                // Извлекаем поля из схем в responses
                if (endpoint.getResponses() != null) {
                    for (Object responseObj : endpoint.getResponses().values()) {
                        if (responseObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> response = (Map<String, Object>) responseObj;
                            Object content = response.get("content");
                            if (content instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> contentMap = (Map<String, Object>) content;
                                for (Object mediaTypeObj : contentMap.values()) {
                                    if (mediaTypeObj instanceof Map) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> mediaType = (Map<String, Object>) mediaTypeObj;
                                        Object schema = mediaType.get("schema");
                                        if (schema instanceof Map) {
                                            @SuppressWarnings("unchecked")
                                            Map<String, Object> schemaMap = (Map<String, Object>) schema;
                                            fields.addAll(extractSchemaFieldsFromMap(schemaMap));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                pathFields.put(endpoint.getPath(), fields);
            }
                
            // Анализируем зависимости полей
            for (Map.Entry<String, Set<String>> sourceEntry : pathFields.entrySet()) {
                for (Map.Entry<String, Set<String>> targetEntry : pathFields.entrySet()) {
                    if (sourceEntry.getKey().equals(targetEntry.getKey())) continue;
                    
                    Set<String> sourceFields = sourceEntry.getValue();
                    Set<String> targetFields = targetEntry.getValue();
                    
                    // Ищем ID поля
                    Set<String> sourceIds = sourceFields.stream()
                        .filter(field -> ID_FIELD_PATTERN.matcher(field).matches())
                        .collect(Collectors.toSet());
                    
                    Set<String> targetIdFields = targetFields.stream()
                        .filter(field -> ID_FIELD_PATTERN.matcher(field).matches())
                        .collect(Collectors.toSet());
                    
                    // Создаем зависимости на основе ID
                    for (String sourceId : sourceIds) {
                        for (String targetIdField : targetIdFields) {
                            if (isRelatedIdField(sourceId, targetIdField)) {
                                ApiDependency dependency = new ApiDependency(
                                    sourceEntry.getKey(), targetEntry.getKey(), 
                                    "GET", "GET", ApiDependency.DependencyType.FOREIGN_KEY
                                );
                                
                                dependency.setForeignKeyRelationship(sourceId + " -> " + targetIdField);
                                dependency.setCreatedFields(Collections.singleton(sourceId));
                                dependency.setConsumedFields(Collections.singleton(targetIdField));
                                dependencies.add(dependency);
                            }
                        }
                    }
                    
                    // Анализируем foreign key relationships
                    Set<String> sourceForeignKeys = sourceFields.stream()
                        .filter(field -> FOREIGN_KEY_PATTERN.matcher(field).matches())
                        .collect(Collectors.toSet());
                    
                    if (!sourceForeignKeys.isEmpty()) {
                        ApiDependency dependency = new ApiDependency(
                            sourceEntry.getKey(), targetEntry.getKey(),
                            "POST", "GET", ApiDependency.DependencyType.FOREIGN_KEY
                        );
                        
                        dependency.setForeignKeyRelationship(sourceForeignKeys.toString());
                        dependency.setCreatedFields(sourceForeignKeys);
                        dependencies.add(dependency);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing field dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует последовательности API вызовов
     */
    private List<ApiDependency> analyzeApiSequences(OpenApiSpecification spec) {
        List<ApiDependency> dependencies = new ArrayList<>();
        
        try {
            // Статический анализ очевидных последовательностей
            if (spec.getEndpoints() != null) {
                for (ApiEndpoint endpoint : spec.getEndpoints()) {
                    // Проверяем наличие POST followed by GET patterns
                    if (hasPostMethod(spec.getEndpoints(), endpoint.getPath()) && 
                        hasGetMethod(spec.getEndpoints(), endpoint.getPath())) {
                        ApiDependency dependency = new ApiDependency(
                            endpoint.getPath(), endpoint.getPath(), "POST", "GET", 
                            ApiDependency.DependencyType.SEQUENCE
                        );
                        dependency.setSequenceDependencies(Arrays.asList("Create", "Read"));
                        dependencies.add(dependency);
                    }
                    
                    // Проверяем наличие PUT/DELETE followed by GET patterns
                    if (hasPutOrDeleteMethod(spec.getEndpoints(), endpoint.getPath()) && 
                        hasGetMethod(spec.getEndpoints(), endpoint.getPath())) {
                        ApiDependency dependency = new ApiDependency(
                            endpoint.getPath(), endpoint.getPath(), "PUT", "GET", 
                            ApiDependency.DependencyType.SEQUENCE
                        );
                        dependency.setSequenceDependencies(Arrays.asList("Update", "Read"));
                        dependencies.add(dependency);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing API sequences", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует зависимости справочных данных
     */
    private List<ApiDependency> analyzeReferenceDataDependencies(OpenApiSpecification spec) {
        List<ApiDependency> dependencies = new ArrayList<>();
        
        try {
            if (spec.getEndpoints() == null) return dependencies;
            
            // Ищем endpoints, которые могут быть справочными
            List<String> referenceEndpoints = new ArrayList<>();
            List<String> dataEndpoints = new ArrayList<>();
            
            for (ApiEndpoint endpoint : spec.getEndpoints()) {
                // Проверяем, является ли endpoint справочным
                if (isReferenceDataEndpoint(endpoint.getPath(), endpoint.getMethod().toString())) {
                    referenceEndpoints.add(endpoint.getPath());
                } else {
                    dataEndpoints.add(endpoint.getPath());
                }
            }
            
            // Создаем зависимости от справочных данных
            for (String referenceEndpoint : referenceEndpoints) {
                for (String dataEndpoint : dataEndpoints) {
                    ApiDependency dependency = new ApiDependency(
                        referenceEndpoint, dataEndpoint, "GET", "GET", ApiDependency.DependencyType.MASTER_DATA
                    );
                    
                    dependency.setMasterDataReference(referenceEndpoint);
                    dependencies.add(dependency);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing reference data dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует с помощью OpenApiAnalysisService
     */
    private List<ApiDependency> analyzeWithOpenApiService(String specId, OpenApiSpecification spec) {
        try {
            // Используем существующий OpenApiAnalysisService для получения данных
            CompletableFuture<OpenApiAnalysisService.AnalysisResult> analysisFuture =
                openApiAnalysisService.analyzeSpecification(specId, spec);
            
            OpenApiAnalysisService.AnalysisResult result = analysisFuture.get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            
            List<ApiDependency> dependencies = new ArrayList<>();
            
            // Извлекаем зависимости из результата анализа
            if (result.getComprehensiveAnalysis() != null && 
                result.getComprehensiveAnalysis().getComprehensiveData() != null) {
                
                Map<String, Object> comprehensiveData = result.getComprehensiveAnalysis().getComprehensiveData();
                
                // Ищем информацию о зависимостях в comprehensive data
                Object dependenciesObj = comprehensiveData.get("apiDependencies");
                if (dependenciesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> dependenciesList = (List<Map<String, Object>>) dependenciesObj;
                    
                    for (Map<String, Object> depData : dependenciesList) {
                        ApiDependency dependency = convertMapToApiDependency(depData);
                        if (dependency != null) {
                            dependencies.add(dependency);
                        }
                    }
                }
            }
            
            return dependencies;
            
        } catch (Exception e) {
            logger.error("Error analyzing with OpenApiAnalysisService", e);
            return Collections.emptyList();
        }
    }
    
    // Вспомогательные методы
    
    private ApiDependency.DependencyType determineDirectDependencyType(String sourceMethod, String targetMethod) {
        if (sourceMethod.equals("POST") && targetMethod.equals("GET")) {
            return ApiDependency.DependencyType.DIRECT_RESPONSE;
        } else if (sourceMethod.equals("PUT") && targetMethod.equals("GET")) {
            return ApiDependency.DependencyType.DIRECT_RESPONSE;
        } else if (sourceMethod.equals("DELETE") && targetMethod.equals("GET")) {
            return ApiDependency.DependencyType.DIRECT_RESPONSE;
        }
        return null;
    }
    
    private Set<String> extractResponseFields(ApiEndpoint endpoint) {
        Set<String> fields = new HashSet<>();
        
        if (endpoint.getResponses() != null) {
            for (Object responseObj : endpoint.getResponses().values()) {
                if (responseObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = (Map<String, Object>) responseObj;
                    Object content = response.get("content");
                    if (content instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> contentMap = (Map<String, Object>) content;
                        for (Object mediaTypeObj : contentMap.values()) {
                            if (mediaTypeObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> mediaType = (Map<String, Object>) mediaTypeObj;
                                Object schema = mediaType.get("schema");
                                if (schema instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> schemaMap = (Map<String, Object>) schema;
                                    fields.addAll(extractSchemaFieldsFromMap(schemaMap));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return fields;
    }
    
    private Set<String> extractRequestFields(ApiEndpoint endpoint) {
        Set<String> fields = new HashSet<>();
        
        if (endpoint.getRequestBody() != null) {
            Object content = endpoint.getRequestBody().get("content");
            if (content instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> contentMap = (Map<String, Object>) content;
                for (Object mediaTypeObj : contentMap.values()) {
                    if (mediaTypeObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> mediaType = (Map<String, Object>) mediaTypeObj;
                        Object schema = mediaType.get("schema");
                        if (schema instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> schemaMap = (Map<String, Object>) schema;
                            fields.addAll(extractSchemaFieldsFromMap(schemaMap));
                        }
                    }
                }
            }
        }
        
        // Добавляем параметры
        if (endpoint.getParameters() != null) {
            for (ApiParameter param : endpoint.getParameters()) {
                fields.add(param.getName());
            }
        }
        
        return fields;
    }
    
    private Set<String> extractSchemaFieldsFromMap(Map<String, Object> schema) {
        Set<String> fields = new HashSet<>();
        
        Object properties = schema.get("properties");
        if (properties instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> propertiesMap = (Map<String, Object>) properties;
            fields.addAll(propertiesMap.keySet());
            
            // Рекурсивно добавляем вложенные свойства
            for (Object propertyObj : propertiesMap.values()) {
                if (propertyObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> property = (Map<String, Object>) propertyObj;
                    Object nestedProperties = property.get("properties");
                    if (nestedProperties instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> nestedPropertiesMap = (Map<String, Object>) nestedProperties;
                        fields.addAll(extractSchemaFieldsFromMap(nestedPropertiesMap));
                    }
                }
            }
        }
        
        return fields;
    }
    
    private Set<String> findSharedFields(Set<String> sourceFields, Set<String> targetFields) {
        return sourceFields.stream()
            .filter(targetFields::contains)
            .collect(Collectors.toSet());
    }
    
    private boolean isRelatedIdField(String sourceId, String targetId) {
        String sourceBase = sourceId.replaceAll("[Ii]d$|_id$", "").toLowerCase();
        String targetBase = targetId.replaceAll("[Ii]d$|_id$", "").toLowerCase();
        
        return sourceBase.equals(targetBase) || 
               sourceBase.contains(targetBase) || 
               targetBase.contains(sourceBase);
    }
    
    private boolean isReferenceDataEndpoint(String path, String method) {
        String pathLower = path.toLowerCase();
        return (pathLower.contains("lookup") ||
               pathLower.contains("reference") ||
               pathLower.contains("dictionary") ||
               pathLower.contains("enum") ||
               pathLower.contains("config") ||
               pathLower.contains("setting")) && "GET".equals(method);
    }
    
    private boolean hasPostMethod(List<ApiEndpoint> endpoints, String path) {
        return endpoints.stream()
            .anyMatch(e -> path.equals(e.getPath()) && HttpMethod.POST.equals(e.getMethod()));
    }
    
    private boolean hasGetMethod(List<ApiEndpoint> endpoints, String path) {
        return endpoints.stream()
            .anyMatch(e -> path.equals(e.getPath()) && HttpMethod.GET.equals(e.getMethod()));
    }
    
    private boolean hasPutOrDeleteMethod(List<ApiEndpoint> endpoints, String path) {
        return endpoints.stream()
            .anyMatch(e -> path.equals(e.getPath()) && 
                          (HttpMethod.PUT.equals(e.getMethod()) || HttpMethod.DELETE.equals(e.getMethod())));
    }
    
    private ApiDependency convertMapToApiDependency(Map<String, Object> depData) {
        try {
            ApiDependency dependency = new ApiDependency();
            dependency.setSourceEndpoint((String) depData.get("sourceEndpoint"));
            dependency.setTargetEndpoint((String) depData.get("targetEndpoint"));
            dependency.setSourceMethod((String) depData.get("sourceMethod"));
            dependency.setTargetMethod((String) depData.get("targetMethod"));
            
            String typeStr = (String) depData.get("type");
            if (typeStr != null) {
                dependency.setType(ApiDependency.DependencyType.valueOf(typeStr));
            }
            
            return dependency;
        } catch (Exception e) {
            logger.error("Error converting map to ApiDependency", e);
            return null;
        }
    }
}