package org.example.infrastructure.repository;

import org.example.domain.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDataRepository extends JpaRepository<Test, Long> {
    
    List<Test> findByTestType(String testType);
    
    List<Test> findByTestName(String testName);
}