package org.example.features.openapi.application.usecases;

import org.example.features.openapi.domain.entities.OpenAPISpecification;
import java.util.Map;

/**
 * Use case for analyzing OpenAPI specifications.
 * This class contains the business logic for specification analysis.
 */
public interface AnalyzeOpenAPISpecUseCase {

    /**
     * Analyzes an OpenAPI specification and extracts key information
     *
     * @param specification The OpenAPI specification to analyze
     * @return AnalysisResult containing extracted information
     */
    AnalysisResult analyze(OpenAPISpecification specification);

    /**
     * Extracts endpoints and their operations from the specification
     *
     * @param specification The OpenAPI specification
     * @return Map of paths to their operations
     */
    Map<String, Map<String, Object>> extractEndpoints(OpenAPISpecification specification);

    /**
     * Extracts data models (schemas) from the specification
     *
     * @param specification The OpenAPI specification
     * @return Map of schema names to their definitions
     */
    Map<String, Map<String, Object>> extractSchemas(OpenAPISpecification specification);

    /**
     * Generates a summary report of the specification
     *
     * @param specification The OpenAPI specification
     * @return Summary report as formatted text
     */
    String generateSummaryReport(OpenAPISpecification specification);

    /**
     * Result class for analysis operations
     */
    class AnalysisResult {
        private final Map<String, Object> metadata;
        private final int endpointCount;
        private final int schemaCount;
        private final Map<String, Integer> operationsByMethod;

        public AnalysisResult(Map<String, Object> metadata, int endpointCount,
                            int schemaCount, Map<String, Integer> operationsByMethod) {
            this.metadata = metadata;
            this.endpointCount = endpointCount;
            this.schemaCount = schemaCount;
            this.operationsByMethod = operationsByMethod;
        }

        // Getters
        public Map<String, Object> getMetadata() { return metadata; }
        public int getEndpointCount() { return endpointCount; }
        public int getSchemaCount() { return schemaCount; }
        public Map<String, Integer> getOperationsByMethod() { return operationsByMethod; }
    }
}