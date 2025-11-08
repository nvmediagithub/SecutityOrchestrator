package org.example.application.service.dependency;

import org.example.domain.dto.dependency.BpmnDependency;
import org.example.domain.entities.BpmnDiagram;
import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.infrastructure.services.ArtifactService;
import org.example.domain.valueobjects.BpmnProcessId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * Анализатор зависимостей BPMN процессов с интеграцией BpmnAnalysisService
 * Анализирует связи между задачами, потоки данных, контекст процессов, состояния
 */
@Service
public class BpmnDependencyAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(BpmnDependencyAnalyzer.class);
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    @Autowired
    private ArtifactService artifactService;
    
    // Константы для анализа
    private static final long ANALYSIS_TIMEOUT_MINUTES = 20;
    private static final Pattern TASK_PATTERN = Pattern.compile(".*[Tt]ask.*|.*[Aa]ctivity.*|.*[Ss]ervice.*");
    private static final Pattern GATEWAY_PATTERN = Pattern.compile(".*[Gg]ateway.*|.*[Dd]ecision.*|.*[Cc]ondition.*");
    private static final Pattern EVENT_PATTERN = Pattern.compile(".*[Ee]vent.*|.*[Ss]tart.*|.*[Ee]nd.*|.*[Tt]hrow.*|.*[Cc]atch.*");
    
    /**
     * Анализирует зависимости BPMN для диаграммы
     */
    public CompletableFuture<List<BpmnDependency>> analyzeBpmnDependencies(String diagramId, BpmnDiagram diagram) {
        logger.info("Starting BPMN dependency analysis for diagram: {}", diagramId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<BpmnDependency> dependencies = new ArrayList<>();
                
                // 1. Анализ связей между задачами
                List<BpmnDependency> taskDependencies = analyzeTaskDependencies(diagram);
                dependencies.addAll(taskDependencies);
                
                // 2. Анализ потоков данных между задачами
                List<BpmnDependency> dataFlowDependencies = analyzeDataFlowDependencies(diagram);
                dependencies.addAll(dataFlowDependencies);
                
                // 3. Анализ gateway условий и контекста
                List<BpmnDependency> gatewayDependencies = analyzeGatewayDependencies(diagram);
                dependencies.addAll(gatewayDependencies);
                
                // 4. Анализ событий и их влияния на данные
                List<BpmnDependency> eventDependencies = analyzeEventDependencies(diagram);
                dependencies.addAll(eventDependencies);
                
                // 5. Анализ состояний процесса
                List<BpmnDependency> stateDependencies = analyzeStateDependencies(diagram);
                dependencies.addAll(stateDependencies);
                
                // 6. Анализ пользовательских journey
                List<BpmnDependency> userJourneyDependencies = analyzeUserJourneyDependencies(diagram);
                dependencies.addAll(userJourneyDependencies);
                
                // 7. Анализ с помощью BpmnAnalysisService
                List<BpmnDependency> analysisServiceDependencies = analyzeWithBpmnService(diagramId, diagram);
                dependencies.addAll(analysisServiceDependencies);
                
                // Удаляем дубликаты
                List<BpmnDependency> uniqueDependencies = dependencies.stream()
                    .distinct()
                    .collect(Collectors.toList());
                
                logger.info("BPMN dependency analysis completed for diagram: {}, found {} dependencies", 
                    diagramId, uniqueDependencies.size());
                
                return uniqueDependencies;
                
            } catch (Exception e) {
                logger.error("BPMN dependency analysis failed for diagram: {}", diagramId, e);
                throw new RuntimeException("BPMN dependency analysis failed", e);
            }
        });
    }
    
    /**
     * Анализирует зависимости между задачами процесса
     */
    private List<BpmnDependency> analyzeTaskDependencies(BpmnDiagram diagram) {
        List<BpmnDependency> dependencies = new ArrayList<>();
        
        try {
            // Анализируем BPMN содержание на основе простого парсинга XML
            if (diagram.getBpmnContent() == null) return dependencies;
            
            String bpmnContent = diagram.getBpmnContent();
            
            // Извлекаем задачи из BPMN XML
            List<BpmnTask> tasks = extractTasksFromBpmn(bpmnContent);
            
            // Анализируем связи между задачами
            for (int i = 0; i < tasks.size(); i++) {
                for (int j = i + 1; j < tasks.size(); j++) {
                    BpmnTask sourceTask = tasks.get(i);
                    BpmnTask targetTask = tasks.get(j);
                    
                    // Проверяем связь между задачами через sequence flows
                    if (hasSequenceFlow(bpmnContent, sourceTask.getId(), targetTask.getId())) {
                        BpmnDependency dependency = new BpmnDependency(
                            sourceTask.getId(), targetTask.getId(), 
                            diagram.getDiagramId(), BpmnDependency.DependencyType.TASK_TO_TASK
                        );
                        
                        dependency.setSourceTaskName(sourceTask.getName());
                        dependency.setTargetTaskName(targetTask.getName());
                        
                        // Анализируем созданные и потребляемые данные
                        dependency.setCreatedData(extractTaskOutputData(sourceTask));
                        dependency.setConsumedData(extractTaskInputData(targetTask));
                        
                        dependencies.add(dependency);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing task dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует потоки данных между задачами
     */
    private List<BpmnDependency> analyzeDataFlowDependencies(BpmnDiagram diagram) {
        List<BpmnDependency> dependencies = new ArrayList<>();
        
        try {
            if (diagram.getBpmnContent() == null) return dependencies;
            
            String bpmnContent = diagram.getBpmnContent();
            
            // Анализируем DataObject элементы
            List<BpmnDataObject> dataObjects = extractDataObjectsFromBpmn(bpmnContent);
            
            for (BpmnDataObject dataObject : dataObjects) {
                // Находим задачи, которые используют этот data object
                List<String> producingTasks = findTasksProducingDataObject(bpmnContent, dataObject.getId());
                List<String> consumingTasks = findTasksConsumingDataObject(bpmnContent, dataObject.getId());
                
                for (String producerTask : producingTasks) {
                    for (String consumerTask : consumingTasks) {
                        if (!producerTask.equals(consumerTask)) {
                            BpmnDependency dependency = new BpmnDependency(
                                producerTask, consumerTask, 
                                diagram.getDiagramId(), BpmnDependency.DependencyType.DATA_OBJECT
                            );
                            
                            dependency.setDataObjectReference(dataObject.getName());
                            dependency.setContextData(Collections.singleton(dataObject.getDataState()));
                            
                            dependencies.add(dependency);
                        }
                    }
                }
            }
            
            // Анализируем MessageFlow
            List<BpmnMessageFlow> messageFlows = extractMessageFlowsFromBpmn(bpmnContent);
            
            for (BpmnMessageFlow messageFlow : messageFlows) {
                BpmnDependency dependency = new BpmnDependency(
                    messageFlow.getSource(), messageFlow.getTarget(),
                    diagram.getDiagramId(), BpmnDependency.DependencyType.MESSAGE_DATA
                );
                
                dependency.setMessageFlow(messageFlow.getMessageRef());
                dependency.setContextData(Collections.singleton(messageFlow.getName()));
                
                dependencies.add(dependency);
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing data flow dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует зависимости gateway и условий
     */
    private List<BpmnDependency> analyzeGatewayDependencies(BpmnDiagram diagram) {
        List<BpmnDependency> dependencies = new ArrayList<>();
        
        try {
            if (diagram.getBpmnContent() == null) return dependencies;
            
            String bpmnContent = diagram.getBpmnContent();
            
            // Анализируем Gateway элементы
            List<BpmnGateway> gateways = extractGatewaysFromBpmn(bpmnContent);
            
            for (BpmnGateway gateway : gateways) {
                // Находим входящие и исходящие sequence flows
                List<String> incomingFlows = findIncomingSequenceFlows(bpmnContent, gateway.getId());
                List<String> outgoingFlows = findOutgoingSequenceFlows(bpmnContent, gateway.getId());
                
                for (String incomingFlow : incomingFlows) {
                    for (String outgoingFlow : outgoingFlows) {
                        BpmnDependency dependency = new BpmnDependency(
                            incomingFlow, outgoingFlow,
                            diagram.getDiagramId(), BpmnDependency.DependencyType.GATEWAY_DATA
                        );
                        
                        dependency.setGatewayCondition(extractGatewayCondition(bpmnContent, gateway.getId()));
                        dependency.setProcessVariable("process_variable_" + gateway.getId());
                        
                        dependencies.add(dependency);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing gateway dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует события и их влияние на данные
     */
    private List<BpmnDependency> analyzeEventDependencies(BpmnDiagram diagram) {
        List<BpmnDependency> dependencies = new ArrayList<>();
        
        try {
            if (diagram.getBpmnContent() == null) return dependencies;
            
            String bpmnContent = diagram.getBpmnContent();
            
            // Анализируем Start/End Events
            List<BpmnEvent> events = extractEventsFromBpmn(bpmnContent);
            
            for (BpmnEvent event : events) {
                String eventType = event.getEventType(); // Start, End, IntermediateThrow, IntermediateCatch
                
                if ("Start".equals(eventType) || "IntermediateCatch".equals(eventType)) {
                    // События, которые инициируют данные
                    List<String> subsequentTasks = findSubsequentTasks(bpmnContent, event.getId());
                    
                    for (String task : subsequentTasks) {
                        BpmnDependency dependency = new BpmnDependency(
                            event.getId(), task,
                            diagram.getDiagramId(), BpmnDependency.DependencyType.EVENT_DATA
                        );
                        
                        dependency.setEventTrigger(event.getName());
                        dependency.setCreatedData(extractEventOutputData(event));
                        
                        dependencies.add(dependency);
                    }
                } else if ("End".equals(eventType) || "IntermediateThrow".equals(eventType)) {
                    // События, которые завершают обработку данных
                    List<String> precedingTasks = findPrecedingTasks(bpmnContent, event.getId());
                    
                    for (String task : precedingTasks) {
                        BpmnDependency dependency = new BpmnDependency(
                            task, event.getId(),
                            diagram.getDiagramId(), BpmnDependency.DependencyType.EVENT_DATA
                        );
                        
                        dependency.setEventResult(event.getName());
                        dependency.setConsumedData(extractEventInputData(event));
                        
                        dependencies.add(dependency);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing event dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует состояния процесса и их влияние на данные
     */
    private List<BpmnDependency> analyzeStateDependencies(BpmnDiagram diagram) {
        List<BpmnDependency> dependencies = new ArrayList<>();
        
        try {
            if (diagram.getBpmnContent() == null) return dependencies;
            
            String bpmnContent = diagram.getBpmnContent();
            
            // Анализируем изменения состояний через gateways
            List<BpmnGateway> gateways = extractGatewaysFromBpmn(bpmnContent);
            
            for (BpmnGateway gateway : gateways) {
                String gatewayType = gateway.getGatewayType(); // Exclusive, Parallel, Inclusive
                
                if ("Exclusive".equals(gatewayType)) {
                    // Exclusive Gateway - определяет состояние процесса
                    String stateCondition = extractStateCondition(bpmnContent, gateway.getId());
                    
                    List<String> outgoingFlows = findOutgoingSequenceFlows(bpmnContent, gateway.getId());
                    
                    for (String flow : outgoingFlows) {
                        BpmnDependency dependency = new BpmnDependency(
                            gateway.getId(), flow,
                            diagram.getDiagramId(), BpmnDependency.DependencyType.STATE_TRANSITION
                        );
                        
                        dependency.setProcessState(stateCondition);
                        dependency.setDataStateTransition("Conditional data generation based on: " + stateCondition);
                        
                        dependencies.add(dependency);
                    }
                }
            }
            
            // Анализируем Business Rule Tasks
            List<BpmnTask> tasks = extractTasksFromBpmn(bpmnContent);
            
            for (BpmnTask task : tasks) {
                if ("BusinessRule".equals(task.getTaskType())) {
                    String businessRule = extractBusinessRule(bpmnContent, task.getId());
                    
                    BpmnDependency dependency = new BpmnDependency(
                        task.getId(), task.getId(),
                        diagram.getDiagramId(), BpmnDependency.DependencyType.TASK_TO_TASK
                    );
                    
                    dependency.setBusinessRule(businessRule);
                    dependency.setContextData(Collections.singleton("Business rule application in state: " + task.getState()));
                    
                    dependencies.add(dependency);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing state dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует пользовательские journey
     */
    private List<BpmnDependency> analyzeUserJourneyDependencies(BpmnDiagram diagram) {
        List<BpmnDependency> dependencies = new ArrayList<>();
        
        try {
            if (diagram.getBpmnContent() == null) return dependencies;
            
            String bpmnContent = diagram.getBpmnContent();
            
            // Анализируем User Tasks
            List<BpmnTask> tasks = extractTasksFromBpmn(bpmnContent);
            
            for (BpmnTask task : tasks) {
                if ("User".equals(task.getTaskType())) {
                    // Находим следующие задачи в пользовательском journey
                    List<String> subsequentUserTasks = findSubsequentUserTasks(bpmnContent, task.getId());
                    
                    for (String nextUserTask : subsequentUserTasks) {
                        BpmnDependency dependency = new BpmnDependency(
                            task.getId(), nextUserTask,
                            diagram.getDiagramId(), BpmnDependency.DependencyType.USER_JOURNEY
                        );
                        
                        dependency.setUserJourneyStep(task.getName());
                        dependency.setContextData(Collections.singleton("User interaction step: " + task.getFormKey()));
                        dependency.setProcessVariable(extractUserDataRequirement(bpmnContent, task.getId()));
                    
                        dependencies.add(dependency);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error analyzing user journey dependencies", e);
        }
        
        return dependencies;
    }
    
    /**
     * Анализирует с помощью BpmnAnalysisService
     */
    private List<BpmnDependency> analyzeWithBpmnService(String diagramId, BpmnDiagram diagram) {
        try {
            // Используем существующий BpmnAnalysisService для получения данных
            BpmnProcessId processId = BpmnProcessId.fromString(diagramId);
            CompletableFuture<BpmnAnalysisService.BpmnAnalysisResult> analysisFuture =
                bpmnAnalysisService.analyzeBpmnDiagram(processId.getValue(), diagram);
            
            BpmnAnalysisService.BpmnAnalysisResult result = analysisFuture.get(ANALYSIS_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            
            List<BpmnDependency> dependencies = new ArrayList<>();
            
            // Извлекаем зависимости из результата анализа
            if (result.getComprehensiveAnalysis() != null && 
                result.getComprehensiveAnalysis().getComprehensiveData() != null) {
                
                Map<String, Object> comprehensiveData = result.getComprehensiveAnalysis().getComprehensiveData();
                
                // Ищем информацию о зависимостях в comprehensive data
                Object dependenciesObj = comprehensiveData.get("bpmnDependencies");
                if (dependenciesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> dependenciesList = (List<Map<String, Object>>) dependenciesObj;
                    
                    for (Map<String, Object> depData : dependenciesList) {
                        BpmnDependency dependency = convertMapToBpmnDependency(depData);
                        if (dependency != null) {
                            dependencies.add(dependency);
                        }
                    }
                }
            }
            
            return dependencies;
            
        } catch (Exception e) {
            logger.error("Error analyzing with BpmnAnalysisService", e);
            return Collections.emptyList();
        }
    }
    
    // Вспомогательные классы для парсинга BPMN
    
    private static class BpmnTask {
        private String id;
        private String name;
        private String taskType;
        private String state;
        private String formKey;
        
        public BpmnTask() {}
        
        public BpmnTask(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getTaskType() { return taskType; }
        public void setTaskType(String taskType) { this.taskType = taskType; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getFormKey() { return formKey; }
        public void setFormKey(String formKey) { this.formKey = formKey; }
    }
    
    private static class BpmnDataObject {
        private String id;
        private String name;
        private String dataState;
        
        public BpmnDataObject() {}
        
        public BpmnDataObject(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDataState() { return dataState; }
        public void setDataState(String dataState) { this.dataState = dataState; }
    }
    
    private static class BpmnGateway {
        private String id;
        private String gatewayType;
        
        public BpmnGateway() {}
        
        public BpmnGateway(String id, String gatewayType) {
            this.id = id;
            this.gatewayType = gatewayType;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getGatewayType() { return gatewayType; }
        public void setGatewayType(String gatewayType) { this.gatewayType = gatewayType; }
    }
    
    private static class BpmnEvent {
        private String id;
        private String name;
        private String eventType;
        
        public BpmnEvent() {}
        
        public BpmnEvent(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
    }
    
    private static class BpmnMessageFlow {
        private String id;
        private String source;
        private String target;
        private String name;
        private String messageRef;
        
        public BpmnMessageFlow() {}
        
        public BpmnMessageFlow(String id, String source, String target) {
            this.id = id;
            this.source = source;
            this.target = target;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getMessageRef() { return messageRef; }
        public void setMessageRef(String messageRef) { this.messageRef = messageRef; }
    }
    
    // Вспомогательные методы для извлечения данных из BPMN XML
    
    private List<BpmnTask> extractTasksFromBpmn(String bpmnContent) {
        List<BpmnTask> tasks = new ArrayList<>();
        
        // Простое извлечение задач из XML
        // В реальной реализации должен использоваться proper XML parser
        try {
            // Заглушка - извлекаем базовую информацию о задачах
            // В реальности нужно парсить BPMN XML
            String[] taskElements = bpmnContent.split("<task[^>]*>");
            for (int i = 1; i < taskElements.length; i++) {
                String taskElement = taskElements[i];
                String taskId = extractIdFromElement(taskElement);
                String taskName = extractNameFromElement(taskElement);
                
                if (taskId != null && taskName != null) {
                    BpmnTask task = new BpmnTask(taskId, taskName);
                    task.setTaskType("User"); // По умолчанию
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting tasks from BPMN", e);
        }
        
        return tasks;
    }
    
    private List<BpmnDataObject> extractDataObjectsFromBpmn(String bpmnContent) {
        List<BpmnDataObject> dataObjects = new ArrayList<>();
        
        try {
            String[] dataObjectElements = bpmnContent.split("<dataObject[^>]*>");
            for (int i = 1; i < dataObjectElements.length; i++) {
                String dataObjectElement = dataObjectElements[i];
                String dataObjectId = extractIdFromElement(dataObjectElement);
                String dataObjectName = extractNameFromElement(dataObjectElement);
                
                if (dataObjectId != null && dataObjectName != null) {
                    BpmnDataObject dataObject = new BpmnDataObject(dataObjectId, dataObjectName);
                    dataObject.setDataState("Active");
                    dataObjects.add(dataObject);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting data objects from BPMN", e);
        }
        
        return dataObjects;
    }
    
    private List<BpmnGateway> extractGatewaysFromBpmn(String bpmnContent) {
        List<BpmnGateway> gateways = new ArrayList<>();
        
        try {
            String[] gatewayElements = bpmnContent.split("<gateway[^>]*>");
            for (int i = 1; i < gatewayElements.length; i++) {
                String gatewayElement = gatewayElements[i];
                String gatewayId = extractIdFromElement(gatewayElement);
                String gatewayType = extractGatewayTypeFromElement(gatewayElement);
                
                if (gatewayId != null) {
                    BpmnGateway gateway = new BpmnGateway(gatewayId, gatewayType);
                    gateways.add(gateway);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting gateways from BPMN", e);
        }
        
        return gateways;
    }
    
    private List<BpmnEvent> extractEventsFromBpmn(String bpmnContent) {
        List<BpmnEvent> events = new ArrayList<>();
        
        try {
            String[] startEventElements = bpmnContent.split("<startEvent[^>]*>");
            for (int i = 1; i < startEventElements.length; i++) {
                String eventElement = startEventElements[i];
                String eventId = extractIdFromElement(eventElement);
                String eventName = extractNameFromElement(eventElement);
                
                if (eventId != null) {
                    BpmnEvent event = new BpmnEvent(eventId, eventName);
                    event.setEventType("Start");
                    events.add(event);
                }
            }
            
            String[] endEventElements = bpmnContent.split("<endEvent[^>]*>");
            for (int i = 1; i < endEventElements.length; i++) {
                String eventElement = endEventElements[i];
                String eventId = extractIdFromElement(eventElement);
                String eventName = extractNameFromElement(eventElement);
                
                if (eventId != null) {
                    BpmnEvent event = new BpmnEvent(eventId, eventName);
                    event.setEventType("End");
                    events.add(event);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting events from BPMN", e);
        }
        
        return events;
    }
    
    private List<BpmnMessageFlow> extractMessageFlowsFromBpmn(String bpmnContent) {
        List<BpmnMessageFlow> messageFlows = new ArrayList<>();
        
        try {
            String[] messageFlowElements = bpmnContent.split("<messageFlow[^>]*>");
            for (int i = 1; i < messageFlowElements.length; i++) {
                String messageFlowElement = messageFlowElements[i];
                String messageFlowId = extractIdFromElement(messageFlowElement);
                String sourceRef = extractAttribute(messageFlowElement, "sourceRef");
                String targetRef = extractAttribute(messageFlowElement, "targetRef");
                
                if (messageFlowId != null && sourceRef != null && targetRef != null) {
                    BpmnMessageFlow messageFlow = new BpmnMessageFlow(messageFlowId, sourceRef, targetRef);
                    messageFlows.add(messageFlow);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting message flows from BPMN", e);
        }
        
        return messageFlows;
    }
    
    // Дополнительные вспомогательные методы
    
    private boolean hasSequenceFlow(String bpmnContent, String sourceId, String targetId) {
        // Проверяем наличие sequence flow между элементами
        String pattern = "sourceRef=\"" + sourceId + "\".*targetRef=\"" + targetId + "\"";
        return bpmnContent.contains("sourceRef=\"" + sourceId + "\"") && 
               bpmnContent.contains("targetRef=\"" + targetId + "\"");
    }
    
    private Set<String> extractTaskOutputData(BpmnTask task) {
        Set<String> output = new HashSet<>();
        output.add("output_" + task.getId());
        return output;
    }
    
    private Set<String> extractTaskInputData(BpmnTask task) {
        Set<String> input = new HashSet<>();
        input.add("input_" + task.getId());
        return input;
    }
    
    private List<String> findTasksProducingDataObject(String bpmnContent, String dataObjectId) {
        // Находим задачи, которые производят data object
        List<String> producers = new ArrayList<>();
        // Заглушка - в реальности нужно анализировать data associations
        producers.add("task_" + dataObjectId + "_producer");
        return producers;
    }
    
    private List<String> findTasksConsumingDataObject(String bpmnContent, String dataObjectId) {
        // Находим задачи, которые потребляют data object
        List<String> consumers = new ArrayList<>();
        // Заглушка - в реальности нужно анализировать data associations
        consumers.add("task_" + dataObjectId + "_consumer");
        return consumers;
    }
    
    private List<String> findIncomingSequenceFlows(String bpmnContent, String elementId) {
        List<String> incoming = new ArrayList<>();
        // Заглушка - ищем sourceRef
        incoming.add("source_of_" + elementId);
        return incoming;
    }
    
    private List<String> findOutgoingSequenceFlows(String bpmnContent, String elementId) {
        List<String> outgoing = new ArrayList<>();
        // Заглушка - ищем targetRef
        outgoing.add("target_of_" + elementId);
        return outgoing;
    }
    
    private String extractGatewayCondition(String bpmnContent, String gatewayId) {
        return "condition_for_gateway_" + gatewayId;
    }
    
    private String extractStateCondition(String bpmnContent, String gatewayId) {
        return "state_condition_" + gatewayId;
    }
    
    private String extractBusinessRule(String bpmnContent, String taskId) {
        return "business_rule_" + taskId;
    }
    
    private String extractUserDataRequirement(String bpmnContent, String taskId) {
        return "user_data_requirement_" + taskId;
    }
    
    private Set<String> extractEventOutputData(BpmnEvent event) {
        Set<String> output = new HashSet<>();
        output.add("event_output_" + event.getId());
        return output;
    }
    
    private Set<String> extractEventInputData(BpmnEvent event) {
        Set<String> input = new HashSet<>();
        input.add("event_input_" + event.getId());
        return input;
    }
    
    private List<String> findSubsequentTasks(String bpmnContent, String elementId) {
        return findOutgoingSequenceFlows(bpmnContent, elementId);
    }
    
    private List<String> findPrecedingTasks(String bpmnContent, String elementId) {
        return findIncomingSequenceFlows(bpmnContent, elementId);
    }
    
    private List<String> findSubsequentUserTasks(String bpmnContent, String elementId) {
        // Находим последующие User Tasks
        return findSubsequentTasks(bpmnContent, elementId);
    }
    
    // Вспомогательные методы для парсинга XML
    
    private String extractIdFromElement(String element) {
        int start = element.indexOf("id=\"");
        if (start == -1) return null;
        start += 4;
        int end = element.indexOf("\"", start);
        return end == -1 ? null : element.substring(start, end);
    }
    
    private String extractNameFromElement(String element) {
        int start = element.indexOf("name=\"");
        if (start == -1) return null;
        start += 6;
        int end = element.indexOf("\"", start);
        return end == -1 ? null : element.substring(start, end);
    }
    
    private String extractAttribute(String element, String attributeName) {
        int start = element.indexOf(attributeName + "=\"");
        if (start == -1) return null;
        start += attributeName.length() + 2;
        int end = element.indexOf("\"", start);
        return end == -1 ? null : element.substring(start, end);
    }
    
    private String extractGatewayTypeFromElement(String element) {
        if (element.contains("exclusiveGateway")) return "Exclusive";
        if (element.contains("parallelGateway")) return "Parallel";
        if (element.contains("inclusiveGateway")) return "Inclusive";
        return "Unknown";
    }
    
    private BpmnDependency convertMapToBpmnDependency(Map<String, Object> depData) {
        try {
            BpmnDependency dependency = new BpmnDependency();
            dependency.setSourceTaskId((String) depData.get("sourceTaskId"));
            dependency.setTargetTaskId((String) depData.get("targetTaskId"));
            dependency.setProcessId((String) depData.get("processId"));
            
            String typeStr = (String) depData.get("type");
            if (typeStr != null) {
                dependency.setType(BpmnDependency.DependencyType.valueOf(typeStr));
            }
            
            return dependency;
        } catch (Exception e) {
            logger.error("Error converting map to BpmnDependency", e);
            return null;
        }
    }
}