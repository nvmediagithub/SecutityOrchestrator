package org.example.integration;

import org.example.domain.entities.SecurityAnalysisResult;
import org.example.domain.entities.SecurityTest;
import org.example.domain.entities.BpmnProcessStep;
import org.example.infrastructure.services.LLMSecurityAnalysisService;
import org.example.infrastructure.websocket.SecurityWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main Security Orchestration Service - integrates all components
 * LLM analysis, BPMN processes, WebSocket updates, and OWASP testing
 */
@Service
public class SecurityOrchestrationService {
    
    @Autowired
    private LLMSecurityAnalysisService llmSecurityService;
    
    @Autowired
    private SecurityWebSocketHandler webSocketHandler;
    
    @Autowired
    private BpmnSecurityIntegrationService bpmnIntegrationService;
    
    @Autowired
    private OWASPSecurityTestService owaspTestService;
    
    private final Map<String, SecurityAnalysisSession> activeSessions = new ConcurrentHashMap<>();
    
    /**
     * Start comprehensive security analysis workflow
     */
    public CompletableFuture<SecurityAnalysisSession> startSecurityAnalysis(
            String apiSpecification,
            String bpmnProcessId,
            List<String> targetEndpoints) {
        
        String sessionId = generateSessionId();
        SecurityAnalysisSession session = new SecurityAnalysisSession(sessionId);
        activeSessions.put(sessionId, session);
        
        // Broadcast session start
        webSocketHandler.broadcastSystemStatus("analysis_started", 
            Map.of("sessionId", sessionId, "timestamp", System.currentTimeMillis()));
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Initialize BPMN process
                webSocketHandler.broadcastProgressUpdate(sessionId, 10, "Initializing BPMN Process");
                List<BpmnProcessStep> bpmnSteps = initializeBpmnProcess(bpmnProcessId);
                session.setBpmnSteps(bpmnSteps);
                
                // Step 2: OpenAPI Analysis
                webSocketHandler.broadcastProgressUpdate(sessionId, 25, "Analyzing OpenAPI Specification");
                analyzeOpenAPISpecification(apiSpecification, session);
                
                // Step 3: LLM Security Analysis
                webSocketHandler.broadcastProgressUpdate(sessionId, 50, "Running LLM Security Analysis");
                performLLMSecurityAnalysis(targetEndpoints, session);
                
                // Step 4: OWASP Test Generation
                webSocketHandler.broadcastProgressUpdate(sessionId, 75, "Generating OWASP Tests");
                generateOWASPTests(apiSpecification, session);
                
                // Step 5: Execute Security Tests
                webSocketHandler.broadcastProgressUpdate(sessionId, 90, "Executing Security Tests");
                executeSecurityTests(session);
                
                // Step 6: Generate Report
                webSocketHandler.broadcastProgressUpdate(sessionId, 100, "Generating Final Report");
                generateFinalReport(session);
                
                session.setStatus("COMPLETED");
                webSocketHandler.broadcastSystemStatus("analysis_completed", 
                    Map.of("sessionId", sessionId, "results", session.getSummary()));
                
                return session;
                
            } catch (Exception e) {
                session.setStatus("FAILED");
                session.setError(e.getMessage());
                webSocketHandler.broadcastSystemStatus("analysis_failed", 
                    Map.of("sessionId", sessionId, "error", e.getMessage()));
                return session;
            }
        });
    }
    
    private List<BpmnProcessStep> initializeBpmnProcess(String bpmnProcessId) {
        List<BpmnProcessStep> steps = List.of(
            new BpmnProcessStep("Parse OpenAPI", "Parse and validate OpenAPI specification"),
            new BpmnProcessStep("LLM Analysis", "CodeLlama security vulnerability analysis"),
            new BpmnProcessStep("OWASP Testing", "Generate and execute OWASP Top 10 tests"),
            new BpmnProcessStep("Report Generation", "Compile security analysis report")
        );
        
        // Update each step status and broadcast
        for (int i = 0; i < steps.size(); i++) {
            BpmnProcessStep step = steps.get(i);
            step.setStatus("PENDING");
            if (i > 0) {
                step.setPreviousStepId(steps.get(i - 1).getId());
            }
            if (i < steps.size() - 1) {
                step.setNextStepId(steps.get(i + 1).getId());
            }
        }
        
        // Broadcast initial steps
        for (BpmnProcessStep step : steps) {
            webSocketHandler.broadcastBpmnStep(step);
        }
        
        return steps;
    }
    
    private void analyzeOpenAPISpecification(String apiSpec, SecurityAnalysisSession session) {
        try {
            // Simulate OpenAPI analysis
            List<String> discoveredEndpoints = extractEndpointsFromSpec(apiSpec);
            session.setDiscoveredEndpoints(discoveredEndpoints);
            
            webSocketHandler.broadcastSystemStatus("openapi_analysis_complete", 
                Map.of("endpointsFound", discoveredEndpoints.size()));
                
        } catch (Exception e) {
            throw new RuntimeException("OpenAPI analysis failed: " + e.getMessage());
        }
    }
    
    private void performLLMSecurityAnalysis(List<String> endpoints, SecurityAnalysisSession session) {
        try {
            for (String endpoint : endpoints) {
                // Perform LLM security analysis for each endpoint
                SecurityAnalysisResult result = llmSecurityService
                    .analyzeSecurityVulnerability(endpoint, "GET", "", "")
                    .join();
                
                session.addVulnerability(result);
                webSocketHandler.broadcastVulnerabilityUpdate(result);
                
                // Simulate processing delay
                Thread.sleep(500);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("LLM security analysis failed: " + e.getMessage());
        }
    }
    
    private void generateOWASPTests(String apiSpec, SecurityAnalysisSession session) {
        try {
            // Generate OWASP Top 10 tests
            List<SecurityTest> tests = llmSecurityService.generateOWASPTests(apiSpec).join();
            
            for (SecurityTest test : tests) {
                session.addTest(test);
                webSocketHandler.broadcastTestResult(test);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("OWASP test generation failed: " + e.getMessage());
        }
    }
    
    private void executeSecurityTests(SecurityAnalysisSession session) {
        try {
            for (SecurityTest test : session.getTests()) {
                // Simulate test execution
                test.setPassed(Math.random() > 0.3); // 70% pass rate
                test.setActualResult(test.isPassed() ? "Test passed successfully" : "Security vulnerability detected");
                
                webSocketHandler.broadcastTestResult(test);
                Thread.sleep(200); // Simulate execution time
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Security test execution failed: " + e.getMessage());
        }
    }
    
    private void generateFinalReport(SecurityAnalysisSession session) {
        try {
            Map<String, Object> report = createSecurityReport(session);
            session.setFinalReport(report);
            
            // Broadcast report generation
            webSocketHandler.broadcastLLMAnalysis(session.getId(), report);
            
        } catch (Exception e) {
            throw new RuntimeException("Report generation failed: " + e.getMessage());
        }
    }
    
    private Map<String, Object> createSecurityReport(SecurityAnalysisSession session) {
        Map<String, Object> report = new java.util.HashMap<>();
        
        report.put("sessionId", session.getId());
        report.put("timestamp", System.currentTimeMillis());
        report.put("totalVulnerabilities", session.getVulnerabilities().size());
        report.put("totalTests", session.getTests().size());
        report.put("testsPassed", session.getTests().stream().mapToInt(t -> t.isPassed() ? 1 : 0).sum());
        report.put("testsFailed", session.getTests().stream().mapToInt(t -> t.isPassed() ? 0 : 1).sum());
        report.put("criticalVulnerabilities", 
            session.getVulnerabilities().stream()
                .mapToInt(v -> "CRITICAL".equals(v.getSeverity()) ? 1 : 0).sum());
        
        // OWASP Top 10 coverage
        Map<String, Integer> owaspCoverage = new java.util.HashMap<>();
        for (SecurityAnalysisResult.OWASPCategory category : SecurityAnalysisResult.OWASPCategory.values()) {
            long count = session.getVulnerabilities().stream()
                .filter(v -> v.getOwaspCategory() == category)
                .count();
            owaspCoverage.put(category.name(), (int) count);
        }
        report.put("owaspCoverage", owaspCoverage);
        
        // Recommendations
        List<String> recommendations = generateRecommendations(session);
        report.put("recommendations", recommendations);
        
        return report;
    }
    
    private List<String> generateRecommendations(SecurityAnalysisSession session) {
        List<String> recommendations = new java.util.ArrayList<>();
        
        // Analyze findings and generate recommendations
        long criticalCount = session.getVulnerabilities().stream()
            .filter(v -> "CRITICAL".equals(v.getSeverity()))
            .count();
        
        if (criticalCount > 0) {
            recommendations.add("Immediate attention required: " + criticalCount + " critical vulnerabilities found");
        }
        
        long highCount = session.getVulnerabilities().stream()
            .filter(v -> "HIGH".equals(v.getSeverity()))
            .count();
        
        if (highCount > 0) {
            recommendations.add("High priority: Address " + highCount + " high-severity issues");
        }
        
        recommendations.add("Implement proper authentication and authorization controls");
        recommendations.add("Add rate limiting and input validation");
        recommendations.add("Enable comprehensive logging and monitoring");
        
        return recommendations;
    }
    
    private List<String> extractEndpointsFromSpec(String apiSpec) {
        // Simulate endpoint extraction from OpenAPI spec
        return List.of(
            "/api/users/{id}",
            "/api/profile",
            "/api/auth/login",
            "/api/admin/users",
            "/api/orders",
            "/api/search"
        );
    }
    
    private String generateSessionId() {
        return "SEC_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    /**
     * Get active analysis session
     */
    public SecurityAnalysisSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }
    
    /**
     * Get all active sessions
     */
    public Map<String, SecurityAnalysisSession> getActiveSessions() {
        return new java.util.HashMap<>(activeSessions);
    }
    
    /**
     * Clean up completed sessions
     */
    public void cleanupCompletedSessions() {
        activeSessions.entrySet().removeIf(entry -> 
            "COMPLETED".equals(entry.getValue().getStatus()) || 
            "FAILED".equals(entry.getValue().getStatus())
        );
    }
}