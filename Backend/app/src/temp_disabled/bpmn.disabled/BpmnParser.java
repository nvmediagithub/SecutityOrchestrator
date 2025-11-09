package org.example.infrastructure.services.bpmn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Парсер BPMN 2.0 диаграмм
 * Извлекает структурированные данные из BPMN XML/JSON содержимого
 */
@Service
public class BpmnParser {
    
    private static final Logger logger = LoggerFactory.getLogger(BpmnParser.class);
    
    // Паттерны для извлечения BPMN информации
    private static final Pattern BPMN_ELEMENT_PATTERN = Pattern.compile(
        "<(?:bpmn:|)([A-Za-z]+)(?:Task|Event|Gateway|Flow)?[^>]*id=\"([^\"]+)\"[^>]*(?:name=\"([^\"]*)\")?[^>]*>",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern SEQUENCE_FLOW_PATTERN = Pattern.compile(
        "<(?:bpmn:|)sequenceFlow[^>]*id=\"([^\"]+)\"[^>]*sourceRef=\"([^\"]+)\"[^>]*targetRef=\"([^\"]+)\"[^>]*>",
        Pattern.CASE_INSENSITIVE
    );
    
    // Карта BPMN типов элементов
    private static final Map<String, String> BPMN_ELEMENT_TYPES = Map.of(
        "startEvent", "StartEvent",
        "endEvent", "EndEvent", 
        "userTask", "UserTask",
        "serviceTask", "ServiceTask",
        "scriptTask", "ScriptTask",
        "businessRuleTask", "BusinessRuleTask",
        "manualTask", "ManualTask",
        "sendTask", "SendTask",
        "receiveTask", "ReceiveTask",
        "task", "Task",
        "exclusiveGateway", "ExclusiveGateway",
        "parallelGateway", "ParallelGateway",
        "inclusiveGateway", "InclusiveGateway",
        "eventBasedGateway", "EventBasedGateway",
        "complexGateway", "ComplexGateway",
        "timerEvent", "TimerEvent",
        "messageEvent", "MessageEvent",
        "errorEvent", "ErrorEvent",
        "escalationEvent", "EscalationEvent",
        "signalEvent", "SignalEvent",
        "conditionalEvent", "ConditionalEvent",
        "subProcess", "SubProcess",
        "callActivity", "CallActivity",
        "lane", "Lane",
        "pool", "Pool"
    );
    
    /**
     * Парсит BPMN содержимое и извлекает структурированные данные
     */
    public BpmnParsedData parseBpmnContent(String bpmnContent) {
        if (bpmnContent == null || bpmnContent.trim().isEmpty()) {
            throw new IllegalArgumentException("BPMN content cannot be null or empty");
        }
        
        try {
            BpmnParsedData parsedData = new BpmnParsedData();
            
            // Извлекаем базовую информацию
            extractBasicInfo(bpmnContent, parsedData);
            
            // Парсим элементы
            parseElements(bpmnContent, parsedData);
            
            // Парсим потоки
            parseFlows(bpmnContent, parsedData);
            
            // Связываем элементы и потоки
            linkElementsAndFlows(parsedData);
            
            logger.info("Successfully parsed BPMN content: {} elements, {} flows", 
                parsedData.getElements().size(), parsedData.getFlows().size());
            
            return parsedData;
            
        } catch (Exception e) {
            logger.error("Error parsing BPMN content", e);
            throw new RuntimeException("Failed to parse BPMN content", e);
        }
    }
    
    /**
     * Извлекает базовую информацию о процессе
     */
    private void extractBasicInfo(String bpmnContent, BpmnParsedData parsedData) {
        // Извлекаем название процесса
        Pattern processPattern = Pattern.compile(
            "<(?:bpmn:|)process[^>]*id=\"([^\"]+)\"[^>]*(?:name=\"([^\"]*)\")?[^>]*>",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher processMatcher = processPattern.matcher(bpmnContent);
        if (processMatcher.find()) {
            String processId = processMatcher.group(1);
            String processName = processMatcher.group(2);
            
            parsedData.setProcessId(processId);
            parsedData.setProcessName(processName != null ? processName : "Unnamed Process");
            
            // Извлекаем атрибуты процесса
            extractProcessAttributes(bpmnContent, processId, parsedData);
        }
        
        // Извлекаем версию BPMN
        Pattern versionPattern = Pattern.compile(
            "xmlns(?:bpmn)?:=\"[^\"]*version[^\"]*\"",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher versionMatcher = versionPattern.matcher(bpmnContent);
        if (versionMatcher.find()) {
            parsedData.setVersion(extractVersionFromNamespace(versionMatcher.group()));
        } else {
            parsedData.setVersion("2.0");
        }
        
        // Извлекаем описание процесса
        Pattern descriptionPattern = Pattern.compile(
            "<(?:bpmn:|)documentation[^>]*>([^<]*)</(?:bpmn:|)documentation>",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher descriptionMatcher = descriptionPattern.matcher(bpmnContent);
        if (descriptionMatcher.find()) {
            parsedData.setProcessDescription(descriptionMatcher.group(1).trim());
        }
    }
    
    /**
     * Парсит BPMN элементы
     */
    private void parseElements(String bpmnContent, BpmnParsedData parsedData) {
        Matcher elementMatcher = BPMN_ELEMENT_PATTERN.matcher(bpmnContent);
        
        while (elementMatcher.find()) {
            String elementType = normalizeElementType(elementMatcher.group(1));
            String elementId = elementMatcher.group(2);
            String elementName = elementMatcher.group(3);
            
            // Пропускаем если не распознали тип
            if (!BPMN_ELEMENT_TYPES.containsValue(elementType)) {
                continue;
            }
            
            BpmnElement element = new BpmnElement();
            element.setId(elementId);
            element.setType(elementType);
            element.setName(elementName != null ? elementName : elementType + "_" + elementId);
            element.setDescription(extractElementDescription(bpmnContent, elementId));
            
            // Извлекаем атрибуты элемента
            extractElementAttributes(bpmnContent, elementId, element);
            
            parsedData.addElement(element);
        }
    }
    
    /**
     * Парсит BPMN потоки
     */
    private void parseFlows(String bpmnContent, BpmnParsedData parsedData) {
        Matcher flowMatcher = SEQUENCE_FLOW_PATTERN.matcher(bpmnContent);
        
        while (flowMatcher.find()) {
            String flowId = flowMatcher.group(1);
            String sourceElement = flowMatcher.group(2);
            String targetElement = flowMatcher.group(3);
            
            BpmnFlow flow = new BpmnFlow();
            flow.setId(flowId);
            flow.setSourceElement(sourceElement);
            flow.setTargetElement(targetElement);
            flow.setFlowType("sequenceFlow");
            flow.setName(extractFlowName(bpmnContent, flowId));
            flow.setDescription(extractFlowDescription(bpmnContent, flowId));
            
            // Определяем условие для потока
            String condition = extractFlowCondition(bpmnContent, flowId);
            if (condition != null) {
                flow.setConditionExpression(condition);
            }
            
            parsedData.addFlow(flow);
        }
    }
    
    /**
     * Связывает элементы и потоки
     */
    private void linkElementsAndFlows(BpmnParsedData parsedData) {
        // Связываем входящие потоки с элементами
        for (BpmnElement element : parsedData.getElements()) {
            List<String> incomingFlows = parsedData.getFlows().stream()
                .filter(flow -> flow.getTargetElement().equals(element.getId()))
                .map(BpmnFlow::getId)
                .collect(java.util.stream.Collectors.toList());
            element.setIncomingFlows(incomingFlows);
            
            List<String> outgoingFlows = parsedData.getFlows().stream()
                .filter(flow -> flow.getSourceElement().equals(element.getId()))
                .map(BpmnFlow::getId)
                .collect(java.util.stream.Collectors.toList());
            element.setOutgoingFlows(outgoingFlows);
        }
    }
    
    // Вспомогательные методы
    
    private String normalizeElementType(String type) {
        return BPMN_ELEMENT_TYPES.getOrDefault(type.toLowerCase(), type);
    }
    
    private void extractProcessAttributes(String bpmnContent, String processId, BpmnParsedData parsedData) {
        Pattern processAttrPattern = Pattern.compile(
            "<(?:bpmn:|)process[^>]*id=\"" + Pattern.quote(processId) + "\"[^>]*>",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = processAttrPattern.matcher(bpmnContent);
        if (matcher.find()) {
            String processTag = matcher.group();
            
            // Извлекаем атрибуты
            Map<String, String> attributes = new HashMap<>();
            
            Pattern attrPattern = Pattern.compile("([a-zA-Z:]+)=\"([^\"]*)\"");
            Matcher attrMatcher = attrPattern.matcher(processTag);
            
            while (attrMatcher.find()) {
                attributes.put(attrMatcher.group(1), attrMatcher.group(2));
            }
            
            parsedData.setProcessAttributes(attributes);
        }
    }
    
    private String extractElementDescription(String bpmnContent, String elementId) {
        Pattern descPattern = Pattern.compile(
            "<(?:bpmn:|)documentation[^>]*>([^<]*)</(?:bpmn:|)documentation>",
            Pattern.CASE_INSENSITIVE
        );
        
        // Ищем документацию рядом с элементом
        int elementIndex = bpmnContent.indexOf("id=\"" + elementId + "\"");
        if (elementIndex != -1) {
            String afterElement = bpmnContent.substring(elementIndex, Math.min(elementIndex + 500, bpmnContent.length()));
            Matcher descMatcher = descPattern.matcher(afterElement);
            if (descMatcher.find()) {
                return descMatcher.group(1).trim();
            }
        }
        
        return null;
    }
    
    private void extractElementAttributes(String bpmnContent, String elementId, BpmnElement element) {
        Map<String, String> attributes = new HashMap<>();
        
        // Поиск элемента в XML
        Pattern elementTagPattern = Pattern.compile(
            "<(?:bpmn:|)" + Pattern.quote(element.getType().toLowerCase()) + 
            "[^>]*id=\"" + Pattern.quote(elementId) + "\"[^>]*>",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = elementTagPattern.matcher(bpmnContent);
        if (matcher.find()) {
            String elementTag = matcher.group();
            
            // Извлекаем атрибуты
            Pattern attrPattern = Pattern.compile("([a-zA-Z:]+)=\"([^\"]*)\"");
            Matcher attrMatcher = attrPattern.matcher(elementTag);
            
            while (attrMatcher.find()) {
                String attrName = attrMatcher.group(1);
                String attrValue = attrMatcher.group(2);
                
                // Пропускаем стандартные атрибуты
                if (!attrName.equals("id") && !attrName.equals("name")) {
                    attributes.put(attrName, attrValue);
                }
            }
            
            // Специальная обработка для некоторых типов задач
            if ("ServiceTask".equals(element.getType())) {
                extractServiceTaskAttributes(elementTag, attributes);
            } else if ("ScriptTask".equals(element.getType())) {
                extractScriptTaskAttributes(elementTag, attributes);
            } else if ("UserTask".equals(element.getType())) {
                extractUserTaskAttributes(elementTag, attributes);
            }
        }
        
        element.setAttributes(attributes);
    }
    
    private void extractServiceTaskAttributes(String elementTag, Map<String, String> attributes) {
        // Извлекаем delegateExpression, implementation, etc.
        Pattern delegatePattern = Pattern.compile("camunda:delegateExpression=\"([^\"]*)\"");
        Matcher delegateMatcher = delegatePattern.matcher(elementTag);
        if (delegateMatcher.find()) {
            attributes.put("camunda:delegateExpression", delegateMatcher.group(1));
        }
        
        Pattern implementationPattern = Pattern.compile("camunda:implementation=\"([^\"]*)\"");
        Matcher implMatcher = implementationPattern.matcher(elementTag);
        if (implMatcher.find()) {
            attributes.put("camunda:implementation", implMatcher.group(1));
        }
    }
    
    private void extractScriptTaskAttributes(String elementTag, Map<String, String> attributes) {
        Pattern scriptFormatPattern = Pattern.compile("scriptFormat=\"([^\"]*)\"");
        Matcher formatMatcher = scriptFormatPattern.matcher(elementTag);
        if (formatMatcher.find()) {
            attributes.put("scriptFormat", formatMatcher.group(1));
        }
        
        // Извлекаем скрипт из вложенного тега
        Pattern scriptContentPattern = Pattern.compile(
            "<(?:bpmn:|)script[^>]*>([^<]*)</(?:bpmn:|)script>",
            Pattern.CASE_INSENSITIVE
        );
        Matcher scriptMatcher = scriptContentPattern.matcher(elementTag);
        if (scriptMatcher.find()) {
            attributes.put("script", scriptMatcher.group(1).trim());
        }
    }
    
    private void extractUserTaskAttributes(String elementTag, Map<String, String> attributes) {
        Pattern formKeyPattern = Pattern.compile("camunda:formKey=\"([^\"]*)\"");
        Matcher formMatcher = formKeyPattern.matcher(elementTag);
        if (formMatcher.find()) {
            attributes.put("camunda:formKey", formMatcher.group(1));
        }
        
        Pattern assigneePattern = Pattern.compile("camunda:assignee=\"([^\"]*)\"");
        Matcher assigneeMatcher = assigneePattern.matcher(elementTag);
        if (assigneeMatcher.find()) {
            attributes.put("camunda:assignee", assigneeMatcher.group(1));
        }
        
        Pattern candidateUsersPattern = Pattern.compile("camunda:candidateUsers=\"([^\"]*)\"");
        Matcher candidateMatcher = candidateUsersPattern.matcher(elementTag);
        if (candidateMatcher.find()) {
            attributes.put("camunda:candidateUsers", candidateMatcher.group(1));
        }
    }
    
    private String extractFlowName(String bpmnContent, String flowId) {
        Pattern namePattern = Pattern.compile(
            "<(?:bpmn:|)sequenceFlow[^>]*id=\"" + Pattern.quote(flowId) + 
            "\"[^>]*name=\"([^\"]*)\"[^>]*>",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = namePattern.matcher(bpmnContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    private String extractFlowDescription(String bpmnContent, String flowId) {
        return null; // Пока не реализовано
    }
    
    private String extractFlowCondition(String bpmnContent, String flowId) {
        Pattern conditionPattern = Pattern.compile(
            "<(?:bpmn:|)sequenceFlow[^>]*id=\"" + Pattern.quote(flowId) + 
            "\"[^>]*>.*?<conditionExpression[^>]*>([^<]*)</conditionExpression>.*?</sequenceFlow>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = conditionPattern.matcher(bpmnContent);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }
    
    private String extractVersionFromNamespace(String namespace) {
        if (namespace.contains("2.0")) {
            return "2.0";
        } else if (namespace.contains("2010")) {
            return "2.0";
        }
        return "2.0"; // По умолчанию
    }
}