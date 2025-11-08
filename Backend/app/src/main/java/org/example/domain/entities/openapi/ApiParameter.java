package org.example.domain.entities.openapi;

import org.example.domain.valueobjects.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Параметр API эндпоинта
 */
public class ApiParameter {
    
    private String name;
    private String in; // query, path, header, cookie
    private boolean required;
    private String description;
    private boolean deprecated;
    private boolean allowEmptyValue;
    private boolean allowReserved;
    private String style;
    private boolean explode;
    private boolean allowReservedValue;
    private String schema;
    private Map<String, Object> examples;
    private Object example;
    private Map<String, Object> content;
    private String originalRef;
    
    public ApiParameter() {
        this.examples = new java.util.HashMap<>();
        this.content = new java.util.HashMap<>();
    }
    
    public ApiParameter(String name, String in, boolean required) {
        this();
        this.name = name;
        this.in = in;
        this.required = required;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIn() {
        return in;
    }
    
    public void setIn(String in) {
        this.in = in;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isDeprecated() {
        return deprecated;
    }
    
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
    
    public boolean isAllowEmptyValue() {
        return allowEmptyValue;
    }
    
    public void setAllowEmptyValue(boolean allowEmptyValue) {
        this.allowEmptyValue = allowEmptyValue;
    }
    
    public boolean isAllowReserved() {
        return allowReserved;
    }
    
    public void setAllowReserved(boolean allowReserved) {
        this.allowReserved = allowReserved;
    }
    
    public String getStyle() {
        return style;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public boolean isExplode() {
        return explode;
    }
    
    public void setExplode(boolean explode) {
        this.explode = explode;
    }
    
    public boolean isAllowReservedValue() {
        return allowReservedValue;
    }
    
    public void setAllowReservedValue(boolean allowReservedValue) {
        this.allowReservedValue = allowReservedValue;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    public Map<String, Object> getExamples() {
        return examples;
    }
    
    public void setExamples(Map<String, Object> examples) {
        this.examples = examples;
    }
    
    public Object getExample() {
        return example;
    }
    
    public void setExample(Object example) {
        this.example = example;
    }
    
    public Map<String, Object> getContent() {
        return content;
    }
    
    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
    
    public String getOriginalRef() {
        return originalRef;
    }
    
    public void setOriginalRef(String originalRef) {
        this.originalRef = originalRef;
    }
    
    @Override
    public String toString() {
        return "ApiParameter{" +
                "name='" + name + '\'' +
                ", in='" + in + '\'' +
                ", required=" + required +
                ", schema='" + schema + '\'' +
                '}';
    }
}