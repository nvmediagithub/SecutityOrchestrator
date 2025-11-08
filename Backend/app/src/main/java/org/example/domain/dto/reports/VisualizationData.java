package org.example.domain.dto.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for visualization data used in frontend dashboards
 */
public class VisualizationData {
    
    @JsonProperty
    private UUID id;
    
    @JsonProperty
    private String type;
    
    @JsonProperty
    private Map<String, Object> data;
    
    @JsonProperty
    private Map<String, Object> configuration;
    
    @JsonProperty
    private List<String> labels;
    
    @JsonProperty
    private List<String> colors;
    
    @JsonProperty
    private String title;
    
    @JsonProperty
    private String description;
    
    @JsonProperty
    private String generatedAt;
    
    public VisualizationData() {}
    
    public VisualizationData(String type, String title) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.title = title;
        this.generatedAt = java.time.LocalDateTime.now().toString();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public Map<String, Object> getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }
    
    public List<String> getLabels() {
        return labels;
    }
    
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    
    public List<String> getColors() {
        return colors;
    }
    
    public void setColors(List<String> colors) {
        this.colors = colors;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    /**
     * Process Flow Visualization data structure
     */
    public static class ProcessFlowVisualization {
        @JsonProperty
        private List<ProcessNode> nodes;
        
        @JsonProperty
        private List<ProcessEdge> edges;
        
        @JsonProperty
        private Map<String, Object> metrics;
        
        // Getters and Setters
        public List<ProcessNode> getNodes() {
            return nodes;
        }
        
        public void setNodes(List<ProcessNode> nodes) {
            this.nodes = nodes;
        }
        
        public List<ProcessEdge> getEdges() {
            return edges;
        }
        
        public void setEdges(List<ProcessEdge> edges) {
            this.edges = edges;
        }
        
        public Map<String, Object> getMetrics() {
            return metrics;
        }
        
        public void setMetrics(Map<String, Object> metrics) {
            this.metrics = metrics;
        }
    }
    
    /**
     * Issue Heat Map visualization data
     */
    public static class IssueHeatMap {
        @JsonProperty
        private List<HeatMapCell> cells;
        
        @JsonProperty
        private Map<String, Integer> intensity;
        
        @JsonProperty
        private List<String> categories;
        
        // Getters and Setters
        public List<HeatMapCell> getCells() {
            return cells;
        }
        
        public void setCells(List<HeatMapCell> cells) {
            this.cells = cells;
        }
        
        public Map<String, Integer> getIntensity() {
            return intensity;
        }
        
        public void setIntensity(Map<String, Integer> intensity) {
            this.intensity = intensity;
        }
        
        public List<String> getCategories() {
            return categories;
        }
        
        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
    }
    
    /**
     * Security Risk Matrix data
     */
    public static class SecurityRiskMatrix {
        @JsonProperty
        private List<RiskItem> risks;
        
        @JsonProperty
        private Map<String, String> riskLevels;
        
        @JsonProperty
        private List<String> severityLevels;
        
        // Getters and Setters
        public List<RiskItem> getRisks() {
            return risks;
        }
        
        public void setRisks(List<RiskItem> risks) {
            this.risks = risks;
        }
        
        public Map<String, String> getRiskLevels() {
            return riskLevels;
        }
        
        public void setRiskLevels(Map<String, String> riskLevels) {
            this.riskLevels = riskLevels;
        }
        
        public List<String> getSeverityLevels() {
            return severityLevels;
        }
        
        public void setSeverityLevels(List<String> severityLevels) {
            this.severityLevels = severityLevels;
        }
    }
    
    /**
     * Helper classes for visualizations
     */
    public static class ProcessNode {
        @JsonProperty
        private String id;
        
        @JsonProperty
        private String label;
        
        @JsonProperty
        private String type;
        
        @JsonProperty
        private Map<String, Object> properties;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Map<String, Object> getProperties() {
            return properties;
        }
        
        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }
    }
    
    public static class ProcessEdge {
        @JsonProperty
        private String from;
        
        @JsonProperty
        private String to;
        
        @JsonProperty
        private String label;
        
        @JsonProperty
        private Map<String, Object> properties;
        
        // Getters and Setters
        public String getFrom() {
            return from;
        }
        
        public void setFrom(String from) {
            this.from = from;
        }
        
        public String getTo() {
            return to;
        }
        
        public void setTo(String to) {
            this.to = to;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
        
        public Map<String, Object> getProperties() {
            return properties;
        }
        
        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }
    }
    
    public static class HeatMapCell {
        @JsonProperty
        private String category;
        
        @JsonProperty
        private String subcategory;
        
        @JsonProperty
        private int value;
        
        @JsonProperty
        private String color;
        
        // Getters and Setters
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getSubcategory() {
            return subcategory;
        }
        
        public void setSubcategory(String subcategory) {
            this.subcategory = subcategory;
        }
        
        public int getValue() {
            return value;
        }
        
        public void setValue(int value) {
            this.value = value;
        }
        
        public String getColor() {
            return color;
        }
        
        public void setColor(String color) {
            this.color = color;
        }
    }
    
    public static class RiskItem {
        @JsonProperty
        private String id;
        
        @JsonProperty
        private String description;
        
        @JsonProperty
        private int probability;
        
        @JsonProperty
        private int impact;
        
        @JsonProperty
        private String level;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public int getProbability() {
            return probability;
        }
        
        public void setProbability(int probability) {
            this.probability = probability;
        }
        
        public int getImpact() {
            return impact;
        }
        
        public void setImpact(int impact) {
            this.impact = impact;
        }
        
        public String getLevel() {
            return level;
        }
        
        public void setLevel(String level) {
            this.level = level;
        }
    }
}