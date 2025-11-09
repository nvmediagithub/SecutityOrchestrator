package org.example.infrastructure.services.bpmn;

import org.example.domain.entities.bpmn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Service for parsing BPMN 2.0 files and converting them to domain entities
 */
@Service
public class BpmnParsingService {

    private static final Logger logger = LoggerFactory.getLogger(BpmnParsingService.class);
    
    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String DI_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/DI";
    private static final String DC_NAMESPACE = "http://www.omg.org/spec/DD/20100524/DC";
    
    private final BusinessProcessRepository processRepository;
    private final BpmnTaskRepository taskRepository;
    private final BpmnSequenceRepository sequenceRepository;
    private final BpmnGatewayRepository gatewayRepository;
    private final BpmnEventRepository eventRepository;
    
    public BpmnParsingService(BusinessProcessRepository processRepository,
                             BpmnTaskRepository taskRepository,
                             BpmnSequenceRepository sequenceRepository,
                             BpmnGatewayRepository gatewayRepository,
                             BpmnEventRepository eventRepository) {
        this.processRepository = processRepository;
        this.taskRepository = taskRepository;
        this.sequenceRepository = sequenceRepository;
        this.gatewayRepository = gatewayRepository;
        this.eventRepository = eventRepository;
    }
    
    /**
     * Parse all BPMN files from the specified directory
     */
    public CompletableFuture<List<BusinessProcess>> parseDirectory(String directoryPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<BusinessProcess> processes = new ArrayList<>();
                Path directory = Paths.get(directoryPath);
                
                if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                    logger.warn("Directory {} does not exist or is not a directory", directoryPath);
                    return processes;
                }
                
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.bpmn")) {
                    for (Path file : stream) {
                        try {
                            BusinessProcess process = parseFile(file);
                            if (process != null) {
                                processes.add(process);
                                logger.info("Successfully parsed BPMN file: {}", file.getFileName());
                            }
                        } catch (Exception e) {
                            logger.error("Failed to parse BPMN file: {}", file, e);
                        }
                    }
                }
                
                return processes;
            } catch (Exception e) {
                logger.error("Error parsing BPMN directory: {}", directoryPath, e);
                throw new RuntimeException("Failed to parse BPMN directory", e);
            }
        });
    }
    
    /**
     * Parse a single BPMN file
     */
    public BusinessProcess parseFile(Path filePath) throws Exception {
        String fileName = filePath.getFileName().toString();
        logger.info("Parsing BPMN file: {}", fileName);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filePath.toFile());
        
        return parseDocument(document, filePath.toString(), fileName);
    }
    
    /**
     * Parse BPMN content from string
     */
    public BusinessProcess parseContent(String bpmnContent, String sourceName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        StringReader reader = new StringReader(bpmnContent);
        Document document = builder.parse(new InputSource(reader));
        
        return parseDocument(document, "inline", sourceName);
    }
    
    private BusinessProcess parseDocument(Document document, String filePath, String fileName) throws Exception {
        Element definitions = document.getDocumentElement();
        
        // Get BPMN namespace URI
        String bpmnNamespace = getNamespaceUri(definitions, "bpmn", BPMN_NAMESPACE);
        
        NodeList processNodes = definitions.getElementsByTagNameNS(bpmnNamespace, "process");
        if (processNodes.getLength() == 0) {
            throw new IllegalArgumentException("No BPMN process found in file: " + fileName);
        }
        
        BusinessProcess process = null;
        for (int i = 0; i < processNodes.getLength(); i++) {
            Element processElement = (Element) processNodes.item(i);
            process = parseProcessElement(processElement, filePath, fileName, bpmnNamespace);
            
            // Parse diagram information if available
            parseDiagramInformation(definitions, process, bpmnNamespace);
        }
        
        return process;
    }
    
    private BusinessProcess parseProcessElement(Element processElement, String filePath, String fileName, String bpmnNamespace) {
        String processId = processElement.getAttribute("id");
        String name = processElement.getAttribute("name");
        boolean isExecutable = "true".equals(processElement.getAttribute("isExecutable"));
        
        BusinessProcess process = new BusinessProcess();
        process.setProcessId(processId);
        process.setName(name != null && !name.isEmpty() ? name : "Process " + processId);
        process.setFilePath(filePath);
        process.setFileName(fileName);
        process.setExecutable(isExecutable);
        process.setParseStatus(BusinessProcess.ParseStatus.PARSING);
        process.setLastParsedAt(LocalDateTime.now());
        
        // Set target namespace
        Document document = processElement.getOwnerDocument();
        Element definitions = document.getDocumentElement();
        String targetNamespace = definitions.getAttribute("targetNamespace");
        if (targetNamespace != null && !targetNamespace.isEmpty()) {
            process.setTargetNamespace(targetNamespace);
        }
        
        // Parse all process elements
        parseTasks(processElement, process, bpmnNamespace);
        parseGateways(processElement, process, bpmnNamespace);
        parseEvents(processElement, process, bpmnNamespace);
        parseSequenceFlows(processElement, process, bpmnNamespace);
        
        // Validate and save
        process.setParseStatus(BusinessProcess.ParseStatus.SUCCESS);
        return processRepository.save(process);
    }
    
    private void parseTasks(Element processElement, BusinessProcess process, String bpmnNamespace) {
        NodeList taskNodes = processElement.getElementsByTagNameNS(bpmnNamespace, "*");
        
        for (int i = 0; i < taskNodes.getLength(); i++) {
            Element taskElement = (Element) taskNodes.item(i);
            String tagName = taskElement.getLocalName();
            
            if (isTaskElement(tagName)) {
                BpmnTask task = createTaskFromElement(taskElement, process, tagName);
                taskRepository.save(task);
            }
        }
    }
    
    private void parseGateways(Element processElement, BusinessProcess process, String bpmnNamespace) {
        NodeList gatewayNodes = processElement.getElementsByTagNameNS(bpmnNamespace, "gateway");
        
        for (int i = 0; i < gatewayNodes.getLength(); i++) {
            Element gatewayElement = (Element) gatewayNodes.item(i);
            BpmnGateway gateway = createGatewayFromElement(gatewayElement, process);
            gatewayRepository.save(gateway);
        }
    }
    
    private void parseEvents(Element processElement, BusinessProcess process, String bpmnNamespace) {
        NodeList eventNodes = processElement.getElementsByTagNameNS(bpmnNamespace, "*");
        
        for (int i = 0; i < eventNodes.getLength(); i++) {
            Element eventElement = (Element) eventNodes.item(i);
            String tagName = eventElement.getLocalName();
            
            if (isEventElement(tagName)) {
                BpmnEvent event = createEventFromElement(eventElement, process, tagName);
                eventRepository.save(event);
            }
        }
    }
    
    private void parseSequenceFlows(Element processElement, BusinessProcess process, String bpmnNamespace) {
        NodeList sequenceFlowNodes = processElement.getElementsByTagNameNS(bpmnNamespace, "sequenceFlow");
        
        for (int i = 0; i < sequenceFlowNodes.getLength(); i++) {
            Element sequenceFlowElement = (Element) sequenceFlowNodes.item(i);
            BpmnSequence sequence = createSequenceFromElement(sequenceFlowElement, process);
            if (sequence != null) {
                sequenceRepository.save(sequence);
            }
        }
    }
    
    private BpmnTask createTaskFromElement(Element taskElement, BusinessProcess process, String tagName) {
        BpmnTask task = new BpmnTask();
        task.setTaskId(taskElement.getAttribute("id"));
        task.setName(taskElement.getAttribute("name"));
        task.setProcess(process);
        
        // Determine task type
        task.setTaskType(determineTaskType(tagName));
        
        // Set description if available
        String description = taskElement.getAttribute("name");
        if (description != null && !description.isEmpty()) {
            task.setDescription(description);
        }
        
        // Parse task-specific attributes
        if (BpmnTask.TaskType.SERVICE_TASK.equals(task.getTaskType())) {
            String implementation = taskElement.getAttribute("implementation");
            if (implementation != null) {
                task.setImplementation(implementation);
            }
        } else if (BpmnTask.TaskType.USER_TASK.equals(task.getTaskType())) {
            String assignee = taskElement.getAttribute("camunda:assignee");
            if (assignee == null) {
                assignee = taskElement.getAttribute("activiti:assignee");
            }
            if (assignee != null) {
                task.setAssignee(assignee);
            }
        }
        
        return task;
    }
    
    private BpmnGateway createGatewayFromElement(Element gatewayElement, BusinessProcess process) {
        BpmnGateway gateway = new BpmnGateway();
        gateway.setGatewayId(gatewayElement.getAttribute("id"));
        gateway.setName(gatewayElement.getAttribute("name"));
        gateway.setProcess(process);
        
        // Determine gateway type
        String gatewayDirection = gatewayElement.getAttribute("gatewayDirection");
        if ("converging".equals(gatewayDirection)) {
            gateway.setGatewayType(BpmnGateway.GatewayType.PARALLEL_GATEWAY);
            gateway.setMerge(true);
        } else {
            gateway.setGatewayType(BpmnGateway.GatewayType.EXCLUSIVE_GATEWAY);
        }
        
        return gateway;
    }
    
    private BpmnEvent createEventFromElement(Element eventElement, BusinessProcess process, String tagName) {
        BpmnEvent event = new BpmnEvent();
        event.setEventId(eventElement.getAttribute("id"));
        event.setName(eventElement.getAttribute("name"));
        event.setProcess(process);
        
        // Determine event type
        event.setEventType(determineEventType(tagName));
        
        return event;
    }
    
    private BpmnSequence createSequenceFromElement(Element sequenceFlowElement, BusinessProcess process) {
        String sourceRef = sequenceFlowElement.getAttribute("sourceRef");
        String targetRef = sequenceFlowElement.getAttribute("targetRef");
        
        if (sourceRef == null || targetRef == null) {
            logger.warn("Sequence flow missing sourceRef or targetRef: {}", sequenceFlowElement.getAttribute("id"));
            return null;
        }
        
        BpmnSequence sequence = new BpmnSequence();
        sequence.setSequenceId(sequenceFlowElement.getAttribute("id"));
        sequence.setSourceRef(sourceRef);
        sequence.setTargetRef(targetRef);
        sequence.setName(sequenceFlowElement.getAttribute("name"));
        sequence.setProcess(process);
        
        // Set condition if available
        Element conditionElement = getFirstChildElement(sequenceFlowElement, "conditionExpression");
        if (conditionElement != null) {
            String condition = conditionElement.getTextContent();
            if (condition != null) {
                sequence.setConditionExpression(condition);
            }
        }
        
        return sequence;
    }
    
    private void parseDiagramInformation(Element definitions, BusinessProcess process, String bpmnNamespace) {
        // Parse diagram information for positioning
        NodeList diagramNodes = definitions.getElementsByTagNameNS(DI_NAMESPACE, "BPMNDiagram");
        for (int i = 0; i < diagramNodes.getLength(); i++) {
            Element diagramElement = (Element) diagramNodes.item(i);
            parseDiagramElement(diagramElement, process, bpmnNamespace);
        }
    }
    
    private void parseDiagramElement(Element diagramElement, BusinessProcess process, String bpmnNamespace) {
        // This is a simplified implementation
        // In a full implementation, you would parse the DI information to get positioning
        logger.debug("Parsed diagram information for process: {}", process.getProcessId());
    }
    
    private BpmnTask.TaskType determineTaskType(String tagName) {
        switch (tagName.toLowerCase()) {
            case "usertask":
                return BpmnTask.TaskType.USER_TASK;
            case "servicetask":
                return BpmnTask.TaskType.SERVICE_TASK;
            case "scripttask":
                return BpmnTask.TaskType.SCRIPT_TASK;
            case "businessruletask":
                return BpmnTask.TaskType.BUSINESS_RULE_TASK;
            case "sendtask":
                return BpmnTask.TaskType.SEND_TASK;
            case "receivetask":
                return BpmnTask.TaskType.RECEIVE_TASK;
            case "subprocess":
                return BpmnTask.TaskType.SUB_PROCESS;
            case "callactivity":
                return BpmnTask.TaskType.CALL_ACTIVITY;
            default:
                return BpmnTask.TaskType.USER_TASK;
        }
    }
    
    private BpmnEvent.EventType determineEventType(String tagName) {
        switch (tagName.toLowerCase()) {
            case "startevent":
                return BpmnEvent.EventType.START_EVENT;
            case "endevent":
                return BpmnEvent.EventType.END_EVENT;
            case "intermediatethrowevent":
                return BpmnEvent.EventType.INTERMEDIATE_THROW_EVENT;
            case "intermediatecatchevent":
                return BpmnEvent.EventType.INTERMEDIATE_CATCH_EVENT;
            case "boundaryevent":
                return BpmnEvent.EventType.BOUNDARY_EVENT;
            default:
                return BpmnEvent.EventType.START_EVENT;
        }
    }
    
    private boolean isTaskElement(String tagName) {
        String[] taskTags = {"usertask", "servicetask", "scripttask", "businessruletask", 
                           "sendtask", "receivetask", "subprocess", "callactivity"};
        return Arrays.asList(taskTags).contains(tagName.toLowerCase());
    }
    
    private boolean isEventElement(String tagName) {
        String[] eventTags = {"startevent", "endevent", "intermediatethrowevent", 
                            "intermediatecatchevent", "boundaryevent"};
        return Arrays.asList(eventTags).contains(tagName.toLowerCase());
    }
    
    private String getNamespaceUri(Element element, String prefix, String defaultUri) {
        String uri = element.getNamespaceURI(prefix);
        return uri != null ? uri : defaultUri;
    }
    
    private Element getFirstChildElement(Element parent, String childName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (childName.equals(childElement.getLocalName())) {
                    return childElement;
                }
            }
        }
        return null;
    }
}