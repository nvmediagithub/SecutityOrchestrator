package org.example.infrastructure.services.datamanagement;

import org.example.domain.model.datamanagement.DataVersion;
import org.example.domain.model.datamanagement.TestDataSet;
import org.example.infrastructure.repository.TestDataRepository;
import org.example.infrastructure.repository.TestDataVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing data versioning operations
 */
@Service
@Transactional
public class DataVersioningService {
    
    @Autowired
    private TestDataVersionRepository versionRepository;
    
    @Autowired
    private TestDataRepository dataSetRepository;
    
    // Version Creation Methods
    
    /**
     * Create a new version of test data
     */
    public DataVersion createVersion(String dataSetId, String dataContent, String userId) {
        TestDataSet dataSet = dataSetRepository.findByDataSetId(dataSetId)
                .orElseThrow(() -> new IllegalArgumentException("DataSet not found: " + dataSetId));
        
        // Get next version number
        Integer nextVersionNumber = getNextVersionNumber(dataSetId);
        
        // Determine version type based on changes
        String versionType = determineVersionType(dataSet, dataContent);
        
        DataVersion version = new DataVersion(dataSetId, nextVersionNumber, dataContent);
        version.setVersionType(versionType);
        version.setCreatedBy(userId);
        version.setDataSize((long) dataContent.length());
        
        // Calculate diff if previous version exists
        DataVersion previousVersion = getPreviousVersion(dataSetId, nextVersionNumber);
        if (previousVersion != null) {
            String diffData = calculateDiff(previousVersion.getDataContent(), dataContent);
            version.setDiffData(diffData);
            version.setParentVersionId(previousVersion.getVersionId());
        }
        
        DataVersion savedVersion = versionRepository.save(version);
        
        // Update dataSet with current version
        dataSet.setVersionId(savedVersion.getVersionId());
        dataSetRepository.save(dataSet);
        
        return savedVersion;
    }
    
    /**
     * Create a branch from existing version
     */
    public DataVersion createBranch(String sourceVersionId, String branchName, String userId) {
        DataVersion sourceVersion = versionRepository.findByVersionId(sourceVersionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + sourceVersionId));
        
        DataVersion branchVersion = new DataVersion();
        branchVersion.setDataSetId(sourceVersion.getDataSetId());
        branchVersion.setVersionNumber(sourceVersion.getVersionNumber());
        branchVersion.setDataContent(sourceVersion.getDataContent());
        branchVersion.setCreatedBy(userId);
        branchVersion.setBranchName(branchName);
        branchVersion.setParentVersionId(sourceVersion.getVersionId());
        branchVersion.setVersionType("BRANCH");
        
        return versionRepository.save(branchVersion);
    }
    
    // Version Retrieval Methods
    
    /**
     * Get all versions for a data set
     */
    public List<DataVersion> getVersionsByDataSet(String dataSetId) {
        return versionRepository.findByDataSetIdOrderByVersionNumberDesc(dataSetId);
    }
    
    /**
     * Get specific version
     */
    public Optional<DataVersion> getVersion(String versionId) {
        return versionRepository.findByVersionId(versionId);
    }
    
    /**
     * Get latest version for data set
     */
    public Optional<DataVersion> getLatestVersion(String dataSetId) {
        return versionRepository.findLatestVersionForDataSet(dataSetId);
    }
    
    /**
     * Get version by data set and version number
     */
    public Optional<DataVersion> getVersionByNumber(String dataSetId, Integer versionNumber) {
        return versionRepository.findByDataSetIdAndVersionNumber(dataSetId, versionNumber);
    }
    
    // Version Management Methods
    
    /**
     * Mark version as stable
     */
    public void markAsStable(String versionId, String userId) {
        DataVersion version = getVersion(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
        
        version.markAsStable(userId);
        versionRepository.save(version);
    }
    
    /**
     * Mark version as production ready
     */
    public void markAsProductionReady(String versionId, String userId) {
        DataVersion version = getVersion(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
        
        version.markAsProductionReady(userId);
        versionRepository.save(version);
    }
    
    /**
     * Merge branch back to main
     */
    public void mergeBranch(String branchVersionId, String targetVersionId, String userId) {
        DataVersion branchVersion = getVersion(branchVersionId)
                .orElseThrow(() -> new IllegalArgumentException("Branch version not found: " + branchVersionId));
        
        // Create new version on main branch
        DataVersion newVersion = createVersion(branchVersion.getDataSetId(), 
                                             branchVersion.getDataContent(), userId);
        newVersion.setChangeDescription("Merged from branch: " + branchVersion.getBranchName());
        newVersion.setParentVersionId(targetVersionId);
        
        versionRepository.save(newVersion);
        
        // Update branch version
        branchVersion.mergeBranch(targetVersionId);
        versionRepository.save(branchVersion);
    }
    
    /**
     * Rollback to previous version
     */
    public DataVersion rollbackToVersion(String dataSetId, Integer targetVersionNumber, String userId) {
        DataVersion targetVersion = getVersionByNumber(dataSetId, targetVersionNumber)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + targetVersionNumber));
        
        // Create new rollback version
        DataVersion rollbackVersion = createVersion(dataSetId, targetVersion.getDataContent(), userId);
        rollbackVersion.setVersionType("ROLLBACK");
        rollbackVersion.setChangeDescription("Rollback to version " + targetVersionNumber);
        rollbackVersion.setParentVersionId(targetVersion.getVersionId());
        
        return versionRepository.save(rollbackVersion);
    }
    
    // Version Comparison Methods
    
    /**
     * Compare two versions
     */
    public VersionComparison compareVersions(String versionId1, String versionId2) {
        DataVersion version1 = getVersion(versionId1)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId1));
        DataVersion version2 = getVersion(versionId2)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId2));
        
        String diff = calculateDiff(version1.getDataContent(), version2.getDataContent());
        boolean isNewer = version2.getVersionNumber() > version1.getVersionNumber();
        
        return new VersionComparison(version1, version2, diff, isNewer);
    }
    
    /**
     * Get version history
     */
    public List<DataVersion> getVersionHistory(String dataSetId) {
        return versionRepository.findByDataSetIdOrderByVersionNumberDesc(dataSetId);
    }
    
    /**
     * Get version statistics
     */
    public VersionStatistics getVersionStatistics(String dataSetId) {
        long totalVersions = versionRepository.countByDataSetId(dataSetId);
        long stableVersions = versionRepository.findByDataSetIdAndIsStableTrue(dataSetId).size();
        long productionReadyVersions = versionRepository.findByDataSetIdAndIsProductionReadyTrue(dataSetId).size();
        
        Optional<DataVersion> latestVersion = getLatestVersion(dataSetId);
        Optional<DataVersion> earliestVersion = versionRepository.findByDataSetId(dataSetId).stream()
                .min(Comparator.comparing(DataVersion::getVersionNumber));
        
        return new VersionStatistics(dataSetId, totalVersions, stableVersions, 
                                    productionReadyVersions, latestVersion, earliestVersion);
    }
    
    // Private Helper Methods
    
    private Integer getNextVersionNumber(String dataSetId) {
        List<DataVersion> existingVersions = versionRepository.findByDataSetIdOrderByVersionNumberDesc(dataSetId);
        return existingVersions.isEmpty() ? 1 : existingVersions.get(0).getVersionNumber() + 1;
    }
    
    private String determineVersionType(TestDataSet dataSet, String newContent) {
        // Simple logic - can be enhanced with actual content comparison
        if (dataSet.getDataContent() == null || dataSet.getDataContent().isEmpty()) {
            return "MAJOR";
        }
        
        // For demo purposes, assume any change is a minor version
        return "MINOR";
    }
    
    private DataVersion getPreviousVersion(String dataSetId, Integer currentVersionNumber) {
        return versionRepository.findByDataSetIdAndVersionNumber(dataSetId, currentVersionNumber - 1).orElse(null);
    }
    
    private String calculateDiff(String oldContent, String newContent) {
        // Simple diff implementation - in real scenario, use proper diff library
        if (Objects.equals(oldContent, newContent)) {
            return "{\"type\":\"no_changes\"}";
        }
        
        return String.format("{\"type\":\"modified\",\"old_size\":%d,\"new_size\":%d}", 
                           oldContent != null ? oldContent.length() : 0, 
                           newContent != null ? newContent.length() : 0);
    }
    
    // Inner classes for return types
    
    public static class VersionComparison {
        private final DataVersion olderVersion;
        private final DataVersion newerVersion;
        private final String diff;
        private final boolean isNewer;
        
        public VersionComparison(DataVersion olderVersion, DataVersion newerVersion, String diff, boolean isNewer) {
            this.olderVersion = olderVersion;
            this.newerVersion = newerVersion;
            this.diff = diff;
            this.isNewer = isNewer;
        }
        
        // Getters
        public DataVersion getOlderVersion() { return olderVersion; }
        public DataVersion getNewerVersion() { return newerVersion; }
        public String getDiff() { return diff; }
        public boolean isNewer() { return isNewer; }
    }
    
    public static class VersionStatistics {
        private final String dataSetId;
        private final long totalVersions;
        private final long stableVersions;
        private final long productionReadyVersions;
        private final Optional<DataVersion> latestVersion;
        private final Optional<DataVersion> earliestVersion;
        
        public VersionStatistics(String dataSetId, long totalVersions, long stableVersions,
                               long productionReadyVersions, Optional<DataVersion> latestVersion,
                               Optional<DataVersion> earliestVersion) {
            this.dataSetId = dataSetId;
            this.totalVersions = totalVersions;
            this.stableVersions = stableVersions;
            this.productionReadyVersions = productionReadyVersions;
            this.latestVersion = latestVersion;
            this.earliestVersion = earliestVersion;
        }
        
        // Getters
        public String getDataSetId() { return dataSetId; }
        public long getTotalVersions() { return totalVersions; }
        public long getStableVersions() { return stableVersions; }
        public long getProductionReadyVersions() { return productionReadyVersions; }
        public Optional<DataVersion> getLatestVersion() { return latestVersion; }
        public Optional<DataVersion> getEarliestVersion() { return earliestVersion; }
        
        public double getStabilityRate() {
            return totalVersions > 0 ? (double) stableVersions / totalVersions : 0.0;
        }
        
        public double getProductionReadyRate() {
            return totalVersions > 0 ? (double) productionReadyVersions / totalVersions : 0.0;
        }
    }
}