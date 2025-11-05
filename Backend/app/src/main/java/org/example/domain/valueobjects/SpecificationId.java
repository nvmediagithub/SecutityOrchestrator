package org.example.domain.valueobjects;

public class SpecificationId {
    private final String value;

    public SpecificationId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SpecificationId specId = (SpecificationId) obj;
        return value.equals(specId.value);
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