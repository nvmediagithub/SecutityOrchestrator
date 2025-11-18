package org.example.infrastructure.services.openapi;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Service for validating OpenAPI specifications
 */
@Service
public class OpenApiValidator {
    
    private static final Pattern OPENAPI_VERSION_PATTERN = Pattern.compile("^3\\.0\\.\\d+$|^3\\.1\\.\\d+$");
    private static final Pattern PATH_PATTERN = Pattern.compile("^/.*");
    
    public ValidationResult validateOpenApi(String apiSpec) {
        if (apiSpec == null || apiSpec.trim().isEmpty()) {
            return new ValidationResult(false, "API specification cannot be null or empty");
        }
        
        try {
            Map<String, Object> spec = parseYamlOrJson(apiSpec);
            List<ValidationError> errors = new ArrayList<>();
            
            // Basic validation
            validateOpenApiVersion(spec, errors);
            validateInfo(spec, errors);
            validatePaths(spec, errors);
            validateComponents(spec, errors);
            validateSecurity(spec, errors);
            
            boolean isValid = errors.isEmpty();
            return new ValidationResult(isValid, isValid ? "API specification is valid" : "Validation failed", errors);
            
        } catch (Exception e) {
            return new ValidationResult(false, "Failed to parse API specification: " + e.getMessage());
        }
    }
    
    public List<ValidationError> getValidationErrors(String apiSpec) {
        ValidationResult result = validateOpenApi(apiSpec);
        return result.getErrors();
    }
    
    public boolean isValidOpenApi(String apiSpec) {
        return validateOpenApi(apiSpec).isValid();
    }
    
    public List<String> validateEndpointSecurity(String method, String path, Map<String, Object> endpoint) {
        List<String> issues = new ArrayList<>();
        
        if (endpoint == null) {
            issues.add("Endpoint configuration is null");
            return issues;
        }
        
        // Check for security requirements
        Map<String, Object> security = (Map<String, Object>) endpoint.get("security");
        if (security == null || security.isEmpty()) {
            issues.add("No security requirements defined for " + method.toUpperCase() + " " + path);
        }
        
        return issues;
    }
    
    private void validateOpenApiVersion(Map<String, Object> spec, List<ValidationError> errors) {
        String openapi = (String) spec.get("openapi");
        if (openapi == null) {
            errors.add(new ValidationError("Missing openapi version", "The 'openapi' field is required"));
            return;
        }
        
        if (!OPENAPI_VERSION_PATTERN.matcher(openapi).matches()) {
            errors.add(new ValidationError("Invalid openapi version", "OpenAPI version must be 3.0.x or 3.1.x"));
        }
    }
    
    private void validateInfo(Map<String, Object> spec, List<ValidationError> errors) {
        Map<String, Object> info = (Map<String, Object>) spec.get("info");
        if (info == null) {
            errors.add(new ValidationError("Missing info object", "The 'info' object is required"));
            return;
        }
        
        if (info.get("title") == null) {
            errors.add(new ValidationError("Missing title", "The 'title' field in info object is required"));
        }
        
        if (info.get("version") == null) {
            errors.add(new ValidationError("Missing version", "The 'version' field in info object is required"));
        }
    }
    
    private void validatePaths(Map<String, Object> spec, List<ValidationError> errors) {
        Map<String, Object> paths = (Map<String, Object>) spec.get("paths");
        if (paths == null || paths.isEmpty()) {
            errors.add(new ValidationError("Missing paths", "At least one path must be defined"));
            return;
        }
        
        for (String path : paths.keySet()) {
            if (!PATH_PATTERN.matcher(path).matches()) {
                errors.add(new ValidationError("Invalid path format", "Path must start with '/': " + path));
            }
            
            Map<String, Object> pathItem = (Map<String, Object>) paths.get(path);
            validatePathItem(path, pathItem, errors);
        }
    }
    
    private void validatePathItem(String path, Map<String, Object> pathItem, List<ValidationError> errors) {
        Set<String> allowedMethods = Set.of("get", "post", "put", "delete", "patch", "options", "head", "trace");
        
        for (String method : pathItem.keySet()) {
            if (allowedMethods.contains(method)) {
                Map<String, Object> operation = (Map<String, Object>) pathItem.get(method);
                validateOperation(path, method, operation, errors);
            }
        }
    }
    
    private void validateOperation(String path, String method, Map<String, Object> operation, List<ValidationError> errors) {
        if (operation.get("operationId") == null) {
            errors.add(new ValidationError("Missing operationId", 
                "Operation " + method.toUpperCase() + " " + path + " should have an operationId"));
        }
        
        // Validate responses
        Map<String, Object> responses = (Map<String, Object>) operation.get("responses");
        if (responses == null || responses.isEmpty()) {
            errors.add(new ValidationError("Missing responses", 
                "Operation " + method.toUpperCase() + " " + path + " must have responses defined"));
        } else {
            boolean hasSuccessResponse = responses.keySet().stream()
                .anyMatch(code -> code.matches("^2\\d\\d$"));
            
            if (!hasSuccessResponse) {
                errors.add(new ValidationError("Missing success response", 
                    "Operation " + method.toUpperCase() + " " + path + " should have a 2xx response"));
            }
        }
    }
    
    private void validateComponents(Map<String, Object> spec, List<ValidationError> errors) {
        Map<String, Object> components = (Map<String, Object>) spec.get("components");
        if (components == null) return;
        
        // Validate schemas
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        if (schemas != null) {
            for (String schemaName : schemas.keySet()) {
                if (!isValidIdentifier(schemaName)) {
                    errors.add(new ValidationError("Invalid schema name", 
                        "Schema name must be a valid identifier: " + schemaName));
                }
            }
        }
    }
    
    private void validateSecurity(Map<String, Object> spec, List<ValidationError> errors) {
        List<Map<String, Object>> security = (List<Map<String, Object>>) spec.get("security");
        if (security != null) {
            // Validate security schemes
            Map<String, Object> components = (Map<String, Object>) spec.get("components");
            if (components != null) {
                Map<String, Object> securitySchemes = (Map<String, Object>) components.get("securitySchemes");
                if (securitySchemes != null) {
                    for (Map<String, Object> securityRequirement : security) {
                        for (String schemeName : securityRequirement.keySet()) {
                            if (!securitySchemes.containsKey(schemeName)) {
                                errors.add(new ValidationError("Undefined security scheme", 
                                    "Security scheme '" + schemeName + "' is not defined"));
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean isValidIdentifier(String name) {
        return name != null && name.matches("^[A-Za-z_][A-Za-z0-9_]*$");
    }
    
    private Map<String, Object> parseYamlOrJson(String spec) {
        // Simplified parsing - in real implementation, use Jackson or SnakeYAML
        try {
            // Try JSON first
            return parseJson(spec);
        } catch (Exception e) {
            // Fall back to YAML parsing
            return parseYaml(spec);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String json) {
        // Simplified JSON parsing
        return new HashMap<>();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseYaml(String yaml) {
        // Simplified YAML parsing
        return new HashMap<>();
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final List<ValidationError> errors;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
            this.errors = new ArrayList<>();
        }
        
        public ValidationResult(boolean valid, String message, List<ValidationError> errors) {
            this.valid = valid;
            this.message = message;
            this.errors = errors != null ? errors : new ArrayList<>();
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public List<ValidationError> getErrors() {
            return errors;
        }
    }
    
    public static class ValidationError {
        private final String type;
        private final String message;
        
        public ValidationError(String type, String message) {
            this.type = type;
            this.message = message;
        }
        
        public String getType() {
            return type;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return type + ": " + message;
        }
    }
}