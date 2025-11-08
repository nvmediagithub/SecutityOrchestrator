package org.example.domain.model.testdata.valueobjects;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Value object representing constraints for test data generation
 */
public final class DataConstraints {
    private final Map<String, Object> fieldConstraints;
    private final Map<String, Pattern> regexPatterns;
    private final Map<String, List<Object>> allowedValues;
    private final Map<String, List<Object>> excludedValues;
    private final Map<String, Comparable<?>> minValues;
    private final Map<String, Comparable<?>> maxValues;
    private final Map<String, Integer> minLengths;
    private final Map<String, Integer> maxLengths;
    private final Map<String, Boolean> requiredFields;
    private final Map<String, String> customValidationRules;
    private final Set<String> maskedFields;
    private final Set<String> sensitiveFields;
    private final boolean allowNull;
    private final boolean allowEmpty;
    
    private DataConstraints(Builder builder) {
        this.fieldConstraints = new HashMap<>(builder.fieldConstraints);
        this.regexPatterns = new HashMap<>(builder.regexPatterns);
        this.allowedValues = new HashMap<>(builder.allowedValues);
        this.excludedValues = new HashMap<>(builder.excludedValues);
        this.minValues = new HashMap<>(builder.minValues);
        this.maxValues = new HashMap<>(builder.maxValues);
        this.minLengths = new HashMap<>(builder.minLengths);
        this.maxLengths = new HashMap<>(builder.maxLengths);
        this.requiredFields = new HashMap<>(builder.requiredFields);
        this.customValidationRules = new HashMap<>(builder.customValidationRules);
        this.maskedFields = new HashSet<>(builder.maskedFields);
        this.sensitiveFields = new HashSet<>(builder.sensitiveFields);
        this.allowNull = builder.allowNull;
        this.allowEmpty = builder.allowEmpty;
    }
    
    /**
     * Creates empty constraints
     */
    public static DataConstraints empty() {
        return new Builder().build();
    }
    
    /**
     * Creates constraints for personal data
     */
    public static DataConstraints personalData() {
        return new Builder()
                .allowNull(false)
                .allowEmpty(false)
                .addSensitiveField("name")
                .addSensitiveField("email")
                .addSensitiveField("phone")
                .addSensitiveField("address")
                .addMaskedField("email")
                .addMaskedField("phone")
                .build();
    }
    
    /**
     * Creates constraints for financial data
     */
    public static DataConstraints financialData() {
        return new Builder()
                .allowNull(false)
                .allowEmpty(false)
                .addSensitiveField("accountNumber")
                .addSensitiveField("cardNumber")
                .addSensitiveField("cvv")
                .addMaskedField("accountNumber")
                .addMaskedField("cardNumber")
                .addMaskedField("cvv")
                .withRegexPattern("cardNumber", Pattern.compile("\\d{16}"))
                .withRegexPattern("cvv", Pattern.compile("\\d{3,4}"))
                .build();
    }
    
    /**
     * Creates constraints for API data
     */
    public static DataConstraints apiData() {
        return new Builder()
                .allowNull(false)
                .allowEmpty(false)
                .addRequiredField("endpoint", true)
                .addRequiredField("method", true)
                .addRequiredField("statusCode", true)
                .withMinValue("statusCode", 100)
                .withMaxValue("statusCode", 599)
                .build();
    }
    
    public Map<String, Object> getFieldConstraints() {
        return new HashMap<>(fieldConstraints);
    }
    
    public Map<String, Pattern> getRegexPatterns() {
        return new HashMap<>(regexPatterns);
    }
    
    public Map<String, List<Object>> getAllowedValues() {
        return new HashMap<>(allowedValues);
    }
    
    public Map<String, List<Object>> getExcludedValues() {
        return new HashMap<>(excludedValues);
    }
    
    public Map<String, Comparable<?>> getMinValues() {
        return new HashMap<>(minValues);
    }
    
    public Map<String, Comparable<?>> getMaxValues() {
        return new HashMap<>(maxValues);
    }
    
    public Map<String, Integer> getMinLengths() {
        return new HashMap<>(minLengths);
    }
    
    public Map<String, Integer> getMaxLengths() {
        return new HashMap<>(maxLengths);
    }
    
    public Map<String, Boolean> getRequiredFields() {
        return new HashMap<>(requiredFields);
    }
    
    public Map<String, String> getCustomValidationRules() {
        return new HashMap<>(customValidationRules);
    }
    
    public Set<String> getMaskedFields() {
        return new HashSet<>(maskedFields);
    }
    
    public Set<String> getSensitiveFields() {
        return new HashSet<>(sensitiveFields);
    }
    
    public boolean isAllowNull() {
        return allowNull;
    }
    
    public boolean isAllowEmpty() {
        return allowEmpty;
    }
    
    /**
     * Validates a field value against constraints
     */
    public ValidationResult validateField(String fieldName, Object value) {
        List<String> errors = new ArrayList<>();
        
        // Check if field is required
        if (requiredFields.containsKey(fieldName) && requiredFields.get(fieldName)) {
            if (value == null) {
                errors.add("Field '" + fieldName + "' is required but is null");
                return ValidationResult.failure(errors);
            }
            if (!allowEmpty && value instanceof String && ((String) value).trim().isEmpty()) {
                errors.add("Field '" + fieldName + "' is required but is empty");
                return ValidationResult.failure(errors);
            }
        }
        
        // If value is null and nulls are not allowed
        if (value == null && !allowNull) {
            errors.add("Field '" + fieldName + "' cannot be null");
            return ValidationResult.failure(errors);
        }
        
        // If value is null and nulls are allowed, no further validation needed
        if (value == null) {
            return ValidationResult.success();
        }
        
        // Check allowed values
        if (allowedValues.containsKey(fieldName)) {
            if (!allowedValues.get(fieldName).contains(value)) {
                errors.add("Field '" + fieldName + "' value '" + value + "' is not in allowed values");
            }
        }
        
        // Check excluded values
        if (excludedValues.containsKey(fieldName)) {
            if (excludedValues.get(fieldName).contains(value)) {
                errors.add("Field '" + fieldName + "' value '" + value + "' is in excluded values");
            }
        }
        
        // Check minimum value
        if (minValues.containsKey(fieldName) && value instanceof Comparable) {
            Comparable comparable = (Comparable) value;
            if (comparable.compareTo(minValues.get(fieldName)) < 0) {
                errors.add("Field '" + fieldName + "' value '" + value + "' is less than minimum '" + minValues.get(fieldName) + "'");
            }
        }
        
        // Check maximum value
        if (maxValues.containsKey(fieldName) && value instanceof Comparable) {
            Comparable comparable = (Comparable) value;
            if (comparable.compareTo(maxValues.get(fieldName)) > 0) {
                errors.add("Field '" + fieldName + "' value '" + value + "' is greater than maximum '" + maxValues.get(fieldName) + "'");
            }
        }
        
        // Check string length constraints
        if (value instanceof String) {
            String stringValue = (String) value;
            if (minLengths.containsKey(fieldName) && stringValue.length() < minLengths.get(fieldName)) {
                errors.add("Field '" + fieldName + "' length '" + stringValue.length() + "' is less than minimum '" + minLengths.get(fieldName) + "'");
            }
            if (maxLengths.containsKey(fieldName) && stringValue.length() > maxLengths.get(fieldName)) {
                errors.add("Field '" + fieldName + "' length '" + stringValue.length() + "' is greater than maximum '" + maxLengths.get(fieldName) + "'");
            }
        }
        
        // Check regex pattern
        if (regexPatterns.containsKey(fieldName) && value instanceof String) {
            if (!regexPatterns.get(fieldName).matcher((String) value).matches()) {
                errors.add("Field '" + fieldName + "' does not match required pattern");
            }
        }
        
        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }
    
    /**
     * Checks if a field should be masked
     */
    public boolean isFieldMasked(String fieldName) {
        return maskedFields.contains(fieldName);
    }
    
    /**
     * Checks if a field is sensitive
     */
    public boolean isFieldSensitive(String fieldName) {
        return sensitiveFields.contains(fieldName);
    }
    
    /**
     * Checks if a field is required
     */
    public boolean isFieldRequired(String fieldName) {
        return requiredFields.getOrDefault(fieldName, false);
    }
    
    /**
     * Result of validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, Collections.emptyList());
        }
        
        public static ValidationResult failure(List<String> errors) {
            return new ValidationResult(false, errors);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
        
        public String getErrorMessage() {
            return errors.stream().collect(Collectors.joining("; "));
        }
    }
    
    /**
     * Builder for DataConstraints
     */
    public static class Builder {
        private Map<String, Object> fieldConstraints = new HashMap<>();
        private Map<String, Pattern> regexPatterns = new HashMap<>();
        private Map<String, List<Object>> allowedValues = new HashMap<>();
        private Map<String, List<Object>> excludedValues = new HashMap<>();
        private Map<String, Comparable<?>> minValues = new HashMap<>();
        private Map<String, Comparable<?>> maxValues = new HashMap<>();
        private Map<String, Integer> minLengths = new HashMap<>();
        private Map<String, Integer> maxLengths = new HashMap<>();
        private Map<String, Boolean> requiredFields = new HashMap<>();
        private Map<String, String> customValidationRules = new HashMap<>();
        private Set<String> maskedFields = new HashSet<>();
        private Set<String> sensitiveFields = new HashSet<>();
        private boolean allowNull = true;
        private boolean allowEmpty = true;
        
        public Builder withFieldConstraint(String fieldName, Object constraint) {
            this.fieldConstraints.put(fieldName, constraint);
            return this;
        }
        
        public Builder withRegexPattern(String fieldName, Pattern pattern) {
            this.regexPatterns.put(fieldName, pattern);
            return this;
        }
        
        public Builder withAllowedValues(String fieldName, List<Object> values) {
            this.allowedValues.put(fieldName, new ArrayList<>(values));
            return this;
        }
        
        public Builder withExcludedValues(String fieldName, List<Object> values) {
            this.excludedValues.put(fieldName, new ArrayList<>(values));
            return this;
        }
        
        public Builder withMinValue(String fieldName, Comparable<?> value) {
            this.minValues.put(fieldName, value);
            return this;
        }
        
        public Builder withMaxValue(String fieldName, Comparable<?> value) {
            this.maxValues.put(fieldName, value);
            return this;
        }
        
        public Builder withMinLength(String fieldName, int length) {
            this.minLengths.put(fieldName, length);
            return this;
        }
        
        public Builder withMaxLength(String fieldName, int length) {
            this.maxLengths.put(fieldName, length);
            return this;
        }
        
        public Builder addRequiredField(String fieldName, boolean required) {
            this.requiredFields.put(fieldName, required);
            return this;
        }
        
        public Builder withCustomValidationRule(String fieldName, String rule) {
            this.customValidationRules.put(fieldName, rule);
            return this;
        }
        
        public Builder addMaskedField(String fieldName) {
            this.maskedFields.add(fieldName);
            return this;
        }
        
        public Builder addSensitiveField(String fieldName) {
            this.sensitiveFields.add(fieldName);
            return this;
        }
        
        public Builder allowNull(boolean allowNull) {
            this.allowNull = allowNull;
            return this;
        }
        
        public Builder allowEmpty(boolean allowEmpty) {
            this.allowEmpty = allowEmpty;
            return this;
        }
        
        public DataConstraints build() {
            return new DataConstraints(this);
        }
    }
}