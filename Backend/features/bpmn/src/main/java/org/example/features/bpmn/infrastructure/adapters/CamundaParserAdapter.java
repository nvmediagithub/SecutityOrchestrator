package org.example.features.bpmn.infrastructure.adapters;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessParser;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter for Camunda BPMN parser
 */
@Slf4j
@Component
public class CamundaParserAdapter implements ProcessParser {

    @Override
    public BpmnDiagram parseBpmnContent(String bpmnContent) throws BpmnParseException {
        try {
            log.debug("Parsing BPMN content with Camunda parser");

            // Parse BPMN using Camunda
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(
                new java.io.ByteArrayInputStream(bpmnContent.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            );

            if (modelInstance == null) {
                throw new BpmnParseException("Failed to parse BPMN model instance");
            }

            // Extract basic information
            String processId = extractProcessId(modelInstance);
            String processName = extractProcessName(modelInstance);

            // Create domain entity
            return BpmnDiagram.builder()
                .diagramName(processName != null ? processName : "Unnamed Process")
                .bpmnContent(bpmnContent)
                .diagramId(processId)
                .isActive(true)
                .executable(true)
                .processEngine("camunda")
                .build();

        } catch (Exception e) {
            log.error("Error parsing BPMN content", e);
            throw new BpmnParseException("Failed to parse BPMN content: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateBpmnStructure(String bpmnContent) {
        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(
                new java.io.ByteArrayInputStream(bpmnContent.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            );
            return modelInstance != null && !modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.BaseElement.class).isEmpty();
        } catch (Exception e) {
            log.debug("BPMN structure validation failed", e);
            return false;
        }
    }

    @Override
    public String extractProcessId(String bpmnContent) {
        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(
                new java.io.ByteArrayInputStream(bpmnContent.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            );
            return extractProcessId(modelInstance);
        } catch (Exception e) {
            log.debug("Failed to extract process ID", e);
            return null;
        }
    }

    @Override
    public String extractProcessName(String bpmnContent) {
        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(
                new java.io.ByteArrayInputStream(bpmnContent.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            );
            return extractProcessName(modelInstance);
        } catch (Exception e) {
            log.debug("Failed to extract process name", e);
            return null;
        }
    }

    private String extractProcessId(BpmnModelInstance modelInstance) {
        var processes = modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Process.class);
        if (!processes.isEmpty()) {
            var process = processes.iterator().next();
            return process.getId();
        }
        return null;
    }

    private String extractProcessName(BpmnModelInstance modelInstance) {
        var processes = modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Process.class);
        if (!processes.isEmpty()) {
            var process = processes.iterator().next();
            return process.getName();
        }
        return null;
    }
}