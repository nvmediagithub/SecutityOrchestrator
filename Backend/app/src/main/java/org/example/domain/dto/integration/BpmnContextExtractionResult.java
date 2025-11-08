package org.example.domain.dto.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Результат извлечения контекста из BPMN процессов
 */
public class BpmnContextExtractionResult {
    
    private String extractionId;
    private String diagramId;
    private LocalDateTime extractionStartTime;
    private LocalDateTime extractionEndTime;
    private long processingTimeMs;
    private String status; // SUCCESS, FAILED, PARTIAL
    private String errorMessage;
    
    // Контекст процессов
    private List<ProcessVariable> processVariables;
    private List<TaskInputOutput> taskInputs;
    private List<TaskInputOutput> taskOutputs;
    private List<GatewayCondition> gatewayConditions;
    private List<EventData> eventDataFlows;
    
    // Бизнес-контекст
    private List<BusinessRule> businessRules;
    private List<DecisionPoint> decisionPoints;
    private List<UserInteractionPattern> userInteractionPatterns;
    private List<SystemIntegrationPoint> systemIntegrationPoints;
    
    // Структурный контекст
    private List<BpmnTask> tasks;
    private List<BpmnGateway> gateways;
    private List<BpmnEvent> events;
    private List<BpmnSequenceFlow> sequenceFlows;
    private List<BpmnProcess> processes;
    
    // Данные для генерации
    private List<DataTemplate> dataTemplates;
    private List<GenerationContext> generationContexts;
    private Map<String, ProcessContext> processContexts;
    
    // Метрики
    private ExtractionMetrics metrics;
    
    public BpmnContextExtractionResult() {
        this.extractionStartTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getExtractionId() { return extractionId; }
    public void setExtractionId(String extractionId) { this.extractionId = extractionId; }
    
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public LocalDateTime getExtractionStartTime() { return extractionStartTime; }
    public void setExtractionStartTime(LocalDateTime extractionStartTime) { this.extractionStartTime = extractionStartTime; }
    
    public LocalDateTime getExtractionEndTime() { return extractionEndTime; }
    public void setExtractionEndTime(LocalDateTime extractionEndTime) { this.extractionEndTime = extractionEndTime; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public List<ProcessVariable> getProcessVariables() { return processVariables; }
    public void setProcessVariables(List<ProcessVariable> processVariables) { this.processVariables = processVariables; }
    
    public List<TaskInputOutput> getTaskInputs() { return taskInputs; }
    public void setTaskInputs(List<TaskInputOutput> taskInputs) { this.taskInputs = taskInputs; }
    
    public List<TaskInputOutput> getTaskOutputs() { return taskOutputs; }
    public void setTaskOutputs(List<TaskInputOutput> taskOutputs) { this.taskOutputs = taskOutputs; }
    
    public List<GatewayCondition> getGatewayConditions() { return gatewayConditions; }
    public void setGatewayConditions(List<GatewayCondition> gatewayConditions) { this.gatewayConditions = gatewayConditions; }
    
    public List<EventData> getEventDataFlows() { return eventDataFlows; }
    public void setEventDataFlows(List<EventData> eventDataFlows) { this.eventDataFlows = eventDataFlows; }
    
    public List<BusinessRule> getBusinessRules() { return businessRules; }
    public void setBusinessRules(List<BusinessRule> businessRules) { this.businessRules = businessRules; }
    
    public List<DecisionPoint> getDecisionPoints() { return decisionPoints; }
    public void setDecisionPoints(List<DecisionPoint> decisionPoints) { this.decisionPoints = decisionPoints; }
    
    public List<UserInteractionPattern> getUserInteractionPatterns() { return userInteractionPatterns; }
    public void setUserInteractionPatterns(List<UserInteractionPattern> userInteractionPatterns) { this.userInteractionPatterns = userInteractionPatterns; }
    
    public List<SystemIntegrationPoint> getSystemIntegrationPoints() { return systemIntegrationPoints; }
    public void setSystemIntegrationPoints(List<SystemIntegrationPoint> systemIntegrationPoints) { this.systemIntegrationPoints = systemIntegrationPoints; }
    
    public List<BpmnTask> getTasks() { return tasks; }
    public void setTasks(List<BpmnTask> tasks) { this.tasks = tasks; }
    
    public List<BpmnGateway> getGateways() { return gateways; }
    public void setGateways(List<BpmnGateway> gateways) { this.gateways = gateways; }
    
    public List<BpmnEvent> getEvents() { return events; }
    public void setEvents(List<BpmnEvent> events) { this.events = events; }
    
    public List<BpmnSequenceFlow> getSequenceFlows() { return sequenceFlows; }
    public void setSequenceFlows(List<BpmnSequenceFlow> sequenceFlows) { this.sequenceFlows = sequenceFlows; }
    
    public List<BpmnProcess> getProcesses() { return processes; }
    public void setProcesses(List<BpmnProcess> processes) { this.processes = processes; }
    
    public List<DataTemplate> getDataTemplates() { return dataTemplates; }
    public void setDataTemplates(List<DataTemplate> dataTemplates) { this.dataTemplates = dataTemplates; }
    
    public List<GenerationContext> getGenerationContexts() { return generationContexts; }
    public void setGenerationContexts(List<GenerationContext> generationContexts) { this.generationContexts = generationContexts; }
    
    public Map<String, ProcessContext> getProcessContexts() { return processContexts; }
    public void setProcessContexts(Map<String, ProcessContext> processContexts) { this.processContexts = processContexts; }
    
    public ExtractionMetrics getMetrics() { return metrics; }
    public void setMetrics(ExtractionMetrics metrics) { this.metrics = metrics; }
    
    // Вложенные классы для структуры данных
    
    public static class ProcessVariable {
        private String name;
        private String type;
        private String scope; // PROCESS, TASK, GLOBAL
        private String description;
        private boolean isRequired;
        private String defaultValue;
        private List<String> validationRules;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isRequired() { return isRequired; }
        public void setRequired(boolean required) { this.isRequired = required; }
        
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
        
        public List<String> getValidationRules() { return validationRules; }
        public void setValidationRules(List<String> validationRules) { this.validationRules = validationRules; }
    }
    
    public static class TaskInputOutput {
        private String taskId;
        private String name;
        private String type; // INPUT, OUTPUT
        private String dataType;
        private String description;
        private String source; // PROCESS_VARIABLE, USER_INPUT, SYSTEM, etc.
        private List<String> validationRules;
        
        // Getters and Setters
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public List<String> getValidationRules() { return validationRules; }
        public void setValidationRules(List<String> validationRules) { this.validationRules = validationRules; }
    }
    
    public static class GatewayCondition {
        private String gatewayId;
        private String name;
        private String conditionType; // EXCLUSIVE, PARALLEL, INCLUSIVE
        private String condition;
        private String description;
        private List<String> outgoingFlows;
        
        // Getters and Setters
        public String getGatewayId() { return gatewayId; }
        public void setGatewayId(String gatewayId) { this.gatewayId = gatewayId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getConditionType() { return conditionType; }
        public void setConditionType(String conditionType) { this.conditionType = conditionType; }
        
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getOutgoingFlows() { return outgoingFlows; }
        public void setOutgoingFlows(List<String> outgoingFlows) { this.outgoingFlows = outgoingFlows; }
    }
    
    public static class EventData {
        private String eventId;
        private String name;
        private String eventType; // START, END, INTERMEDIATE
        private String triggerType; // MESSAGE, TIMER, SIGNAL, etc.
        private Map<String, Object> data;
        private String description;
        
        // Getters and Setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        
        public String getTriggerType() { return triggerType; }
        public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class BusinessRule {
        private String name;
        private String type;
        private String description;
        private List<String> conditions;
        private List<String> actions;
        private String scope; // PROCESS, TASK, GLOBAL
        private List<String> applicableTasks;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getConditions() { return conditions; }
        public void setConditions(List<String> conditions) { this.conditions = conditions; }
        
        public List<String> getActions() { return actions; }
        public void setActions(List<String> actions) { this.actions = actions; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public List<String> getApplicableTasks() { return applicableTasks; }
        public void setApplicableTasks(List<String> applicableTasks) { this.applicableTasks = applicableTasks; }
    }
    
    public static class DecisionPoint {
        private String id;
        private String name;
        private String description;
        private String decisionType;
        private List<String> options;
        private String criteria;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getDecisionType() { return decisionType; }
        public void setDecisionType(String decisionType) { this.decisionType = decisionType; }
        
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
        
        public String getCriteria() { return criteria; }
        public void setCriteria(String criteria) { this.criteria = criteria; }
    }
    
    public static class UserInteractionPattern {
        private String patternId;
        private String name;
        private String type; // FORM, APPROVAL, NOTIFICATION, etc.
        private List<String> userActions;
        private String description;
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getPatternId() { return patternId; }
        public void setPatternId(String patternId) { this.patternId = patternId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public List<String> getUserActions() { return userActions; }
        public void setUserActions(List<String> userActions) { this.userActions = userActions; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    public static class SystemIntegrationPoint {
        private String pointId;
        private String name;
        private String integrationType; // API, DATABASE, FILE, etc.
        private String direction; // IN, OUT, BIDIRECTIONAL
        private Map<String, Object> configuration;
        private String description;
        
        // Getters and Setters
        public String getPointId() { return pointId; }
        public void setPointId(String pointId) { this.pointId = pointId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getIntegrationType() { return integrationType; }
        public void setIntegrationType(String integrationType) { this.integrationType = integrationType; }
        
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
        
        public Map<String, Object> getConfiguration() { return configuration; }
        public void setConfiguration(Map<String, Object> configuration) { this.configuration = configuration; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    // BPMN элементы
    public static class BpmnTask {
        private String id;
        private String name;
        private String type; // USER_TASK, SERVICE_TASK, SCRIPT_TASK, etc.
        private String description;
        private List<String> inputs;
        private List<String> outputs;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getInputs() { return inputs; }
        public void setInputs(List<String> inputs) { this.inputs = inputs; }
        
        public List<String> getOutputs() { return outputs; }
        public void setOutputs(List<String> outputs) { this.outputs = outputs; }
    }
    
    public static class BpmnGateway {
        private String id;
        private String name;
        private String type; // EXCLUSIVE, PARALLEL, INCLUSIVE, EVENT
        private String description;
        private List<String> incomingFlows;
        private List<String> outgoingFlows;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getIncomingFlows() { return incomingFlows; }
        public void setIncomingFlows(List<String> incomingFlows) { this.incomingFlows = incomingFlows; }
        
        public List<String> getOutgoingFlows() { return outgoingFlows; }
        public void setOutgoingFlows(List<String> outgoingFlows) { this.outgoingFlows = outgoingFlows; }
    }
    
    public static class BpmnEvent {
        private String id;
        private String name;
        private String type; // START, END, INTERMEDIATE
        private String subType; // MESSAGE, TIMER, SIGNAL, etc.
        private String description;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getSubType() { return subType; }
        public void setSubType(String subType) { this.subType = subType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class BpmnSequenceFlow {
        private String id;
        private String name;
        private String sourceRef;
        private String targetRef;
        private String condition;
        private String description;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getSourceRef() { return sourceRef; }
        public void setSourceRef(String sourceRef) { this.sourceRef = sourceRef; }
        
        public String getTargetRef() { return targetRef; }
        public void setTargetRef(String targetRef) { this.targetRef = targetRef; }
        
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class BpmnProcess {
        private String id;
        private String name;
        private String description;
        private List<String> startEvents;
        private List<String> endEvents;
        private List<String> tasks;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getStartEvents() { return startEvents; }
        public void setStartEvents(List<String> startEvents) { this.startEvents = startEvents; }
        
        public List<String> getEndEvents() { return endEvents; }
        public void setEndEvents(List<String> endEvents) { this.endEvents = endEvents; }
        
        public List<String> getTasks() { return tasks; }
        public void setTasks(List<String> tasks) { this.tasks = tasks; }
    }
    
    public static class DataTemplate {
        private String name;
        private String type;
        private Map<String, Object> template;
        private String description;
        private String applicableTo; // TASK, PROCESS, GLOBAL
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Map<String, Object> getTemplate() { return template; }
        public void setTemplate(Map<String, Object> template) { this.template = template; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getApplicableTo() { return applicableTo; }
        public void setApplicableTo(String applicableTo) { this.applicableTo = applicableTo; }
    }
    
    public static class GenerationContext {
        private String name;
        private String type;
        private Map<String, Object> context;
        private String description;
        private List<String> dependencies;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    }
    
    public static class ProcessContext {
        private String processId;
        private String name;
        private Map<String, Object> variables;
        private List<String> tasks;
        private String description;
        
        // Getters and Setters
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Map<String, Object> getVariables() { return variables; }
        public void setVariables(Map<String, Object> variables) { this.variables = variables; }
        
        public List<String> getTasks() { return tasks; }
        public void setTasks(List<String> tasks) { this.tasks = tasks; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class ExtractionMetrics {
        private int totalProcesses;
        private int totalTasks;
        private int totalGateways;
        private int totalEvents;
        private int totalVariables;
        private int totalBusinessRules;
        private int complexityScore;
        
        // Getters and Setters
        public int getTotalProcesses() { return totalProcesses; }
        public void setTotalProcesses(int totalProcesses) { this.totalProcesses = totalProcesses; }
        
        public int getTotalTasks() { return totalTasks; }
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
        
        public int getTotalGateways() { return totalGateways; }
        public void setTotalGateways(int totalGateways) { this.totalGateways = totalGateways; }
        
        public int getTotalEvents() { return totalEvents; }
        public void setTotalEvents(int totalEvents) { this.totalEvents = totalEvents; }
        
        public int getTotalVariables() { return totalVariables; }
        public void setTotalVariables(int totalVariables) { this.totalVariables = totalVariables; }
        
        public int getTotalBusinessRules() { return totalBusinessRules; }
        public void setTotalBusinessRules(int totalBusinessRules) { this.totalBusinessRules = totalBusinessRules; }
        
        public int getComplexityScore() { return complexityScore; }
        public void setComplexityScore(int complexityScore) { this.complexityScore = complexityScore; }
    }
}