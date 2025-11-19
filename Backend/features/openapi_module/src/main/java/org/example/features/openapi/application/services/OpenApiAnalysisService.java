package org.example.features.openapi.application.services;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.servers.Server;
import org.example.features.openapi.application.dto.OpenApiAnalysisResponse;
import org.example.features.openapi.application.dto.OpenApiSecurityIssueResponse;
import org.example.features.openapi.domain.entities.OpenAPISpecification;
import org.example.features.openapi.domain.entities.ValidationResult;
import org.example.features.openapi_module.application.usecases.AnalyzeOpenAPISpecUseCase;
import org.example.features.openapi_module.infrastructure.adapters.OpenAPISpecificationParser;
import org.example.features.openapi_module.infrastructure.adapters.OpenAPISpecificationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OpenApiAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiAnalysisService.class);

    private final OpenAPISpecificationParser parser;
    private final OpenAPISpecificationValidator validator;
    private final AnalyzeOpenAPISpecUseCase analyzeUseCase;

    public OpenApiAnalysisService(
        OpenAPISpecificationParser parser,
        OpenAPISpecificationValidator validator,
        AnalyzeOpenAPISpecUseCase analyzeUseCase
    ) {
        this.parser = parser;
        this.validator = validator;
        this.analyzeUseCase = analyzeUseCase;
    }

    public Result analyze(String rawContent, String fallbackName) {
        try {
            OpenAPI openAPI = parser.parseFromContent(rawContent);
            OpenAPISpecification specification = parser.toDomain(openAPI);
            specification.setId(UUID.randomUUID().toString());

            if (!StringUtils.hasText(specification.getTitle()) && StringUtils.hasText(fallbackName)) {
                specification.setTitle(fallbackName);
            }

            ValidationResult validationResult = validator.validate(specification);
            AnalyzeOpenAPISpecUseCase.AnalysisResult analysisResult = analyzeUseCase.analyze(specification);
            Map<String, Map<String, Object>> endpoints = analyzeUseCase.extractEndpoints(specification);
            Map<String, Map<String, Object>> schemas = analyzeUseCase.extractSchemas(specification);

            List<OpenApiSecurityIssueResponse> securityIssues = detectSecurityIssues(openAPI);
            List<String> recommendations = buildRecommendations(validationResult, securityIssues, analysisResult);

            OpenApiAnalysisResponse response = OpenApiAnalysisResponse.from(
                specification,
                validationResult,
                analysisResult,
                endpoints,
                schemas,
                securityIssues,
                recommendations,
                rawContent
            );

            return new Result(specification, response);
        } catch (OpenAPISpecificationParser.OpenAPIParseException e) {
            LOGGER.warn("Unable to parse OpenAPI specification: {}", e.getMessage());
            throw new InvalidOpenApiSpecificationException(
                "Unable to parse OpenAPI specification: " + e.getMessage(), e
            );
        } catch (Exception e) {
            LOGGER.error("Failed to analyze OpenAPI specification", e);
            throw new InvalidOpenApiSpecificationException(
                "Failed to analyze OpenAPI specification: " + e.getMessage(), e
            );
        }
    }

    private List<OpenApiSecurityIssueResponse> detectSecurityIssues(OpenAPI openAPI) {
        List<OpenApiSecurityIssueResponse> issues = new ArrayList<>();

        if (openAPI.getComponents() == null
            || openAPI.getComponents().getSecuritySchemes() == null
            || openAPI.getComponents().getSecuritySchemes().isEmpty()) {
            issues.add(new OpenApiSecurityIssueResponse(
                "security-schemes",
                "HIGH",
                "Specification does not define any security schemes.",
                "components.securitySchemes"
            ));
        }

        if (openAPI.getServers() != null) {
            for (Server server : openAPI.getServers()) {
                if (server.getUrl() != null && server.getUrl().startsWith("http://")) {
                    issues.add(new OpenApiSecurityIssueResponse(
                        "server-transport",
                        "MEDIUM",
                        "Server URL uses insecure HTTP transport: " + server.getUrl(),
                        "servers"
                    ));
                }
            }
        }

        if (openAPI.getPaths() != null) {
            openAPI.getPaths().forEach((path, pathItem) -> {
                Map<PathItem.HttpMethod, Operation> operations = new EnumMap<>(PathItem.HttpMethod.class);
                if (pathItem.getGet() != null) operations.put(PathItem.HttpMethod.GET, pathItem.getGet());
                if (pathItem.getPost() != null) operations.put(PathItem.HttpMethod.POST, pathItem.getPost());
                if (pathItem.getPut() != null) operations.put(PathItem.HttpMethod.PUT, pathItem.getPut());
                if (pathItem.getDelete() != null) operations.put(PathItem.HttpMethod.DELETE, pathItem.getDelete());
                if (pathItem.getPatch() != null) operations.put(PathItem.HttpMethod.PATCH, pathItem.getPatch());
                if (pathItem.getHead() != null) operations.put(PathItem.HttpMethod.HEAD, pathItem.getHead());
                if (pathItem.getOptions() != null) operations.put(PathItem.HttpMethod.OPTIONS, pathItem.getOptions());

                operations.forEach((method, operation) -> {
                    if (!StringUtils.hasText(operation.getDescription()) && !StringUtils.hasText(operation.getSummary())) {
                        issues.add(new OpenApiSecurityIssueResponse(
                            "operation-documentation",
                            "LOW",
                            "Operation lacks description or summary.",
                            method + " " + path
                        ));
                    }
                });
            });
        }

        return issues;
    }

    private List<String> buildRecommendations(
        ValidationResult validationResult,
        List<OpenApiSecurityIssueResponse> securityIssues,
        AnalyzeOpenAPISpecUseCase.AnalysisResult analysisResult
    ) {
        List<String> recommendations = new ArrayList<>();

        if (validationResult != null && !validationResult.isValid()) {
            recommendations.add("Resolve " + validationResult.getErrorCount() + " validation issues.");
        }
        if (securityIssues != null && !securityIssues.isEmpty()) {
            recommendations.add("Review security configuration for " + securityIssues.size() + " findings.");
        }
        if (analysisResult != null && analysisResult.getEndpointCount() == 0) {
            recommendations.add("Add at least one endpoint to the specification.");
        }
        if (analysisResult != null && analysisResult.getSchemaCount() == 0) {
            recommendations.add("Define schemas to describe request and response payloads.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("Specification looks healthy. Keep documenting security and endpoint details.");
        }
        return recommendations;
    }

    public record Result(OpenAPISpecification specification, OpenApiAnalysisResponse response) {
    }

    public static class InvalidOpenApiSpecificationException extends RuntimeException {
        public InvalidOpenApiSpecificationException(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidOpenApiSpecificationException(String message) {
            super(message);
        }
    }
}
