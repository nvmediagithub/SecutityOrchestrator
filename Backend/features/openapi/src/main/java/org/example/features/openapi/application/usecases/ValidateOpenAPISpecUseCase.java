package org.example.features.openapi.application.usecases;

import org.example.features.openapi.domain.entities.OpenAPISpecification;
import org.example.features.openapi.domain.entities.ValidationResult;

/**
 * Use case for validating OpenAPI specifications.
 * This class contains the business logic for specification validation.
 */
public interface ValidateOpenAPISpecUseCase {

    /**
     * Validates an OpenAPI specification
     *
     * @param specification The OpenAPI specification to validate
     * @return ValidationResult containing validation status and any errors
     */
    ValidationResult validate(OpenAPISpecification specification);

    /**
     * Validates an OpenAPI specification from raw content
     *
     * @param content The raw OpenAPI specification content (JSON/YAML)
     * @return ValidationResult containing validation status and any errors
     */
    ValidationResult validateFromContent(String content);

    /**
     * Performs comprehensive validation including schema and semantic checks
     *
     * @param specification The OpenAPI specification to validate
     * @return ValidationResult with detailed validation results
     */
    ValidationResult comprehensiveValidate(OpenAPISpecification specification);
}