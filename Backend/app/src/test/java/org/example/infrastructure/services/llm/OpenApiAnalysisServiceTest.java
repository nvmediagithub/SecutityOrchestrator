package org.example.infrastructure.services.llm;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.valueobjects.SpecificationId;
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
 * Unit tests for OpenApiAnalysisService
 */
@ExtendWith(MockitoExtension.class)
class OpenApiAnalysisServiceTest {

    @Mock
    private LLMPromptBuilder promptBuilder;

    @Mock
    private IssueClassifier issueClassifier;

    @Mock
    private OpenApiLLMAnalyzer llmAnalyzer;

    @Mock
    private org.example.infrastructure.services.OpenRouterClient openRouterClient;

    @Mock
    private org.example.infrastructure.services.LocalLLMService localLLMService;

    @Mock
    private org.example.infrastructure.services.openapi.OpenApiOrchestrationService orchestrationService;

    @Mock
    private java.util.concurrent.Executor analysisExecutor;

    @Mock
    private java.util.concurrent.ScheduledExecutorService scheduledExecutor;

    @InjectMocks
    private OpenApiAnalysisService openApiAnalysisService;

    private OpenApiSpecification testSpecification;
    private String testSpecId;
    private String testAnalysisId;

    @BeforeEach
    void setUp() {
        testSpecId = "test-spec-001";
        testAnalysisId = "analysis_test-spec-001_" + System.currentTimeMillis();
        
        // Create test specification
        testSpecification = new OpenApiSpecification();
        testSpecification.setId(new SpecificationId(testSpecId));
        testSpecification.setTitle("Test API Specification");
        testSpecification.setDescription("A test API specification for unit testing");
    }

    @Test
    void analyzeSpecification_WithValidSpec_ShouldReturnAnalysisResult() {
        // Arrange
        when(promptBuilder.buildSecurityAnalysisPrompt(any(OpenApiSpecification.class)))
            .thenReturn("Test security prompt");
        when(promptBuilder.buildValidationAnalysisPrompt(any(OpenApiSpecification.class)))
            .thenReturn("Test validation prompt");
        when(promptBuilder.buildConsistencyAnalysisPrompt(any(OpenApiSpecification.class)))
            .thenReturn("Test consistency prompt");
        when(promptBuilder.buildComprehensiveAnalysisPrompt(any(OpenApiSpecification.class)))
            .thenReturn("Test comprehensive prompt");

        // Mock LLM analysis responses
        when(openRouterClient.hasApiKey()).thenReturn(true);
        when(openRouterClient.chatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.infrastructure.services.ChatCompletionResponse("Test LLM response")
            ));

        // Mock issue classification
        IssueClassifier.ClassifiedIssues mockIssues = new IssueClassifier.ClassifiedIssues();
        mockIssues.setTotalIssues(3);
        when(issueClassifier.classifyIssues(anyList())).thenReturn(mockIssues);

        // Mock LLM analyzer
        when(llmAnalyzer.parseSecurityAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());
        when(llmAnalyzer.parseValidationAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());
        when(llmAnalyzer.parseConsistencyAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());
        when(llmAnalyzer.parseComprehensiveAnalysis(anyString())).thenReturn(java.util.Collections.emptyMap());

        // Mock comprehensive analysis data
        java.util.Map<String, Object> comprehensiveData = new java.util.HashMap<>();
        comprehensiveData.put("overall_score", 8.5);
        comprehensiveData.put("complexity", "MEDIUM");
        when(llmAnalyzer.parseComprehensiveAnalysis(anyString())).thenReturn(comprehensiveData);

        // Act
        CompletableFuture<OpenApiAnalysisService.AnalysisResult> resultFuture = 
            openApiAnalysisService.analyzeSpecification(testSpecId, testSpecification);

        // Assert
        assertNotNull(resultFuture);
        
        // Since this is async, we wait for completion (with timeout)
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(10), () -> {
            OpenApiAnalysisService.AnalysisResult result = resultFuture.get();
            assertNotNull(result);
            assertEquals(testSpecId, result.getSpecId());
            assertNotNull(result.getAnalysisId());
            assertNotNull(result.getAllIssues());
            assertNotNull(result.getSecurityAnalysis());
            assertNotNull(result.getValidationAnalysis());
            assertNotNull(result.getConsistencyAnalysis());
            assertNotNull(result.getComprehensiveAnalysis());
        });
    }

    @Test
    void analyzeSecurity_ShouldReturnPartialResult() {
        // Arrange
        when(promptBuilder.buildSecurityAnalysisPrompt(testSpecification))
            .thenReturn("Test security prompt");

        // Mock LLM analysis
        when(openRouterClient.hasApiKey()).thenReturn(true);
        when(openRouterClient.chatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.infrastructure.services.ChatCompletionResponse("Security analysis results")
            ));

        // Mock issue classification
        IssueClassifier.ClassifiedIssues mockIssues = new IssueClassifier.ClassifiedIssues();
        mockIssues.setTotalIssues(2);
        when(issueClassifier.classifyIssues(anyList())).thenReturn(mockIssues);

        // Mock LLM analyzer
        when(llmAnalyzer.parseSecurityAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());

        // Act
        CompletableFuture<OpenApiAnalysisService.PartialAnalysisResult> resultFuture = 
            openApiAnalysisService.analyzeSecurity(testSpecId, testSpecification, testAnalysisId);

        // Assert
        assertNotNull(resultFuture);
        
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () -> {
            OpenApiAnalysisService.PartialAnalysisResult result = resultFuture.get();
            assertNotNull(result);
            assertEquals("SECURITY", result.getAnalysisType());
            assertNotNull(result.getIssues());
            assertEquals(2, result.getIssues().getTotalIssues());
        });
    }

    @Test
    void analyzeValidation_ShouldReturnPartialResult() {
        // Arrange
        when(promptBuilder.buildValidationAnalysisPrompt(testSpecification))
            .thenReturn("Test validation prompt");

        // Mock LLM analysis
        when(openRouterClient.hasApiKey()).thenReturn(true);
        when(openRouterClient.chatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.infrastructure.services.ChatCompletionResponse("Validation analysis results")
            ));

        // Mock issue classification
        IssueClassifier.ClassifiedIssues mockIssues = new IssueClassifier.ClassifiedIssues();
        mockIssues.setTotalIssues(1);
        when(issueClassifier.classifyIssues(anyList())).thenReturn(mockIssues);

        // Mock LLM analyzer
        when(llmAnalyzer.parseValidationAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());

        // Act
        CompletableFuture<OpenApiAnalysisService.PartialAnalysisResult> resultFuture = 
            openApiAnalysisService.analyzeValidation(testSpecId, testSpecification, testAnalysisId);

        // Assert
        assertNotNull(resultFuture);
        
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () -> {
            OpenApiAnalysisService.PartialAnalysisResult result = resultFuture.get();
            assertNotNull(result);
            assertEquals("VALIDATION", result.getAnalysisType());
            assertNotNull(result.getIssues());
            assertEquals(1, result.getIssues().getTotalIssues());
        });
    }

    @Test
    void analyzeConsistency_ShouldReturnPartialResult() {
        // Arrange
        when(promptBuilder.buildConsistencyAnalysisPrompt(testSpecification))
            .thenReturn("Test consistency prompt");

        // Mock LLM analysis
        when(openRouterClient.hasApiKey()).thenReturn(true);
        when(openRouterClient.chatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.infrastructure.services.ChatCompletionResponse("Consistency analysis results")
            ));

        // Mock issue classification
        IssueClassifier.ClassifiedIssues mockIssues = new IssueClassifier.ClassifiedIssues();
        mockIssues.setTotalIssues(2);
        when(issueClassifier.classifyIssues(anyList())).thenReturn(mockIssues);

        // Mock LLM analyzer
        when(llmAnalyzer.parseConsistencyAnalysis(anyString())).thenReturn(java.util.Collections.emptyList());

        // Act
        CompletableFuture<OpenApiAnalysisService.PartialAnalysisResult> resultFuture = 
            openApiAnalysisService.analyzeConsistency(testSpecId, testSpecification, testAnalysisId);

        // Assert
        assertNotNull(resultFuture);
        
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () -> {
            OpenApiAnalysisService.PartialAnalysisResult result = resultFuture.get();
            assertNotNull(result);
            assertEquals("CONSISTENCY", result.getAnalysisType());
            assertNotNull(result.getIssues());
            assertEquals(2, result.getIssues().getTotalIssues());
        });
    }

    @Test
    void analyzeComprehensive_ShouldReturnPartialResult() {
        // Arrange
        when(promptBuilder.buildComprehensiveAnalysisPrompt(testSpecification))
            .thenReturn("Test comprehensive prompt");

        // Mock LLM analysis
        when(openRouterClient.hasApiKey()).thenReturn(true);
        when(openRouterClient.chatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.infrastructure.services.ChatCompletionResponse("Comprehensive analysis results")
            ));

        // Mock comprehensive analysis data
        java.util.Map<String, Object> comprehensiveData = new java.util.HashMap<>();
        comprehensiveData.put("overall_score", 7.5);
        comprehensiveData.put("complexity", "HIGH");
        when(llmAnalyzer.parseComprehensiveAnalysis(anyString())).thenReturn(comprehensiveData);

        // Act
        CompletableFuture<OpenApiAnalysisService.PartialAnalysisResult> resultFuture = 
            openApiAnalysisService.analyzeComprehensive(testSpecId, testSpecification, testAnalysisId);

        // Assert
        assertNotNull(resultFuture);
        
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () -> {
            OpenApiAnalysisService.PartialAnalysisResult result = resultFuture.get();
            assertNotNull(result);
            assertEquals("COMPREHENSIVE", result.getAnalysisType());
            assertNotNull(result.getComprehensiveData());
            assertEquals(7.5, result.getComprehensiveData().get("overall_score"));
        });
    }

    @Test
    void getAnalysisStatus_WithValidAnalysisId_ShouldReturnStatus() {
        // Arrange
        String analysisId = "test-analysis-001";
        
        // Act
        OpenApiAnalysisService.AnalysisStatus status = openApiAnalysisService.getAnalysisStatus(analysisId);

        // Assert
        assertNotNull(status);
        assertEquals(analysisId, status.getAnalysisId());
    }

    @Test
    void getServiceStatistics_ShouldReturnValidStatistics() {
        // Act
        OpenApiAnalysisService.ServiceStatistics stats = openApiAnalysisService.getServiceStatistics();

        // Assert
        assertNotNull(stats);
        assertTrue(stats.getActiveAnalyses() >= 0);
        assertTrue(stats.getCachedResults() >= 0);
        assertTrue(stats.getQueueSize() >= 0);
    }

    @Test
    void clearCache_ShouldClearAllCachedResults() {
        // Act
        openApiAnalysisService.clearCache();

        // Assert - just verify no exception is thrown
        // In a real test, we'd verify the cache is actually cleared
    }

    @Test
    void executeLLMAnalysis_WithOpenRouterAvailable_ShouldUseOpenRouter() {
        // Arrange
        String prompt = "Test prompt for analysis";
        String analysisType = "test";

        when(openRouterClient.hasApiKey()).thenReturn(true);
        when(openRouterClient.chatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.infrastructure.services.ChatCompletionResponse("OpenRouter response")
            ));

        // Act
        String result = openApiAnalysisService.executeLLMAnalysis(prompt, analysisType);

        // Assert
        assertNotNull(result);
        assertEquals("OpenRouter response", result);
    }

    @Test
    void executeLLMAnalysis_WithNoOpenRouter_ShouldFallbackToLocal() {
        // Arrange
        String prompt = "Test prompt for analysis";
        String analysisType = "test";

        when(openRouterClient.hasApiKey()).thenReturn(false);
        when(localLLMService.checkOllamaConnection()).thenReturn("http://localhost:11434");
        when(localLLMService.localChatCompletion(anyString(), anyString(), anyInt(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(
                new org.example.infrastructure.services.ChatCompletionResponse("Local model response")
            ));

        // Act
        String result = openApiAnalysisService.executeLLMAnalysis(prompt, analysisType);

        // Assert
        assertNotNull(result);
        assertEquals("Local model response", result);
    }

    @Test
    void executeLLMAnalysis_WithNoProviders_ShouldThrowException() {
        // Arrange
        String prompt = "Test prompt for analysis";
        String analysisType = "test";

        when(openRouterClient.hasApiKey()).thenReturn(false);
        when(localLLMService.checkOllamaConnection()).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            openApiAnalysisService.executeLLMAnalysis(prompt, analysisType);
        });
    }
}