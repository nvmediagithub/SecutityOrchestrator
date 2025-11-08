package org.example.application.service.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.domain.entities.openapi.ApiSchema;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.OpenApiService;
import org.example.domain.valueobjects.OpenApiVersion;
import org.example.infrastructure.repositories.openapi.OpenApiServiceRepository;
import org.example.infrastructure.repositories.openapi.ApiSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для парсинга и загрузки OpenAPI спецификаций
 */
@Service
public class OpenApiParsingServiceFixed {
    
    private final OpenApiServiceRepository serviceRepository;
    private final ApiSchemaRepository schemaRepository;
    private final ObjectMapper jsonMapper;
    private final RestTemplate restTemplate;
    
    @Autowired
    public OpenApiParsingServiceFixed(OpenApiServiceRepository serviceRepository, 
                                     ApiSchemaRepository schemaRepository) {
        this.serviceRepository = serviceRepository;
        this.schemaRepository = schemaRepository;
        this.jsonMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Парсинг OpenAPI спецификации по URL
     */
    public CompletableFuture<OpenApiParsingResult> parseFromUrl(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Валидация URL
                validateUrl(url);
                
                // Загрузка спецификации
                String specContent = loadSpecificationFromUrl(url);
                
                // Определение формата
                OpenApiFormat format = detectFormat(specContent);
                
                // Парсинг
                return parseSpecification(specContent, url, format);
                
            } catch (Exception e) {
                return OpenApiParsingResult.error("Ошибка загрузки по URL: " + e.getMessage());
            }
        });
    }
    
    /**
     * Парсинг OpenAPI спецификации из строки
     */
    public OpenApiParsingResult parseFromContent(String specContent, String sourceUrl) {
        try {
            // Валидация контента
            validateSpecContent(specContent);
            
            // Определение формата
            OpenApiFormat format = detectFormat(specContent);
            
            // Парсинг
            return parseSpecification(specContent, sourceUrl, format);
            
        } catch (Exception e) {
            return OpenApiParsingResult.error("Ошибка парсинга контента: " + e.getMessage());
        }
    }
    
    /**
     * Парсинг OpenAPI спецификации из файла
     */
    public OpenApiParsingResult parseFromFile(byte[] fileContent, String fileName) {
        try {
            // Валидация файла
            validateFileContent(fileContent, fileName);
            
            // Преобразование в строку
            String specContent = new String(fileContent);
            
            // Определение формата по имени файла
            OpenApiFormat format = detectFormatFromFileName(fileName);
            
            // Парсинг
            return parseSpecification(specContent, fileName, format);
            
        } catch (Exception e) {
            return OpenApiParsingResult.error("Ошибка парсинга файла: " + e.getMessage());
        }
    }
    
    /**
     * Основной метод парсинга спецификации
     */
    private OpenApiParsingResult parseSpecification(String specContent, String sourceUrl, OpenApiFormat format) {
        try {
            // Парсинг JSON/YAML
            JsonNode specNode = parseToJsonNode(specContent, format);
            
            // Валидация структуры
            OpenApiValidationResult validation = validateSpecification(specNode);
            if (!validation.isValid()) {
                return OpenApiParsingResult.error("Невалидная спецификация: " + validation.getErrorMessage());
            }
            
            // Извлечение метаданных
            OpenApiMetadata metadata = extractMetadata(specNode);
            
            // Извлечение эндпоинтов
            List<ApiEndpoint> endpoints = extractEndpoints(specNode);
            
            // Извлечение схем
            List<ApiSchema> schemas = extractSchemas(specNode, metadata.getApiVersion());
            
            // Создание сервиса
            OpenApiService service = createService(metadata, sourceUrl);
            
            // Сохранение в базу
            OpenApiService savedService = saveService(service, endpoints, schemas);
            
            return OpenApiParsingResult.success(savedService, endpoints.size(), schemas.size());
            
        } catch (Exception e) {
            return OpenApiParsingResult.error("Ошибка парсинга: " + e.getMessage());
        }
    }
    
    /**
     * Вспомогательные методы
     */
    private void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный URL: " + url);
        }
    }
    
    private String loadSpecificationFromUrl(String url) {
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Не удалось загрузить спецификацию: " + e.getMessage());
        }
    }
    
    private void validateSpecContent(String specContent) {
        if (specContent == null || specContent.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пустое содержимое спецификации");
        }
    }
    
    private void validateFileContent(byte[] fileContent, String fileName) {
        if (fileContent == null || fileContent.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пустой файл: " + fileName);
        }
    }
    
    private OpenApiFormat detectFormat(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("{")) {
            return OpenApiFormat.JSON;
        } else if (trimmed.startsWith("openapi:") || trimmed.startsWith("swagger:")) {
            return OpenApiFormat.YAML;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неподдерживаемый формат спецификации");
    }
    
    private OpenApiFormat detectFormatFromFileName(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".json")) {
            return OpenApiFormat.JSON;
        } else if (lowerName.endsWith(".yaml") || lowerName.endsWith(".yml")) {
            return OpenApiFormat.YAML;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
            "Неподдерживаемый формат файла: " + fileName);
    }
    
    private JsonNode parseToJsonNode(String content, OpenApiFormat format) {
        try {
            if (format == OpenApiFormat.JSON) {
                return jsonMapper.readTree(content);
            } else {
                // Упрощенная обработка YAML - конвертируем в JSON парсер
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                return yamlMapper.readTree(content);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Ошибка парсинга " + format.name() + ": " + e.getMessage());
        }
    }
    
    private OpenApiValidationResult validateSpecification(JsonNode specNode) {
        // Базовая валидация структуры
        JsonNode openapiNode = specNode.get("openapi");
        JsonNode swaggerNode = specNode.get("swagger");
        JsonNode infoNode = specNode.get("info");
        
        if (openapiNode == null && swaggerNode == null) {
            return OpenApiValidationResult.invalid("Отсутствует поле 'openapi' или 'swagger'");
        }
        
        if (infoNode == null || infoNode.get("title") == null) {
            return OpenApiValidationResult.invalid("Отсутствует информация о сервисе (info.title)");
        }
        
        return OpenApiValidationResult.valid();
    }
    
    private OpenApiMetadata extractMetadata(JsonNode specNode) {
        OpenApiMetadata metadata = new OpenApiMetadata();
        
        // Версия OpenAPI
        JsonNode openapiNode = specNode.get("openapi");
        JsonNode swaggerNode = specNode.get("swagger");
        
        if (openapiNode != null) {
            String version = openapiNode.asText();
            metadata.setApiVersion(OpenApiVersion.V3_0_0); // Упрощенно
        } else if (swaggerNode != null) {
            String version = swaggerNode.asText();
            metadata.setApiVersion(OpenApiVersion.V2_0);
        }
        
        // Информация о сервисе
        JsonNode infoNode = specNode.get("info");
        if (infoNode != null) {
            metadata.setTitle(getTextNode(infoNode, "title"));
            metadata.setDescription(getTextNode(infoNode, "description"));
            metadata.setVersion(getTextNode(infoNode, "version"));
            metadata.setTermsOfService(getTextNode(infoNode, "termsOfService"));
            metadata.setContact(getTextNode(infoNode, "contact"));
            metadata.setLicense(getTextNode(infoNode, "license"));
        }
        
        // Базовый URL
        JsonNode serversNode = specNode.get("servers");
        if (serversNode != null && serversNode.isArray() && serversNode.size() > 0) {
            metadata.setBaseUrl(getTextNode(serversNode.get(0), "url"));
        }
        
        return metadata;
    }
    
    private List<ApiEndpoint> extractEndpoints(JsonNode specNode) {
        List<ApiEndpoint> endpoints = new ArrayList<>();
        
        JsonNode pathsNode = specNode.get("paths");
        if (pathsNode == null || !pathsNode.isObject()) {
            return endpoints;
        }
        
        Iterator<Map.Entry<String, JsonNode>> pathsIterator = pathsNode.fields();
        while (pathsIterator.hasNext()) {
            Map.Entry<String, JsonNode> pathEntry = pathsIterator.next();
            String path = pathEntry.getKey();
            JsonNode pathItem = pathEntry.getValue();
            
            // Извлечение HTTP методов
            extractHttpMethods(path, pathItem, endpoints);
        }
        
        return endpoints;
    }
    
    private void extractHttpMethods(String path, JsonNode pathItem, List<ApiEndpoint> endpoints) {
        // Список HTTP методов
        String[] methods = {"get", "post", "put", "delete", "patch", "head", "options", "trace"};
        
        for (String method : methods) {
            JsonNode operationNode = pathItem.get(method);
            if (operationNode != null && operationNode.isObject()) {
                ApiEndpoint endpoint = createEndpointFromNode(path, method, operationNode);
                endpoints.add(endpoint);
            }
        }
    }
    
    private ApiEndpoint createEndpointFromNode(String path, String httpMethod, JsonNode operationNode) {
        ApiEndpoint endpoint = new ApiEndpoint();
        
        endpoint.setPath(path);
        
        try {
            org.example.domain.valueobjects.HttpMethod method = 
                org.example.domain.valueobjects.HttpMethod.valueOf(httpMethod.toUpperCase());
            endpoint.setMethod(method);
        } catch (IllegalArgumentException e) {
            // Игнорируем неизвестные методы
        }
        
        endpoint.setSummary(getTextNode(operationNode, "summary"));
        endpoint.setDescription(getTextNode(operationNode, "description"));
        endpoint.setOperationId(getTextNode(operationNode, "operationId"));
        
        // Теги
        JsonNode tagsNode = operationNode.get("tags");
        if (tagsNode != null && tagsNode.isArray()) {
            List<String> tags = new ArrayList<>();
            for (JsonNode tagNode : tagsNode) {
                if (tagNode.isTextual()) {
                    tags.add(tagNode.asText());
                }
            }
            endpoint.setTags(tags);
        }
        
        // Параметры
        extractParameters(operationNode, endpoint);
        
        return endpoint;
    }
    
    private void extractParameters(JsonNode operationNode, ApiEndpoint endpoint) {
        // Извлечение параметров из operation
        JsonNode parametersNode = operationNode.get("parameters");
        if (parametersNode != null && parametersNode.isArray()) {
            for (JsonNode paramNode : parametersNode) {
                if (paramNode.isObject()) {
                    // Создание простого параметра
                    // В реальной реализации нужно создать полноценный ApiParameter
                }
            }
        }
    }
    
    private List<ApiSchema> extractSchemas(JsonNode specNode, OpenApiVersion version) {
        List<ApiSchema> schemas = new ArrayList<>();
        
        JsonNode componentsNode = null;
        JsonNode definitionsNode = null;
        
        if (version.ordinal() >= OpenApiVersion.V3_0_0.ordinal()) {
            // OpenAPI 3.x
            componentsNode = specNode.get("components");
            if (componentsNode != null) {
                componentsNode = componentsNode.get("schemas");
            }
        } else {
            // OpenAPI 2.0
            definitionsNode = specNode.get("definitions");
            if (definitionsNode != null) {
                componentsNode = definitionsNode;
            }
        }
        
        if (componentsNode != null && componentsNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> schemaIterator = componentsNode.fields();
            while (schemaIterator.hasNext()) {
                Map.Entry<String, JsonNode> schemaEntry = schemaIterator.next();
                ApiSchema schema = createSchemaFromNode(schemaEntry.getKey(), schemaEntry.getValue());
                schemas.add(schema);
            }
        }
        
        return schemas;
    }
    
    private ApiSchema createSchemaFromNode(String name, JsonNode schemaNode) {
        ApiSchema schema = new ApiSchema();
        
        schema.setName(name);
        schema.setType(getTextNode(schemaNode, "type"));
        schema.setFormat(getTextNode(schemaNode, "format"));
        schema.setDescription(getTextNode(schemaNode, "description"));
        schema.setTitle(getTextNode(schemaNode, "title"));
        
        // Обязательные поля
        JsonNode requiredNode = schemaNode.get("required");
        if (requiredNode != null && requiredNode.isArray()) {
            List<String> requiredFields = new ArrayList<>();
            for (JsonNode reqNode : requiredNode) {
                if (reqNode.isTextual()) {
                    requiredFields.add(reqNode.asText());
                }
            }
            schema.setRequiredFields(requiredFields);
        }
        
        // Свойства
        JsonNode propertiesNode = schemaNode.get("properties");
        if (propertiesNode != null && propertiesNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> propsIterator = propertiesNode.fields();
            while (propsIterator.hasNext()) {
                Map.Entry<String, JsonNode> propEntry = propsIterator.next();
                JsonNode propNode = propEntry.getValue();
                String propertyValue = String.format("type: %s, format: %s, description: %s",
                    getTextNode(propNode, "type"),
                    getTextNode(propNode, "format"),
                    getTextNode(propNode, "description"));
                schema.addProperty(propEntry.getKey(), propertyValue);
            }
        }
        
        return schema;
    }
    
    private OpenApiService createService(OpenApiMetadata metadata, String sourceUrl) {
        OpenApiService service = new OpenApiService();
        service.setServiceName(metadata.getTitle().toLowerCase().replace(" ", "_"));
        service.setServiceTitle(metadata.getTitle());
        service.setServiceVersion(metadata.getVersion());
        service.setDescription(metadata.getDescription());
        service.setBaseUrl(metadata.getBaseUrl());
        service.setSpecificationUrl(sourceUrl);
        service.setOpenApiVersion(metadata.getApiVersion());
        service.setParseStatus(OpenApiService.ParseStatus.PENDING);
        service.setIsActive(true);
        
        return service;
    }
    
    private OpenApiService saveService(OpenApiService service, List<ApiEndpoint> endpoints, List<ApiSchema> schemas) {
        // Проверяем существование сервиса
        Optional<OpenApiService> existingService = serviceRepository.findByServiceName(service.getServiceName());
        if (existingService.isPresent()) {
            service.setId(existingService.get().getId());
        }
        
        // Сохраняем сервис
        OpenApiService savedService = serviceRepository.save(service);
        
        // Сохраняем схемы
        for (ApiSchema schema : schemas) {
            schema.setService(savedService);
            schemaRepository.save(schema);
        }
        
        // Обновляем сервис с эндпоинтами
        savedService.setEndpoints(endpoints);
        for (ApiEndpoint endpoint : endpoints) {
            // В реальной реализации нужно сохранить эндпоинты
        }
        
        // Обновляем статус парсинга
        savedService.setParseStatus(OpenApiService.ParseStatus.SUCCESS);
        savedService.setLastParsedAt(LocalDateTime.now());
        
        return serviceRepository.save(savedService);
    }
    
    private String getTextNode(JsonNode parent, String field) {
        JsonNode node = parent.get(field);
        return node != null && node.isTextual() ? node.asText() : null;
    }
    
    // Вспомогательные классы
    public enum OpenApiFormat {
        JSON, YAML
    }
    
    public static class OpenApiMetadata {
        private String title;
        private String description;
        private String version;
        private String termsOfService;
        private String contact;
        private String license;
        private String baseUrl;
        private OpenApiVersion openApiVersion;
        
        // Геттеры и сеттеры
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getTermsOfService() { return termsOfService; }
        public void setTermsOfService(String termsOfService) { this.termsOfService = termsOfService; }
        
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        
        public String getLicense() { return license; }
        public void setLicense(String license) { this.license = license; }
        
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        
        public OpenApiVersion getApiVersion() { return openApiVersion; }
        public void setApiVersion(OpenApiVersion openApiVersion) { this.openApiVersion = openApiVersion; }
    }
    
    public static class OpenApiValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private OpenApiValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static OpenApiValidationResult valid() {
            return new OpenApiValidationResult(true, null);
        }
        
        public static OpenApiValidationResult invalid(String errorMessage) {
            return new OpenApiValidationResult(false, errorMessage);
        }
        
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class OpenApiParsingResult {
        private final boolean success;
        private final String errorMessage;
        private final OpenApiService service;
        private final int endpointsCount;
        private final int schemasCount;
        
        private OpenApiParsingResult(boolean success, String errorMessage, OpenApiService service, 
                                   int endpointsCount, int schemasCount) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.service = service;
            this.endpointsCount = endpointsCount;
            this.schemasCount = schemasCount;
        }
        
        public static OpenApiParsingResult success(OpenApiService service, int endpointsCount, int schemasCount) {
            return new OpenApiParsingResult(true, null, service, endpointsCount, schemasCount);
        }
        
        public static OpenApiParsingResult error(String errorMessage) {
            return new OpenApiParsingResult(false, errorMessage, null, 0, 0);
        }
        
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public OpenApiService getService() { return service; }
        public int getEndpointsCount() { return endpointsCount; }
        public int getSchemasCount() { return schemasCount; }
    }
}