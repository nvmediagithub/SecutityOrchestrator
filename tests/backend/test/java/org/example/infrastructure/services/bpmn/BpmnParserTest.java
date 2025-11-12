package org.example.infrastructure.services.bpmn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BpmnParser
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BpmnParser Unit Tests")
class BpmnParserTest {

    private BpmnParser parser;

    @BeforeEach
    void setUp() {
        parser = new BpmnParser();
    }

    @Test
    @DisplayName("Should parse valid BPMN content successfully")
    void shouldParseValidBpmnContent() {
        // Arrange
        String bpmnContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        targetNamespace="http://bpmn.io/schema/bpmn">
                <process id="TestProcess" name="Test Process">
                    <startEvent id="start1" name="Start Event"/>
                    <task id="task1" name="User Task"/>
                    <endEvent id="end1" name="End Event"/>
                    <sequenceFlow id="flow1" sourceRef="start1" targetRef="task1"/>
                    <sequenceFlow id="flow2" sourceRef="task1" targetRef="end1"/>
                </process>
            </definitions>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProcessId()).isEqualTo("TestProcess");
        assertThat(result.getProcessName()).isEqualTo("Test Process");
        assertThat(result.getVersion()).isEqualTo("2.0");
        assertThat(result.getElements()).hasSize(3);
        assertThat(result.getFlows()).hasSize(2);
    }

    @Test
    @DisplayName("Should throw exception for null BPMN content")
    void shouldThrowExceptionForNullBpmnContent() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parseBpmnContent(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty BPMN content")
    void shouldThrowExceptionForEmptyBpmnContent() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parseBpmnContent("");
        });
    }

    @Test
    @DisplayName("Should throw exception for whitespace-only BPMN content")
    void shouldThrowExceptionForWhitespaceBpmnContent() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parseBpmnContent("   \n\t   ");
        });
    }

    @Test
    @DisplayName("Should handle BPMN content with namespaces")
    void shouldHandleBpmnContentWithNamespaces() {
        // Arrange
        String bpmnContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                        xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                        xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                        targetNamespace="http://bpmn.io/schema/bpmn">
                <process id="Process_1" name="Process with namespaces">
                    <bpmn:startEvent id="StartEvent_1" name="Start"/>
                    <bpmn:task id="Task_1" name="Task"/>
                </process>
            </definitions>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProcessId()).isEqualTo("Process_1");
        assertThat(result.getProcessName()).isEqualTo("Process with namespaces");
        assertThat(result.getElements()).hasSize(2);
    }

    @Test
    @DisplayName("Should extract task attributes correctly")
    void shouldExtractTaskAttributes() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <serviceTask id="serviceTask1" name="Service Task" 
                           camunda:delegateExpression="testDelegate" 
                           camunda:implementation="Java"/>
                <userTask id="userTask1" name="User Task" 
                         camunda:assignee="testUser" 
                         camunda:formKey="testForm"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getElements()).hasSize(2);
        
        BpmnElement serviceTask = result.getElements().stream()
            .filter(e -> "serviceTask1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        assertThat(serviceTask).isNotNull();
        assertThat(serviceTask.getAttributes()).containsKey("camunda:delegateExpression");
        assertThat(serviceTask.getAttributes().get("camunda:delegateExpression")).isEqualTo("testDelegate");
        
        BpmnElement userTask = result.getElements().stream()
            .filter(e -> "userTask1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        assertThat(userTask).isNotNull();
        assertThat(userTask.getAttributes()).containsKey("camunda:assignee");
        assertThat(userTask.getAttributes().get("camunda:assignee")).isEqualTo("testUser");
    }

    @Test
    @DisplayName("Should handle different gateway types")
    void shouldHandleDifferentGatewayTypes() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <exclusiveGateway id="exclusiveGateway1" name="Exclusive Gateway"/>
                <parallelGateway id="parallelGateway1" name="Parallel Gateway"/>
                <inclusiveGateway id="inclusiveGateway1" name="Inclusive Gateway"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getElements()).hasSize(3);
        
        List<String> gatewayTypes = result.getElements().stream()
            .map(BpmnElement::getType)
            .toList();
            
        assertThat(gatewayTypes).containsExactlyInAnyOrder(
            "ExclusiveGateway", "ParallelGateway", "InclusiveGateway"
        );
    }

    @Test
    @DisplayName("Should handle different event types")
    void shouldHandleDifferentEventTypes() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <startEvent id="startEvent1" name="Start Event"/>
                <endEvent id="endEvent1" name="End Event"/>
                <timerEvent id="timerEvent1" name="Timer Event"/>
                <messageEvent id="messageEvent1" name="Message Event"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getElements()).hasSize(4);
        
        List<String> eventTypes = result.getElements().stream()
            .map(BpmnElement::getType)
            .toList();
            
        assertThat(eventTypes).containsExactlyInAnyOrder(
            "StartEvent", "EndEvent", "TimerEvent", "MessageEvent"
        );
    }

    @Test
    @DisplayName("Should extract sequence flow conditions")
    void shouldExtractSequenceFlowConditions() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <startEvent id="start1"/>
                <exclusiveGateway id="gateway1"/>
                <task id="task1"/>
                <task id="task2"/>
                <endEvent id="end1"/>
                <sequenceFlow id="flow1" sourceRef="start1" targetRef="gateway1"/>
                <sequenceFlow id="flow2" sourceRef="gateway1" targetRef="task1">
                    <conditionExpression>${condition == true}</conditionExpression>
                </sequenceFlow>
                <sequenceFlow id="flow3" sourceRef="gateway1" targetRef="task2">
                    <conditionExpression>${condition == false}</conditionExpression>
                </sequenceFlow>
                <sequenceFlow id="flow4" sourceRef="task1" targetRef="end1"/>
                <sequenceFlow id="flow5" sourceRef="task2" targetRef="end1"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getFlows()).hasSize(5);
        
        // Find flows with conditions
        List<BpmnFlow> conditionalFlows = result.getFlows().stream()
            .filter(flow -> flow.getConditionExpression() != null)
            .toList();
            
        assertThat(conditionalFlows).hasSize(2);
        
        BpmnFlow flow2 = conditionalFlows.stream()
            .filter(flow -> "flow2".equals(flow.getId()))
            .findFirst()
            .orElse(null);
            
        assertThat(flow2).isNotNull();
        assertThat(flow2.getConditionExpression()).contains("${condition == true}");
    }

    @Test
    @DisplayName("Should handle malformed BPMN content gracefully")
    void shouldHandleMalformedBpmnContent() {
        // Arrange
        String malformedContent = "<process id=\"TestProcess\"><startEvent id=\"start1\""; // Incomplete XML

        // Act & Assert - Should throw runtime exception
        assertThrows(RuntimeException.class, () -> {
            parser.parseBpmnContent(malformedContent);
        });
    }

    @Test
    @DisplayName("Should handle BPMN content with documentation")
    void shouldHandleBpmnContentWithDocumentation() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess" name="Process with Documentation">
                <documentation>Test process documentation</documentation>
                <startEvent id="start1" name="Start Event">
                    <documentation>Start event documentation</documentation>
                </startEvent>
                <task id="task1" name="Task">
                    <documentation>Task documentation</documentation>
                </task>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProcessDescription()).isEqualTo("Test process documentation");
        
        BpmnElement startEvent = result.getElements().stream()
            .filter(e -> "start1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        assertThat(startEvent).isNotNull();
        assertThat(startEvent.getDescription()).isEqualTo("Start event documentation");
    }

    @Test
    @DisplayName("Should link elements and flows correctly")
    void shouldLinkElementsAndFlows() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <startEvent id="start1"/>
                <task id="task1"/>
                <task id="task2"/>
                <endEvent id="end1"/>
                <sequenceFlow id="flow1" sourceRef="start1" targetRef="task1"/>
                <sequenceFlow id="flow2" sourceRef="task1" targetRef="task2"/>
                <sequenceFlow id="flow3" sourceRef="task2" targetRef="end1"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        BpmnElement startEvent = result.getElements().stream()
            .filter(e -> "start1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        BpmnElement task1 = result.getElements().stream()
            .filter(e -> "task1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        BpmnElement task2 = result.getElements().stream()
            .filter(e -> "task2".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        BpmnElement endEvent = result.getElements().stream()
            .filter(e -> "end1".equals(e.getId()))
            .findFirst()
            .orElse(null);

        // Check incoming/outgoing flows
        assertThat(startEvent.getOutgoingFlows()).containsExactly("flow1");
        assertThat(startEvent.getIncomingFlows()).isEmpty();
        
        assertThat(task1.getOutgoingFlows()).containsExactly("flow2");
        assertThat(task1.getIncomingFlows()).containsExactly("flow1");
        
        assertThat(task2.getOutgoingFlows()).containsExactly("flow3");
        assertThat(task2.getIncomingFlows()).containsExactly("flow2");
        
        assertThat(endEvent.getOutgoingFlows()).isEmpty();
        assertThat(endEvent.getIncomingFlows()).containsExactly("flow3");
    }

    @Test
    @DisplayName("Should handle sub-processes correctly")
    void shouldHandleSubProcesses() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <subProcess id="subProcess1" name="Sub Process">
                    <startEvent id="subStart1"/>
                    <endEvent id="subEnd1"/>
                </subProcess>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getElements()).hasSize(3); // subProcess + subStart + subEnd
        
        BpmnElement subProcess = result.getElements().stream()
            .filter(e -> "subProcess1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        assertThat(subProcess).isNotNull();
        assertThat(subProcess.getType()).isEqualTo("SubProcess");
    }

    @Test
    @DisplayName("Should extract process attributes correctly")
    void shouldExtractProcessAttributes() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess" name="Test Process" 
                    version="1.0" executable="true" versionTag="v1.0"
                    processType="None" isClosed="false" isExecutable="true">
                <startEvent id="start1"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProcessAttributes()).isNotNull();
        assertThat(result.getProcessAttributes()).containsKey("version");
        assertThat(result.getProcessAttributes()).containsKey("executable");
    }

    @Test
    @DisplayName("Should handle complex BPMN content with multiple pools and lanes")
    void shouldHandleComplexBpmnContent() {
        // Arrange
        String bpmnContent = """
            <definitions>
                <process id="Pool1Process" isExecutable="true">
                    <laneSet id="LaneSet_1">
                        <lane id="Lane1" name="Lane 1">
                            <flowNodeRef>task1</flowNodeRef>
                        </lane>
                        <lane id="Lane2" name="Lane 2">
                            <flowNodeRef>task2</flowNodeRef>
                        </lane>
                    </laneSet>
                    <userTask id="task1" name="Task 1"/>
                    <userTask id="task2" name="Task 2"/>
                </process>
            </definitions>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getElements()).hasSize(3); // task1, task2, and implicitly defined lane elements
        assertThat(result.getProcessId()).isEqualTo("Pool1Process");
    }

    @Test
    @DisplayName("Should handle script task with inline scripts")
    void shouldHandleScriptTaskWithInlineScripts() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <scriptTask id="scriptTask1" name="Script Task" scriptFormat="JavaScript">
                    <script>
                        console.log("Hello World");
                        return "test";
                    </script>
                </scriptTask>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        BpmnElement scriptTask = result.getElements().stream()
            .filter(e -> "scriptTask1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        assertThat(scriptTask).isNotNull();
        assertThat(scriptTask.getType()).isEqualTo("ScriptTask");
        assertThat(scriptTask.getAttributes()).containsKey("scriptFormat");
        assertThat(scriptTask.getAttributes()).containsKey("script");
        assertThat(scriptTask.getAttributes().get("scriptFormat")).isEqualTo("JavaScript");
    }

    @Test
    @DisplayName("Should extract task names correctly")
    void shouldExtractTaskNames() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <startEvent id="start1" name="Start Process"/>
                <task id="task1" name="User Task 1"/>
                <task id="task2" name=""/>
                <task id="task3"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getElements()).hasSize(4);
        
        List<String> taskNames = result.getElements().stream()
            .map(BpmnElement::getName)
            .toList();
            
        assertThat(taskNames).containsExactlyInAnyOrder(
            "Start Process", "User Task 1", "Task_2", "Task_3"
        );
    }

    @Test
    @DisplayName("Should handle complex nested BPMN structures")
    void shouldHandleComplexNestedBpmnStructures() {
        // Arrange
        String bpmnContent = """
            <definitions>
                <process id="ComplexProcess">
                    <startEvent id="start1"/>
                    <callActivity id="callActivity1" name="Call Activity"/>
                    <subProcess id="subProcess1" name="Sub Process">
                        <startEvent id="subStart1"/>
                        <endEvent id="subEnd1"/>
                    </subProcess>
                    <endEvent id="end1"/>
                    <sequenceFlow id="flow1" sourceRef="start1" targetRef="callActivity1"/>
                    <sequenceFlow id="flow2" sourceRef="callActivity1" targetRef="subProcess1"/>
                    <sequenceFlow id="flow3" sourceRef="subProcess1" targetRef="end1"/>
                </process>
            </definitions>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        assertThat(result.getElements()).hasSize(6); // start, callActivity, subProcess, subStart, subEnd, end
        assertThat(result.getFlows()).hasSize(3);
    }

    @Test
    @DisplayName("Should handle call activities")
    void shouldHandleCallActivities() {
        // Arrange
        String bpmnContent = """
            <process id="TestProcess">
                <callActivity id="callActivity1" name="Call Sub Process" 
                            calledElement="SubProcess" 
                            processVersion="2" 
                            businessKey="key123"/>
            </process>
            """;

        // Act
        BpmnParsedData result = parser.parseBpmnContent(bpmnContent);

        // Assert
        BpmnElement callActivity = result.getElements().stream()
            .filter(e -> "callActivity1".equals(e.getId()))
            .findFirst()
            .orElse(null);
            
        assertThat(callActivity).isNotNull();
        assertThat(callActivity.getType()).isEqualTo("CallActivity");
        assertThat(callActivity.getAttributes()).containsKey("calledElement");
        assertThat(callActivity.getAttributes()).containsKey("processVersion");
    }
}