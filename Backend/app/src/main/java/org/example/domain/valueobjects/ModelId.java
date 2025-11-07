package org.example.domain.valueobjects;

public class ModelId {
    private final String value;

    private ModelId(String value) {
        this.value = value;
    }

    public static ModelId generate() {
        return new ModelId("model-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
    }

    public static ModelId fromString(String value) {
        return new ModelId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ModelId modelId = (ModelId) obj;
        return value.equals(modelId.value);
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