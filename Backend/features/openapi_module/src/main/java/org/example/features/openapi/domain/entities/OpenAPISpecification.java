package org.example.features.openapi.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

/**
 * Domain entity representing an OpenAPI specification.
 * This entity encapsulates the core business logic and data for OpenAPI spec handling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAPISpecification {
    private String id;
    private String version;
    private String title;
    private String description;
    private Map<String, Object> rawSpecification;
    private ValidationResult validationResult;
    private SpecificationMetadata metadata;

    /**
     * Validates if the specification is properly structured
     */
    public boolean isValid() {
        return validationResult != null && validationResult.isValid();
    }

    /**
     * Gets the OpenAPI version (e.g., "3.0.0")
     */
    public String getOpenAPIVersion() {
        return version;
    }
}