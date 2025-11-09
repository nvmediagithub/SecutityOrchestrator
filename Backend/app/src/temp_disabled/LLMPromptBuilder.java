package org.example.infrastructure.services.llm;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.ApiSchema;
import org.example.domain.entities.openapi.ApiParameter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Построитель специализированных промптов для анализа OpenAPI спецификаций
 * Создает оптимизированные промпты для различных типов анализа
 */
@Service
public class LLMPromptBuilder {
    
    private static final int MAX_PROMPT_LENGTH = 8000;
    private static final int CHUNK_SIZE = 2000;
    
    /**
     * Создает промпт для анализа безопасности
     */
    public String buildSecurityAnalysisPrompt(OpenApiSpecification spec) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Проанализируй OpenAPI спецификацию на предмет уязвимостей безопасности.\n\n");
        prompt.append("**СПЕЦИФИКАЦИЯ:**\n");
        prompt.append("Название: ").append(spec.getTitle()).append("\n");
        if (spec.getDescription() != null) {
            prompt.append("Описание: ").append(spec.getDescription()).append("\n");
        }
        prompt.append("Версия: ").append(spec.getVersion()).append("\n");
        prompt.append("OpenAPI версия: ").append(spec.getOpenApiVersion()).append("\n\n");
        
        if (spec.getEndpoints() != null && !spec.getEndpoints().isEmpty()) {
            prompt.append("**ЭНДПОИНТЫ:**\n");
            for (ApiEndpoint endpoint : spec.getEndpoints()) {
                prompt.append(String.format("- %s %s: %s\n", 
                    endpoint.getMethod(), endpoint.getPath(), 
                    endpoint.getSummary() != null ? endpoint.getSummary() : ""));
                if (endpoint.getParameters() != null) {
                    for (ApiParameter param : endpoint.getParameters()) {
                        prompt.append(String.format("  * Параметр %s (%s): %s\n", 
                            param.getName(), param.getIn(), 
                            param.getDescription() != null ? param.getDescription() : ""));
                    }
                }
                if (endpoint.getSecurity() != null && !endpoint.getSecurity().isEmpty()) {
                    prompt.append(String.format("  * Безопасность: %s\n", 
                        String.join(", ", endpoint.getSecurity())));
                }
            }
            prompt.append("\n");
        }
        
        if (spec.getSecuritySchemes() != null && !spec.getSecuritySchemes().isEmpty()) {
            prompt.append("**СХЕМЫ БЕЗОПАСНОСТИ:**\n");
            for (ApiSecurityScheme scheme : spec.getSecuritySchemes()) {
                prompt.append(String.format("- %s (%s): %s\n", 
                    scheme.getName(), scheme.getType(),
                    scheme.getDescription() != null ? scheme.getDescription() : ""));
            }
            prompt.append("\n");
        }
        
        prompt.append("**ПРОВЕРИТЬ СЛЕДУЮЩИЕ ПРОБЛЕМЫ:**\n");
        prompt.append("1. Эндпоинты без аутентификации\n");
        prompt.append("2. Небезопасные HTTP методы (GET для изменяющих операций)\n");
        prompt.append("3. Утечка чувствительных данных в схемах\n");
        prompt.append("4. Потенциальные SQL injection уязвимости\n");
        prompt.append("5. XSS уязвимости в user input\n");
        prompt.append("6. Отсутствие rate limiting описания\n");
        prompt.append("7. Небезопасные схемы аутентификации\n");
        prompt.append("8. Недостаточная валидация входных данных\n\n");
        
        prompt.append("**ФОРМАТ ОТВЕТА:**\n");
        prompt.append("Верни результат в JSON формате:\n");
        prompt.append("{\n");
        prompt.append("  \"securityIssues\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"type\": \"Тип проблемы\",\n");
        prompt.append("      \"severity\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"endpoint\": \"Эндпоинт\",\n");
        prompt.append("      \"description\": \"Описание проблемы\",\n");
        prompt.append("      \"recommendation\": \"Рекомендации по исправлению\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"overallRisk\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("  \"summary\": \"Общее описание найденных проблем\"\n");
        prompt.append("}\n");
        
        return truncateIfNeeded(prompt.toString());
    }
    
    /**
     * Создает промпт для анализа валидации
     */
    public String buildValidationAnalysisPrompt(OpenApiSpecification spec) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Проанализируй схемы данных и параметры в OpenAPI спецификации.\n\n");
        prompt.append("**СПЕЦИФИКАЦИЯ:**\n");
        prompt.append("Название: ").append(spec.getTitle()).append("\n\n");
        
        if (spec.getSchemas() != null && !spec.getSchemas().isEmpty()) {
            prompt.append("**СХЕМЫ ДАННЫХ:**\n");
            for (ApiSchema schema : spec.getSchemas()) {
                prompt.append(String.format("- %s (%s): %s\n", 
                    schema.getName(), schema.getType(),
                    schema.getDescription() != null ? schema.getDescription() : ""));
                
                if (schema.getProperties() != null && !schema.getProperties().isEmpty()) {
                    prompt.append("  Свойства:\n");
                    for (ApiSchema property : schema.getProperties().values()) {
                        prompt.append(String.format("    * %s (%s)\n", 
                            property.getName(), property.getType()));
                    }
                }
            }
            prompt.append("\n");
        }
        
        if (spec.getEndpoints() != null && !spec.getEndpoints().isEmpty()) {
            prompt.append("**ПАРАМЕТРЫ ЭНДПОИНТОВ:**\n");
            for (ApiEndpoint endpoint : spec.getEndpoints()) {
                if (endpoint.getParameters() != null) {
                    for (ApiParameter param : endpoint.getParameters()) {
                        prompt.append(String.format("- %s %s - Параметр %s (%s): %s\n", 
                            endpoint.getMethod(), endpoint.getPath(),
                            param.getName(), param.getIn(),
                            param.getDescription() != null ? param.getDescription() : ""));
                    }
                }
            }
            prompt.append("\n");
        }
        
        prompt.append("**ПРОВЕРИТЬ СЛЕДУЮЩИЕ ПРОБЛЕМЫ:**\n");
        prompt.append("1. Отсутствие валидации входных параметров\n");
        prompt.append("2. Некорректные типы данных в схемах\n");
        prompt.append("3. Отсутствие required полей\n");
        prompt.append("4. Некорректные регулярные выражения\n");
        prompt.append("5. Отсутствие ограничений на размеры\n");
        prompt.append("6. Некорректные форматы данных\n");
        prompt.append("7. Отсутствие валидации enum значений\n");
        prompt.append("8. Некорректные constraints для числовых полей\n\n");
        
        prompt.append("**ФОРМАТ ОТВЕТА:**\n");
        prompt.append("Верни структурированный анализ:\n");
        prompt.append("{\n");
        prompt.append("  \"validationIssues\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"type\": \"Тип проблемы\",\n");
        prompt.append("      \"severity\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"location\": \"Местоположение (схема/эндпоинт)\",\n");
        prompt.append("      \"field\": \"Поле\",\n");
        prompt.append("      \"description\": \"Описание проблемы\",\n");
        prompt.append("      \"fix\": \"Рекомендуемое исправление\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"schemaQuality\": \"GOOD|FAIR|POOR\",\n");
        prompt.append("  \"recommendations\": [\"список рекомендаций\"]\n");
        prompt.append("}\n");
        
        return truncateIfNeeded(prompt.toString());
    }
    
    /**
     * Создает промпт для анализа согласованности
     */
    public String buildConsistencyAnalysisPrompt(OpenApiSpecification spec) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Проанализируй согласованность OpenAPI спецификации.\n\n");
        prompt.append("**СПЕЦИФИКАЦИЯ:**\n");
        prompt.append("Название: ").append(spec.getTitle()).append("\n");
        if (spec.getDescription() != null) {
            prompt.append("Описание: ").append(spec.getDescription()).append("\n");
        }
        prompt.append("Версия: ").append(spec.getVersion()).append("\n");
        prompt.append("OpenAPI версия: ").append(spec.getOpenApiVersion()).append("\n\n");
        
        if (spec.getEndpoints() != null && !spec.getEndpoints().isEmpty()) {
            prompt.append("**ЭНДПОИНТЫ И ИХ ОПИСАНИЯ:**\n");
            for (ApiEndpoint endpoint : spec.getEndpoints()) {
                prompt.append(String.format("- %s %s: %s\n", 
                    endpoint.getMethod(), endpoint.getPath(), 
                    endpoint.getSummary() != null ? endpoint.getSummary() : ""));
                if (endpoint.getDescription() != null && !endpoint.getDescription().equals(endpoint.getSummary())) {
                    prompt.append(String.format("  Описание: %s\n", endpoint.getDescription()));
                }
                if (endpoint.getTags() != null) {
                    prompt.append(String.format("  Теги: %s\n", String.join(", ", endpoint.getTags())));
                }
            }
            prompt.append("\n");
        }
        
        if (spec.getSchemas() != null && !spec.getSchemas().isEmpty()) {
            prompt.append("**СХЕМЫ:**\n");
            for (ApiSchema schema : spec.getSchemas()) {
                prompt.append(String.format("- %s (%s): %s\n", 
                    schema.getName(), schema.getType(),
                    schema.getDescription() != null ? schema.getDescription() : ""));
            }
            prompt.append("\n");
        }
        
        prompt.append("**ПРОВЕРИТЬ СЛЕДУЮЩИЕ НЕСОГЛАСОВАННОСТИ:**\n");
        prompt.append("1. Несоответствие описаний и схем\n");
        prompt.append("2. Различные версии API в документации\n");
        prompt.append("3. Противоречивые статусы коды\n");
        prompt.append("4. Несоответствие типов параметров\n");
        prompt.append("5. Неконсистентное именование полей\n");
        prompt.append("6. Противоречивые форматы данных\n");
        prompt.append("7. Различные стили документации\n\n");
        
        prompt.append("**ФОРМАТ ОТВЕТА:**\n");
        prompt.append("{\n");
        prompt.append("  \"inconsistencies\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"type\": \"Тип несогласованности\",\n");
        prompt.append("      \"severity\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"location\": \"Местоположение\",\n");
        prompt.append("      \"description\": \"Описание\",\n");
        prompt.append("      \"conflicts\": [\"список конфликтующих элементов\"]\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"consistencyScore\": \"1-10\",\n");
        prompt.append("  \"suggestions\": [\"предложения по улучшению\"]\n");
        prompt.append("}\n");
        
        return truncateIfNeeded(prompt.toString());
    }
    
    /**
     * Создает промпт для комплексного анализа
     */
    public String buildComprehensiveAnalysisPrompt(OpenApiSpecification spec) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Проведи комплексный анализ OpenAPI спецификации.\n\n");
        
        // Добавляем краткую информацию о спецификации
        prompt.append("**СПЕЦИФИКАЦИЯ:**\n");
        prompt.append("Название: ").append(spec.getTitle()).append("\n");
        if (spec.getDescription() != null) {
            prompt.append("Описание: ").append(spec.getDescription()).append("\n");
        }
        prompt.append("Версия: ").append(spec.getVersion()).append("\n");
        prompt.append("OpenAPI версия: ").append(spec.getOpenApiVersion()).append("\n");
        prompt.append("Эндпоинтов: ").append(spec.getEndpoints() != null ? spec.getEndpoints().size() : 0).append("\n");
        prompt.append("Схем: ").append(spec.getSchemas() != null ? spec.getSchemas().size() : 0).append("\n");
        prompt.append("Схем безопасности: ").append(spec.getSecuritySchemes() != null ? spec.getSecuritySchemes().size() : 0).append("\n\n");
        
        // Добавляем краткий обзор эндпоинтов
        if (spec.getEndpoints() != null && !spec.getEndpoints().isEmpty()) {
            prompt.append("**КЛЮЧЕВЫЕ ЭНДПОИНТЫ:**\n");
            spec.getEndpoints().stream()
                .limit(10) // Ограничиваем для экономии токенов
                .forEach(endpoint -> {
                    prompt.append(String.format("- %s %s: %s\n", 
                        endpoint.getMethod(), endpoint.getPath(), 
                        endpoint.getSummary() != null ? endpoint.getSummary() : ""));
                });
            if (spec.getEndpoints().size() > 10) {
                prompt.append(String.format("... и еще %d эндпоинтов\n", spec.getEndpoints().size() - 10));
            }
            prompt.append("\n");
        }
        
        prompt.append("**ПРОВЕСТИ АНАЛИЗ:**\n");
        prompt.append("1. Безопасность - уязвимости, аутентификация, авторизация\n");
        prompt.append("2. Валидация - схемы данных, параметры, ограничения\n");
        prompt.append("3. Согласованность - противоречия, несоответствия\n");
        prompt.append("4. Качество - документация, полнота, стандарты\n");
        prompt.append("5. Производительность - оптимизация, ограничения\n");
        prompt.append("6. Документированность - ясность, полнота описаний\n\n");
        
        prompt.append("**ФОРМАТ ОТВЕТА:**\n");
        prompt.append("{\n");
        prompt.append("  \"overallScore\": \"1-10\",\n");
        prompt.append("  \"grade\": \"A|B|C|D|F\",\n");
        prompt.append("  \"summary\": \"Общее описание\",\n");
        prompt.append("  \"analysis\": {\n");
        prompt.append("    \"security\": {\n");
        prompt.append("      \"score\": \"1-10\",\n");
        prompt.append("      \"issues\": [\"проблемы безопасности\"]\n");
        prompt.append("    },\n");
        prompt.append("    \"validation\": {\n");
        prompt.append("      \"score\": \"1-10\",\n");
        prompt.append("      \"issues\": [\"проблемы валидации\"]\n");
        prompt.append("    },\n");
        prompt.append("    \"consistency\": {\n");
        prompt.append("      \"score\": \"1-10\",\n");
        prompt.append("      \"issues\": [\"проблемы согласованности\"]\n");
        prompt.append("    }\n");
        prompt.append("  },\n");
        prompt.append("  \"recommendations\": [\"список рекомендаций\"]\n");
        prompt.append("}\n");
        
        return truncateIfNeeded(prompt.toString());
    }
    
    /**
     * Создает промпт для анализа отдельного эндпоинта
     */
    public String buildEndpointAnalysisPrompt(ApiEndpoint endpoint) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Проанализируй этот API эндпоинт:\n\n");
        prompt.append(String.format("**ЭНДПОИНТ:** %s %s\n", endpoint.getMethod(), endpoint.getPath()));
        
        if (endpoint.getSummary() != null) {
            prompt.append("**Краткое описание:** ").append(endpoint.getSummary()).append("\n");
        }
        if (endpoint.getDescription() != null) {
            prompt.append("**Описание:** ").append(endpoint.getDescription()).append("\n");
        }
        if (endpoint.getOperationId() != null) {
            prompt.append("**Operation ID:** ").append(endpoint.getOperationId()).append("\n");
        }
        
        if (endpoint.getParameters() != null && !endpoint.getParameters().isEmpty()) {
            prompt.append("**Параметры:**\n");
            for (ApiParameter param : endpoint.getParameters()) {
                prompt.append(String.format("- %s (%s)%s: %s\n", 
                    param.getName(), param.getIn(), 
                    param.isRequired() ? " [required]" : "",
                    param.getDescription() != null ? param.getDescription() : ""));
            }
        }
        
        if (endpoint.getSecurity() != null && !endpoint.getSecurity().isEmpty()) {
            prompt.append(String.format("**Безопасность:** %s\n", String.join(", ", endpoint.getSecurity())));
        }
        
        prompt.append("\n**ФОРМАТ ОТВЕТА:**\n");
        prompt.append("{\n");
        prompt.append("  \"endpoint\": \"").append(endpoint.getMethod()).append(" ").append(endpoint.getPath()).append("\",\n");
        prompt.append("  \"issues\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"безопасность|валидация|согласованность\",\n");
        prompt.append("      \"severity\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"description\": \"описание проблемы\",\n");
        prompt.append("      \"recommendation\": \"рекомендация\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        
        return truncateIfNeeded(prompt.toString());
    }
    
    /**
     * Создает промпт для анализа схемы данных
     */
    public String buildSchemaAnalysisPrompt(ApiSchema schema) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Проанализируй эту схему данных:\n\n");
        prompt.append(String.format("**СХЕМА:** %s (%s)\n", schema.getName(), schema.getType()));
        
        if (schema.getDescription() != null) {
            prompt.append("**Описание:** ").append(schema.getDescription()).append("\n");
        }
        
        if (schema.getRequiredFields() != null && !schema.getRequiredFields().isEmpty()) {
            prompt.append(String.format("**Обязательные поля:** %s\n", 
                String.join(", ", schema.getRequiredFields())));
        }
        
        if (schema.getProperties() != null && !schema.getProperties().isEmpty()) {
            prompt.append("**Свойства:**\n");
            for (ApiSchema property : schema.getProperties().values()) {
                prompt.append(String.format("- %s (%s)%s\n", 
                    property.getName(), property.getType(),
                    schema.getRequiredFields() != null && 
                    schema.getRequiredFields().contains(property.getName()) ? " [required]" : ""));
                if (property.getDescription() != null) {
                    prompt.append(String.format("  %s\n", property.getDescription()));
                }
            }
        }
        
        prompt.append("\n**ФОРМАТ ОТВЕТА:**\n");
        prompt.append("{\n");
        prompt.append("  \"schema\": \"").append(schema.getName()).append("\",\n");
        prompt.append("  \"issues\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"field\": \"название поля\",\n");
        prompt.append("      \"type\": \"проблема\",\n");
        prompt.append("      \"severity\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"description\": \"описание\",\n");
        prompt.append("      \"fix\": \"исправление\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        
        return truncateIfNeeded(prompt.toString());
    }
    
    /**
     * Создает промпт для сравнения эндпоинтов
     */
    public String buildEndpointComparisonPrompt(List<ApiEndpoint> endpoints) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Сравни следующие эндпоинты и найди несогласованности:\n\n");
        
        for (int i = 0; i < endpoints.size() && i < 5; i++) {
            ApiEndpoint endpoint = endpoints.get(i);
            prompt.append(String.format("%d. %s %s: %s\n", 
                i + 1, endpoint.getMethod(), endpoint.getPath(),
                endpoint.getSummary() != null ? endpoint.getSummary() : ""));
        }
        
        prompt.append("\n**ПРОВЕРИТЬ:**\n");
        prompt.append("- Согласованность описаний\n");
        prompt.append("- Единообразие параметров\n");
        prompt.append("- Согласованность типов данных\n");
        prompt.append("- Единообразие схем безопасности\n");
        
        prompt.append("\n**ФОРМАТ ОТВЕТА:**\n");
        prompt.append("{\n");
        prompt.append("  \"comparisons\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"endpoints\": [\"список сравниваемых эндпоинтов\"],\n");
        prompt.append("      \"issue\": \"описание несогласованности\",\n");
        prompt.append("      \"severity\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"suggestion\": \"предложение\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        
        return truncateIfNeeded(prompt.toString());
    }
    
    /**
     * Обрезает промпт если он слишком длинный
     */
    private String truncateIfNeeded(String prompt) {
        if (prompt.length() > MAX_PROMPT_LENGTH) {
            return prompt.substring(0, MAX_PROMPT_LENGTH - 100) + 
                   "\n\n[Промпт обрезан для экономии токенов]";
        }
        return prompt;
    }
    
    /**
     * Разбивает большую спецификацию на части для анализа
     */
    public List<String> splitSpecificationForAnalysis(OpenApiSpecification spec) {
        if (spec.getEndpoints() == null || spec.getEndpoints().size() <= 10) {
            return List.of(buildComprehensiveAnalysisPrompt(spec));
        }
        
        List<String> chunks = new java.util.ArrayList<>();
        
        // Первая часть - общая информация
        StringBuilder firstChunk = new StringBuilder();
        firstChunk.append("**ЧАСТЬ 1: ОБЩАЯ ИНФОРМАЦИЯ**\n\n");
        firstChunk.append(String.format("Спецификация: %s\n", spec.getTitle()));
        firstChunk.append(String.format("Версия: %s\n", spec.getVersion()));
        firstChunk.append(String.format("Всего эндпоинтов: %d\n", spec.getEndpoints().size()));
        firstChunk.append(String.format("Всего схем: %d\n", spec.getSchemas() != null ? spec.getSchemas().size() : 0));
        chunks.add(firstChunk.toString());
        
        // Последующие части - группы эндпоинтов
        List<ApiEndpoint> endpoints = spec.getEndpoints();
        for (int i = 0; i < endpoints.size(); i += CHUNK_SIZE) {
            List<ApiEndpoint> chunk = endpoints.subList(i, Math.min(i + CHUNK_SIZE, endpoints.size()));
            StringBuilder chunkPrompt = new StringBuilder();
            chunkPrompt.append(String.format("**ЧАСТЬ %d: ЭНДПОИНТЫ %d-%d**\n\n", chunks.size() + 1, i + 1, i + chunk.size()));
            
            for (ApiEndpoint endpoint : chunk) {
                chunkPrompt.append(String.format("- %s %s: %s\n", 
                    endpoint.getMethod(), endpoint.getPath(),
                    endpoint.getSummary() != null ? endpoint.getSummary() : ""));
            }
            chunks.add(chunkPrompt.toString());
        }
        
        return chunks;
    }
}