package org.example.domain.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCase {
    private org.example.domain.valueobjects.TestCaseId id;
    private String name;
    private org.example.domain.valueobjects.HttpMethod method;
    private String path;
    private Map<String, Object> parameters;
    private Object requestBody;
    private List<Object> expectedResponses;
    private Object testData;

    // Mock data for testing
    public static TestCase createMockTestCase() {
        TestCase testCase = new TestCase();
        testCase.id = new org.example.domain.valueobjects.TestCaseId("mock-test-case-1");
        testCase.name = "Mock Test Case";
        testCase.method = org.example.domain.valueobjects.HttpMethod.GET;
        testCase.path = "/api/test";
        testCase.parameters = new HashMap<>();
        return testCase;
    }
}