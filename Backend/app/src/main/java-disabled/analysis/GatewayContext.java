package org.example.domain.entities.analysis;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Контекст шлюзов (gateway) в BPMN процессе
 */
public class GatewayContext {
    private final String gatewayId;
    private final String gatewayType;
    private final String gatewayName;
    private final String condition;
    private final List<String> outgoingFlows;
    private final List<String> incomingFlows;
    private final Boolean isExclusive;
    private final Boolean isParallel;
    private final Boolean isInclusive;
    private final Integer priority;
    private final String defaultFlow;
    
    public GatewayContext(Builder builder) {
        this.gatewayId = builder.gatewayId != null ? builder.gatewayId : UUID.randomUUID().toString();
        this.gatewayType = builder.gatewayType != null ? builder.gatewayType : "UNKNOWN";
        this.gatewayName = builder.gatewayName != null ? builder.gatewayName : "";
        this.condition = builder.condition;
        this.outgoingFlows = builder.outgoingFlows != null ? builder.outgoingFlows : new ArrayList<>();
        this.incomingFlows = builder.incomingFlows != null ? builder.incomingFlows : new ArrayList<>();
        this.isExclusive = builder.isExclusive != null ? builder.isExclusive : false;
        this.isParallel = builder.isParallel != null ? builder.isParallel : false;
        this.isInclusive = builder.isInclusive != null ? builder.isInclusive : false;
        this.priority = builder.priority != null ? builder.priority : 0;
        this.defaultFlow = builder.defaultFlow;
    }
    
    // Getters
    public String getGatewayId() { return gatewayId; }
    public String getGatewayType() { return gatewayType; }
    public String getGatewayName() { return gatewayName; }
    public String getCondition() { return condition; }
    public List<String> getOutgoingFlows() { return outgoingFlows; }
    public List<String> getIncomingFlows() { return incomingFlows; }
    public Boolean getIsExclusive() { return isExclusive; }
    public Boolean getIsParallel() { return isParallel; }
    public Boolean getIsInclusive() { return isInclusive; }
    public Integer getPriority() { return priority; }
    public String getDefaultFlow() { return defaultFlow; }
    
    // Utility methods
    public boolean isDecisionGateway() {
        return "DECISION".equals(gatewayType) || "EXCLUSIVE".equals(gatewayType);
    }
    
    public boolean isParallelGateway() {
        return "PARALLEL".equals(gatewayType) || isParallel;
    }
    
    public boolean hasConditions() {
        return condition != null && !condition.trim().isEmpty();
    }
    
    public int getFlowCount() {
        return outgoingFlows.size();
    }
    
    public boolean isComplex() {
        return getFlowCount() > 2 || priority > 1;
    }
    
    public static class Builder {
        private String gatewayId;
        private String gatewayType;
        private String gatewayName;
        private String condition;
        private List<String> outgoingFlows;
        private List<String> incomingFlows;
        private Boolean isExclusive;
        private Boolean isParallel;
        private Boolean isInclusive;
        private Integer priority;
        private String defaultFlow;
        
        public Builder gatewayId(String gatewayId) {
            this.gatewayId = gatewayId;
            return this;
        }
        
        public Builder gatewayType(String gatewayType) {
            this.gatewayType = gatewayType;
            return this;
        }
        
        public Builder gatewayName(String gatewayName) {
            this.gatewayName = gatewayName;
            return this;
        }
        
        public Builder condition(String condition) {
            this.condition = condition;
            return this;
        }
        
        public Builder outgoingFlows(List<String> outgoingFlows) {
            this.outgoingFlows = outgoingFlows;
            return this;
        }
        
        public Builder incomingFlows(List<String> incomingFlows) {
            this.incomingFlows = incomingFlows;
            return this;
        }
        
        public Builder isExclusive(Boolean isExclusive) {
            this.isExclusive = isExclusive;
            return this;
        }
        
        public Builder isParallel(Boolean isParallel) {
            this.isParallel = isParallel;
            return this;
        }
        
        public Builder isInclusive(Boolean isInclusive) {
            this.isInclusive = isInclusive;
            return this;
        }
        
        public Builder priority(Integer priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder defaultFlow(String defaultFlow) {
            this.defaultFlow = defaultFlow;
            return this;
        }
        
        public GatewayContext build() {
            return new GatewayContext(this);
        }
    }
}