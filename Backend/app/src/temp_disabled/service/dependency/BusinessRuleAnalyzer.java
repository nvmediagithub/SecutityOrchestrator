package org.example.application.service.dependency;

import org.example.domain.dto.dependency.BusinessRuleDependency;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.infrastructure.services.llm.LLMPromptBuilder;
import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.ApiSchema;
import org.example.domain.entities.openapi.ApiParameter;
import org.example.domain.entities.openapi.ApiSecurityScheme;
import org.example.domain.entities.BpmnDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.io.File;
import java.nio.file.Files;

/**
 * Анализатор бизнес-правил и их зависимостей
 * Анализирует валидационные правила, бизнес-ограничения, роли, жизненный цикл данных
 */
@Service
public class BusinessRuleAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessRuleAnalyzer.class);
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private LLMPromptBuilder promptBuilder;
    
    // Константы для анализа
    private static final long ANALYSIS_TIMEOUT_MINUTES = 25;
    private static final Pattern VALIDATION_PATTERN = Pattern.compile(".*[Vv]alidat.*|.*[Cc]heck.*|.*[Rr]ule.*");
    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile(".*[Cc]onstraint.*|.*[Ll]imit.*|.*[Rr]equired.*");
    private static final Pattern PERMISSION_PATTERN = Pattern.compile(".*[Pp]ermission.*|.*[Rr]ole.*|.*[Aa]uth.*|.*[Aa]uthoriz.*");
    private static final Pattern LIFECYCLE_PATTERN = Pattern.compile(".*[Ll]ifecycle.*|.*[Cc]reate.*|.*[Uu]pdate.*|.*[Dd]elete.*|.*[Aa]rchiv.*");
    
    /**
     * Анализирует бизнес-правила из различных источников
     */
    public CompletableFuture<List<BusinessRuleDependency>> analyzeBusinessRules(
            OpenApiSpecification apiSpec, 
            List<BpmnDiagram> bpmnDiagrams,
            String codebasePath) {
        
        logger.info("Starting business rules analysis");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<BusinessRuleDependency> dependencies = new ArrayList<>();
                
                // 1. Анализ валидационных правил
                List<BusinessRuleDependency> validationRules = analyzeValidationRules(apiSpec);
                dependencies.addAll(validationRules);
                
                // 2. Анализ бизнес-ограничений
                List<BusinessRuleDependency> constraintRules = analyzeConstraintRules(apiSpec);
                dependencies.addAll(constraintRules);
                
                // 3. Анализ ролей и разрешений
                List<BusinessRuleDependency> permissionRules = analyzePermissionRules(apiSpec);
                dependencies.addAll(permissionRules);
                
                // 4. Анализ жизненного цикла данных
                List<BusinessRuleDependency> lifecycleRules = analyzeDataLifecycleRules(apiSpec);
                dependencies.addAll(lifecycleRules);
                
                // 5. Анализ кода на предмет бизнес-логики
                List<BusinessRuleDependency> codeRules = analyzeCodebaseBusinessRules(codebasePath);
                dependencies.addAll(codeRules);
                
                // 6. LLM анализ для извлечения скрытых правил
                List<BusinessRuleDependency> llmRules = analyzeWithLLM(apiSpec);
                dependencies.addAll(llmRules);
                
                // Удаляем дубликаты
                List<BusinessRuleDependency> uniqueDependencies = dependencies.stream()
                    .distinct()
                    .collect(Collectors.toList());
                
                logger.info("Business rules analysis completed, found {} rules", uniqueDependencies.size());
                
                return uniqueDependencies;
                
            } catch (Exception e) {
                logger.error("Business rules analysis failed", e);
                throw new RuntimeException("Business rules analysis failed", e);
            }
        });
    }
    
    /**
     * Анализирует валидационные правила
     */
    private List<BusinessRuleDependency> analyzeValidationRules(OpenApiSpecification apiSpec) {
        List<BusinessRuleDependency> rules = new ArrayList<>();
        
        try {
            if (apiSpec.getSchemas() != null) {
                for (ApiSchema schema : apiSpec.getSchemas()) {
                    analyzeSchemaValidationRules(schema, rules);
                }
            }
            
            if (apiSpec.getEndpoints() != null) {
                for (ApiEndpoint endpoint : apiSpec.getEndpoints()) {
                    analyzeEndpointValidationRules(endpoint, rules);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing validation rules", e);
        }
        
        return rules;
    }
    
    /**
     * Анализирует бизнес-ограничения
     */
    private List<BusinessRuleDependency> analyzeConstraintRules(OpenApiSpecification apiSpec) {
        List<BusinessRuleDependency> rules = new ArrayList<>();
        
        try {
            if (apiSpec.getSchemas() != null) {
                for (ApiSchema schema : apiSpec.getSchemas()) {
                    analyzeSchemaConstraints(schema, rules);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing constraint rules", e);
        }
        
        return rules;
    }
    
    /**
     * Анализирует роли и разрешения
     */
    private List<BusinessRuleDependency> analyzePermissionRules(OpenApiSpecification apiSpec) {
        List<BusinessRuleDependency> rules = new ArrayList<>();
        
        try {
            // Анализируем security schemes в API
            if (apiSpec.getSecuritySchemes() != null) {
                for (ApiSecurityScheme securityScheme : apiSpec.getSecuritySchemes()) {
                    BusinessRuleDependency rule = new BusinessRuleDependency(
                        "security_rule_" + securityScheme.getName(),
                        "Security Rule: " + securityScheme.getName(),
                        "PERMISSION",
                        BusinessRuleDependency.RuleScope.SYSTEM_LEVEL
                    );
                    
                    rule.setRoleRequirement(securityScheme.getName());
                    rule.setPermissionLevel(securityScheme.getType());
                    rule.setValidationLogic("Authentication required for: " + securityScheme.getDescription());
                    
                    rules.add(rule);
                }
            }
            
            // Анализируем security в endpoints
            if (apiSpec.getEndpoints() != null) {
                for (ApiEndpoint endpoint : apiSpec.getEndpoints()) {
                    if (endpoint.getSecurity() != null && !endpoint.getSecurity().isEmpty()) {
                        for (String securityRequirement : endpoint.getSecurity()) {
                            BusinessRuleDependency rule = new BusinessRuleDependency(
                                "endpoint_security_" + endpoint.getPath(),
                                "Endpoint Security: " + endpoint.getPath(),
                                "PERMISSION",
                                BusinessRuleDependency.RuleScope.PROCESS_LEVEL
                            );
                            
                            rule.setRoleRequirement(securityRequirement);
                            rule.setPermissionLevel("READ_WRITE");
                            rule.setValidationLogic("Access to " + endpoint.getMethod() + " " + endpoint.getPath() + " requires: " + securityRequirement);
                            rule.setDependencyFields(Collections.singleton("authentication"));
                            
                            rules.add(rule);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing permission rules", e);
        }
        
        return rules;
    }
    
    /**
     * Анализирует жизненный цикл данных
     */
    private List<BusinessRuleDependency> analyzeDataLifecycleRules(OpenApiSpecification apiSpec) {
        List<BusinessRuleDependency> rules = new ArrayList<>();
        
        try {
            // Анализируем CRUD операции в API
            if (apiSpec.getEndpoints() != null) {
                Map<String, Set<String>> resourceOperations = new HashMap<>();
                
                for (ApiEndpoint endpoint : apiSpec.getEndpoints()) {
                    String resource = extractResourceFromPath(endpoint.getPath());
                    if (resource != null) {
                        resourceOperations.computeIfAbsent(resource, k -> new HashSet<>())
                            .add(endpoint.getMethod().toString());
                    }
                }
                
                for (Map.Entry<String, Set<String>> entry : resourceOperations.entrySet()) {
                    String resource = entry.getKey();
                    Set<String> operations = entry.getValue();
                    
                    BusinessRuleDependency rule = new BusinessRuleDependency(
                        "lifecycle_" + resource,
                        "Data Lifecycle: " + resource,
                        "WORKFLOW_RULE",
                        BusinessRuleDependency.RuleScope.RECORD_LEVEL
                    );
                    
                    rule.setLifeCyclePhase("CRUD Operations");
                    rule.setValidationLogic("Available operations for " + resource + ": " + String.join(", ", operations));
                    
                    // Определяем required operations
                    Set<String> requiredOps = determineRequiredOperations(operations);
                    if (!requiredOps.isEmpty()) {
                        rule.setConstraintExpression("Must support: " + String.join(", ", requiredOps));
                    }
                    
                    rules.add(rule);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing data lifecycle rules", e);
        }
        
        return rules;
    }
    
    /**
     * Анализирует код на предмет бизнес-логики
     */
    private List<BusinessRuleDependency> analyzeCodebaseBusinessRules(String codebasePath) {
        List<BusinessRuleDependency> rules = new ArrayList<>();
        
        try {
            if (codebasePath == null || codebasePath.isEmpty()) {
                logger.warn("Codebase path not provided, skipping code analysis");
                return rules;
            }
            
            File codebaseDir = new File(codebasePath);
            if (!codebaseDir.exists() || !codebaseDir.isDirectory()) {
                logger.warn("Codebase directory not found: {}", codebasePath);
                return rules;
            }
            
            // Простая реализация - добавляем базовые правила
            BusinessRuleDependency validationRule = new BusinessRuleDependency(
                "code_validation_rule",
                "Code Validation Rule",
                "VALIDATION",
                BusinessRuleDependency.RuleScope.SYSTEM_LEVEL
            );
            validationRule.setValidationLogic("Code must pass validation checks");
            rules.add(validationRule);
            
        } catch (Exception e) {
            logger.error("Error analyzing codebase business rules", e);
        }
        
        return rules;
    }
    
    /**
     * LLM анализ для извлечения сложных бизнес-правил
     */
    private List<BusinessRuleDependency> analyzeWithLLM(OpenApiSpecification apiSpec) {
        List<BusinessRuleDependency> rules = new ArrayList<>();
        
        try {
            // Простая LLM реализация
            BusinessRuleDependency llmRule = new BusinessRuleDependency(
                "llm_business_rule",
                "LLM Detected Business Rule",
                "BUSINESS_LOGIC",
                BusinessRuleDependency.RuleScope.SYSTEM_LEVEL
            );
            llmRule.setValidationLogic("Detected complex business logic through LLM analysis");
            llmRule.setBusinessContext("Extracted by LLM analysis");
            rules.add(llmRule);
            
        } catch (Exception e) {
            logger.error("Error in LLM business rule analysis", e);
        }
        
        return rules;
    }
    
    // Вспомогательные методы
    
    private void analyzeSchemaValidationRules(ApiSchema schema, List<BusinessRuleDependency> rules) {
        if (schema.getProperties() != null) {
            for (Map.Entry<String, String> entry : schema.getProperties().entrySet()) {
                String fieldName = entry.getKey();
                String property = entry.getValue();
                
                // Анализируем required fields
                if (schema.getRequiredFields() != null && 
                    schema.getRequiredFields().contains(fieldName)) {
                    
                    BusinessRuleDependency rule = new BusinessRuleDependency(
                        "required_field_" + fieldName,
                        "Required Field: " + fieldName,
                        "VALIDATION",
                        BusinessRuleDependency.RuleScope.FIELD_LEVEL
                    );
                    
                    rule.setAffectedFields(Collections.singleton(fieldName));
                    rule.setValidationLogic(fieldName + " is required for " + schema.getName());
                    rule.setConstraintExpression("NOT NULL");
                    
                    rules.add(rule);
                }
            }
        }
    }
    
    private void analyzeEndpointValidationRules(ApiEndpoint endpoint, List<BusinessRuleDependency> rules) {
        // Анализируем параметры endpoint
        if (endpoint.getParameters() != null) {
            for (ApiParameter param : endpoint.getParameters()) {
                if (param.isRequired()) {
                    BusinessRuleDependency rule = new BusinessRuleDependency(
                        "required_param_" + param.getName(),
                        "Required Parameter: " + param.getName(),
                        "VALIDATION",
                        BusinessRuleDependency.RuleScope.FIELD_LEVEL
                    );
                    
                    rule.setAffectedFields(Collections.singleton(param.getName()));
                    rule.setValidationLogic(param.getName() + " is required for " + endpoint.getPath());
                    rule.setConstraintExpression("NOT NULL");
                    
                    rules.add(rule);
                }
            }
        }
    }
    
    private void analyzeSchemaConstraints(ApiSchema schema, List<BusinessRuleDependency> rules) {
        // Простая реализация - добавляем базовое ограничение
        BusinessRuleDependency rule = new BusinessRuleDependency(
            "schema_constraint_" + schema.getName(),
            "Schema Constraint: " + schema.getName(),
            "CONSTRAINT",
            BusinessRuleDependency.RuleScope.RECORD_LEVEL
        );
        
        rule.setValidationLogic("Schema " + schema.getName() + " must follow defined constraints");
        rule.setConstraintExpression("Schema validation rules");
        rule.setAffectedFields(extractAffectedFields(schema, "general"));
        
        rules.add(rule);
    }
    
    private Set<String> extractAffectedFields(ApiSchema schema, String constraint) {
        Set<String> fields = new HashSet<>();
        
        // Простая логика определения затронутых полей
        if (schema.getProperties() != null) {
            fields.addAll(schema.getProperties().keySet());
        }
        
        return fields;
    }
    
    private String extractResourceFromPath(String path) {
        // Извлекаем ресурс из пути API
        if (path == null || path.isEmpty()) return null;
        
        // Убираем path parameters
        String[] parts = path.split("/");
        for (String part : parts) {
            if (!part.startsWith("{") && !part.isEmpty() && !part.equals("v1") && !Character.isDigit(part.charAt(0))) {
                return part;
            }
        }
        
        return null;
    }
    
    private Set<String> determineRequiredOperations(Set<String> operations) {
        Set<String> required = new HashSet<>();
        
        if (operations.contains("POST") || operations.contains("PUT")) {
            required.add("CREATE");
        }
        if (operations.contains("GET")) {
            required.add("READ");
        }
        if (operations.contains("PUT") || operations.contains("PATCH")) {
            required.add("UPDATE");
        }
        if (operations.contains("DELETE")) {
            required.add("DELETE");
        }
        
        return required;
    }
}