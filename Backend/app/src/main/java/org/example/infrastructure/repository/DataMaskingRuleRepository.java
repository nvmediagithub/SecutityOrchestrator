package org.example.infrastructure.repository;

import org.example.domain.entities.DataMaskingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataMaskingRuleRepository extends JpaRepository<DataMaskingRule, Long> {
    
    List<DataMaskingRule> findByActive(boolean active);
    
    List<DataMaskingRule> findByFieldName(String fieldName);
    
    List<DataMaskingRule> findByMaskingType(DataMaskingRule.MaskingType maskingType);
}