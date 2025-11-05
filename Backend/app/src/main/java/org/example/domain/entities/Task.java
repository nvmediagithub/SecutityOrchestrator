package org.example.domain.entities;

import java.util.HashMap;
import java.util.Map;

public class Task extends FlowElement {
    private org.example.domain.valueobjects.TaskType taskType;
    private Map<String, Object> properties;

    public Task() {
        this.properties = new HashMap<>();
    }

    public org.example.domain.valueobjects.TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(org.example.domain.valueobjects.TaskType taskType) {
        this.taskType = taskType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}