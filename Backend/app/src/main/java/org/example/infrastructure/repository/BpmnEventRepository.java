package org.example.infrastructure.repository;

import org.example.domain.entities.BpmnEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpmnEventRepository extends JpaRepository<BpmnEvent, Long> {
    
    List<BpmnEvent> findByProcessId(Long processId);
    
    List<BpmnEvent> findByType(BpmnEvent.EventType type);
    
    List<BpmnEvent> findByEventType(BpmnEvent.EventSubType eventType);
}