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
 * Unit tests for BpmnLLMAnalyzer
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BpmnLLMAnalyzer Unit Tests")
class BpmnLLMAnalyzerTest {

    private BpmnLLMAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new BpmnLLMAnalyzer();
    }

    @Test
    @DisplayName("Should parse structure analysis response with valid JSON")
    void shouldParseStructureAnalysisResponse() {
        // Arrange
        String llmResponse = """
            {
                "structureIssues": [
                    {
                        "type": "missingEndEvent",
                        "severity": "HIGH",
                        "element": "StartEvent_1",
                        "description": "Process missing end event",
                        "recommendation": "Add an end event to properly close the process"
                    },
                    {
                        "type": "deadEnd",
                        "severity": "MEDIUM",
                        "element": "Task_1",
                        "description": "Task has no outgoing flows",
                        "recommendation": "Connect task to next element or end event"
                    }
                ]
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        BpmnIssueClassifier.RawBpmnIssue firstIssue = result.get(0);
        assertThat(firstIssue.getType()).isEqualTo("STRUCTURE");
        assertThat(firstIssue.getTitle()).isEqualTo("missingEndEvent");
        assertThat(firstIssue.getSeverity()).isEqualTo("HIGH");
        assertThat(firstIssue.getElementId()).isEqualTo("StartEvent_1");
        assertThat(firstIssue.getDescription()).isEqualTo("Process missing end event");
        assertThat(firstIssue.getRecommendation()).isEqualTo("Add an end event to properly close the process");
    }

    @Test
    @DisplayName("Should parse security analysis response")
    void shouldParseSecurityAnalysisResponse() {
        // Arrange
        String llmResponse = """
            {
                "securityIssues": [
                    {
                        "type": "unauthenticatedServiceCall",
                        "severity": "CRITICAL",
                        "element": "ServiceTask_1",
                        "description": "Service task makes calls without authentication",
                        "recommendation": "Add authentication mechanism",
                        "cweId": "CWE-306"
                    },
                    {
                        "type": "dataExposure",
                        "severity": "HIGH",
                        "element": "UserTask_1",
                        "description": "User task may expose sensitive data",
                        "recommendation": "Implement data masking",
                        "cweId": "CWE-200"
                    }
                ]
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseSecurityAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        BpmnIssueClassifier.RawBpmnIssue firstIssue = result.get(0);
        assertThat(firstIssue.getType()).isEqualTo("SECURITY");
        assertThat(firstIssue.getTitle()).isEqualTo("unauthenticatedServiceCall");
        assertThat(firstIssue.getSeverity()).isEqualTo("CRITICAL");
        assertThat(firstIssue.getMetadata("cweId")).isEqualTo("CWE-306");
    }

    @Test
    @DisplayName("Should parse performance analysis response")
    void shouldParsePerformanceAnalysisResponse() {
        // Arrange
        String llmResponse = """
            {
                "performanceIssues": [
                    {
                        "type": "complexCondition",
                        "severity": "MEDIUM",
                        "element": "ExclusiveGateway_1",
                        "description": "Gateway has complex conditional logic",
                        "recommendation": "Simplify condition expressions",
                        "estimatedImpact": "medium"
                    },
                    {
                        "type": "bottleneck",
                        "severity": "HIGH",
                        "element": "ServiceTask_1",
                        "description": "Service task creates performance bottleneck",
                        "recommendation": "Optimize service implementation",
                        "estimatedImpact": "high"
                    }
                ]
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parsePerformanceAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        BpmnIssueClassifier.RawBpmnIssue firstIssue = result.get(0);
        assertThat(firstIssue.getType()).isEqualTo("PERFORMANCE");
        assertThat(firstIssue.getTitle()).isEqualTo("complexCondition");
        assertThat(firstIssue.getSeverity()).isEqualTo("MEDIUM");
        assertThat(firstIssue.getMetadata("impact")).isEqualTo("medium");
    }

    @Test
    @DisplayName("Should parse comprehensive analysis response")
    void shouldParseComprehensiveAnalysisResponse() {
        // Arrange
        String llmResponse = """
            {
                "overallScore": "7.5",
                "grade": "B",
                "summary": "Well-structured process with some security and performance concerns",
                "analysis": {
                    "structure": {
                        "score": "8.0",
                        "issues": ["Minor structural issues found"]
                    },
                    "security": {
                        "score": "6.5",
                        "issues": ["Authentication missing", "Data exposure risk"]
                    },
                    "performance": {
                        "score": "8.0",
                        "issues": ["Gateway complexity"]
                    },
                    "logic": {
                        "score": "7.5",
                        "issues": ["Logic flow is mostly correct"]
                    }
                },
                "recommendations": [
                    "Add authentication to service calls",
                    "Simplify gateway conditions",
                    "Implement data validation"
                ],
                "complianceStatus": "PARTIAL"
            }
            """;

        // Act
        Map<String, Object> result = analyzer.parseComprehensiveAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.get("overallScore")).isEqualTo("7.5");
        assertThat(result.get("grade")).isEqualTo("B");
        assertThat(result.get("summary")).isEqualTo("Well-structured process with some security and performance concerns");
        assertThat(result.get("complianceStatus")).isEqualTo("PARTIAL");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> analysis = (Map<String, Object>) result.get("analysis");
        assertThat(analysis).isNotNull();
        assertThat(analysis.get("structure")).isInstanceOf(Map.class);
        assertThat(analysis.get("security")).isInstanceOf(Map.class);
        assertThat(analysis.get("performance")).isInstanceOf(Map.class);
        assertThat(analysis.get("logic")).isInstanceOf(Map.class);
        
        @SuppressWarnings("unchecked")
        List<String> recommendations = (List<String>) result.get("recommendations");
        assertThat(recommendations).hasSize(3);
        assertThat(recommendations.get(0)).isEqualTo("Add authentication to service calls");
    }

    @Test
    @DisplayName("Should handle empty structure analysis response")
    void shouldHandleEmptyStructureAnalysisResponse() {
        // Arrange
        String llmResponse = """
            {
                "structureIssues": []
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle malformed JSON response")
    void shouldHandleMalformedJsonResponse() {
        // Arrange
        String llmResponse = """
            {
                "structureIssues": [
                    {
                        "type": "missingEndEvent",
                        "severity": "HIGH"
                        // Missing closing braces
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis(llmResponse);

        // Assert - Should return fallback issues
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("STRUCTURE");
        assertThat(result.get(0).getTitle()).isEqualTo("Не удалось проанализировать структуру");
        assertThat(result.get(0).getSeverity()).isEqualTo("MEDIUM");
    }

    @Test
    @DisplayName("Should handle response without expected structure")
    void shouldHandleResponseWithoutExpectedStructure() {
        // Arrange
        String llmResponse = """
            {
                "message": "Analysis completed successfully"
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should extract JSON from response with additional text")
    void shouldExtractJsonFromResponseWithAdditionalText() {
        // Arrange
        String llmResponse = """
            Analysis Results:
            {
                "structureIssues": [
                    {
                        "type": "missingEndEvent",
                        "severity": "HIGH",
                        "element": "StartEvent_1",
                        "description": "Process missing end event"
                    }
                ]
            }
            End of analysis.
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("missingEndEvent");
    }

    @Test
    @DisplayName("Should handle response with array only")
    void shouldHandleResponseWithArrayOnly() {
        // Arrange
        String llmResponse = """
            [
                {
                    "type": "missingEndEvent",
                    "severity": "HIGH",
                    "element": "StartEvent_1",
                    "description": "Process missing end event"
                }
            ]
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("missingEndEvent");
    }

    @Test
    @DisplayName("Should parse logic analysis response")
    void shouldParseLogicAnalysisResponse() {
        // Arrange
        String llmResponse = """
            {
                "logicIssues": [
                    {
                        "type": "circularReference",
                        "severity": "HIGH",
                        "element": "Gateway_1",
                        "description": "Gateway creates circular flow reference",
                        "recommendation": "Remove circular dependency"
                    }
                ]
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseLogicAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("LOGIC_ERROR");
        assertThat(result.get(0).getTitle()).isEqualTo("circularReference");
        assertThat(result.get(0).getSeverity()).isEqualTo("HIGH");
    }

    @Test
    @DisplayName("Should parse validation analysis response")
    void shouldParseValidationAnalysisResponse() {
        // Arrange
        String llmResponse = """
            {
                "validationIssues": [
                    {
                        "type": "invalidGatewayType",
                        "severity": "MEDIUM",
                        "element": "Gateway_1",
                        "description": "Gateway type is not valid BPMN 2.0",
                        "fix": "Use valid gateway type like exclusiveGateway"
                    }
                ]
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseValidationAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("VALIDATION");
        assertThat(result.get(0).getTitle()).isEqualTo("invalidGatewayType");
        assertThat(result.get(0).getRecommendation()).isEqualTo("Use valid gateway type like exclusiveGateway");
    }

    @Test
    @DisplayName("Should handle null response")
    void shouldHandleNullResponse() {
        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty response")
    void shouldHandleEmptyResponse() {
        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis("");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle whitespace-only response")
    void shouldHandleWhitespaceOnlyResponse() {
        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result = analyzer.parseStructureAnalysis("   \n\t   ");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should generate unique issue IDs")
    void shouldGenerateUniqueIssueIds() {
        // Arrange
        String llmResponse = """
            {
                "structureIssues": [
                    {
                        "type": "missingEndEvent",
                        "severity": "HIGH"
                    }
                ]
            }
            """;

        // Act
        List<BpmnIssueClassifier.RawBpmnIssue> result1 = analyzer.parseStructureAnalysis(llmResponse);
        List<BpmnIssueClassifier.RawBpmnIssue> result2 = analyzer.parseStructureAnalysis(llmResponse);

        // Assert
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.get(0).getId()).isNotEqualTo(result2.get(0).getId());
        assertThat(result1.get(0).getId()).startsWith("bpmn_issue_");
    }

    @Test
    @DisplayName("Should create fallback comprehensive data for invalid response")
    void shouldCreateFallbackComprehensiveDataForInvalidResponse() {
        // Arrange
        String invalidResponse = "This is not valid JSON at all";

        // Act
        Map<String, Object> result = analyzer.parseComprehensiveAnalysis(invalidResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.get("overallScore")).isEqualTo("5");
        assertThat(result.get("grade")).isEqualTo("C");
        assertThat(result.get("summary")).isEqualTo("Анализ не удался - LLM вернул некорректный формат ответа");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> analysis = (Map<String, Object>) result.get("analysis");
        assertThat(analysis).isNotNull();
        assertThat(analysis.get("structure")).isInstanceOf(Map.class);
        assertThat(analysis.get("security")).isInstanceOf(Map.class);
        assertThat(analysis.get("performance")).isInstanceOf(Map.class);
        
        @SuppressWarnings("unchecked")
        List<String> recommendations = (List<String>) result.get("recommendations");
        assertThat(recommendations).hasSize(2);
    }

    @Test
    @DisplayName("Should handle complex nested analysis results")
    void shouldHandleComplexNestedAnalysisResults() {
        // Arrange
        String llmResponse = """
            {
                "overallScore": "8.5",
                "grade": "A",
                "summary": "Excellent process design",
                "analysis": {
                    "structure": {
                        "score": "9.0",
                        "issues": ["Well structured", "Clear flow"],
                        "details": {
                            "elements": 15,
                            "flows": 14
                        }
                    },
                    "security": {
                        "score": "8.0",
                        "issues": ["Authentication implemented"],
                        "recommendations": ["Add audit logging"]
                    }
                },
                "recommendations": [
                    "Maintain current standards",
                    "Consider performance optimization"
                ],
                "complianceStatus": "COMPLIANT"
            }
            """;

        // Act
        Map<String, Object> result = analyzer.parseComprehensiveAnalysis(llmResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.get("overallScore")).isEqualTo("8.5");
        assertThat(result.get("grade")).isEqualTo("A");
        assertThat(result.get("complianceStatus")).isEqualTo("COMPLIANT");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> analysis = (Map<String, Object>) result.get("analysis");
        assertThat(analysis).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> structure = (Map<String, Object>) analysis.get("structure");
        assertThat(structure.get("score")).isEqualTo("9.0");
        assertThat(structure.get("issues")).isInstanceOf(List.class);
        
        @SuppressWarnings("unchecked")
        List<String> recommendations = (List<String>) result.get("recommendations");
        assertThat(recommendations).hasSize(2);
    }
}