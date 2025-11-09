package org.example.domain.model.datamanagement;

public class DataVersion {
    private Long id;
    private String versionId;
    private String dataSetId;
    private String versionName;
    private String versionType;
    private String status;
    private String createdBy;
    private String releasedBy;
    private String branchName;
    private String parentVersionId;
    private Integer versionNumber;
    
    public enum VersionStatus {
        DRAFT, ACTIVE, ARCHIVED, DEPRECATED
    }
    
    public DataVersion() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }
    public String getDataSetId() { return dataSetId; }
    public void setDataSetId(String dataSetId) { this.dataSetId = dataSetId; }
    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }
    public String getVersionType() { return versionType; }
    public void setVersionType(String versionType) { this.versionType = versionType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getReleasedBy() { return releasedBy; }
    public void setReleasedBy(String releasedBy) { this.releasedBy = releasedBy; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getParentVersionId() { return parentVersionId; }
    public void setParentVersionId(String parentVersionId) { this.parentVersionId = parentVersionId; }
    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }
}
