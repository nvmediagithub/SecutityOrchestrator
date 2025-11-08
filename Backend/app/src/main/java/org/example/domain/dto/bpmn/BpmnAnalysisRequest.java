package org.example.domain.dto.bpmn;

/**
 * Request DTO for BPMN analysis
 */
public class BpmnAnalysisRequest {
    private String processName;
    private String processContent;
    private String processDescription;
    private boolean includeSecurityChecks;
    private boolean includeApiMapping;
    private boolean includeComplianceCheck;

    public BpmnAnalysisRequest() {}

    public BpmnAnalysisRequest(String processName, String processContent, String processDescription,
                              boolean includeSecurityChecks, boolean includeApiMapping, boolean includeComplianceCheck) {
        this.processName = processName;
        this.processContent = processContent;
        this.processDescription = processDescription;
        this.includeSecurityChecks = includeSecurityChecks;
        this.includeApiMapping = includeApiMapping;
        this.includeComplianceCheck = includeComplianceCheck;
    }

    // Getters and setters
    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }

    public String getProcessContent() { return processContent; }
    public void setProcessContent(String processContent) { this.processContent = processContent; }

    public String getProcessDescription() { return processDescription; }
    public void setProcessDescription(String processDescription) { this.processDescription = processDescription; }

    public boolean isIncludeSecurityChecks() { return includeSecurityChecks; }
    public void setIncludeSecurityChecks(boolean includeSecurityChecks) { this.includeSecurityChecks = includeSecurityChecks; }

    public boolean isIncludeApiMapping() { return includeApiMapping; }
    public void setIncludeApiMapping(boolean includeApiMapping) { this.includeApiMapping = includeApiMapping; }

    public boolean isIncludeComplianceCheck() { return includeComplianceCheck; }
    public void setIncludeComplianceCheck(boolean includeComplianceCheck) { this.includeComplianceCheck = includeComplianceCheck; }

    // Validation methods
    public boolean isValid() {
        return processName != null && !processName.trim().isEmpty() &&
               processContent != null && !processContent.trim().isEmpty();
    }
}