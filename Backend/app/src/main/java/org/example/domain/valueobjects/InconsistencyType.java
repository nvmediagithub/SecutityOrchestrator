package org.example.domain.valueobjects;

/**
 * Enumeration of different types of inconsistencies found in OpenAPI specifications
 */
public enum InconsistencyType {
    SCHEMA_INCONSISTENCY("Inconsistent schema definitions"),
    RESPONSE_STATUS_CONFLICT("Conflicting response status codes"),
    PARAMETER_INCONSISTENCY("Inconsistent parameter definitions"),
    AUTHENTICATION_INCONSISTENCY("Inconsistent authentication requirements"),
    CONTENT_TYPE_MISMATCH("Content type mismatches between request/response"),
    DATA_TYPE_INCONSISTENCY("Inconsistent data types"),
    REQUIRED_FIELDS_MISSING("Missing required fields in different places"),
    EXAMPLE_INCONSISTENCY("Inconsistent examples"),
    DESCRIPTION_INCONSISTENCY("Inconsistent descriptions"),
    VERSION_INCOMPATIBILITY("Version compatibility issues"),
    NAMESPACE_COLLISION("Namespace or naming conflicts"),
    CIRCULAR_REFERENCE("Circular references in schema"),
    DUPLICATE_DEFINITION("Duplicate definitions"),
    METADATA_INCONSISTENCY("Inconsistent metadata");

    private final String description;

    InconsistencyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}