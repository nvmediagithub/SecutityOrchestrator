package org.example.infrastructure.repository;

import org.example.domain.entities.BpmnSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpmnSequenceRepository extends JpaRepository<BpmnSequence, Long> {
    
    List<BpmnSequence> findByProcessId(Long processId);
    
    List<BpmnSequence> findBySourceElementId(String sourceElementId);
    
    List<BpmnSequence> findByTargetElementId(String targetElementId);
    
    @Query("SELECT s FROM BpmnSequence s WHERE s.process.id = :processId AND s.sourceElementId = :sourceElementId")
    List<BpmnSequence> findByProcessIdAndSourceElementId(@Param("processId") Long processId, @Param("sourceElementId") String sourceElementId);
}