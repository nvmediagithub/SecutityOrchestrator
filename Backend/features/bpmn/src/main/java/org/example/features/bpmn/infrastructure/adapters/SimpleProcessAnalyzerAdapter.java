package org.example.features.bpmn.infrastructure.adapters;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessAnalyzer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple heuristic implementation of {@link ProcessAnalyzer}.
 * Provides lightweight structural, security and performance analysis based on BPMN model.
 */
@Slf4j
@Component
public class SimpleProcessAnalyzerAdapter implements ProcessAnalyzer {

    @Override
    public List<ProcessIssue> analyzeStructure(BpmnDiagram diagram) {
        List<ProcessIssue> issues = new ArrayList<>();
        BpmnModelInstance model = readModel(diagram);
        if (model == null) {
            issues.add(issue("STRUCTURE", "HIGH",
                "Unable to parse BPMN content", null,
                "Verify the BPMN XML syntax."));
            return issues;
        }

        Collection<StartEvent> startEvents = model.getModelElementsByType(StartEvent.class);
        Collection<EndEvent> endEvents = model.getModelElementsByType(EndEvent.class);

        if (startEvents.isEmpty()) {
            issues.add(issue("STRUCTURE", "HIGH",
                "Diagram does not contain a start event",
                null,
                "Add exactly one start event to the diagram."));
        } else if (startEvents.size() > 1) {
            issues.add(issue("STRUCTURE", "MEDIUM",
                "Diagram contains multiple start events",
                null,
                "Verify that additional start events are required."));
        }

        if (endEvents.isEmpty()) {
            issues.add(issue("STRUCTURE", "MEDIUM",
                "Diagram does not contain an end event",
                null,
                "Add at least one end event to mark process completion."));
        }

        Collection<ExclusiveGateway> gateways = model.getModelElementsByType(ExclusiveGateway.class);
        for (ExclusiveGateway gateway : gateways) {
            int outgoing = gateway.getOutgoing().size();
            if (outgoing > 3) {
                issues.add(issue("STRUCTURE", "LOW",
                    "Gateway has more than three outgoing paths",
                    gateway.getId(),
                    "Consider splitting complex branching logic into smaller gateways."));
            }
        }

        return issues;
    }

    @Override
    public List<ProcessIssue> analyzeSecurity(BpmnDiagram diagram) {
        List<ProcessIssue> issues = new ArrayList<>();
        BpmnModelInstance model = readModel(diagram);
        if (model == null) {
            return issues;
        }

        Collection<UserTask> userTasks = model.getModelElementsByType(UserTask.class);
        for (UserTask task : userTasks) {
            if (task.getDocumentations().isEmpty()) {
                issues.add(issue("SECURITY", "MEDIUM",
                    "User task lacks documentation/instructions",
                    task.getId(),
                    "Provide operator guidelines and controls in task documentation."));
            }
            if (task.getCamundaAssignee() == null || task.getCamundaAssignee().isBlank()) {
                issues.add(issue("SECURITY", "LOW",
                    "User task is not restricted to assignee/role",
                    task.getId(),
                    "Specify assignee or candidate groups to avoid unauthorized access."));
            }
        }

        Collection<ServiceTask> serviceTasks = model.getModelElementsByType(ServiceTask.class);
        for (ServiceTask task : serviceTasks) {
            if (task.getCamundaResultVariable() == null || task.getCamundaResultVariable().isBlank()) {
                issues.add(issue("SECURITY", "LOW",
                    "Service task does not capture output",
                    task.getId(),
                    "Store service responses in process variables for auditing."));
            }
        }
        return issues;
    }

    @Override
    public List<ProcessIssue> analyzePerformance(BpmnDiagram diagram) {
        List<ProcessIssue> issues = new ArrayList<>();
        BpmnModelInstance model = readModel(diagram);
        if (model == null) {
            return issues;
        }

        Collection<FlowNode> flowNodes = model.getModelElementsByType(FlowNode.class);
        if (flowNodes.size() > 50) {
            issues.add(issue("PERFORMANCE", "MEDIUM",
                "Diagram contains more than 50 elements",
                null,
                "Consider splitting the process into sub-processes to improve maintainability."));
        }

        Collection<ServiceTask> serviceTasks = model.getModelElementsByType(ServiceTask.class);
        for (ServiceTask task : serviceTasks) {
            if (task.getExtensionElements() == null || task.getExtensionElements().getElementsQuery().count() == 0) {
                issues.add(issue("PERFORMANCE", "LOW",
                    "Service task has no retry/timeout configuration",
                    task.getId(),
                    "Configure retry cycles or timeout handlers to improve resiliency."));
            }
        }
        return issues;
    }

    @Override
    public AnalysisResult analyzeComprehensive(BpmnDiagram diagram) {
        List<ProcessIssue> structural = analyzeStructure(diagram);
        List<ProcessIssue> security = analyzeSecurity(diagram);
        List<ProcessIssue> performance = analyzePerformance(diagram);
        return new AnalysisResult(diagram.getId(), structural, security, performance);
    }

    private BpmnModelInstance readModel(BpmnDiagram diagram) {
        try {
            return Bpmn.readModelFromStream(
                new java.io.ByteArrayInputStream(diagram.getBpmnContent().getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            log.warn("Failed to read BPMN model instance", e);
            return null;
        }
    }

    private ProcessIssue issue(String type, String severity, String description, String elementId, String suggestion) {
        return new DefaultProcessIssue(type, severity, description, elementId, suggestion);
    }

    private record DefaultProcessIssue(
        String type,
        String severity,
        String description,
        String elementId,
        String suggestion
    ) implements ProcessAnalyzer.ProcessIssue {
        @Override public String getType() { return type; }
        @Override public String getSeverity() { return severity; }
        @Override public String getDescription() { return description; }
        @Override public String getElementId() { return elementId; }
        @Override public String getSuggestion() { return suggestion; }
    }
}
