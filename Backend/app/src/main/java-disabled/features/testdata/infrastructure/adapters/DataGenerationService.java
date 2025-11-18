package org.example.features.testdata.infrastructure.adapters;

import org.example.features.testdata.application.GenerateTestDataUseCase;
import org.example.features.testdata.domain.*;

import java.util.List;

/**
 * Port interface for data generation services
 */
public interface DataGenerationService {

    /**
     * Generate test data from a template
     */
    GenerateTestDataUseCase.GenerationResult generateFromTemplate(TestDataTemplate template,
                                                                   GenerateTestDataUseCase.GenerationRequest request);

    /**
     * Generate test data from field definitions
     */
    GenerateTestDataUseCase.GenerationResult generateFromFields(List<DataField> fields,
                                                                 GenerateTestDataUseCase.GenerationRequest request);

    /**
     * Generate custom test data
     */
    GenerateTestDataUseCase.GenerationResult generateCustom(GenerateTestDataUseCase.GenerationRequest request);

    /**
     * Preview generation without persisting
     */
    GenerateTestDataUseCase.GenerationResult previewGeneration(TestDataTemplate template,
                                                                GenerateTestDataUseCase.GenerationRequest request);

    /**
     * Validate generation request
     */
    GenerateTestDataUseCase.ValidationResult validateRequest(GenerateTestDataUseCase.GenerationRequest request);

    /**
     * Get available generation strategies
     */
    List<String> getAvailableStrategies();
}