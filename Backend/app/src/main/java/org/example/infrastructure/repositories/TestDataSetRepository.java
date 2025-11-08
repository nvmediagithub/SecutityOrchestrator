package org.example.infrastructure.repositories;

import org.example.domain.entities.TestDataSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for TestDataSet entities
 */
@Repository
public interface TestDataSetRepository extends JpaRepository<TestDataSet, Long> {

    /**
     * Find datasets by data type
     */
    List<TestDataSet> findByDataType(String dataType);

    /**
     * Find datasets by category
     */
    List<TestDataSet> findByCategory(String category);

    /**
     * Find datasets by sensitivity level
     */
    List<TestDataSet> findBySensitivityLevel(TestDataSet.SensitivityLevel sensitivityLevel);

    /**
     * Find datasets by status
     */
    List<TestDataSet> findByStatus(TestDataSet.DataStatus status);

    /**
     * Find datasets by source
     */
    List<TestDataSet> findBySource(String source);

    /**
     * Find datasets by environment
     */
    List<TestDataSet> findByEnvironment(String environment);

    /**
     * Find encrypted datasets
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.isEncrypted = true")
    List<TestDataSet> findEncryptedDatasets();

    /**
     * Find dynamic datasets
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.isDynamic = true")
    List<TestDataSet> findDynamicDatasets();

    /**
     * Find reusable datasets
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.isReusable = true")
    List<TestDataSet> findReusableDatasets();

    /**
     * Find datasets by name pattern
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE LOWER(tds.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TestDataSet> findByNameContaining(@Param("name") String name);

    /**
     * Find datasets by description keyword
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE LOWER(tds.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TestDataSet> findByDescriptionContaining(@Param("keyword") String keyword);

    /**
     * Find datasets by tag
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE :tag MEMBER OF tds.tags")
    List<TestDataSet> findByTag(@Param("tag") String tag);

    /**
     * Find datasets with usage count greater than
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.usageCount > :count")
    List<TestDataSet> findByUsageCountGreaterThan(@Param("count") Integer count);

    /**
     * Find recently used datasets
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.lastUsed >= :since ORDER BY tds.lastUsed DESC")
    List<TestDataSet> findRecentlyUsed(@Param("since") LocalDateTime since);

    /**
     * Find sensitive datasets
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.sensitivityLevel IN ('CONFIDENTIAL', 'SECRET')")
    List<TestDataSet> findSensitiveDatasets();

    /**
     * Find JSON datasets
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.dataType LIKE '%JSON%'")
    List<TestDataSet> findJsonDatasets();

    /**
     * Find XML datasets
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.dataType LIKE '%XML%'")
    List<TestDataSet> findXmlDatasets();

    /**
     * Find datasets by version
     */
    List<TestDataSet> findByVersion(String version);

    /**
     * Count datasets by data type
     */
    @Query("SELECT COUNT(tds) FROM TestDataSet tds WHERE tds.dataType = :dataType")
    long countByDataType(@Param("dataType") String dataType);

    /**
     * Find datasets created by user
     */
    List<TestDataSet> findByCreatedBy(String createdBy);

    /**
     * Find datasets with masked fields
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.maskedFields IS NOT EMPTY")
    List<TestDataSet> findDatasetsWithMaskedFields();

    /**
     * Find datasets with required variables
     */
    @Query("SELECT tds FROM TestDataSet tds WHERE tds.requiredVariables IS NOT EMPTY")
    List<TestDataSet> findDatasetsWithRequiredVariables();
}