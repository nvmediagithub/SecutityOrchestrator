package org.example.features.testdata.presentation;

import org.example.features.testdata.application.GenerateTestDataUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for test data generation endpoints
 */
@RestController
@RequestMapping("/api/testdata")
public class TestDataController {

    private final GenerateTestDataUseCase generateTestDataUseCase;

    public TestDataController(GenerateTestDataUseCase generateTestDataUseCase) {
        this.generateTestDataUseCase = generateTestDataUseCase;
    }

    /**
     * Generate test data from a template
     */
    @PostMapping("/generate/template/{templateId}")
    public ResponseEntity<GenerateTestDataUseCase.GenerationResult> generateFromTemplate(
            @PathVariable String templateId,
            @RequestBody GenerateTestDataRequest request) {

        GenerateTestDataUseCase.GenerationRequest genRequest = mapToGenerationRequest(request);
        GenerateTestDataUseCase.GenerationResult result = generateTestDataUseCase.generateFromTemplate(templateId, genRequest);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Generate test data from field definitions
     */
    @PostMapping("/generate/fields")
    public ResponseEntity<GenerateTestDataUseCase.GenerationResult> generateFromFields(
            @RequestBody GenerateFromFieldsRequest request) {

        GenerateTestDataUseCase.GenerationRequest genRequest = mapToGenerationRequest(request.getGenerationRequest());
        GenerateTestDataUseCase.GenerationResult result = generateTestDataUseCase.generateFromFields(request.getFieldIds(), genRequest);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Preview generation without persisting
     */
    @PostMapping("/preview/template/{templateId}")
    public ResponseEntity<GenerateTestDataUseCase.GenerationResult> previewGeneration(
            @PathVariable String templateId,
            @RequestBody GenerateTestDataRequest request) {

        GenerateTestDataUseCase.GenerationRequest genRequest = mapToGenerationRequest(request);
        GenerateTestDataUseCase.GenerationResult result = generateTestDataUseCase.previewGeneration(templateId, genRequest);

        return ResponseEntity.ok(result);
    }

    /**
     * Validate generation request
     */
    @PostMapping("/validate")
    public ResponseEntity<GenerateTestDataUseCase.ValidationResult> validateRequest(
            @RequestBody GenerateTestDataRequest request) {

        GenerateTestDataUseCase.GenerationRequest genRequest = mapToGenerationRequest(request);
        GenerateTestDataUseCase.ValidationResult result = generateTestDataUseCase.validateRequest(genRequest);

        return ResponseEntity.ok(result);
    }

    /**
     * Get available generation strategies
     */
    @GetMapping("/strategies")
    public ResponseEntity<List<String>> getAvailableStrategies() {
        List<String> strategies = generateTestDataUseCase.getAvailableStrategies();
        return ResponseEntity.ok(strategies);
    }

    private GenerateTestDataUseCase.GenerationRequest mapToGenerationRequest(GenerateTestDataRequest request) {
        GenerateTestDataUseCase.GenerationRequest genRequest = new GenerateTestDataUseCase.GenerationRequest();
        genRequest.setCount(request.getCount() != null ? request.getCount() : 1);
        genRequest.setStrategy(request.getStrategy() != null ? request.getStrategy() : "RANDOM");
        genRequest.setParameters(request.getParameters());
        genRequest.setIncludeMetadata(request.isIncludeMetadata());

        if (request.getFieldOverrides() != null) {
            // Map field overrides if needed
        }

        return genRequest;
    }

    // DTOs
    public static class GenerateTestDataRequest {
        private Integer count = 1;
        private String strategy = "RANDOM";
        private Map<String, Object> parameters;
        private List<FieldOverrideRequest> fieldOverrides;
        private boolean includeMetadata = false;

        // Getters and setters
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }

        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

        public List<FieldOverrideRequest> getFieldOverrides() { return fieldOverrides; }
        public void setFieldOverrides(List<FieldOverrideRequest> fieldOverrides) { this.fieldOverrides = fieldOverrides; }

        public boolean isIncludeMetadata() { return includeMetadata; }
        public void setIncludeMetadata(boolean includeMetadata) { this.includeMetadata = includeMetadata; }
    }

    public static class FieldOverrideRequest {
        private String fieldName;
        private Object value;

        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
    }

    public static class GenerateFromFieldsRequest {
        private List<String> fieldIds;
        private GenerateTestDataRequest generationRequest;

        // Getters and setters
        public List<String> getFieldIds() { return fieldIds; }
        public void setFieldIds(List<String> fieldIds) { this.fieldIds = fieldIds; }

        public GenerateTestDataRequest getGenerationRequest() { return generationRequest; }
        public void setGenerationRequest(GenerateTestDataRequest generationRequest) { this.generationRequest = generationRequest; }
    }
}