package org.example.domain.valueobjects;

public class TestCaseId {
    private final String value;

    public TestCaseId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TestCaseId testCaseId = (TestCaseId) obj;
        return value.equals(testCaseId.value);
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