package org.example.domain.model.testdata;

import org.example.domain.model.testdata.enums.ChainState;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing chains of dependent data elements
 */
@Entity
@Table(name = "data_flow_chains")
public class DataFlowChain {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String chainId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false, length = 2000)
    private String context; // Context where this chain is used
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChainState state = ChainState.ACTIVE;
    
    @ElementCollection
    private List<String> dataElements = new ArrayList<>();
    
    @ElementCollection
    private List<String> executionOrder = new ArrayList<>();
    
    @Column(length = 2000)
    private String validationRules;
    
    @Column(length = 2000)
    private String preconditions;
    
    @Column(length = 2000)
    private String postconditions;
    
    private Boolean isParallelExecution = false;
    private Boolean isTransactional = false;
    private Boolean isReversible = false;
    
    @Column(length = 1000)
    private String triggerType; // MANUAL, AUTOMATIC, SCHEDULED, EVENT_DRIVEN
    
    @Column(length = 2000)
    private String triggerConditions;
    
    private Integer maxRetries = 0;
    private Integer retryDelaySeconds = 0;
    private Integer timeoutSeconds;
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection
    private Map<String, Object> metadata = new HashMap<>();
    
    private Integer usageCount = 0;
    private LocalDateTime lastUsed;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastExecuted;
    
    private String createdBy;
    private String lastModifiedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public DataFlowChain() {
        this.chainId = "DFC_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.dataElements = new ArrayList<>();
        this.executionOrder = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public DataFlowChain(String name, String context) {
        this();
        this.name = name;
        this.context = context;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getChainId() { return chainId; }
    public void setChainId(String chainId) { this.chainId = chainId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public ChainState getState() { return state; }
    public void setState(ChainState state) { this.state = state; }
    
    public List<String> getDataElements() { return dataElements; }
    public void setDataElements(List<String> dataElements) { this.dataElements = dataElements; }
    
    public List<String> getExecutionOrder() { return executionOrder; }
    public void setExecutionOrder(List<String> executionOrder) { this.executionOrder = executionOrder; }
    
    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }
    
    public String getPreconditions() { return preconditions; }
    public void setPreconditions(String preconditions) { this.preconditions = preconditions; }
    
    public String getPostconditions() { return postconditions; }
    public void setPostconditions(String postconditions) { this.postconditions = postconditions; }
    
    public Boolean getIsParallelExecution() { return isParallelExecution; }
    public void setIsParallelExecution(Boolean isParallelExecution) { this.isParallelExecution = isParallelExecution; }
    
    public Boolean getIsTransactional() { return isTransactional; }
    public void setIsTransactional(Boolean isTransactional) { this.isTransactional = isTransactional; }
    
    public Boolean getIsReversible() { return isReversible; }
    public void setIsReversible(Boolean isReversible) { this.isReversible = isReversible; }
    
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    
    public String getTriggerConditions() { return triggerConditions; }
    public void setTriggerConditions(String triggerConditions) { this.triggerConditions = triggerConditions; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public Integer getRetryDelaySeconds() { return retryDelaySeconds; }
    public void setRetryDelaySeconds(Integer retryDelaySeconds) { this.retryDelaySeconds = retryDelaySeconds; }
    
    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    
    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastExecuted() { return lastExecuted; }
    public void setLastExecuted(LocalDateTime lastExecuted) { this.lastExecuted = lastExecuted; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Helper methods
    public boolean isActive() {
        return state == ChainState.ACTIVE;
    }
    
    public boolean isValid() {
        return isActive() && 
               dataElements != null && !dataElements.isEmpty() &&
               (executionOrder == null || executionOrder.isEmpty() || 
                executionOrder.size() == dataElements.size());
    }
    
    public boolean canExecute() {
        return isValid() && state.isUsable() && !state.isTransient();
    }
    
    public boolean hasParallelExecution() {
        return isParallelExecution != null && isParallelExecution;
    }
    
    public boolean isTransactional() {
        return isTransactional != null && isTransactional;
    }
    
    public boolean hasTimeout() {
        return timeoutSeconds != null && timeoutSeconds > 0;
    }
    
    public boolean hasRetries() {
        return maxRetries != null && maxRetries > 0;
    }
    
    public boolean isReversible() {
        return isReversible != null && isReversible;
    }
    
    public void addDataElement(String elementId) {
        if (elementId != null && !dataElements.contains(elementId)) {
            dataElements.add(elementId);
        }
    }
    
    public void removeDataElement(String elementId) {
        dataElements.remove(elementId);
        executionOrder.remove(elementId);
    }
    
    public void updateExecutionOrder(List<String> orderedElements) {
        if (orderedElements != null &&
            new HashSet<>(orderedElements).equals(new HashSet<>(dataElements))) {
            this.executionOrder = new ArrayList<>(orderedElements);
        }
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsExecuted() {
        this.lastExecuted = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        incrementUsage();
    }
    
    public void setStateError(String errorMessage) {
        this.state = ChainState.ERROR;
        this.notes = (notes != null ? notes + "\n" : "") + "Error: " + errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setStateMaintenance() {
        this.state = ChainState.MAINTENANCE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setStateActive() {
        this.state = ChainState.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setStateDeprecated() {
        this.state = ChainState.DEPRECATED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "DataFlowChain{" +
                "chainId='" + chainId + '\'' +
                ", name='" + name + '\'' +
                ", context='" + context + '\'' +
                ", state=" + state +
                ", dataElementsCount=" + (dataElements != null ? dataElements.size() : 0) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFlowChain that = (DataFlowChain) o;
        return Objects.equals(chainId, that.chainId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(chainId);
    }
}