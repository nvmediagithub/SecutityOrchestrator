package org.example.domain.dto.context;

import java.util.List;
import java.util.Map;

/**
 * DTO for parameter analysis results
 */
public class ParameterAnalysis {
    private String analysisId;
    private String endpointId;
    private List<ParameterInfo> parameters;
    private Map<String, Object> validationResults;
    private String summary;
    private Integer totalParameters;
    private Integer requiredParameters;
    private Integer optionalParameters;
    private List<String> validationErrors;
    private List<String> suggestions;

    public ParameterAnalysis() {}

    public ParameterAnalysis(String analysisId, String endpointId) {
        this.analysisId = analysisId;
        this.endpointId = endpointId;
    }

    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }

    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String endpointId) { this.endpointId = endpointId; }

    public List<ParameterInfo> getParameters() { return parameters; }
    public void setParameters(List<ParameterInfo> parameters) { this.parameters = parameters; }

    public Map<String, Object> getValidationResults() { return validationResults; }
    public void setValidationResults(Map<String, Object> validationResults) { this.validationResults = validationResults; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getTotalParameters() { return totalParameters; }
    public void setTotalParameters(Integer totalParameters) { this.totalParameters = totalParameters; }

    public Integer getRequiredParameters() { return requiredParameters; }
    public void setRequiredParameters(Integer requiredParameters) { this.requiredParameters = requiredParameters; }

    public Integer getOptionalParameters() { return optionalParameters; }
    public void setOptionalParameters(Integer optionalParameters) { this.optionalParameters = optionalParameters; }

    public List<String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(List<String> validationErrors) { this.validationErrors = validationErrors; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public static class ParameterInfo {
        private String name;
        private String type;
        private Boolean required;
        private String description;
        private String location; // path, query, header, body
        private List<String> validationRules;
        private String defaultValue;

        public ParameterInfo() {}

        public ParameterInfo(String name, String type, Boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Boolean getRequired() { return required; }
        public void setRequired(Boolean required) { this.required = required; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public List<String> getValidationRules() { return validationRules; }
        public void setValidationRules(List<String> validationRules) { this.validationRules = validationRules; }

        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    }
}