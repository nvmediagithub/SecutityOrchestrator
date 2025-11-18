package org.example.features.testdata.application;

import org.example.shared.domain.entities.DataField;
import org.example.shared.domain.entities.TestDataTemplate;
import org.example.features.testdata.domain.DataType;
import org.example.features.testdata.domain.FieldConstraint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Use case for generating test data based on templates and field definitions
 */
@Service
public class GenerateTestDataUseCase {

    private final org.example.features.testdata.domain.services.DataGenerationService dataGenerationService;
    private final org.example.features.testdata.domain.repositories.TestDataTemplateRepository templateRepository;
    private final org.example.features.testdata.domain.repositories.DataFieldRepository fieldRepository;

    public GenerateTestDataUseCase(org.example.features.testdata.domain.services.DataGenerationService dataGenerationService,
                                   org.example.features.testdata.domain.repositories.TestDataTemplateRepository templateRepository,
                                   org.example.features.testdata.domain.repositories.DataFieldRepository fieldRepository) {
        this.dataGenerationService = dataGenerationService;
        this.templateRepository = templateRepository;
        this.fieldRepository = fieldRepository;
    }

    /**
     * Generate test data based on a template
     */
    public GenerationResult generateFromTemplate(String templateId, GenerationRequest request) {
        TestDataTemplate template = templateRepository.findByTemplateId(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        if (!template.isActive()) {
            throw new IllegalStateException("Template is not active: " + templateId);
        }

        return dataGenerationService.generateFromTemplate(template, request);
    }

    /**
     * Generate test data based on field definitions
     */
    public GenerationResult generateFromFields(List<String> fieldIds, GenerationRequest request) {
        List<DataField> fields = fieldRepository.findByFieldIds(fieldIds);
        return dataGenerationService.generateFromFields(fields, request);
    }

    /**
     * Generate test data using custom configuration
     */
    public GenerationResult generateCustom(GenerationRequest request) {
        return dataGenerationService.generateCustom(request);
    }

    /**
     * Preview generated data without persisting
     */
    public GenerationResult previewGeneration(String templateId, GenerationRequest request) {
        TestDataTemplate template = templateRepository.findByTemplateId(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        return dataGenerationService.previewGeneration(template, request);
    }

    /**
     * Validate generation request
     */
    public ValidationResult validateRequest(GenerationRequest request) {
        return dataGenerationService.validateRequest(request);
    }

    /**
     * Get available generation strategies
     */
    public List<String> getAvailableStrategies() {
        return dataGenerationService.getAvailableStrategies();
    }

    // Inner classes for request/response
    public static class GenerationRequest {
        private int count = 1;
        private String strategy = "RANDOM";
        private Map<String, Object> parameters;
        private List<FieldOverride> fieldOverrides;
        private boolean includeMetadata = false;

        // Getters and setters
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

        public List<FieldOverride> getFieldOverrides() { return fieldOverrides; }
        public void setFieldOverrides(List<FieldOverride> fieldOverrides) { this.fieldOverrides = fieldOverrides; }

        public boolean isIncludeMetadata() { return includeMetadata; }
        public void setIncludeMetadata(boolean includeMetadata) { this.includeMetadata = includeMetadata; }
    }

    public static class FieldOverride {
        private String fieldName;
        private Object value;
        private List<FieldConstraint> constraints;

        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }

        public List<FieldConstraint> getConstraints() { return constraints; }
        public void setConstraints(List<FieldConstraint> constraints) { this.constraints = constraints; }
    }

    public static class GenerationResult {
        private boolean success;
        private List<Map<String, Object>> generatedData;
        private Map<String, Object> metadata;
        private List<String> errors;

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public List<Map<String, Object>> getGeneratedData() { return generatedData; }
        public void setGeneratedData(List<Map<String, Object>> generatedData) { this.generatedData = generatedData; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }

    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }
}