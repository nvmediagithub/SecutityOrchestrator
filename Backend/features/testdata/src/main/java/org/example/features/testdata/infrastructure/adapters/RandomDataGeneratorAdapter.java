package org.example.features.testdata.infrastructure.adapters;

import org.example.features.testdata.application.GenerateTestDataUseCase;
import org.example.features.testdata.domain.DataType;
import org.example.features.testdata.domain.services.DataGenerationService;
import org.example.shared.domain.entities.DataField;
import org.example.shared.domain.entities.TestDataTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Infrastructure adapter for generating random test data
 */
@Component
public class RandomDataGeneratorAdapter implements DataGenerationService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String ALPHANUMERIC = ALPHABET + NUMBERS;

    @Override
    public GenerateTestDataUseCase.GenerationResult generateFromTemplate(TestDataTemplate template,
                                                                          GenerateTestDataUseCase.GenerationRequest request) {
        GenerateTestDataUseCase.GenerationResult result = new GenerateTestDataUseCase.GenerationResult();
        List<Map<String, Object>> generatedData = new ArrayList<>();

        try {
            for (int i = 0; i < request.getCount(); i++) {
                Map<String, Object> dataItem = generateFromTemplateStructure(template);
                applyFieldOverrides(dataItem, request.getFieldOverrides());
                generatedData.add(dataItem);
            }

            result.setSuccess(true);
            result.setGeneratedData(generatedData);
            result.setMetadata(createMetadata(request, template.getTemplateId()));

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrors(Arrays.asList("Generation failed: " + e.getMessage()));
        }

        return result;
    }

    @Override
    public GenerateTestDataUseCase.GenerationResult generateFromFields(List<DataField> fields,
                                                                         GenerateTestDataUseCase.GenerationRequest request) {
        GenerateTestDataUseCase.GenerationResult result = new GenerateTestDataUseCase.GenerationResult();
        List<Map<String, Object>> generatedData = new ArrayList<>();

        try {
            for (int i = 0; i < request.getCount(); i++) {
                Map<String, Object> dataItem = new HashMap<>();
                for (DataField field : fields) {
                    if (field.isActive()) {
                        Object value = generateValueForField(field, request);
                        dataItem.put(field.getFieldName(), value);
                    }
                }
                applyFieldOverrides(dataItem, request.getFieldOverrides());
                generatedData.add(dataItem);
            }

            result.setSuccess(true);
            result.setGeneratedData(generatedData);
            result.setMetadata(createMetadata(request, "custom"));

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrors(Arrays.asList("Generation failed: " + e.getMessage()));
        }

        return result;
    }

    @Override
    public GenerateTestDataUseCase.GenerationResult generateCustom(GenerateTestDataUseCase.GenerationRequest request) {
        // For custom generation, we need field specifications in the request parameters
        GenerateTestDataUseCase.GenerationResult result = new GenerateTestDataUseCase.GenerationResult();
        result.setSuccess(false);
        result.setErrors(Arrays.asList("Custom generation not implemented for random strategy"));
        return result;
    }

    @Override
    public GenerateTestDataUseCase.GenerationResult previewGeneration(TestDataTemplate template,
                                                                        GenerateTestDataUseCase.GenerationRequest request) {
        GenerateTestDataUseCase.GenerationRequest previewRequest = new GenerateTestDataUseCase.GenerationRequest();
        previewRequest.setCount(1);
        previewRequest.setStrategy(request.getStrategy());
        previewRequest.setParameters(request.getParameters());
        previewRequest.setFieldOverrides(request.getFieldOverrides());
        previewRequest.setIncludeMetadata(true);

        return generateFromTemplate(template, previewRequest);
    }

    @Override
    public GenerateTestDataUseCase.ValidationResult validateRequest(GenerateTestDataUseCase.GenerationRequest request) {
        GenerateTestDataUseCase.ValidationResult result = new GenerateTestDataUseCase.ValidationResult();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (request.getCount() <= 0) {
            errors.add("Count must be positive");
        }

        if (request.getCount() > 10000) {
            errors.add("Count cannot exceed 10000");
        }

        if (request.getStrategy() == null || request.getStrategy().trim().isEmpty()) {
            warnings.add("Strategy not specified, using default");
        }

        result.setValid(errors.isEmpty());
        result.setErrors(errors);
        result.setWarnings(warnings);

        return result;
    }

    @Override
    public List<String> getAvailableStrategies() {
        return Arrays.asList("RANDOM", "SEQUENTIAL", "WEIGHTED");
    }

    private Map<String, Object> generateFromTemplateStructure(TestDataTemplate template) {
        Map<String, Object> data = new HashMap<>();
        // This would parse the template structure and generate data accordingly
        // For now, return empty map - would need proper template parsing logic
        return data;
    }

    private Object generateValueForField(DataField field, GenerateTestDataUseCase.GenerationRequest request) {
        DataType dataType = DataType.fromString(field.getDataType());

        switch (dataType) {
            case STRING:
                return generateRandomString(field.getMinLength(), field.getMaxLength());
            case INTEGER:
                return generateRandomInteger(field.getMinValue(), field.getMaxValue());
            case DECIMAL:
                return generateRandomDecimal(field.getMinValue(), field.getMaxValue());
            case BOOLEAN:
                return ThreadLocalRandom.current().nextBoolean();
            case DATE:
                return generateRandomDate();
            case DATETIME:
                return generateRandomDateTime();
            case EMAIL:
                return generateRandomEmail();
            case PHONE:
                return generateRandomPhone();
            case URL:
                return generateRandomUrl();
            case UUID:
                return java.util.UUID.randomUUID().toString();
            default:
                return generateRandomString(5, 20);
        }
    }

    private String generateRandomString(Integer minLength, Integer maxLength) {
        int length = calculateLength(minLength, maxLength);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(ThreadLocalRandom.current().nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private Integer generateRandomInteger(String minValue, String maxValue) {
        int min = minValue != null ? Integer.parseInt(minValue) : 0;
        int max = maxValue != null ? Integer.parseInt(maxValue) : 1000;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private Double generateRandomDecimal(String minValue, String maxValue) {
        double min = minValue != null ? Double.parseDouble(minValue) : 0.0;
        double max = maxValue != null ? Double.parseDouble(maxValue) : 1000.0;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private String generateRandomDate() {
        LocalDate start = LocalDate.of(2000, 1, 1);
        LocalDate end = LocalDate.now();
        long days = start.toEpochDay() + ThreadLocalRandom.current().nextLong(end.toEpochDay() - start.toEpochDay());
        return LocalDate.ofEpochDay(days).toString();
    }

    private String generateRandomDateTime() {
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        long seconds = start.toEpochSecond(java.time.ZoneOffset.UTC) +
                       ThreadLocalRandom.current().nextLong(end.toEpochSecond(java.time.ZoneOffset.UTC) - start.toEpochSecond(java.time.ZoneOffset.UTC));
        return LocalDateTime.ofEpochSecond(seconds, 0, java.time.ZoneOffset.UTC).toString();
    }

    private String generateRandomEmail() {
        String username = generateRandomString(5, 10).toLowerCase();
        String domain = generateRandomString(5, 8).toLowerCase();
        return username + "@" + domain + ".com";
    }

    private String generateRandomPhone() {
        StringBuilder sb = new StringBuilder("+");
        for (int i = 0; i < 11; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    private String generateRandomUrl() {
        String protocol = ThreadLocalRandom.current().nextBoolean() ? "https" : "http";
        String domain = generateRandomString(5, 10).toLowerCase();
        return protocol + "://" + domain + ".com";
    }

    private int calculateLength(Integer minLength, Integer maxLength) {
        int min = minLength != null ? minLength : 5;
        int max = maxLength != null ? maxLength : 20;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void applyFieldOverrides(Map<String, Object> data,
                                   List<GenerateTestDataUseCase.FieldOverride> overrides) {
        if (overrides != null) {
            for (GenerateTestDataUseCase.FieldOverride override : overrides) {
                data.put(override.getFieldName(), override.getValue());
            }
        }
    }

    private Map<String, Object> createMetadata(GenerateTestDataUseCase.GenerationRequest request, String source) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("strategy", request.getStrategy());
        metadata.put("count", request.getCount());
        metadata.put("source", source);
        metadata.put("generatedAt", LocalDateTime.now());
        metadata.put("generator", "RandomDataGeneratorAdapter");
        return metadata;
    }
}