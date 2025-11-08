package org.example.domain.entities.openapi;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Схема данных API
 */
public class ApiSchema {
    
    private String name;
    private String type; // object, array, string, number, boolean, integer
    private String format; // int32, int64, float, double, string, date, date-time, etc.
    private String description;
    private Object example;
    private Map<String, Object> examples;
    private Object defaultValue;
    private List<String> enumValues;
    private List<String> requiredFields;
    private Map<String, ApiSchema> properties;
    private ApiSchema items; // for arrays
    private Object minimum;
    private Object maximum;
    private String pattern;
    private int minLength;
    private int maxLength;
    private int minItems;
    private int maxItems;
    private boolean uniqueItems;
    private boolean nullable;
    private String discriminator;
    private Map<String, Object> externalDocs;
    private Map<String, Object> extensions;
    private String originalRef;
    private boolean deprecated;
    private String xml;
    
    public ApiSchema() {
        this.properties = new java.util.HashMap<>();
        this.enumValues = new ArrayList<>();
        this.requiredFields = new ArrayList<>();
        this.examples = new java.util.HashMap<>();
        this.extensions = new java.util.HashMap<>();
        this.externalDocs = new java.util.HashMap<>();
    }
    
    public ApiSchema(String name, String type) {
        this();
        this.name = name;
        this.type = type;
    }
    
    public void addProperty(String propertyName, ApiSchema property) {
        this.properties.put(propertyName, property);
    }
    
    public void addRequiredField(String field) {
        this.requiredFields.add(field);
    }
    
    public void addEnumValue(String value) {
        this.enumValues.add(value);
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Object getExample() {
        return example;
    }
    
    public void setExample(Object example) {
        this.example = example;
    }
    
    public Map<String, Object> getExamples() {
        return examples;
    }
    
    public void setExamples(Map<String, Object> examples) {
        this.examples = examples;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public List<String> getEnumValues() {
        return enumValues;
    }
    
    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }
    
    public List<String> getRequiredFields() {
        return requiredFields;
    }
    
    public void setRequiredFields(List<String> requiredFields) {
        this.requiredFields = requiredFields;
    }
    
    public Map<String, ApiSchema> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, ApiSchema> properties) {
        this.properties = properties;
    }
    
    public ApiSchema getItems() {
        return items;
    }
    
    public void setItems(ApiSchema items) {
        this.items = items;
    }
    
    public Object getMinimum() {
        return minimum;
    }
    
    public void setMinimum(Object minimum) {
        this.minimum = minimum;
    }
    
    public Object getMaximum() {
        return maximum;
    }
    
    public void setMaximum(Object maximum) {
        this.maximum = maximum;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public int getMinLength() {
        return minLength;
    }
    
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
    
    public int getMaxLength() {
        return maxLength;
    }
    
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    public int getMinItems() {
        return minItems;
    }
    
    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }
    
    public int getMaxItems() {
        return maxItems;
    }
    
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
    
    public boolean isUniqueItems() {
        return uniqueItems;
    }
    
    public void setUniqueItems(boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
    }
    
    public boolean isNullable() {
        return nullable;
    }
    
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    
    public String getDiscriminator() {
        return discriminator;
    }
    
    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }
    
    public Map<String, Object> getExternalDocs() {
        return externalDocs;
    }
    
    public void setExternalDocs(Map<String, Object> externalDocs) {
        this.externalDocs = externalDocs;
    }
    
    public Map<String, Object> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
    
    public String getOriginalRef() {
        return originalRef;
    }
    
    public void setOriginalRef(String originalRef) {
        this.originalRef = originalRef;
    }
    
    public boolean isDeprecated() {
        return deprecated;
    }
    
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
    
    public String getXml() {
        return xml;
    }
    
    public void setXml(String xml) {
        this.xml = xml;
    }
    
    @Override
    public String toString() {
        return "ApiSchema{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", format='" + format + '\'' +
                ", properties=" + properties.size() +
                '}';
    }
}