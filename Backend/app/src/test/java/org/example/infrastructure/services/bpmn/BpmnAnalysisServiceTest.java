
package org.example.infrastructure.services.bpmn;

import org.example.domain.entities.BpmnDiagram;
import org.example.domain.valueobjects.BpmnProcessId;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.infrastructure.services.ArtifactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for BpmnAnalysisService
 */
@ExtendWith(MockitoExtension.class)
class BpmnAnalysisServiceTest {

    @Mock
    private BpmnLLMPromptBuilder promptBuilder;

    @Mock
    private BpmnIssueClassifier issueClassifier;

    @Mock
    private BpmnLLMAnalyzer llmAnalyzer;

    @Mock
    private BpmnParser bpmnParser;

    @Mock
    private ProcessFlowAnalyzer flowAnalyzer;

    @Mock
    private StructureAnalyzer structureAnalyzer;

    @Mock
    private SecurityProcessAnalyzer securityAnalyzer;

    @Mock
    private PerformanceProcessAnalyzer performanceAnalyzer;

    @Mock
    private BpmnApiMapper apiMapper;

    @Mock
    private OpenRouterClient openRouterClient;

    @Mock
    private LocalLLMService localLLMService;

    @Mock
    private ArtifactService artifactService;

    @Mock
    private java.util.concurrent.Executor bpmnAnalysisExecutor;

    @Mock
    private java.util.concurrent.ScheduledExecutorService scheduledExecutor;

    @InjectMocks
    private BpmnAnalysisService bpmnAnalysisService;

    private BpmnDiagram testDiagram;
    private String testDiagramId;
    private BpmnParsedData parsedData;

    @BeforeEach
    void setUp() {
        testDiagramId = "test-bpmn-001";
        testDiagram = new BpmnDiagram();
        testDiagram.setId(testDiagramId);
        testDiagram.setDiagramName("Test BPMN Diagram");
        testDiagram.setBpmnContent(createTestBpmnContent());
        
        parsedData = new BpmnParsedData();
    }

    @Test
    void analyzeBpmnDiagram_WithValidDiagram_ShouldReturnAnalysisResult() throws Exception {
        // Arrange
        when(bpmnParser.parseBpmnContent(anyString())).thenReturn(parsedData);
        when(openRouterClient.hasApiKey()).thenReturn(true);
        when(openRouterClient.chatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.domain.dto.llm.ChatCompletionResponse("Test LLM response")
            ));
        
        // Mock analyzer results
        when(llmAnalyzer.parseStructureAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());
        when(llmAnalyzer.parseSecurityAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());
        when(llmAnalyzer.parsePerformanceAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());
        when(llmAnalyzer.parseComprehensiveAnalysis(anyString())).thenReturn(java.util.Collections.emptyMap());
        
        // Mock structure analyzer
        when(structureAnalyzer.analyzeStructure(any(BpmnParsedData.class)))
            .thenReturn(new StructureAnalysisResult());
        
        // Mock security analyzer
        when(securityAnalyzer.analyzeSecurity(any(BpmnParsedData.class)))
            .thenReturn(new SecurityAnalysisResult());
        
        // Mock flow analyzer
        when(flowAnalyzer.analyzeFlows(any(BpmnParsedData.class)))
            .thenReturn(new FlowAnalysisResult());
        
        // Mock performance analyzer
        when(performanceAnalyzer.analyzePerformance(any(BpmnParsedData.class), any(FlowAnalysisResult.class)))
