package org.example.infrastructure.services.reporting;

import org.example.domain.dto.reports.*;
import org.example.domain.entities.*;
import org.example.domain.valueobjects.BpmnProcessId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating visualization data for frontend dashboards
 */
@Service
public class VisualizationService {
    
    @Autowired
    private ReportingService reportingService;
    
    /**
     * Generate Process Flow Visualization data
     */
    public VisualizationData generateProcessFlowVisualization(BpmnProcessId diagramId) {
        VisualizationData data = new VisualizationData("process-flow", "Process Flow Visualization");
        
        try {
            VisualizationData.ProcessFlowVisualization flowData = new VisualizationData.ProcessFlowVisualization();
            
            // Generate mock process nodes
            List<VisualizationData.ProcessNode> nodes = Arrays.asList(
                createProcessNode("start_1", "Process Start", "startEvent"),
                createProcessNode("validate_1", "Validate Input", "serviceTask"),
                createProcessNode("decision_1", "Decision Point", "exclusiveGateway"),
                createProcessNode("approve_1", "Approve Request", "userTask"),
                createProcessNode("end_1", "Process End", "endEvent")
            );
            flowData.setNodes(nodes);
            
            // Generate mock process edges
            List<VisualizationData.ProcessEdge> edges = Arrays.asList(
                createProcessEdge("start_1", "validate_1", "Start Process"),
                createProcessEdge("validate_1", "decision_1", "Validated"),
                createProcessEdge("decision_1", "approve_1", "Approved"),
                createProcessEdge("decision_1", "end_1", "Rejected"),
                createProcessEdge("approve_1", "end_1", "Completed")
            );
            flowData.setEdges(edges);
            
            // Add metrics
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalNodes", nodes.size());
            metrics.put("totalEdges", edges.size());
            metrics.put("complexity", "Medium");
            metrics.put("estimatedTime", "45 minutes");
            flowData.setMetrics(metrics);
            
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("nodes", nodes);
            dataMap.put("edges", edges);
            dataMap.put("metrics", metrics);
            
            data.setData(dataMap);
            data.setTitle("BPMN Process Flow Analysis");
            data.setDescription("Visual representation of the process flow with all elements and connections");
            
        } catch (Exception e) {
            data.setData(Collections.singletonMap("error", e.getMessage()));
        }
        
        return data;
    }
    
    /**
     * Generate Issue Heat Map data
     */
    public VisualizationData generateIssueHeatMap(BpmnProcessId diagramId) {
        VisualizationData data = new VisualizationData("issue-heatmap", "Issue Heat Map");
        
        try {
            VisualizationData.IssueHeatMap heatMap = new VisualizationData.IssueHeatMap();
            
            // Generate mock heat map cells
            List<VisualizationData.HeatMapCell> cells = Arrays.asList(
                createHeatMapCell("Security", "Authentication", 8, "#FF4444"),
                createHeatMapCell("Security", "Authorization", 5, "#FF8800"),
                createHeatMapCell("Performance", "Response Time", 3, "#FFAA00"),
                createHeatMapCell("Performance", "Throughput", 2, "#88FF00"),
                createHeatMapCell("Compliance", "Data Privacy", 6, "#FF8800"),
                createHeatMapCell("Compliance", "Audit Trail", 4, "#FFAA00"),
                createHeatMapCell("Integration", "API Compatibility", 7, "#FF4444"),
                createHeatMapCell("Integration", "Error Handling", 3, "#FFAA00")
            );
            heatMap.setCells(cells);
            
            // Set intensity levels
            Map<String, Integer> intensity = new HashMap<>();
            intensity.put("Critical", 8);
            intensity.put("High", 6);
            intensity.put("Medium", 4);
            intensity.put("Low", 2);
            intensity.put("None", 0);
            heatMap.setIntensity(intensity);
            
            // Set categories
            List<String> categories = Arrays.asList("Security", "Performance", "Compliance", "Integration");
            heatMap.setCategories(categories);
            
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("cells", cells);
            dataMap.put("intensity", intensity);
            dataMap.put("categories", categories);
            
            data.setData(dataMap);
            data.setTitle("Issue Distribution Heat Map");
            data.setDescription("Heat map showing the distribution and severity of issues across different categories");
            
        } catch (Exception e) {
            data.setData(Collections.singletonMap("error", e.getMessage()));
        }
        
        return data;
    }
    
    /**
     * Generate API Process Mapping Chart data
     */
    public VisualizationData generateApiProcessMappingChart(BpmnProcessId diagramId) {
        VisualizationData data = new VisualizationData("api-process-mapping", "API Process Mapping Chart");
        
        try {
            Map<String, Object> chartData = new HashMap<>();
            
            // Mock API-process mapping data
            List<Map<String, Object>> mappings = Arrays.asList(
                createMapping("Validate Input", "POST", "/api/validate", "High", 95.0),
                createMapping("Get Status", "GET", "/api/status", "Medium", 88.5),
                createMapping("Approve Request", "PUT", "/api/approve", "Critical", 92.3),
                createMapping("Send Notification", "POST", "/api/notify", "Low", 96.7),
                createMapping("Log Activity", "POST", "/api/log", "Medium", 89.1)
            );
            
            chartData.put("mappings", mappings);
            chartData.put("totalMappings", mappings.size());
            chartData.put("averageCompliance", 92.3);
            
            data.setData(chartData);
            data.setTitle("API-Process Mapping Analysis");
            data.setDescription("Chart showing the mapping between BPMN process elements and API endpoints");
            
        } catch (Exception e) {
            data.setData(Collections.singletonMap("error", e.getMessage()));
        }
        
        return data;
    }
    
    /**
     * Generate Security Risk Matrix data
     */
    public VisualizationData generateSecurityRiskMatrix(BpmnProcessId diagramId) {
        VisualizationData data = new VisualizationData("security-risk-matrix", "Security Risk Matrix");
        
        try {
            VisualizationData.SecurityRiskMatrix riskMatrix = new VisualizationData.SecurityRiskMatrix();
            
            // Generate mock risk items
            List<VisualizationData.RiskItem> risks = Arrays.asList(
                createRiskItem("AUTH_001", "Missing Authentication", 8, 7, "CRITICAL"),
                createRiskItem("AUTH_002", "Weak Password Policy", 6, 5, "HIGH"),
                createRiskItem("DATA_001", "Unencrypted Data Storage", 9, 8, "CRITICAL"),
                createRiskItem("DATA_002", "Insufficient Data Validation", 7, 6, "HIGH"),
                createRiskItem("NET_001", "Insecure Network Communication", 5, 8, "HIGH"),
                createRiskItem("LOG_001", "Insufficient Logging", 3, 4, "MEDIUM"),
                createRiskItem("BACKUP_001", "Missing Backup Strategy", 4, 7, "MEDIUM")
            );
            riskMatrix.setRisks(risks);
            
            // Set risk levels
            Map<String, String> riskLevels = new HashMap<>();
            riskLevels.put("CRITICAL", "Immediate action required");
            riskLevels.put("HIGH", "Action required within 30 days");
            riskLevels.put("MEDIUM", "Action required within 90 days");
            riskLevels.put("LOW", "Monitor and review quarterly");
            riskMatrix.setRiskLevels(riskLevels);
            
            // Set severity levels
            List<String> severityLevels = Arrays.asList("LOW", "MEDIUM", "HIGH", "CRITICAL");
            riskMatrix.setSeverityLevels(severityLevels);
            
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("risks", risks);
            dataMap.put("riskLevels", riskLevels);
            dataMap.put("severityLevels", severityLevels);
            
            data.setData(dataMap);
            data.setTitle("Security Risk Assessment Matrix");
            data.setDescription("Matrix showing security risks by probability and impact levels");
            
        } catch (Exception e) {
            data.setData(Collections.singletonMap("error", e.getMessage()));
        }
        
        return data;
    }
    
    /**
     * Generate Performance Metrics Chart data
     */
    public VisualizationData generatePerformanceMetricsChart(BpmnProcessId diagramId) {
        VisualizationData data = new VisualizationData("performance-metrics", "Performance Metrics Chart");
        
        try {
            Map<String, Object> chartData = new HashMap<>();
            
            // Mock performance data over time
            List<Map<String, Object>> timeSeriesData = Arrays.asList(
                createTimePoint("2024-01", 450.0, 95.2, 0.8),
                createTimePoint("2024-02", 420.0, 96.1, 0.6),
                createTimePoint("2024-03", 380.0, 97.3, 0.4),
                createTimePoint("2024-04", 390.0, 96.8, 0.5),
                createTimePoint("2024-05", 370.0, 98.1, 0.3),
                createTimePoint("2024-06", 360.0, 98.5, 0.2)
            );
            
            chartData.put("timeSeries", timeSeriesData);
            chartData.put("averageResponseTime", 395.0);
            chartData.put("averageSuccessRate", 97.0);
            chartData.put("averageErrorRate", 0.47);
            
            data.setData(chartData);
            data.setTitle("Performance Metrics Over Time");
            data.setDescription("Chart showing performance trends including response time, success rate, and error rate");
            
        } catch (Exception e) {
            data.setData(Collections.singletonMap("error", e.getMessage()));
        }
        
        return data;
    }
    
    /**
     * Generate Dashboard Metrics for a BPMN diagram
     */
    public DashboardMetrics generateDashboardMetrics(BpmnProcessId diagramId) {
        DashboardMetrics metrics = new DashboardMetrics(diagramId.getValue());
        
        try {
            // Process Metrics
            DashboardMetrics.ProcessMetrics processMetrics = new DashboardMetrics.ProcessMetrics();
            processMetrics.setTotalProcesses(1);
            processMetrics.setActiveProcesses(1);
            processMetrics.setCompletedProcesses(0);
            processMetrics.setFailedProcesses(0);
            processMetrics.setAverageCompletionTime(450.0);
            processMetrics.setProcessDistribution(createProcessDistribution());
            processMetrics.setProcessTypes(Arrays.asList("Service Process", "User Task Process"));
            metrics.setProcessMetrics(processMetrics);
            
            // Security Metrics
            DashboardMetrics.SecurityMetrics securityMetrics = new DashboardMetrics.SecurityMetrics();
            securityMetrics.setTotalSecurityChecks(15);
            securityMetrics.setPassedChecks(12);
            securityMetrics.setFailedChecks(3);
            securityMetrics.setCriticalIssues(1);
            securityMetrics.setHighPriorityIssues(2);
            securityMetrics.setMediumPriorityIssues(3);
            securityMetrics.setLowPriorityIssues(2);
            securityMetrics.setSecurityScore(78.5);
            securityMetrics.setVulnerabilityTypes(createVulnerabilityTypes());
            metrics.setSecurityMetrics(securityMetrics);
            
            // Compliance Metrics
            DashboardMetrics.ComplianceMetrics complianceMetrics = new DashboardMetrics.ComplianceMetrics();
            complianceMetrics.setOverallComplianceScore(86.5);
            complianceMetrics.setComplianceByStandard(createComplianceByStandard());
            complianceMetrics.setTotalComplianceChecks(20);
            complianceMetrics.setPassedComplianceChecks(17);
            complianceMetrics.setFailedComplianceChecks(3);
            complianceMetrics.setComplianceStandards(Arrays.asList("BPMN 2.0", "ISO 9001", "SOX"));
            complianceMetrics.setComplianceGaps(createComplianceGaps());
            metrics.setComplianceMetrics(complianceMetrics);
            
            // Performance Metrics
            DashboardMetrics.PerformanceMetricsData performanceMetrics = new DashboardMetrics.PerformanceMetricsData();
            performanceMetrics.setAverageResponseTime(450.0);
            performanceMetrics.setPeakResponseTime(1200.0);
            performanceMetrics.setTotalRequests(15420);
            performanceMetrics.setThroughput(85.7);
            performanceMetrics.setErrorRate(0.8);
            performanceMetrics.setResourceUtilization(createResourceUtilization());
            performanceMetrics.setPerformanceTrends(Arrays.asList("Improving", "Stable", "Monitoring"));
            metrics.setPerformanceMetrics(performanceMetrics);
            
            // Summary
            Map<String, Object> summary = new HashMap<>();
            summary.put("overallHealth", "GOOD");
            summary.put("lastAnalysisDate", java.time.LocalDateTime.now().toString());
            summary.put("totalIssues", 8);
            summary.put("criticalIssues", 1);
            summary.put("recommendationCount", 5);
            metrics.setSummary(summary);
            
        } catch (Exception e) {
            metrics.getSummary().put("error", e.getMessage());
        }
        
        return metrics;
    }
    
    // Helper methods
    
    private VisualizationData.ProcessNode createProcessNode(String id, String label, String type) {
        VisualizationData.ProcessNode node = new VisualizationData.ProcessNode();
        node.setId(id);
        node.setLabel(label);
        node.setType(type);
        node.setProperties(createNodeProperties(type));
        return node;
    }
    
    private VisualizationData.ProcessEdge createProcessEdge(String from, String to, String label) {
        VisualizationData.ProcessEdge edge = new VisualizationData.ProcessEdge();
        edge.setFrom(from);
        edge.setTo(to);
        edge.setLabel(label);
        edge.setProperties(createEdgeProperties());
        return edge;
    }
    
    private VisualizationData.HeatMapCell createHeatMapCell(String category, String subcategory, int value, String color) {
        VisualizationData.HeatMapCell cell = new VisualizationData.HeatMapCell();
        cell.setCategory(category);
        cell.setSubcategory(subcategory);
        cell.setValue(value);
        cell.setColor(color);
        return cell;
    }
    
    private VisualizationData.RiskItem createRiskItem(String id, String description, int probability, int impact, String level) {
        VisualizationData.RiskItem risk = new VisualizationData.RiskItem();
        risk.setId(id);
        risk.setDescription(description);
        risk.setProbability(probability);
        risk.setImpact(impact);
        risk.setLevel(level);
        return risk;
    }
    
    private Map<String, Object> createMapping(String processElement, String httpMethod, String apiPath, String priority, Double compliance) {
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("processElement", processElement);
        mapping.put("httpMethod", httpMethod);
        mapping.put("apiPath", apiPath);
        mapping.put("priority", priority);
        mapping.put("complianceScore", compliance);
        return mapping;
    }
    
    private Map<String, Object> createTimePoint(String period, Double responseTime, Double successRate, Double errorRate) {
        Map<String, Object> point = new HashMap<>();
        point.put("period", period);
        point.put("responseTime", responseTime);
        point.put("successRate", successRate);
        point.put("errorRate", errorRate);
        return point;
    }
    
    private Map<String, Object> createNodeProperties(String type) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("icon", getIconForType(type));
        properties.put("color", getColorForType(type));
        return properties;
    }
    
    private Map<String, Object> createEdgeProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("type", "sequenceFlow");
        properties.put("style", "solid");
        return properties;
    }
    
    private Map<String, Integer> createProcessDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("Service Task", 3);
        distribution.put("User Task", 2);
        distribution.put("Gateway", 1);
        distribution.put("Start Event", 1);
        distribution.put("End Event", 1);
        return distribution;
    }
    
    private Map<String, String> createVulnerabilityTypes() {
        Map<String, String> vulnerabilities = new HashMap<>();
        vulnerabilities.put("Authentication", "HIGH");
        vulnerabilities.put("Authorization", "MEDIUM");
        vulnerabilities.put("Data Validation", "HIGH");
        vulnerabilities.put("Input Sanitization", "LOW");
        return vulnerabilities;
    }
    
    private Map<String, Double> createComplianceByStandard() {
        Map<String, Double> compliance = new HashMap<>();
        compliance.put("BPMN 2.0", 92.0);
        compliance.put("ISO 9001", 88.5);
        compliance.put("SOX", 85.0);
        return compliance;
    }
    
    private Map<String, Object> createComplianceGaps() {
        Map<String, Object> gaps = new HashMap<>();
        gaps.put("Missing Documentation", 2);
        gaps.put("Incomplete Testing", 1);
        gaps.put("Outdated Standards", 1);
        return gaps;
    }
    
    private Map<String, Object> createResourceUtilization() {
        Map<String, Object> utilization = new HashMap<>();
        utilization.put("CPU", 65.0);
        utilization.put("Memory", 72.0);
        utilization.put("Disk", 45.0);
        utilization.put("Network", 38.0);
        return utilization;
    }
    
    private String getIconForType(String type) {
        switch (type) {
            case "startEvent": return "play-circle";
            case "endEvent": return "stop-circle";
            case "serviceTask": return "cog";
            case "userTask": return "user";
            case "exclusiveGateway": return "git-branch";
            default: return "circle";
        }
    }
    
    private String getColorForType(String type) {
        switch (type) {
            case "startEvent": return "#28a745";
            case "endEvent": return "#dc3545";
            case "serviceTask": return "#007bff";
            case "userTask": return "#6f42c1";
            case "exclusiveGateway": return "#fd7e14";
            default: return "#6c757d";
        }
    }
}