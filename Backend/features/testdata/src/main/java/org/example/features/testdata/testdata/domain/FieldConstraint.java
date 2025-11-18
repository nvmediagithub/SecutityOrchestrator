package org.example.features.testdata.domain;

import java.util.regex.Pattern;

/**
 * Value object representing constraints for data field validation
 */
public class FieldConstraint {

    private final String constraintType; // REQUIRED, MIN_LENGTH, MAX_LENGTH, PATTERN, RANGE, etc.
    private final String constraintValue;
    private final String errorMessage;

    public FieldConstraint(String constraintType, String constraintValue) {
        this(constraintType, constraintValue, null);
    }

    public FieldConstraint(String constraintType, String constraintValue, String errorMessage) {
        this.constraintType = constraintType;
        this.constraintValue = constraintValue;
        this.errorMessage = errorMessage != null ? errorMessage : generateDefaultErrorMessage();
    }

    public String getConstraintType() {
        return constraintType;
    }

    public String getConstraintValue() {
        return constraintValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean validate(String value) {
        if (value == null) {
            return constraintType.equals("OPTIONAL");
        }

        switch (constraintType.toUpperCase()) {
            case "REQUIRED":
                return !value.trim().isEmpty();
            case "MIN_LENGTH":
                return value.length() >= Integer.parseInt(constraintValue);
            case "MAX_LENGTH":
                return value.length() <= Integer.parseInt(constraintValue);
            case "EXACT_LENGTH":
                return value.length() == Integer.parseInt(constraintValue);
            case "PATTERN":
                return Pattern.matches(constraintValue, value);
            case "MIN_VALUE":
                return Double.parseDouble(value) >= Double.parseDouble(constraintValue);
            case "MAX_VALUE":
                return Double.parseDouble(value) <= Double.parseDouble(constraintValue);
            case "RANGE":
                String[] range = constraintValue.split("-");
                double numValue = Double.parseDouble(value);
                return numValue >= Double.parseDouble(range[0]) && numValue <= Double.parseDouble(range[1]);
            case "EMAIL":
                return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", value);
            case "PHONE":
                return Pattern.matches("^[+]?[0-9\\s\\-\\(\\)]{10,15}$", value);
            case "URL":
                return Pattern.matches("^(http|https)://.*$", value);
            case "UUID":
                return Pattern.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", value);
            case "OPTIONAL":
                return true;
            default:
                return true;
        }
    }

    private String generateDefaultErrorMessage() {
        switch (constraintType.toUpperCase()) {
            case "REQUIRED": return "This field is required";
            case "MIN_LENGTH": return "Minimum length is " + constraintValue;
            case "MAX_LENGTH": return "Maximum length is " + constraintValue;
            case "EXACT_LENGTH": return "Length must be exactly " + constraintValue;
            case "PATTERN": return "Value does not match required pattern";
            case "MIN_VALUE": return "Minimum value is " + constraintValue;
            case "MAX_VALUE": return "Maximum value is " + constraintValue;
            case "RANGE": return "Value must be between " + constraintValue.replace("-", " and ");
            case "EMAIL": return "Invalid email format";
            case "PHONE": return "Invalid phone number format";
            case "URL": return "Invalid URL format";
            case "UUID": return "Invalid UUID format";
            default: return "Validation failed";
        }
    }

    @Override
    public String toString() {
        return constraintType + ":" + constraintValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FieldConstraint that = (FieldConstraint) obj;
        return constraintType.equals(that.constraintType) && constraintValue.equals(that.constraintValue);
    }

    @Override
    public int hashCode() {
        return constraintType.hashCode() * 31 + constraintValue.hashCode();
    }
}