package org.example.infrastructure.repositories.bpmn;

import org.example.domain.entities.bpmn.BusinessProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for BusinessProcess entities
 */
@Repository
public interface BusinessProcessRepository extends JpaRepository<BusinessProcess, UUID> {

    /**
     * Find processes by parse status
     */
    List<BusinessProcess> findByParseStatus(BusinessProcess.ParseStatus parseStatus);

    /**
     * Find processes by process ID
     */
    Optional<BusinessProcess> findByProcessId(String processId);

    /**
     * Find processes by file name pattern
     */
    @Query("SELECT bp FROM BusinessProcess bp WHERE bp.fileName LIKE %:fileName%")
    List<BusinessProcess> findByFileNameContaining(@Param("fileName") String fileName);

    /**
     * Find processes with tasks
     */
    @Query("SELECT DISTINCT bp FROM BusinessProcess bp JOIN bp.tasks WHERE SIZE(bp.tasks) > 0")
    List<BusinessProcess> findProcessesWithTasks();

    /**
     * Count processes by status
     */
    @Query("SELECT bp.parseStatus, COUNT(bp) FROM BusinessProcess bp GROUP BY bp.parseStatus")
    List<Object[]> countByParseStatus();

    /**
     * Get process statistics
     */
    @Query("SELECT " +
           "COUNT(bp), " +
           "SUM(CASE WHEN bp.parseStatus = 'SUCCESS' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN bp.parseStatus = 'FAILED' THEN 1 ELSE 0 END), " +
           "AVG(SIZE(bp.tasks)) " +
           "FROM BusinessProcess bp")
    Object[] getProcessStatistics();
}