# Data Validation Framework

## Overview

The Security Orchestrator Data Validation Framework provides comprehensive validation capabilities for all data types processed by the system, ensuring data integrity, security, and compliance with enterprise standards.

## Framework Architecture

### Core Components

```java
@Component
public class DataValidationFramework {
    
    private final Map<DataType, DataValidator> validators;
    private final SchemaRegistry schemaRegistry;
    private final SecurityValidationEngine securityEngine;
    private final ComplianceValidator complianceValidator;
    
    public DataValidationFramework(
            Map<DataType, DataValidator> validators,
            SchemaRegistry schemaRegistry,
            SecurityValidationEngine securityEngine,
            ComplianceValidator complianceValidator) {
        this.validators = validators;
        this.schemaRegistry = schemaRegistry;
        this.securityEngine = securityEngine;
        this.complianceValidator = complianceValidator;
    }
    
    public ValidationResult validateData(DataValidationRequest request) {
        ValidationResult result = new ValidationResult();
        
        // 1. Schema validation
        result.addAll(validateSchema(request));
        
        // 2. Security validation
        result.addAll(securityEngine.validate(request));
        
        // 3. Compliance validation
        result.addAll(complianceValidator.validate(request));
        
        // 4. Business logic validation
        result.addAll(validateBusinessRules(request));
        
        return result;
    }
}
```

## Input Validation by Data Type

### BPMN File Validation

#### Schema Validation
```java
@Component
public class BpmnDataValidator implements DataValidator {
    
    private static final Pattern BPMN_SCHEMA_PATTERN = Pattern.compile(
        "xmlns.*bpmn.*\\.camunda\\.com.*/xmlns/bpmn"
    );
    
    private static final Set<String> ALLOWED_ELEMENTS = Set.of(
        "definitions", "process", "startEvent", "endEvent", "task", "gateway",
        "sequenceFlow", "lane", "dataObject", "messageEvent", "timerEvent"
    );
    
    private static final Set<String> FORBIDDEN_ELEMENTS = Set.of(
        "script", "expression", "condition", "evaluation"
    );
    
    @Override
    public ValidationResult validate(DataValidationRequest request) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Parse BPMN document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(request.getData())));
            
            // Validate schema compliance
            if (!validateSchemaCompliance(document, result)) {
                return result;
            }
            
            // Validate element restrictions
            validateElements(document, result);
            
            // Validate security constraints
            validateSecurityConstraints(document, result);
            
            // Validate business rules
            validateBusinessRules(document, result);
            
        } catch (Exception e) {
            result.addError("BPMN_VALIDATION_ERROR", "Failed to parse BPMN document: " + e.getMessage());
        }
        
        return result;
    }
    
    private boolean validateSchemaCompliance(Document document, ValidationResult result) {
        // Check BPMN namespace
        Element rootElement = document.getDocumentElement();
        String namespace = rootElement.getNamespaceURI();
        
        if (namespace == null || !namespace.contains("bpmn")) {
            result.addError("INVALID_NAMESPACE", "Document must be a valid BPMN 2.0 file");
            return false;
        }
        
        // Check required BPMN elements
        NodeList processes = document.getElementsByTagName("process");
        if (processes.getLength() == 0) {
            result.addError("MISSING_PROCESS", "BPMN file must contain at least one process");
            return false;
        }
        
        return true;
    }
    
    private void validateElements(Document document, ValidationResult result) {
        NodeList allElements = document.getElementsByTagName("*");
        
        for (int i = 0; i < allElements.getLength(); i++) {
            Element element = (Element) allElements.item(i);
            String elementName = element.getNodeName();
            
            // Check allowed elements
            if (!ALLOWED_ELEMENTS.contains(elementName) && !elementName.contains(":")) {
                result.addWarning("UNCOMMON_ELEMENT", "Element '" + elementName + "' is not commonly used");
            }
            
            // Check forbidden elements
            if (FORBIDDEN_ELEMENTS.contains(elementName)) {
                result.addError("FORBIDDEN_ELEMENT", "Forbidden element found: " + elementName);
            }
            
            // Validate attributes
            validateAttributes(element, result);
        }
    }
    
    private void validateAttributes(Element element, ValidationResult result) {
        NamedNodeMap attributes = element.getAttributes();
        
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            
            // Check for script execution patterns
            if (attrValue != null && 
                (attrValue.contains("javascript:") || 
                 attrValue.contains("eval(") ||
                 attrValue.contains("function("))) {
                result.addError("POTENTIALLY_DANGEROUS_ATTRIBUTE", 
                    "Potentially dangerous attribute value in " + element.getNodeName() + ":" + attrName);
            }
            
            // Check for SQL injection patterns
            if (containsSqlInjectionPattern(attrValue)) {
                result.addError("SQL_INJECTION_PATTERN", 
                    "SQL injection pattern detected in " + element.getNodeName() + ":" + attrName);
            }
        }
    }
}
```

### OpenAPI Specification Validation

#### Comprehensive OpenAPI Validator
```java
@Component
public class OpenApiDataValidator implements DataValidator {
    
    private static final Pattern OPENAPI_VERSION_PATTERN = Pattern.compile(
        "\"openapi\"\\s*:\\s*\"([0-9]+\\.[0-9]+\\.[0-9]+)\""
    );
    
    private static final Set<String> REQUIRED_OPENAPI_FIELDS = Set.of(
        "openapi", "info", "paths"
    );
    
    @Override
    public ValidationResult validate(DataValidationRequest request) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Parse JSON/YAML
            JsonNode openApiDocument = parseDocument(request);
            
            // Validate structure
            validateStructure(openApiDocument, result);
            
            // Validate security
            validateSecurity(openApiDocument, result);
            
            // Validate paths and operations
            validatePaths(openApiDocument, result);
            
            // Validate schemas
            validateSchemas(openApiDocument, result);
            
            // Validate examples
            validateExamples(openApiDocument, result);
            
        } catch (Exception e) {
            result.addError("OPENAPI_VALIDATION_ERROR", "Failed to validate OpenAPI document: " + e.getMessage());
        }
        
        return result;
    }
    
    private JsonNode parseDocument(DataValidationRequest request) throws JsonProcessingException {
        if (request.getContentType().contains("yaml") || request.getContentType().contains("yml")) {
            return yamlMapper.readTree(request.getData());
        } else {
            return jsonMapper.readTree(request.getData());
        }
    }
    
    private void validateStructure(JsonNode document, ValidationResult result) {
        // Check required fields
        for (String requiredField : REQUIRED_OPENAPI_FIELDS) {
            if (document.get(requiredField) == null) {
                result.addError("MISSING_REQUIRED_FIELD", "Required field '" + requiredField + "' is missing");
            }
        }
        
        // Validate OpenAPI version
        JsonNode versionNode = document.get("openapi");
        if (versionNode != null) {
            String version = versionNode.asText();
            if (!isSupportedOpenApiVersion(version)) {
                result.addError("UNSUPPORTED_OPENAPI_VERSION", "OpenAPI version '" + version + "' is not supported");
            }
        }
        
        // Validate info section
        JsonNode infoNode = document.get("info");
        if (infoNode != null) {
            validateInfoSection(infoNode, result);
        }
    }
    
    private void validateSecurity(JsonNode document, ValidationResult result) {
        JsonNode securityNode = document.get("security");
        
        if (securityNode != null) {
            // Check global security schemes
            JsonNode componentsNode = document.get("components");
            if (componentsNode != null) {
                JsonNode securitySchemesNode = componentsNode.get("securitySchemes");
                if (securitySchemesNode != null) {
                    validateSecuritySchemes(securitySchemesNode, result);
                }
            }
            
            // Check for unsecured endpoints
            validateEndpointSecurity(document, result);
        }
    }
    
    private void validateSecuritySchemes(JsonNode securitySchemes, ValidationResult result) {
        securitySchemes.fieldNames().forEachRemaining(schemeName -> {
            JsonNode scheme = securitySchemes.get(schemeName);
            String type = scheme.path("type").asText();
            String schemeValue = scheme.path("scheme").asText();
            
            // Validate scheme types
            if (!type.equals("http") && !type.equals("apiKey") && 
                !type.equals("oauth2") && !type.equals("openIdConnect")) {
                result.addError("INVALID_SECURITY_SCHEME_TYPE", 
                    "Invalid security scheme type '" + type + "' for scheme '" + schemeName + "'");
            }
            
            // Validate HTTP schemes
            if (type.equals("http")) {
                if (!schemeValue.equals("bearer") && !schemeValue.equals("basic")) {
                    result.addWarning("UNCOMMON_HTTP_SCHEME", 
                        "Uncommon HTTP authentication scheme '" + schemeValue + "' for scheme '" + schemeName + "'");
                }
            }
            
            // Validate OAuth2 flows
            if (type.equals("oauth2")) {
                JsonNode flowsNode = scheme.get("flows");
                if (flowsNode != null) {
                    validateOAuth2Flows(flowsNode, result, schemeName);
                }
            }
        });
    }
    
    private void validatePaths(JsonNode document, ValidationResult result) {
        JsonNode pathsNode = document.get("paths");
        
        if (pathsNode != null) {
            pathsNode.fieldNames().forEachRemaining(path -> {
                JsonNode pathItem = pathsNode.get(path);
                validatePathItem(path, pathItem, result);
            });
        }
    }
    
    private void validatePathItem(String path, JsonNode pathItem, ValidationResult result) {
        // Validate path parameters
        validatePathParameters(path, result);
        
        // Validate HTTP methods
        pathItem.fieldNames().forEachRemaining(method -> {
            if (isHttpMethod(method)) {
                JsonNode operation = pathItem.get(method);
                validateOperation(path, method, operation, result);
            }
        });
    }
    
    private void validateOperation(String path, String method, JsonNode operation, ValidationResult result) {
        // Validate operation ID
        JsonNode operationIdNode = operation.get("operationId");
        if (operationIdNode == null) {
            result.addWarning("MISSING_OPERATION_ID", "Operation '" + method + " " + path + "' missing operationId");
        }
        
        // Validate parameters
        JsonNode parametersNode = operation.get("parameters");
        if (parametersNode != null) {
            validateParameters(parametersNode, result, path + " " + method);
        }
        
        // Validate request body
        JsonNode requestBodyNode = operation.get("requestBody");
        if (requestBodyNode != null) {
            validateRequestBody(requestBodyNode, result, path + " " + method);
        }
        
        // Validate responses
        JsonNode responsesNode = operation.get("responses");
        if (responsesNode != null) {
            validateResponses(responsesNode, result, path + " " + method);
        }
        
        // Validate security
        validateOperationSecurity(operation, result, path + " " + method);
    }
}
```

### JSON Data Validation

#### Generic JSON Schema Validator
```java
@Component
public class JsonDataValidator implements DataValidator {
    
    private final JsonSchemaFactory schemaFactory;
    private final JsonNodeValidator nodeValidator;
    
    @Override
    public ValidationResult validate(DataValidationRequest request) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Parse JSON
            JsonNode jsonDocument = objectMapper.readTree(request.getData());
            
            // Validate against schema if provided
            if (request.getSchema() != null) {
                result.addAll(validateAgainstSchema(jsonDocument, request.getSchema()));
            }
            
            // Validate JSON structure
            validateStructure(jsonDocument, result);
            
            // Validate data types
            validateDataTypes(jsonDocument, result);
            
            // Validate business rules
            validateBusinessRules(jsonDocument, result);
            
            // Check for injection patterns
            validateSecurityPatterns(jsonDocument, result);
            
        } catch (Exception e) {
            result.addError("JSON_VALIDATION_ERROR", "Failed to validate JSON document: " + e.getMessage());
        }
        
        return result;
    }
    
    private void validateStructure(JsonNode node, ValidationResult result) {
        if (node.isObject()) {
            validateObjectStructure(node, result);
        } else if (node.isArray()) {
            validateArrayStructure(node, result);
        }
    }
    
    private void validateObjectStructure(JsonNode object, ValidationResult result) {
        object.fieldNames().forEachRemaining(fieldName -> {
            // Validate field name patterns
            if (!isValidFieldName(fieldName)) {
                result.addError("INVALID_FIELD_NAME", "Invalid field name: " + fieldName);
            }
            
            JsonNode fieldValue = object.get(fieldName);
            
            // Validate field value
            validateFieldValue(fieldName, fieldValue, result);
            
            // Recursively validate nested structures
            validateStructure(fieldValue, result);
        });
    }
    
    private void validateDataTypes(JsonNode node, ValidationResult result) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                validateDataType(entry.getKey(), entry.getValue(), result);
                validateStructure(entry.getValue(), result);
            });
        } else if (node.isArray()) {
            node.elements().forEachRemaining(element -> {
                validateStructure(element, result);
            });
        } else {
            validatePrimitiveType(node, result);
        }
    }
    
    private void validateSecurityPatterns(JsonNode node, ValidationResult result) {
        String nodeString = node.toString();
        
        // Check for script injection
        if (containsScriptInjection(nodeString)) {
            result.addError("SCRIPT_INJECTION_PATTERN", "Potential script injection detected");
        }
        
        // Check for SQL injection
        if (containsSqlInjection(nodeString)) {
            result.addError("SQL_INJECTION_PATTERN", "Potential SQL injection detected");
        }
        
        // Check for command injection
        if (containsCommandInjection(nodeString)) {
            result.addError("COMMAND_INJECTION_PATTERN", "Potential command injection detected");
        }
        
        // Check for XSS patterns
        if (containsXssPattern(nodeString)) {
            result.addError("XSS_PATTERN", "Potential XSS pattern detected");
        }
    }
    
    private boolean containsScriptInjection(String data) {
        return Pattern.compile("(?i)(<script|javascript:|on\\w+\\s*=)", Pattern.CASE_INSENSITIVE)
                     .matcher(data).find();
    }
    
    private boolean containsSqlInjection(String data) {
        return Pattern.compile("(?i)(union|select|insert|delete|update|drop|create|alter|exec|execute)", 
                              Pattern.CASE_INSENSITIVE)
                     .matcher(data).find();
    }
    
    private boolean containsCommandInjection(String data) {
        return Pattern.compile("(?i)(&&|\\|\\||;|`|\\$\\(|\\$\\{\\(|subprocess|os\\.system)", 
                              Pattern.CASE_INSENSITIVE)
                     .matcher(data).find();
    }
    
    private boolean containsXssPattern(String data) {
        return Pattern.compile("(?i)(<iframe|<object|<embed|<form|<input|onerror|onload)", 
                              Pattern.CASE_INSENSITIVE)
                     .matcher(data).find();
    }
}
```

## Schema Validation and Type Checking

### Dynamic Schema Registry
```java
@Component
public class SchemaRegistry {
    
    private final Map<String, JsonSchema> registeredSchemas = new ConcurrentHashMap<>();
    private final Map<DataType, String> defaultSchemas = new EnumMap<>(DataType.class);
    
    @PostConstruct
    public void initializeDefaultSchemas() {
        // Register default schemas for common data types
        defaultSchemas.put(DataType.BPMN, "classpath:schemas/bpmn-schema.json");
        defaultSchemas.put(DataType.OPENAPI, "classpath:schemas/openapi-schema.json");
        defaultSchemas.put(DataType.JSON, "classpath:schemas/json-schema.json");
        defaultSchemas.put(DataType.XML, "classpath:schemas/xml-schema.xsd");
    }
    
    public JsonSchema getSchema(String schemaId) {
        return registeredSchemas.get(schemaId);
    }
    
    public JsonSchema getSchema(DataType dataType) {
        String schemaPath = defaultSchemas.get(dataType);
        if (schemaPath != null) {
            return loadSchemaFromPath(schemaPath);
        }
        return null;
    }
    
    public void registerSchema(String schemaId, JsonSchema schema) {
        registeredSchemas.put(schemaId, schema);
    }
    
    private JsonSchema loadSchemaFromPath(String schemaPath) {
        try {
            Resource resource = resourceLoader.getResource(schemaPath);
            String schemaContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
                   .getSchema(schemaContent);
        } catch (Exception e) {
            throw new SchemaLoadingException("Failed to load schema from path: " + schemaPath, e);
        }
    }
}
```

### Type Safety Validation
```java
@Component
public class TypeSafetyValidator {
    
    private final Map<DataType, TypeValidator> typeValidators = new EnumMap<>(DataType.class);
    
    @PostConstruct
    public void initializeValidators() {
        typeValidators.put(DataType.STRING, new StringTypeValidator());
        typeValidators.put(DataType.NUMBER, new NumberTypeValidator());
        typeValidators.put(DataType.BOOLEAN, new BooleanTypeValidator());
        typeValidators.put(DataType.DATE, new DateTypeValidator());
        typeValidators.put(DataType.EMAIL, new EmailTypeValidator());
        typeValidators.put(DataType.URL, new UrlTypeValidator());
        typeValidators.put(DataType.UUID, new UuidTypeValidator());
        typeValidators.put(DataType.JSON, new JsonTypeValidator());
        typeValidators.put(DataType.XML, new XmlTypeValidator());
    }
    
    public ValidationResult validateTypeSafety(Object value, DataType expectedType) {
        TypeValidator validator = typeValidators.get(expectedType);
        if (validator == null) {
            return ValidationResult.error("UNKNOWN_TYPE", "No validator found for type: " + expectedType);
        }
        
        return validator.validate(value);
    }
    
    public interface TypeValidator {
        ValidationResult validate(Object value);
    }
    
    // Specific type validators
    public static class EmailTypeValidator implements TypeValidator {
        private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
        
        @Override
        public ValidationResult validate(Object value) {
            ValidationResult result = new ValidationResult();
            
            if (value == null) {
                result.addError("NULL_VALUE", "Email cannot be null");
                return result;
            }
            
            String email = value.toString();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                result.addError("INVALID_EMAIL", "Invalid email format: " + email);
            }
            
            return result;
        }
    }
    
    public static class UrlTypeValidator implements TypeValidator {
        @Override
        public ValidationResult validate(Object value) {
            ValidationResult result = new ValidationResult();
            
            if (value == null) {
                result.addError("NULL_VALUE", "URL cannot be null");
                return result;
            }
            
            try {
                new URL(value.toString());
            } catch (MalformedURLException e) {
                result.addError("INVALID_URL", "Invalid URL format: " + value);
            }
            
            return result;
        }
    }
}
```

## SQL Injection Prevention

### SQL Injection Detection and Prevention
```java
@Component
public class SqlInjectionValidator {
    
    private static final Pattern SQL_INJECTION_PATTERNS = Pattern.compile(
        "(?i)(\\b(union|select|insert|update|delete|drop|create|alter|exec|execute|sp_)\\b.*\\b(from|table|database|schema)\\b|" +
        "\\b(or|and)\\b.*[=']{1}.*[=']{1}|" +
        "\\b(drop|delete|truncate)\\b.*\\b(table|database)\\b|" +
        "\\b(union|select)\\b.*\\b(into|outfile|dumpfile)\\b|" +
        "\\b(exec|execute)\\b.*\\b(sp_)\\w+|" +
        "\\b(convert|char|ascii|hex|substring|length)\\b.*\\(|" +
        "\\b(or|and)\\b.*['\"]?\\d+['\"]?\\s*[=><!]+|\\b(and|or)\\b.*['\";]|" +
        "\\b(updatexml|extractvalue)\\b.*\\(|\\b(benchmark)\\b.*\\(|" +
        "\\b(sleep|benchmark)\\b.*\\(|\\b(into\\s+outfile)\\b|" +
        "\\b(load_file)\\b.*\\(|\\b(uuid|rand)\\b.*\\(|" +
        "\\b(mid|substr|ascii|hex)\\b.*\\(|\\b(group_concat|concat)\\b.*\\(|" +
        "\\b(version|user|database)\\b.*\\(|\\b(length|count)\\b.*\\(|" +
        "['\";].*['\";]|\\b(drop\\s+table)\\b|\\b(delete\\s+from)\\b|\\b(update\\s+set)\\b|" +
        "\\b(insert\\s+into)\\b|\\b(alter\\s+table)\\b|\\b(create\\s+table)\\b|" +
        "\\b(grant|revoke)\\b.*\\b(on)\\b|\\b(execute\\s+immediate)\\b|" +
        "\\b(openrowset|opendatasource)\\b|\\b(linkedserver)\\b)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );
    
    public ValidationResult validateSqlSafety(String input) {
        ValidationResult result = new ValidationResult();
        
        if (input == null || input.trim().isEmpty()) {
            return result; // Empty input is considered safe
        }
        
        // Check for SQL injection patterns
        if (SQL_INJECTION_PATTERNS.matcher(input).find()) {
            result.addError("SQL_INJECTION_DETECTED", 
                "Potential SQL injection pattern detected in input");
        }
        
        // Check for suspicious characters
        if (containsSuspiciousCharacters(input)) {
            result.addWarning("SUSPICIOUS_CHARACTERS", 
                "Input contains potentially dangerous characters");
        }
        
        // Check for encoded injection attempts
        if (containsEncodedInjection(input)) {
            result.addWarning("ENCODED_INJECTION", 
                "Input may contain encoded injection attempts");
        }
        
        return result;
    }
    
    private boolean containsSuspiciousCharacters(String input) {
        return Pattern.compile("[\\x00\\x08\\x0B\\x0C\\x0E-\\x1F]", Pattern.MULTILINE)
                     .matcher(input).find();
    }
    
    private boolean containsEncodedInjection(String input) {
        // Check for double URL encoding
        if (input.contains("%25")) {
            return true;
        }
        
        // Check for HTML entities that could be used for injection
        if (input.contains("<") || input.contains(">") || 
            input.contains(""") || input.contains("&#x")) {
            return true;
        }
        
        // Check for Unicode bypass attempts
        if (Pattern.compile("[\\u200B-\\u200D\\uFEFF]", Pattern.MULTILINE)
                   .matcher(input).find()) {
            return true;
        }
        
        return false;
    }
    
    @Component
    public static class SqlParameterValidator implements ParameterValidator {
        
        private final SqlInjectionValidator injectionValidator;
        private final ParameterizedQueryValidator queryValidator;
        
        @Override
        public ValidationResult validateParameter(String parameterName, Object value) {
            ValidationResult result = new ValidationResult();
            
            // Validate parameter name
            if (!isValidParameterName(parameterName)) {
                result.addError("INVALID_PARAMETER_NAME", "Invalid parameter name: " + parameterName);
            }
            
            // Validate parameter value
            if (value instanceof String) {
                result.addAll(injectionValidator.validateSqlSafety((String) value));
            }
            
            // Validate query construction
            if (isDynamicQuery(value)) {
                result.addError("DYNAMIC_QUERY_DETECTED", 
                    "Dynamic query detected in parameter: " + parameterName);
            }
            
            return result;
        }
        
        private boolean isValidParameterName(String parameterName) {
            return Pattern.matches("[a-zA-Z_][a-zA-Z0-9_]*", parameterName);
        }
        
        private boolean isDynamicQuery(Object value) {
            if (!(value instanceof String)) {
                return false;
            }
            
            String stringValue = (String) value;
            return stringValue.contains(" ") || stringValue.contains(";") || 
                   stringValue.toLowerCase().contains("select") ||
                   stringValue.toLowerCase().contains("from") ||
                   stringValue.toLowerCase().contains("where");
        }
    }
}
```

## XSS Protection

### Cross-Site Scripting (XSS) Prevention
```java
@Component
public class XssProtectionValidator {
    
    private static final Pattern XSS_PATTERNS = Pattern.compile(
        "(?i)(<\\s*script\\b[^>]*>[\\s\\S]*?<\\s*/\\s*script\\s*>|" +
        "javascript\\s*:|" +
        "<\\s*iframe\\b[^>]*>[\\s\\S]*?<\\s*/\\s*iframe\\s*>|" +
        "<\\s*object\\b[^>]*>[\\s\\S]*?<\\s*/\\s*object\\s*>|" +
        "<\\s*embed\\b[^>]*>[\\s\\S]*?<\\s*/\\s*embed\\s*>|" +
        "<\\s*link\\b[^>]*\\brel\\s*=\\s*[\"']?stylesheet[\"']?[^>]*>|" +
        "<\\s*style\\b[^>]*>[\\s\\S]*?<\\s*/\\s*style\\s*>|" +
        "<\\s*meta\\b[^>]*\\bhttp-equiv\\s*=\\s*[\"']?refresh[\"']?[^>]*>|" +
        "<\\s*meta\\b[^>]*\\bhttp-equiv\\s*=\\s*[\"']?set-cookie[\"']?[^>]*>|" +
        "<\\s*img\\b[^>]*\\bsrc\\s*=\\s*[\"']?javascript\\s*:|" +
        "<\\s*svg\\b[^>]*\\bon\\w+\\s*=|" +
        "\\bon\\w+\\s*=\\s*[\"'][^\"']*[\"']|" +
        "\\bexpression\\s*\\(|" +
        "\\beval\\s*\\(|" +
        "<\\s*frame\\b[^>]*>[\\s\\S]*?<\\s*/\\s*frame\\s*>|" +
        "<\\s*frameset\\b[^>]*>[\\s\\S]*?<\\s*/\\s*frameset\\s*>|" +
        "<\\s*applet\\b[^>]*>[\\s\\S]*?<\\s*/\\s*applet\\s*>|" +
        "<\\s*form\\b[^>]*>[\\s\\S]*?<\\s*/\\s*form\\s*>|" +
        "<\\s*input\\b[^>]*\\btype\\s*=\\s*[\"']?file[\"']?[^>]*>|" +
        "<\\s*input\\b[^>]*\\btype\\s*=\\s*[\"']?password[\"']?[^>]*>|" +
        "<\\s*input\\b[^>]*\\btype\\s*=\\s*[\"']?image[\"']?[^>]*\\bsrc\\s*=\\s*[\"']?javascript\\s*:)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );
    
    private static final Pattern ENCODED_XSS_PATTERNS = Pattern.compile(
        "(?i)(%3c\\s*script\\b|%3C\\s*SCRIPT\\b|" +
        "%3c\\s*iframe\\b|%3C\\s*IFRAME\\b|" +
        "javascript\\s*:|%6A%61%76%61%73%63%72%69%70%74%3A|" +
        "%3c\\s*object\\b|%3C\\s*OBJECT\\b|" +
        "on\\w+\\s*=|%6F%6E%77%69%64%74%68%3D|" +
        "expression\\s*\\(|%65%78%70%72%65%73%73%69%6F%6E%3A|" +
        "eval\\s*\\(|%65%76%61%6C%3A|" +
        "&#x[0-9a-fA-F]+;|%26%23x[0-9A-F]+%3B)",
        Pattern.CASE_INSENSITIVE
    );
    
    public ValidationResult validateXssSafety(String input) {
        ValidationResult result = new ValidationResult();
        
        if (input == null || input.trim().isEmpty()) {
            return result; // Empty input is considered safe
        }
        
        // Check for direct XSS patterns
        if (XSS_PATTERNS.matcher(input).find()) {
            result.addError("XSS_DETECTED", "Direct XSS pattern detected in input");
        }
        
        // Check for encoded XSS patterns
        if (ENCODED_XSS_PATTERNS.matcher(input).find()) {
            result.addWarning("ENCODED_XSS_DETECTED", "Encoded XSS pattern detected in input");
        }
        
        // Check for filter evasion techniques
        if (containsFilterEvasion(input)) {
            result.addWarning("FILTER_EVASION", "Possible filter evasion technique detected");
        }
        
        // Validate HTML context
        if (containsHtmlContext(input)) {
            validateHtmlContext(input, result);
        }
        
        return result;
    }
    
    private boolean containsFilterEvasion(String input) {
        // Check for mixed case evasion
        if (Pattern.compile("(?i)<\\s*[sS][cC][rR][iI][pP][tT]", Pattern.MULTILINE)
                   .matcher(input).find()) {
            return true;
        }
        
        // Check for null byte injection
        if (input.contains("\\0")) {
            return true;
        }
        
        // Check for unusual whitespace
        if (Pattern.compile("\\s+").matcher(input).find() && 
            Pattern.compile("[\\t\\n\\r\\x0B\\x0C\\x1C\\x1D\\x1E\\x1F]").matcher(input).find()) {
            return true;
        }
        
        // Check for Unicode normalization issues
        if (containsNormalizationIssues(input)) {
            return true;
        }
        
        return false;
    }
    
    private boolean containsNormalizationIssues(String input) {
        // Check for homoglyph attacks
        if (input.contains("javascript") && 
            !input.toLowerCase().contains("javascript")) {
            return true;
        }
        
        // Check for invisible characters
        if (Pattern.compile("[\\u200B-\\u200D\\uFEFF]").matcher(input).find()) {
            return true;
        }
        
        return false;
    }
    
    private boolean containsHtmlContext(String input) {
        return input.contains("<") || input.contains(">") || input.contains("&");
    }
    
    private void validateHtmlContext(String input, ValidationResult result) {
        // Validate HTML tag structure
        if (!isValidHtmlStructure(input)) {
            result.addError("INVALID_HTML_STRUCTURE", "Malformed HTML structure detected");
        }
        
        // Check for dangerous HTML attributes
        if (containsDangerousAttributes(input)) {
            result.addError("DANGEROUS_HTML_ATTRIBUTES", "Dangerous HTML attributes detected");
        }
    }
    
    private boolean isValidHtmlStructure(String input) {
        // Check for balanced brackets
        int openBrackets = 0;
        for (char c : input.toCharArray()) {
            if (c == '<') openBrackets++;
            else if (c == '>') {
                openBrackets--;
                if (openBrackets < 0) return false;
            }
        }
        return openBrackets == 0;
    }
    
    private boolean containsDangerousAttributes(String input) {
        return Pattern.compile(
            "(?i)\\s+(on\\w+)\\s*=\\s*[\"'][^\"']*[\"']",
            Pattern.CASE_INSENSITIVE
        ).matcher(input).find();
    }
    
    @Component
    public static class OutputEncoder {
        
        public String encodeForHtml(String input) {
            if (input == null) {
                return "";
            }
            
            return input.replace("&", "&")
                       .replace("<", "<")
                       .replace(">", ">")
                       .replace("\"", """)
                       .replace("'", "&#x27;");
        }
        
        public String encodeForJavascript(String input) {
            if (input == null) {
                return "";
            }
            
            return input.replace("\\", "\\\\")
                       .replace("\"", "\\\"")
                       .replace("'", "\\'")
                       .replace("\n", "\\n")
                       .replace("\r", "\\r")
                       .replace("\t", "\\t")
                       .replace("\b", "\\b")
                       .replace("\f", "\\f");
        }
        
        public String encodeForUrl(String input) {
            try {
                return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
            } catch (Exception e) {
                // Fallback to basic encoding
                return encodeForJavascript(input);
            }
        }
        
        public String encodeForCss(String input) {
            if (input == null) {
                return "";
            }
            
            // CSS escape sequence
            StringBuilder escaped = new StringBuilder();
            for (char c : input.toCharArray()) {
                if (c < 0x20 || c >= 0x7F) {
                    escaped.append(String.format("\\%h ", (int)c));
                } else {
                    escaped.append(c);
                }
            }
            return escaped.toString();
        }
    }
}
```

## File Upload Security Validation

### Advanced File Upload Security
```java
@Component
public class SecureFileUploadValidator {
    
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
        ".bpmn", ".xml", ".json", ".yaml", ".yml", ".png", ".jpg", ".jpeg", ".pdf", ".txt"
    );
    
    private static final Map<String, String> ALLOWED_MIME_TYPES = Map.of(
        "application/xml", "xml",
        "text/xml", "xml",
        "application/json", "json",
        "application/yaml", "yaml",
        "text/yaml", "yaml",
        "image/png", "png",
        "image/jpeg", "jpg",
        "application/pdf", "pdf",
        "text/plain", "txt"
    );
    
    private final AntivirusScanner antivirusScanner;
    private final FileContentAnalyzer contentAnalyzer;
    private final MagicNumberValidator magicNumberValidator;
    
    public ValidationResult validateFileUpload(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        // 1. Basic file validation
        result.addAll(validateBasicFileProperties(file));
        
        // 2. Content type validation
        result.addAll(validateContentType(file));
        
        // 3. Magic number validation
        result.addAll(validateMagicNumber(file));
        
        // 4. Malware scanning
        result.addAll(scanForMalware(file));
        
        // 5. Content analysis
        result.addAll(analyzeFileContent(file));
        
        // 6. Security pattern detection
        result.addAll(detectSecurityPatterns(file));
        
        // 7. File size and complexity validation
        result.addAll(validateFileComplexity(file));
        
        return result;
    }
    
    private ValidationResult validateBasicFileProperties(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        // Check if file is empty
        if (file.isEmpty()) {
            result.addError("EMPTY_FILE", "Uploaded file is empty");
            return result;
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            result.addError("FILE_TOO_LARGE", 
                "File size exceeds maximum allowed size of " + MAX_FILE_SIZE + " bytes");
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            result.addError("MISSING_FILENAME", "File must have a valid filename");
            return result;
        }
        
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_FILE_TYPES.contains(extension.toLowerCase())) {
            result.addError("INVALID_FILE_TYPE", 
                "File type '" + extension + "' is not allowed");
        }
        
        // Check for path traversal attempts
        if (containsPathTraversal(originalFilename)) {
            result.addError("PATH_TRAVERSAL", "Filename contains path traversal attempts");
        }
        
        // Check for double extensions
        if (containsDoubleExtension(originalFilename)) {
            result.addWarning("DOUBLE_EXTENSION", "Filename has suspicious double extension");
        }
        
        return result;
    }
    
    private ValidationResult validateContentType(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        String contentType = file.getContentType();
        if (contentType == null) {
            result.addWarning("NO_CONTENT_TYPE", "Content type not specified");
            return result;
        }
        
        if (!ALLOWED_MIME_TYPES.containsKey(contentType)) {
            result.addError("INVALID_MIME_TYPE", 
                "MIME type '" + contentType + "' is not allowed");
        } else {
            String expectedExtension = ALLOWED_MIME_TYPES.get(contentType);
            String actualExtension = getFileExtension(file.getOriginalFilename());
            
            if (!expectedExtension.equals(actualExtension.toLowerCase())) {
                result.addWarning("MISMATCHED_EXTENSION", 
                    "File extension '" + actualExtension + "' doesn't match MIME type '" + contentType + "'");
            }
        }
        
        return result;
    }
    
    private ValidationResult validateMagicNumber(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        try {
            byte[] fileHeader = file.getBytes();
            String magicNumber = bytesToHex(Arrays.copyOf(fileHeader, 8));
            
            if (!isValidMagicNumber(magicNumber, file.getOriginalFilename())) {
                result.addError("INVALID_MAGIC_NUMBER", 
                    "File magic number doesn't match declared file type");
            }
            
        } catch (IOException e) {
            result.addError("MAGIC_NUMBER_CHECK_FAILED", 
                "Failed to read file header for magic number validation");
        }
        
        return result;
    }
    
    private ValidationResult scanForMalware(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        try {
            ScanResult scanResult = antivirusScanner.scanFile(file);
            
            if (!scanResult.isClean()) {
                result.addError("MALWARE_DETECTED", 
                    "Malware detected: " + scanResult.getThreatType());
                
                // Log security incident
                securityLogger.warn("MALWARE_DETECTED | file: {} | threat: {} | size: {}",
                    file.getOriginalFilename(), scanResult.getThreatType(), file.getSize());
            }
            
        } catch (Exception e) {
            result.addError("MALWARE_SCAN_FAILED", 
                "Antivirus scan failed: " + e.getMessage());
        }
        
        return result;
    }
    
    private ValidationResult analyzeFileContent(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Analyze file content based on type
            String contentType = file.getContentType();
            if (contentType != null && contentType.contains("text")) {
                result.addAll(analyzeTextContent(file));
            } else if (contentType.contains("xml")) {
                result.addAll(analyzeXmlContent(file));
            } else if (contentType.contains("json")) {
                result.addAll(analyzeJsonContent(file));
            }
            
        } catch (Exception e) {
            result.addError("CONTENT_ANALYSIS_FAILED", 
                "Failed to analyze file content: " + e.getMessage());
        }
        
        return result;
    }
    
    private ValidationResult detectSecurityPatterns(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            
            // Check for script injection patterns
            if (containsScriptPatterns(content)) {
                result.addError("SCRIPT_INJECTION", "File contains script injection patterns");
            }
            
            // Check for command execution patterns
            if (containsCommandPatterns(content)) {
                result.addError("COMMAND_INJECTION", "File contains command execution patterns");
            }
            
            // Check for SQL injection patterns
            if (containsSqlPatterns(content)) {
                result.addError("SQL_INJECTION", "File contains SQL injection patterns");
            }
            
        } catch (IOException e) {
            result.addError("SECURITY_PATTERN_CHECK_FAILED", 
                "Failed to check security patterns");
        }
        
        return result;
    }
    
    private boolean containsPathTraversal(String filename) {
        return filename.contains("..") || filename.contains("/") || filename.contains("\\");
    }
    
    private boolean containsDoubleExtension(String filename) {
        return filename.matches(".*\\.[a-zA-Z0-9]+\\.[a-zA-Z0-9]+.*");
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
```

This comprehensive Data Validation Framework ensures that all data processed by the Security Orchestrator is thoroughly validated, secure, and compliant with enterprise standards. The framework provides multiple layers of validation including schema validation, type checking, SQL injection prevention, XSS protection, and secure file upload handling.