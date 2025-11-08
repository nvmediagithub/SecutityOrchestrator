package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Кросс-системный контекст
 */
public class CrossSystemContext {
    
    private String analysisId;
    private LocalDateTime analyzedAt;
    private List<BpmnApiConnection> bpmnApiConnections;
    private List<EndToEndScenario> endToEndScenarios;
    private List<UserJourney> userJourneys;
    private List<DataLineageMapping> dataLineage;
    
    public CrossSystemContext() {
        this.analysisId = "cross_ctx_" + System.currentTimeMillis();
        this.analyzedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public List<BpmnApiConnection> getBpmnApiConnections() { return bpmnApiConnections; }
    public void setBpmnApiConnections(List<BpmnApiConnection> bpmnApiConnections) { this.bpmnApiConnections = bpmnApiConnections; }
    
    public List<EndToEndScenario> getEndToEndScenarios() { return endToEndScenarios; }
    public void setEndToEndScenarios(List<EndToEndScenario> endToEndScenarios) { this.endToEndScenarios = endToEndScenarios; }
    
    public List<UserJourney> getUserJourneys() { return userJourneys; }
    public void setUserJourneys(List<UserJourney> userJourneys) { this.userJourneys = userJourneys; }
    
    public List<DataLineageMapping> getDataLineage() { return dataLineage; }
    public void setDataLineage(List<DataLineageMapping> dataLineage) { this.dataLineage = dataLineage; }
}