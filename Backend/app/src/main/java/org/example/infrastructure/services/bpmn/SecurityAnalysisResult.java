package org.example.infrastructure.services.bpmn;

public class SecurityAnalysisResult {
    private int criticalIssues = 0;
    private int highIssues = 0;
    private int mediumIssues = 0;
    private int lowIssues = 0;
    private double overallRiskScore = 0.0;
    private String riskLevel = "LOW";
    
    public SecurityAnalysisResult() {}
    public int getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
    public int getHighIssues() { return highIssues; }
    public void setHighIssues(int highIssues) { this.highIssues = highIssues; }
    public int getMediumIssues() { return mediumIssues; }
    public void setMediumIssues(int mediumIssues) { this.mediumIssues = mediumIssues; }
    public int getLowIssues() { return lowIssues; }
    public void setLowIssues(int lowIssues) { this.lowIssues = lowIssues; }
    public double getOverallRiskScore() { return overallRiskScore; }
    public void setOverallRiskScore(double overallRiskScore) { this.overallRiskScore = overallRiskScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}
