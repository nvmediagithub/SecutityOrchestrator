package org.example.infrastructure.repository;

import org.example.domain.model.datamanagement.TestDataSet;
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
 * JPA Repository for TestDataSet entities
 */
@Repository
public interface TestDataRepository extends JpaRepository<TestDataSet, Long> {
    
    // Basic finders
    Optional<TestDataSet> findByDataSetId(String dataSetId);
    
    List<TestDataSet> findByNameContainingIgnoreCase(String name);
    
    List<TestDataSet> findByStatus(TestDataSet.DataSetStatus status);
    
    List<TestDataSet> findByPrivacyClassification(String classification);
    
    List<TestDataSet> findByDataType(String dataType);
    
    List<TestDataSet> findByCreatedBy(String createdBy);
    
    List<TestDataSet> findByTagsContaining(String tag);
    
    // Version-based queries
    List<TestDataSet> findByVersionId(String versionId);
    
    Optional<TestDataSet> findByDataSetIdAndStatus(String dataSetId, TestDataSet.DataSetStatus status);
    
    // Date-based queries
    List<TestDataSet> findByCreatedAtAfter(LocalDateTime date);
    
    List<TestDataSet> findByCreatedAtBefore(LocalDateTime date);
    
    List<TestDataSet> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<TestDataSet> findByLastAccessedBefore(LocalDateTime date);
    
    List<TestDataSet> findByUpdatedAtAfter(LocalDateTime date);
    
    // Size and count queries
    List<TestDataSet> findByDataSizeGreaterThan(Long size);
    
    List<TestDataSet> findByRecordCountGreaterThan(Integer count);
    
    List<TestDataSet> findByIsEncryptedTrue();
    
    List<TestDataSet> findByIsCompressedTrue();
    
    List<TestDataSet> findByIsArchivedTrue();
    
    List<TestDataSet> findByIsArchivedFalse();
    
    // Complex queries
    @Query("SELECT t FROM TestDataSet t WHERE " +
           "(:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:privacyClassification IS NULL OR t.privacyClassification = :privacyClassification) AND " +
           "(:dataType IS NULL OR t.dataType = :dataType) AND " +
           "(:createdBy IS NULL OR t.createdBy = :createdBy) AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate)")
    Page<TestDataSet> findWithFilters(
            @Param("name") String name,
            @Param("status") TestDataSet.DataSetStatus status,
            @Param("privacyClassification") String privacyClassification,
            @Param("dataType") String dataType,
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    // Tag-based queries
    @Query("SELECT t FROM TestDataSet t JOIN t.tags tag WHERE tag = :tag")
    List<TestDataSet> findByTag(@Param("tag") String tag);
    
    @Query("SELECT t FROM TestDataSet t WHERE SIZE(t.tags) >= :minTags")
    List<TestDataSet> findByMinimumTags(@Param("minTags") int minTags);
    
    // Related data queries
    @Query("SELECT t FROM TestDataSet t JOIN t.relatedDataSets related WHERE related = :relatedId")
    List<TestDataSet> findByRelatedDataSet(@Param("relatedId") String relatedId);
    
    // Metadata queries
    @Query("SELECT t FROM TestDataSet t WHERE t.properties IS NOT EMPTY")
    List<TestDataSet> findWithMetadata();
    
    @Query("SELECT t FROM TestDataSet t WHERE t.properties[:key] = :value")
    List<TestDataSet> findByMetadataValue(@Param("key") String key, @Param("value") Object value);
    
    // Statistical queries
    @Query("SELECT COUNT(t) FROM TestDataSet t WHERE t.status = :status")
    long countByStatus(@Param("status") TestDataSet.DataSetStatus status);
    
    @Query("SELECT COUNT(t) FROM TestDataSet t WHERE t.privacyClassification = :classification")
    long countByPrivacyClassification(@Param("classification") String classification);
    
    @Query("SELECT SUM(t.dataSize) FROM TestDataSet t WHERE t.dataSize IS NOT NULL")
    Long getTotalDataSize();
    
    @Query("SELECT AVG(t.dataSize) FROM TestDataSet t WHERE t.dataSize IS NOT NULL")
    Double getAverageDataSize();
    
    @Query("SELECT SUM(t.recordCount) FROM TestDataSet t WHERE t.recordCount IS NOT NULL")
    Long getTotalRecordCount();
    
    @Query("SELECT AVG(t.recordCount) FROM TestDataSet t WHERE t.recordCount IS NOT NULL")
    Double getAverageRecordCount();
    
    // Activity queries
    @Query("SELECT t FROM TestDataSet t WHERE t.lastAccessed IS NOT NULL ORDER BY t.lastAccessed DESC")
    List<TestDataSet> findMostRecentlyAccessed(Pageable pageable);
    
    @Query("SELECT t FROM TestDataSet t WHERE t.usageCount >= :minUsage ORDER BY t.usageCount DESC")
    List<TestDataSet> findMostUsed(@Param("minUsage") int minUsage, Pageable pageable);
    
    // Orphaned data queries (no relations)
    @Query("SELECT t FROM TestDataSet t WHERE t.relatedDataSets IS EMPTY")
    List<TestDataSet> findOrphaned();
    
    // Large data queries
    @Query("SELECT t FROM TestDataSet t WHERE t.dataSize >= :minSize ORDER BY t.dataSize DESC")
    List<TestDataSet> findLargeDataSets(@Param("minSize") Long minSize, Pageable pageable);
    
    // Sensitive data queries
    @Query("SELECT t FROM TestDataSet t WHERE t.privacyClassification IN ('CONFIDENTIAL', 'RESTRICTED', 'PII')")
    List<TestDataSet> findSensitiveData();
    
    // Archive queries
    @Query("SELECT t FROM TestDataSet t WHERE t.isArchived = true AND t.createdAt <= :beforeDate")
    List<TestDataSet> findArchivedBefore(@Param("beforeDate") LocalDateTime beforeDate);
    
    // Performance queries
    @Query("SELECT t FROM TestDataSet t WHERE t.isCacheable = true AND t.isValid() = true")
    List<TestDataSet> findCacheableData();
    
    // Custom aggregate queries
    @Query("SELECT new map(t.status as status, COUNT(t) as count) FROM TestDataSet t GROUP BY t.status")
    List<Object> getStatusStatistics();
    
    @Query("SELECT new map(t.privacyClassification as classification, COUNT(t) as count) FROM TestDataSet t WHERE t.privacyClassification IS NOT NULL GROUP BY t.privacyClassification")
    List<Object> getPrivacyClassificationStatistics();
    
    @Query("SELECT new map(t.dataType as dataType, COUNT(t) as count, SUM(t.dataSize) as totalSize) FROM TestDataSet t WHERE t.dataType IS NOT NULL AND t.dataSize IS NOT NULL GROUP BY t.dataType")
    List<Object> getDataTypeStatistics();
    
    // Cleanup queries
    @Query("SELECT t FROM TestDataSet t WHERE t.isArchived = true AND t.createdAt <= :cutoffDate")
    List<TestDataSet> findForDeletion(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("DELETE FROM TestDataSet t WHERE t.id IN :ids")
    void deleteByIds(@Param("ids") List<Long> ids);
    
    // Search with full-text capabilities (if supported)
    @Query("SELECT t FROM TestDataSet t WHERE " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.metadata) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<TestDataSet> fullTextSearch(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Bulk operations support
    @Query("UPDATE TestDataSet t SET t.status = :newStatus WHERE t.status = :oldStatus")
    int updateStatusByStatus(@Param("oldStatus") TestDataSet.DataSetStatus oldStatus, 
                            @Param("newStatus") TestDataSet.DataSetStatus newStatus);
    
    @Query("UPDATE TestDataSet t SET t.lastAccessed = :accessedAt, t.lastAccessedBy = :userId WHERE t.id IN :ids")
    int markAsAccessed(@Param("ids") List<Long> ids, 
                      @Param("accessedAt") LocalDateTime accessedAt, 
                      @Param("userId") String userId);
    
    // Version control support
    @Query("SELECT t FROM TestDataSet t WHERE t.versionId = :versionId OR t.dataSetId = :dataSetId")
    List<TestDataSet> findByVersionOrDataSetId(@Param("versionId") String versionId, 
                                              @Param("dataSetId") String dataSetId);
    
    // Archive and restore operations
    @Query("UPDATE TestDataSet t SET t.isArchived = true, t.status = 'ARCHIVED' WHERE t.id IN :ids")
    int archiveByIds(@Param("ids") List<Long> ids);
    
    @Query("UPDATE TestDataSet t SET t.isArchived = false, t.status = 'ACTIVE' WHERE t.id IN :ids")
    int restoreByIds(@Param("ids") List<Long> ids);
}