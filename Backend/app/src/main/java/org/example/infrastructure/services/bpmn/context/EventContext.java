package org.example.infrastructure.services.bpmn.context;

public class EventContext {
    private String id;
    private String name;
    private String type;
    
    public EventContext() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
