package org.example.features.openapi_module.infrastructure.adapters;

import org.example.features.openapi.domain.entities.OpenAPISpecification;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Infrastructure adapter for parsing OpenAPI specifications.
 * Uses the Swagger parser library to parse JSON/YAML content into OpenAPI objects.
 */
@Slf4j
@Component
public class OpenAPISpecificationParser {

    private final OpenAPIV3Parser parser;

    public OpenAPISpecificationParser() {
        this.parser = new OpenAPIV3Parser();
    }

    /**
     * Parses raw OpenAPI content (JSON/YAML) into an OpenAPI object
     *
     * @param content The raw OpenAPI specification content
     * @return Parsed OpenAPI object
     * @throws OpenAPIParseException if parsing fails
     */
    public OpenAPI parseFromContent(String content) throws OpenAPIParseException {
        try {
            var result = parser.readContents(content, null, null);
            if (result.getMessages() != null && !result.getMessages().isEmpty()) {
                log.warn("Parser warnings: {}", result.getMessages());
            }

            if (result.getOpenAPI() == null) {
                throw new OpenAPIParseException("Failed to parse OpenAPI content", result.getMessages());
            }

            return result.getOpenAPI();
        } catch (Exception e) {
            log.error("Error parsing OpenAPI content", e);
            throw new OpenAPIParseException("Failed to parse OpenAPI content: " + e.getMessage(), e);
        }
    }

    /**
     * Parses OpenAPI content and converts it to domain entity
     *
     * @param content The raw OpenAPI specification content
     * @return OpenAPISpecification domain entity
     * @throws OpenAPIParseException if parsing fails
     */
    public OpenAPISpecification parseToDomainEntity(String content) throws OpenAPIParseException {
        OpenAPI openAPI = parseFromContent(content);
        return toDomain(openAPI);
    }

    /**
     * Converts an OpenAPI object to the domain entity representation.
     */
    public OpenAPISpecification toDomain(OpenAPI openAPI) {
        return OpenAPISpecification.builder()
                .version(openAPI.getOpenapi())
                .title(openAPI.getInfo() != null ? openAPI.getInfo().getTitle() : null)
                .description(openAPI.getInfo() != null ? openAPI.getInfo().getDescription() : null)
                .rawSpecification(convertToMap(openAPI))
                .build();
    }

    /**
     * Validates if content can be parsed as OpenAPI
     *
     * @param content The content to validate
     * @return true if content is valid OpenAPI, false otherwise
     */
    public boolean isValidOpenAPI(String content) {
        try {
            parseFromContent(content);
            return true;
        } catch (OpenAPIParseException e) {
            return false;
        }
    }

    /**
     * Converts OpenAPI object to Map representation for domain use
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(OpenAPI openAPI) {
        // This is a simplified conversion - in production, use proper serialization
        try {
            var objectMapper = new ObjectMapper();
            var json = objectMapper.writeValueAsString(openAPI);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("Error converting OpenAPI to Map", e);
            return Map.of();
        }
    }

    /**
     * Exception thrown when OpenAPI parsing fails
     */
    public static class OpenAPIParseException extends Exception {
        private final java.util.List<String> messages;

        public OpenAPIParseException(String message, java.util.List<String> messages) {
            super(message);
            this.messages = messages;
        }

        public OpenAPIParseException(String message, Throwable cause) {
            super(message, cause);
            this.messages = null;
        }

        public java.util.List<String> getMessages() {
            return messages;
        }
    }
}
