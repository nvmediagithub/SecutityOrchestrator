package org.example.domain.dto.test;

import org.example.domain.entities.BpmnDiagram;

import java.time.LocalDateTime;
import java.util.List;

public class BpmnDiagramResponse {
    
    private Long id;
    private String diagramId;
    private String name;
    private String description;
    private String bpmnContent;
    private String processDefinitionId;
    private String version;
    private String diagramType;
    private List<String> elements;
    private List<String> flows;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String processEngine;
    private String targetNamespace;
    private Boolean executable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private String tags;
    private List<String> tagList;
    
    public BpmnDiagramResponse() {
    }
    
    public BpmnDiagramResponse(BpmnDiagram bpmnDiagram) {
        if (bpmnDiagram != null) {
            this.id = bpmnDiagram.getId();
            this.diagramId = bpmnDiagram.getDiagramId();
            this.name = bpmnDiagram.getName();
            this.description = bpmnDiagram.getDescription();
            this.bpmnContent = bpmnDiagram.getBpmnContent();
            this.processDefinitionId = bpmnDiagram.getProcessDefinitionId();
            this.version = bpmnDiagram.getVersion();
            this.diagramType = bpmnDiagram.getDiagramType();
            this.elements = bpmnDiagram.getElements();
            this.flows = bpmnDiagram.getFlows();
            this.fileName = bpmnDiagram.getFileName();
            this.fileSize = bpmnDiagram.getFileSize();
            this.contentType = bpmnDiagram.getContentType();
            this.processEngine = bpmnDiagram.getProcessEngine();
            this.targetNamespace = bpmnDiagram.getTargetNamespace();
            this.executable = bpmnDiagram.getExecutable();
            this.createdAt = bpmnDiagram.getCreatedAt();
            this.updatedAt = bpmnDiagram.getUpdatedAt();
            this.isActive = bpmnDiagram.getIsActive();
            this.tags = bpmnDiagram.getTags();
            this.tagList = bpmnDiagram.getTagList();
        }
    }
    
    public static BpmnDiagramResponse from(BpmnDiagram bpmnDiagram) {
        return new BpmnDiagramResponse(bpmnDiagram);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getBpmnContent() { return bpmnContent; }
    public void setBpmnContent(String bpmnContent) { this.bpmnContent = bpmnContent; }
    
    public String getProcessDefinitionId() { return processDefinitionId; }
    public void setProcessDefinitionId(String processDefinitionId) { this.processDefinitionId = processDefinitionId; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDiagramType() { return diagramType; }
    public void setDiagramType(String diagramType) { this.diagramType = diagramType; }
    
    public List<String> getElements() { return elements; }
    public void setElements(List<String> elements) { this.elements = elements; }
    
    public List<String> getFlows() { return flows; }
    public void setFlows(List<String> flows) { this.flows = flows; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getProcessEngine() { return processEngine; }
    public void setProcessEngine(String processEngine) { this.processEngine = processEngine; }
    
    public String getTargetNamespace() { return targetNamespace; }
    public void setTargetNamespace(String targetNamespace) { this.targetNamespace = targetNamespace; }
    
    public Boolean getExecutable() { return executable; }
    public void setExecutable(Boolean executable) { this.executable = executable; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public List<String> getTagList() { return tagList; }
    public void setTagList(List<String> tagList) { this.tagList = tagList; }
    
    @Override
    public String toString() {
        return "BpmnDiagramResponse{" +
                "id=" + id +
                ", diagramId='" + diagramId + '\'' +
                ", name='" + name + '\'' +
                ", processDefinitionId='" + processDefinitionId + '\'' +
                ", version='" + version + '\'' +
                ", executable=" + executable +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}