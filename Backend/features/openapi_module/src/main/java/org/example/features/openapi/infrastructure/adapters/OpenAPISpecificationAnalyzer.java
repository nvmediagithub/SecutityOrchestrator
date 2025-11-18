package org.example.features.openapi_module.infrastructure.adapters;

import org.example.features.openapi_module.application.usecases.AnalyzeOpenAPISpecUseCase;
import org.example.features.openapi_module.domain.entities.OpenAPISpecification;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Infrastructure adapter for analyzing OpenAPI specifications.
 * Extracts endpoints, schemas, and generates reports.
 */
@Slf4j
@Component
public class OpenAPISpecificationAnalyzer implements AnalyzeOpenAPISpecUseCase {

    private final OpenAPISpecificationParser parser;

    public OpenAPISpecificationAnalyzer(OpenAPISpecificationParser parser) {
        this.parser = parser;
    }

    @Override
    public AnalysisResult analyze(OpenAPISpecification specification) {
        try {
            // Convert domain entity to OpenAPI object for analysis
            String content = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(specification.getRawSpecification());
            OpenAPI openAPI = parser.parseFromContent(content);

            Map<String, Object> metadata = extractMetadata(openAPI);
            Map<String, Integer> operationsByMethod = countOperationsByMethod(openAPI);

            return new AnalysisResult(
                    metadata,
                    countEndpoints(openAPI),
                    countSchemas(openAPI),
                    operationsByMethod
            );
        } catch (Exception e) {
            log.error("Error analyzing specification {}", specification.getId(), e);
            return new AnalysisResult(Map.of(), 0, 0, Map.of());
        }
    }

    @Override
    public Map<String, Map<String, Object>> extractEndpoints(OpenAPISpecification specification) {
        Map<String, Map<String, Object>> endpoints = new HashMap<>();

        try {
            String content = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(specification.getRawSpecification());
            OpenAPI openAPI = parser.parseFromContent(content);

            if (openAPI.getPaths() != null) {
                openAPI.getPaths().forEach((path, pathItem) -> {
                    Map<String, Object> operations = new HashMap<>();
                    extractOperations(pathItem, operations);
                    endpoints.put(path, operations);
                });
            }
        } catch (Exception e) {
            log.error("Error extracting endpoints", e);
        }

        return endpoints;
    }

    @Override
    public Map<String, Map<String, Object>> extractSchemas(OpenAPISpecification specification) {
        Map<String, Map<String, Object>> schemas = new HashMap<>();

        try {
            String content = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(specification.getRawSpecification());
            OpenAPI openAPI = parser.parseFromContent(content);

            if (openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
                openAPI.getComponents().getSchemas().forEach((name, schema) -> {
                    // Convert schema to map representation
                    schemas.put(name, convertSchemaToMap(schema));
                });
            }
        } catch (Exception e) {
            log.error("Error extracting schemas", e);
        }

        return schemas;
    }

    @Override
    public String generateSummaryReport(OpenAPISpecification specification) {
        AnalysisResult result = analyze(specification);

        StringBuilder report = new StringBuilder();
        report.append("OpenAPI Specification Summary\n");
        report.append("=============================\n\n");

        report.append("Title: ").append(specification.getTitle()).append("\n");
        report.append("Version: ").append(specification.getVersion()).append("\n");
        report.append("Description: ").append(specification.getDescription()).append("\n\n");

        report.append("Statistics:\n");
        report.append("- Endpoints: ").append(result.getEndpointCount()).append("\n");
        report.append("- Schemas: ").append(result.getSchemaCount()).append("\n\n");

        report.append("Operations by HTTP Method:\n");
        result.getOperationsByMethod().forEach((method, count) ->
            report.append("- ").append(method.toUpperCase()).append(": ").append(count).append("\n")
        );

        return report.toString();
    }

    private Map<String, Object> extractMetadata(OpenAPI openAPI) {
        Map<String, Object> metadata = new HashMap<>();
        if (openAPI.getInfo() != null) {
            metadata.put("version", openAPI.getInfo().getVersion());
            metadata.put("title", openAPI.getInfo().getTitle());
            metadata.put("description", openAPI.getInfo().getDescription());
        }
        metadata.put("openapiVersion", openAPI.getOpenapi());
        return metadata;
    }

    private int countEndpoints(OpenAPI openAPI) {
        return openAPI.getPaths() != null ? openAPI.getPaths().size() : 0;
    }

    private int countSchemas(OpenAPI openAPI) {
        if (openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
            return openAPI.getComponents().getSchemas().size();
        }
        return 0;
    }

    private Map<String, Integer> countOperationsByMethod(OpenAPI openAPI) {
        Map<String, Integer> counts = new HashMap<>();

        if (openAPI.getPaths() != null) {
            openAPI.getPaths().values().forEach(pathItem -> {
                if (pathItem.getGet() != null) counts.merge("get", 1, Integer::sum);
                if (pathItem.getPost() != null) counts.merge("post", 1, Integer::sum);
                if (pathItem.getPut() != null) counts.merge("put", 1, Integer::sum);
                if (pathItem.getDelete() != null) counts.merge("delete", 1, Integer::sum);
                if (pathItem.getPatch() != null) counts.merge("patch", 1, Integer::sum);
                if (pathItem.getHead() != null) counts.merge("head", 1, Integer::sum);
                if (pathItem.getOptions() != null) counts.merge("options", 1, Integer::sum);
            });
        }

        return counts;
    }

    private void extractOperations(PathItem pathItem, Map<String, Object> operations) {
        addOperationIfPresent(operations, "get", pathItem.getGet());
        addOperationIfPresent(operations, "post", pathItem.getPost());
        addOperationIfPresent(operations, "put", pathItem.getPut());
        addOperationIfPresent(operations, "delete", pathItem.getDelete());
        addOperationIfPresent(operations, "patch", pathItem.getPatch());
        addOperationIfPresent(operations, "head", pathItem.getHead());
        addOperationIfPresent(operations, "options", pathItem.getOptions());
    }

    private void addOperationIfPresent(Map<String, Object> operations, String method, Operation operation) {
        if (operation != null) {
            Map<String, Object> opDetails = new HashMap<>();
            opDetails.put("summary", operation.getSummary());
            opDetails.put("description", operation.getDescription());
            opDetails.put("operationId", operation.getOperationId());
            operations.put(method.toLowerCase(), opDetails);
        }
    }

    private Map<String, Object> convertSchemaToMap(Schema<?> schema) {
        // Simplified schema conversion - in production, use proper serialization
        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", schema.getType());
        schemaMap.put("title", schema.getTitle());
        schemaMap.put("description", schema.getDescription());
        return schemaMap;
    }
}