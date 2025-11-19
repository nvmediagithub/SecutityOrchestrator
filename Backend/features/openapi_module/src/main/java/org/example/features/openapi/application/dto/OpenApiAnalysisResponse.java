package org.example.features.openapi.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.features.openapi.domain.entities.OpenAPISpecification;
import org.example.features.openapi.domain.entities.ValidationError;
import org.example.features.openapi.domain.entities.ValidationResult;
import org.example.features.openapi_module.application.usecases.AnalyzeOpenAPISpecUseCase;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenApiAnalysisResponse(
    String specificationName,
    String version,
    boolean valid,
    Map<String, Object> metadata,
    OpenApiValidationSummaryResponse validationSummary,
    List<OpenApiValidationErrorResponse> validationErrors,
    int endpointCount,
    int schemaCount,
    Map<String, Integer> operationsByMethod,
    Map<String, Map<String, Object>> endpoints,
    Map<String, Map<String, Object>> schemas,
    List<OpenApiSecurityIssueResponse> securityIssues,
    List<String> recommendations,
    String openapiContent,
    Instant analyzedAt
) {

    public static OpenApiAnalysisResponse from(
        OpenAPISpecification specification,
        ValidationResult validationResult,
        AnalyzeOpenAPISpecUseCase.AnalysisResult analysisResult,
        Map<String, Map<String, Object>> endpoints,
        Map<String, Map<String, Object>> schemas,
        List<OpenApiSecurityIssueResponse> securityIssues,
        List<String> recommendations,
        String rawContent
    ) {
        return new OpenApiAnalysisResponse(
            specification.getTitle(),
            specification.getVersion(),
            validationResult != null && validationResult.isValid(),
            analysisResult != null && analysisResult.getMetadata() != null
                ? analysisResult.getMetadata()
                : Collections.emptyMap(),
            OpenApiValidationSummaryResponse.from(validationResult != null ? validationResult.getSummary() : null),
            mapValidationErrors(validationResult != null ? validationResult.getErrors() : null),
            analysisResult != null ? analysisResult.getEndpointCount() : 0,
            analysisResult != null ? analysisResult.getSchemaCount() : 0,
            analysisResult != null && analysisResult.getOperationsByMethod() != null
                ? analysisResult.getOperationsByMethod()
                : Collections.emptyMap(),
            endpoints != null ? endpoints : Collections.emptyMap(),
            schemas != null ? schemas : Collections.emptyMap(),
            securityIssues != null ? securityIssues : Collections.emptyList(),
            recommendations != null ? recommendations : Collections.emptyList(),
            rawContent,
            Instant.now()
        );
    }

    private static List<OpenApiValidationErrorResponse> mapValidationErrors(List<ValidationError> errors) {
        if (errors == null) {
            return Collections.emptyList();
        }
        return errors.stream()
            .map(OpenApiValidationErrorResponse::from)
            .collect(Collectors.toList());
    }
}
