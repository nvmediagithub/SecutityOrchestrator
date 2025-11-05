package org.example.domain.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiSpecification {
    private org.example.domain.valueobjects.SpecificationId id;
    private String title;
    private org.example.domain.valueobjects.Version version;
    private org.example.domain.valueobjects.OpenApiVersion openApiVersion;
    private Map<String, Object> paths;
    private Object components;
    private List<Object> servers;

    // Mock data for testing
    public static ApiSpecification createMockSpecification() {
        ApiSpecification spec = new ApiSpecification();
        spec.id = new org.example.domain.valueobjects.SpecificationId("mock-spec-1");
        spec.title = "Mock API Specification";
        spec.version = new org.example.domain.valueobjects.Version(1, 0, 0);
        spec.openApiVersion = org.example.domain.valueobjects.OpenApiVersion.V3_0_3;
        spec.paths = new HashMap<>();
        return spec;
    }
}