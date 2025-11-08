package org.example.domain.dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for uploading BPMN diagrams
 */
public class BpmnDiagramUploadRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "BPMN content is required")
    private String bpmnContent;
    
    private String fileName;
    private String version;
    private String diagramType;
    private Long fileSize;
    private String contentType;
    private String uploadedBy;
    private String projectId; // optional: associate with a project immediately
    private String processDefinitionId;
    private String processEngine;
    private String targetNamespace;
    private Boolean executable = false;
    
    // Constructors
    public BpmnDiagramUploadRequest() {
    }
    
    public BpmnDiagramUploadRequest(String name, String bpmnContent) {
        this.name = name;
        this.bpmnContent = bpmnContent;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getBpmnContent() {
        return bpmnContent;
    }
    
    public void setBpmnContent(String bpmnContent) {
        this.bpmnContent = bpmnContent;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDiagramType() {
        return diagramType;
    }
    
    public void setDiagramType(String diagramType) {
        this.diagramType = diagramType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    
    public String getProcessEngine() {
        return processEngine;
    }
    
    public void setProcessEngine(String processEngine) {
        this.processEngine = processEngine;
    }
    
    public String getTargetNamespace() {
        return targetNamespace;
    }
    
    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }
    
    public Boolean getExecutable() {
        return executable;
    }
    
    public void setExecutable(Boolean executable) {
        this.executable = executable;
    }
    
    @Override
    public String toString() {
        return "BpmnDiagramUploadRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fileName='" + fileName + '\'' +
                ", version='" + version + '\'' +
                ", diagramType='" + diagramType + '\'' +
                ", fileSize=" + fileSize +
                ", contentType='" + contentType + '\'' +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", projectId='" + projectId + '\'' +
                ", processDefinitionId='" + processDefinitionId + '\'' +
                ", processEngine='" + processEngine + '\'' +
                ", targetNamespace='" + targetNamespace + '\'' +
                ", executable=" + executable +
                '}';
    }
}