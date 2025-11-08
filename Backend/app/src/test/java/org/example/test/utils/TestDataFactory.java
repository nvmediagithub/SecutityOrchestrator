package org.example.test.utils;

import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.OpenApiSpec;
import org.example.domain.entities.TestArtifact;
import org.example.domain.valueobjects.BpmnProcessId;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.valueobjects.Version;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Фабрика для создания тестовых данных
 * Создает реалистичные тестовые объекты для различных сценариев тестирования
 */
public class TestDataFactory {
    
    // ==========================================
    // BPMN Test Data
    // ==========================================
    
    /**
     * Создает тестовую BPMN диаграмму
     */
    public static BpmnDiagram createTestBpmnDiagram() {
        BpmnDiagram diagram = new BpmnDiagram();
        diagram.setId(1L);
        diagram.setDiagramName("Test Payment Process");
        diagram.setDescription("Test BPMN diagram for payment processing");
        diagram.setBpmnContent(createSampleBpmnContent("PaymentProcess"));
        diagram.setVersion(Version.fromString("1.0.0"));
        diagram.setCreatedAt(LocalDateTime.now().minusDays(1));
        diagram.setCreatedBy("test@example.com");
        diagram.setIsActive(true);
        return diagram;
    }
    
    /**
     * Создает тестовую BPMN диаграмму с известными проблемами
     */
    public static BpmnDiagram createProblematicBpmnDiagram() {
        BpmnDiagram diagram = createTestBpmnDiagram();
        diagram.setDiagramName("Problematic Process");
        diagram.setBpmnContent(createProblematicBpmnContent());
        return diagram;
    }
    
    /**
     * Создает большую BPMN диаграмму для тестирования производительности
     */
    public static BpmnDiagram createLargeBpmnDiagram() {
        BpmnDiagram diagram = createTestBpmnDiagram();
        diagram.setDiagramName("Large Process");
        diagram.setBpmnContent(createLargeBpmnContent());
        return diagram;
    }
    
    /**
     * Создает образец BPMN содержимого
     */
    public static String createSampleBpmnContent(String processName) {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        targetNamespace="http://bpmn.io/schema/bpmn">
                <process id="""".concat(processName).concat(""" " name="Payment Processing">
                    <startEvent id="startPayment" name="Payment Started"/>
                    <userTask id="validatePayment" name="Validate Payment" 
                             camunda:assignee="payment-processor" 
                             camunda:formKey="payment-form"/>
                    <serviceTask id="processPayment" name="Process Payment" 
                               camunda:delegateExpression="${paymentProcessorDelegate}"/>
                    <exclusiveGateway id="paymentValidation" name="Payment Valid?"/>
                    <endEvent id="endSuccess" name="Payment Completed"/>
                    <endEvent id="endFailure" name="Payment Failed"/>
                    <sequenceFlow id="flow1" sourceRef="startPayment" targetRef="validatePayment"/>
                    <sequenceFlow id="flow2" sourceRef="validatePayment" targetRef="processPayment"/>
                    <sequenceFlow id="flow3" sourceRef="processPayment" targetRef="paymentValidation">
                        <conditionExpression>${paymentResult == 'success'}</conditionExpression>
                    </sequenceFlow>
                    <sequenceFlow id="flow4" sourceRef="paymentValidation" targetRef="endSuccess">
                        <conditionExpression>${validation == true}</conditionExpression>
                    </sequenceFlow>
                    <sequenceFlow id="flow5" sourceRef="paymentValidation" targetRef="endFailure"/>
                </process>
            </definitions>
            """);
    }
    
    /**
     * Создает проблемное BPMN содержимое с известными ошибками
     */
    public static String createProblematicBpmnContent() {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions>
                <process id="ProblematicProcess">
                    <startEvent id="start1"/>
                    <task id="task1" name="User Task"/>
                    <exclusiveGateway id="gateway1"/>
                    <serviceTask id="service1" name="Service Task"/>
                    <endEvent id="end1"/>
                    <!-- Проблема 1: Неполная последовательность потоков -->
                    <sequenceFlow id="flow1" sourceRef="start1" targetRef="task1"/>
                    <sequenceFlow id="flow2" sourceRef="task1" targetRef="gateway1"/>
                    <!-- Проблема 2: Сервисная задача без делегата -->
                    <sequenceFlow id="flow3" sourceRef="gateway1" targetRef="service1"/>
                    <sequenceFlow id="flow4" sourceRef="service1" targetRef="end1"/>
                    <!-- Проблема 3: Пропущенная закрывающая схема -->
            </definitions>
            """);
    }
    
    /**
     * Создает большое BPMN содержимое для тестирования производительности
     */
    public static String createLargeBpmnContent() {
        StringBuilder content = new StringBuilder("""
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions>
                <process id="LargeProcess" name="Large Process with Many Elements">
            """);
        
        // Создаем множество элементов
        for (int i = 1; i <= 50; i++) {
            content.append("""
                <task id="task_""").append(i).append(""" " name="Task """).append(i).append(""" "/>
                """);
        }
        
        // Создаем последовательные потоки
        for (int i = 1; i < 50; i++) {
            content.append("""
                <sequenceFlow id="flow_""").append(i).append(""" " sourceRef="task_""").append(i)
                .append(""" " targetRef="task_""").append(i + 1).append(""" "/>
                """);
        }
        
        content.append("""
                <startEvent id="start_large"/>
                <endEvent id="end_large"/>
                <sequenceFlow id="start_flow" sourceRef="start_large" targetRef="task_1"/>
                <sequenceFlow id="end_flow" sourceRef="task_50" targetRef="end_large"/>
            </process>
            </definitions>
            """);
        
        return content.toString();
    }
    
    // ==========================================
    // OpenAPI Test Data
    // ==========================================
    
    /**
     * Создает тестовую OpenAPI спецификацию
     */
    public static OpenApiSpec createTestOpenApiSpec() {
        OpenApiSpec spec = new OpenApiSpec();
        spec.setId(new SpecificationId("test-spec-001"));
        spec.setTitle("Test API Specification");
        spec.setDescription("Test API for BPMN analysis system");
        spec.setVersion("1.0.0");
        spec.setCreatedAt(LocalDateTime.now().minusDays(2));
        spec.setCreatedBy("test@example.com");
        spec.setIsActive(true);
        spec.setOpenApiContent(createSampleOpenApiContent());
        return spec;
    }
    
    /**
     * Создает образец OpenAPI содержимого
     */
    public static String createSampleOpenApiContent() {
        return """
            openapi: 3.0.3
            info:
              title: Test API
              version: 1.0.0
              description: API for testing BPMN analysis integration
            servers:
              - url: http://localhost:8080/api
            paths:
              /payments:
                post:
                  summary: Create payment
                  requestBody:
                    required: true
                    content:
                      application/json:
                        schema:
                          $ref: '#/components/schemas/PaymentRequest'
                  responses:
                    '201':
                      description: Payment created
                    '400':
                      description: Invalid payment data
              /payments/{paymentId}:
                get:
                  summary: Get payment by ID
                  parameters:
                    - name: paymentId
                      in: path
                      required: true
                      schema:
                        type: string
                  responses:
                    '200':
                      description: Payment found
                    '404':
                      description: Payment not found
            components:
              schemas:
                PaymentRequest:
                  type: object
                  required:
                    - amount
                    - currency
                  properties:
                    amount:
                      type: number
                      minimum: 0.01
                    currency:
                      type: string
                      pattern: '^[A-Z]{3}$'
                    description:
                      type: string
                      maxLength: 255
                PaymentResponse:
                  type: object
                  properties:
                    paymentId:
                      type: string
                    status:
                      type: string
                      enum: [PENDING, SUCCESS, FAILED]
                    amount:
                      type: number
            security:
              - bearerAuth: []
            """);
    }
    
    // ==========================================
    // Test Artifact Data
    // ==========================================
    
    /**
     * Создает тестовый артефакт
     */
    public static TestArtifact createTestArtifact() {
        TestArtifact artifact = new TestArtifact();
        artifact.setId(1L);
        artifact.setArtifactName("Test Artifact");
        artifact.setArtifactDescription("Test artifact for BPMN analysis");
        artifact.setActive(true);
        artifact.setCreatedAt(LocalDateTime.now().minusHours(2));
        return artifact;
    }
    
    // ==========================================
    // Mock Response Data
    // ==========================================
    
    /**
     * Создает мок ответ LLM для структурного анализа
     */
    public static String createMockStructureAnalysisResponse() {
        return """
            {
                "structureIssues": [
                    {
                        "type": "missingEndEvent",
                        "severity": "HIGH",
                        "element": "startPayment",
                        "description": "Process missing end event for failure path",
                        "recommendation": "Add an end event to handle payment failure"
                    },
                    {
                        "type": "deadEnd",
                        "severity": "MEDIUM",
                        "element": "processPayment",
                        "description": "Service task has no outgoing flows on error",
                        "recommendation": "Add error handling path to service task"
                    }
                ]
            }
            """;
    }
    
    /**
     * Создает мок ответ LLM для анализа безопасности
     */
    public static String createMockSecurityAnalysisResponse() {
        return """
            {
                "securityIssues": [
                    {
                        "type": "unauthenticatedServiceCall",
                        "severity": "CRITICAL",
                        "element": "processPayment",
                        "description": "Service task processes payment without authentication",
                        "recommendation": "Implement authentication check in service task",
                        "cweId": "CWE-306"
                    },
                    {
                        "type": "dataExposure",
                        "severity": "HIGH",
                        "element": "validatePayment",
                        "description": "User task may expose sensitive payment data",
                        "recommendation": "Implement data masking and field-level security",
                        "cweId": "CWE-200"
                    }
                ]
            }
            """;
    }
    
    /**
     * Создает мок ответ LLM для анализа производительности
     */
    public static String createMockPerformanceAnalysisResponse() {
        return """
            {
                "performanceIssues": [
                    {
                        "type": "complexCondition",
                        "severity": "MEDIUM",
                        "element": "paymentValidation",
                        "description": "Gateway has complex conditional logic",
                        "recommendation": "Simplify condition expressions for better performance",
                        "estimatedImpact": "medium"
                    },
                    {
                        "type": "bottleneck",
                        "severity": "HIGH",
                        "element": "processPayment",
                        "description": "Service task creates performance bottleneck",
                        "recommendation": "Optimize service implementation or add parallel processing",
                        "estimatedImpact": "high"
                    }
                ]
            }
            """;
    }
    
    /**
     * Создает мок ответ LLM для комплексного анализа
     */
    public static String createMockComprehensiveAnalysisResponse() {
        return """
            {
                "overallScore": "7.2",
                "grade": "B-",
                "summary": "Well-structured payment process with some security concerns and performance optimization opportunities",
                "analysis": {
                    "structure": {
                        "score": "8.0",
                        "issues": ["Minor missing end event for failure path", "Service task lacks error handling"]
                    },
                    "security": {
                        "score": "6.5",
                        "issues": ["Authentication missing in service task", "Data exposure risk in user task"]
                    },
                    "performance": {
                        "score": "7.0",
                        "issues": ["Complex gateway conditions", "Potential service bottleneck"]
                    },
                    "logic": {
                        "score": "8.5",
                        "issues": ["Logic flow is clear and correct"]
                    }
                },
                "recommendations": [
                    "Add authentication to service task",
                    "Implement proper error handling path",
                    "Simplify gateway condition expressions",
                    "Optimize service implementation for performance",
                    "Add data masking in user task"
                ],
                "complianceStatus": "PARTIAL"
            }
            """;
    }
    
    // ==========================================
    // Test Scenarios
    // ==========================================
    
    /**
     * Создает набор тестовых сценариев BPMN
     */
    public static List<BpmnTestScenario> createBpmnTestScenarios() {
        return Arrays.asList(
            BpmnTestScenario.SIMPLE_PAYMENT,
            BpmnTestScenario.COMPLEX_WORKFLOW,
            BpmnTestScenario.PROBLEMATIC_PROCESS,
            BpmnTestScenario.LARGE_PROCESS,
            BpmnTestScenario.SECURITY_RISK,
            BpmnTestScenario.PERFORMANCE_ISSUE
        );
    }
    
    /**
     * Создает набор тестовых сценариев OpenAPI
     */
    public static List<OpenApiTestScenario> createOpenApiTestScenarios() {
        return Arrays.asList(
            OpenApiTestScenario.SIMPLE_API,
            OpenApiTestScenario.COMPLEX_API,
            OpenApiTestScenario.SECURITY_RISK_API,
            OpenApiTestScenario.PERFORMANCE_API
        );
    }
    
    // ==========================================
    // Performance Test Data
    // ==========================================
    
    /**
     * Создает большой набор BPMN диаграмм для нагрузочного тестирования
     */
    public static List<BpmnDiagram> createPerformanceTestDataset(int count) {
        List<BpmnDiagram> diagrams = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            BpmnDiagram diagram = createTestBpmnDiagram();
            diagram.setId((long) i);
            diagram.setDiagramName("Performance Test Diagram " + i);
            diagram.setBpmnContent(createLargeBpmnContent());
            diagrams.add(diagram);
        }
        return diagrams;
    }
    
    /**
     * Создает данные для тестирования с различными размерами
     */
    public static Map<String, String> createSizeVariations() {
        Map<String, String> variations = new HashMap<>();
        variations.put("small", createSampleBpmnContent("SmallProcess"));
        variations.put("medium", createProblematicBpmnContent());
        variations.put("large", createLargeBpmnContent());
        return variations;
    }
    
    // ==========================================
    // Security Test Data
    // ==========================================
    
    /**
     * Создает BPMN диаграмму с проблемами безопасности
     */
    public static BpmnDiagram createSecurityRiskBpmnDiagram() {
        BpmnDiagram diagram = createTestBpmnDiagram();
        diagram.setDiagramName("Security Risk Process");
        diagram.setBpmnContent(createSecurityRiskBpmnContent());
        return diagram;
    }
    
    /**
     * Создает OpenAPI спецификацию с проблемами безопасности
     */
    public static OpenApiSpec createSecurityRiskOpenApiSpec() {
        OpenApiSpec spec = createTestOpenApiSpec();
        spec.setTitle("Security Risk API");
        spec.setOpenApiContent(createSecurityRiskOpenApiContent());
        return spec;
    }
    
    /**
     * Создает BPMN содержимое с проблемами безопасности
     */
    public static String createSecurityRiskBpmnContent() {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions>
                <process id="SecurityRiskProcess">
                    <startEvent id="start1"/>
                    <userTask id="adminTask" name="Admin Task" 
                             camunda:assignee="admin" 
                             camunda:formKey="admin-form"/>
                    <serviceTask id="executeCommand" name="Execute Command"
                               camunda:delegateExpression="${commandExecutor}"/>
                    <serviceTask id="queryDatabase" name="Query Database"
                               camunda:delegateExpression="${databaseQuery}"/>
                    <endEvent id="end1"/>
                    <sequenceFlow id="flow1" sourceRef="start1" targetRef="adminTask"/>
                    <sequenceFlow id="flow2" sourceRef="adminTask" targetRef="executeCommand"/>
                    <sequenceFlow id="flow3" sourceRef="executeCommand" targetRef="queryDatabase"/>
                    <sequenceFlow id="flow4" sourceRef="queryDatabase" targetRef="end1"/>
                </process>
            </definitions>
            """;
    }
    
    /**
     * Создает OpenAPI содержимое с проблемами безопасности
     */
    public static String createSecurityRiskOpenApiContent() {
        return """
            openapi: 3.0.3
            info:
              title: Security Risk API
              version: 1.0.0
            paths:
              /admin/execute:
                post:
                  summary: Execute admin command
                  requestBody:
                    content:
                      application/json:
                        schema:
                          type: object
                  responses:
                    '200':
                      description: Command executed
              /sensitive-data:
                get:
                  summary: Get sensitive data
                  responses:
                    '200':
                      description: Sensitive data
            components:
              securitySchemes:
                BearerAuth:
                  type: http
                  scheme: bearer
                  bearerFormat: JWT
            """;
    }
}