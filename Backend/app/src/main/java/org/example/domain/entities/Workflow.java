package org.example.domain.entities;

import java.util.List;

public class Workflow {
    private org.example.domain.valueobjects.WorkflowId id;
    private String name;
    private Process process;
    private List<TestCase> testCases;
    private Object config;
    private org.example.domain.valueobjects.WorkflowStatus status;

    // Mock data for testing
    public static Workflow createMockWorkflow() {
        Workflow workflow = new Workflow();
        workflow.id = new org.example.domain.valueobjects.WorkflowId("mock-workflow-1");
        workflow.name = "Mock Workflow";
        workflow.process = Process.createMockProcess();
        workflow.status = org.example.domain.valueobjects.WorkflowStatus.DRAFT;
        return workflow;
    }
}