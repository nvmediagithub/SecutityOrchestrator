package org.example.domain.entities;

public abstract class FlowElement {
    protected org.example.domain.valueobjects.ElementId id;
    protected String name;
    protected org.example.domain.valueobjects.ElementType type;

    // Mock data
    public static FlowElement createMockTask() {
        Task task = new Task();
        task.id = new org.example.domain.valueobjects.ElementId("mock-task-1");
        task.name = "Mock Task";
        task.type = org.example.domain.valueobjects.ElementType.TASK;
        return task;
    }
}