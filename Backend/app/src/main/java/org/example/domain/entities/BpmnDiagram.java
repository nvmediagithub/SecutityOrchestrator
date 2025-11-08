package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity for BPMN diagrams
 * Represents a BPMN (Business Process Model and Notation) diagram artifact
 * that can be used for business process testing and validation
 */
@Entity
@Table(name = "bpmn_diagrams")
public class BpmnDiagram {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Diagram ID is required")
    @Column(name = "diagram_id", unique = true, nullable = false)
    private String diagramId;
    
    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @NotBlank(message = "BPMN content is required")
    @Lob
    @Column(name = "bpmn_content", nullable = false, columnDefinition = "TEXT")
    private String bpmnContent;
    
    @Column(name = "process_definition_id")
    private String processDefinitionId;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "diagram_type")
    private String diagramType;
    
    @ElementCollection
    @CollectionTable(name = "bpmn_elements", joinColumns = @JoinColumn(name = "bpmn_diagram_id"))
    @Column(name = "element_data", columnDefinition = "TEXT")
    private List<String> elements = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "bpmn_flows", joinColumns = @JoinColumn(name = "bpmn_diagram_id"))
    @Column(name = "flow_data", columnDefinition = "TEXT")
    private List<String> flows = new ArrayList<>();
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "process_engine")
    private String processEngine;
    
    @Column(name = "target_namespace")
    private String targetNamespace;
    
    @Column(name = "executable", nullable = false)
    private Boolean executable = false;
    
    @NotNull(message = "Creation timestamp is required")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;
    
    // Constructors
    public BpmnDiagram() {
        this.diagramId = generateUniqueId();
        this.createdAt = LocalDateTime.now();
    }
    
    public BpmnDiagram(String name, String bpmnContent) {
        this();
        this.name = name;
        this.bpmnContent = bpmnContent;
    }
    
    public BpmnDiagram(String name, String bpmnContent, String processDefinitionId, String version) {
        this(name, bpmnContent);
        this.processDefinitionId = processDefinitionId;
        this.version = version;
    }
    
    // PrePersist and PreUpdate callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (diagramId == null || diagramId.trim().isEmpty()) {
            diagramId = generateUniqueId();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    private String generateUniqueId() {
        return "bpmn-" + UUID.randomUUID().toString();
    }
    
    public void addElement(String elementData) {
        this.elements.add(elementData);
    }
    
    public void removeElement(String elementData) {
        this.elements.remove(elementData);
    }
    
    public void addFlow(String flowData) {
        this.flows.add(flowData);
    }
    
    public void removeFlow(String flowData) {
        this.flows.remove(flowData);
    }
    
    public void markAsExecutable() {
        this.executable = true;
    }
    
    public void markAsNonExecutable() {
        this.executable = false;
    }
    
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public boolean isActive() {
        return isActive != null && isActive;
    }
    
    public boolean isExecutable() {
        return executable != null && executable;
    }
    
    public void addTag(String tag) {
        if (tags == null || tags.trim().isEmpty()) {
            tags = tag;
        } else {
            String[] existingTags = tags.split(",");
            for (String existingTag : existingTags) {
                if (existingTag.trim().equals(tag.trim())) {
                    return; // Tag already exists
                }
            }
            tags = tags + "," + tag;
        }
    }
    
    public void removeTag(String tag) {
        if (tags != null) {
            String[] existingTags = tags.split(",");
            StringBuilder newTags = new StringBuilder();
            for (int i = 0; i < existingTags.length; i++) {
                if (!existingTags[i].trim().equals(tag.trim())) {
                    if (newTags.length() > 0) {
                        newTags.append(",");
                    }
                    newTags.append(existingTags[i]);
                }
            }
            tags = newTags.toString().trim();
        }
    }
    
    public List<String> getTagList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] tagArray = tags.split(",");
        List<String> tagList = new ArrayList<>();
        for (String tag : tagArray) {
            String trimmedTag = tag.trim();
            if (!trimmedTag.isEmpty()) {
                tagList.add(trimmedTag);
            }
        }
        return tagList;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDiagramId() {
        return diagramId;
    }
    
    public void setDiagramId(String diagramId) {
        this.diagramId = diagramId;
    }
    
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
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
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
    
    public List<String> getElements() {
        return elements;
    }
    
    public void setElements(List<String> elements) {
        this.elements = elements;
    }
    
    public List<String> getFlows() {
        return flows;
    }
    
    public void setFlows(List<String> flows) {
        this.flows = flows;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    @Override
    public String toString() {
        return "BpmnDiagram{" +
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BpmnDiagram that = (BpmnDiagram) o;
        return diagramId != null && diagramId.equals(that.diagramId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}