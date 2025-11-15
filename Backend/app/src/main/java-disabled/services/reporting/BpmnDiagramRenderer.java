package org.example.infrastructure.services.reporting;

import org.example.domain.dto.reports.VisualizationData;
import org.example.domain.entities.BpmnDiagram;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility service for rendering BPMN diagrams and generating flow chart data
 */
@Service
public class BpmnDiagramRenderer {
    
    private static final Pattern BPMN_ELEMENT_PATTERN = Pattern.compile(
        "<(bpmn2:)?(startEvent|endEvent|serviceTask|userTask|exclusiveGateway|parallelGateway|sequenceFlow)\\s+[^>]*(?:id=['\"]([^'\"]+)['\"]|name=['\"]([^'\"]+)['\"])?[^>]*>"
    );
    
    private static final Map<String, String> ELEMENT_COLORS = Map.of(
        "startEvent", "#28a745",
        "endEvent", "#dc3545", 
        "serviceTask", "#007bff",
        "userTask", "#6f42c1",
        "exclusiveGateway", "#fd7e14",
        "parallelGateway", "#20c997",
        "sequenceFlow", "#6c757d"
    );
    
    /**
     * Parse BPMN XML and extract elements for visualization
     */
    public List<BpmnElement> parseBpmnElements(String bpmnXml) {
        List<BpmnElement> elements = new ArrayList<>();
        
        try {
            Matcher matcher = BPMN_ELEMENT_PATTERN.matcher(bpmnXml);
            
            while (matcher.find()) {
                String elementType = matcher.group(2);
                String elementId = matcher.group(3);
                String elementName = matcher.group(4);
                
                if (elementId != null) {
                    BpmnElement element = new BpmnElement();
                    element.setId(elementId);
                    element.setType(elementType);
                    element.setName(elementName != null ? elementName : elementType);
                    element.setColor(ELEMENT_COLORS.getOrDefault(elementType, "#6c757d"));
                    
                    // Calculate position (simplified - in real implementation, parse from XML)
                    element.setX(calculateElementX(elements.size()));
                    element.setY(calculateElementY(elements.size()));
                    
                    elements.add(element);
                }
            }
            
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Failed to parse BPMN elements: " + e.getMessage());
        }
        
        return elements;
    }
    
    /**
     * Generate visualization data from BPMN diagram
     */
    public VisualizationData generateVisualizationData(BpmnDiagram diagram) {
        VisualizationData data = new VisualizationData("bpmn-diagram", "BPMN Diagram Visualization");
        
        try {
            List<BpmnElement> elements = parseBpmnElements(diagram.getBpmnContent());
            List<VisualizationData.ProcessNode> nodes = elements.stream()
                .filter(element -> !element.getType().equals("sequenceFlow"))
                .map(this::convertToProcessNode)
                .toList();
            
            List<VisualizationData.ProcessEdge> edges = elements.stream()
                .filter(element -> element.getType().equals("sequenceFlow"))
                .map(this::convertToProcessEdge)
                .toList();
            
            // Create process flow visualization
            VisualizationData.ProcessFlowVisualization flowData = new VisualizationData.ProcessFlowVisualization();
            flowData.setNodes(nodes);
            flowData.setEdges(edges);
            
            // Add metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("totalElements", elements.size());
            metadata.put("processName", diagram.getName());
            metadata.put("diagramId", diagram.getId());
            metadata.put("complexity", calculateComplexity(elements));
            
            flowData.setMetrics(metadata);
            
            // Set visualization data
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("nodes", nodes);
            dataMap.put("edges", edges);
            dataMap.put("metadata", metadata);
            
            data.setData(dataMap);
            data.setTitle(diagram.getName() + " - Process Flow");
            data.setDescription("Interactive visualization of the BPMN process flow");
            
        } catch (Exception e) {
            data.setData(Collections.singletonMap("error", e.getMessage()));
        }
        
        return data;
    }
    
    /**
     * Get SVG representation of BPMN diagram
     */
    public String generateSvg(BpmnDiagram diagram) {
        List<BpmnElement> elements = parseBpmnElements(diagram.getBpmnContent());
        
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"800\" height=\"600\">");
        svg.append("<rect width=\"100%\" height=\"100%\" fill=\"#f8f9fa\"/>");
        
        // Draw elements
        for (BpmnElement element : elements) {
            if (!element.getType().equals("sequenceFlow")) {
                svg.append(drawElement(element));
            }
        }
        
        // Draw connections
        for (BpmnElement element : elements) {
            if (element.getType().equals("sequenceFlow")) {
                svg.append(drawConnection(element));
            }
        }
        
        svg.append("</svg>");
        return svg.toString();
    }
    
    /**
     * Get element statistics for analysis
     */
    public Map<String, Object> getElementStatistics(BpmnDiagram diagram) {
        List<BpmnElement> elements = parseBpmnElements(diagram.getBpmnContent());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalElements", elements.size());
        
        // Count by type
        Map<String, Long> typeCounts = elements.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                BpmnElement::getType,
                java.util.stream.Collectors.counting()
            ));
        stats.put("typeCounts", typeCounts);
        
        // Calculate complexity score
        stats.put("complexityScore", calculateComplexity(elements));
        
        // Identify potential issues
        List<String> issues = identifyPotentialIssues(elements);
        stats.put("issues", issues);
        
        return stats;
    }
    
    // Private helper methods
    
    private int calculateElementX(int index) {
        return 100 + (index % 3) * 200;
    }
    
    private int calculateElementY(int index) {
        return 100 + (index / 3) * 150;
    }
    
    private VisualizationData.ProcessNode convertToProcessNode(BpmnElement element) {
        VisualizationData.ProcessNode node = new VisualizationData.ProcessNode();
        node.setId(element.getId());
        node.setLabel(element.getName());
        node.setType(element.getType());
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("color", element.getColor());
        properties.put("x", element.getX());
        properties.put("y", element.getY());
        node.setProperties(properties);
        
        return node;
    }
    
    private VisualizationData.ProcessEdge convertToProcessEdge(BpmnElement element) {
        VisualizationData.ProcessEdge edge = new VisualizationData.ProcessEdge();
        edge.setFrom(element.getSourceRef());
        edge.setTo(element.getTargetRef());
        edge.setLabel(element.getName());
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("color", element.getColor());
        properties.put("type", "sequenceFlow");
        edge.setProperties(properties);
        
        return edge;
    }
    
    private String calculateComplexity(List<BpmnElement> elements) {
        int totalElements = elements.size();
        long gateways = elements.stream()
            .filter(e -> e.getType().contains("Gateway"))
            .count();
        long tasks = elements.stream()
            .filter(e -> e.getType().contains("Task"))
            .count();
        
        double complexityScore = (totalElements * 0.5) + (gateways * 2.0) + (tasks * 1.0);
        
        if (complexityScore < 10) return "Low";
        if (complexityScore < 25) return "Medium";
        return "High";
    }
    
    private String drawElement(BpmnElement element) {
        StringBuilder svg = new StringBuilder();
        String color = element.getColor();
        
        switch (element.getType()) {
            case "startEvent":
                svg.append(String.format(
                    "<circle cx=\"%d\" cy=\"%d\" r=\"20\" fill=\"%s\" stroke=\"#333\" stroke-width=\"2\"/>",
                    element.getX(), element.getY(), color
                ));
                break;
            case "endEvent":
                svg.append(String.format(
                    "<circle cx=\"%d\" cy=\"%d\" r=\"20\" fill=\"%s\" stroke=\"#333\" stroke-width=\"3\"/>",
                    element.getX(), element.getY(), color
                ));
                break;
            case "serviceTask":
            case "userTask":
                svg.append(String.format(
                    "<rect x=\"%d\" y=\"%d\" width=\"100\" height=\"50\" rx=\"5\" fill=\"%s\" stroke=\"#333\" stroke-width=\"2\"/>",
                    element.getX() - 50, element.getY() - 25, color
                ));
                break;
            case "exclusiveGateway":
            case "parallelGateway":
                svg.append(String.format(
                    "<polygon points=\"%d,%d %d,%d %d,%d %d,%d\" fill=\"%s\" stroke=\"#333\" stroke-width=\"2\"/>",
                    element.getX(), element.getY() - 30,   // top
                    element.getX() + 30, element.getY(),  // right
                    element.getX(), element.getY() + 30,  // bottom
                    element.getX() - 30, element.getY(),  // left
                    color
                ));
                break;
        }
        
        // Add label
        svg.append(String.format(
            "<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"Arial\" font-size=\"12\">%s</text>",
            element.getX(), element.getY() + 50, element.getName()
        ));
        
        return svg.toString();
    }
    
    private String drawConnection(BpmnElement element) {
        // Simplified line drawing
        return String.format(
            "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"%s\" stroke-width=\"2\" marker-end=\"url(#arrowhead)\"/>",
            element.getX(), element.getY(), element.getTargetX(), element.getTargetY(), element.getColor()
        );
    }
    
    private List<String> identifyPotentialIssues(List<BpmnElement> elements) {
        List<String> issues = new ArrayList<>();
        
        // Check for disconnected elements
        long startEvents = elements.stream()
            .filter(e -> e.getType().equals("startEvent"))
            .count();
        long endEvents = elements.stream()
            .filter(e -> e.getType().equals("endEvent"))
            .count();
        
        if (startEvents == 0) {
            issues.add("No start event found");
        }
        if (endEvents == 0) {
            issues.add("No end event found");
        }
        
        // Check for excessive complexity
        long gateways = elements.stream()
            .filter(e -> e.getType().contains("Gateway"))
            .count();
        if (gateways > 5) {
            issues.add("High number of gateways may indicate complex process");
        }
        
        return issues;
    }
    
    /**
     * Inner class representing a BPMN element
     */
    public static class BpmnElement {
        private String id;
        private String type;
        private String name;
        private String color;
        private int x;
        private int y;
        private String sourceRef;
        private String targetRef;
        private int targetX;
        private int targetY;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        
        public String getSourceRef() { return sourceRef; }
        public void setSourceRef(String sourceRef) { this.sourceRef = sourceRef; }
        
        public String getTargetRef() { return targetRef; }
        public void setTargetRef(String targetRef) { this.targetRef = targetRef; }
        
        public int getTargetX() { return targetX; }
        public void setTargetX(int targetX) { this.targetX = targetX; }
        
        public int getTargetY() { return targetY; }
        public void setTargetY(int targetY) { this.targetY = targetY; }
    }
}