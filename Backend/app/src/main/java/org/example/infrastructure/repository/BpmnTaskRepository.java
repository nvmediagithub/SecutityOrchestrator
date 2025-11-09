package org.example.infrastructure.repository;

import org.example.domain.entities.BpmnTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BpmnTaskRepository extends JpaRepository<BpmnTask, Long> {
    
    List<BpmnTask> findByProcessId(Long processId);
    
    List<BpmnTask> findByType(BpmnTask.BpmnTaskType type);
    
    Optional<BpmnTask> findByElementId(String elementId);
    
    @Query("SELECT t FROM BpmnTask t WHERE t.process.id = :processId AND t.type = :type")
    List<BpmnTask> findByProcessIdAndType(@Param("processId") Long processId, @Param("type") BpmnTask.BpmnTaskType type);
    
    @Query("SELECT COUNT(t) FROM BpmnTask t WHERE t.process.id = :processId")
    long countByProcessId(@Param("processId") Long processId);
}
