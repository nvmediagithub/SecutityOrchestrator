package org.example.infrastructure.services.bpmn.context;

import java.util.List;

/**
 * Gateway context for BPMN analysis
 */
public class GatewayContext {
    private String id;
    private String name;
    private String type;
    private String direction;
    private List<String> incomingFlows;
    private List<String> outgoingFlows;
    private boolean hasDefaultFlow;

    public GatewayContext() {}

    public GatewayContext(String id, String name, String type, String direction) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<String> getIncomingFlows() {
        return incomingFlows;
    }

    public void setIncomingFlows(List<String> incomingFlows) {
        this.incomingFlows = incomingFlows;
    }

    public List<String> getOutgoingFlows() {
        return outgoingFlows;
    }

    public void setOutgoingFlows(List<String> outgoingFlows) {
        this.outgoingFlows = outgoingFlows;
    }

    public boolean isHasDefaultFlow() {
        return hasDefaultFlow;
    }

    public void setHasDefaultFlow(boolean hasDefaultFlow) {
        this.hasDefaultFlow = hasDefaultFlow;
    }
}