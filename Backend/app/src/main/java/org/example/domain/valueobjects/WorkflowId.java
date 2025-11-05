package org.example.domain.valueobjects;

public class WorkflowId {
    private final String value;

    public WorkflowId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WorkflowId workflowId = (WorkflowId) obj;
        return value.equals(workflowId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}