package org.example.infrastructure.repository;

import org.example.domain.entities.BpmnGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BpmnGatewayRepository extends JpaRepository<BpmnGateway, Long> {
    
    List<BpmnGateway> findByProcessId(Long processId);
    
    List<BpmnGateway> findByType(BpmnGateway.GatewayType type);
    
    List<BpmnGateway> findByDirection(BpmnGateway.GatewayDirection direction);
}