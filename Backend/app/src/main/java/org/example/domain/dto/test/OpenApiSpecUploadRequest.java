package org.example.domain.dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for uploading OpenAPI specifications
 */
public class OpenApiSpecUploadRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "OpenAPI content is required")
    private String openApiContent;
    
    @NotBlank(message = "OpenAPI version is required")
    private String openApiVersion;
    
    private String fileName;
    private String version;
    private Long fileSize;
    private String contentType;
    private String uploadedBy;
    private String projectId; // optional: associate with a project immediately
    private String serverUrls;
    private String components;
    
    // Constructors
    public OpenApiSpecUploadRequest() {
    }
    
    public OpenApiSpecUploadRequest(String title, String openApiContent, String openApiVersion) {
        this.title = title;
        this.openApiContent = openApiContent;
        this.openApiVersion = openApiVersion;
    }
    
    // Getters and Setters
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
    
    public String getOpenApiContent() {
        return openApiContent;
    }
    
    public void setOpenApiContent(String openApiContent) {
        this.openApiContent = openApiContent;
    }
    
    public String getOpenApiVersion() {
        return openApiVersion;
    }
    
    public void setOpenApiVersion(String openApiVersion) {
        this.openApiVersion = openApiVersion;
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
    
    public String getServerUrls() {
        return serverUrls;
    }
    
    public void setServerUrls(String serverUrls) {
        this.serverUrls = serverUrls;
    }
    
    public String getComponents() {
        return components;
    }
    
    public void setComponents(String components) {
        this.components = components;
    }
    
    @Override
    public String toString() {
        return "OpenApiSpecUploadRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", fileName='" + fileName + '\'' +
                ", version='" + version + '\'' +
                ", openApiVersion='" + openApiVersion + '\'' +
                ", fileSize=" + fileSize +
                ", contentType='" + contentType + '\'' +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}