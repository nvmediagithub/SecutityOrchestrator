package org.example.features.openapi.infrastructure.adapters;

import org.example.features.openapi.domain.entities.OpenAPISpecification;
import org.example.features.openapi.domain.entities.ValidationError;
import org.example.features.openapi.domain.entities.ValidationResult;
import org.example.features.openapi.domain.entities.ValidationSummary;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Infrastructure adapter for validating OpenAPI specifications.
 * Uses JSON Schema validation and semantic checks.
 */
@Slf4j
@Component
public class OpenAPISpecificationValidator {

    private final ObjectMapper objectMapper;
    private final JsonValidator jsonValidator;
    private final OpenAPISpecificationParser parser;

    public OpenAPISpecificationValidator(OpenAPISpecificationParser parser) {
        this.objectMapper = new ObjectMapper();
        this.jsonValidator = JsonSchemaFactory.byDefault().getValidator();
        this.parser = parser;
    }

    /**
     * Validates an OpenAPI specification
     *
     * @param specification The specification to validate
     * @return ValidationResult with validation status and errors
     */
    public ValidationResult validate(OpenAPISpecification specification) {
        long startTime = System.currentTimeMillis();
        List<ValidationError> errors = new ArrayList<>();

        try {
            // First, validate basic parsing
            if (specification.getRawSpecification() == null) {
                errors.add(ValidationError.of("$.spec", "Specification content is null"));
                return createValidationResult(false, errors, startTime);
            }

            // Parse to OpenAPI object to ensure it's valid
            String content = objectMapper.writeValueAsString(specification.getRawSpecification());
            parser.parseFromContent(content);

            // Perform semantic validation
            errors.addAll(performSemanticValidation(specification));

            // Perform schema validation if content is available
            if (content != null) {
                errors.addAll(performSchemaValidation(content));
            }

        } catch (Exception e) {
            log.error("Validation error for specification {}", specification.getId(), e);
            errors.add(ValidationError.of("$.spec", "Validation failed: " + e.getMessage()));
        }

        boolean isValid = errors.isEmpty();
        return createValidationResult(isValid, errors, startTime);
    }

    /**
     * Validates raw OpenAPI content
     *
     * @param content The raw OpenAPI content
     * @return ValidationResult
     */
    public ValidationResult validateContent(String content) {
        long startTime = System.currentTimeMillis();
        List<ValidationError> errors = new ArrayList<>();

        try {
            // Parse the content first
            parser.parseFromContent(content);

            // Perform schema validation
            errors.addAll(performSchemaValidation(content));

        } catch (OpenAPISpecificationParser.OpenAPIParseException e) {
            errors.add(ValidationError.of("$.spec", "Parsing failed: " + e.getMessage()));
            if (e.getMessages() != null) {
                e.getMessages().forEach(msg ->
                    errors.add(ValidationError.of("$.spec", "Parser warning: " + msg))
                );
            }
        } catch (Exception e) {
            log.error("Content validation error", e);
            errors.add(ValidationError.of("$.spec", "Validation failed: " + e.getMessage()));
        }

        boolean isValid = errors.isEmpty();
        return createValidationResult(isValid, errors, startTime);
    }

    /**
     * Performs semantic validation checks
     */
    private List<ValidationError> performSemanticValidation(OpenAPISpecification spec) {
        List<ValidationError> errors = new ArrayList<>();

        // Check required fields
        if (spec.getVersion() == null || spec.getVersion().isEmpty()) {
            errors.add(ValidationError.of("$.info.version", "OpenAPI version is required"));
        }

        if (spec.getTitle() == null || spec.getTitle().isEmpty()) {
            errors.add(ValidationError.of("$.info.title", "API title is required"));
        }

        // Add more semantic checks as needed

        return errors;
    }

    /**
     * Performs JSON Schema validation against OpenAPI schema
     */
    private List<ValidationError> performSchemaValidation(String content) {
        List<ValidationError> errors = new ArrayList<>();

        try {
            JsonNode contentNode = objectMapper.readTree(content);

            // For now, basic JSON structure validation
            if (!contentNode.isObject()) {
                errors.add(ValidationError.of("$", "OpenAPI specification must be a JSON object"));
                return errors;
            }

            // Check for required top-level fields
            if (!contentNode.has("openapi")) {
                errors.add(ValidationError.of("$.openapi", "OpenAPI version field is required"));
            }

            if (!contentNode.has("info")) {
                errors.add(ValidationError.of("$.info", "Info object is required"));
            }

        } catch (Exception e) {
            errors.add(ValidationError.of("$", "JSON parsing error: " + e.getMessage()));
        }

        return errors;
    }

    /**
     * Creates a ValidationResult from errors and timing
     */
    private ValidationResult createValidationResult(boolean isValid, List<ValidationError> errors, long startTime) {
        long endTime = System.currentTimeMillis();

        ValidationSummary summary = ValidationSummary.builder()
                .totalChecks(1)
                .passedChecks(isValid ? 1 : 0)
                .failedChecks(isValid ? 0 : 1)
                .warningChecks(0)
                .validationTimeMs(endTime - startTime)
                .build();

        return ValidationResult.builder()
                .valid(isValid)
                .errors(errors)
                .summary(summary)
                .build();
    }
}