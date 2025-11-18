package org.example.features.testdata.testdata.infrastructure.repositories;

import org.example.features.testdata.testdata.domain.DataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DataField entity
 */
@Repository
public interface DataFieldRepository extends JpaRepository<DataField, Long> {

    /**
     * Find field by field ID
     */
    Optional<DataField> findByFieldId(@Param("fieldId") String fieldId);

    /**
     * Find fields by field name containing
     */
    List<DataField> findByFieldNameContainingIgnoreCase(@Param("fieldName") String fieldName);

    /**
     * Find fields by data type
     */
    List<DataField> findByDataType(@Param("dataType") String dataType);

    /**
     * Find active fields
     */
    @Query("SELECT f FROM DataField f WHERE f.status = 'ACTIVE'")
    List<DataField> findActiveFields();

    /**
     * Find fields by status
     */
    List<DataField> findByStatus(DataField.FieldStatus status);

    /**
     * Find fields by created by
     */
    List<DataField> findByCreatedBy(@Param("createdBy") String createdBy);

    /**
     * Find fields by field IDs
     */
    @Query("SELECT f FROM DataField f WHERE f.fieldId IN :fieldIds")
    List<DataField> findByFieldIds(@Param("fieldIds") List<String> fieldIds);

    /**
     * Count fields by data type
     */
    @Query("SELECT COUNT(f) FROM DataField f WHERE f.dataType = :dataType")
    long countByDataType(@Param("dataType") String dataType);

    /**
     * Find fields with description containing
     */
    @Query("SELECT f FROM DataField f WHERE LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DataField> findByDescriptionContaining(@Param("keyword") String keyword);
}