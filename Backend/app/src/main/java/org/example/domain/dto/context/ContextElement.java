package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a context element that combines information from different sources
 */
public class ContextElement {
    private String elementId;
    private String name;
    private String type;
    private Object source;
    private Map<String, Object> properties;
    private LocalDateTime createdAt;
    private List<String> tags;
    private String description;
    
    public ContextElement() {
        this.elementId = "element_" + System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
    }
    
    public ContextElement(String name, String type, Object source) {
        this();
        this.name = name;
        this.type = type;
        this.source = source;
    }
    
    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Object getSource() { return source; }
    public void setSource(Object source) { this.source = source; }
    
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}