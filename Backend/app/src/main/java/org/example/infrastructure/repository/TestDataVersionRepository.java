package org.example.infrastructure.repository;

import org.example.domain.model.datamanagement.DataVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for DataVersion entities
 */
@Repository
public interface TestDataVersionRepository extends JpaRepository<DataVersion, Long> {
    
    // Basic finders
    Optional<DataVersion> findByVersionId(String versionId);
    
    List<DataVersion> findByDataSetId(String dataSetId);
    
    List<DataVersion> findByVersionNameContainingIgnoreCase(String name);
    
    List<DataVersion> findByStatus(DataVersion.VersionStatus status);
    
    List<DataVersion> findByVersionType(String versionType);
    
    List<DataVersion> findByCreatedBy(String createdBy);
    
    List<DataVersion> findByReleasedBy(String releasedBy);
    
    List<DataVersion> findByTagsContaining(String tag);
    
    // Version-specific queries
    Optional<DataVersion> findByDataSetIdAndVersionNumber(String dataSetId, Integer versionNumber);
    
    List<DataVersion> findByDataSetIdOrderByVersionNumberDesc(String dataSetId);
    
    List<DataVersion> findByDataSetIdAndStatus(String dataSetId, DataVersion.VersionStatus status);
    
    List<DataVersion> findByParentVersionId(String parentVersionId);
    
    List<DataVersion> findByBranchName(String branchName);
    
    // Date-based queries
    List<DataVersion> findByCreatedAtAfter(LocalDateTime date);
    
    List<DataVersion> findByCreatedAtBefore(LocalDateTime date);
    
    List<DataVersion> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<DataVersion> findByReleasedAtAfter(LocalDateTime date);
    
    List<DataVersion> findByReleasedAtBefore(LocalDateTime date);
    
    List<DataVersion> findByReleasedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<DataVersion> findByUpdatedAtAfter(LocalDateTime date);
    
    // Stability and production queries
    List<DataVersion> findByIsStableTrue();
    
    List<DataVersion> findByIsProductionReadyTrue();
    
    List<DataVersion> findByIsStableFalse();
    
    List<DataVersion> findByIsProductionReadyFalse();
    
    // Size and count queries
    List<DataVersion> findByDataSizeGreaterThan(Long size);
    
    List<DataVersion> findByRecordCountGreaterThan(Integer count);
    
    // Complex queries with filters
    @Query("SELECT v FROM DataVersion v WHERE " +
           "(:dataSetId IS NULL OR v.dataSetId = :dataSetId) AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:versionType IS NULL OR v.versionType = :versionType) AND " +
           "(:createdBy IS NULL OR v.createdBy = :createdBy) AND " +
           "(:isStable IS NULL OR v.isStable = :isStable) AND " +
           "(:isProductionReady IS NULL OR v.isProductionReady = :isProductionReady) AND " +
           "(:startDate IS NULL OR v.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR v.createdAt <= :endDate)")
    Page<DataVersion> findWithFilters(
            @Param("dataSetId") String dataSetId,
            @Param("status") DataVersion.VersionStatus status,
            @Param("versionType") String versionType,
            @Param("createdBy") String createdBy,
            @Param("isStable") Boolean isStable,
            @Param("isProductionReady") Boolean isProductionReady,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    // Tag-based queries
    @Query("SELECT v FROM DataVersion v JOIN v.tags tag WHERE tag = :tag")
    List<DataVersion> findByTag(@Param("tag") String tag);
    
    @Query("SELECT v FROM DataVersion v WHERE SIZE(v.tags) >= :minTags")
    List<DataVersion> findByMinimumTags(@Param("minTags") int minTags);
    
    // Metadata queries
    @Query("SELECT v FROM DataVersion v WHERE v.metadata IS NOT EMPTY")
    List<DataVersion> findWithMetadata();
    
    @Query("SELECT v FROM DataVersion v WHERE v.metadata[:key] = :value")
    List<DataVersion> findByMetadataValue(@Param("key") String key, @Param("value") Object value);
    
    // Branching queries
    @Query("SELECT v FROM DataVersion v WHERE v.branchName IS NOT NULL")
    List<DataVersion> findBranches();
    
    @Query("SELECT v FROM DataVersion v WHERE v.branchName = :branchName AND v.parentVersionId IS NOT NULL")
    List<DataVersion> findBranchVersions(@Param("branchName") String branchName);
    
    @Query("SELECT DISTINCT v.branchName FROM DataVersion v WHERE v.branchName IS NOT NULL")
    List<String> findDistinctBranchNames();
    
    // Latest version queries
    @Query("SELECT v FROM DataVersion v WHERE v.versionNumber = (SELECT MAX(v2.versionNumber) FROM DataVersion v2 WHERE v2.dataSetId = v.dataSetId)")
    List<DataVersion> findLatestVersions();
    
    @Query("SELECT v FROM DataVersion v WHERE v.dataSetId = :dataSetId AND v.versionNumber = (SELECT MAX(v2.versionNumber) FROM DataVersion v2 WHERE v2.dataSetId = :dataSetId)")
    Optional<DataVersion> findLatestVersionForDataSet(@Param("dataSetId") String dataSetId);
    
    // Statistics queries
    @Query("SELECT COUNT(v) FROM DataVersion v WHERE v.status = :status")
    long countByStatus(@Param("status") DataVersion.VersionStatus status);
    
    @Query("SELECT COUNT(v) FROM DataVersion v WHERE v.dataSetId = :dataSetId")
    long countByDataSetId(@Param("dataSetId") String dataSetId);
    
    @Query("SELECT COUNT(v) FROM DataVersion v WHERE v.isStable = true")
    long countStableVersions();
    
    @Query("SELECT COUNT(v) FROM DataVersion v WHERE v.isProductionReady = true")
    long countProductionReadyVersions();
    
    @Query("SELECT SUM(v.dataSize) FROM DataVersion v WHERE v.dataSize IS NOT NULL")
    Long getTotalDataSize();
    
    @Query("SELECT AVG(v.dataSize) FROM DataVersion v WHERE v.dataSize IS NOT NULL")
    Double getAverageDataSize();
    
    @Query("SELECT SUM(v.recordCount) FROM DataVersion v WHERE v.recordCount IS NOT NULL")
    Long getTotalRecordCount();
    
    @Query("SELECT AVG(v.recordCount) FROM DataVersion v WHERE v.recordCount IS NOT NULL")
    Double getAverageRecordCount();
    
    // Activity queries
    @Query("SELECT v FROM DataVersion v WHERE v.releasedAt IS NOT NULL ORDER BY v.releasedAt DESC")
    List<DataVersion> findMostRecentlyReleased(Pageable pageable);
    
    @Query("SELECT v FROM DataVersion v WHERE v.createdAt IS NOT NULL ORDER BY v.createdAt DESC")
    List<DataVersion> findMostRecentlyCreated(Pageable pageable);
    
    @Query("SELECT v FROM DataVersion v WHERE SIZE(v.tags) >= :minTags ORDER BY v.createdAt DESC")
    List<DataVersion> findMostTagged(@Param("minTags") int minTags, Pageable pageable);
    
    // Version comparison queries
    @Query("SELECT v FROM DataVersion v WHERE v.dataSetId = :dataSetId AND v.versionNumber >= :minVersion ORDER BY v.versionNumber ASC")
    List<DataVersion> findVersionsFrom(@Param("dataSetId") String dataSetId, @Param("minVersion") Integer minVersion);
    
    @Query("SELECT v FROM DataVersion v WHERE v.dataSetId = :dataSetId AND v.versionNumber <= :maxVersion ORDER BY v.versionNumber DESC")
    List<DataVersion> findVersionsTo(@Param("dataSetId") String dataSetId, @Param("maxVersion") Integer maxVersion);
    
    // Diff and change queries
    @Query("SELECT v FROM DataVersion v WHERE v.diffData IS NOT NULL")
    List<DataVersion> findWithDiffs();
    
    @Query("SELECT v FROM DataVersion v WHERE v.changeDescription IS NOT NULL AND v.changeDescription != ''")
    List<DataVersion> findWithChangeDescriptions();
    
    // Orphaned version queries
    @Query("SELECT v FROM DataVersion v WHERE v.parentVersionId IS NULL AND v.branchName IS NULL")
    List<DataVersion> findOrphanedVersions();
    
    // Version type queries
    @Query("SELECT v FROM DataVersion v WHERE v.versionType = 'MAJOR' ORDER BY v.versionNumber DESC")
    List<DataVersion> findMajorVersions(Pageable pageable);
    
    @Query("SELECT v FROM DataVersion v WHERE v.versionType = 'MINOR' ORDER BY v.versionNumber DESC")
    List<DataVersion> findMinorVersions(Pageable pageable);
    
    @Query("SELECT v FROM DataVersion v WHERE v.versionType = 'PATCH' ORDER BY v.versionNumber DESC")
    List<DataVersion> findPatchVersions(Pageable pageable);
    
    // Maintenance and cleanup queries
    @Query("SELECT v FROM DataVersion v WHERE v.status = 'DEPRECATED' AND v.createdAt <= :cutoffDate")
    List<DataVersion> findDeprecatedForDeletion(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT v FROM DataVersion v WHERE v.status = 'ARCHIVED' AND v.createdAt <= :cutoffDate")
    List<DataVersion> findArchivedForDeletion(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("DELETE FROM DataVersion v WHERE v.id IN :ids")
    void deleteByIds(@Param("ids") List<Long> ids);
    
    // Custom aggregate queries
    @Query("SELECT new map(v.status as status, COUNT(v) as count) FROM DataVersion v GROUP BY v.status")
    List<Object> getStatusStatistics();
    
    @Query("SELECT new map(v.versionType as versionType, COUNT(v) as count) FROM DataVersion v WHERE v.versionType IS NOT NULL GROUP BY v.versionType")
    List<Object> getVersionTypeStatistics();
    
    @Query("SELECT new map(v.branchName as branchName, COUNT(v) as count) FROM DataVersion v WHERE v.branchName IS NOT NULL GROUP BY v.branchName")
    List<Object> getBranchStatistics();
    
    @Query("SELECT new map(v.dataSetId as dataSetId, COUNT(v) as count, MAX(v.versionNumber) as latestVersion) FROM DataVersion v GROUP BY v.dataSetId")
    List<Object> getDataSetVersionStatistics();
    
    // Version control operations
    @Query("UPDATE DataVersion v SET v.status = :newStatus WHERE v.status = :oldStatus")
    int updateStatusByStatus(@Param("oldStatus") DataVersion.VersionStatus oldStatus, 
                            @Param("newStatus") DataVersion.VersionStatus newStatus);
    
    @Query("UPDATE DataVersion v SET v.isStable = true, v.updatedAt = :updatedAt WHERE v.id IN :ids")
    int markAsStable(@Param("ids") List<Long> ids, @Param("updatedAt") LocalDateTime updatedAt);
    
    @Query("UPDATE DataVersion v SET v.isProductionReady = true, v.releasedAt = :releasedAt, v.releasedBy = :releasedBy WHERE v.id IN :ids")
    int markAsProductionReady(@Param("ids") List<Long> ids, 
                             @Param("releasedAt") LocalDateTime releasedAt, 
                             @Param("releasedBy") String releasedBy);
    
    // Branch operations
    @Query("UPDATE DataVersion v SET v.branchName = :branchName, v.parentVersionId = :parentVersionId WHERE v.id = :versionId")
    int createBranch(@Param("versionId") Long versionId, 
                    @Param("branchName") String branchName, 
                    @Param("parentVersionId") String parentVersionId);
    
    @Query("UPDATE DataVersion v SET v.branchName = NULL WHERE v.branchName = :branchName")
    int mergeBranch(@Param("branchName") String branchName);
    
    // Search queries
    @Query("SELECT v FROM DataVersion v WHERE " +
           "LOWER(v.versionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.changeDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<DataVersion> fullTextSearch(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Validation queries
    @Query("SELECT v FROM DataVersion v WHERE v.dataSetId = :dataSetId AND v.versionNumber = :versionNumber")
    Optional<DataVersion> findByDataSetAndVersionNumber(@Param("dataSetId") String dataSetId, @Param("versionNumber") Integer versionNumber);
    
    @Query("SELECT COUNT(v) FROM DataVersion v WHERE v.dataSetId = :dataSetId AND v.versionNumber = :versionNumber")
    long countByDataSetIdAndVersionNumber(@Param("dataSetId") String dataSetId, @Param("versionNumber") Integer versionNumber);
    
    // Integrity queries
    @Query("SELECT v FROM DataVersion v WHERE v.dataSize IS NULL OR v.recordCount IS NULL")
    List<DataVersion> findIncompleteVersions();
    
    @Query("SELECT v FROM DataVersion v WHERE v.dataContent IS NULL OR v.dataContent.trim() = ''")
    List<DataVersion> findVersionsWithoutContent();
}