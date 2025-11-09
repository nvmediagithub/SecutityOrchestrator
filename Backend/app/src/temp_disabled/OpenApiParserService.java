package org.example.infrastructure.services.openapi;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.ApiSchema;
import org.example.domain.entities.openapi.ApiSecurityScheme;
import org.example.domain.valueobjects.OpenApiVersion;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.valueobjects.Version;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Упрощенный сервис для парсинга OpenAPI спецификаций
 */
@Service
public class OpenApiParserService {
    
    /**
     * Парсинг OpenAPI спецификации из строки
     */
    public OpenApiSpecification parseSpecification(String specContent, String format) {
        try {
            Map<String, Object> specMap = parseJson(specContent);
            return buildOpenApiSpecification(specMap);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга OpenAPI спецификации: " + e.getMessage(), e);
        }
    }
    
    /**
     * Парсинг OpenAPI спецификации из URL
     */
    public OpenApiSpecification parseFromUrl(String specUrl) {
        try {
            // Упрощенная реализация - в реальном проекте нужно использовать RestTemplate
            throw new RuntimeException("Парсинг по URL не реализован");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки спецификации по URL: " + e.getMessage(), e);
        }
    }
    
    /**
     * Парсинг OpenAPI спецификации из файла
     */
    public OpenApiSpecification parseFromFile(byte[] fileContent, String fileName) {
        try {
            String content = new String(fileContent);
            String format = detectFormatFromFileName(fileName);
            return parseSpecification(content, format);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга файла спецификации: " + e.getMessage(), e);
        }
    }
    
    /**
     * Парсинг JSON формата (упрощенный)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String jsonContent) {
        // Упрощенный JSON парсер - в реальном проекте использовать Jackson/Gson
        Map<String, Object> result = new HashMap<>();
        
        // Простейший парсинг для демонстрации
        if (jsonContent.contains("\"openapi\"") || jsonContent.contains("'openapi'")) {
            result.put("openapi", "3.0.1");
        } else if (jsonContent.contains("\"swagger\"") || jsonContent.contains("'swagger'")) {
            result.put("swagger", "2.0");
        }
        
        result.put("title", "Demo API");
        result.put("version", "1.0.0");
        result.put("description", "Демонстрационная API спецификация");
        
        // Добавляем базовые paths
        Map<String, Object> paths = new HashMap<>();
        Map<String, Object> getEndpoint = new HashMap<>();
        getEndpoint.put("summary", "Получить информацию");
        getEndpoint.put("description", "Возвращает информацию о ресурсе");
        paths.put("/api/info", getEndpoint);
        result.put("paths", paths);
        
        return result;
    }
    
    /**
     * Построение OpenApiSpecification из распарсенной карты
     */
    @SuppressWarnings("unchecked")
    private OpenApiSpecification buildOpenApiSpecification(Map<String, Object> specMap) {
        OpenApiSpecification spec = new OpenApiSpecification();
        
        // Основная информация
        String openApiVersion = getString(specMap.get("openapi"));
        String swaggerVersion = getString(specMap.get("swagger"));
        String title = getString(specMap.get("title"));
        String description = getString(specMap.get("description"));
        
        // Определяем версию OpenAPI
        OpenApiVersion apiVersion = OpenApiVersion.V3_0; // По умолчанию
        if (swaggerVersion != null && "2.0".equals(swaggerVersion)) {
            apiVersion = OpenApiVersion.V2_0;
        }
        
        // Устанавливаем базовые свойства
        spec.setId(new SpecificationId("spec-" + System.currentTimeMillis()));
        spec.setTitle(title != null ? title : "Untitled API");
        spec.setDescription(description);
        spec.setVersion(new Version(1, 0, 0));
        spec.setOpenApiVersion(apiVersion);
        spec.setRawContent(specMap.toString());
        spec.setOriginalSpec(specMap);
        spec.setValid(true);
        
        // Парсим эндпоинты
        List<ApiEndpoint> endpoints = parseEndpoints(specMap, apiVersion);
        spec.setEndpoints(endpoints);
        
        // Парсим схемы
        List<ApiSchema> schemas = parseSchemas(specMap, apiVersion);
        spec.setSchemas(schemas);
        
        // Парсим схемы безопасности
        List<ApiSecurityScheme> securitySchemes = parseSecuritySchemes(specMap, apiVersion);
        spec.setSecuritySchemes(securitySchemes);
        
        // Парсим серверы
        Map<String, Object> servers = parseServers(specMap);
        spec.setServers(servers);
        
        // Парсим теги
        Map<String, Object> tags = parseTags(specMap);
        spec.setTags(tags);
        
        return spec;
    }
    
    /**
     * Парсинг эндпоинтов
     */
    @SuppressWarnings("unchecked")
    private List<ApiEndpoint> parseEndpoints(Map<String, Object> specMap, OpenApiVersion version) {
        List<ApiEndpoint> endpoints = new ArrayList<>();
        
        Map<String, Object> paths = (Map<String, Object>) specMap.get("paths");
        if (paths == null) return endpoints;
        
        for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
            String path = pathEntry.getKey();
            Map<String, Object> pathItem = (Map<String, Object>) pathEntry.getValue();
            
            // HTTP методы
            for (String httpMethod : Arrays.asList("get", "post", "put", "delete", "patch")) {
                Map<String, Object> operation = (Map<String, Object>) pathItem.get(httpMethod);
                if (operation != null) {
                    ApiEndpoint endpoint = new ApiEndpoint();
                    endpoint.setPath(path);
                    
                    // Используем HttpMethod из valueobjects
                    try {
                        org.example.domain.valueobjects.HttpMethod method = 
                            org.example.domain.valueobjects.HttpMethod.valueOf(httpMethod.toUpperCase());
                        endpoint.setMethod(method);
                    } catch (IllegalArgumentException e) {
                        // Игнорируем неизвестные методы
                        continue;
                    }
                    
                    endpoint.setSummary(getString(operation.get("summary")));
                    endpoint.setDescription(getString(operation.get("description")));
                    endpoint.setOperationId(getString(operation.get("operationId")));
                    
                    // Параметры
                    List<org.example.domain.entities.openapi.ApiParameter> parameters = parseParameters(operation);
                    endpoint.setParameters(parameters);
                    
                    // Безопасность
                    List<String> security = parseSecurity(operation);
                    endpoint.setSecurity(security);
                    
                    // Теги
                    List<String> tags = parseTagsList(operation);
                    endpoint.setTags(tags);
                    
                    endpoints.add(endpoint);
                }
            }
        }
        
        return endpoints;
    }
    
    /**
     * Парсинг схем
     */
    @SuppressWarnings("unchecked")
    private List<ApiSchema> parseSchemas(Map<String, Object> specMap, OpenApiVersion version) {
        List<ApiSchema> schemas = new ArrayList<>();
        
        // Создаем базовую схему для демонстрации
        ApiSchema userSchema = new ApiSchema();
        userSchema.setName("User");
        userSchema.setType("object");
        userSchema.setDescription("Пользователь системы");
        userSchema.setRequiredFields(Arrays.asList("id", "name", "email"));
        
        Map<String, ApiSchema> properties = new HashMap<>();
        
        ApiSchema idSchema = new ApiSchema();
        idSchema.setName("id");
        idSchema.setType("integer");
        idSchema.setDescription("Идентификатор пользователя");
        properties.put("id", idSchema);
        
        ApiSchema nameSchema = new ApiSchema();
        nameSchema.setName("name");
        nameSchema.setType("string");
        nameSchema.setDescription("Имя пользователя");
        properties.put("name", nameSchema);
        
        ApiSchema emailSchema = new ApiSchema();
        emailSchema.setName("email");
        emailSchema.setType("string");
        emailSchema.setDescription("Email пользователя");
        properties.put("email", emailSchema);
        
        userSchema.setProperties(properties);
        schemas.add(userSchema);
        
        return schemas;
    }
    
    /**
     * Парсинг схем безопасности
     */
    @SuppressWarnings("unchecked")
    private List<ApiSecurityScheme> parseSecuritySchemes(Map<String, Object> specMap, OpenApiVersion version) {
        List<ApiSecurityScheme> securitySchemes = new ArrayList<>();
        
        // Создаем базовую схему безопасности
        ApiSecurityScheme bearerScheme = new ApiSecurityScheme();
        bearerScheme.setName("BearerAuth");
        bearerScheme.setType("http");
        bearerScheme.setScheme("bearer");
        bearerScheme.setBearerFormat("JWT");
        bearerScheme.setDescription("JWT Token Authentication");
        
        securitySchemes.add(bearerScheme);
        
        return securitySchemes;
    }
    
    /**
     * Парсинг параметров
     */
    @SuppressWarnings("unchecked")
    private List<org.example.domain.entities.openapi.ApiParameter> parseParameters(Map<String, Object> operation) {
        List<org.example.domain.entities.openapi.ApiParameter> parameters = new ArrayList<>();
        
        List<Map<String, Object>> paramsList = (List<Map<String, Object>>) operation.get("parameters");
        if (paramsList != null) {
            for (Map<String, Object> paramMap : paramsList) {
                org.example.domain.entities.openapi.ApiParameter param = 
                    new org.example.domain.entities.openapi.ApiParameter();
                param.setName(getString(paramMap.get("name")));
                param.setIn(getString(paramMap.get("in")));
                param.setDescription(getString(paramMap.get("description")));
                param.setRequired(Boolean.TRUE.equals(paramMap.get("required")));
                parameters.add(param);
            }
        }
        
        return parameters;
    }
    
    /**
     * Парсинг безопасности операции
     */
    @SuppressWarnings("unchecked")
    private List<String> parseSecurity(Map<String, Object> operation) {
        List<Map<String, Object>> securityList = (List<Map<String, Object>>) operation.get("security");
        if (securityList == null) return new ArrayList<>();
        
        return securityList.stream()
            .flatMap(security -> security.keySet().stream())
            .collect(Collectors.toList());
    }
    
    /**
     * Парсинг тегов
     */
    private List<String> parseTagsList(Map<String, Object> container) {
        Object tags = container.get("tags");
        if (tags instanceof List) {
            return ((List<?>) tags).stream()
                .map(tag -> tag instanceof String ? (String) tag : tag.toString())
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    /**
     * Парсинг серверов
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseServers(Map<String, Object> specMap) {
        List<Map<String, Object>> servers = (List<Map<String, Object>>) specMap.get("servers");
        if (servers == null) return new HashMap<>();
        
        Map<String, Object> serversMap = new HashMap<>();
        for (int i = 0; i < servers.size(); i++) {
            Map<String, Object> server = servers.get(i);
            serversMap.put("server_" + i, server);
        }
        return serversMap;
    }
    
    /**
     * Парсинг тегов
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseTags(Map<String, Object> specMap) {
        List<Map<String, Object>> tags = (List<Map<String, Object>>) specMap.get("tags");
        if (tags == null) return new HashMap<>();
        
        Map<String, Object> tagsMap = new HashMap<>();
        for (Map<String, Object> tag : tags) {
            String name = getString(tag.get("name"));
            if (name != null) {
                tagsMap.put(name, tag);
            }
        }
        return tagsMap;
    }
    
    /**
     * Определение формата по имени файла
     */
    private String detectFormatFromFileName(String fileName) {
        if (fileName != null) {
            String lowerName = fileName.toLowerCase();
            if (lowerName.endsWith(".yaml") || lowerName.endsWith(".yml")) {
                return "yaml";
            } else if (lowerName.endsWith(".json")) {
                return "json";
            }
        }
        return "auto";
    }
    
    /**
     * Вспомогательный метод для безопасного получения строки
     */
    private String getString(Object value) {
        return value != null ? value.toString() : null;
    }
    
    /**
     * Проверка валидности OpenAPI спецификации
     */
    public boolean isValidOpenApiSpec(Map<String, Object> specMap) {
        if (specMap == null) return false;
        
        String openApi = getString(specMap.get("openapi"));
        String swagger = getString(specMap.get("swagger"));
        
        return (openApi != null && (openApi.startsWith("3.") || openApi.startsWith("3.0"))) ||
               (swagger != null && "2.0".equals(swagger));
    }
    
    /**
     * Извлечение версии OpenAPI
     */
    public String extractOpenApiVersion(Map<String, Object> specMap) {
        String openApi = getString(specMap.get("openapi"));
        String swagger = getString(specMap.get("swagger"));
        
        if (openApi != null) {
            return "OpenAPI " + openApi;
        } else if (swagger != null) {
            return "Swagger " + swagger;
        }
        
        return "Unknown";
    }
}