package org.example.domain.entities;

import java.time.Instant;
import java.util.List;

public class Process {
    public org.example.domain.valueobjects.ProcessId id;
    private String name;
    private org.example.domain.valueobjects.Version version;
    private List<FlowElement> elements;
    private org.example.domain.valueobjects.ProcessStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    // Mock data for testing
    public static Process createMockProcess() {
        Process process = new Process();
        process.id = new org.example.domain.valueobjects.ProcessId("mock-process-1");
        process.name = "Mock BPMN Process";
        process.version = new org.example.domain.valueobjects.Version(1, 0, 0);
        process.status = org.example.domain.valueobjects.ProcessStatus.ACTIVE;
        process.createdAt = Instant.now();
        process.updatedAt = Instant.now();
        return process;
    }
}