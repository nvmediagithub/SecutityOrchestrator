package org.example.features.openapi.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenApiSecurityIssueResponse(
    String type,
    String severity,
    String description,
    String location
) {
}
