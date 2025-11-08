package org.example.domain.dto.context;

import java.util.List;
import java.util.Map;

/**
 * DTO for API data schema information
 */
public class ApiDataSchema {
    private String schemaId;
    private String schemaName;
    private String schemaType;
    private String description;
    private Map<String, Object> properties;
    private List<String> requiredFields;
    private String format;
    private List<String> validationRules;
    private String source;
    private String version;

    public ApiDataSchema() {}

    public ApiDataSchema(String schemaId, String schemaName, String schemaType) {
        this.schemaId = schemaId;
        this.schemaName = schemaName;
        this.schemaType = schemaType;
    }

    // Getters and Setters
    public String getSchemaId() { return schemaId; }
    public void setSchemaId(String schemaId) { this.schemaId = schemaId; }

    public String getSchemaName() { return schemaName; }
    public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

    public String getSchemaType() { return schemaType; }
    public void setSchemaType(String schemaType) { this.schemaType = schemaType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }

    public List<String> getRequiredFields() { return requiredFields; }
    public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public List<String> getValidationRules() { return validationRules; }
    public void setValidationRules(List<String> validationRules) { this.validationRules = validationRules; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}