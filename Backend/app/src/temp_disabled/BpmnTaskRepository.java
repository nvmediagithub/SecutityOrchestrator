package org.example.infrastructure.repositories.bpmn;

import org.example.domain.entities.bpmn.BpmnTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for BpmnTask entities
 */
@Repository
public interface BpmnTaskRepository extends JpaRepository<BpmnTask, UUID> {

    /**
     * Find tasks by process
     */
    List<BpmnTask> findByProcessId(UUID processId);

    /**
     * Find tasks by task type
     */
    List<BpmnTask> findByTaskType(BpmnTask.TaskType taskType);

    /**
     * Find tasks by event ID
     */
    Optional<BpmnTask> findByTaskId(String taskId);

    /**
     * Find service tasks
     */
    @Query("SELECT t FROM BpmnTask t WHERE t.taskType = 'SERVICE_TASK'")
    List<BpmnTask> findServiceTasks();

    /**
     * Find user tasks
     */
    @Query("SELECT t FROM BpmnTask t WHERE t.taskType = 'USER_TASK'")
    List<BpmnTask> findUserTasks();

    /**
     * Find script tasks
     */
    @Query("SELECT t FROM BpmnTask t WHERE t.taskType = 'SCRIPT_TASK'")
    List<BpmnTask> findScriptTasks();

    /**
     * Find tasks by name pattern
     */
    @Query("SELECT t FROM BpmnTask t WHERE LOWER(t.taskName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<BpmnTask> findByTaskNameContaining(@Param("name") String name);

    /**
     * Count tasks by process
     */
    @Query("SELECT COUNT(t) FROM BpmnTask t WHERE t.process.id = :processId")
    Long countByProcessId(@Param("processId") UUID processId);

    /**
     * Find tasks with specific configuration
     */
    @Query("SELECT t FROM BpmnTask t WHERE t.configuration LIKE %:config%")
    List<BpmnTask> findByConfigurationContaining(@Param("config") String config);
}