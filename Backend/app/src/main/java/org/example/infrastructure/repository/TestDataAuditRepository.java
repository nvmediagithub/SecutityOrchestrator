package org.example.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for TestDataAudit entities
 * (Entity for audit tracking will need to be created)
 */
@Repository
public interface TestDataAuditRepository extends JpaRepository<Object, Long> {
    
    // Basic audit queries (will be implemented when TestDataAudit entity is created)
    
    // Date-based queries
    List<Object> findByTimestampAfter(LocalDateTime date);
    
    List<Object> findByTimestampBefore(LocalDateTime date);
    
    List<Object> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    // User-based queries
    List<Object> findByUserId(String userId);
    
    List<Object> findByAction(String action);
    
    // Data set based queries
    List<Object> findByDataSetId(String dataSetId);
    
    // Complex audit queries
    @Query("SELECT a FROM TestDataAudit a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:dataSetId IS NULL OR a.dataSetId = :dataSetId) AND " +
           "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR a.timestamp <= :endDate)")
    Page<Object> findWithFilters(
            @Param("userId") String userId,
            @Param("action") String action,
            @Param("dataSetId") String dataSetId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(a) FROM TestDataAudit a WHERE a.action = :action")
    long countByAction(@Param("action") String action);
    
    @Query("SELECT COUNT(a) FROM TestDataAudit a WHERE a.userId = :userId")
    long countByUserId(@Param("userId") String userId);
    
    @Query("SELECT COUNT(a) FROM TestDataAudit a WHERE a.dataSetId = :dataSetId")
    long countByDataSetId(@Param("dataSetId") String dataSetId);
    
    // Activity queries
    @Query("SELECT a FROM TestDataAudit a ORDER BY a.timestamp DESC")
    List<Object> findMostRecentActivity(Pageable pageable);
    
    @Query("SELECT a FROM TestDataAudit a WHERE a.userId = :userId ORDER BY a.timestamp DESC")
    List<Object> findUserActivity(@Param("userId") String userId, Pageable pageable);
    
    @Query("SELECT a FROM TestDataAudit a WHERE a.dataSetId = :dataSetId ORDER BY a.timestamp DESC")
    List<Object> findDataSetActivity(@Param("dataSetId") String dataSetId, Pageable pageable);
    
    // Time-based analytics
    @Query("SELECT new map(DATE(a.timestamp) as date, COUNT(a) as count) FROM TestDataAudit a GROUP BY DATE(a.timestamp) ORDER BY date DESC")
    List<Object> getDailyActivityStats();
    
    @Query("SELECT new map(a.action as action, COUNT(a) as count) FROM TestDataAudit a GROUP BY a.action ORDER BY count DESC")
    List<Object> getActionStatistics();
    
    @Query("SELECT new map(a.userId as userId, COUNT(a) as count) FROM TestDataAudit a GROUP BY a.userId ORDER BY count DESC")
    List<Object> getUserActivityStatistics();
    
    // Data change tracking
    @Query("SELECT a FROM TestDataAudit a WHERE a.action IN ('CREATE', 'UPDATE', 'DELETE') ORDER BY a.timestamp DESC")
    List<Object> findDataModifications(Pageable pageable);
    
    @Query("SELECT a FROM TestDataAudit a WHERE a.action = 'UPDATE' AND a.newValues IS NOT NULL ORDER BY a.timestamp DESC")
    List<Object> findDataUpdates(Pageable pageable);
    
    // Security queries
    @Query("SELECT a FROM TestDataAudit a WHERE a.action IN ('READ', 'EXPORT', 'SHARE') ORDER BY a.timestamp DESC")
    List<Object> findDataAccessActivity(Pageable pageable);
    
    @Query("SELECT a FROM TestDataAudit a WHERE a.userId = :userId AND a.action = 'DELETE' ORDER BY a.timestamp DESC")
    List<Object> findUserDeletions(@Param("userId") String userId, Pageable pageable);
    
    // Cleanup queries
    @Query("SELECT a FROM TestDataAudit a WHERE a.timestamp <= :cutoffDate")
    List<Object> findForCleanup(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("DELETE FROM TestDataAudit a WHERE a.timestamp <= :cutoffDate")
    int deleteOldAuditRecords(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Compliance queries
    @Query("SELECT a FROM TestDataAudit a WHERE a.dataSetId = :dataSetId AND a.action = 'EXPORT' AND a.timestamp >= :startDate ORDER BY a.timestamp DESC")
    List<Object> findDataExports(@Param("dataSetId") String dataSetId, @Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    @Query("SELECT a FROM TestDataAudit a WHERE a.dataSetId = :dataSetId AND a.action = 'SHARE' AND a.timestamp >= :startDate ORDER BY a.timestamp DESC")
    List<Object> findDataShares(@Param("dataSetId") String dataSetId, @Param("startDate") LocalDateTime startDate, Pageable pageable);
}