package org.example.infrastructure.repositories;

import org.example.domain.entities.TestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TestData entity
 */
@Repository
public interface TestDataRepository extends JpaRepository<TestData, UUID> {
    
    @Query("SELECT td FROM TestData td WHERE td.dataSetId = :dataSetId")
    List<TestData> findByDataSetId(@Param("dataSetId") String dataSetId);
    
    @Query("SELECT td FROM TestData td WHERE td.dataType = :dataType")
    List<TestData> findByDataType(@Param("dataType") String dataType);
    
    @Query("SELECT td FROM TestData td WHERE td.name = :name")
    Optional<TestData> findByName(@Param("name") String name);
    
    @Query("SELECT td FROM TestData td WHERE td.isSensitive = true")
    List<TestData> findSensitiveData();
}