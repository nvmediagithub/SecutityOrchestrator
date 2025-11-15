package org.example.integration;

import org.example.domain.entities.SecurityAnalysisResult;
import org.example.domain.entities.SecurityTest;
import org.example.domain.entities.BpmnProcessStep;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Security Analysis Session - tracks complete security analysis workflow
 */
public class SecurityAnalysisSession {
    private String id;
    private String status; // RUNNING, COMPLETED, FAILED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String error;
    
    // Analysis results
    private List<SecurityAnalysisResult> vulnerabilities = new ArrayList<>();
    private List<SecurityTest> tests = new ArrayList<>();
    private List<BpmnProcessStep> bpmnSteps = new ArrayList<>();
    private List<String> discoveredEndpoints = new ArrayList<>();
    private Map<String, Object> finalReport = new HashMap<>();
    
    // Performance metrics
    private long totalExecutionTime;
    private int llmAnalysisCount;
    private int owaspTestsGenerated;
    
    public SecurityAnalysisSession(String id) {
        this.id = id;
        this.status = "RUNNING";
        this.startTime = LocalDateTime.now();
        this.totalExecutionTime = 0;
        this.llmAnalysisCount = 0;
        this.owaspTestsGenerated = 0;
    }
    
    public void addVulnerability(SecurityAnalysisResult vulnerability) {
        vulnerabilities.add(vulnerability);
        llmAnalysisCount++;
    }
    
    public void addTest(SecurityTest test) {
        tests.add(test);
        owaspTestsGenerated++;
    }
    
    public void setBpmnSteps(List<BpmnProcessStep> bpmnSteps) {
        this.bpmnSteps = bpmnSteps;
    }
    
    public void setDiscoveredEndpoints(List<String> discoveredEndpoints) {
        this.discoveredEndpoints = discoveredEndpoints;
    }
    
    public void setFinalReport(Map<String, Object> finalReport) {
        this.finalReport = finalReport;
    }
    
    public void setStatus(String status) {
        this.status = status;
        if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
            this.endTime = LocalDateTime.now();
            this.totalExecutionTime = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }
    
    public void setError(String error) {
        this.error = error;
        this.status = "FAILED";
        this.endTime = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getStatus() { return status; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getError() { return error; }
    public List<SecurityAnalysisResult> getVulnerabilities() { return vulnerabilities; }
    public List<SecurityTest> getTests() { return tests; }
    public List<BpmnProcessStep> getBpmnSteps() { return bpmnSteps; }
    public List<String> getDiscoveredEndpoints() { return discoveredEndpoints; }
    public Map<String, Object> getFinalReport() { return finalReport; }
    public long getTotalExecutionTime() { return totalExecutionTime; }
    public int getLlmAnalysisCount() { return llmAnalysisCount; }
    public int getOwaspTestsGenerated() { return owaspTestsGenerated; }
    
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("sessionId", id);
        summary.put("status", status);
        summary.put("totalVulnerabilities", vulnerabilities.size());
        summary.put("totalTests", tests.size());
        summary.put("discoveredEndpoints", discoveredEndpoints.size());
        summary.put("executionTime", totalExecutionTime);
        summary.put("llmAnalysisCount", llmAnalysisCount);
        summary.put("owaspTestsGenerated", owaspTestsGenerated);
        
        if (error != null) {
            summary.put("error", error);
        }
        
        return summary;
    }
    
    @Override
    public String toString() {
        return "SecurityAnalysisSession{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", vulnerabilities=" + vulnerabilities.size() +
                ", tests=" + tests.size() +
                ", executionTime=" + totalExecutionTime + "ms" +
                '}';
    }
}