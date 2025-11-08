package org.example.domain.dto.dependency;

import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * Represents BPMN process dependencies and data flow
 */
public class BpmnDependency {
    private String sourceTaskId;
    private String targetTaskId;
    private String sourceTaskName;
    private String targetTaskName;
    private String processId;
    private String processName;
    private DependencyType type;
    private Set<String> createdData;
    private Set<String> consumedData;
    private Set<String> contextData;
    private String gatewayCondition;
    private String eventTrigger;
    private String eventResult;
    private String subprocessId;
    private String callActivityId;
    private String processVariable;
    private String dataObjectReference;
    private String messageFlow;
    private String sequenceFlow;
    private String userJourneyStep;
    private String processState;
    private String dataStateTransition;
    private String businessRule;
    private List<String> workflowDependencies;
    private String exceptionHandlingData;
    
    public enum DependencyType {
        TASK_TO_TASK,
        GATEWAY_DATA,
        EVENT_DATA,
        SUBPROCESS_DATA,
        CALL_ACTIVITY_DATA,
        SEQUENCE_DATA,
        MESSAGE_DATA,
        PROCESS_VARIABLE,
        DATA_OBJECT,
        USER_JOURNEY,
        STATE_TRANSITION,
        EXCEPTION_HANDLING
    }
    
    public BpmnDependency() {}
    
    public BpmnDependency(String sourceTaskId, String targetTaskId, String processId, DependencyType type) {
        this.sourceTaskId = sourceTaskId;
        this.targetTaskId = targetTaskId;
        this.processId = processId;
        this.type = type;
    }
    
    // Getters and Setters
    public String getSourceTaskId() {
        return sourceTaskId;
    }
    
    public void setSourceTaskId(String sourceTaskId) {
        this.sourceTaskId = sourceTaskId;
    }
    
    public String getTargetTaskId() {
        return targetTaskId;
    }
    
    public void setTargetTaskId(String targetTaskId) {
        this.targetTaskId = targetTaskId;
    }
    
    public String getSourceTaskName() {
        return sourceTaskName;
    }
    
    public void setSourceTaskName(String sourceTaskName) {
        this.sourceTaskName = sourceTaskName;
    }
    
    public String getTargetTaskName() {
        return targetTaskName;
    }
    
    public void setTargetTaskName(String targetTaskName) {
        this.targetTaskName = targetTaskName;
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public String getProcessName() {
        return processName;
    }
    
    public void setProcessName(String processName) {
        this.processName = processName;
    }
    
    public DependencyType getType() {
        return type;
    }
    
    public void setType(DependencyType type) {
        this.type = type;
    }
    
    public Set<String> getCreatedData() {
        return createdData;
    }
    
    public void setCreatedData(Set<String> createdData) {
        this.createdData = createdData;
    }
    
    public Set<String> getConsumedData() {
        return consumedData;
    }
    
    public void setConsumedData(Set<String> consumedData) {
        this.consumedData = consumedData;
    }
    
    public Set<String> getContextData() {
        return contextData;
    }
    
    public void setContextData(Set<String> contextData) {
        this.contextData = contextData;
    }
    
    public String getGatewayCondition() {
        return gatewayCondition;
    }
    
    public void setGatewayCondition(String gatewayCondition) {
        this.gatewayCondition = gatewayCondition;
    }
    
    public String getEventTrigger() {
        return eventTrigger;
    }
    
    public void setEventTrigger(String eventTrigger) {
        this.eventTrigger = eventTrigger;
    }
    
    public String getEventResult() {
        return eventResult;
    }
    
    public void setEventResult(String eventResult) {
        this.eventResult = eventResult;
    }
    
    public String getSubprocessId() {
        return subprocessId;
    }
    
    public void setSubprocessId(String subprocessId) {
        this.subprocessId = subprocessId;
    }
    
    public String getCallActivityId() {
        return callActivityId;
    }
    
    public void setCallActivityId(String callActivityId) {
        this.callActivityId = callActivityId;
    }
    
    public String getProcessVariable() {
        return processVariable;
    }
    
    public void setProcessVariable(String processVariable) {
        this.processVariable = processVariable;
    }
    
    public String getDataObjectReference() {
        return dataObjectReference;
    }
    
    public void setDataObjectReference(String dataObjectReference) {
        this.dataObjectReference = dataObjectReference;
    }
    
    public String getMessageFlow() {
        return messageFlow;
    }
    
    public void setMessageFlow(String messageFlow) {
        this.messageFlow = messageFlow;
    }
    
    public String getSequenceFlow() {
        return sequenceFlow;
    }
    
    public void setSequenceFlow(String sequenceFlow) {
        this.sequenceFlow = sequenceFlow;
    }
    
    public String getUserJourneyStep() {
        return userJourneyStep;
    }
    
    public void setUserJourneyStep(String userJourneyStep) {
        this.userJourneyStep = userJourneyStep;
    }
    
    public String getProcessState() {
        return processState;
    }
    
    public void setProcessState(String processState) {
        this.processState = processState;
    }
    
    public String getDataStateTransition() {
        return dataStateTransition;
    }
    
    public void setDataStateTransition(String dataStateTransition) {
        this.dataStateTransition = dataStateTransition;
    }
    
    public String getBusinessRule() {
        return businessRule;
    }
    
    public void setBusinessRule(String businessRule) {
        this.businessRule = businessRule;
    }
    
    public List<String> getWorkflowDependencies() {
        return workflowDependencies;
    }
    
    public void setWorkflowDependencies(List<String> workflowDependencies) {
        this.workflowDependencies = workflowDependencies;
    }
    
    public String getExceptionHandlingData() {
        return exceptionHandlingData;
    }
    
    public void setExceptionHandlingData(String exceptionHandlingData) {
        this.exceptionHandlingData = exceptionHandlingData;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BpmnDependency that = (BpmnDependency) o;
        return Objects.equals(sourceTaskId, that.sourceTaskId) &&
                Objects.equals(targetTaskId, that.targetTaskId) &&
                Objects.equals(processId, that.processId) &&
                type == that.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sourceTaskId, targetTaskId, processId, type);
    }
    
    @Override
    public String toString() {
        return "BpmnDependency{" +
                "sourceTaskId='" + sourceTaskId + '\'' +
                ", targetTaskId='" + targetTaskId + '\'' +
                ", processId='" + processId + '\'' +
                ", type=" + type +
                '}';
    }
}